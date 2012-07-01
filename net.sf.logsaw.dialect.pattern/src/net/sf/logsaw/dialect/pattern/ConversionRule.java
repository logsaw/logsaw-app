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
package net.sf.logsaw.dialect.pattern;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philipp Nanz
 */
public final class ConversionRule {

	private boolean followedByQuotedString;
	private int beginIndex;
	private int length;
	private int minWidth = -1;
	private int maxWidth = -1;
	private String placeholderName;
	private String modifier;
	private Map<String, Object> properties = new HashMap<String, Object>();

	/**
	 * @return the followedByQuotedString
	 */
	public boolean isFollowedByQuotedString() {
		return followedByQuotedString;
	}

	/**
	 * @param followedByQuotedString the followedByQuotedString to set
	 */
	public void setFollowedByQuotedString(boolean followedByQuotedString) {
		this.followedByQuotedString = followedByQuotedString;
	}

	/**
	 * @return the beginIndex
	 */
	public int getBeginIndex() {
		return beginIndex;
	}

	/**
	 * @param beginIndex the beginIndex to set
	 */
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the minWidth or <code>-1</code> if not specified
	 */
	public int getMinWidth() {
		return minWidth;
	}

	/**
	 * @param minWidth the minWidth to set
	 */
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * @return the maxWidth or <code>-1</code> if not specified
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * @param maxWidth the maxWidth to set
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * @return the placeholderName
	 */
	public String getPlaceholderName() {
		return placeholderName;
	}

	/**
	 * @param placeholderName the placeholderName to set
	 */
	public void setPlaceholderName(String placeholderName) {
		this.placeholderName = placeholderName;
	}

	/**
	 * @return the modifier
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * Stores the given key value pair into this conversion rule.
	 * @param key the key
	 * @param value the value
	 */
	public void putProperty(String key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Returns the value for the given key from this conversion rule.
	 * @param <VT> the data type
	 * @param key the key
	 * @param clazz the class of the value object
	 * @return the value object or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, Class<T> clazz) {
		return (T) properties.get(key);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConversionRule [modifier=" + modifier + ", placeholderName=" //$NON-NLS-1$ //$NON-NLS-2$
				+ placeholderName + "]"; //$NON-NLS-1$
	}
}
