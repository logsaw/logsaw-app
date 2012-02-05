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
package net.sf.logsaw.core.field;

import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.Assert;

/**
 * Abstract base class for indexed log entry fields.
 * 
 * @author Philipp Nanz
 * @param <IT> the index type
 * @param <VT> the value type
 */
public abstract class ALogEntryField<IT, VT> {

	private String key;
	private String label;

	/**
	 * Constructor.
	 * 
	 * @param key the key
	 * @param label the label
	 */
	public ALogEntryField(String key, String label) {
		Assert.isNotNull(key, "key must not be null"); //$NON-NLS-1$
		Assert.isNotNull(label, "label must not be null"); //$NON-NLS-1$
		this.key = key;
		this.label = label;
	}

	/**
	 * Returns the key that will be used internally for indexing the field.
	 * 
	 * @return the key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * Returns the label to display for the field.
	 * 
	 * @return the label
	 */
	public final String getLabel() {
		return label;
	}

	/**
	 * Converts the given index object (represented as <code>String</code>) 
	 * that was retrieved from the index into a value object.
	 * 
	 * @param str the index representation of the value
	 * @return the value object
	 */
	public abstract VT fromIndexedValue(String str);

	/**
	 * Converts the given value object into an index object for storing 
	 * in the index.
	 * 
	 * @param obj the value object
	 * @return the index object
	 */
	public abstract IT toIndexedValue(VT obj);

	/**
	 * Returns whether the given input <code>String</code> is a valid input 
	 * for this log entry field.
	 * 
	 * @param str the <code>String</code> to validate
	 * @param log the log resource
	 * @return <code>true</code> if valid
	 */
	public abstract boolean isValidInput(String str, ILogResource log);

	/**
	 * Converts the given <code>String</code> that was input in the UI  
	 * into a value object.
	 * 
	 * @param str the <code>String</code> representation
	 * @param log the log resource
	 * @return the value object
	 */
	public abstract VT fromInputValue(String str, ILogResource log);

	/**
	 * Converts the given value object to a <code>String</code> for display 
	 * in the UI.
	 * 
	 * @param obj the value object
	 * @param log the log resource
	 * @return the <code>String</code> representation
	 */
	public abstract String toInputValue(VT obj, ILogResource log);

	/**
	 * Implements the visitor pattern.
	 * @param visitor the visitor
	 */
	public abstract void visit(ILogEntryFieldVisitor visitor);

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ALogEntryField<?, ?> other = (ALogEntryField<?, ?>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
