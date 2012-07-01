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
package net.sf.logsaw.core.internal.query;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.query.IRestrictionFactory;
import net.sf.logsaw.core.query.Operator;
import net.sf.logsaw.core.query.Operators;
import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public final class DefaultRestrictionFactoryImpl implements IRestrictionFactory {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionFactory#getOperators(net.sf.logsaw.core.model.ALogEntryField)
	 */
	@Override
	public Set<Operator> getOperators(ALogEntryField<?, ?> fld) {
		Assert.isNotNull(fld, "fld"); //$NON-NLS-1$
		final Set<Operator> ret = new HashSet<Operator>();
		fld.visit(new ILogEntryFieldVisitor() {

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
			 */
			@Override
			public void visit(StringLogEntryField fld) {
				// Does the field consist of multiple terms?
				if (fld.isAnalyzed()) {
					ret.add(Operators.OPERATOR_CONTAINS);
					ret.add(Operators.OPERATOR_NOT_CONTAINS);
				} else {
					ret.add(Operators.OPERATOR_EQUALS);
					ret.add(Operators.OPERATOR_NOT_EQUALS);
					ret.add(Operators.OPERATOR_BEGINS_WITH);
					ret.add(Operators.OPERATOR_NOT_BEGINS_WITH);
				}
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
			 */
			@Override
			public void visit(LevelLogEntryField fld) {
				ret.add(Operators.OPERATOR_LESS_THAN);
				ret.add(Operators.OPERATOR_GREATER_THAN);
				ret.add(Operators.OPERATOR_EQUALS);
				ret.add(Operators.OPERATOR_NOT_EQUALS);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
			 */
			@Override
			public void visit(DateLogEntryField fld) {
				ret.add(Operators.OPERATOR_BEFORE);
				ret.add(Operators.OPERATOR_AFTER);
			}
		});
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionFactory#newRestriction(net.sf.logsaw.core.model.DateLogEntryField, net.sf.logsaw.core.query.Operator, java.util.Date)
	 */
	@Override
	public DateRestriction newRestriction(DateLogEntryField fld, 
			Operator op, Date val) {
		Assert.isNotNull(op, "op"); //$NON-NLS-1$
		Assert.isNotNull(fld, "fld"); //$NON-NLS-1$
		switch (op.getId()) {
		case Operators.ID_OP_BEFORE:
			return new DateRestriction(fld, Operators.OPERATOR_BEFORE, val);
		case Operators.ID_OP_AFTER:
			return new DateRestriction(fld, Operators.OPERATOR_AFTER, val);
		}
		Assert.isTrue(false, "Unsupported operator: " + op.getLabel()); //$NON-NLS-1$
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionFactory#newRestriction(net.sf.logsaw.core.model.LevelLogEntryField, net.sf.logsaw.core.query.Operator, net.sf.logsaw.core.Level)
	 */
	@Override
	public LevelRestriction newRestriction(LevelLogEntryField fld, 
			Operator op, Level val) {
		Assert.isNotNull(op, "op"); //$NON-NLS-1$
		Assert.isNotNull(fld, "fld"); //$NON-NLS-1$
		switch (op.getId()) {
		case Operators.ID_OP_GREATER_THAN:
			return new LevelRestriction(fld, Operators.OPERATOR_GREATER_THAN, val);
		case Operators.ID_OP_LESS_THAN:
			return new LevelRestriction(fld, Operators.OPERATOR_LESS_THAN, val);
		case Operators.ID_OP_EQUALS:
			return new LevelRestriction(fld, Operators.OPERATOR_EQUALS, val);
		case Operators.ID_OP_NOT_EQUALS:
			return new LevelRestriction(fld, Operators.OPERATOR_NOT_EQUALS, val);
		}
		Assert.isTrue(false, "Unsupported operator: " + op.getLabel()); //$NON-NLS-1$
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictionFactory#newRestriction(net.sf.logsaw.core.model.StringLogEntryField, net.sf.logsaw.core.query.Operator, java.lang.String)
	 */
	@Override
	public StringRestriction newRestriction(StringLogEntryField fld, 
			Operator op, String val) {
		Assert.isNotNull(op, "op"); //$NON-NLS-1$
		Assert.isNotNull(fld, "fld"); //$NON-NLS-1$
		switch (op.getId()) {
		case Operators.ID_OP_CONTAINS:
			return new StringRestriction(fld, Operators.OPERATOR_CONTAINS, val);
		case Operators.ID_OP_NOT_CONTAINS:
			return new StringRestriction(fld, Operators.OPERATOR_NOT_CONTAINS, val);
		case Operators.ID_OP_EQUALS:
			return new StringRestriction(fld, Operators.OPERATOR_EQUALS, val);
		case Operators.ID_OP_NOT_EQUALS:
			return new StringRestriction(fld, Operators.OPERATOR_NOT_EQUALS, val);
		case Operators.ID_OP_BEGINS_WITH:
			return new StringRestriction(fld, Operators.OPERATOR_BEGINS_WITH, val);
		case Operators.ID_OP_NOT_BEGINS_WITH:
			return new StringRestriction(fld, Operators.OPERATOR_NOT_BEGINS_WITH, val);
		}
		Assert.isTrue(false, "Unsupported operator: " + op.getLabel()); //$NON-NLS-1$
		return null;
	}
}
