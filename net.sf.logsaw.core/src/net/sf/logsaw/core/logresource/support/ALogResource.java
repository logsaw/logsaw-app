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

import java.util.Collections;
import java.util.List;

import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.support.AConfigurableObject;
import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for log resources.
 * 
 * @author Philipp Nanz
 */
public abstract class ALogResource extends AConfigurableObject implements ILogResource {

	private transient Logger logger;

	private String name;
	private String pk;
	private ILogDialect dialect;
	private ILogResourceFactory factory;

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
	 * @return the pk
	 */
	@Override
	public final String getPK() {
		return pk;
	}

	/**
	 * @param pk the pk to set
	 */
	@Override
	public final void setPK(String pk) {
		this.pk = pk;
	}

	/**
	 * @return the dialect
	 */
	@Override
	public final ILogDialect getDialect() {
		return dialect;
	}

	/**
	 * @param dialect the dialect to set
	 */
	@Override
	public final void setDialect(ILogDialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * @return the factory
	 */
	@Override
	public final ILogResourceFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory the factory to set
	 */
	@Override
	public final void setFactory(ILogResourceFactory factory) {
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#getRequiredConfigOptions()
	 */
	@Override
	public List<IConfigOption<?>> getRequiredConfigOptions() {
		// Subclasses may override
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.support.AConfigurableObject#isConfigured()
	 */
	@Override
	public boolean isConfigured() {
		// Validate field provider
		if (getDialect() == null) {
			getLogger().error("Dialect not set"); //$NON-NLS-1$
			return false;
		}
		return super.isConfigured();
	}

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
