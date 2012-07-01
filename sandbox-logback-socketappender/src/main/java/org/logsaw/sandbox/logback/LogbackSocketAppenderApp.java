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
package org.logsaw.sandbox.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philipp Nanz
 */
public class LogbackSocketAppenderApp {

	private static transient Logger logger = LoggerFactory.getLogger(LogbackSocketAppenderApp.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i = 0;
		try {
			while (true) {
				logger.info("Log Event #" + i++);
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			// nadda
		}
	}
}
