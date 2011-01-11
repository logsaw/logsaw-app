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

import java.io.IOException;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.IQueryContext;

import org.apache.lucene.index.IndexReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Lucene specific implementation of <code>IQueryContext</code>.
 * <p>
 * It can be used to cache one <code>IndexReader</code> instance across multiple calls.
 * 
 * @author Philipp Nanz
 */
public class LuceneQueryContextImpl implements IQueryContext {

	private static transient Logger logger = LoggerFactory.getLogger(LuceneQueryContextImpl.class);

	private boolean open = true;
	private ILogResource logResource;
	private IndexReader indexReader;

	/**
	 * Constructor.
	 * @param logResource the log resource to query
	 */
	public LuceneQueryContextImpl(ILogResource logResource) {
		this.logResource = logResource;
	}

	/**
	 * @return the indexReader
	 */
	protected IndexReader getIndexReader() {
		return indexReader;
	}

	/**
	 * @param reader the indexReader to set
	 */
	protected void setIndexReader(IndexReader reader) {
		this.indexReader = reader;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IQueryContext#getLogResource()
	 */
	@Override
	public ILogResource getLogResource() {
		return logResource;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexReaderSession#close()
	 */
	@Override
	public void close() throws IOException {
		open = false;
		if (indexReader != null) {
			logger.info("Closing index reader owned by " + toString()); //$NON-NLS-1$ //$NON-NLS-2$
			indexReader.close();
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexReaderSession#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return open;
	}
}
