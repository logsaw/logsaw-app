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
package net.sf.logsaw.dialect.log4j.xml;

import java.io.IOException;
import java.util.Date;

import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.dialect.log4j.Log4JFieldProvider;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.OperationCanceledException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * @author Philipp Nanz
 */
public final class Log4JXMLFilter extends XMLFilterImpl {

	/** The name of the logger attribute */
	private static final String ATTRIBUTE_LOGGER = "logger"; //$NON-NLS-1$
	/** The name of the logger attribute */
	private static final String ATTRIBUTE_TIMESTAMP = "timestamp"; //$NON-NLS-1$
	/** The name of the logger attribute */
	private static final String ATTRIBUTE_LEVEL = "level"; //$NON-NLS-1$
	/** The name of the logger attribute */
	private static final String ATTRIBUTE_THREAD = "thread"; //$NON-NLS-1$
	/** The name of the log4j:event element */
	private static final String ELEMENT_EVENT = "event"; //$NON-NLS-1$
	/** The name of the log4j:message element */
	private static final String ELEMENT_MESSAGE = "message"; //$NON-NLS-1$
	/** The name of the log4j:NDC element */
	private static final String ELEMENT_NDC = "NDC"; //$NON-NLS-1$
	/** The name of the log4j:throwable element */
	private static final String ELEMENT_THROWABLE = "throwable"; //$NON-NLS-1$
	/** The name of the log4j:locationInfo element */
	private static final String ELEMENT_LOCATION_INFO = "locationInfo"; //$NON-NLS-1$
	/** The name of the class attribute */
	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	/** The name of the method attribute */
	private static final String ATTRIBUTE_METHOD = "method"; //$NON-NLS-1$
	/** The name of the file attribute */
	private static final String ATTRIBUTE_FILE = "file"; //$NON-NLS-1$
	/** The name of the line attribute */
	private static final String ATTRIBUTE_LINE = "line"; //$NON-NLS-1$
	/** The namespace of the Log4J xml format */
	private static final String NAMESPACE_LOG4J = "http://jakarta.apache.org/log4j/"; //$NON-NLS-1$

	private Locator documentLocator;
	private ILogEntryCollector collector;
	private LogEntry currentEntry;
	private StringBuilder buffer;

	/**
	 * Constructor.
	 * @param collector the log entry collector
	 */
	public Log4JXMLFilter(ILogEntryCollector collector) {
		Assert.isNotNull(collector, "collector"); //$NON-NLS-1$
		this.collector = collector;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.XMLFilterImpl#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator locator) {
		documentLocator = locator;
		super.setDocumentLocator(locator);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (uri.equals(NAMESPACE_LOG4J) && localName.equals(ELEMENT_EVENT)) {
			currentEntry = new LogEntry();
			currentEntry.put(Log4JFieldProvider.FIELD_LOGGER, atts.getValue(ATTRIBUTE_LOGGER));
			currentEntry.put(Log4JFieldProvider.FIELD_TIMESTAMP, 
					new Date(Long.parseLong(atts.getValue(ATTRIBUTE_TIMESTAMP))));
			currentEntry.put(Log4JFieldProvider.FIELD_LEVEL, 
					Log4JFieldProvider.FIELD_LEVEL.getLevelProvider().findLevel(atts.getValue(ATTRIBUTE_LEVEL)));
			currentEntry.put(Log4JFieldProvider.FIELD_THREAD, atts.getValue(ATTRIBUTE_THREAD));
		} else if (uri.equals(NAMESPACE_LOG4J) && (localName.equals(ELEMENT_MESSAGE) || 
				localName.equals(ELEMENT_NDC) || localName.equals(ELEMENT_THROWABLE))) {
			buffer = new StringBuilder();
		} else if (uri.equals(NAMESPACE_LOG4J) && localName.equals(ELEMENT_LOCATION_INFO)) {
			currentEntry.put(Log4JFieldProvider.FIELD_LOC_FILENAME, atts.getValue(ATTRIBUTE_FILE));
			currentEntry.put(Log4JFieldProvider.FIELD_LOC_CLASS, atts.getValue(ATTRIBUTE_CLASS));
			currentEntry.put(Log4JFieldProvider.FIELD_LOC_METHOD, atts.getValue(ATTRIBUTE_METHOD));
			currentEntry.put(Log4JFieldProvider.FIELD_LOC_LINE, atts.getValue(ATTRIBUTE_LINE));
		}
		super.startElement(uri, localName, qName, atts);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.XMLFilterImpl#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (buffer != null) {
			buffer.append(ch, start, length);
		}
		super.characters(ch, start, length);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (uri.equals(NAMESPACE_LOG4J) && localName.equals(ELEMENT_EVENT)) {
			try {
				collector.collect(currentEntry);
			} catch (IOException e) {
				throw new SAXParseException(e.getLocalizedMessage(), documentLocator, e);
			}
			// Check if canceled
			checkMonitorCanceled();
		} else if (uri.equals(NAMESPACE_LOG4J) && localName.equals(ELEMENT_MESSAGE)) {
			currentEntry.put(Log4JFieldProvider.FIELD_MESSAGE, buffer.toString());
			buffer = null;
		} else if (uri.equals(NAMESPACE_LOG4J) && localName.equals(ELEMENT_NDC)) {
			currentEntry.put(Log4JFieldProvider.FIELD_NDC, buffer.toString());
			buffer = null;
		} else if (uri.equals(NAMESPACE_LOG4J) && localName.equals(ELEMENT_THROWABLE)) {
			currentEntry.put(Log4JFieldProvider.FIELD_THROWABLE, buffer.toString());
			buffer = null;
		}
		super.endElement(uri, localName, qName);
	}

	private void checkMonitorCanceled() {
		if (collector.isCanceled()) {
			throw new OperationCanceledException();
		}
	}
}
