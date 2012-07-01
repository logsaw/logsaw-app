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
package net.sf.logsaw.dialect.websphere;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.dialect.support.ALogFieldProvider;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.dialect.websphere.internal.Messages;

/**
 * @author Philipp Nanz
 */
public final class WebsphereFieldProvider extends ALogFieldProvider {

	public static final DateLogEntryField FIELD_TIMESTAMP = 
		new DateLogEntryField("timestamp", Messages.WebsphereDialect_label_timestamp); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_THREAD_ID = 
		new StringLogEntryField("threadId", Messages.WebsphereDialect_label_threadId, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_SHORT_NAME = 
		new StringLogEntryField("shortName", Messages.WebsphereDialect_label_shortName, false); //$NON-NLS-1$
	public static final LevelLogEntryField FIELD_EVENT_TYPE = 
		new LevelLogEntryField("eventType", Messages.WebsphereDialect_label_eventType,  //$NON-NLS-1$
				new WebsphereLevelProvider());
	public static final StringLogEntryField FIELD_CLASS_NAME = 
		new StringLogEntryField("className", Messages.WebsphereDialect_label_className, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_METHOD_NAME = 
		new StringLogEntryField("methodName", Messages.WebsphereDialect_label_methodName, false); //$NON-NLS-1$
	public static final StringLogEntryField FIELD_MESSAGE = 
		new StringLogEntryField("message", Messages.WebsphereDialect_label_message, true); //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getAllFields()
	 */
	@Override
	public List<ALogEntryField<?, ?>> getAllFields() {
		List<ALogEntryField<?, ?>> ret = new ArrayList<ALogEntryField<?, ?>>();
		ret.add(FIELD_TIMESTAMP);
		ret.add(FIELD_THREAD_ID);
		ret.add(FIELD_SHORT_NAME);
		ret.add(FIELD_EVENT_TYPE);
		ret.add(FIELD_CLASS_NAME);
		ret.add(FIELD_METHOD_NAME);
		ret.add(FIELD_MESSAGE);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getDefaultFields()
	 */
	@Override
	public List<ALogEntryField<?, ?>> getDefaultFields() {
		List<ALogEntryField<?, ?>> ret = new ArrayList<ALogEntryField<?, ?>>();
		ret.add(FIELD_TIMESTAMP);
		ret.add(FIELD_THREAD_ID);
		ret.add(FIELD_SHORT_NAME);
		ret.add(FIELD_EVENT_TYPE);
		ret.add(FIELD_CLASS_NAME);
		ret.add(FIELD_METHOD_NAME);
		ret.add(FIELD_MESSAGE);
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
		return FIELD_EVENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.ILogDialect#getStacktraceField()
	 */
	@Override
	public StringLogEntryField getStacktraceField() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogDialect#getMessageField()
	 */
	@Override
	public StringLogEntryField getMessageField() {
		return FIELD_MESSAGE;
	}
}
