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
package net.sf.logsaw.ui.wizards.support;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Philipp Nanz
 */
public interface ILogResourceWizardPage extends IWizardPage {

	/**
	 * Returns a newly created log resource.
	 * @param dialect the dialect to pass into the log resource
	 * @throws CoreException if an error occurred
	 */
	ILogResource createLogResource(ILogDialect dialect) throws CoreException;
}
