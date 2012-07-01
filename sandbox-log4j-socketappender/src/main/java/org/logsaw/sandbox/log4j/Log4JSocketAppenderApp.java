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
package org.logsaw.sandbox.log4j;

import org.apache.log4j.Logger;

/**
 * @author Philipp Nanz
 */
public class Log4JSocketAppenderApp {

	private static transient Logger logger = Logger.getLogger(Log4JSocketAppenderApp.class);
	
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
