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
package net.sf.logsaw.dialect.log4j.ui.jboss;

import net.sf.logsaw.core.config.IConfigurableObject;
import net.sf.logsaw.dialect.log4j.jboss.JBossDialect;
import net.sf.logsaw.dialect.log4j.ui.Messages;
import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Philipp Nanz
 */
public class JBossDialectWizardPage extends WizardPage implements IConfigurableObjectWizardPage {

	private IConfigurableObject configurableObject;
	private Combo versionCombo;

	/**
	 * Constructor.
	 */
	public JBossDialectWizardPage() {
		super("jboss", Messages.JBossDialectWizardPage_title, null); //$NON-NLS-1$
		setDescription(Messages.JBossDialectWizardPage_description);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage#setConfigurableObject(net.sf.logsaw.core.config.IConfigurableObject)
	 */
	@Override
	public void setConfigurableObject(IConfigurableObject obj) {
		configurableObject = obj;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage#performFinish()
	 */
	@Override
	public void performFinish() throws CoreException {
		String ver = null;
		// Beware of NPEs because versionCombo may not be initialized if finish is pressed directly
		if ((versionCombo == null) || (versionCombo.getSelectionIndex() == 0)) {
			ver = JBossDialect.VERSION_4;
		} else if (versionCombo.getSelectionIndex() == 1) {
			ver = JBossDialect.VERSION_5;
		} else if (versionCombo.getSelectionIndex() == 2) {
			ver = JBossDialect.VERSION_6;
		}
		Assert.isNotNull(ver, "version"); //$NON-NLS-1$
		configurableObject.configure(JBossDialect.OPTION_VERSION, ver);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		root.setLayout(layout);
		
		Label label = new Label(root, SWT.NONE);
		label.setText(Messages.JBossDialectWizardPage_label_version);
		versionCombo = new Combo(root, SWT.BORDER | SWT.READ_ONLY);
		String[] versions = new String[] {
				Messages.JBossDialectWizardPage_label_version4x, 
				Messages.JBossDialectWizardPage_label_version5x, 
				Messages.JBossDialectWizardPage_label_version6x};
		
		versionCombo.setItems(versions);
		versionCombo.select(0);
		
		setControl(root);
	}
}
