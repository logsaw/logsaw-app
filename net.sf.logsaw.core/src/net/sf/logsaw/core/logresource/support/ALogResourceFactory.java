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
package net.sf.logsaw.core.logresource.support;

import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;

import org.eclipse.core.runtime.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for log resource factories.
 * 
 * @author Philipp Nanz
 */
public abstract class ALogResourceFactory implements ILogResourceFactory {

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
	 * @see net.sf.logsaw.core.logresource.ILogResourceFactory#createLogResource()
	 */
	@Override
	public final ILogResource createLogResource() {
		ILogResource log = doCreateLogResource();
		Assert.isNotNull(log, "log");
		log.setFactory(this);
		return log;
	}

	/**
	 * Returns a newly created log resource instance.
	 * @return a newly created log resource instance
	 */
	protected abstract ILogResource doCreateLogResource();

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
