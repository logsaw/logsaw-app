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
package net.sf.logsaw.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.logresource.IHasEncoding;
import net.sf.logsaw.core.logresource.IHasLocale;
import net.sf.logsaw.core.logresource.IHasTimeZone;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;
import net.sf.logsaw.core.util.LocaleUtils;
import net.sf.logsaw.index.IndexPlugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a convenience base class for test cases dealing with log resources and dialects.
 * 
 * @author Philipp Nanz
 */
public abstract class ADialectTest {

	private transient Logger logger;

	private File logFile;
	private File indexFile;
	private ILogResource logResource;

	/**
	 * Returns the dialect factory to use for testing.
	 * @return the dialect factory to use for testing
	 */
	protected abstract ILogDialectFactory doGetLogDialectFactory();

	/**
	 * Returns the log dialect to test with.
	 * @return the log dialect to test with
	 */
	protected final ILogDialect createLogDialect() {
		ILogDialectFactory factory = doGetLogDialectFactory();
		Assert.isNotNull(factory, "factory");
		return factory.createLogDialect();
	}

	/**
	 * Loads the given log file into a temporary file.
	 * Subsequent calls to <code>getLogFile</code> will return this instance.
	 * @param filename the file to load
	 * @throws IOException if an error occurred
	 */
	protected final void loadLogFile(String filename) throws IOException {
		cleanUp(true, false);
		InputStream is = null;
		OutputStream os = null;
		File f = File.createTempFile("log", null);
		try {
			is = getClass().getResourceAsStream(filename);
			Assert.isTrue(is != null, "File not found: " + filename);
			os = new FileOutputStream(f);
			IOUtils.copy(is, os);
			setLogFile(f);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
	}

	/**
	 * Returns the resource factory to use for testing.
	 * @return the resource factory to use for testing
	 */
	protected abstract ILogResourceFactory doGetLogResourceFactory();

	/**
	 * Creates the log resource to work with.
	 * Subsequent calls to <code>getLogResource</code> will return this instance.
	 * @param encoding the file encoding
	 * @param locale the locale to use
	 * @param timeZone the timezone to apply
	 * @throws IOException if an error occurred
	 */
	protected final void createLogResourceWithPK(String encoding, Locale locale, 
			TimeZone timeZone) throws IOException {
		cleanUp(false, true);
		File logFile = getLogFile();
		Assert.isNotNull(logFile, "logFile");
		ILogResourceFactory factory = doGetLogResourceFactory();
		Assert.isNotNull(factory, "factory");
		ILogResource log = factory.createLogResource();
		log.setName(getLogFile().getName());
		log.setDialect(createLogDialect());
		try {
			log.configure(IHasEncoding.OPTION_ENCODING, encoding);
			log.configure(IHasLocale.OPTION_LOCALE, LocaleUtils.getLocaleId(locale));
			log.configure(IHasTimeZone.OPTION_TIMEZONE, timeZone.getID());
			doConfigure(log);
			// Create PK
			IndexPlugin.getDefault().getIndexService().createIndex(log);
			// That's it
			setIndexFile(IndexPlugin.getDefault().getIndexFile(log));
			setLogResource(log);
		} catch (CoreException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Creates the log resource to work with.
	 * Subsequent calls to <code>getLogResource</code> will return this instance.
	 * @param encoding the file encoding
	 * @param locale the locale to use
	 * @param timeZone the timezone to apply
	 * @throws IOException if an error occurred
	 */
	protected final void createLogResource(String encoding, Locale locale, 
			TimeZone timeZone) throws IOException {
		cleanUp(false, true);
		File logFile = getLogFile();
		Assert.isNotNull(logFile, "logFile");
		ILogResourceFactory factory = doGetLogResourceFactory();
		Assert.isNotNull(factory, "factory");
		ILogResource log = factory.createLogResource();
		log.setName(getLogFile().getName());
		log.setDialect(createLogDialect());
		try {
			log.configure(IHasEncoding.OPTION_ENCODING, encoding);
			log.configure(IHasLocale.OPTION_LOCALE, LocaleUtils.getLocaleId(locale));
			log.configure(IHasTimeZone.OPTION_TIMEZONE, timeZone.getID());
			doConfigure(log);
			// That's it
			setLogResource(log);
		} catch (CoreException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Subclasses may override to perform necessary configuration on 
	 * newly created log resources.
	 * @param log the log
	 * @throws CoreException if an error occurred
	 */
	protected void doConfigure(ILogResource log) throws CoreException {
		// to override
	}

	/**
	 * @return the logFile
	 */
	protected final File getLogFile() {
		return logFile;
	}

	/**
	 * @param logFile the logFile to set
	 */
	protected final void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	/**
	 * @return the indexFile
	 */
	protected final File getIndexFile() {
		return indexFile;
	}

	/**
	 * @param indexFile the indexFile to set
	 */
	protected final void setIndexFile(File indexFile) {
		this.indexFile = indexFile;
	}

	/**
	 * @return the logResource
	 */
	protected final ILogResource getLogResource() {
		return logResource;
	}

	/**
	 * @param logResource the logResource to set
	 */
	protected final void setLogResource(ILogResource logResource) {
		this.logResource = logResource;
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

	/**
	 * Removes the specified temporary files.
	 * @param deleteLogFile
	 * @param deleteIndexFile
	 * @throws IOException
	 */
	protected final void cleanUp(boolean deleteLogFile, boolean deleteIndexFile) throws IOException {
		File logFile = getLogFile();
		File indexFile = getIndexFile();
		if (deleteLogFile && (logFile != null)) {
			assertTrue("Could not delete file", logFile.delete());
			logFile = null;
		}
		if (deleteIndexFile && (indexFile != null)) {
			FileUtils.deleteDirectory(indexFile);
			indexFile = null;
		}
	}
}
