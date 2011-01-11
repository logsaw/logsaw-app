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
package net.sf.logsaw.dialect.log4j.ui.pattern;

import net.sf.logsaw.core.config.IConfigurableObject;
import net.sf.logsaw.dialect.log4j.pattern.Log4JPatternLayoutDialect;
import net.sf.logsaw.dialect.log4j.ui.Messages;
import net.sf.logsaw.ui.util.UIUtils;
import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Philipp Nanz
 */
public class Log4JPatternLayoutWizardPage extends WizardPage implements IConfigurableObjectWizardPage {

	private IConfigurableObject configurableObject;
	private Text patternText;
	private ControlDecoration patternDecoration;

	/**
	 * Constructor.
	 */
	public Log4JPatternLayoutWizardPage() {
		super("pattern", Messages.Log4JPatternLayoutWizardPage_title, null); //$NON-NLS-1$
		setDescription(Messages.Log4JPatternLayoutWizardPage_description);
		setPageComplete(false);
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
		configurableObject.configure(Log4JPatternLayoutDialect.OPTION_PATTERN, patternText.getText());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 10;
		root.setLayout(layout);
		
		Label label = new Label(root, SWT.NONE);
		label.setText(Messages.Log4JPatternLayoutWizardPage_label_pattern);
		patternText = new Text(root, SWT.BORDER);
		patternText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		patternText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		patternDecoration = UIUtils.createDecorator(patternText, ""); //$NON-NLS-1$
		
		setControl(root);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			// Update decorator
			validateInput();
		}
	}

	private void validateInput() {
		try {
			configurableObject.validate(Log4JPatternLayoutDialect.OPTION_PATTERN, patternText.getText());
			patternDecoration.hide();
			setPageComplete(true);
		} catch (CoreException e1) {
			patternDecoration.setDescriptionText(e1.getMessage());
			patternDecoration.show();
			setPageComplete(false);
		}
	}
}
