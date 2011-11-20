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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public class LocaleUtils {

	/**
	 * Constructor.
	 */
	private LocaleUtils() {
		// Hide constructor
	}

	/**
	 * Returns the most commonly used locales.
	 * @return an array of commonly used locale ids.
	 */
	public static String[] getLocaleIds() {
		List<String> ids = new ArrayList<String>();
		ids.add(getLocaleId(Locale.getDefault()));
		ids.add(getLocaleId(Locale.US));
		return ids.toArray(new String[ids.size()]);
	}

	/**
	 * Returns the id for the given locale.
	 * That is: <code>loc.language + "_" + loc.country</code> 
	 * @param loc the locale
	 * @return the locale id
	 */
	public static String getLocaleId(Locale loc) {
		Assert.isNotNull(loc, "loc"); //$NON-NLS-1$
		return loc.toString();
	}

	/**
	 * Returns the locale for the given id.
	 * @param id the id
	 * @return the matching locale
	 */
	public static Locale getLocaleById(String id) {
		Assert.isNotNull(id, "id"); //$NON-NLS-1$
		String[] seg = id.split("_"); //$NON-NLS-1$
		Assert.isTrue((seg.length <= 3) && (seg.length > 0), "Invalid locale id"); //$NON-NLS-1$
		for (Locale loc : Locale.getAvailableLocales()) {
			if ((seg.length == 1) && loc.getLanguage().equals(seg[0])) {
				return loc;
			} else if ((seg.length == 2) && loc.getLanguage().equals(seg[0]) && 
					loc.getCountry().equals(seg[1])) {
				return loc;
			} else if ((seg.length == 3) && loc.getLanguage().equals(seg[0]) && 
					loc.getCountry().equals(seg[1]) && loc.getVariant().equals(seg[2])) {
				return loc;
			}
		}
		return null;
	}
}
