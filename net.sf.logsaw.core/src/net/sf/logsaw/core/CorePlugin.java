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
package net.sf.logsaw.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.internal.Messages;
import net.sf.logsaw.core.internal.query.DefaultRestrictionFactoryImpl;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictionFactory;
import net.sf.logsaw.core.query.Operator;
import net.sf.logsaw.core.query.Operators;
import net.sf.logsaw.core.query.support.ARestriction;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public final class CorePlugin extends Plugin {

	private static transient Logger logger = LoggerFactory.getLogger(CorePlugin.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.logsaw.core"; //$NON-NLS-1$

	// The extension point ID
	public static final String EXT_DIALECT_FACTORY_ID = "net.sf.logsaw.core.dialectFactory"; //$NON-NLS-1$

	// The class attribute
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	// The id attribute
	private static final String ATTR_ID = "id"; //$NON-NLS-1$

	// The shared instance
	private static CorePlugin plugin;

	private IRestrictionFactory restrictionFactory;

	/**
	 * The constructor
	 */
	public CorePlugin() {
		restrictionFactory = new DefaultRestrictionFactoryImpl();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CorePlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the restrictionFactory
	 */
	public IRestrictionFactory getRestrictionFactory() {
		return restrictionFactory;
	}

	/**
	 * Creates a new default restriction instance for the given input parameters.
	 * @param log the log resource
	 * @param fldKey the field key
	 * @param opId the operator id
	 * @param strVal the value as <code>String</code>
	 * @return a new default restriction instance
	 */
	public ARestriction<?> createRestriction(final ILogResource log, 
			final String fldKey, final Integer opId, final String strVal) {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isNotNull(fldKey, "fldKey"); //$NON-NLS-1$
		Assert.isNotNull(opId, "opId"); //$NON-NLS-1$
		Assert.isNotNull(strVal, "strVal"); //$NON-NLS-1$
		ALogEntryField<?, ?> fld = log.getDialect().getFieldProvider().findField(fldKey);
		final Operator op = Operators.getOperator(opId);
		final IRestrictionFactory rf = CorePlugin.getDefault().getRestrictionFactory();
		final ARestriction<?>[] ret = new ARestriction<?>[1];
		fld.visit(new ILogEntryFieldVisitor() {
			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
			 */
			@Override
			public void visit(StringLogEntryField fld) {
				String val = fld.fromInputValue(strVal, log);
				ret[0] = rf.newRestriction(fld, op, val);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
			 */
			@Override
			public void visit(LevelLogEntryField fld) {
				Level val = fld.fromInputValue(strVal, log);
				ret[0] = rf.newRestriction(fld, op, val);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
			 */
			@Override
			public void visit(DateLogEntryField fld) {
				Date val = fld.fromInputValue(strVal, log);
				ret[0] = rf.newRestriction(fld, op, val);
			}
		});
		return ret[0];
	}

	/**
	 * Returns a collection of all log dialect factories available.
	 * @return a collection of the available log dialect factories
	 * @throws CoreException if any dialect could not be loaded
	 */
	public Collection<ILogDialectFactory> getLogDialectFactories() throws CoreException {
		List<ILogDialectFactory> ret = new ArrayList<ILogDialectFactory>();
		IConfigurationElement[] factories = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_DIALECT_FACTORY_ID);
		for (IConfigurationElement factoryElem : factories) {
			String thisId = factoryElem.getAttribute(ATTR_ID);
			logger.info("Loading dialect factory: " + thisId); //$NON-NLS-1$
			ILogDialectFactory factory = (ILogDialectFactory) factoryElem.createExecutableExtension(ATTR_CLASS);
			initLogDialectFactory(factoryElem, factory);
			ret.add(factory);
		}
		return ret;
	}

	/**
	 * Returns the specified log dialect factory.
	 * @param id the id of the dialect factory to lookup
	 * @return the specified dialect factory
	 * @throws CoreException if dialect factory was not found or could not be loaded
	 */
	public ILogDialectFactory getLogDialectFactory(String id) throws CoreException {
		Assert.isNotNull(id, "id"); //$NON-NLS-1$
		IConfigurationElement[] factories = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_DIALECT_FACTORY_ID);
		for (IConfigurationElement factoryElem : factories) {
			String thisId = factoryElem.getAttribute(ATTR_ID);
			if ((thisId != null) && thisId.equals(id)) {
				logger.info("Found dialect factory: " + thisId); //$NON-NLS-1$
				ILogDialectFactory factory = (ILogDialectFactory) factoryElem.createExecutableExtension(ATTR_CLASS);
				initLogDialectFactory(factoryElem, factory);
				return factory;
			}
		}
		// Fatal error
		throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 
				NLS.bind(Messages.CorePlugin_error_dialectNotFound, 
						new Object[] {id})));
	}

	private void initLogDialectFactory(IConfigurationElement config, ILogDialectFactory factory) {
		factory.setName(config.getAttribute("name")); //$NON-NLS-1$
		factory.setId(config.getAttribute(ATTR_ID));
		if (config.getChildren("description").length == 1) { //$NON-NLS-1$
			factory.setDescription(config.getChildren("description")[0].getValue()); //$NON-NLS-1$
		}
		factory.setContributor(config.getContributor().getName());
	}
}
