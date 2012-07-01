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
package net.sf.logsaw.core.internal.logresource.simple;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An input stream that advances an progress monitor as kilobytes are read 
 * from the underlying inputstream.
 * 
 * @author Philipp Nanz
 */
public final class ProgressingInputStream extends FilterInputStream {

	private IProgressMonitor monitor;
	private int processed;

	/**
	 * Constructor.
	 * @param in the input stream
	 * @param monitor the progress monitor
	 */
	public ProgressingInputStream(InputStream in, IProgressMonitor monitor) {
		super(in);
		this.monitor = monitor;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = super.read(b, off, len);
		processed += read;
		while (processed > FileUtils.ONE_KB) {
			// Advance progress monitor
			processed -= FileUtils.ONE_KB;
			monitor.worked(1);
		}
		return read;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
}
