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
package net.sf.logsaw.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Philipp Nanz
 */
public class DateFormatUtils {

	/**
	 * Constructor.
	 */
	private DateFormatUtils() {
		// Hide constructor
	}

	/**
	 * Returns whether the given date format pattern contains date components.
	 * @return <code>true</code> if pattern contains date components
	 */
	public static boolean hasDateComponent(String pattern) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date dateIn = cal.getTime();
			DateFormat df = new SimpleDateFormat(pattern);
			String str = df.format(dateIn);
			Date dateOut = df.parse(str);
			return dateIn.equals(dateOut);
		} catch (ParseException e) {
			// No problem
			return false;
		}
	}
}
