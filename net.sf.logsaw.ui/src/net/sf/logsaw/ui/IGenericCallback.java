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
package net.sf.logsaw.ui;

/**
 * This interface defines a generic callback for use by methods that 
 * return asynchronously.
 * 
 * @author Philipp Nanz
 * @param <T> the type of the callback payload
 */
public interface IGenericCallback<T> {
	
	/**
	 * Execute the callback.
	 * @param payload the payload
	 */
	void doCallback(T payload);
}
