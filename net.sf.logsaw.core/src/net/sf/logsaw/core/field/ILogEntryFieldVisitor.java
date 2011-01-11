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
package net.sf.logsaw.core.field;

import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;

/**
 * @author Philipp Nanz
 */
public interface ILogEntryFieldVisitor {

	/**
	 * Implements the visitor pattern.
	 * @param fld the concrete field
	 */
	void visit(DateLogEntryField fld);

	/**
	 * Implements the visitor pattern.
	 * @param fld the concrete field
	 */
	void visit(LevelLogEntryField fld);

	/**
	 * Implements the visitor pattern.
	 * @param fld the concrete field
	 */
	void visit(StringLogEntryField fld);
}
