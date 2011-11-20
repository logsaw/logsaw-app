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
package net.sf.logsaw.core.dialect;

import java.io.InputStream;

import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

/**
 * This is the base interface for a log dialect.
 * 
 * @author Philipp Nanz
 */
public interface ILogDialect extends IAdaptable {

	/**
	 * @return the factory
	 */
	ILogDialectFactory getFactory();

	/**
	 * @param factory the factory to set
	 */
	void setFactory(ILogDialectFactory factory);

	/**
	 * Parse the given inputstream and feed the log events to the specified collector.
	 * <p>
	 * <strong>Note:</strong> The inputstream is unbuffered; it obliges 
	 * the dialect to do the necessary buffering.
	 * 
	 * @param log the owner log resource being parsed
	 * @param input the input stream to parse
	 * @param collector the log entry collector
	 * @throws CoreException if an error occurred
	 */
	void parse(ILogResource log, InputStream input, ILogEntryCollector collector) throws CoreException;

	/**
	 * Returns the log entry field provider for use by this dialect.
	 * @return the log entry field provider
	 */
	ILogFieldProvider getFieldProvider();
}
