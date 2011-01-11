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
package net.sf.logsaw.core.dialect;

import java.util.List;

import net.sf.logsaw.core.field.Level;

/**
 * @author Philipp Nanz
 */
public interface ILogLevelProvider {

	/** The default ID of the dummy unknown level */
	int ID_LEVEL_UNKNOWN = -1;

	/**
	 * Returns a collection of log levels.
	 * @return a list containing log levels
	 */
	List<Level> getLevels();

	/**
	 * Returns the level with the given name.
	 * @param name the name
	 * @return the level or the unknown level with the given string as name
	 */
	Level findLevel(String name);

	/**
	 * Returns the level with the given value.
	 * @param value the value
	 * @return the level or <code>null</code>
	 */
	Level findLevel(int value);

	/**
	 * Returns the icon path for the given log level or <code>null</code>.
	 * @param lvl the level
	 * @return the icon path or <code>null</code>
	 */
	String getIconPathForLevel(Level lvl);
}
