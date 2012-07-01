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

import java.util.EventObject;

/**
 * @author Philipp Nanz
 */
public class FilterClauseEvent extends EventObject {

	private static final long serialVersionUID = -7962037370595604167L;

	private boolean valid;

	/**
	 * Constructor.
	 * @param source the source object
	 * @param valid whether the source object is valid
	 */
	public FilterClauseEvent(Object source, boolean valid) {
		super(source);
		this.valid = valid;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}
}
