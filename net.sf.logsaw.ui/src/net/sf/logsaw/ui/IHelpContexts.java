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
package net.sf.logsaw.ui;

/**
 * @author Philipp Nanz
 */
public interface IHelpContexts {

	String PREFIX = UIPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

	String LOG_RESOURCES_VIEW = PREFIX + "log_resources_view"; //$NON-NLS-1$
	String LOG_VIEWER = PREFIX + "log_viewer"; //$NON-NLS-1$
	String DIALECT_WIZARD_PAGE = PREFIX + "dialect_wizard_page"; //$NON-NLS-1$
	String SIMPLE_LOG_RESOURCE_WIZARD_PAGE = PREFIX + "simple_log_resource_wizard_page"; //$NON-NLS-1$
	String FILTER_SETTINGS_DIALOG = PREFIX + "filter_settings_dialog"; //$NON-NLS-1$
	String MESSAGE_AND_STACKTRACE_VIEWS = PREFIX + "message_and_stacktrace_views"; //$NON-NLS-1$
}
