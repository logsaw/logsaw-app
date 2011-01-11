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
package net.sf.logsaw.core.field.model;

import net.sf.logsaw.core.dialect.ILogLevelProvider;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public final class LevelLogEntryField extends ALogEntryField<Integer, Level> {

	private ILogLevelProvider levelProvider;

	/**
	 * Constructor.
	 * 
	 * @param key the key
	 * @param label the label
	 * @param levelProvider the log level provider
	 */
	public LevelLogEntryField(String key, String label, ILogLevelProvider levelProvider) {
		super(key, label);
		Assert.isNotNull(levelProvider, "levelProvider"); //$NON-NLS-1$
		this.levelProvider = levelProvider;
	}

	/**
	 * @return the levelProvider
	 */
	public ILogLevelProvider getLevelProvider() {
		return levelProvider;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.field.ALogEntryField#fromIndexedValue(java.lang.String)
	 */
	@Override
	public Level fromIndexedValue(String str) {
		Assert.isNotNull(str, "str"); //$NON-NLS-1$
		Level lvl = levelProvider.findLevel(Integer.parseInt(str));
		if (lvl != null) {
			return lvl;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.field.ALogEntryField#toIndexedValue(java.lang.Object)
	 */
	@Override
	public Integer toIndexedValue(Level obj) {
		Assert.isNotNull(obj, "obj"); //$NON-NLS-1$
		return Integer.valueOf(obj.getValue());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#isValidInput(java.lang.String)
	 */
	@Override
	public boolean isValidInput(String str) {
		return levelProvider.findLevel(str).getValue() != ILogLevelProvider.ID_LEVEL_UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#fromInputValue(java.lang.String, net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public Level fromInputValue(String str, ILogResource log) {
		Assert.isNotNull(str, "str"); //$NON-NLS-1$
		return levelProvider.findLevel(str);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.model.ALogEntryField#toInputValue(java.lang.Object, net.sf.logsaw.core.ILogResource)
	 */
	@Override
	public String toInputValue(Level obj, ILogResource log) {
		Assert.isNotNull(obj, "obj"); //$NON-NLS-1$
		return obj.getName();
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
