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
package net.sf.logsaw.core.config;

import java.util.List;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Interface to be implemented by clients that wish to be configurable.
 * 
 * @author Philipp Nanz
 */
public interface IConfigurableObject extends IAdaptable {

	/**
	 * Adds the given listener.
	 * @param listener the listener to add
	 */
	void addConfigChangedListener(IConfigChangedListener listener);

	/**
	 * Removes the given listener.
	 * @param listener the listener to remove
	 */
	void removeConfigChangedListener(IConfigChangedListener listener);

	/**
	 * Configures the default options.
	 * @throws CoreException if an error occurred
	 */
	void configureDefaults() throws CoreException;

	/**
	 * Configures the given option with the specified value.
	 * 
	 * @param <T> the value type
	 * @param option the option to configure
	 * @param value the value
	 * @throws CoreException if an error occurred
	 */
	<T> void configure(IConfigOption<T> option, T value) throws CoreException;

	/**
	 * Returns whether all config options have been configured.
	 * @return <code>true</code> when all config options have been configured
	 */
	boolean isConfigured();

	/**
	 * Validates the given option with the specified value.
	 * 
	 * @param <T> the value type
	 * @param option the option to validate
	 * @param value the value
	 * @throws CoreException if an error occurred
	 */
	<T> void validate(IConfigOption<T> option, T value) throws CoreException;

	/**
	 * Returns the value for the given option.
	 * @param <T> the value type
	 * @param option the option
	 * @return the value
	 */
	<T> T getConfigValue(IConfigOption<T> option);

	/**
	 * Returns all the options configured for this instance.
	 * @return all the options configured
	 */
	List<IConfigOption<?>> getAllConfigOptions();

	/**
	 * Returns the options required by this instance.
	 * @return the options required
	 */
	List<IConfigOption<?>> getRequiredConfigOptions();
}
