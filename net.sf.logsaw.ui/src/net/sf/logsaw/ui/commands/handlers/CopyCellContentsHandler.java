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

import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handler copies the contents of the focused cell into the clipboard.
 * 
 * @author Philipp Nanz
 */
public class CopyCellContentsHandler extends AbstractHandler {

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
			String text = editor.getFocusCellText();
			if (text.length() > 0) {
				// Copy to clipboard
				Clipboard cb = new Clipboard(Display.getDefault());
				TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[] {text}, new Transfer[] {textTransfer});
			}
		}
		return null;
	}
}
