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


/**
 * Interface for use by the extension point <code>net.sf.logsaw.ui.dialectWizardPage</code>.
 * 
 * @author Philipp Nanz
 */
public interface ILogResourceWizardPageFactory {

	/**
	 * Constructs a new wizard page instance.
	 * @return the newly constructed wizard page
	 */
	ILogResourceWizardPage newWizardPage();
}
