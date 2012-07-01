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
package net.sf.logsaw.ui.wizards;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.core.util.LocaleUtils;
import net.sf.logsaw.ui.IHelpContexts;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.util.UIUtils;
import net.sf.logsaw.ui.wizards.support.ILogResourceWizardPage;

import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchEncoding;

/**
 * @author Philipp Nanz
 */
public class SimpleLogResourceWizardPage extends WizardPage implements ILogResourceWizardPage {

	private Text fileText;
	private ControlDecoration fileDecoration;
	private Combo encodingCombo;
	private ControlDecoration encodingDecoration;
	private Combo localeCombo;
	private ControlDecoration localeDecoration;
	private ComboViewer timeZoneComboViewer;
	private ControlDecoration timeZoneDecoration;
	private String filename;

	/**
	 * Constructor.
	 * @param filename the filename provided via DND
	 */
	public SimpleLogResourceWizardPage(String filename) {
		super("simpleLogResource", Messages.SimpleLogResourceWizardPage_title, null); //$NON-NLS-1$
		this.filename = filename;
		setDescription(Messages.SimpleLogResourceWizardPage_description);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		root.setLayout(layout);
		
		Label label = new Label(root, SWT.NONE);
		label.setText(Messages.SimpleLogResourceWizardPage_label_file);
		fileText = new Text(root, SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileDecoration = UIUtils.createErrorDecorator(fileText, 
				Messages.SimpleLogResourceWizardPage_error_selectFile);
		Button browseButton = new Button(root, SWT.PUSH);
		browseButton.setText(Messages.SimpleLogResourceWizardPage_label_browse);
		browseButton.addSelectionListener(new SelectionAdapter() {
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				String file = fd.open();
				if (file != null) {
					// Update text box
					fileText.setText(file);
				}
			}
			
		});
		
		fileText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		
		Group group = new Group(root, SWT.NONE);
		group.setText(Messages.SimpleLogResourceWizardPage_label_advanced);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.horizontalSpan = 3;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));
		
		label = new Label(group, SWT.NONE);
		label.setText(Messages.SimpleLogResourceWizardPage_label_fileEncoding);
		
		encodingCombo = new Combo(group, SWT.BORDER);
		encodingDecoration = UIUtils.createErrorDecorator(encodingCombo, 
				Messages.SimpleLogResourceWizardPage_error_specifyEncoding);
		List<String> encodings = new ArrayList<String>();
		encodings.add(WorkbenchEncoding.getWorkbenchDefaultEncoding());
		for (Object obj : WorkbenchEncoding.getDefinedEncodings()) {
			encodings.add(obj.toString());
		}
		encodingCombo.setItems(encodings.toArray(new String[encodings.size()]));
		encodingCombo.select(0);
		encodingCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		
		label = new Label(group, SWT.NONE);
		label.setText(Messages.SimpleLogResourceWizardPage_label_locale);
		
		localeCombo = new Combo(group, SWT.BORDER);
		localeDecoration = UIUtils.createErrorDecorator(localeCombo, 
				Messages.SimpleLogResourceWizardPage_error_specifyLocale);
		localeCombo.setItems(LocaleUtils.getLocaleIds());
		localeCombo.select(0);
		localeCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		
		label = new Label(group, SWT.NONE);
		label.setText(Messages.SimpleLogResourceWizardPage_label_timeZone);
		
		timeZoneComboViewer = new ComboViewer(group, SWT.BORDER | SWT.READ_ONLY);
		timeZoneDecoration = UIUtils.createErrorDecorator(timeZoneComboViewer.getControl(), 
				Messages.SimpleLogResourceWizardPage_error_specifyTimeZone);
		WritableList model = new WritableList(Arrays.asList(UIUtils.getTimeZones()), TimeZone.class);
		ViewerSupport.bind(timeZoneComboViewer, model, 
				PojoProperties.values(new String[] {"displayName"})); //$NON-NLS-1$
		TimeZone defTZ = UIUtils.getDefaultTimeZone();
		if (defTZ != null) {
			timeZoneComboViewer.setSelection(new StructuredSelection(defTZ));
		}
		
		// Initialize
		fileText.setText(filename != null ? filename : ""); //$NON-NLS-1$
		
		setControl(root);
		
		// Bind help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), 
				IHelpContexts.SIMPLE_LOG_RESOURCE_WIZARD_PAGE);
	}

	private void validateInput() {
		boolean complete = true;
		File f = new File(fileText.getText().trim());
		if (f.isFile() && f.exists()) {
			// Ok
			fileDecoration.hide();
		} else {
			// Not ok
			fileDecoration.show();
			complete = false;
		}
		
		boolean encodingSupported = false;
		try {
			encodingSupported = Charset.isSupported(encodingCombo.getText());
		} catch (IllegalCharsetNameException e) {
			// Nadda
		}
		if (encodingSupported) {
			// Ok
			encodingDecoration.hide();
		} else {
			// Not ok
			encodingDecoration.show();
			complete = false;
		}
		
		boolean localeSupported = 
			LocaleUtils.getLocaleById(localeCombo.getText()) != null;
		if (localeSupported) {
			// Ok
			localeDecoration.hide();
		} else {
			// Not ok
			localeDecoration.show();
			complete = false;
		}
		
		if (timeZoneComboViewer.getSelection().isEmpty()) {
			// Not ok
			timeZoneDecoration.show();
			complete = false;
		} else {
			// Ok
			timeZoneDecoration.hide();
		}
		setPageComplete(complete);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.wizards.support.ILogResourceWizardPage#createLogResource(net.sf.logsaw.core.framework.ILogDialect)
	 */
	@Override
	public ILogResource createLogResource(ILogDialect dialect)
			throws CoreException {
		return SimpleLogResourceFactory.getInstance().createLogResource(
				getSelectedFile(), getEncoding(), getLocale(), getTimeZone(), dialect);
	}

	private File getSelectedFile() {
		File f = new File(fileText.getText().trim());
		Assert.isTrue(f.isFile() && f.exists(), "File does not exist"); //$NON-NLS-1$
		return f;
	}

	private String getEncoding() {
		return encodingCombo.getText();
	}

	private Locale getLocale() {
		return LocaleUtils.getLocaleById(localeCombo.getText());
	}

	private TimeZone getTimeZone() {
		IStructuredSelection sel = (IStructuredSelection) timeZoneComboViewer.getSelection();
		return (TimeZone) sel.getFirstElement();
	}
}
