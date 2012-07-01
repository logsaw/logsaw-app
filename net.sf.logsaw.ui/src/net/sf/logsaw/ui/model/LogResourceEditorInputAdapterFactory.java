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
package net.sf.logsaw.ui.model;

import net.sf.logsaw.core.logresource.ILogResource;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IEditorInput;

/**
 * @author Philipp Nanz
 */
public class LogResourceEditorInputAdapterFactory implements IAdapterFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if ((adapterType == IEditorInput.class) && (adaptableObject instanceof ILogResource)) {
			return new LogResourceEditorInputAdapter((ILogResource) adaptableObject);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] {IEditorInput.class};
	}
}
