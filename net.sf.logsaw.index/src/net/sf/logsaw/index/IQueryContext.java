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
package net.sf.logsaw.index;

import java.io.IOException;

import net.sf.logsaw.core.logresource.ILogResource;

/**
 * @author Philipp Nanz
 */
public interface IQueryContext {

	/**
	 * Returns the log resource held by this query context.
	 * @return the log resource
	 */
	ILogResource getLogResource();

	/**
	 * Returns whether this query context is open.
	 * @return <code>true</code> if query context is open
	 */
	boolean isOpen();

	/**
	 * Closes all resources held by this query context.
	 * @throws IOException if an error occurred
	 */
	void close() throws IOException;
}
