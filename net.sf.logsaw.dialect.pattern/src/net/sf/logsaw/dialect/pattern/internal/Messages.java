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
package net.sf.logsaw.dialect.pattern.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.dialect.pattern.internal.messages"; //$NON-NLS-1$
	public static String APatternDialect_error_failedToParseFile;
	public static String APatternDialect_error_failedToParseLine;
	public static String APatternDialect_error_invalidPattern;
	public static String APatternDialect_error_failedToTranslateToRegex;
	public static String RegexUtils_error_invalidDateFormat;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
