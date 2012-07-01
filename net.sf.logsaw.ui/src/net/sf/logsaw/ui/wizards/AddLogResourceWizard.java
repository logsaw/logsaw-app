/*******************************************************************************
 * Copyright (c) 2010, 2011 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.ui.wizards;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.logsaw.core.config.IConfigurableObject;
import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.SynchronizationResult;
import net.sf.logsaw.ui.IGenericCallback;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.editors.ILogViewEditor;
import net.sf.logsaw.ui.util.UIUtils;
import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage;
import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPageFactory;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philipp Nanz
 */
public class AddLogResourceWizard extends Wizard {

	private static transient Logger logger = LoggerFactory.getLogger(AddLogResourceWizard.class);

	private ILogDialectFactory selectedDialectFactory;
	private ILogDialect dialectInstance;
	private List<IConfigurableObjectWizardPage> dialectPages = 
		new ArrayList<IConfigurableObjectWizardPage>();
	private Collection<ILogDialectFactory> dialectFactories;
	private SimpleLogResourceWizardPage logResourcePage;
	private DialectWizardPage selectDialectPage;
	private String filename;

	/**
	 * Constructor.
	 * @param dialectFactories the dialect factories available
	 * @param filename the filename provided via DND
	 */
	public AddLogResourceWizard(Collection<ILogDialectFactory> dialectFactories, String filename) {
		Assert.isNotNull(dialectFactories, "dialectFactories"); //$NON-NLS-1$
		this.dialectFactories = dialectFactories;
		this.filename = filename;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		setWindowTitle(Messages.AddLogResourceWizard_title);
		super.createPageControls(pageContainer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		logResourcePage = new SimpleLogResourceWizardPage(filename);
		selectDialectPage = new DialectWizardPage(dialectFactories);
		addPage(logResourcePage);
		addPage(selectDialectPage);
		super.addPages();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectDialectPage) {
			// Determine dynamic follow-up pages
			ILogDialectFactory dialect = selectDialectPage.getSelectedDialectFactory();
			updateWizardPages(dialectPages, "net.sf.logsaw.ui.dialectWizardPageFactory", dialect); //$NON-NLS-1$
			if (!dialectPages.isEmpty()) {
				return dialectPages.get(0);
			}
		}
		IConfigurableObjectWizardPage[] array = dialectPages.toArray(
				new IConfigurableObjectWizardPage[dialectPages.size()]);
		for (int i = 0; i < array.length; i++) {
			if ((page == array[i]) && (array.length > i + 1)) {
				return array[i + 1];
			}
		}
		return super.getNextPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		ILogDialectFactory dialectFactory = selectDialectPage.getSelectedDialectFactory();
		if (dialectFactory != null) {
			// Determine dynamic follow-up pages
			updateWizardPages(dialectPages, "net.sf.logsaw.ui.dialectWizardPageFactory", dialectFactory); //$NON-NLS-1$
			for (IConfigurableObjectWizardPage page : dialectPages) {
				if (!page.isPageComplete()) {
					return false;
				}
			}
		}
		return super.canFinish();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			// Signal dynamic pages to apply settings
			IConfigurableObject obj = (IConfigurableObject) dialectInstance.getAdapter(IConfigurableObject.class);
			if (obj != null) {
				// Configure option defaults (if any)
				obj.configureDefaults();
				
				for (IConfigurableObjectWizardPage page : dialectPages) {
					page.performFinish();
				}
				Assert.isTrue(obj.isConfigured(), "Dialect should be configured by now"); //$NON-NLS-1$
			}
			
			// Create the log resource
			final ILogResource log = logResourcePage.createLogResource(dialectInstance);
			Assert.isTrue(log.isConfigured(), "Log resource should be configured by now"); //$NON-NLS-1$
			UIPlugin.getDefault().getLogResourceManager().add(log);
			UIPlugin.getDefault().getLogResourceManager().saveState();
			UIPlugin.getDefault().getLogResourceManager().synchronize(log, 
					new IGenericCallback<SynchronizationResult>() {
				
				@Override
				public void doCallback(final SynchronizationResult payload) {
					Display.getDefault().asyncExec(new Runnable() {

						/* (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IEditorReference[] editorRefs = page.findEditors(
									(IEditorInput) log.getAdapter(IEditorInput.class), 
									null, IWorkbenchPage.MATCH_INPUT);
							for (IEditorReference editorRef : editorRefs) {
								// Refresh editor
								IEditorPart editorPart = editorRef.getEditor(false);
								ILogViewEditor editor = editorPart != null ? 
										(ILogViewEditor) editorPart.getAdapter(ILogViewEditor.class) : null;
								if (editor != null) {
									editor.clearQueryContext();
									editor.refresh();
								}
							}
							NumberFormat fmt = DecimalFormat.getInstance();
							String title = payload.isCanceled() ? 
									Messages.SynchronizeLogResourceAction_canceled_title : 
										Messages.SynchronizeLogResourceAction_finished_title;
							String message = payload.isCanceled() ? 
									Messages.SynchronizeLogResourceAction_canceled_message : 
										Messages.SynchronizeLogResourceAction_finished_message;
							message = NLS.bind(message, new Object[] {
									log.getName(), 
									UIUtils.formatRuntime(payload.getRuntime()), 
									fmt.format(payload.getNumberOfEntriesAdded())});
							if (payload.getMessages().isEmpty()) {
								// Everything is fine
								MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
										title, message);
							} else {
								// We got one or more warnings
								IStatus status = new MultiStatus(UIPlugin.PLUGIN_ID, 0, 
										payload.getMessages().toArray(new IStatus[0]), message, null);
								StatusManager.getManager().handle(status, StatusManager.BLOCK);
							}
						}
					});
				}
			});
			return true;
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, false);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	@Override
	public void dispose() {
		disposeDynamicPages(dialectPages);
		super.dispose();
	}

	private void disposeDynamicPages(List<IConfigurableObjectWizardPage> pages) {
		Assert.isNotNull(pages, "pages"); //$NON-NLS-1$
		synchronized (pages) {
			// Dispose all pages and clear list
			for (IConfigurableObjectWizardPage page : pages) {
				page.dispose();
			}
			pages.clear();	
		}
	}

	private void updateWizardPages(List<IConfigurableObjectWizardPage> pages, 
			String extensionPointId, ILogDialectFactory dialectFactory) {
		Assert.isNotNull(pages, "pages"); //$NON-NLS-1$
		Assert.isNotNull(extensionPointId, "extensionPointId"); //$NON-NLS-1$
		synchronized (pages) {
			if ((selectedDialectFactory != null) && (dialectFactory.getId() == null)) {
				// Dispose and return
				logger.info("Clearing dynamic pages for " + extensionPointId); //$NON-NLS-1$
				selectedDialectFactory = null;
				disposeDynamicPages(pages);
				return;
			} else if ((selectedDialectFactory == null) && (dialectFactory.getId() == null)) {
				// Same dialect as before (none)
				return;
			} else if ((selectedDialectFactory != null) && dialectFactory.getId().equals(selectedDialectFactory.getId())) {
				// Same dialect as before
				return;
			}
			// Dispose first, then lookup new pages
			logger.info("Updating dynamic pages for " + extensionPointId); //$NON-NLS-1$
			
			// The factory has changed, recreate instance
			selectedDialectFactory = dialectFactory;
			dialectInstance = dialectFactory.createLogDialect();
			
			disposeDynamicPages(pages);
			// Re-fill
			IConfigurableObject obj = (IConfigurableObject) dialectInstance.getAdapter(IConfigurableObject.class);
			if (obj == null) {
				return;
			}
			IConfigurationElement[] wizardPages = Platform.getExtensionRegistry().getConfigurationElementsFor(
					extensionPointId); //$NON-NLS-1$
			for (IConfigurationElement wizardPage : wizardPages) {
				if ((wizardPage.getAttribute("class") != null) &&  //$NON-NLS-1$
						dialectFactory.getId().equals(wizardPage.getAttribute("dialect"))) { //$NON-NLS-1$
					try {
						IConfigurableObjectWizardPageFactory wizPageFactory = (IConfigurableObjectWizardPageFactory) wizardPage.createExecutableExtension("class"); //$NON-NLS-1$
						IConfigurableObjectWizardPage page = wizPageFactory.newWizardPage();
						page.setConfigurableObject(obj);
						page.setWizard(this);
						pages.add(page); //$NON-NLS-1$
					} catch (CoreException e) {
						// Log and show error
						UIPlugin.logAndShowError(e, false);
					}
				}
			}
		}
	}
}
