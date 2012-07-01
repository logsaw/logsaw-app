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

import java.util.Date;
import java.util.Set;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;

/**
 * This interface defines the contract for a service that manages available 
 * operators and can create restrictions.
 * 
 * @author Philipp Nanz
 */
public interface IRestrictionFactory {

	/**
	 * Returns the possible operators for the given field.
	 * @param fld the log entry field
	 * @return a set of possible operators
	 */
	Set<Operator> getOperators(ALogEntryField<?, ?> fld);
	
	/**
	 * Returns a newly constructed restrictions representing the given input.
	 * @param fld the field to apply the restriction to
	 * @param op the operator to use
	 * @param val the value to match
	 * @return the newly constructed restriction
	 */
	StringRestriction newRestriction(StringLogEntryField fld, Operator op, String val);

	/**
	 * Returns a newly constructed restrictions representing the given input.
	 * @param fld the field to apply the restriction to
	 * @param op the operator to use
	 * @param val the value to match
	 * @return the newly constructed restriction
	 */
	LevelRestriction newRestriction(LevelLogEntryField fld, Operator op, Level val);

	/**
	 * Returns a newly constructed restrictions representing the given input.
	 * @param fld the field to apply the restriction to
	 * @param op the operator to use
	 * @param val the value to match
	 * @return the newly constructed restriction
	 */
	DateRestriction newRestriction(DateLogEntryField fld, Operator op, Date val);
}
