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
package net.sf.logsaw.dialect.websphere;

import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.dialect.ILogFieldProvider;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public final class WebsphereDialectPlugin extends Plugin {

	private ILogFieldProvider fieldProvider = new WebsphereFieldProvider();
	private ILogDialectFactory dialectFactory = new WebsphereDialectFactory();

	// The shared instance
	private static WebsphereDialectPlugin plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.logsaw.dialect.websphere"; //$NON-NLS-1$

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
	public static WebsphereDialectPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the dialectFactory
	 */
	public ILogDialectFactory getDialectFactory() {
		return dialectFactory;
	}

	/**
	 * @return the fieldProvider
	 */
	public ILogFieldProvider getFieldProvider() {
		return fieldProvider;
	}
}
