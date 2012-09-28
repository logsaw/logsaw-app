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
package net.sf.logsaw.ui.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Philipp Nanz
 */
public interface IFilterClauseRenderer {

	/**
	 * Adds the given listener to the internal list of listeners.
	 * @param listener the listener to add
	 */
	void addFilterClauseListener(IFilterClauseListener listener);

	/**
	 * Removes the given listener from the internal list of listeners.
	 * @param listener the listener to remove
	 */
	void removeFilterClauseListener(IFilterClauseListener listener);

	/**
	 * Creates the control.
	 * @param parent the parent composite
	 */
	void createControl(Composite parent);

	/**
	 * Causes the receiver to have the focus.
	 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
	 */
	boolean setFocus();

	/**
	 * Releases all associated resources.
	 */
	void dispose();

	/**
	 * @return the control
	 */
	Control getControl();

	/**
	 * Validates the input of this filter clause.
	 */
	void validateInput();

	/**
	 * Returns whether this clause is valid.
	 * @return <code>true</code> if valid
	 */
	boolean isValid();

	/**
	 * @return the value
	 */
	String getValue();

	/**
	 * @param value the value to set
	 */
	void setValue(String value);
}
