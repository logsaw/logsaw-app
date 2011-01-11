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
package net.sf.logsaw.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.field.ALogEntryField;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public abstract class AFilterClauseRenderer implements IFilterClauseRenderer {

	private boolean valid;
	private ALogEntryField<?, ?> field;
	private List<IFilterClauseListener> listeners = 
		new ArrayList<IFilterClauseListener>();

	/**
	 * Constructor.
	 * @param field the field
	 */
	public AFilterClauseRenderer(ALogEntryField<?, ?> field) {
		Assert.isNotNull(field, "field"); //$NON-NLS-1$
		this.field = field;
	}

	/**
	 * Adds the given listener to the internal list of listeners.
	 * @param listener the listener to add
	 */
	@Override
	public void addFilterClauseListener(IFilterClauseListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the given listener from the internal list of listeners.
	 * @param listener the listener to remove
	 */
	@Override
	public void removeFilterClauseListener(IFilterClauseListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fires the inputChanged event on registered listeners.
	 * @param valid whether the input is valid
	 */
	protected void fireInputChanged(boolean valid) {
		FilterClauseEvent e = new FilterClauseEvent(this, valid);
		for (IFilterClauseListener listener : listeners) {
			listener.inputChanged(e);
		}
	}

	/**
	 * @return the valid
	 */
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	protected void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the field
	 */
	protected ALogEntryField<?, ?> getField() {
		return field;
	}
}
