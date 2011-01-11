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
package net.sf.logsaw.core.field;

import java.util.HashMap;
import java.util.Map;


/**
 * This class represents a single log entry, consisting of multiple 
 * field value pairs.
 * <p>
 * Internally, this class is backed by a hash map.
 * 
 * @author Philipp Nanz
 */
public class LogEntry {
	
	private Map<Object, Object> map = new HashMap<Object, Object>();
	
	/**
	 * Puts the given field value pair into this log entry.
	 * @param <VT> the data type
	 * @param key the field
	 * @param value the value
	 */
	public final <VT> void put(ALogEntryField<?, VT> key, VT value) {
		map.put(key, value);
	}

	/**
	 * Returns the value for the given field from this log entry.
	 * @param <VT> the data type
	 * @param key the field
	 * @return the value or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public final <VT> VT get(ALogEntryField<?, VT> key) {
		return (VT) map.get(key);
	}

	/**
	 * Returns whether this log entry contains the given field.
	 * @param <VT> the data type
	 * @param key the field
	 * @return <code>true</code> if it contains <code>key</code>
	 */
	public final <VT> boolean contains(ALogEntryField<?, VT> key) {
		return map.containsKey(key);
	}
}
