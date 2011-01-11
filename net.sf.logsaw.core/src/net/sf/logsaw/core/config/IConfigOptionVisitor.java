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
package net.sf.logsaw.core.config;

import net.sf.logsaw.core.config.model.StringConfigOption;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Philipp Nanz
 */
public interface IConfigOptionVisitor {

	/**
	 * Implements the visitor pattern.
	 * @param opt the concrete option
	 * @throws CoreException if an error occurred
	 */
	void visit(StringConfigOption opt, String value) throws CoreException;
}
