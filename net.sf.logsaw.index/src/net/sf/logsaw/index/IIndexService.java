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

import java.util.List;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.support.ARestriction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This interface defines methods for updating and querying the log index.
 * 
 * @author Philipp Nanz
 */
public interface IIndexService {

	/**
	 * Synchronizes the log index for the given log resource.
	 * @param log the log resource to index
	 * @param monitor the progress monitor
	 * @throws CoreException if synchronization was interrupted by an error
	 * @return the number of log entries added
	 */
	SynchronizationResult synchronize(ILogResource log, IProgressMonitor monitor) throws CoreException;

	/**
	 * Unlocks the index for the given log resource.
	 * @param log the log resource to unlock
	 * @return <code>true</code> when log resource was locked <strong>AND</strong> has been unlocked
	 * @throws CoreException if an error occurs
	 */
	boolean unlock(ILogResource log) throws CoreException;

	/**
	 * Returns the number of entries indexed for the given log resource.
	 * @param log the log resource to index
	 * @return the number of entries in the given log resource
	 * @throws CoreException if an error occurs
	 */
	int count(ILogResource log) throws CoreException;

	/**
	 * Returns the size of the index on disk.
	 * @param log the log resource to index
	 * @return the human-readable size of the index
	 */
	String size(ILogResource log);

	/**
	 * Truncates the given index.
	 * @param log the log resource to truncate
	 * @throws CoreException if an error occurs
	 */
	void truncate(ILogResource log) throws CoreException;

	/**
	 * Creates an index for the given log resource.
	 * @param log the log resource to create an index for
	 * @throws CoreException if an error occurs
	 */
	void createIndex(ILogResource log) throws CoreException;

	/**
	 * Deletes the given index.
	 * @param log the log resource to delete
	 * @throws CoreException if an error occurs
	 */
	void deleteIndex(ILogResource log) throws CoreException;

	/**
	 * Returns the log entries for the given query.
	 * @param context a context object, used for caching state across multiple calls
	 * @param restrictions the query restrictions
	 * @param offset the offset for the resulting page
	 * @param limit the limit for the resulting page
	 * @return a non-<code>null</code> list of log entries
	 * @throws CoreException if an error occurs
	 */
	ResultPage query(IQueryContext context, List<ARestriction<?>> restrictions, 
			int offset, int limit) throws CoreException;

	/**
	 * Returns a newly constructed query context object that is required to 
	 * issue queries.
	 * @param log the log resource to query
	 * @return the query context object
	 */
	IQueryContext createQueryContext(ILogResource log);
}
