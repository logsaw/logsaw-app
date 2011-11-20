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

import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.IEvaluationService;

/**
 * This handler navigates to the next page of the active <code>LogViewEditor</code>.
 * 
 * @author Philipp Nanz
 */
public class NextPageHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the editor
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ILogViewEditor editor = 
			(ILogViewEditor) page.getActiveEditor().getAdapter(ILogViewEditor.class);
		if (editor != null) {
			editor.nextPage();
			
			// Request re-evaluation
			IEvaluationService service = 
				(IEvaluationService) window.getService(IEvaluationService.class);
			service.requestEvaluation("net.sf.logsaw.ui.expressions.logViewEditor.isPreviousPageAllowed"); //$NON-NLS-1$
			service.requestEvaluation("net.sf.logsaw.ui.expressions.logViewEditor.isNextPageAllowed"); //$NON-NLS-1$
		}
		return null;
	}

}
