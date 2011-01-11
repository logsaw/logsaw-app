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
package net.sf.logsaw.core.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.internal.Messages;

/**
 * This class defines query operators available.
 * 
 * @author Philipp Nanz
 */
public final class Operators {

	public static final int ID_OP_BEFORE = 1;
	public static final int ID_OP_AFTER = 2;
	public static final int ID_OP_CONTAINS = 3;
	public static final int ID_OP_NOT_CONTAINS = 4;
	public static final int ID_OP_LESS_THAN = 5;
	public static final int ID_OP_GREATER_THAN = 6;
	public static final int ID_OP_EQUALS = 7;
	public static final int ID_OP_NOT_EQUALS = 8;
	public static final int ID_OP_BEGINS_WITH = 9;
	public static final int ID_OP_NOT_BEGINS_WITH = 10;

	public static final Operator OPERATOR_BEFORE = 
		new Operator(ID_OP_BEFORE, Messages.Operators_before, false);
	public static final Operator OPERATOR_AFTER = 
		new Operator(ID_OP_AFTER, Messages.Operators_after, false);
	public static final Operator OPERATOR_CONTAINS = 
		new Operator(ID_OP_CONTAINS, Messages.Operators_contains, false);
	public static final Operator OPERATOR_NOT_CONTAINS = 
		new Operator(ID_OP_NOT_CONTAINS, Messages.Operators_notContains, true);
	public static final Operator OPERATOR_LESS_THAN = 
		new Operator(ID_OP_LESS_THAN, Messages.Operators_lessThan, false);
	public static final Operator OPERATOR_GREATER_THAN = 
		new Operator(ID_OP_GREATER_THAN, Messages.Operators_greaterThan, false);
	public static final Operator OPERATOR_EQUALS = 
		new Operator(ID_OP_EQUALS, Messages.Operators_equals, false);
	public static final Operator OPERATOR_NOT_EQUALS = 
		new Operator(ID_OP_NOT_EQUALS, Messages.Operators_notEquals, true);
	public static final Operator OPERATOR_BEGINS_WITH = 
		new Operator(ID_OP_BEGINS_WITH, Messages.Operators_beginsWith, false);
	public static final Operator OPERATOR_NOT_BEGINS_WITH = 
		new Operator(ID_OP_NOT_BEGINS_WITH, Messages.Operators_notBeginsWith, true);

	private static final List<Operator> OPERATORS = new ArrayList<Operator>();

	static {
		OPERATORS.add(OPERATOR_AFTER);
		OPERATORS.add(OPERATOR_BEFORE);
		OPERATORS.add(OPERATOR_BEGINS_WITH);
		OPERATORS.add(OPERATOR_CONTAINS);
		OPERATORS.add(OPERATOR_EQUALS);
		OPERATORS.add(OPERATOR_GREATER_THAN);
		OPERATORS.add(OPERATOR_LESS_THAN);
		OPERATORS.add(OPERATOR_NOT_BEGINS_WITH);
		OPERATORS.add(OPERATOR_NOT_CONTAINS);
		OPERATORS.add(OPERATOR_NOT_EQUALS);
	}

	/**
	 * Hide constructor.
	 */
	private Operators() {
	}

	/**
	 * Returns the operator matching the given id.
	 * @param id the id of the operator
	 * @return the matching operator or <code>null</code>
	 */
	public static Operator getOperator(int id) {
		for (Operator op : OPERATORS) {
			if (op.getId() == id) {
				return op;
			}
		}
		return null;
	}
}
