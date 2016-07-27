/*******************************************************************************
 * Copyright (c) 2016 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.index.internal;

import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.CATENATE_ALL;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.GENERATE_NUMBER_PARTS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.GENERATE_WORD_PARTS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.PRESERVE_ORIGINAL;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.SPLIT_ON_CASE_CHANGE;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter.SPLIT_ON_NUMERICS;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 * A custom <code>Analyzer</code> for use with message fields.
 * 
 * @author Philipp Nanz
 */
public class LogMessageAnalyzer extends Analyzer {

	private Version matchVersion;
	private boolean inQueryMode;

	/**
	 * @param matchVersion
	 * @param inQueryMode
	 */
	public LogMessageAnalyzer(Version matchVersion, boolean inQueryMode) {
		this.matchVersion = matchVersion;
		this.inQueryMode = inQueryMode;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.Analyzer#createComponents(java.lang.String, java.io.Reader)
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);
		int flags = SPLIT_ON_CASE_CHANGE | SPLIT_ON_NUMERICS | GENERATE_NUMBER_PARTS | GENERATE_WORD_PARTS;
		if (!inQueryMode) {
			flags = flags | CATENATE_ALL | PRESERVE_ORIGINAL;
		}
		TokenStream filter = new WordDelimiterFilter(source, flags, null);
		filter = new LowerCaseFilter(matchVersion, filter);
		filter = new StopFilter(matchVersion, filter, StandardAnalyzer.STOP_WORDS_SET);
		return new TokenStreamComponents(source, filter);
	}
}
