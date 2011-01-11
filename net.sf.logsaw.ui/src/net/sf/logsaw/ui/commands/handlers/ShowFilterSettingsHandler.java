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

import java.util.List;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictable;
import net.sf.logsaw.core.query.support.ARestriction;
import net.sf.logsaw.ui.dialogs.FilterSettingsDialog;
import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.IEvaluationService;

/**
 * This handler opens the filter settings for the active <code>LogViewEditor</code>.
 * 
 * @author Philipp Nanz
 */
public class ShowFilterSettingsHandler extends AbstractHandler {

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
			ILogResource log = editor.getLogResource();
			IRestrictable rest = editor.getRestrictable();
			List<ARestriction<?>> list = rest.getRestrictions();
			// Add restriction provided by Quick Filter
			boolean added = addNewRestriction(list, event, log);
			
			FilterSettingsDialog dlg = new FilterSettingsDialog(
					Display.getDefault().getActiveShell(), log, list, added);
			if (dlg.open() == Window.OK) {
				// Update input and refresh editor view
				rest.setRestrictions(dlg.getRestrictions());
				editor.goToPage(1);
				
				// Request re-evaluation
				IEvaluationService service = 
					(IEvaluationService) window.getService(IEvaluationService.class);
				service.requestEvaluation("net.sf.logsaw.ui.expressions.logViewEditor.isPreviousPageAllowed"); //$NON-NLS-1$
				service.requestEvaluation("net.sf.logsaw.ui.expressions.logViewEditor.isNextPageAllowed"); //$NON-NLS-1$
			}
		}
		return null;
	}

	private boolean addNewRestriction(List<ARestriction<?>> rest, ExecutionEvent event, ILogResource log) {
		String fieldKey = event.getParameter(
				"net.sf.logsaw.ui.commands.ShowFilterSettingsCommand.newField"); //$NON-NLS-1$
		String operatorId = event.getParameter(
				"net.sf.logsaw.ui.commands.ShowFilterSettingsCommand.newOperator"); //$NON-NLS-1$
		String value = event.getParameter(
				"net.sf.logsaw.ui.commands.ShowFilterSettingsCommand.newValue"); //$NON-NLS-1$
		if ((fieldKey != null) && (operatorId != null) && (value != null)) {
			rest.add(CorePlugin.getDefault().createRestriction(log, fieldKey, Integer.valueOf(operatorId), value));
			return true;
		}
		return false;
	}
}
