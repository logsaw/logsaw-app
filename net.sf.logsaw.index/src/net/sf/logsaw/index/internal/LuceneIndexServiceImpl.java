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
package net.sf.logsaw.index.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.dialect.ILogLevelProvider;
import net.sf.logsaw.core.dialect.support.ALogEntryCollector;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.field.LogEntryFieldVisitorAdapter;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictionVisitor;
import net.sf.logsaw.core.query.Operators;
import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;
import net.sf.logsaw.core.query.support.ARestriction;
import net.sf.logsaw.index.IIndexService;
import net.sf.logsaw.index.IQueryContext;
import net.sf.logsaw.index.IndexPlugin;
import net.sf.logsaw.index.ResultPage;
import net.sf.logsaw.index.SynchronizationResult;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Lucene-based implementation of <code>IIndexService</code>.
 * 
 * @author Philipp Nanz
 */
public class LuceneIndexServiceImpl implements IIndexService {

	private static transient Logger logger = LoggerFactory.getLogger(LuceneIndexServiceImpl.class);

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#synchronize(net.sf.logsaw.core.ILogResource, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public SynchronizationResult synchronize(ILogResource log, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Date latestEntryDate = getLatestEntryDate(log); // the barrier timestamp
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		return updateIndex(log, latestEntryDate, monitor);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#unlock(net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public boolean unlock(ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		try {
			Directory dir = FSDirectory.open(IndexPlugin.getDefault().getIndexFile(log));
			if (IndexWriter.isLocked(dir)) {
				IndexWriter.unlock(dir);
				return true;
			}
			return false;
		} catch (IOException e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LuceneIndexService_error_failedToUnlockIndex, 
							new Object[] {log.getName(), e.getLocalizedMessage()}), e));
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#count(net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public int count(ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		ARunWithIndexReader<Integer> runnable = new ARunWithIndexReader<Integer>() {
			
			/* (non-Javadoc)
			 * @see net.sf.logsaw.index.impl.ARunWithIndexReader#doRunWithIndexReader(org.apache.lucene.index.IndexReader, net.sf.logsaw.core.framework.ILogResource)
			 */
			@Override
			protected Integer doRunWithIndexReader(IndexReader reader, ILogResource log) throws CoreException {
				if (reader != null) {
					return Integer.valueOf(reader.numDocs());
				}
				// Index does not exist yet
				return Integer.valueOf(0);
			}
		};
		return runnable.runWithIndexReader(log);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#size(net.sf.logsaw.core.framework.ILogResource)
	 */
	@Override
	public String size(ILogResource log) {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		long size = FileUtils.sizeOfDirectory(IndexPlugin.getDefault().getIndexFile(log));
		return FileUtils.byteCountToDisplaySize(size);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#truncate(net.sf.logsaw.core.framework.ILogResource)
	 */
	@Override
	public void truncate(ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		ARunWithIndexWriter<Boolean> runnable = new ARunWithIndexWriter<Boolean>() {
			
			/* (non-Javadoc)
			 * @see net.sf.logsaw.index.impl.ARunWithIndexWriter#doRunWithIndexWriter(org.apache.lucene.index.IndexWriter, net.sf.logsaw.core.framework.ILogResource)
			 */
			@Override
			protected Boolean doRunWithIndexWriter(IndexWriter writer, ILogResource log) throws CoreException {
				try {
					writer.deleteAll();
					writer.commit();
					return Boolean.TRUE;
				} catch (Exception e) {
					// Unexpected exception; wrap with CoreException
					throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, 
							NLS.bind(Messages.LuceneIndexService_error_failedToTruncateIndex, 
									new Object[] {log.getName(), e.getLocalizedMessage()}), e));
				}
			}
		};
		runnable.runWithIndexWriter(log, getAnalyzer(), getMatchVersion());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#createIndex(net.sf.logsaw.core.logresource.ILogResource)
	 */
	@Override
	public void createIndex(ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isTrue(log.getPK() == null, "PK must be null"); //$NON-NLS-1$
		log.setPK(UUID.randomUUID().toString());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#deleteIndex(net.sf.logsaw.core.logresource.ILogResource)
	 */
	@Override
	public void deleteIndex(ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		try {
			FileUtils.deleteDirectory(IndexPlugin.getDefault().getIndexFile(log));
		} catch (IOException e) {
			// Throw warning
			throw new CoreException(new Status(IStatus.WARNING, IndexPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LuceneIndexService_error_failedToDeleteIndex, log.getName())));
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#createQueryContext(net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public IQueryContext createQueryContext(ILogResource log) {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		return new LuceneQueryContextImpl(log);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.index.IIndexService#query(net.sf.logsaw.index.IQueryContext, java.util.List, int, int)
	 */
	@Override
	public ResultPage query(IQueryContext context, final List<ARestriction<?>> restrictions, 
			final int offset, final int limit) throws CoreException {
		Assert.isNotNull(context, "context"); //$NON-NLS-1$
		Assert.isTrue(context instanceof LuceneQueryContextImpl, 
				"Query context must be of type net.sf.logsaw.index.impl.LuceneQueryContextImpl"); //$NON-NLS-1$
		Assert.isTrue(context.isOpen(), "Query context must be open"); //$NON-NLS-1$
		Assert.isNotNull(restrictions, "restrictions"); //$NON-NLS-1$
		
		ARunWithIndexReader<ResultPage> runnable = new ARunWithIndexReader<ResultPage>() {
			
			/* (non-Javadoc)
			 * @see net.sf.logsaw.index.impl.ARunWithIndexReader#doRunWithIndexReader(org.apache.lucene.index.IndexReader, net.sf.logsaw.core.framework.ILogResource)
			 */
			@Override
			protected ResultPage doRunWithIndexReader(IndexReader reader, ILogResource log) throws CoreException {
				if (reader == null) {
					// Index does not exist yet
					return new ResultPage();
				}
				
				try {
					IndexSearcher searcher = new IndexSearcher(reader);
					try {
						Sort sort = new Sort(new SortField[] {
								new SortField(log.getDialect().getFieldProvider().getTimestampField().getKey(), SortField.LONG), 
								SortField.FIELD_DOC});
						TopFieldCollector collector = TopFieldCollector.create(
								sort, offset + limit, false, false, false, true);
						// TODO Investigate use of searchAfter
						searcher.search(convertToQuery(restrictions), collector);
						List<LogEntry> result = new LinkedList<LogEntry>();
						collectHits(searcher, collector.topDocs(offset), log.getDialect(), result);
						return new ResultPage(result, offset, collector.getTotalHits());
					} finally {
						searcher.close();
					}
				} catch (IOException e) {
					// Unexpected exception; wrap with CoreException
					throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, 
							NLS.bind(Messages.LuceneIndexService_error_failedToReadIndex, 
									new Object[] {log.getName(), e.getLocalizedMessage()}), e));
				}
			}
		};
		runnable.setQueryContext((LuceneQueryContextImpl) context);
		return runnable.runWithIndexReader(context.getLogResource());
	}

	/**
	 * Returns the Lucene analyzer to use for indexing text fields.
	 * <p>
	 * Defaults to a <code>StandardAnalyzer</code> with Lucene 3.0 semantics.
	 * 
	 * @return the Lucene analyzer to use
	 */
	protected Analyzer getAnalyzer() {
		return new LimitTokenCountAnalyzer(new StandardAnalyzer(getMatchVersion()), 10000);
	}

	/**
	 * Returns the Lucene match version.
	 * <p>
	 * Defaults to Lucene 3.0 semantics.
	 * 
	 * @return the Lucene match version to use
	 */
	protected Version getMatchVersion() {
		return Version.LUCENE_30;
	}

	/*
	 * PRIVATE METHODS
	 */

	private Date getLatestEntryDate(ILogResource log) throws CoreException {
		ARunWithIndexReader<Date> runnable = new ARunWithIndexReader<Date>() {
			
			/* (non-Javadoc)
			 * @see net.sf.logsaw.index.impl.ARunWithIndexReader#doRunWithIndexReader(org.apache.lucene.index.IndexReader, net.sf.logsaw.core.framework.ILogResource)
			 */
			@Override
			protected Date doRunWithIndexReader(IndexReader reader, ILogResource log) throws CoreException {
				if (reader == null) {
					// Index does not exist yet
					return null;
				}
				int i = reader.maxDoc();
				if (i > 0) {
					try {
						Document doc = reader.document(i - 1);
						String val = doc.get(log.getDialect().getFieldProvider().getTimestampField().getKey());
						return log.getDialect().getFieldProvider().getTimestampField().fromIndexedValue(val);
					} catch (IOException e) {
						// Unexpected exception; wrap with CoreException
						throw new CoreException(new Status(IStatus.ERROR, IndexPlugin.PLUGIN_ID, 
								NLS.bind(Messages.LuceneIndexService_error_failedToReadIndex, 
										new Object[] {log.getName(), e.getLocalizedMessage()}), e));
					}
				}
				return null;
			}
		};
		return runnable.runWithIndexReader(log);
	}

	private SynchronizationResult updateIndex(ILogResource log, final Date latestEntryDate, 
			final IProgressMonitor monitor) throws CoreException {
		// Measure runtime
		final long startTime = System.currentTimeMillis();
		
		ARunWithIndexWriter<SynchronizationResult> runnable = new ARunWithIndexWriter<SynchronizationResult>() {
			
			/* (non-Javadoc)
			 * @see net.sf.logsaw.index.impl.ARunWithIndexWriter#doRunWithIndexWriter(org.apache.lucene.index.IndexWriter, net.sf.logsaw.core.framework.ILogResource)
			 */
			@Override
			protected SynchronizationResult doRunWithIndexWriter(final IndexWriter writer, 
					final ILogResource log) throws CoreException {
				ILogEntryCollector collector = new ALogEntryCollector(monitor) {
					
					/* (non-Javadoc)
					 * @see net.sf.logsaw.core.framework.support.ALogEntryCollector#doCollect(net.sf.logsaw.core.model.LogEntry)
					 */
					@Override
					protected boolean doCollect(final LogEntry entry)
							throws IOException {
						final Document doc = new Document();
						if (latestEntryDate != null) {
							Date d = entry.get(log.getDialect().getFieldProvider().getTimestampField());
							if (!d.after(latestEntryDate)) {
								// Skip entry because it was already indexed
								return false;
							}
						}
						
						// Setup visitor
						ILogEntryFieldVisitor visitor = new ILogEntryFieldVisitor() {

							/* (non-Javadoc)
							 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
							 */
							@Override
							public void visit(StringLogEntryField fld) {
								// Decide whether to analyze the field
								Field.Index analyzed = fld.isAnalyzed() ? 
										Field.Index.ANALYZED : Field.Index.NOT_ANALYZED;
								doc.add(new Field(fld.getKey(), 
										fld.toIndexedValue(entry.get(fld)), 
										Field.Store.YES, analyzed));
							}

							/* (non-Javadoc)
							 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
							 */
							@Override
							public void visit(LevelLogEntryField fld) {
								Level lvl = entry.get(fld);
								Assert.isTrue(lvl.getValue() > 0, "Level value must be a positive integer"); //$NON-NLS-1$
								doc.add(new NumericField(
										fld.getKey(), Field.Store.YES, true).setIntValue(
										fld.toIndexedValue(lvl)));
							}

							/* (non-Javadoc)
							 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
							 */
							@Override
							public void visit(DateLogEntryField fld) {
								doc.add(new NumericField(
										fld.getKey(), Field.Store.YES, true).setLongValue(
										fld.toIndexedValue(entry.get(fld))));
							}
						};
						for (ALogEntryField<?, ?> fld : log.getDialect().getFieldProvider().getAllFields()) {
							if (entry.contains(fld)) {
								fld.visit(visitor);
							}
						}
						writer.addDocument(doc);
						return true;
					}
				};
				// Perform synchronize
				log.synchronize(collector, monitor);
				return new SynchronizationResult(monitor.isCanceled(), collector.getTotalCollected(), 
						System.currentTimeMillis() - startTime, collector.getMessages());
			}
		};
		return runnable.runWithIndexWriter(log, getAnalyzer(), getMatchVersion());
	}

	private Query convertToQuery(final List<ARestriction<?>> restrictions) {
		if (restrictions.isEmpty()) {
			// Unrestricted
			return new MatchAllDocsQuery();
		}
		final BooleanQuery query = new BooleanQuery();
		
		// Setup visitor
		IRestrictionVisitor visitor = new IRestrictionVisitor() {

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.DateRestriction)
			 */
			@Override
			public void visit(final DateRestriction restriction) {
				ILogEntryFieldVisitor visitor = new LogEntryFieldVisitorAdapter() {
					/* (non-Javadoc)
					 * @see net.sf.logsaw.core.model.LogEntryFieldVisitorAdapter#visit(net.sf.logsaw.core.model.DateLogEntryField)
					 */
					@Override
					public void visit(DateLogEntryField fld) {
						if (restriction.getOperator().equals(Operators.OPERATOR_BEFORE)) {
							query.add(NumericRangeQuery.newLongRange(
									fld.getKey(), null, 
									fld.toIndexedValue(restriction.getValue()), 
									false, false), Occur.MUST);
						} else if (restriction.getOperator().equals(Operators.OPERATOR_AFTER)) {
							query.add(NumericRangeQuery.newLongRange(
									fld.getKey(), 
									fld.toIndexedValue(restriction.getValue()), null, 
									false, false), Occur.MUST);
						}
					}
				};
				restriction.getField().visit(visitor);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.LevelRestriction)
			 */
			@Override
			public void visit(final LevelRestriction restriction) {
				ILogEntryFieldVisitor visitor = new LogEntryFieldVisitorAdapter() {
					/* (non-Javadoc)
					 * @see net.sf.logsaw.core.model.LogEntryFieldVisitorAdapter#visit(net.sf.logsaw.core.model.LevelLogEntryField)
					 */
					@Override
					public void visit(LevelLogEntryField fld) {
						if (restriction.getOperator().equals(Operators.OPERATOR_GREATER_THAN)) {
							int val = restriction.getValue().getValue();
							if (val == ILogLevelProvider.ID_LEVEL_UNKNOWN) {
								// The value of -1 would match everything
								val = Integer.MAX_VALUE;
							}
							query.add(NumericRangeQuery.newIntRange(
									fld.getKey(), 
									val, null, 
									false, false), Occur.MUST);
						} else if (restriction.getOperator().equals(Operators.OPERATOR_LESS_THAN)) {
							query.add(NumericRangeQuery.newIntRange(
									fld.getKey(), null,  
									fld.toIndexedValue(restriction.getValue()), 
									false, false), Occur.MUST);
						} else if (restriction.getOperator().equals(Operators.OPERATOR_EQUALS)) {
							query.add(new TermQuery(new Term(restriction.getField().getKey(), 
									NumericUtils.intToPrefixCoded(restriction.getValue().getValue()))), Occur.MUST);
						} else if (restriction.getOperator().equals(Operators.OPERATOR_NOT_EQUALS)) {
							query.add(new TermQuery(new Term(restriction.getField().getKey(), 
									NumericUtils.intToPrefixCoded(restriction.getValue().getValue()))), Occur.MUST_NOT);
							if (isAllNegative(restrictions) && restrictions.get(0).equals(restriction)) {
								// By design Lucene does not process negative-only queries
								query.add(new MatchAllDocsQuery(), Occur.SHOULD);
							}
						}
					}
				};
				restriction.getField().visit(visitor);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.StringRestriction)
			 */
			@Override
			public void visit(final StringRestriction restriction) {
				if (restriction.getOperator().equals(Operators.OPERATOR_CONTAINS)) {
					try {
						// Setup phrase query with tokenized query string
						PhraseQuery phrase = new PhraseQuery();
						fillPhraseQuery(phrase, getAnalyzer(), 
								restriction.getField().getKey(), restriction.getValue());
						query.add(phrase, Occur.MUST);
					} catch (IOException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				} else if (restriction.getOperator().equals(Operators.OPERATOR_NOT_CONTAINS)) {
					try {
						// Setup phrase query with tokenized query string
						PhraseQuery phrase = new PhraseQuery();
						fillPhraseQuery(phrase, getAnalyzer(), 
								restriction.getField().getKey(), restriction.getValue());
						query.add(phrase, Occur.MUST_NOT);
						if (isAllNegative(restrictions) && restrictions.get(0).equals(restriction)) {
							// By design Lucene does not process negative-only queries
							query.add(new MatchAllDocsQuery(), Occur.SHOULD);
						}
					} catch (IOException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				} else if (restriction.getOperator().equals(Operators.OPERATOR_EQUALS)) {
					query.add(new TermQuery(new Term(restriction.getField().getKey(), 
							restriction.getValue())), Occur.MUST);
				} else if (restriction.getOperator().equals(Operators.OPERATOR_NOT_EQUALS)) {
					query.add(new TermQuery(new Term(restriction.getField().getKey(), 
							restriction.getValue())), Occur.MUST_NOT);
					if (isAllNegative(restrictions) && restrictions.get(0).equals(restriction)) {
						// By design Lucene does not process negative-only queries
						query.add(new MatchAllDocsQuery(), Occur.SHOULD);
					}
				} else if (restriction.getOperator().equals(Operators.OPERATOR_BEGINS_WITH)) {
					query.add(new PrefixQuery(new Term(restriction.getField().getKey(), 
							restriction.getValue())), Occur.MUST);
				} else if (restriction.getOperator().equals(Operators.OPERATOR_NOT_BEGINS_WITH)) {
					query.add(new PrefixQuery(new Term(restriction.getField().getKey(), 
							restriction.getValue())), Occur.MUST_NOT);
					if (isAllNegative(restrictions) && restrictions.get(0).equals(restriction)) {
						// By design Lucene does not process negative-only queries
						query.add(new MatchAllDocsQuery(), Occur.SHOULD);
					}
				}
			}
		};
		for (ARestriction<?> restriction : restrictions) {
			restriction.visit(visitor);
		}
		return query;
	}

	private void fillPhraseQuery(PhraseQuery phrase, Analyzer analyzer, String fld, String val) throws IOException {
		TokenStream ts = analyzer.tokenStream(fld, new StringReader(val));
		try {
			// Iterate over tokens and treat each token as term
			int pos = 0;
			while (ts.incrementToken()) {
				CharTermAttribute t = ts.getAttribute(CharTermAttribute.class);
				PositionIncrementAttribute p = ts.getAttribute(PositionIncrementAttribute.class);
				pos += p.getPositionIncrement();
				phrase.add(new Term(fld, t.toString()), pos - 1);
			}
			// End-of-stream clean-up
			ts.end();
		} finally {
			ts.close();
		}
	}

	private boolean isAllNegative(List<ARestriction<?>> restrictions) {
		for (ARestriction<?> restriction : restrictions) {
			if (!restriction.getOperator().isNegative()) {
				return false;
			}
		}
		return true;
	}

	private void collectHits(IndexSearcher searcher, TopDocs hits, 
			ILogDialect dialect, List<LogEntry> result) throws IOException {
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			final Document doc = searcher.doc(hits.scoreDocs[i].doc);
			final LogEntry entry = new LogEntry();
			
			// Setup visitor
			ILogEntryFieldVisitor visitor = new ILogEntryFieldVisitor() {

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
				 */
				@Override
				public void visit(StringLogEntryField fld) {
					String value = doc.get(fld.getKey());
					if (value != null) {
						entry.put(fld, fld.fromIndexedValue(value));
					}
				}

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
				 */
				@Override
				public void visit(LevelLogEntryField fld) {
					String value = doc.get(fld.getKey());
					if (value != null) {
						entry.put(fld, fld.fromIndexedValue(value));
					}
				}

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
				 */
				@Override
				public void visit(DateLogEntryField fld) {
					String value = doc.get(fld.getKey());
					if (value != null) {
						entry.put(fld, fld.fromIndexedValue(value));
					}	
				}
			};
			for (ALogEntryField<?, ?> field : dialect.getFieldProvider().getAllFields()) {
				field.visit(visitor);
			}
			result.add(entry);
		}
	}
}
