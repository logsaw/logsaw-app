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
package net.sf.logsaw.dialect.log4j.xml;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.support.ALogDialectFactory;

/**
 * @author Philipp Nanz
 */
public final class Log4JXMLLayoutDialectFactory extends ALogDialectFactory {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.dialect.support.ALogDialectFactory#doCreateLogDialect()
	 */
	@Override
	protected ILogDialect doCreateLogDialect() {
		return new Log4JXMLLayoutDialect();
	}
}
