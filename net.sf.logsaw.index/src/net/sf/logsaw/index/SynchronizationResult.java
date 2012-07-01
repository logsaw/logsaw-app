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
package net.sf.logsaw.index;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Philipp Nanz
 */
public class SynchronizationResult {

	private boolean canceled;
	private int numberOfEntriesAdded;
	private long runtime;
	private List<IStatus> messages;

	/**
	 * Constructor.
	 * @param canceled whether the synchronization was canceled by the user
	 * @param numberOfEntriesAdded the number of entries added
	 * @param runtime the synchronization runtime 
	 * @param messages the messages generated during synchronization
	 */
	public SynchronizationResult(boolean canceled, int numberOfEntriesAdded, 
			long runtime, List<IStatus> messages) {
		this.canceled = canceled;
		this.numberOfEntriesAdded = numberOfEntriesAdded;
		this.runtime = runtime;
		this.messages = messages;
	}

	/**
	 * @return the canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * @return the numberOfEntriesAdded
	 */
	public int getNumberOfEntriesAdded() {
		return numberOfEntriesAdded;
	}

	/**
	 * @return the runtime
	 */
	public long getRuntime() {
		return runtime;
	}

	/**
	 * @return the messages
	 */
	public List<IStatus> getMessages() {
		return messages;
	}
}
