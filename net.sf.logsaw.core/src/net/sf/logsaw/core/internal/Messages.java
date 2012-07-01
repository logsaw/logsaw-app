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
package net.sf.logsaw.core.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.core.internal.messages"; //$NON-NLS-1$
	public static String CorePlugin_error_dialectNotFound;
	public static String Operators_after;
	public static String Operators_before;
	public static String Operators_beginsWith;
	public static String Operators_contains;
	public static String Operators_equals;
	public static String Operators_greaterThan;
	public static String Operators_lessThan;
	public static String Operators_notBeginsWith;
	public static String Operators_notContains;
	public static String Operators_notEquals;
	public static String SimpleLogResource_error_fileNotFound;
	public static String SimpleLogResource_synchronizeTask_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
