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
package net.sf.logsaw.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.logsaw.ui.messages"; //$NON-NLS-1$
	public static String AddLogResourceAction_noLogDialects;
	public static String AddLogResourceWizard_title;
	public static String ALogEntryFieldView_label_fieldNotSupported;
	public static String ALogEntryFieldView_label_noLogResourceOpened;
	public static String ColumnsPropertyPage_label_deselectAll;
	public static String ColumnsPropertyPage_label_moveDown;
	public static String ColumnsPropertyPage_label_moveUp;
	public static String ColumnsPropertyPage_label_selectAll;
	public static String DefaultFilterClauseRenderer_error_invalidInput;
	public static String DialectPropertyPage_label_config;
	public static String DialectPropertyPage_label_name;
	public static String DialectWizardPage_description;
	public static String DialectWizardPage_label_details;
	public static String DialectWizardPage_title;
	public static String SimpleLogResourceWizardPage_description;
	public static String SimpleLogResourceWizardPage_error_selectFile;
	public static String SimpleLogResourceWizardPage_error_specifyEncoding;
	public static String SimpleLogResourceWizardPage_error_specifyLocale;
	public static String SimpleLogResourceWizardPage_error_specifyTimeZone;
	public static String SimpleLogResourceWizardPage_label_advanced;
	public static String SimpleLogResourceWizardPage_label_browse;
	public static String SimpleLogResourceWizardPage_label_file;
	public static String SimpleLogResourceWizardPage_label_fileEncoding;
	public static String SimpleLogResourceWizardPage_label_locale;
	public static String SimpleLogResourceWizardPage_label_timeZone;
	public static String SimpleLogResourceWizardPage_title;
	public static String FilterClauseComposite_label_remove;
	public static String FilterSettingsDialog_confirm_question;
	public static String FilterSettingsDialog_confirm_title;
	public static String FilterSettingsDialog_label_add;
	public static String FilterSettingsDialog_label_clear;
	public static String FilterSettingsDialog_label_delete;
	public static String FilterSettingsDialog_label_filterName;
	public static String FilterSettingsDialog_label_save;
	public static String FilterSettingsDialog_label_saveLoad;
	public static String FilterSettingsDialog_message;
	public static String FilterSettingsDialog_title;
	public static String LogEntryTableLabelProvider_emptyValue;
	public static String LogEntryTableLabelProvider_snipSuffix;
	public static String LogResourceManager_error_resourceAlreadyManaged;
	public static String LogResourceManager_error_failedToLoadState;
	public static String LogResourceManager_error_failedToUpdateState;
	public static String LogResourceManager_indexSynchronizingJob_name;
	public static String LogResourceManager_error_failedToRestoreLogResource;
	public static String LogResourceManager_error_someLogResourcesCouldNotBeRestored;
	public static String LogResourceManager_warn_stateFileIncompatible;
	public static String LogViewEditor_pageHeader;
	public static String LogViewEditor_pageHeader_empty;
	public static String LogViewEditor_pageHeader_filterSuffix;
	public static String LogViewEditorColumnConfiguration_error_failedToSave;
	public static String Generic_errorExecutingCommand;
	public static String Generic_info_jobInProgress;
	public static String GoToPageContribution_error_failedToExecuteCommand;
	public static String ResourcePropertyPage_label_config;
	public static String ResourcePropertyPage_label_identifier;
	public static String ResourcePropertyPage_label_indexSize;
	public static String ResourcePropertyPage_label_type;
	public static String SynchronizeLogResourceAction_canceled_message;
	public static String SynchronizeLogResourceAction_canceled_title;
	public static String SynchronizeLogResourceAction_finished_message;
	public static String SynchronizeLogResourceAction_finished_title;
	public static String RemoveLogResourceHandler_confirm_question;
	public static String RemoveLogResourceHandler_confirm_title;
	public static String RemoveLogResourceHandler_info_someCouldNotBeRemoved;
	public static String SynchronizeLogResourceHandler_info_someCouldNotBeSynchronized;
	public static String TruncateLogResourceHandler_confirm_question;
	public static String TruncateLogResourceHandler_confirm_title;
	public static String TruncateLogResourceHandler_info_someCouldNotBeTruncated;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
