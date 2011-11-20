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
package net.sf.logsaw.core.dialect.support;

import java.util.HashMap;
import java.util.Map;

import net.sf.logsaw.core.dialect.ILogLevelProvider;
import net.sf.logsaw.core.field.Level;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public abstract class ALogLevelProvider implements ILogLevelProvider {

	private Map<String, Level> nameToLevel = new HashMap<String, Level>();
	private Map<Integer, Level> valueToLevel = new HashMap<Integer, Level>();

	/**
	 * Default constructor.
	 */
	public ALogLevelProvider() {
		for (Level lvl : getLevels()) {
			Assert.isTrue(lvl.getValue() > 0, "Level value must be a positive integer"); //$NON-NLS-1$
			nameToLevel.put(lvl.getName().toLowerCase(), lvl);
			valueToLevel.put(lvl.getValue(), lvl);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILevelProvider#findLevel(java.lang.String)
	 */
	@Override
	public final Level findLevel(String name) {
		Assert.isNotNull(name, "name"); //$NON-NLS-1$
		name = doRewriteAlias(name);
		Level lvl = nameToLevel.get(name.toLowerCase());
		if (lvl == null) {
			lvl = new Level(ID_LEVEL_UNKNOWN, name);
		}
		return lvl;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILevelProvider#findLevel(int)
	 */
	@Override
	public final Level findLevel(int value) {
		return valueToLevel.get(Integer.valueOf(value));
	}

	/**
	 * Override this method to allow rewriting of alias names to the 
	 * internal representation.
	 * @param name the name
	 * @return either the original or a modified name
	 */
	protected String doRewriteAlias(String name) {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogLevelProvider#getIconPathForLevel(net.sf.logsaw.core.Level)
	 */
	@Override
	public String getIconPathForLevel(Level lvl) {
		return null;
	}
}
