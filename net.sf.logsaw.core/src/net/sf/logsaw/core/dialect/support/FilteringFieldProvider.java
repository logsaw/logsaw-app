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
package net.sf.logsaw.core.dialect.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.logsaw.core.dialect.ILogFieldProvider;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public final class FilteringFieldProvider extends ALogFieldProvider {

	private ILogFieldProvider provider;
	private Collection<ALogEntryField<?, ?>> filter;

	/**
	 * Constructor.
	 * @param provider
	 * @param filter
	 */
	public FilteringFieldProvider(ILogFieldProvider provider,
			Collection<ALogEntryField<?, ?>> filter) {
		Assert.isNotNull(provider, "provider"); //$NON-NLS-1$
		Assert.isNotNull(filter, "filter"); //$NON-NLS-1$
		this.provider = provider;
		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogFieldProvider#getAllFields()
	 */
	@Override
	public List<ALogEntryField<?, ?>> getAllFields() {
		List<ALogEntryField<?, ?>> ret = provider.getAllFields();
		filterCollection(ret);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogFieldProvider#getDefaultFields()
	 */
	@Override
	public List<ALogEntryField<?, ?>> getDefaultFields() {
		List<ALogEntryField<?, ?>> ret = provider.getDefaultFields();
		filterCollection(ret);
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogFieldProvider#getLevelField()
	 */
	@Override
	public LevelLogEntryField getLevelField() {
		return getFiltered(provider.getLevelField());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogFieldProvider#getMessageField()
	 */
	@Override
	public StringLogEntryField getMessageField() {
		return getFiltered(provider.getMessageField());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogFieldProvider#getStacktraceField()
	 */
	@Override
	public StringLogEntryField getStacktraceField() {
		return getFiltered(provider.getStacktraceField());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogFieldProvider#getTimestampField()
	 */
	@Override
	public DateLogEntryField getTimestampField() {
		return getFiltered(provider.getTimestampField());
	}

	private void filterCollection(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (!filter.contains(obj)) {
				iter.remove();
			}
		}
	}

	private <T> T getFiltered(T fld) {
		if (filter.contains(fld)) {
			return fld;
		}
		return null;
	}
}
