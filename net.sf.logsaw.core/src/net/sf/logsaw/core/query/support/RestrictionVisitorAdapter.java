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
package net.sf.logsaw.core.query.support;

import net.sf.logsaw.core.query.IRestrictionVisitor;
import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;

/**
 * @author Philipp Nanz
 */
public class RestrictionVisitorAdapter implements IRestrictionVisitor {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.DateRestriction)
	 */
	@Override
	public void visit(DateRestriction restriction) {
		// to override
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.StringRestriction)
	 */
	@Override
	public void visit(StringRestriction restriction) {
		// to override
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.LevelRestriction)
	 */
	@Override
	public void visit(LevelRestriction restriction) {
		// to override
	}
}
