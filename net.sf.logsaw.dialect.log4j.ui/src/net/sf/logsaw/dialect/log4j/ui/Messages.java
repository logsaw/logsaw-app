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
package net.sf.logsaw.dialect.log4j.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.dialect.log4j.ui.messages"; //$NON-NLS-1$
	public static String JBossDialectWizardPage_description;
	public static String JBossDialectWizardPage_label_version;
	public static String JBossDialectWizardPage_label_version4x;
	public static String JBossDialectWizardPage_label_version5x;
	public static String JBossDialectWizardPage_label_version6x;
	public static String JBossDialectWizardPage_title;
	public static String Log4JPatternLayoutWizardPage_description;
	public static String Log4JPatternLayoutWizardPage_label_pattern;
	public static String Log4JPatternLayoutWizardPage_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
