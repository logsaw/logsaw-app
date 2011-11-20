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
package net.sf.logsaw.ui.commands.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.parts.IRefreshablePart;
import net.sf.logsaw.ui.views.LogResourcesView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handler removes one or more selected log resources.
 * 
 * @author Philipp Nanz
 */
public class RemoveLogResourceHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the view
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		IViewPart view = page.findView(LogResourcesView.ID);
		// Get the selection
		ISelection selection = view.getSite().getSelectionProvider()
				.getSelection();
		if ((selection != null) && (selection instanceof IStructuredSelection)) {
			if (!MessageDialog.openQuestion(Display.getDefault().getActiveShell(), 
					Messages.RemoveLogResourceHandler_confirm_title, 
					Messages.RemoveLogResourceHandler_confirm_question)) {
				return null;
			}
			
			// Collect for multi status
			List<IStatus> statuses = new ArrayList<IStatus>();
			Iterator it = ((IStructuredSelection) selection).iterator();
			while (it.hasNext()) {
				ILogResource log = (ILogResource) it.next();
				if (UIPlugin.getDefault().getLogResourceManager().isJobInProgress(log)) {
					// Log index is in use
					statuses.add(new Status(IStatus.INFO, UIPlugin.PLUGIN_ID, 
							NLS.bind(Messages.Generic_info_jobInProgress, log.getName())));
					continue;
				}
				
				// Close editors
				IEditorReference[] editorRefs = page.findEditors(
						(IEditorInput) log.getAdapter(IEditorInput.class), 
						null, IWorkbenchPage.MATCH_INPUT);
				page.closeEditors(editorRefs, false);
				
				try {
					UIPlugin.getDefault().getLogResourceManager().remove(log);
				} catch (CoreException e) {
					statuses.add(e.getStatus());
				}
			}
			
			// Save log resources
			try {
				UIPlugin.getDefault().getLogResourceManager().saveState();
			} catch (CoreException e) {
				statuses.add(e.getStatus());
			}
			
			// Refresh resource view
			IRefreshablePart refresh = (IRefreshablePart) view.getAdapter(IRefreshablePart.class);
			if (refresh != null) {
				refresh.refresh();
			}
			
			if (!statuses.isEmpty()) {
				IStatus multiStatus = new MultiStatus(UIPlugin.PLUGIN_ID, 
						0, statuses.toArray(new IStatus[statuses.size()]), 
						Messages.RemoveLogResourceHandler_info_someCouldNotBeRemoved, null);
				// Log and show error
				UIPlugin.logAndShowError(new CoreException(multiStatus), false);
			}
		}
		return null;
	}
}
