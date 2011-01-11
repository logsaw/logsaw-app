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
package net.sf.logsaw.core.config.support;

import net.sf.logsaw.core.config.IConfigOption;

/**
 * Abstract base class for config options.
 * 
 * @author Philipp Nanz
 * @param <T> the value type
 */
public abstract class AConfigOption<T> implements IConfigOption<T> {

	private boolean visible;
	private String key;
	private String label;

	/**
	 * Constructor with <code>visible</code> defaulting to <code>true</code>.
	 * @param key the unique key of the config option
	 * @param label the label to display
	 */
	public AConfigOption(String key, String label) {
		this(key, label, true);
	}

	/**
	 * Constructor.
	 * @param key the unique key of the config option
	 * @param label the label to display
	 * @param visible whether to display this config option in the UI
	 */
	public AConfigOption(String key, String label, boolean visible) {
		this.key = key;
		this.label = label;
		this.visible = visible;
	}

	/**
	 * @return the key
	 */
	@Override
	public final String getKey() {
		return key;
	}

	/**
	 * @return the label
	 */
	@Override
	public final String getLabel() {
		return label;
	}

	/**
	 * @return the visible
	 */
	@Override
	public final boolean isVisible() {
		return visible;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AConfigOption<?> other = (AConfigOption<?>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
