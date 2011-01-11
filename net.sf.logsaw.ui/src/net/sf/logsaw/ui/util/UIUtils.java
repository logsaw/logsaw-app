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
package net.sf.logsaw.ui.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * @author Philipp Nanz
 */
public class UIUtils {

	private static int millisHour = 3600000;
	private static int millisMinute = 60000;
	private static DateFormat runtimeDateFormat = new SimpleDateFormat("D'T'HH:mm:ss"); //$NON-NLS-1$
	private static DecimalFormat hourFormat = new DecimalFormat();
	private static DecimalFormat minuteFormat = new DecimalFormat();

	static {
		runtimeDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
		hourFormat.setMinimumIntegerDigits(2);
		hourFormat.setPositivePrefix("+"); //$NON-NLS-1$
		minuteFormat.setMinimumIntegerDigits(2);
		minuteFormat.setNegativePrefix(""); //$NON-NLS-1$
	}

	/**
	 * Constructor.
	 */
	private UIUtils() {
		// Hide constructor
	}

	/**
	 * Returns the given runtime formatted as ISO-8601 string.
	 * @param runtime the runtime
	 * @return the formatted runtime
	 */
	public static synchronized String formatRuntime(long runtime) {
		return decrementDays(runtimeDateFormat.format(new Date(runtime)));
	}

	/**
	 * Creates an error decorator for the given control.
	 * @param control the control to decorate
	 * @param message the message to show
	 * @return the decorator
	 */
	public static ControlDecoration createDecorator(Control control, String message) {
		ControlDecoration controlDecoration = new ControlDecoration(control,
				SWT.RIGHT | SWT.TOP);
		controlDecoration.setDescriptionText(message);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.hide();
		return controlDecoration;
	}

	/**
	 * Returns the default timezone.
	 * @return the default timezone or <code>null</code>
	 */
	public static TimeZone getDefaultTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		if (isKeepTimeZone(tz)) {
			return new TimeZoneWrapper(tz);
		}
		return null;
	}

	/**
	 * Returns the requested timezone for display in the UI.
	 * @param id the ID of the timezone
	 * @return the timezone
	 */
	public static TimeZone getTimeZone(String id) {
		return new TimeZoneWrapper(TimeZone.getTimeZone(id));
	}

	/**
	 * Returns all available timezones for display in the UI.
	 * @return an array of available timezones
	 */
	public static TimeZone[] getTimeZones() {
		String[] ids = TimeZone.getAvailableIDs();
		List<TimeZone> tzs = new ArrayList<TimeZone>();
		for (String id : ids) {
			TimeZone tz = TimeZone.getTimeZone(id);
			if (!isKeepTimeZone(tz)) {
				continue;
			}
			tzs.add(new TimeZoneWrapper(tz));
		}
		Collections.sort(tzs, new Comparator<TimeZone>() {

			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(TimeZone tz1, TimeZone tz2) {
				int cmp = tz1.getRawOffset() - tz2.getRawOffset();
				if (cmp != 0) {
					return cmp;
				}
				return tz1.getDisplayName().compareTo(tz2.getDisplayName());
			}
		});
		return tzs.toArray(new TimeZone[tzs.size()]);
	}

	private static String formatTimeZoneID(String id) {
		return id.replaceAll("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static boolean isKeepTimeZone(TimeZone tz) {
		if (tz.getID().startsWith("Africa")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("America")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Antarctica")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Asia")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Atlantic")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Australia")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Europe")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Indian")) { //$NON-NLS-1$
			return true;
		} else if (tz.getID().startsWith("Pacific")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	private static String decrementDays(String str) {
		int idx = str.indexOf('T');
		int days = Integer.parseInt(str.substring(0, idx));
		return Integer.toString(--days) + str.substring(idx);
	}

	private static class TimeZoneWrapper extends TimeZone {

		private static final long serialVersionUID = -6497503126257364607L;

		private TimeZone baseTZ;
		private String displayName;

		/**
		 * Constructor.
		 * @param baseTZ the base TZ
		 */
		public TimeZoneWrapper(TimeZone baseTZ) {
			Assert.isNotNull(baseTZ, "baseTZ"); //$NON-NLS-1$
			this.baseTZ = baseTZ;
			setID(baseTZ.getID());
			int offset = baseTZ.getRawOffset();
			int hours = offset / millisHour;
			int minutes = (offset % millisHour) / millisMinute;
			
			String displayName = null;
			if (offset != 0) {
				displayName = "(GMT" + hourFormat.format(hours) + ":" +  //$NON-NLS-1$ //$NON-NLS-2$
				minuteFormat.format(minutes) + ") " + formatTimeZoneID(baseTZ.getID()); //$NON-NLS-1$
			} else {
				displayName = "(GMT) " + formatTimeZoneID(baseTZ.getID()); //$NON-NLS-1$
			}
			this.displayName = displayName;
		}

		/* (non-Javadoc)
		 * @see java.util.TimeZone#getDisplayName(boolean, int, java.util.Locale)
		 */
		@Override
		public String getDisplayName(boolean daylight, int style, Locale locale) {
			return displayName;
		}

		/* (non-Javadoc)
		 * @see java.util.TimeZone#getOffset(int, int, int, int, int, int)
		 */
		@Override
		public int getOffset(int era, int year, int month, int day,
				int dayOfWeek, int milliseconds) {
			return baseTZ.getOffset(era, year, month, day, dayOfWeek, milliseconds);
		}

		/* (non-Javadoc)
		 * @see java.util.TimeZone#getRawOffset()
		 */
		@Override
		public int getRawOffset() {
			return baseTZ.getRawOffset();
		}

		/* (non-Javadoc)
		 * @see java.util.TimeZone#inDaylightTime(java.util.Date)
		 */
		@Override
		public boolean inDaylightTime(Date date) {
			return baseTZ.inDaylightTime(date);
		}

		/* (non-Javadoc)
		 * @see java.util.TimeZone#setRawOffset(int)
		 */
		@Override
		public void setRawOffset(int offsetMillis) {
			baseTZ.setRawOffset(offsetMillis);
		}

		/* (non-Javadoc)
		 * @see java.util.TimeZone#useDaylightTime()
		 */
		@Override
		public boolean useDaylightTime() {
			return baseTZ.useDaylightTime();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getID() == null) ? 0 : getID().hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TimeZoneWrapper other = (TimeZoneWrapper) obj;
			if (getID() == null) {
				if (other.getID() != null)
					return false;
			} else if (!getID().equals(other.getID()))
				return false;
			return true;
		}
	}
}
