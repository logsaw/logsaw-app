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
import net.sf.logsaw.ui.UIPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author Philipp Nanz
 */
public class LogResourceWorkbenchAdapter implements IWorkbenchAdapter {

	private ILogResource log;

	/**
	 * Constructor.
	 * @param log the log file
	 */
	public LogResourceWorkbenchAdapter(ILogResource log) {
		Assert.isNotNull(log, "log must not be null"); //$NON-NLS-1$
		this.log = log;
	}

	/**
	 * @return the log resource
	 */
	public ILogResource getLogResource() {
		return log;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object o) {
		return new Object[] {};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return UIPlugin.getImageDescriptor("icons/file_obj.gif"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	@Override
	public String getLabel(Object o) {
		return log.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object o) {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogResourceWorkbenchAdapter [log=" + log + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogResourceWorkbenchAdapter other = (LogResourceWorkbenchAdapter) obj;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}
}
