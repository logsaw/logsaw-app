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
package net.sf.logsaw.dialect.websphere.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.dialect.websphere.internal.messages"; //$NON-NLS-1$
	public static String WebsphereDialect_error_dateFormatNotSupported;
	public static String WebsphereDialect_error_failedToParseFile;
	public static String WebsphereDialect_error_failedToParseLine;
	public static String WebsphereDialect_error_failedToParseTimestamp;
	public static String WebsphereDialect_error_regexGroupNotSupported;
	public static String WebsphereDialect_label_className;
	public static String WebsphereDialect_label_eventType;
	public static String WebsphereDialect_label_message;
	public static String WebsphereDialect_label_methodName;
	public static String WebsphereDialect_label_shortName;
	public static String WebsphereDialect_label_threadId;
	public static String WebsphereDialect_label_timestamp;
	public static String WebsphereDialect_warning_unknownEventType;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
