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
package net.sf.logsaw.ui.editors;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * @author Philipp Nanz
 */
public class LogViewEditorColumnConfiguration {
	
	private static final String ELEM_ROOT = "columns"; //$NON-NLS-1$
	private static final String ELEM_COLUMN = "column"; //$NON-NLS-1$
	private static final String ATTRIB_WIDTH = "width"; //$NON-NLS-1$
	private static final StringConfigOption OPTION_COLUMNS = 
		new StringConfigOption("columns", "Columns", false); //$NON-NLS-1$ //$NON-NLS-2$
	
	private List<ALogEntryField<?, ?>> fields = new ArrayList<ALogEntryField<?, ?>>();
	private int[] widths = new int[0];
	
	private ILogResource log;
	
	/**
	 * Constructor.
	 * @param log the log resource
	 */
	public LogViewEditorColumnConfiguration(ILogResource log) {
		Assert.isNotNull(log, "log must not be null"); //$NON-NLS-1$
		this.log = log;
		try {
			init();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, true);
		}
	}

	/**
	 * @return the fields
	 */
	public List<ALogEntryField<?, ?>> getFields() {
		return fields;
	}

	/**
	 * @return the widths
	 */
	public int[] getWidths() {
		return widths;
	}

	/**
	 * Updates this column configuration with new values.
	 * @param fields the new fields to show
	 * @param widths the new widths
	 */
	public void update(List<ALogEntryField<?, ?>> fields, int[] widths) {
		Assert.isNotNull(fields, "fields must not be null"); //$NON-NLS-1$
		Assert.isNotNull(widths, "widths must not be null"); //$NON-NLS-1$
		Assert.isTrue(fields.size() == widths.length, "lengths must match"); //$NON-NLS-1$
		this.fields = fields;
		this.widths = widths;
		try {
			save();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, true);
		}
	}

	private void save() throws CoreException {
		XMLMemento rootElem = XMLMemento.createWriteRoot(ELEM_ROOT);
		int i = 0;
		for (ALogEntryField<?, ?> fld : fields) {
			IMemento colElem = rootElem.createChild(ELEM_COLUMN);
			colElem.putInteger(ATTRIB_WIDTH, widths[i++]);
			colElem.putTextData(fld.getKey());
		}
		try {
			StringWriter w = new StringWriter();
			// Save to byte buffer first
			rootElem.save(w);
			// Update log resource
			log.configure(OPTION_COLUMNS, w.toString());
		} catch (IOException e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LogViewEditorColumnConfiguration_error_failedToSave, 
							new Object[] {e.getLocalizedMessage()}), e));
		}
	}

	private void init() throws CoreException {
		String str = log.getConfigValue(OPTION_COLUMNS);
		if (str == null) {
			// Load default
			fields = log.getDialect().getFieldProvider().getDefaultFields();
			widths = new int[fields.size()];
		} else {
			// Read memento
			IMemento rootElem = XMLMemento.createReadRoot(new StringReader(str));
			IMemento[] columns = rootElem.getChildren(ELEM_COLUMN);
			fields = new ArrayList<ALogEntryField<?, ?>>();
			widths = new int[columns.length];
			int i = 0;
			for (IMemento colElem : columns) {
				ALogEntryField<?, ?> fld = log.getDialect().getFieldProvider().findField(colElem.getTextData());
				fields.add(fld);
				widths[i++] = colElem.getInteger(ATTRIB_WIDTH);
			}
		}
	}
}
