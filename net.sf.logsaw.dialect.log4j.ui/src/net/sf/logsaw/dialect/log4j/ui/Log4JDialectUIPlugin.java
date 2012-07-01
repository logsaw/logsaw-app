/*******************************************************************************
 * Copyright (c) 2011 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.dialect.log4j.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public final class Log4JDialectUIPlugin extends AbstractUIPlugin {

	public static final String PREF_PATTERNS = "PATTERNS"; //$NON-NLS-1$

	// The shared instance
	private static Log4JDialectUIPlugin plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.logsaw.dialect.log4j.ui"; //$NON-NLS-1$

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
	public static Log4JDialectUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Loads the saved patterns from the preference store.
	 * @return the saved patterns
	 * @throws IOException if an IO error occurred
	 */
	public Set<String> loadKnownPatterns() throws IOException {
		Set<String> ret = new HashSet<String>();
		String str = getPreferenceStore().getString(PREF_PATTERNS);
		if (str != null) {
			BufferedReader reader = new BufferedReader(new StringReader(str));
			String line = null;
			while ((line = reader.readLine()) != null) {
				ret.add(line);
			}
		}
		return ret;
	}

	/**
	 * Saves the patterns into the preference store.
	 * @param patterns the patterns to save
	 * @throws IOException if an IO error occurred
	 */
	public void saveKnownPatterns(Set<String> patterns) throws IOException {
		StringWriter buffer = new StringWriter();
		BufferedWriter writer = new BufferedWriter(buffer);
		boolean first = true;
		for (String pattern : patterns) {
			if (first) {
				first = false;
			} else {
				writer.newLine();
			}
			writer.append(pattern.trim());
		}
		writer.flush();
		getPreferenceStore().setValue(PREF_PATTERNS, buffer.toString());
	}
}
