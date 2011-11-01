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
package net.sf.logsaw.rcp;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManagerOverrides;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction quitAction;
	private IContributionItem showViewItem;
	private IWorkbenchAction newEditorAction;
	private IWorkbenchAction resetPerspectiveAction;
	private IWorkbenchAction openPreferencesAction;
	private IWorkbenchAction showHelpAction;
	private IWorkbenchAction searchHelpAction;
	private IWorkbenchAction dynamicHelpAction;
	private IWorkbenchAction aboutAction;

	/**
	 * Constructor.
	 * @param configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		quitAction = ActionFactory.QUIT.create(window);
		register(quitAction);
		
		showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		
		newEditorAction = ActionFactory.NEW_EDITOR.create(window);
		register(newEditorAction);
		
		resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
		register(resetPerspectiveAction);
		
		openPreferencesAction = ActionFactory.PREFERENCES.create(window);
		register(openPreferencesAction);
		
		showHelpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(showHelpAction);
		
		searchHelpAction = ActionFactory.HELP_SEARCH.create(window);
		register(searchHelpAction);
		
		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);
		register(dynamicHelpAction);
		
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_menu_file,
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		{
			fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			fileMenu.add(new Separator());
	        // If we're on OS X we shouldn't show this command in the File menu. It
			// should be invisible to the user. However, we should not remove it -
			// the carbon UI code will do a search through our menu structure
			// looking for it when Cmd-Q is invoked (or Quit is chosen from the
			// application menu.
	        ActionContributionItem quitItem = new ActionContributionItem(quitAction);
	        quitItem.setVisible(!Util.isMac());
			fileMenu.add(quitItem);
		}
		
		MenuManager windowMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_menu_window,
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);
		{
			MenuManager openViewMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_menu_showView, "showView"); //$NON-NLS-1$
			openViewMenu.add(showViewItem);
			windowMenu.add(newEditorAction);
	        windowMenu.add(new Separator());
			windowMenu.add(openViewMenu);
	        windowMenu.add(new Separator());
	        windowMenu.add(resetPerspectiveAction);
	        Separator sep = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
			sep.setVisible(!Util.isMac());
			windowMenu.add(sep);
			// See comment above at quitAction
	        ActionContributionItem openPreferencesItem = new ActionContributionItem(openPreferencesAction);
	        openPreferencesItem.setVisible(!Util.isMac());
	        windowMenu.add(openPreferencesItem);
		}
		
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_menu_help,
				IWorkbenchActionConstants.M_HELP);
		helpMenu.setOverrides(new IContributionManagerOverrides() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IContributionManagerOverrides#getVisible(org.eclipse.jface.action.IContributionItem)
			 */
			@Override
			public Boolean getVisible(IContributionItem item) {
				if ((item instanceof CommandContributionItem) && 
						((CommandContributionItem) item).getId().equals("org.eclipse.equinox.p2.ui.sdk.install")) {
					// Hide the 'Install New Software' action
					return Boolean.FALSE;
				}
				return null;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IContributionManagerOverrides#getText(org.eclipse.jface.action.IContributionItem)
			 */
			@Override
			public String getText(IContributionItem item) {
				return null;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IContributionManagerOverrides#getEnabled(org.eclipse.jface.action.IContributionItem)
			 */
			@Override
			public Boolean getEnabled(IContributionItem item) {
				return null;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IContributionManagerOverrides#getAcceleratorText(org.eclipse.jface.action.IContributionItem)
			 */
			@Override
			public String getAcceleratorText(IContributionItem item) {
				return null;
			}
			
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.IContributionManagerOverrides#getAccelerator(org.eclipse.jface.action.IContributionItem)
			 */
			@Override
			public Integer getAccelerator(IContributionItem item) {
				return null;
			}
		});
		menuBar.add(helpMenu);
		{
			helpMenu.add(showHelpAction);
			helpMenu.add(searchHelpAction);
			helpMenu.add(dynamicHelpAction);
			helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			helpMenu.add(new Separator());
			helpMenu.add(aboutAction);
		}
	}
}
