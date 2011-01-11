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
package net.sf.logsaw.ui;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.SynchronizationResult;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Philipp Nanz
 */
public interface ILogResourceManager {

	/**
	 * Closes the manager instance.
	 * 
	 * @throws CoreException if an error occurred
	 */
	void close() throws CoreException;

	/**
	 * Removes the given log resource from the internal data structure.
	 * 
	 * @param log the log file to remove
	 * @throws CoreException if an error occurred
	 */
	void add(ILogResource log) throws CoreException;

	/**
	 * Triggers index synchronization for the given log resource.
	 * <p>
	 * This operation is being executed asynchronously. On finish <code>callback</code> 
	 * will be called with the log resources that had changes as arguments.
	 * 
	 * @param log the log resource to synchronize
	 * @param callback listener to be called upon completion
	 * @throws CoreException if an error occurred
	 */
	void synchronize(ILogResource log, IGenericCallback<SynchronizationResult> callback) 
			throws CoreException;

	/**
	 * Returns whether the given log resource is currently in use by an asynchronous job.
	 * @param log the log resource to check
	 * @return <code>true</code> if the log resource is currently in use
	 */
	boolean isJobInProgress(ILogResource log);

	/**
	 * Removes the given log resource from the internal data structure.
	 * 
	 * @param log the log file to remove
	 * @throws CoreException if an error occurred
	 */
	void remove(ILogResource log) throws CoreException;

	/**
	 * Returns the log resources contained in the internal data structure.
	 * 
	 * @return the log resources
	 * @throws CoreException if an error occurred
	 */
	ILogResource[] getAll() throws CoreException;

	/**
	 * Persists the internal data structure into a file.
	 * @throws CoreException if an error occurred
	 */
	void saveState() throws CoreException;
}