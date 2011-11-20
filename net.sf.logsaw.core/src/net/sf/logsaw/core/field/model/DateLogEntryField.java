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
package net.sf.logsaw.core.field.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.logresource.IHasTimeZone;
import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public final class DateLogEntryField extends ALogEntryField<Long, Date> {

	private static String utcDateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param key the key
	 * @param label the label
	 */
	public DateLogEntryField(String key, String label) {
		super(key, label);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#fromIndexedValue(java.lang.Object)
	 */
	@Override
	public Date fromIndexedValue(String str) {
		return new Date(Long.parseLong(str));
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#toIndexedValue(java.lang.Object)
	 */
	@Override
	public Long toIndexedValue(Date obj) {
		return Long.valueOf(obj.getTime());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#isValidInput(java.lang.String)
	 */
	@Override
	public boolean isValidInput(String str) {
		try {
			new SimpleDateFormat(utcDateFormatPattern).parse(str);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#fromInputValue(java.lang.String, net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public Date fromInputValue(String str, ILogResource log) {
		try {
			DateFormat df = new SimpleDateFormat(utcDateFormatPattern);
			IHasTimeZone tz = (IHasTimeZone) log.getAdapter(IHasTimeZone.class);
			if (tz != null) {
				// Apply TZ from source
				df.setTimeZone(tz.getTimeZone());
			}
			return df.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#toInputValue(java.lang.Object, net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public String toInputValue(Date obj, ILogResource log) {
		DateFormat df = new SimpleDateFormat(utcDateFormatPattern);
		IHasTimeZone tz = (IHasTimeZone) log.getAdapter(IHasTimeZone.class);
		if (tz != null) {
			// Apply TZ from source
			df.setTimeZone(tz.getTimeZone());
		}
		return df.format(obj);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#visit(net.sf.logsaw.core.model.ILogEntryFieldVisitor)
	 */
	@Override
	public void visit(ILogEntryFieldVisitor visitor) {
		Assert.isNotNull(visitor, "visitor"); //$NON-NLS-1$
		visitor.visit(this);
	}
}
