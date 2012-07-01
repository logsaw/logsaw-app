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
package net.sf.logsaw.core.field.model;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public final class StringLogEntryField extends ALogEntryField<String, String> {

	private boolean analyzed;
	
	/**
	 * Constructor.
	 * 
	 * @param key the key
	 * @param label the label
	 * @param analyzed whether values are to be analyzed
	 */
	public StringLogEntryField(String key, String label, boolean analyzed) {
		super(key, label);
		this.analyzed = analyzed;
	}

	/**
	 * @return the analyzed
	 */
	public boolean isAnalyzed() {
		return analyzed;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.field.ALogEntryField#fromIndexedValue(java.lang.String)
	 */
	@Override
	public String fromIndexedValue(String str) {
		return str;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.field.ALogEntryField#toIndexedValue(java.lang.Object)
	 */
	@Override
	public String toIndexedValue(String obj) {
		return obj;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.field.ALogEntryField#isValidInput(java.lang.String, net.sf.logsaw.core.logresource.ILogResource)
	 */
	@Override
	public boolean isValidInput(String str, ILogResource log) {
		return str.trim().length() > 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#fromInputValue(java.lang.String, net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public String fromInputValue(String str, ILogResource log) {
		return str;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#toInputValue(java.lang.Object, net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public String toInputValue(String obj, ILogResource log) {
		return obj;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#visit(net.sf.logsaw.core.model.ILogEntryFieldVisitor)
	 */
	@Override
	public void visit(ILogEntryFieldVisitor visitor) {
		Assert.isNotNull(visitor, "visitor"); //$NON-NLS-1$
		visitor.visit(this);
	}
}
