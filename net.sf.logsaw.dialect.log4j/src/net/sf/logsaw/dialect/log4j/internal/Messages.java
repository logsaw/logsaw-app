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
package net.sf.logsaw.dialect.log4j.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.dialect.log4j.internal.messages"; //$NON-NLS-1$
	public static String Log4JConversionRuleExtractor_error_moreThanOneNewLine;
	public static String Log4JConversionRuleExtractor_error_mustEndWithNewLine;
	public static String Log4JConversionRuleTranslator_error_dataFormatNotSupported;
	public static String Log4JConversionRuleTranslator_error_failedToParseTimestamp;
	public static String Log4JConversionRuleTranslator_error_unsupportedConversionCharacter;
	public static String Log4JConversionRuleTranslator_warning_unknownPriority;
	public static String Log4JDialect_error_parsingFailed;
	public static String Log4JDialect_label_level;
	public static String Log4JDialect_label_locClass;
	public static String Log4JDialect_label_locFilename;
	public static String Log4JDialect_label_locLine;
	public static String Log4JDialect_label_locMethod;
	public static String Log4JDialect_label_logger;
	public static String Log4JDialect_label_message;
	public static String Log4JDialect_label_ndc;
	public static String Log4JDialect_label_thread;
	public static String Log4JDialect_label_throwable;
	public static String Log4JDialect_label_timestamp;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
