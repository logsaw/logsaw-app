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
package net.sf.logsaw.tests.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.ILogResourceFactory;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.core.query.IRestrictionFactory;
import net.sf.logsaw.core.query.Operators;
import net.sf.logsaw.core.query.support.ARestriction;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.log4j.Log4JFieldProvider;
import net.sf.logsaw.index.IIndexService;
import net.sf.logsaw.index.IQueryContext;
import net.sf.logsaw.index.IndexPlugin;
import net.sf.logsaw.index.ResultPage;
import net.sf.logsaw.tests.ADialectTest;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Test;

/**
 * @author Philipp Nanz
 */
public class IndexServiceTest extends ADialectTest {

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

	private IRestrictionFactory getRestrictionFactory() {
		return CorePlugin.getDefault().getRestrictionFactory();
	}

	@Test
	public void testCreateAndDelete() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResource("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.createIndex(getLogResource());
			indexService.synchronize(getLogResource(), null);
			assertTrue(IndexPlugin.getDefault().getIndexFile(getLogResource()).exists());
			assertEquals(5, indexService.count(getLogResource()));
			indexService.deleteIndex(getLogResource());
			assertTrue(!IndexPlugin.getDefault().getIndexFile(getLogResource()).exists());
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testEncoding() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-cp1252.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			try {
				indexService.synchronize(getLogResource(), null);
				fail("Indexing should fail because of encoding");
			} catch (Exception e) {
				// nadda
			}
			createLogResourceWithPK("Cp1252", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			assertEquals(6, indexService.count(getLogResource()));
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testTimeZone() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("server.10-11-09.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getTimeZone("Europe/Berlin"));
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				ResultPage p = indexService.query(ctx, new LinkedList<ARestriction<?>>(), 0, 1000);
				assertEquals("2009-11-10T23:08:46.453", Log4JFieldProvider.FIELD_TIMESTAMP.toInputValue(
						p.getItems().get(0).get(Log4JFieldProvider.FIELD_TIMESTAMP), getLogResource()));
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_TIMESTAMP, 
						Operators.OPERATOR_AFTER, Log4JFieldProvider.FIELD_TIMESTAMP.fromInputValue("2009-11-10T23:08:47.609", getLogResource())));
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_TIMESTAMP, 
						Operators.OPERATOR_BEFORE, Log4JFieldProvider.FIELD_TIMESTAMP.fromInputValue("2009-11-10T23:08:50.609", getLogResource())));
				p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(2, p.getItems().size());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testTruncate() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			assertEquals(5, indexService.count(getLogResource()));
			indexService.truncate(getLogResource());
			assertEquals(0, indexService.count(getLogResource()));
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testOverlappingNoContext() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			assertEquals(5, indexService.count(getLogResource()));
			loadLogFile("sample-2.log.xml");
			getLogResource().configure(SimpleLogResourceFactory.OPTION_LOGFILE, getLogFile().getPath());
			indexService.synchronize(getLogResource(), null);
			assertEquals(6, indexService.count(getLogResource()));
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testOverlappingWithContext() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				ResultPage p = indexService.query(ctx, new LinkedList<ARestriction<?>>(), 0, 1000);
				assertEquals(5, p.getItems().size());
				loadLogFile("sample-2.log.xml");
				getLogResource().configure(SimpleLogResourceFactory.OPTION_LOGFILE, getLogFile().getPath());
				indexService.synchronize(getLogResource(), null);
				// additional entry is indexed, but the cached reader cannot see it
				p = indexService.query(ctx, new LinkedList<ARestriction<?>>(), 0, 1000);
				assertEquals(5, p.getItems().size());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testStopwords() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-stopwords.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_CONTAINS, "owner document not found"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(2, p.getItems().size());
				assertEquals(2, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testAllQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				ResultPage p = indexService.query(ctx, new LinkedList<ARestriction<?>>(), 0, 1000);
				assertEquals(5, p.getItems().size());
				assertEquals(5, p.getTotalHits());
				assertEquals("Context.PROVIDER_URL in server jndi.properties, url=localhost:1099", p.getItems().get(0).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("JBossTS Transaction Service (JTA version) - JBoss Inc.", p.getItems().get(1).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("Setting up property manager MBean and JMX layer", p.getItems().get(2).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("Starting recovery manager", p.getItems().get(3).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("Recovery manager started", p.getItems().get(4).get(Log4JFieldProvider.FIELD_MESSAGE));
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testPagination() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				ResultPage p = indexService.query(ctx, new LinkedList<ARestriction<?>>(), 2, 2);
				assertEquals(2, p.getItems().size());
				assertEquals("Setting up property manager MBean and JMX layer", p.getItems().get(0).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("Starting recovery manager", p.getItems().get(1).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals(5, p.getTotalHits());	
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testPaginationLastPage() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				ResultPage p = indexService.query(ctx, new LinkedList<ARestriction<?>>(), 2, 1000);
				assertEquals(3, p.getItems().size());
				assertEquals("Setting up property manager MBean and JMX layer", p.getItems().get(0).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("Starting recovery manager", p.getItems().get(1).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals("Recovery manager started", p.getItems().get(2).get(Log4JFieldProvider.FIELD_MESSAGE));
				assertEquals(5, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testContainsQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_CONTAINS, "recovery"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(2, p.getItems().size());
				assertEquals(2, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testNotContainsQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_NOT_CONTAINS, "recovery"));
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_NOT_CONTAINS, "starting"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(3, p.getItems().size());
				assertEquals(3, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testEqualsQueryWithString() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LOGGER, Operators.OPERATOR_EQUALS, 
						"org.jnp.server.NamingBeanImpl"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testNotEqualsQueryWithString() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LOGGER, Operators.OPERATOR_NOT_EQUALS, 
						"org.jnp.server.NamingBeanImpl"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(4, p.getItems().size());
				assertEquals(4, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testBeginsWithQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LOGGER, Operators.OPERATOR_BEGINS_WITH, 
						"com.arjuna.ats.jbossatx.jta"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(4, p.getItems().size());
				assertEquals(4, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testNotBeginsWithQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LOGGER, Operators.OPERATOR_NOT_BEGINS_WITH, 
						"com.arjuna.ats.jbossatx.jta"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testBeforeQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_TIMESTAMP, 
						Operators.OPERATOR_BEFORE, new Date(1248183795937L)));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testAfterQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_TIMESTAMP, 
						Operators.OPERATOR_AFTER, new Date(1248183796156L)));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testLessThanQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LEVEL, Operators.OPERATOR_LESS_THAN, 
						Log4JFieldProvider.FIELD_LEVEL.getLevelProvider().findLevel("warn")));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(4, p.getItems().size());
				assertEquals(4, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testGreaterThanQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LEVEL, Operators.OPERATOR_GREATER_THAN, 
						Log4JFieldProvider.FIELD_LEVEL.getLevelProvider().findLevel("info")));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testEqualsQueryWithLevel() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LEVEL, Operators.OPERATOR_EQUALS, 
						Log4JFieldProvider.FIELD_LEVEL.getLevelProvider().findLevel("warn")));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testNotEqualsQueryWithLevel() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_LEVEL, Operators.OPERATOR_NOT_EQUALS, 
						Log4JFieldProvider.FIELD_LEVEL.getLevelProvider().findLevel("info")));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(1, p.getItems().size());
				assertEquals(1, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testComplexQuery() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_TIMESTAMP, 
						Operators.OPERATOR_AFTER, new Date(1248183795312L)));
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_TIMESTAMP, 
						Operators.OPERATOR_BEFORE, new Date(1248183796234L)));
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_CONTAINS, "manager"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(2, p.getItems().size());
				assertEquals(2, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testCaseInsensitive() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_CONTAINS, "Recovery"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(2, p.getItems().size());
				assertEquals(2, p.getTotalHits());
			} finally {
				ctx.close();
			}
		} catch (Exception e) {
			getLogger().error(e.getLocalizedMessage(), e);
			fail("Exception should not occur: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testPhrase() {
		try {
			IIndexService indexService = IndexPlugin.getDefault().getIndexService();
			loadLogFile("sample-1.log.xml");
			createLogResourceWithPK("UTF-8", Locale.getDefault(), TimeZone.getDefault());
			indexService.synchronize(getLogResource(), null);
			IQueryContext ctx = indexService.createQueryContext(getLogResource());
			try {
				List<ARestriction<?>> ops = new LinkedList<ARestriction<?>>();
				ops.add(getRestrictionFactory().newRestriction(Log4JFieldProvider.FIELD_MESSAGE, 
						Operators.OPERATOR_CONTAINS, "recovery manager"));
				ResultPage p = indexService.query(ctx, ops, 0, 1000);
				assertEquals(2, p.getItems().size());
				assertEquals(2, p.getTotalHits());
			} finally {
				ctx.close();
			}
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
