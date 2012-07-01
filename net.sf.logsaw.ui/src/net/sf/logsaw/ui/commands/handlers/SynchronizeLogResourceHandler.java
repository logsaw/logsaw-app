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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.SynchronizationResult;
import net.sf.logsaw.ui.IGenericCallback;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.editors.ILogViewEditor;
import net.sf.logsaw.ui.util.UIUtils;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * This handler synchronizes one or more selected log resources.
 * 
 * @author Philipp Nanz
 */
public class SynchronizeLogResourceHandler extends AbstractHandler {

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
				
				try {
					doSynchronize(log);
				} catch (CoreException e) {
					statuses.add(e.getStatus());
				}
			}
			
			if (!statuses.isEmpty()) {
				IStatus multiStatus = new MultiStatus(UIPlugin.PLUGIN_ID, 
						0, statuses.toArray(new IStatus[statuses.size()]), 
						Messages.SynchronizeLogResourceHandler_info_someCouldNotBeSynchronized, null);
				// Log and show error
				UIPlugin.logAndShowError(new CoreException(multiStatus), false);
			}
		}
		return null;
	}

	private void doSynchronize(final ILogResource log) throws CoreException {
		UIPlugin.getDefault().getLogResourceManager().synchronize(
				log, new IGenericCallback<SynchronizationResult>() {
			
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
	}
}
