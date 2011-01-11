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
package net.sf.logsaw.ui.commands.handlers;

import java.util.Collection;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.parts.IRefreshablePart;
import net.sf.logsaw.ui.views.LogResourcesView;
import net.sf.logsaw.ui.wizards.AddLogResourceWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * This handler creates a new log resource.
 * 
 * @author Philipp Nanz
 */
public class AddLogResourceHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the view early (avoid NPE on Linux)
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		IViewPart view = page.findView(LogResourcesView.ID);
		
		Collection<ILogDialectFactory> dialectFactories = null;
		try {
			dialectFactories = CorePlugin.getDefault().getLogDialectFactories();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, false);
			return null;
		}
		if (dialectFactories.isEmpty()) {
			// Show error only
			StatusManager.getManager().handle(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					Messages.AddLogResourceAction_noLogDialects), StatusManager.SHOW);
			return null;
		}
		String filename = event.getParameter(
				"net.sf.logsaw.ui.commands.AddLogResourceCommand.filename"); //$NON-NLS-1$
		WizardDialog wiz = new WizardDialog(Display.getDefault().getActiveShell(), 
				new AddLogResourceWizard(dialectFactories, filename));
		// Enable help button
		wiz.setHelpAvailable(true);
		if (wiz.open() == Window.OK) {
			// Refresh resource view
			IRefreshablePart refresh = view != null ? 
					(IRefreshablePart) view.getAdapter(IRefreshablePart.class) : null;
			if (refresh != null) {
				refresh.refresh();
			}
		}
		return null;
	}

}
