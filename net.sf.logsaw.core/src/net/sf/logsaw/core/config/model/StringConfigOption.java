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
package net.sf.logsaw.core.config.model;

import net.sf.logsaw.core.config.IConfigOptionVisitor;
import net.sf.logsaw.core.config.support.AConfigOption;

import org.eclipse.core.runtime.CoreException;

/**
 * A simple String based config option.
 * 
 * @author Philipp Nanz
 */
public final class StringConfigOption extends AConfigOption<String> {

	/**
	 * Constructor with <code>visible</code> defaulting to <code>true</code>.
	 * @param name the unique key of the config option
	 * @param label the label to display
	 */
	public StringConfigOption(String key, String label) {
		super(key, label);
	}

	/**
	 * Constructor.
	 * @param key the unique key of the config option
	 * @param label the label to display
	 * @param visible whether to display this config option in the UI
	 */
	public StringConfigOption(String key, String label, boolean visible) {
		super(key, label, visible);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigOption#visit(net.sf.logsaw.core.config.IConfigOptionVisitor, java.lang.Object)
	 */
	@Override
	public void visit(IConfigOptionVisitor visitor, String value) throws CoreException {
		visitor.visit(this, value);
	}
}
