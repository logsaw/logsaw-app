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
package net.sf.logsaw.index.internal;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.IndexPlugin;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for executing code which requires an <code>IndexWriter</code>.
 * 
 * @author Philipp Nanz
 * @param <T> the return type
 */
public abstract class ARunWithIndexWriter<T> {

	private static transient Logger logger = LoggerFactory.getLogger(ARunWithIndexWriter.class);

	/**
	 * Opens a Lucene index writer, executes the callback method and then closes the writer.
	 * @param log the log resource, may be <code>null</code>
	 * @param analyzer the Lucene analyzer to set on the index writer
	 * @param matchVersion the Lucene match version
	 * @return any object or <code>null</code>
	 * @throws CoreException if an <strong>expected</strong> error occurred
	 */
	protected final T runWithIndexWriter(ILogResource log, Analyzer analyzer, 
			Version matchVersion) throws CoreException {
		logger.info("Opening index writer for '" + log.getName() + "'..."); //$NON-NLS-1$ //$NON-NLS-2$
		IndexWriter writer = null;
		try {
			Directory dir = FSDirectory.open(IndexPlugin.getDefault().getIndexFile(log));
			LogMergePolicy mp = new LogByteSizeMergePolicy();
			mp.setMergeFactor(30);
			IndexWriterConfig cfg = new IndexWriterConfig(matchVersion, analyzer);
			cfg.setMaxBufferedDocs(1000);
			cfg.setMergePolicy(mp);
			writer = new IndexWriter(dir, cfg);
			try {
				return doRunWithIndexWriter(writer, log);
			} finally {
				logger.info("Closing index writer for '" + log.getName() + "'..."); //$NON-NLS-1$ //$NON-NLS-2$
				writer.close();
			}
		} catch (CoreException e) {
			// Rethrow original CoreException
			throw e;
		} catch (Exception e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LuceneIndexService_error_failedToUpdateIndex, 
							new Object[] {log.getName(), e.getLocalizedMessage()}), e));
		}
	}

	/**
	 * Callback method being called by <code>runWithIndexWriter(ILogResource, ILogDialect)</code>.
	 * @param writer the index writer
	 * @param log the log resource, may be <code>null</code>
	 * @return any object or <code>null</code>
	 * @throws CoreException if an <strong>expected</strong> error occurred
	 */
	protected abstract T doRunWithIndexWriter(IndexWriter writer, ILogResource log) throws CoreException;

}
