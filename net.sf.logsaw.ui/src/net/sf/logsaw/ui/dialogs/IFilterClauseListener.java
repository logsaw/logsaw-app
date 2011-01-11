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

/**
 * @author Philipp Nanz
 */
public interface IFilterClauseListener {

	/**
	 * Callback method that is being called when the input of 
	 * the filter clause changes.
	 * 
	 * @param e the event
	 */
	void inputChanged(FilterClauseEvent e);
	
	/**
	 * Callback method that is being called when the remove button is pressed.
	 * 
	 * @param e the event
	 */
	void removeButtonPressed(FilterClauseEvent e);
}
