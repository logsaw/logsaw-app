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
package net.sf.logsaw.ui.commands.expressions;

import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.core.expressions.PropertyTester;

/**
 * A property tester for the <code>LogViewEditor</code>.
 * 
 * @author Philipp Nanz
 */
public class LogViewEditorPropertyTester extends PropertyTester {

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		ILogViewEditor editor = (ILogViewEditor) receiver;
		boolean value = false;
		if ("isNextPageAllowed".equals(property)) { //$NON-NLS-1$
			value = editor.isNextPageAllowed();
		} else if ("isPreviousPageAllowed".equals(property)) { //$NON-NLS-1$
			value = editor.isPreviousPageAllowed();
		} else if ("isFocusCellBackedByLogEntryField".equals(property)) { //$NON-NLS-1$
			value = editor.getFocusCellLogEntryField() != null;
		} else if ("isFocusCellTextNotEmpty".equals(property)) { //$NON-NLS-1$
			value = editor.getFocusCellText().length() > 0;
		}
		return expectedValue == null ? 
				value : value == ((Boolean) expectedValue).booleanValue();
	}

}
