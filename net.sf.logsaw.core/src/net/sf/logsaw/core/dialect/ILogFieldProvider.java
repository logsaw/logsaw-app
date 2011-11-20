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
package net.sf.logsaw.core.dialect;

import java.util.List;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;

/**
 * @author Philipp Nanz
 */
public interface ILogFieldProvider {

	/**
	 * Returns the field that matches the given key.
	 * @param key the key to search for
	 * @return the matching field or <code>null</code>
	 */
	ALogEntryField<?, ?> findField(String key);

	/**
	 * Returns the default fields provided by this dialect in an ordered fashion.
	 * @return a non-<code>null</code> ordered list of fields
	 */
	List<ALogEntryField<?, ?>> getDefaultFields();

	/**
	 * Returns all the fields provided by this dialect.
	 * @return a non-<code>null</code> list of fields
	 */
	List<ALogEntryField<?, ?>> getAllFields();

	/**
	 * Returns the timestamp field used for up-to-date check.
	 * @return the timestamp field or <code>null</code>
	 */
	DateLogEntryField getTimestampField();

	/**
	 * Returns the level field used for filtering.
	 * @return the level field or <code>null</code>
	 */
	LevelLogEntryField getLevelField();

	/**
	 * Returns the field that contains stacktrace information
	 * @return the stacktrace field or <code>null</code>
	 */
	StringLogEntryField getStacktraceField();

	/**
	 * Returns the field that contains the message
	 * @return the message field or <code>null</code>
	 */
	StringLogEntryField getMessageField();

}
