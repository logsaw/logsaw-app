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
package net.sf.logsaw.ui;

import net.sf.logsaw.ui.impl.LogResourceManagerImpl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public final class UIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.logsaw.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIPlugin plugin;

	private ILogResourceManager logResourceManager;

	/**
	 * @return the logResourceManager
	 */
	public ILogResourceManager getLogResourceManager() {
		return logResourceManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeResourceManager();
		plugin = this;
	}

	private void initializeResourceManager() throws CoreException {
		logResourceManager = new LogResourceManagerImpl(getStateLocation());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		// Close log resource manager
		logResourceManager.close();
		logResourceManager = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Logs an error <strong>and</strong> shows an error message.
	 * <p>
	 * Something that <code>StatusManager</code> fails to provide.
	 * 
	 * @param e the exception to log and show
	 * @param blocking whether error dialog should be blocking
	 */
	public static void logAndShowError(CoreException e, boolean blocking) {
		// Log and show error
		StatusManager.getManager().handle(e, UIPlugin.PLUGIN_ID);
		StatusManager.getManager().handle(e.getStatus(), 
				blocking ? StatusManager.BLOCK : StatusManager.SHOW);
	}
}
