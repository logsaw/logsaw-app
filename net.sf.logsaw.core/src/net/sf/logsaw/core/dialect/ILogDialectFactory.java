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

import net.sf.logsaw.core.dialect.ILogDialect;

/**
 * @author Philipp Nanz
 */
public interface ILogDialectFactory {

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * @return the description
	 */
	String getDescription();

	/**
	 * @param description the description to set
	 */
	void setDescription(String description);

	/**
	 * @return the contributor
	 */
	String getContributor();

	/**
	 * @param contributor the contributor to set
	 */
	void setContributor(String contributor);

	/**
	 * @return the id
	 */
	String getId();

	/**
	 * @param id the id to set
	 */
	void setId(String id);

	/**
	 * Returns a newly created log dialect instance.
	 */
	ILogDialect createLogDialect();
}
