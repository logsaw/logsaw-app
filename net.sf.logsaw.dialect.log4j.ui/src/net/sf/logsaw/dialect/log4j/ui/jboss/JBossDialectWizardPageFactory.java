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
package net.sf.logsaw.dialect.log4j.ui.jboss;

import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage;
import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPageFactory;

/**
 * @author Philipp Nanz
 */
public class JBossDialectWizardPageFactory implements
		IConfigurableObjectWizardPageFactory {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPageFactory#getWizardPage()
	 */
	@Override
	public IConfigurableObjectWizardPage newWizardPage() {
		return new JBossDialectWizardPage();
	}
}
