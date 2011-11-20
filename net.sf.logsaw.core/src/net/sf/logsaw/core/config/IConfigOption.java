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
package net.sf.logsaw.core.config;

import org.eclipse.core.runtime.CoreException;

/**
 * Interface that defines generic configuration options for use by 
 * log dialects and resources.
 * 
 * @author Philipp Nanz
 * @param <T> the value type
 */
public interface IConfigOption<T> {

	/**
	 * @return the key
	 */
	String getKey();

	/**
	 * @return the label
	 */
	String getLabel();

	/**
	 * @return the visible
	 */
	boolean isVisible();

	/**
	 * Implements the visitor pattern.
	 * @param visitor the visitor
	 * @throws CoreException if an error occurred
	 */
	void visit(IConfigOptionVisitor visitor, T value) throws CoreException;
}
