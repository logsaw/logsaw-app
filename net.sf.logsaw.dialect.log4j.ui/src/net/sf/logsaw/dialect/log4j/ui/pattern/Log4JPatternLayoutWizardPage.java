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
package net.sf.logsaw.dialect.log4j.ui.pattern;

import java.io.IOException;
import java.util.Set;

import net.sf.logsaw.core.config.IConfigurableObject;
import net.sf.logsaw.dialect.log4j.pattern.Log4JPatternLayoutDialect;
import net.sf.logsaw.dialect.log4j.ui.Log4JDialectUIPlugin;
import net.sf.logsaw.dialect.log4j.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.util.UIUtils;
import net.sf.logsaw.ui.wizards.support.IConfigurableObjectWizardPage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
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
		try {
			Set<String> patterns = Log4JDialectUIPlugin.getDefault().loadKnownPatterns();
			if (patterns.add(patternText.getText())) {
				// Only call save when necessary
				Log4JDialectUIPlugin.getDefault().saveKnownPatterns(patterns);
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, 
					UIPlugin.PLUGIN_ID, e.getLocalizedMessage(), e));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
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
		patternDecoration = UIUtils.createErrorDecorator(patternText, ""); //$NON-NLS-1$
		
		try {
			Set<String> patterns = Log4JDialectUIPlugin.getDefault().loadKnownPatterns();
			if (!patterns.isEmpty()) {
				// Content assist is available
				UIUtils.createContentProposalDecorator(patternText,
						Messages.Log4JPatternLayoutWizardPage_dec_contentAssistAvailable);
				
				KeyStroke keyStroke = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
				ContentProposalAdapter adapter = new ContentProposalAdapter(patternText,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(patterns.toArray(new String[patterns.size()])),
						keyStroke, null);
				adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			}
		} catch (Exception e) {
			// Log and show error
			UIPlugin.logAndShowError(new CoreException(new Status(IStatus.ERROR, 
					UIPlugin.PLUGIN_ID, e.getLocalizedMessage(), e)), false);
		}
		
		setControl(root);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			// Update decorator and set focus
			validateInput();
			patternText.setFocus();
		}
	}

	private void validateInput() {
		try {
			configurableObject.validate(Log4JPatternLayoutDialect.OPTION_PATTERN, patternText.getText());
			patternDecoration.hide();
			setPageComplete(true);
		} catch (CoreException e1) {
			patternDecoration.setDescriptionText(e1.getLocalizedMessage());
			patternDecoration.show();
			setPageComplete(false);
		}
	}
}
