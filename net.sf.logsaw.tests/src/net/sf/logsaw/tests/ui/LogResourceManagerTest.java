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
package net.sf.logsaw.tests.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.index.IndexPlugin;
import net.sf.logsaw.index.SynchronizationResult;
import net.sf.logsaw.tests.ADialectTest;
import net.sf.logsaw.ui.IGenericCallback;
import net.sf.logsaw.ui.ILogResourceManager;
import net.sf.logsaw.ui.UIPlugin;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Test;

/**
 * @author Philipp Nanz
 */
public class LogResourceManagerTest extends ADialectTest {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.tests.ADialectTest#doGetLogDialectFactory()
	 */
	@Override
	protected ILogDialectFactory doGetLogDialectFactory() {
		return Log4JDialectPlugin.getDefault().getXMLLayoutDialectFactory();
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
	public void testAddAndRemove() {
		final ILogResourceManager mgr = UIPlugin.getDefault().getLogResourceManager();
		try {
			final int size = mgr.getAll().length;
			loadLogFile("sample-1.log.xml");
			createLogResource("UTF-8", Locale.getDefault(), getTimeZone());
			assertNotNull(getLogResource());
			assertTrue(getLogResource().getPK() == null);
			mgr.add(getLogResource());
			assertTrue(getLogResource().getPK() != null);
			assertEquals(size + 1, mgr.getAll().length);
			
			mgr.remove(getLogResource());
			assertEquals(size, mgr.getAll().length);
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testSynchronize() {
		ILogResourceManager mgr = UIPlugin.getDefault().getLogResourceManager();
		try {
			int size = mgr.getAll().length;
			loadLogFile("sample-1.log.xml");
			createLogResource("UTF-8", Locale.getDefault(), getTimeZone());
			assertNotNull(getLogResource());
			assertTrue(getLogResource().getPK() == null);
			mgr.add(getLogResource());
			assertTrue(getLogResource().getPK() != null);
			assertEquals(size + 1, mgr.getAll().length);
			
			final SynchronizationResult[] result = new SynchronizationResult[1];
			final CountDownLatch countDown = new CountDownLatch(1);
			mgr.synchronize(getLogResource(), new IGenericCallback<SynchronizationResult>() {
				/* (non-Javadoc)
				 * @see net.sf.logsaw.ui.IGenericCallback#doCallback(java.lang.Object)
				 */
				@Override
				public void doCallback(SynchronizationResult payload) {
					result[0] = payload;
					countDown.countDown();
				}
			});
			countDown.await();
			assertTrue(IndexPlugin.getDefault().getIndexFile(getLogResource()).exists());
			assertEquals(5, result[0].getNumberOfEntriesAdded());
			assertEquals(5, IndexPlugin.getDefault().getIndexService().count(getLogResource()));
			
			mgr.remove(getLogResource());
			assertTrue(!IndexPlugin.getDefault().getIndexFile(getLogResource()).exists());
			assertEquals(size, mgr.getAll().length);
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@After
	public void tearDown() throws IOException {
		cleanUp(true, true);
	}
}
