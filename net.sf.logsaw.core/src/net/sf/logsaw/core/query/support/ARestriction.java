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

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.query.IRestrictionVisitor;
import net.sf.logsaw.core.query.Operator;

import org.eclipse.core.runtime.Assert;

/**
 * Abstract base class for query restrictions.
 * 
 * @author Philipp Nanz
 * @param <VT> the value type
 */
public abstract class ARestriction<VT> {

	private ALogEntryField<?, VT> field;
	private VT value;
	private Operator operator;

	/**
	 * Constructor.
	 * @param field the log entry field
	 * @param operator the operator to apply
	 * @param value the value
	 */
	public ARestriction(ALogEntryField<?, VT> field, Operator operator, VT value) {
		Assert.isNotNull(field, "field"); //$NON-NLS-1$
		Assert.isNotNull(operator, "operator"); //$NON-NLS-1$
		Assert.isNotNull(value, "value"); //$NON-NLS-1$
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	/**
	 * @return the field
	 */
	public final ALogEntryField<?, VT> getField() {
		return field;
	}

	/**
	 * @return the value
	 */
	public final VT getValue() {
		return value;
	}

	/**
	 * Returns the operator that this restriction is representing.
	 * @return the operator instance
	 */
	public final Operator getOperator() {
		return operator;
	}

	/**
	 * Implements the visitor pattern.
	 * @param visitor the visitor
	 */
	public abstract void visit(IRestrictionVisitor visitor);
}
