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
package net.sf.logsaw.ui.wizards.support;

import net.sf.logsaw.core.config.IConfigurableObject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Philipp Nanz
 */
public interface IConfigurableObjectWizardPage extends IWizardPage {

	/**
	 * Sets the configurable object to modify.
	 * @param obj the configurable object to set
	 */
	void setConfigurableObject(IConfigurableObject obj);

	/**
	 * Applies the config options maintained by this wizard page to the given object.
	 * @throws CoreException if an error occurred
	 */
	void performFinish() throws CoreException;
}
