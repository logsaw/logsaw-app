/*******************************************************************************
 * Copyright (c) 2011 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.dialect.log4j.ui.pattern;

import net.sf.logsaw.dialect.log4j.pattern.Log4JPatternLayoutDialect;
import net.sf.logsaw.dialect.log4j.ui.Messages;
import net.sf.logsaw.dialect.pattern.APatternDialect;
import net.sf.logsaw.ui.util.UIUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Philipp Nanz
 */
public class PatternDialog extends Dialog {

	private APatternDialect dialect;
	private String pattern;
	private Text text;
	private ControlDecoration decoration;

	/**
	 * @param dialect
	 * @param parentShell
	 * @param pattern
	 */
	public PatternDialog(Shell parentShell, APatternDialect dialect, String pattern) {
		super(parentShell);
		this.dialect = dialect;
		this.pattern = pattern;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		root.setLayout(layout);
		root.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label label = new Label(root, SWT.NONE);
		label.setText(Messages.PatternDialog_label);
		text = new Text(root, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		if (pattern != null) {
			text.setText(pattern);
		}
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		decoration = UIUtils.createErrorDecorator(text, ""); //$NON-NLS-1$
		applyDialogFont(root);
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		pattern = text.getText();
		super.okPressed();
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	private void validateInput() {
		try {
			dialect.validate(Log4JPatternLayoutDialect.OPTION_PATTERN, text.getText());
			decoration.hide();
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} catch (CoreException e1) {
			decoration.setDescriptionText(e1.getLocalizedMessage());
			decoration.show();
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}
}
