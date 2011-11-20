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
package net.sf.logsaw.ui.viewers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A simple label provider for log entries
 * 
 * @author Philipp Nanz
 */
public class LogEntryTableLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	// Local image registry for all log level icons that are provided by the dialect
	private ImageRegistry imageRegistry = new ImageRegistry();
	private ALogEntryField<?, ?>[] fields;
	private ILogResource log;

	/**
	 * @param log the log to set
	 */
	public void setLog(ILogResource log) {
		this.log = log;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(ALogEntryField<?, ?>[] fields) {
		this.fields = fields;
	}

	private Image getImageFromRegistry(String iconPath) {
		Assert.isNotNull(iconPath, "iconPath must not be null"); //$NON-NLS-1$
		Image img = imageRegistry.get(iconPath);
		if (img == null) {
			imageRegistry.put(iconPath, UIPlugin.imageDescriptorFromPlugin(
					log.getDialect().getFactory().getContributor(), iconPath));
			img = imageRegistry.get(iconPath);
		}
		return img;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ((columnIndex == 0) && (log.getDialect().getFieldProvider().getLevelField() != null)) {
			// The icon column
			LogEntry entry = (LogEntry) element;
			Level level = entry.get(log.getDialect().getFieldProvider().getLevelField());
			String iconPath = log.getDialect().getFieldProvider().getLevelField().getLevelProvider().getIconPathForLevel(level);
			if (iconPath != null) {
				// Lookup icon in local image registry
				return getImageFromRegistry(iconPath);
			}
		}
		return null;
	}

	private String snipAtLinebreak(String str) {
		String firstLine = null;
		BufferedReader reader = new BufferedReader(new StringReader(str));
		try {
			firstLine = reader.readLine();
			if ((firstLine != null) && (firstLine.length() != str.length())) {
				return firstLine + Messages.LogEntryTableLabelProvider_snipSuffix;
			}
		} catch (IOException e) {
			// Should not happen, and if it happens it's no big deal
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return str;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			// The icon column
			return ""; //$NON-NLS-1$
		}
		final LogEntry entry = (LogEntry) element;
		if ((columnIndex - 1) < fields.length) {
			final String[] ret = new String[1];
			// Setup visitor
			ILogEntryFieldVisitor visitor = new ILogEntryFieldVisitor() {
				
				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
				 */
				@Override
				public void visit(StringLogEntryField fld) {
					String value = entry.get(fld);
					if (value != null) {
						ret[0] = snipAtLinebreak(fld.toInputValue(value, log));
					}
				}

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
				 */
				@Override
				public void visit(LevelLogEntryField fld) {
					Level value = entry.get(fld);
					if (value != null) {
						ret[0] = fld.toInputValue(value, log);
					}
				}

				/* (non-Javadoc)
				 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
				 */
				@Override
				public void visit(DateLogEntryField fld) {
					Date value = entry.get(fld);
					if (value != null) {
						ret[0] = fld.toInputValue(value, log);
					}
				}
			};
			ALogEntryField<?, ?> fld = fields[columnIndex - 1];
			fld.visit(visitor);
			if ((ret[0] == null) || (ret[0].length() == 0)) {
				return Messages.LogEntryTableLabelProvider_emptyValue;
			}
			return ret[0];
		}
		return Messages.LogEntryTableLabelProvider_emptyValue; 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		imageRegistry.dispose();
		super.dispose();
	}
}
