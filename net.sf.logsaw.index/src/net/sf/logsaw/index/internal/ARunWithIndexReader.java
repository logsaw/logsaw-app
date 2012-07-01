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
package net.sf.logsaw.index.internal;

import java.io.IOException;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.IndexPlugin;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for executing code which requires an <code>IndexReader</code>.
 * 
 * @author Philipp Nanz
 * @param <T> the return type
 */
public abstract class ARunWithIndexReader<T> {

	private static transient Logger logger = LoggerFactory.getLogger(ARunWithIndexReader.class);

	private LuceneQueryContextImpl queryContext;

	/**
	 * Obtains a index reader from the query context or opens a new one.
	 * @param log the log resource
	 * @return a new index reader or <code>null</code>
	 * @throws IOException if an error occurred
	 */
	protected IndexReader getFromQueryContextOrOpenIndexReader(ILogResource log) throws IOException {
		IndexReader reader = null;
		if (queryContext != null) {
			reader = queryContext.getIndexReader();
			if (reader == null) {
				reader = openReader(log);
				logger.info("Binding index reader to query context " + queryContext.toString()); //$NON-NLS-1$
				queryContext.setIndexReader(reader);
			}
			return reader;
		}
		return openReader(log);
	}

	/**
	 * Opens a fresh index reader.
	 * @param log the log resource
	 * @return a new index reader or <code>null</code>
	 * @throws IOException if an error occurred
	 */
	protected IndexReader openReader(ILogResource log) throws IOException {
		IndexReader reader = null;
		Directory dir = FSDirectory.open(IndexPlugin.getDefault().getIndexFile(log));
		if (IndexReader.indexExists(dir)) {
			logger.info("Opening index reader for '" + log.getName() + "'..."); //$NON-NLS-1$ //$NON-NLS-2$
			reader = IndexReader.open(dir);
		}
		return reader;
	}

	/**
	 * @param queryContext the queryContext to set
	 */
	protected void setQueryContext(LuceneQueryContextImpl queryContext) {
		this.queryContext = queryContext;
	}

	/**
	 * Opens a Lucene index reader, executes the callback method and then closes the reader.
	 * @param log the log resource, may be <code>null</code>
	 * @return any object or <code>null</code>
	 * @throws CoreException if an <strong>expected</strong> error occurred
	 */
	protected final T runWithIndexReader(ILogResource log) throws CoreException {
		IndexReader reader = null;
		try {
			reader = getFromQueryContextOrOpenIndexReader(log);
			try {
				return doRunWithIndexReader(reader, log);
			} finally {
				if ((queryContext == null) && (reader != null)) {
					// Close only if not in session
					logger.info("Closing index reader for '" + log.getName() + "'..."); //$NON-NLS-1$ //$NON-NLS-2$
					reader.close();
				}
			}
		} catch (CoreException e) {
			// Rethrow original CoreException
			throw e;
		} catch (Exception e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LuceneIndexService_error_failedToReadIndex, 
							new Object[] {log.getName(), e.getLocalizedMessage()}), e));
		}
	}

	/**
	 * Callback method being called by <code>runWithIndexReader(ILogResource, ILogDialect)</code>.
	 * @param reader the index reader, may be <code>null</code> to indicate that index does not exist yet
	 * @param log the log resource, may be <code>null</code>
	 * @return any object or <code>null</code>
	 * @throws CoreException if an <strong>expected</strong> error occurred
	 */
	protected abstract T doRunWithIndexReader(IndexReader reader, ILogResource log) throws CoreException;

}
