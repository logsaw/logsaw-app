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
package net.sf.logsaw.ui.views;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.field.model.StringLogEntryField;

/**
 * @author Philipp Nanz
 */
public class StacktraceFieldView extends ALogEntryFieldView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sf.logsaw.ui.views.StacktraceFieldView"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.views.ALogEntryFieldView#getFieldToDisplay(net.sf.logsaw.core.framework.ILogDialect)
	 */
	@Override
	protected StringLogEntryField getFieldToDisplay(ILogDialect dialect) {
		return dialect.getFieldProvider().getStacktraceField();
	}
}
