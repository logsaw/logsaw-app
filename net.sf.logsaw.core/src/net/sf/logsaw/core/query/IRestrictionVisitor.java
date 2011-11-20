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
package net.sf.logsaw.core.query;

import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;

/**
 * @author Philipp Nanz
 */
public interface IRestrictionVisitor {

	/**
	 * Implements the visitor pattern.
	 * @param restriction the concrete restriction
	 */
	void visit(DateRestriction restriction);

	/**
	 * Implements the visitor pattern.
	 * @param restriction the concrete restriction
	 */
	void visit(StringRestriction restriction);

	/**
	 * Implements the visitor pattern.
	 * @param restriction the concrete restriction
	 */
	void visit(LevelRestriction restriction);
}
