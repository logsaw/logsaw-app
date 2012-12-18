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
package net.sf.logsaw.tests.dialect.websphere;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.dialect.support.ALogEntryCollector;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.dialect.websphere.WebsphereDialectPlugin;
import net.sf.logsaw.dialect.websphere.WebsphereFieldProvider;
import net.sf.logsaw.tests.ADialectTest;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Test;

/**
 * @author Philipp Nanz
 */
public class WebsphereDialectTest extends ADialectTest {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.tests.ADialectTest#doGetLogDialectFactory()
	 */
	@Override
	protected ILogDialectFactory doGetLogDialectFactory() {
		return WebsphereDialectPlugin.getDefault().getDialectFactory();
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
			loadLogFile("websphere_de.log.txt");
			createLogResourceWithPK("UTF-8", Locale.US, getTimeZone());
			
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
				fail("Should not parse because of locale");
			} catch (Exception e) {
				// Expected
			}
			
			// Retry
			createLogResourceWithPK("UTF-8", Locale.GERMANY, getTimeZone());
			getLogResource().synchronize(coll, null);
			
			assertEquals(6, list.size());
			assertEquals(6, coll.getTotalCollected());
			
			assertEquals(1267004712871L, list.get(0).get(WebsphereFieldProvider.FIELD_TIMESTAMP).getTime());
			assertEquals("0000003a", list.get(0).get(WebsphereFieldProvider.FIELD_THREAD_ID));
			assertEquals("DB2CmsService", list.get(0).get(WebsphereFieldProvider.FIELD_SHORT_NAME));
			assertEquals("INFO", list.get(0).get(WebsphereFieldProvider.FIELD_EVENT_TYPE).getName());
			assertEquals("de.docufy.cms.util.log.DocufyLogger", list.get(0).get(WebsphereFieldProvider.FIELD_CLASS_NAME));
			assertEquals("info", list.get(0).get(WebsphereFieldProvider.FIELD_METHOD_NAME));
			assertEquals(">param: 14, 'IOLanguageVariantPK:io=system-folder-doc-in-edition-101:version=1:language=--:variant=--'", list.get(0).get(WebsphereFieldProvider.FIELD_MESSAGE));
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testGetAllFields() {
		ILogDialect dialect = createLogDialect();
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
		ILogDialect dialect = createLogDialect();
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
		ILogDialect dialect = createLogDialect();
		assertNotNull(dialect.getFieldProvider().getTimestampField());
	}

	@After
	public void tearDown() throws IOException {
		cleanUp(true, true);
	}
}
