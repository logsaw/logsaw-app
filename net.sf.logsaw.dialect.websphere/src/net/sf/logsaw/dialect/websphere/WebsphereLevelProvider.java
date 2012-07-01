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

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.dialect.support.ALogLevelProvider;
import net.sf.logsaw.core.field.Level;

/**
 * @author Philipp Nanz
 */
public final class WebsphereLevelProvider extends ALogLevelProvider {

	public static final Level LEVEL_STDOUT = new Level(1, "STDOUT"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Level LEVEL_STDERR = new Level(2, "STDERR"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Level LEVEL_AUDIT = new Level(3, "AUDIT"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Level LEVEL_INFO = new Level(4, "INFO"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Level LEVEL_WARN = new Level(5, "WARN"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Level LEVEL_ERROR = new Level(6, "ERROR"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Level LEVEL_FATAL = new Level(7, "FATAL"); //$NON-NLS-1$ //$NON-NLS-2$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.support.ALogLevelProvider#doRewriteAlias(java.lang.String)
	 */
	@Override
	protected String doRewriteAlias(String name) {
		// In log files, the following aliases are used
		if ("O".equals(name)) { //$NON-NLS-1$
			return LEVEL_STDOUT.getName();
		} else if ("R".equals(name)) { //$NON-NLS-1$
			return LEVEL_STDERR.getName();
		} else if ("A".equals(name)) { //$NON-NLS-1$
			return LEVEL_AUDIT.getName();
		} else if ("I".equals(name)) { //$NON-NLS-1$
			return LEVEL_INFO.getName();
		} else if ("W".equals(name)) { //$NON-NLS-1$
			return LEVEL_WARN.getName();
		} else if ("E".equals(name)) { //$NON-NLS-1$
			return LEVEL_ERROR.getName();
		} else if ("F".equals(name)) { //$NON-NLS-1$
			return LEVEL_FATAL.getName();
		}
		return super.doRewriteAlias(name);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogLevelProvider#getLevels()
	 */
	@Override
	public List<Level> getLevels() {
		List<Level> ret = new ArrayList<Level>();
		ret.add(LEVEL_STDOUT);
		ret.add(LEVEL_STDERR);
		ret.add(LEVEL_AUDIT);
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
		if (LEVEL_STDOUT.equals(lvl)) {
			icon = "lvl_stdout.gif"; //$NON-NLS-1$
		} else if (LEVEL_STDERR.equals(lvl)) {
			icon = "lvl_stderr.gif"; //$NON-NLS-1$
		} else if (LEVEL_AUDIT.equals(lvl)) {
			icon = "lvl_audit.gif"; //$NON-NLS-1$
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
