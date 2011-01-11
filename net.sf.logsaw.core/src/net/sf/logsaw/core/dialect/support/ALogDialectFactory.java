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
package net.sf.logsaw.core.dialect.support;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;

import org.eclipse.core.runtime.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for log dialect factories.
 * 
 * @author Philipp Nanz
 */
public abstract class ALogDialectFactory implements ILogDialectFactory {

	private transient Logger logger;

	private String name;
	private String id;
	private String description;
	private String contributor;

	/**
	 * @return the name
	 */
	@Override
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Override
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	@Override
	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the contributor
	 */
	@Override
	public final String getContributor() {
		return contributor;
	}

	/**
	 * @param contributor the contributor to set
	 */
	@Override
	public final void setContributor(String contributor) {
		this.contributor = contributor;
	}

	/**
	 * @return the id
	 */
	@Override
	public final String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public final void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.dialect.ILogDialectFactory#createLogDialect()
	 */
	@Override
	public final ILogDialect createLogDialect() {
		ILogDialect dialect = doCreateLogDialect();
		Assert.isNotNull(dialect, "dialect");
		dialect.setFactory(this);
		return dialect;
	}

	/**
	 * Returns a newly created log dialect instance.
	 * @return a newly created log dialect instance
	 */
	protected abstract ILogDialect doCreateLogDialect();

	/**
	 * Returns the logger to use by this class.
	 * @return the logger to use
	 */
	protected final Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(getClass());
		}
		return logger;
	}
}
