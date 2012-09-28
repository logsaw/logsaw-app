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
package net.sf.logsaw.dialect.websphere;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.dialect.ILogFieldProvider;
import net.sf.logsaw.core.dialect.ILogLevelProvider;
import net.sf.logsaw.core.dialect.support.ALogDialect;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.logresource.IHasEncoding;
import net.sf.logsaw.core.logresource.IHasLocale;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.dialect.websphere.internal.Messages;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author Philipp Nanz
 */
public final class WebsphereDialect extends ALogDialect {

	private static final String TIME_FORMAT = "H:mm:ss:SSS z"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogDialect#getFieldProvider()
	 */
	@Override
	public ILogFieldProvider getFieldProvider() {
		return WebsphereDialectPlugin.getDefault().getFieldProvider();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogDialect#parse(net.sf.logsaw.core.framework.ILogResource, java.io.InputStream, net.sf.logsaw.core.framework.ILogEntryCollector)
	 */
	@Override
	public void parse(ILogResource log, InputStream input,
			ILogEntryCollector collector) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isNotNull(input, "input"); //$NON-NLS-1$
		Assert.isNotNull(collector, "collector"); //$NON-NLS-1$
		Assert.isTrue(isConfigured(), "Dialect should be configured by now"); //$NON-NLS-1$
		try {
			LogEntry currentEntry = null;
			IHasEncoding enc = (IHasEncoding) log.getAdapter(IHasEncoding.class);
			IHasLocale loc = (IHasLocale) log.getAdapter(IHasLocale.class);
			// WebSphere Dialect doesn't need to care about the timezone, because it is encoded in the log messages
			DateFormat df = getDateFormat(loc.getLocale());
			LineIterator iter = IOUtils.lineIterator(input, enc.getEncoding());
			int lineNo = 0;
			try {
				while (iter.hasNext()) {
					// Error handling
					lineNo++;
					List<IStatus> statuses = null;
					boolean fatal = false; // determines whether to interrupt parsing
					
					String line = iter.nextLine();
					Matcher m = getInternalPattern().matcher(line);
					if (m.find()) {
						// The next line matches, so flush the previous entry and continue
						if (currentEntry != null) {
							collector.collect(currentEntry);
							currentEntry = null;
						}
						currentEntry = new LogEntry();
						for (int i = 0; i < m.groupCount(); i++) {
							try {
								extractField(currentEntry, i + 1, m.group(i + 1), df);
							} catch (CoreException e) {
								// Mark for interruption
								fatal = fatal || e.getStatus().matches(IStatus.ERROR);
								
								// Messages will be displayed later
								if (statuses == null) {
									statuses = new ArrayList<IStatus>();
								}
								if (e.getStatus().isMultiStatus()) {
									Collections.addAll(statuses, e.getStatus().getChildren());
								} else {
									statuses.add(e.getStatus());
								}
							}
						}
						
						// We encountered errors or warnings
						if (statuses != null && !statuses.isEmpty()) {
							currentEntry = null; // Stop propagation
							IStatus status = new MultiStatus(WebsphereDialectPlugin.PLUGIN_ID, 
									0, statuses.toArray(new IStatus[statuses.size()]), 
									NLS.bind(Messages.WebsphereDialect_error_failedToParseLine, lineNo), null);
							if (fatal) {
								// Interrupt parsing in case of error
								throw new CoreException(status);
							} else {
								collector.addMessage(status);
							}
						}
					} else if (currentEntry != null) {
						// Append to message
						String msg = currentEntry.get(getFieldProvider().getMessageField());
						StringWriter strWriter = new StringWriter();
						PrintWriter printWriter = new PrintWriter(strWriter);
						printWriter.print(msg);
						printWriter.println();
						printWriter.print(line);
						currentEntry.put(getFieldProvider().getMessageField(), strWriter.toString());
					}
					
					if (collector.isCanceled()) {
						// Cancel parsing
						break;
					}
				}
				
				if (currentEntry != null) {
					// Collect left over entry
					collector.collect(currentEntry);
				}
			} finally {
				LineIterator.closeQuietly(iter);
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, WebsphereDialectPlugin.PLUGIN_ID, 
					NLS.bind(Messages.WebsphereDialect_error_failedToParseFile, 
							new Object[] {log.getName(), e.getLocalizedMessage()}), e));
		}
	}

	private Pattern getInternalPattern() throws CoreException {
		StringBuilder sb = new StringBuilder();
		sb.append(Pattern.quote("[")); //$NON-NLS-1$
		// Timestamp
		sb.append("(.*)"); //$NON-NLS-1$
		sb.append(Pattern.quote("] ")); //$NON-NLS-1$
		// Thread ID
		sb.append("([0-9a-f]{8})"); //$NON-NLS-1$
		sb.append(Pattern.quote(" ")); //$NON-NLS-1$
		// Short Name
		sb.append("(.{13})"); //$NON-NLS-1$
		sb.append(Pattern.quote(" ")); //$NON-NLS-1$
		// Event Type
		sb.append("([AIWEFORuZ])"); //$NON-NLS-1$
		sb.append(Pattern.quote(" ")); //$NON-NLS-1$
		// Class Name (optional)
		sb.append("([^\\s]*)"); //$NON-NLS-1$
		sb.append(Pattern.quote(" ")); //$NON-NLS-1$
		// Method Name (optional)
		sb.append("([^\\s]*)"); //$NON-NLS-1$
		sb.append(Pattern.quote(" ")); //$NON-NLS-1$
		// Message
		sb.append("(.*)"); //$NON-NLS-1$
		return Pattern.compile(sb.toString());
	}

	private void extractField(LogEntry entry, int group, String val, DateFormat dateFormat) throws CoreException {
		switch (group) {
			case 1:
				try {
					// Timestamp
					// This is a bit complicated because WebSphere will write it out in some localized format
					entry.put(WebsphereFieldProvider.FIELD_TIMESTAMP, 
							dateFormat.parse(val.trim()));
				} catch (ParseException e) {
					throw new CoreException(new Status(IStatus.ERROR, WebsphereDialectPlugin.PLUGIN_ID, 
							NLS.bind(Messages.WebsphereDialect_error_failedToParseTimestamp, val.trim())));
				}
				break;
			case 2:
				// Thread ID
				entry.put(WebsphereFieldProvider.FIELD_THREAD_ID, val.trim());
				break;
			case 3:
				// Short Name
				entry.put(WebsphereFieldProvider.FIELD_SHORT_NAME, val.trim());
				break;
			case 4:
				// Event Type
				Level lvl = WebsphereFieldProvider.FIELD_EVENT_TYPE.getLevelProvider().findLevel(val.trim());
				if (ILogLevelProvider.ID_LEVEL_UNKNOWN == lvl.getValue()) {
					throw new CoreException(new Status(IStatus.WARNING, WebsphereDialectPlugin.PLUGIN_ID, 
							NLS.bind(Messages.WebsphereDialect_warning_unknownEventType, val.trim())));
				}
				entry.put(WebsphereFieldProvider.FIELD_EVENT_TYPE, lvl);
				break;
			case 5:
				// Class Name (optional)
				entry.put(WebsphereFieldProvider.FIELD_CLASS_NAME, val.trim());
				break;
			case 6:
				// Method Name (optional)
				entry.put(WebsphereFieldProvider.FIELD_METHOD_NAME, val.trim());
				break;
			case 7:
				// Message
				entry.put(WebsphereFieldProvider.FIELD_MESSAGE, val.trim());
				break;
			default:
				throw new CoreException(new Status(IStatus.ERROR, WebsphereDialectPlugin.PLUGIN_ID, 
						NLS.bind(Messages.WebsphereDialect_error_regexGroupNotSupported, group)));
		}
	}

	private DateFormat getDateFormat(Locale loc) throws CoreException {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, loc);
		if (!(df instanceof SimpleDateFormat)) {
			return null;
		}
		try {
			// Always use US locale for date format symbols
			return new SimpleDateFormat(((SimpleDateFormat) df).toPattern() + 
					" " + TIME_FORMAT, DateFormatSymbols.getInstance(Locale.US)); //$NON-NLS-1$
		} catch (RuntimeException e) {
			// Could also be ClassCastException
			throw new CoreException(new Status(IStatus.ERROR, WebsphereDialectPlugin.PLUGIN_ID, 
					NLS.bind(Messages.WebsphereDialect_error_dateFormatNotSupported, loc.toString())));
		}
	}
}
