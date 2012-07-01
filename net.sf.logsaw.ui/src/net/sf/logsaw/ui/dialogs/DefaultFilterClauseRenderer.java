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
package net.sf.logsaw.ui.dialogs;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.util.UIUtils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Philipp Nanz
 */
public class DefaultFilterClauseRenderer extends AFilterClauseRenderer {

	private Text valueText;
	private ControlDecoration valueDecoration;

	/**
	 * Constructor.
	 * @param field the field
	 * @param log the log resource
	 */
	public DefaultFilterClauseRenderer(ALogEntryField<?, ?> field, ILogResource log) {
		super(field, log);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		valueText = new Text(parent, SWT.BORDER);
		valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		valueText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		valueDecoration = UIUtils.createErrorDecorator(valueText, 
				Messages.DefaultFilterClauseRenderer_error_invalidInput);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return valueText.setFocus();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#dispose()
	 */
	@Override
	public void dispose() {
		valueDecoration.hide();
		valueDecoration.dispose();
		valueText.dispose();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#getControl()
	 */
	@Override
	public Control getControl() {
		return valueText;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#getValue()
	 */
	@Override
	public String getValue() {
		return valueText.getText();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		Assert.isNotNull(value, "value"); //$NON-NLS-1$
		valueText.setText(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#validateInput()
	 */
	@Override
	public void validateInput() {
		if (getField().isValidInput(getValue(), getLogResource())) {
			// Ok
			setValid(true);
			valueDecoration.hide();
			fireInputChanged(true);
		} else {
			// Not ok
			setValid(false);
			valueDecoration.show();
			fireInputChanged(false);
		}
	}
}
