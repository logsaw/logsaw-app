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
package net.sf.logsaw.core.query.model;

import java.util.Date;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.query.IRestrictionVisitor;
import net.sf.logsaw.core.query.Operator;
import net.sf.logsaw.core.query.support.ARestriction;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public final class DateRestriction extends ARestriction<Date> {

	/**
	 * Constructor.
	 * @param field the log entry field
	 * @param operator the operator to apply
	 * @param value the value
	 */
	public DateRestriction(ALogEntryField<?, Date> field, Operator operator,
			Date value) {
		super(field, operator, value);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.ARestriction#visit(net.sf.logsaw.core.query.IRestrictionVisitor)
	 */
	@Override
	public void visit(IRestrictionVisitor visitor) {
		Assert.isNotNull(visitor, "visitor"); //$NON-NLS-1$
		visitor.visit(this);
	}
}
