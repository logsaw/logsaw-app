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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.logsaw.core.dialect.ILogFieldProvider;
import net.sf.logsaw.core.field.ALogEntryField;

import org.eclipse.core.runtime.Assert;

/**
 * @author Philipp Nanz
 */
public abstract class ALogFieldProvider implements ILogFieldProvider {

	private Map<String, ALogEntryField<?, ?>> fieldMap;

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogEntryFieldProvider#findField(java.lang.String)
	 */
	@Override
	public final ALogEntryField<?, ?> findField(String key) {
		initFieldCache();
		return fieldMap.get(key);
	}

	private synchronized void initFieldCache() {
		if (fieldMap == null) {
			fieldMap = new HashMap<String, ALogEntryField<?, ?>>();
			List<ALogEntryField<?, ?>> fields = getAllFields();
			Assert.isNotNull(fields, "fields"); //$NON-NLS-1$
			for (ALogEntryField<?, ?> fld : fields) {
				fieldMap.put(fld.getKey(), fld);
			}
		}
	}
}
