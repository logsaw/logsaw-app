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

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Philipp Nanz
 */
public class LevelFilterClauseRenderer extends AFilterClauseRenderer {

	private Combo levelCombo;
	private LevelLogEntryField levelField;

	/**
	 * Constructor.
	 * @param field the field
	 * @param log the log resource
	 */
	public LevelFilterClauseRenderer(LevelLogEntryField field, ILogResource log) {
		super(field, log);
		this.levelField = field;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		levelCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		levelCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		List<String> levels = new ArrayList<String>();
		for (Level lvl : levelField.getLevelProvider().getLevels()) {
			levels.add(lvl.getName());
		}
		levelCombo.setItems(levels.toArray(new String[levels.size()]));
		levelCombo.select(0);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return levelCombo.setFocus();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#dispose()
	 */
	@Override
	public void dispose() {
		levelCombo.dispose();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#getControl()
	 */
	@Override
	public Control getControl() {
		return levelCombo;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#getValue()
	 */
	@Override
	public String getValue() {
		return levelCombo.getText();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		Assert.isNotNull(value, "value"); //$NON-NLS-1$
		levelCombo.setText(value);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.dialogs.IFilterClauseRenderer#validateInput()
	 */
	@Override
	public void validateInput() {
		if (getField().isValidInput(getValue(), getLogResource())) {
			// Ok
			setValid(true);
			fireInputChanged(true);
		} else {
			// Not ok
			setValid(false);
			fireInputChanged(false);
		}
	}
}
