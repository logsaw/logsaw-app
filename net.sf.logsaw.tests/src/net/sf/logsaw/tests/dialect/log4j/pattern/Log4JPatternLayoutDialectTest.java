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
package net.sf.logsaw.tests.dialect.log4j.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.dialect.support.ALogEntryCollector;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.log4j.Log4JFieldProvider;
import net.sf.logsaw.dialect.pattern.APatternDialect;
import net.sf.logsaw.tests.ADialectTest;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Test;

/**
 * @author Philipp Nanz
 */
public class Log4JPatternLayoutDialectTest extends ADialectTest {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.tests.ADialectTest#doGetLogDialectFactory()
	 */
	@Override
	protected ILogDialectFactory doGetLogDialectFactory() {
		return Log4JDialectPlugin.getDefault().getPatternLayoutDialectFactory();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.tests.ADialectTest#doGetLogResourceFactory()
	 */
	@Override
	protected ILogResourceFactory doGetLogResourceFactory() {
		return SimpleLogResourceFactory.getInstance();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.tests.ADialectTest#doConfigure(net.sf.logsaw.core.logresource.ILogResource)
	 */
	@Override
	protected void doConfigure(ILogResource log) throws CoreException {
		log.configure(SimpleLogResourceFactory.OPTION_LOGFILE, getLogFile().getPath());
	}

	@Test
	public void testBasicParsing() {
		final List<LogEntry> list = new LinkedList<LogEntry>();
		try {
			loadLogFile("server.10-11-09.log.txt");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			APatternDialect dialect = (APatternDialect) getLogResource().getDialect();
			ILogEntryCollector coll = new ALogEntryCollector(null) {

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.framework.support.ALogEntryCollector#doCollect(net.sf.logsaw.core.model.LogEntry)
				 */
				@Override
				protected boolean doCollect(LogEntry entry) throws IOException {
					list.add(entry);
					return true;
				}
			};
			try {
				getLogResource().synchronize(coll, null);
				fail("Should throw exception because it is not configured");
			} catch (AssertionFailedException e) {
				// should be configured
			}
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d %-5p [%c] (%t) %m%n");
			getLogResource().synchronize(coll, null);
			assertEquals(4, list.size());
			assertEquals(4, coll.getTotalCollected());
			
			assertEquals("INFO", list.get(1).get(Log4JFieldProvider.FIELD_LEVEL).getName());
			assertEquals("org.apache.coyote.http11.Http11Protocol", list.get(1).get(Log4JFieldProvider.FIELD_LOGGER));
			assertEquals("Starting Coyote HTTP/1.1 on http-0.0.0.0-8080", list.get(1).get(Log4JFieldProvider.FIELD_MESSAGE));
			assertEquals("main", list.get(1).get(Log4JFieldProvider.FIELD_THREAD));
			assertEquals(1257890927609L, list.get(1).get(Log4JFieldProvider.FIELD_TIMESTAMP).getTime());
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testLocInfoParsing() {
		final List<LogEntry> list = new LinkedList<LogEntry>();
		try {
			loadLogFile("server-locinfo.log.txt");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			APatternDialect dialect = (APatternDialect) getLogResource().getDialect();
			ILogEntryCollector coll = new ALogEntryCollector(null) {

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.framework.support.ALogEntryCollector#doCollect(net.sf.logsaw.core.model.LogEntry)
				 */
				@Override
				protected boolean doCollect(LogEntry entry) throws IOException {
					list.add(entry);
					return true;
				}
			};
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d{ABSOLUTE} %-5p - %m (%F:%M:%L)%n");
			getLogResource().synchronize(coll, null);
			assertEquals(5, list.size());
			assertEquals(5, coll.getTotalCollected());
			
			assertEquals("Creating dependent components for: jboss.system:type=Log4jService,service=Logging dependents are: []", list.get(4).get(Log4JFieldProvider.FIELD_MESSAGE));
			assertEquals("create", list.get(4).get(Log4JFieldProvider.FIELD_LOC_METHOD));
			assertEquals("ServiceController.java", list.get(4).get(Log4JFieldProvider.FIELD_LOC_FILENAME));
			assertEquals("342", list.get(4).get(Log4JFieldProvider.FIELD_LOC_LINE));
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testLineParseFailure() {
		final List<LogEntry> list = new LinkedList<LogEntry>();
		try {
			loadLogFile("server-invalidline.log.txt");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			APatternDialect dialect = (APatternDialect) getLogResource().getDialect();
			ILogEntryCollector coll = new ALogEntryCollector(null) {

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.framework.support.ALogEntryCollector#doCollect(net.sf.logsaw.core.model.LogEntry)
				 */
				@Override
				protected boolean doCollect(LogEntry entry) throws IOException {
					list.add(entry);
					return true;
				}
			};
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d{ABSOLUTE} %-5p - %m (%t)%n");
			getLogResource().synchronize(coll, null);
			assertEquals(4, list.size());
			assertEquals(4, coll.getTotalCollected());
			assertEquals(1, coll.getMessages().size());
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testLazyParsing() {
		final List<LogEntry> list = new LinkedList<LogEntry>();
		try {
			loadLogFile("lazy.log.txt");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			APatternDialect dialect = (APatternDialect) getLogResource().getDialect();
			ILogEntryCollector coll = new ALogEntryCollector(null) {

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.framework.support.ALogEntryCollector#doCollect(net.sf.logsaw.core.model.LogEntry)
				 */
				@Override
				protected boolean doCollect(LogEntry entry) throws IOException {
					list.add(entry);
					return true;
				}
			};
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d %-5p [%c] %m%n");
			getLogResource().synchronize(coll, null);
			assertEquals(3, list.size());
			assertEquals(3, coll.getTotalCollected());
			
			assertEquals("INFO", list.get(0).get(Log4JFieldProvider.FIELD_LEVEL).getName());
			assertEquals("de.docufy.cms.service.transformation.layout.PubInstanceHandler", list.get(0).get(Log4JFieldProvider.FIELD_LOGGER));
			assertEquals("Element: graphic cmsid: fcda5d8c9489a42e0a00206a001baae7:1:--:--:PNG96", list.get(0).get(Log4JFieldProvider.FIELD_MESSAGE));
			assertEquals("ERROR", list.get(1).get(Log4JFieldProvider.FIELD_LEVEL).getName());
			assertEquals("STDERR", list.get(1).get(Log4JFieldProvider.FIELD_LOGGER));
			assertEquals("[foLib-warning]: Y.ID [ID_bda1c2a87a5283810a00206a01f79aa9-990b01197a5283810a00206a00cb0ddb-en-US] exists already 2 times", list.get(1).get(Log4JFieldProvider.FIELD_MESSAGE));
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testGetAllFields() {
		APatternDialect dialect = (APatternDialect) createLogDialect();
		try {
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d %-5p [%c] (%t) %m%n");
		} catch (CoreException e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
		int oldSize = dialect.getFieldProvider().getAllFields().size();
		assertNotNull(dialect.getFieldProvider().getAllFields());
		assertTrue(!dialect.getFieldProvider().getAllFields().isEmpty());
		Iterator<ALogEntryField<?, ?>> it = dialect.getFieldProvider().getAllFields().iterator();
		it.next();
		it.remove();
		assertEquals("Changes to the set should not change the state of the dialect", 
				oldSize, dialect.getFieldProvider().getAllFields().size());
	}

	@Test
	public void testGetDefaultFields() {
		APatternDialect dialect = (APatternDialect) createLogDialect();
		try {
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d %-5p [%c] (%t) %m%n");
		} catch (CoreException e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
		int oldSize = dialect.getFieldProvider().getDefaultFields().size();
		assertNotNull(dialect.getFieldProvider().getDefaultFields());
		assertTrue(!dialect.getFieldProvider().getDefaultFields().isEmpty());
		Iterator<ALogEntryField<?, ?>> it = dialect.getFieldProvider().getDefaultFields().iterator();
		it.next();
		it.remove();
		assertEquals("Changes to the list should not change the state of the dialect", 
				oldSize, dialect.getFieldProvider().getDefaultFields().size());
	}

	@Test
	public void testTimestampField() {
		APatternDialect dialect = (APatternDialect) createLogDialect();
		try {
			dialect.configure(APatternDialect.OPTION_PATTERN, "%d %-5p [%c] (%t) %m%n");
		} catch (CoreException e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
		assertNotNull(dialect.getFieldProvider().getTimestampField());
	}

	@After
	public void tearDown() throws IOException {
		cleanUp(true, true);
	}
}
