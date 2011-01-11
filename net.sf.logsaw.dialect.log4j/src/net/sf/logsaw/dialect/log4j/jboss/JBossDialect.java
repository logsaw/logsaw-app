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
package net.sf.logsaw.dialect.log4j.jboss;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.IConfigOptionVisitor;
import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.dialect.ILogFieldProvider;
import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.log4j.pattern.Log4JConversionPatternTranslator;
import net.sf.logsaw.dialect.pattern.APatternDialect;
import net.sf.logsaw.dialect.pattern.IConversionPatternTranslator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Philipp Nanz
 */
public final class JBossDialect extends APatternDialect {

	public static final StringConfigOption OPTION_VERSION = 
		new StringConfigOption("version", "JBoss Version"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final String VERSION_4 = "4.x"; //$NON-NLS-1$
	public static final String VERSION_5 = "5.x"; //$NON-NLS-1$
	public static final String VERSION_6 = "6.x"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.APatternDialect#configure(net.sf.logsaw.core.config.IConfigOption, java.lang.Object)
	 */
	@Override
	public <T> void configure(IConfigOption<T> option, T value)
			throws CoreException {
		option.visit(new IConfigOptionVisitor() {
			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.config.IConfigOptionVisitor#visit(net.sf.logsaw.core.config.StringConfigOption, java.lang.String)
			 */
			@Override
			public void visit(StringConfigOption opt, String value) throws CoreException {
				if (OPTION_VERSION.equals(opt)) {
					Assert.isNotNull(value, "version"); //$NON-NLS-1$
					
					if (value.equals(VERSION_4)) {
						configure(OPTION_PATTERN, "%d %-5p [%c] %m%n"); //$NON-NLS-1$
					} else if (value.equals(VERSION_5) || value.equals(VERSION_6)) {
						// 6.x is actually not powered by Log4J, but the parser can read it nonetheless
						configure(OPTION_PATTERN, "%d %-5p [%c] (%t) %m%n"); //$NON-NLS-1$
					}
				}
			}
		}, value);
		super.configure(option, value);
	}

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

	/* (non-Javadoc)
	 * @see net.sf.logsaw.dialect.pattern.APatternDialect#getRequiredConfigOptions()
	 */
	@Override
	public List<IConfigOption<?>> getRequiredConfigOptions() {
		// The pattern is not required by this dialect; it will be set 
		// automatically when the version is configured
		List<IConfigOption<?>> ret = new ArrayList<IConfigOption<?>>();
		ret.add(OPTION_VERSION);
		return ret;
	}
}
