/*******************************************************************************
 * Copyright (c) 2010 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.ui.views;

import net.sf.logsaw.ui.IHelpContexts;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.parts.IRefreshablePart;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Philipp Nanz
 */
public class LogResourcesView extends ViewPart implements IRefreshablePart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sf.logsaw.ui.views.LogResourcesView"; //$NON-NLS-1$

	private TreeViewer viewer;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |  SWT.MULTI);
		viewer.setContentProvider(new BaseWorkbenchContentProvider() {

			private Object[] input;

			/* (non-Javadoc)
			 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getElements(java.lang.Object)
			 */
			@Override
			public Object[] getElements(Object element) {
				if (input == null) {
					return new Object[0];
				}
				return input;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				//Assert.isTrue(newInput instanceof Object[], "input must be instance of java.lang.Object[]");
				input = (Object[]) newInput;
				super.inputChanged(viewer, oldInput, newInput);
			}
		});
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.addOpenListener(new IOpenListener() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.IOpenListener#open(org.eclipse.jface.viewers.OpenEvent)
			 */
			@Override
			public void open(OpenEvent event) {
				IHandlerService handlerService = 
					(IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(
							"net.sf.logsaw.ui.commands.OpenLogResourceCommand", //$NON-NLS-1$
							null);
				} catch (CommandException e) {
					// log and show error
					UIPlugin.logAndShowError(new CoreException(new Status(
							IStatus.ERROR, UIPlugin.PLUGIN_ID, 
							Messages.Generic_errorExecutingCommand, e)), false);
				}
			}
		});
		ViewerDropAdapter dropAdapter = new ViewerDropAdapter(viewer) {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
			 */
			@Override
			public boolean performDrop(final Object target) {
				Display.getDefault().asyncExec(new Runnable() {

					/* (non-Javadoc)
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						IHandlerService handlerService = 
							(IHandlerService) getSite().getService(IHandlerService.class);
						ICommandService commandService = 
							(ICommandService) getSite().getService(ICommandService.class);
						Command cmd = commandService.getCommand("net.sf.logsaw.ui.commands.AddLogResourceCommand"); //$NON-NLS-1$
						try {
							Parameterization param = new Parameterization(
									cmd.getParameter("net.sf.logsaw.ui.commands.AddLogResourceCommand.filename"), ((String[]) target)[0]); //$NON-NLS-1$
							ParameterizedCommand paraCmd = new ParameterizedCommand(cmd, new Parameterization[] {param});
							handlerService.executeCommand(paraCmd, null);
						} catch (CommandException e) {
							// log and show error
							UIPlugin.logAndShowError(new CoreException(new Status(
									IStatus.ERROR, UIPlugin.PLUGIN_ID, 
									Messages.Generic_errorExecutingCommand, e)), false);
						}
					}
				});
				// Always return true
				return true;
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
			 */
			@Override
			public boolean validateDrop(Object target, int op,
					TransferData type) {
				return FileTransfer.getInstance().isSupportedType(type) && 
						(getCurrentTarget() == null);
			}
		};
		dropAdapter.setFeedbackEnabled(false);
		viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, 
				new Transfer[] {FileTransfer.getInstance()}, dropAdapter);
		getSite().setSelectionProvider(viewer);
		
		// Setup popup menu
		hookContextMenu();
		
		// Refresh
		refresh();
		
		// Enable dynamic help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, 
				IHelpContexts.LOG_RESOURCES_VIEW);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// Other plug-ins can contribute their actions here
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.parts.IRefreshablePart#refresh()
	 */
	@Override
	public void refresh() {
		try {
			viewer.setInput(UIPlugin.getDefault().getLogResourceManager().getAll());
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, false);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
