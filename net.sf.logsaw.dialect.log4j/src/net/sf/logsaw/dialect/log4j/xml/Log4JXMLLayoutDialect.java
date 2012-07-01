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
package net.sf.logsaw.dialect.log4j.xml;

import java.io.IOException;
import java.io.InputStream;

import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.dialect.ILogFieldProvider;
import net.sf.logsaw.core.dialect.support.ALogDialect;
import net.sf.logsaw.core.logresource.IHasEncoding;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.log4j.internal.Messages;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Philipp Nanz
 */
public final class Log4JXMLLayoutDialect extends ALogDialect {

	private static final String FILENAME_EVENTSET_XML = "eventset.xml"; //$NON-NLS-1$
	private static final String FILENAME_LOG4J_DTD = "log4j.dtd"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogDialect#getFieldProvider()
	 */
	@Override
	public ILogFieldProvider getFieldProvider() {
		return Log4JDialectPlugin.getDefault().getFieldProvider();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogDialect#parse(net.sf.logsaw.core.framework.ILogResource, java.io.InputStream, net.sf.logsaw.core.framework.ILogEntryCollector)
	 */
	@Override
	public void parse(final ILogResource log, final InputStream input,
			ILogEntryCollector collector) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isNotNull(input, "input"); //$NON-NLS-1$
		Assert.isNotNull(collector, "collector"); //$NON-NLS-1$
		try {
			InputStream eventSetIS = getClass().getResourceAsStream(FILENAME_EVENTSET_XML);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			XMLFilter filter = new Log4JXMLFilter(collector);
			filter.setParent(reader);
			filter.setEntityResolver(new EntityResolver() {
				
				@Override
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					if ((systemId != null) && systemId.toLowerCase().endsWith(FILENAME_LOG4J_DTD)) {
						// Looking for the Log4J DTD?
						InputStream is = getClass().getResourceAsStream(FILENAME_LOG4J_DTD);
						return new InputSource(is);
					}
					if ((systemId != null) && systemId.equals("%file%")) { //$NON-NLS-1$
						// Loading the log file
						InputSource src = new InputSource(input);
						// Apply the passed encoding
						IHasEncoding enc = (IHasEncoding) log.getAdapter(IHasEncoding.class);
						src.setEncoding(enc.getEncoding());
						return src;
					}
					return null;
				}
			});
			filter.parse(new InputSource(eventSetIS));
		} catch (OperationCanceledException e) {
			// Thrown by Log4JXMLFilter to cancel parsing
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, Log4JDialectPlugin.PLUGIN_ID, 
					NLS.bind(Messages.Log4JDialect_error_parsingFailed, 
							new Object[] {log.getName(), e.getLocalizedMessage()}), e));
		}
	}
}
