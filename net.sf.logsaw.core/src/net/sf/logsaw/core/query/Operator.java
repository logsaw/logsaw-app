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

import org.eclipse.core.runtime.Assert;

/**
 * This class represents an query operator.
 * 
 * @author Philipp Nanz
 */
public final class Operator {

	private int id;
	private String label;
	private boolean negative;

	/**
	 * Constructor.
	 * @param id the ID of the operator
	 * @param label the label of the operator
	 * @param negative whether this operator is negative (i.e. not contains, not equals)
	 */
	public Operator(int id, String label, boolean negative) {
		Assert.isTrue(id > 0, "id must be greater than 0"); //$NON-NLS-1$
		Assert.isNotNull(label, "label"); //$NON-NLS-1$
		this.id = id;
		this.label = label;
		this.negative = negative;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the negative
	 */
	public boolean isNegative() {
		return negative;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Operator other = (Operator) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
