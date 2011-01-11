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
package net.sf.logsaw.core.dialect.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.logsaw.core.dialect.ILogEntryCollector;
import net.sf.logsaw.core.field.LogEntry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Philipp Nanz
 */
public abstract class ALogEntryCollector implements ILogEntryCollector {

	private int totalCollected;
	private IProgressMonitor monitor;
	private List<IStatus> messages = new ArrayList<IStatus>();

	/**
	 * Constructor.
	 * @param monitor the progress monitor to check cancel state
	 */
	public ALogEntryCollector(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		this.monitor = monitor;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogEntryCollector#collect(net.sf.logsaw.core.model.LogEntry)
	 */
	@Override
	public final boolean collect(LogEntry entry) throws IOException {
		Assert.isNotNull(entry, "entry"); //$NON-NLS-1$
		if (doCollect(entry)) {
			totalCollected++;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogEntryCollector#getTotalCollected()
	 */
	@Override
	public final int getTotalCollected() {
		return totalCollected;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogEntryCollector#addMessage(org.eclipse.core.runtime.IStatus)
	 */
	@Override
	public final void addMessage(IStatus msg) {
		Assert.isNotNull(msg, "message");
		messages.add(msg);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogEntryCollector#getMessages()
	 */
	@Override
	public final List<IStatus> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.framework.ILogEntryCollector#isCanceled()
	 */
	@Override
	public final boolean isCanceled() {
		return monitor.isCanceled();
	}

	/**
	 * Subclasses may implement their own collection logic here.
	 * 
	 * @param entry a map containing the fields as label and value pairs
	 * @throws IOException if an IO error occurred
	 * @return <code>true</code> if the passed entry has been added to the index
	 */
	protected abstract boolean doCollect(LogEntry entry) throws IOException;
}
