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
package net.sf.logsaw.dialect.log4j;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.dialect.support.ALogFieldProvider;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.dialect.log4j.internal.Messages;

/**
 * @author Philipp Nanz
 */
public final class Log4JFieldProvider extends ALogFieldProvider {

	public static final StringLogEntryField FIELD_LOGGER = 
		new StringLogEntryField("logger", Messages.Log4JDialect_label_logger, false); //$NON-NLS-1$
	public static final DateLogEntryField FIELD_TIMESTAMP = 
		new DateLogEntryField("timestamp", Messages.Log4JDialect_label_timestamp); //$NON-NLS-1$
	public static final LevelLogEntryField FIELD_LEVEL = 
		new LevelLogEntryField("level", Messages.Log4JDialect_label_level,  //$NON-NLS-1$
				new Log4JLevelProvider());
	public static final StringLogEntryField FIELD_THREAD = 
		new StringLogEntryField("thread", Messages.Log4JDialect_label_thread, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_MESSAGE = 
		new StringLogEntryField("message", Messages.Log4JDialect_label_message, true); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_NDC = 
		new StringLogEntryField("ndc", Messages.Log4JDialect_label_ndc, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_THROWABLE = 
		new StringLogEntryField("throwable", Messages.Log4JDialect_label_throwable, true); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_LOC_FILENAME = 
		new StringLogEntryField("locFilename", Messages.Log4JDialect_label_locFilename, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_LOC_CLASS = 
		new StringLogEntryField("locClass", Messages.Log4JDialect_label_locClass, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_LOC_METHOD = 
		new StringLogEntryField("locMethod", Messages.Log4JDialect_label_locMethod, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_LOC_LINE = 
		new StringLogEntryField("locLine", Messages.Log4JDialect_label_locLine, false); //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getAllFields()
	 */
	@Override
	public List<ALogEntryField<?, ?>> getAllFields() {
		List<ALogEntryField<?, ?>> ret = new ArrayList<ALogEntryField<?, ?>>();
		ret.add(FIELD_TIMESTAMP);
		ret.add(FIELD_LEVEL);
		ret.add(FIELD_LOGGER);
		ret.add(FIELD_THREAD);
		ret.add(FIELD_MESSAGE);
		ret.add(FIELD_NDC);
		ret.add(FIELD_THROWABLE);
		ret.add(FIELD_LOC_FILENAME);
		ret.add(FIELD_LOC_CLASS);
		ret.add(FIELD_LOC_METHOD);
		ret.add(FIELD_LOC_LINE);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getDefaultFields()
	 */
	@Override
	public List<ALogEntryField<?, ?>> getDefaultFields() {
		List<ALogEntryField<?, ?>> ret = new ArrayList<ALogEntryField<?, ?>>();
		ret.add(FIELD_TIMESTAMP);
		ret.add(FIELD_LEVEL);
		ret.add(FIELD_LOGGER);
		ret.add(FIELD_MESSAGE);
		ret.add(FIELD_THROWABLE);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getTimestampField()
	 */
	@Override
	public DateLogEntryField getTimestampField() {
		return FIELD_TIMESTAMP;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getLevelField()
	 */
	@Override
	public LevelLogEntryField getLevelField() {
		return FIELD_LEVEL;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getStacktraceField()
	 */
	@Override
	public StringLogEntryField getStacktraceField() {
		return FIELD_THROWABLE;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogDialect#getMessageField()
	 */
	@Override
	public StringLogEntryField getMessageField() {
		return FIELD_MESSAGE;
	}
}
