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
package net.sf.logsaw.dialect.log4j.pattern;

import net.sf.logsaw.core.dialect.ILogFieldProvider;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.pattern.APatternDialect;
import net.sf.logsaw.dialect.pattern.IConversionPatternTranslator;

/**
 * @author Philipp Nanz
 */
public final class Log4JPatternLayoutDialect extends APatternDialect {

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.APatternDialect#doCreateFieldProvider()
	 */
	@Override
	protected ILogFieldProvider doCreateFieldProvider() {
		return Log4JDialectPlugin.getDefault().getFieldProvider();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.APatternDialect#doCreatePatternTranslator()
	 */
	@Override
	protected IConversionPatternTranslator doCreatePatternTranslator() {
		return new Log4JConversionPatternTranslator();
	}
}
