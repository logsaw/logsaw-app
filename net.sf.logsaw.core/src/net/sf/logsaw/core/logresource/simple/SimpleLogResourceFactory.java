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
package net.sf.logsaw.core.logresource.simple;

import java.io.File;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.internal.logresource.simple.SimpleLogResource;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.support.ALogResourceFactory;
import net.sf.logsaw.core.util.LocaleUtils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Philipp Nanz
 */
public final class SimpleLogResourceFactory extends ALogResourceFactory {

	public static final StringConfigOption OPTION_LOGFILE = 
		new StringConfigOption("logFile", "Log file"); //$NON-NLS-1$ //$NON-NLS-2$

	private static final SimpleLogResourceFactory INSTANCE = new SimpleLogResourceFactory();

	/**
	 * Constructor.
	 */
	public SimpleLogResourceFactory() {
		// TODO Remove when extension point is properly wired up
		setName("Simple Log Resource"); //$NON-NLS-1$
	}

	/**
	 * Returns the singleton instance of <code>SimpleLogResourceFactoryImpl</code>.
	 * @return the singleton instance
	 */
	public static SimpleLogResourceFactory getInstance() {
		return INSTANCE;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.logresource.support.ALogResourceFactory#doCreateLogResource()
	 */
	@Override
	protected ILogResource doCreateLogResource() {
		return new SimpleLogResource();
	}

	/**
	 * Custom factory method.
	 * 
	 * @param logFile the log file to read
	 * @param encoding the file encoding
	 * @param locale the locale to use
	 * @param timeZone the timezone
	 * @param dialect the dialect to use for this resource
	 */
	public ILogResource createLogResource(File logFile, String encoding, Locale locale, 
			TimeZone timeZone, ILogDialect dialect) {
		Assert.isNotNull(locale, "locale"); //$NON-NLS-1$
		Assert.isNotNull(logFile, "logFile"); //$NON-NLS-1$
		Assert.isNotNull(timeZone, "timeZone"); //$NON-NLS-1$
		ILogResource log = createLogResource();
		log.setName(logFile.getName());
		log.setDialect(dialect);
		try {
			log.configure(SimpleLogResource.OPTION_ENCODING, encoding);
			log.configure(SimpleLogResource.OPTION_LOCALE, LocaleUtils.getLocaleId(locale));
			log.configure(SimpleLogResource.OPTION_TIMEZONE, timeZone.getID());
			log.configure(OPTION_LOGFILE, logFile.getPath());
		} catch (CoreException e) {
			getLogger().error(e.getLocalizedMessage(), e);
		}
		return log;
	}
}
