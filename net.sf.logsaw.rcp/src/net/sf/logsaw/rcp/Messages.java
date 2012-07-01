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
package net.sf.logsaw.rcp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.rcp.messages"; //$NON-NLS-1$
	public static String ApplicationActionBarAdvisor_menu_file;
	public static String ApplicationActionBarAdvisor_menu_help;
	public static String ApplicationActionBarAdvisor_menu_showView;
	public static String ApplicationActionBarAdvisor_menu_window;
	public static String ApplicationWorkbenchAdvisor_error_eventLoopException;
	public static String LogSawApplication_message_alreadyRunning;
	public static String LogSawApplication_message_internalError;
	public static String LogSawApplication_title_alreadyRunning;
	public static String LogSawApplication_title_internalError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
