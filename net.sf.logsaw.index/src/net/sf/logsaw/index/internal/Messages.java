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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.index.internal.messages"; //$NON-NLS-1$
	public static String LuceneIndexService_error_failedToDeleteIndex;
	public static String LuceneIndexService_error_failedToUnlockIndex;
	public static String LuceneIndexService_error_failedToUpdateIndex;
	public static String LuceneIndexService_error_failedToReadIndex;
	public static String LuceneIndexService_error_failedToTruncateIndex;
	public static String LuceneIndexService_info_autoTruncate;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
