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
package net.sf.logsaw.core.field;

import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;

/**
 * @author Philipp Nanz
 */
public class LogEntryFieldVisitorAdapter implements ILogEntryFieldVisitor {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
	 */
	@Override
	public void visit(DateLogEntryField fld) {
		// to override
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
	 */
	@Override
	public void visit(LevelLogEntryField fld) {
		// to override
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
	 */
	@Override
	public void visit(StringLogEntryField fld) {
		// to override
	}
}
