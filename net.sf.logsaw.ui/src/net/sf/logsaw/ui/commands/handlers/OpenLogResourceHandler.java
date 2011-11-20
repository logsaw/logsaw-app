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

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.editors.LogViewEditor;
import net.sf.logsaw.ui.views.LogResourcesView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handler opens a selected log resource in a <code>LogViewEditor</code>.
 * 
 * @author Philipp Nanz
 */
public class OpenLogResourceHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
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
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			// If we had a selection lets open the editor
			if (obj != null) {
				ILogResource log = (ILogResource) obj;
				try {
					page.openEditor((IEditorInput) log.getAdapter(IEditorInput.class), LogViewEditor.ID);
				} catch (PartInitException e) {
					// Log and show error
					UIPlugin.logAndShowError(e, false);
				}
			}
		}
		return null;
	}
}
