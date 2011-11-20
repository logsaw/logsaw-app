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
package net.sf.logsaw.core.internal.logresource.simple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.IConfigOptionVisitor;
import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.internal.Messages;
import net.sf.logsaw.core.logresource.IHasEncoding;
import net.sf.logsaw.core.logresource.IHasLocale;
import net.sf.logsaw.core.logresource.IHasTimeZone;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.core.logresource.support.ALogResource;
import net.sf.logsaw.core.util.LocaleUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * This class represents a simple <code>ILogResource</code> implementation.
 * 
 * @author Philipp Nanz
 */
public final class SimpleLogResource extends ALogResource implements IHasEncoding, IHasLocale, IHasTimeZone {

	private String encoding;
	private Locale locale;
	private File logFile;
	private TimeZone timeZone;

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogResource#synchronize(net.sf.logsaw.core.framework.ILogEntryCollector, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void synchronize(ILogEntryCollector collector,
			IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(collector, "collector"); //$NON-NLS-1$
		Assert.isTrue(isConfigured(), "must be configured before synchronize"); //$NON-NLS-1$
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask(NLS.bind(Messages.SimpleLogResource_synchronizeTask_name, 
				getName()), getUnitsOfWork()); // Units of work will return atleast 1
		InputStream input = null;
		try {
			// The ProgressingInputStream will take care of updating the monitor
			input = new ProgressingInputStream(FileUtils.openInputStream(logFile), monitor);
			getDialect().parse(this, input, collector);
			monitor.worked(1);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 
					NLS.bind(Messages.SimpleLogResource_error_fileNotFound, logFile.getPath())));
		} finally {
			// Better safe than sorry
			IOUtils.closeQuietly(input);
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#getRequiredConfigOptions()
	 */
	@Override
	public List<IConfigOption<?>> getRequiredConfigOptions() {
		List<IConfigOption<?>> ret = new ArrayList<IConfigOption<?>>();
		ret.add(OPTION_ENCODING);
		ret.add(OPTION_LOCALE);
		ret.add(OPTION_TIMEZONE);
		ret.add(SimpleLogResourceFactory.OPTION_LOGFILE);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.support.AConfigurableObject#configure(net.sf.logsaw.core.config.IConfigOption, java.lang.Object)
	 */
	@Override
	public <T> void configure(IConfigOption<T> option, T value)
			throws CoreException {
		option.visit(new IConfigOptionVisitor() {
			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.config.IConfigOptionVisitor#visit(net.sf.logsaw.core.config.StringConfigOption, java.lang.String)
			 */
			@Override
			public void visit(StringConfigOption opt, String value)
					throws CoreException {
				if (OPTION_ENCODING.equals(opt)) {
					Assert.isNotNull(value, "encoding"); //$NON-NLS-1$
					encoding = value;
				} else if (OPTION_LOCALE.equals(opt)) {
					Assert.isNotNull(value, "localeId"); //$NON-NLS-1$
					locale = LocaleUtils.getLocaleById(value);
				} else if (OPTION_TIMEZONE.equals(opt)) {
					Assert.isNotNull(value, "timeZone"); //$NON-NLS-1$
					timeZone = TimeZone.getTimeZone(value);
				} else if (SimpleLogResourceFactory.OPTION_LOGFILE.equals(opt)) {
					Assert.isNotNull(value, "logFile"); //$NON-NLS-1$
					logFile = new File(value);
				}
			}
		}, value);
		// Add to caches and such
		super.configure(option, value);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.IHasEncoding#getEncoding()
	 */
	@Override
	public String getEncoding() {
		return encoding;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.IHasLocale#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.IHasTimeZone#getTimeZone()
	 */
	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * Returns the units of work for parsing the given log file.
	 * @return the units of work or <code>IProgressMonitor.UNKNOWN</code>
	 */
	protected int getUnitsOfWork() {
		// If file is less than 1kB, totalWork will be one
		return ((int) Math.floor((double) logFile.length() / FileUtils.ONE_KB)) + 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return logFile.getPath();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((logFile == null) ? 0 : logFile.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleLogResource other = (SimpleLogResource) obj;
		if (logFile == null) {
			if (other.logFile != null)
				return false;
		} else if (!logFile.equals(other.logFile))
			return false;
		return true;
	}
}
