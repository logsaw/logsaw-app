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
package net.sf.logsaw.dialect.log4j;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.dialect.support.ALogLevelProvider;
import net.sf.logsaw.core.field.Level;

/**
 * @author Philipp Nanz
 */
public final class Log4JLevelProvider extends ALogLevelProvider {

	public static final Level LEVEL_TRACE = new Level(1, "TRACE"); //$NON-NLS-1$
	public static final Level LEVEL_DEBUG = new Level(2, "DEBUG"); //$NON-NLS-1$
	public static final Level LEVEL_INFO = new Level(3, "INFO"); //$NON-NLS-1$
	public static final Level LEVEL_WARN = new Level(4, "WARN"); //$NON-NLS-1$
	public static final Level LEVEL_ERROR = new Level(5, "ERROR"); //$NON-NLS-1$
	public static final Level LEVEL_FATAL = new Level(6, "FATAL"); //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogLevelProvider#getLevels()
	 */
	@Override
	public List<Level> getLevels() {
		List<Level> ret = new ArrayList<Level>();
		ret.add(LEVEL_TRACE);
		ret.add(LEVEL_DEBUG);
		ret.add(LEVEL_INFO);
		ret.add(LEVEL_WARN);
		ret.add(LEVEL_ERROR);
		ret.add(LEVEL_FATAL);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogLevelProvider#getIconPathForLevel(net.sf.logsaw.core.Level)
	 */
	@Override
	public String getIconPathForLevel(Level lvl) {
		String icon = null;
		if (LEVEL_TRACE.equals(lvl)) {
			icon = "lvl_trace.gif"; //$NON-NLS-1$
		} else if (LEVEL_DEBUG.equals(lvl)) {
			icon = "lvl_debug.gif"; //$NON-NLS-1$
		} else if (LEVEL_INFO.equals(lvl)) {
			icon = "lvl_info.gif"; //$NON-NLS-1$
		} else if (LEVEL_WARN.equals(lvl)) {
			icon = "lvl_warn.gif"; //$NON-NLS-1$
		} else if (LEVEL_ERROR.equals(lvl)) {
			icon = "lvl_error.gif"; //$NON-NLS-1$
		} else if (LEVEL_FATAL.equals(lvl)) {
			icon = "lvl_fatal.gif"; //$NON-NLS-1$
		}
		
		if (icon == null) {
			// That's it
			return null;
		}
		return "icons/" + icon; //$NON-NLS-1$
	}
}
