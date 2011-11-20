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
package net.sf.logsaw.core.config.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.logsaw.core.config.IConfigChangedListener;
import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.IConfigurableObject;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

/**
 * This is a convenience base class for classes that implement <code>IConfigurableObject</code>.
 * 
 * @author Philipp Nanz
 */
public abstract class AConfigurableObject extends PlatformObject implements IConfigurableObject {

	private Map<IConfigOption<?>, Object> configOptionValueMap = new HashMap<IConfigOption<?>, Object>();
	private List<IConfigChangedListener> listeners = new ArrayList<IConfigChangedListener>();

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#addConfigChangedListener(net.sf.logsaw.core.framework.support.IConfigChangedListener)
	 */
	@Override
	public final void addConfigChangedListener(IConfigChangedListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#removeConfigChangedListener(net.sf.logsaw.core.framework.support.IConfigChangedListener)
	 */
	@Override
	public final void removeConfigChangedListener(IConfigChangedListener listener) {
		listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#configureDefaults()
	 */
	@Override
	public void configureDefaults() throws CoreException {
		// to override
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurable#configure(net.sf.logsaw.core.config.IConfigOption, java.lang.Object)
	 */
	@Override
	public <T> void configure(IConfigOption<T> option, T value) throws CoreException {
		configOptionValueMap.put(option, value);
		fireConfigChangedListeners(option);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#isConfigured()
	 */
	@Override
	public boolean isConfigured() {
		List<IConfigOption<?>> options = getRequiredConfigOptions();
		Assert.isNotNull(options, "options"); //$NON-NLS-1$
		for (IConfigOption<?> opt : options) {
			if (!configOptionValueMap.containsKey(opt)) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#validate(net.sf.logsaw.core.config.IConfigOption, java.lang.Object)
	 */
	@Override
	public <T> void validate(IConfigOption<T> option, T value)
			throws CoreException {
		// to be overridden
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurable#getConfigValue(net.sf.logsaw.core.config.IConfigOption)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getConfigValue(IConfigOption<T> option) {
		return (T) configOptionValueMap.get(option);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.config.IConfigurableObject#getAllConfigOptions()
	 */
	@Override
	public List<IConfigOption<?>> getAllConfigOptions() {
		List<IConfigOption<?>> ret = new ArrayList<IConfigOption<?>>(configOptionValueMap.keySet());
		Collections.sort(ret, new Comparator<IConfigOption<?>>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(IConfigOption<?> o1, IConfigOption<?> o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		return ret;
	}

	private void fireConfigChangedListeners(IConfigOption<?> option) {
		for (IConfigChangedListener l : listeners) {
			l.configChanged(this, option);
		}
	}
}
