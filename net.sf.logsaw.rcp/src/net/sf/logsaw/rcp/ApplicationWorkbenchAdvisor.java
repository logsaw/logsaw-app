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
package net.sf.logsaw.rcp;

import net.sf.logsaw.ui.perspectives.DefaultPerspective;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The workbench advisor.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#createWorkbenchWindowAdvisor(org.eclipse.ui.application.IWorkbenchWindowConfigurer)
	 */
	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
		super.initialize(configurer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#eventLoopException(java.lang.Throwable)
	 */
	@Override
	public void eventLoopException(Throwable exception) {
		StatusManager.getManager().handle(new Status(IStatus.ERROR, RCPPlugin.PLUGIN_ID, 
				Messages.ApplicationWorkbenchAdvisor_error_eventLoopException, exception), StatusManager.SHOW);
		// Super call takes care of the logging
		super.eventLoopException(exception);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	@Override
	public String getInitialWindowPerspectiveId() {
		return DefaultPerspective.ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#postStartup()
	 */
	@Override
	public void postStartup() {
		// Remove the 'Available Software Sites' preference page
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();
		pm.remove("org.eclipse.equinox.internal.p2.ui.sdk.ProvisioningPreferencePage/org.eclipse.equinox.internal.p2.ui.sdk.SitesPreferencePage"); //$NON-NLS-1$
	}
}
