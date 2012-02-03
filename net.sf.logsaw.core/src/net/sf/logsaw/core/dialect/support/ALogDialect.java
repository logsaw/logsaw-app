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

import java.util.Collections;
import java.util.List;

import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.support.AConfigurableObject;
import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for log dialects.
 * 
 * @author Philipp Nanz
 */
public abstract class ALogDialect extends AConfigurableObject implements ILogDialect {

	private transient Logger logger;

	private ILogDialectFactory factory;

	/**
	 * @return the factory
	 */
	@Override
	public final ILogDialectFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory the factory to set
	 */
	@Override
	public final void setFactory(ILogDialectFactory factory) {
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
		if (getFieldProvider() == null) {
			getLogger().error("Field provider not set"); //$NON-NLS-1$
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
