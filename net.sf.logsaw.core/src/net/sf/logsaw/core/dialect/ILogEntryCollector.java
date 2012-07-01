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

import java.io.IOException;
import java.util.List;

import net.sf.logsaw.core.field.LogEntry;

import org.eclipse.core.runtime.IStatus;

/**
 * This interface builds the contract for reading log files.
 * 
 * @author Philipp Nanz
 */
public interface ILogEntryCollector {

	/**
	 * This methods is fired by the concrete <code>ILogDialect</code> 
	 * every time a new log event has been read.
	 * 
	 * @param entry a map containing the fields as label and value pairs
	 * @throws IOException if an IO error occurred
	 * @return <code>true</code> if the passed entry has been added to the index
	 */
	boolean collect(LogEntry entry) throws IOException;

	/**
	 * Returns the number of log entries collected by this instance.
	 * @return the number of log entries collected
	 */
	int getTotalCollected();

	/**
	 * Adds the given message to the internal list of messages.
	 * @param msg the message
	 */
	void addMessage(IStatus msg);

	/**
	 * Returns the list of messages added during collection.
	 * @return the non-<code>null</code> list of messages
	 */
	List<IStatus> getMessages();

	/**
	 * Returns whether cancel has been requested by the user.
	 * @return <code>true</code> if cancel has been requested
	 */
	boolean isCanceled();
}
