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
package net.sf.logsaw.core.logresource;

import net.sf.logsaw.core.config.model.StringConfigOption;

/**
 * This interface is to be implemented by log sources which 
 * define their own timestamp pattern.
 * 
 * @author Philipp Nanz
 */
public interface IHasTimestampPattern {

	StringConfigOption OPTION_TIMESTAMP_PATTERN = 
		new StringConfigOption("timestampPattern", "Timestamp pattern", false); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * @return the timestamp pattern
	 */
	String getTimestampPattern();
}
