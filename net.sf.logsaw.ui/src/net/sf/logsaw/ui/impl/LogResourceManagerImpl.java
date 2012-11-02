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
package net.sf.logsaw.ui.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.config.IConfigChangedListener;
import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.IConfigOptionVisitor;
import net.sf.logsaw.core.config.IConfigurableObject;
import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.logresource.simple.SimpleLogResourceFactory;
import net.sf.logsaw.index.IIndexService;
import net.sf.logsaw.index.IndexPlugin;
import net.sf.logsaw.index.SynchronizationResult;
import net.sf.logsaw.ui.IGenericCallback;
import net.sf.logsaw.ui.ILogResourceManager;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.progress.IProgressConstants2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Philipp Nanz
 */
public class LogResourceManagerImpl implements ILogResourceManager {

	private static transient Logger logger = LoggerFactory.getLogger(LogResourceManagerImpl.class);

	private static final String LOG_RESOURCE_STATE_FILE = "logResources.xml"; //$NON-NLS-1$

	private static final int COMPAT_VERSION = 2;
	private static final String ELEM_ROOT = "logResources"; //$NON-NLS-1$
	private static final String ATTRIB_COMPAT = "compat"; //$NON-NLS-1$
	private static final String ELEM_LOG_RESOURCE = "logResource"; //$NON-NLS-1$
	private static final String ELEM_NAME = "name"; //$NON-NLS-1$
	private static final String ELEM_DIALECT = "dialect"; //$NON-NLS-1$
	private static final String ATTRIB_FACTORY = "factory"; //$NON-NLS-1$
	private static final String ELEM_PK = "pk"; //$NON-NLS-1$
	private static final String ELEM_OPTION = "option"; //$NON-NLS-1$
	private static final String ATTRIB_KEY = "key"; //$NON-NLS-1$
	private static final String ATTRIB_LABEL = "label"; //$NON-NLS-1$
	private static final String ATTRIB_VISIBLE = "visible"; //$NON-NLS-1$
	private static final String ATTRIB_TYPE = "type"; //$NON-NLS-1$
	private static final String VALUE_TYPE_STRING = "string"; //$NON-NLS-1$

	private static final QualifiedName QN_RESULT = 
		new QualifiedName(LogResourceManagerImpl.class.getCanonicalName(), "synchronizationResult"); //$NON-NLS-1$

	private IConfigChangedListener configChangedListener = new IConfigChangedListener() {
		
		/* (non-Javadoc)
		 * @see net.sf.logsaw.core.framework.support.IConfigChangedListener#configChanged(net.sf.logsaw.core.config.IConfigurableObject, net.sf.logsaw.core.config.IConfigOption)
		 */
		@Override
		public void configChanged(IConfigurableObject subject,
				IConfigOption<?> option) {
			try {
				saveState();
			} catch (CoreException e) {
				UIPlugin.logAndShowError(e, false);
			}
		}
	};

	private IPath stateFile;
	private Set<ILogResource> logSet;
	private Map<ILogResource, Job> syncInProgressMap = 
		Collections.synchronizedMap(new HashMap<ILogResource, Job>());

	/**
	 * Constructor.
	 * @param stateLocation the state location
	 */
	public LogResourceManagerImpl(IPath stateLocation) {
		this.stateFile = stateLocation.append(LOG_RESOURCE_STATE_FILE);
		try {
			loadState();
		} catch (CoreException e) {
			UIPlugin.logAndShowError(e, false);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#close()
	 */
	@Override
	public void close() throws CoreException {
		// Wait for all jobs to finish
		List<Job> l = new ArrayList<Job>(syncInProgressMap.values());
		for (Job job : l) {
			job.cancel();
			try {
				job.join();
			} catch (InterruptedException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#add(net.sf.logsaw.core.framework.ILogResource)
	 */
	@Override
	public void add(ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		// Check if resource already managed
		if (logSet.contains(log)) {
			throw new CoreException(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LogResourceManager_error_resourceAlreadyManaged, 
							new Object[] {log.toString()})));
		}
		
		// Create PK
		IndexPlugin.getDefault().getIndexService().createIndex(log);
		
		// Register log resource
		registerLogResource(log);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#remove(net.sf.logsaw.core.framework.ILogResource[])
	 */
	@Override
	public void remove(final ILogResource log) throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isTrue(logSet.contains(log), "Log is not managed"); //$NON-NLS-1$
		
		// Delete index folders
		IndexPlugin.getDefault().getIndexService().deleteIndex(log);
		
		// Unregister log resource
		unregisterLogResource(log);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#getAll()
	 */
	@Override
	public ILogResource[] getAll() {
		return logSet.toArray(new ILogResource[logSet.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#synchronize(net.sf.logsaw.core.framework.ILogResource, net.sf.logsaw.ui.IGenericCallback)
	 */
	@Override
	public void synchronize(final ILogResource log, 
			final IGenericCallback<SynchronizationResult> callback) 
			throws CoreException {
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isTrue(logSet.contains(log), "Log is not managed"); //$NON-NLS-1$
		
		// The index is updated asynchronously
		Job job = new Job(NLS.bind(Messages.LogResourceManager_indexSynchronizingJob_name, 
				log.getName())) {

			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IIndexService idx = IndexPlugin.getDefault().getIndexService();
				try {
					// Do the indexing
					setProperty(QN_RESULT, idx.synchronize(log, monitor));
				} catch (CoreException e) {
					return e.getStatus();
				}
				if (monitor.isCanceled()) {
					// Job was canceled
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.LONG);
		// Enable progress in taskbar on Windows 7
		job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, true);
		job.addJobChangeListener(new JobChangeAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
			 */
			@Override
			public void done(IJobChangeEvent event) {
				syncInProgressMap.remove(log);
			}
		});
		if (callback != null) {
			job.addJobChangeListener(new JobChangeAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
				 */
				@Override
				public void done(IJobChangeEvent event) {
					SynchronizationResult result = 
						(SynchronizationResult) event.getJob().getProperty(QN_RESULT);
					if (event.getResult().isOK()) {
						callback.doCallback(result);
					} else if (event.getResult().getSeverity() == IStatus.CANCEL) {
						callback.doCallback(result);
					}
				}
			});
		}
		syncInProgressMap.put(log, job);
		job.schedule();
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#isJobInProgress(net.sf.logsaw.core.framework.ILogResource)
	 */
	@Override
	public boolean isJobInProgress(ILogResource log) {
		return syncInProgressMap.containsKey(log);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.ILogResourceManager#saveState()
	 */
	@Override
	public synchronized void saveState() throws CoreException {
		XMLMemento rootElem = XMLMemento.createWriteRoot(ELEM_ROOT);
		rootElem.putInteger(ATTRIB_COMPAT, COMPAT_VERSION);
		for (ILogResource log : logSet) {
			IMemento logElem = rootElem.createChild(ELEM_LOG_RESOURCE);
			logElem.createChild(ELEM_NAME).putTextData(log.getName());
			IMemento dialectElem = logElem.createChild(ELEM_DIALECT);
			dialectElem.putString(ATTRIB_FACTORY, 
					log.getDialect().getFactory().getId());
			// Save config options of dialect
			saveConfigOptions(dialectElem, (IConfigurableObject) log.getDialect().getAdapter(IConfigurableObject.class));
			logElem.createChild(ELEM_PK).putTextData(log.getPK());
			// Save config options of resource
			saveConfigOptions(logElem, (IConfigurableObject) log.getAdapter(IConfigurableObject.class));
		}
		try {
			// Save to state file
			rootElem.save(new BufferedWriter(new OutputStreamWriter(
					FileUtils.openOutputStream(stateFile.toFile()), "UTF-8"))); //$NON-NLS-1$
		} catch (IOException e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LogResourceManager_error_failedToUpdateState, 
							new Object[] {e.getLocalizedMessage()}), e));
		}
	}

	private void registerLogResource(ILogResource log) {
		logSet.add(log);
		log.addConfigChangedListener(configChangedListener);
	}

	private void unregisterLogResource(ILogResource log) {
		log.removeConfigChangedListener(configChangedListener);
		logSet.remove(log);
	}

	private void saveConfigOptions(final IMemento parentElem, final IConfigurableObject co) throws CoreException {
		if (co == null) {
			// Nothing to save
			return;
		}
		List<IConfigOption<?>> options = co.getAllConfigOptions();
		for (IConfigOption<?> option : options) {
			option.visit(new IConfigOptionVisitor() {
				
				@Override
				public void visit(StringConfigOption opt, String value)
						throws CoreException {
					// Store as child element
					IMemento optElem = parentElem.createChild(ELEM_OPTION);
					optElem.putString(ATTRIB_KEY, opt.getKey());
					optElem.putString(ATTRIB_LABEL, opt.getLabel());
					optElem.putBoolean(ATTRIB_VISIBLE, opt.isVisible());
					optElem.putString(ATTRIB_TYPE, VALUE_TYPE_STRING);
					optElem.putTextData(co.getConfigValue(opt));
				}
			}, null);
		}
	}

	private synchronized void loadState() throws CoreException {
		logSet = new CopyOnWriteArraySet<ILogResource>();
		if (!stateFile.toFile().exists()) {
			// Not exists yet
			return;
		}
		IMemento rootElem = null;
		try {
			rootElem = XMLMemento.createReadRoot(new BufferedReader(
					new InputStreamReader(FileUtils.openInputStream(stateFile.toFile()), "UTF-8"))); //$NON-NLS-1$
		} catch (IOException e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LogResourceManager_error_failedToLoadState, 
							new Object[] {e.getLocalizedMessage()}), e));
		}
		// Check if we can read this
		Integer compat = rootElem.getInteger(ATTRIB_COMPAT);
		if ((compat == null) || (compat.intValue() != COMPAT_VERSION)) {
			throw new CoreException(new Status(IStatus.WARNING, UIPlugin.PLUGIN_ID, 
					Messages.LogResourceManager_warn_stateFileIncompatible));
		}
		
		List<IStatus> statuses = new ArrayList<IStatus>();
		for (IMemento logElem : rootElem.getChildren(ELEM_LOG_RESOURCE)) {
			String name = null;
			try {
				name = logElem.getChild(ELEM_NAME).getTextData();
				IMemento dialectElem = logElem.getChild(ELEM_DIALECT);
				String dialectFactory = dialectElem.getString(ATTRIB_FACTORY);
				ILogDialectFactory factory = CorePlugin.getDefault().getLogDialectFactory(dialectFactory);
				ILogDialect dialect = factory.createLogDialect();
				
				// Restore config options of dialect
				loadConfigOptions(dialectElem, (IConfigurableObject) dialect.getAdapter(IConfigurableObject.class));
				String pk = logElem.getChild(ELEM_PK).getTextData();
				
				// TODO Dynamic factory for log resource
				ILogResource log = SimpleLogResourceFactory.getInstance().createLogResource();
				log.setDialect(dialect);
				log.setName(name);
				log.setPK(pk);
				
				// Restore config options of resource
				loadConfigOptions(logElem, (IConfigurableObject) log.getAdapter(IConfigurableObject.class));
				
				// Unlock if necessary
				if (IndexPlugin.getDefault().getIndexService().unlock(log)) {
					logger.warn("Unlocked log resource " + log.getName()); //$NON-NLS-1$
				}
				// Register log resource
				registerLogResource(log);
			} catch (Exception e) {
				statuses.add(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
						NLS.bind(Messages.LogResourceManager_error_failedToRestoreLogResource, 
								new Object[] {name}), e));
			}
		}
		if (!statuses.isEmpty()) {
			MultiStatus multiStatus = new MultiStatus(UIPlugin.PLUGIN_ID, 
					0, statuses.toArray(new IStatus[statuses.size()]), 
					Messages.LogResourceManager_error_someLogResourcesCouldNotBeRestored, null);
			throw new CoreException(multiStatus);
		}
		logger.info("Loaded " + logSet.size() + " log resource(s)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void loadConfigOptions(final IMemento parentElem, final IConfigurableObject co) throws CoreException {
		if (co == null) {
			// Nothing to restore
			return;
		}
		IMemento[] optElems = parentElem.getChildren(ELEM_OPTION);
		for (final IMemento optElem : optElems) {
			IConfigOption<?> opt = null;
			String key = optElem.getString(ATTRIB_KEY);
			String label = optElem.getString(ATTRIB_LABEL);
			boolean visible = optElem.getBoolean(ATTRIB_VISIBLE);
			String type = optElem.getString(ATTRIB_TYPE);
			if (type.equals(VALUE_TYPE_STRING)) {
				opt = new StringConfigOption(key, label, visible);
			}
			Assert.isNotNull(opt, "Config option type not supported: " + type); //$NON-NLS-1$
			// Configure the option
			opt.visit(new IConfigOptionVisitor() {
				
				@Override
				public void visit(StringConfigOption opt, String value)
						throws CoreException {
					// Restore
					co.configure(opt, optElem.getTextData());
				}
			}, null);
		}
		Assert.isTrue(co.isConfigured(), "Object should be configured by now"); //$NON-NLS-1$
	}
}
