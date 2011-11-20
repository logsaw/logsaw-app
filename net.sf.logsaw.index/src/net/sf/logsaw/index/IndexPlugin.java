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

import java.io.File;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.internal.LuceneIndexServiceImpl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public final class IndexPlugin extends Plugin {

	private final IIndexService indexService = new LuceneIndexServiceImpl();

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.logsaw.index"; //$NON-NLS-1$

	// The shared instance
	private static IndexPlugin plugin;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IndexPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the indexService
	 */
	public IIndexService getIndexService() {
		return indexService;
	}

	/**
	 * Returns the Lucene index directory for the given id.
	 * @param id the id
	 * @return the index directory
	 */
	public File getIndexFile(ILogResource log) {
		Assert.isNotNull(log, "log");
		return getStateLocation().append(log.getPK()).toFile();
	}
}
