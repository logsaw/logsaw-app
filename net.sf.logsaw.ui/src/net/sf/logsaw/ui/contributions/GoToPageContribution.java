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
package net.sf.logsaw.ui.contributions;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandEvent;
import org.eclipse.core.commands.ICommandListener;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * @author Philipp Nanz
 */
public class GoToPageContribution extends
		WorkbenchWindowControlContribution {

	private CoolBar coolBar;
	private ToolBar toolBar;
	private Composite root;
	private Text text;
	private Label label;
	private NumberFormat nf;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		this.toolBar = (ToolBar) parent;
		this.coolBar = (CoolBar) toolBar.getParent(); // This is a bit hack-ish
		root = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		root.setLayout(gridLayout);
		
		ICommandService service = 
			(ICommandService) getWorkbenchWindow().getService(ICommandService.class);
		final Command cmd = service.getCommand("net.sf.logsaw.ui.commands.GoToPageCommand"); //$NON-NLS-1$
		
		text = new Text(root, SWT.BORDER | SWT.RIGHT);
		text.addKeyListener(new KeyAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					try {
						int pageNumber = Integer.valueOf(text.getText().trim());
						IHandlerService service = 
							(IHandlerService) getWorkbenchWindow().getService(IHandlerService.class);
						Parameterization param = new Parameterization(
								cmd.getParameter("net.sf.logsaw.ui.commands.GoToPageCommand.pageNumber"), Integer.toString(pageNumber)); //$NON-NLS-1$
						ParameterizedCommand paraCmd = new ParameterizedCommand(cmd, new Parameterization[] {param});
						service.executeCommand(paraCmd, null);
					} catch (NumberFormatException e1) {
						// nadda
					} catch (CommandException e1) {
						// Log and show error
						UIPlugin.logAndShowError(new CoreException(
								new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
								NLS.bind(Messages.GoToPageContribution_error_failedToExecuteCommand, 
										new Object[] {e1.getLocalizedMessage()}), e1)), false);
					}
				}
			}
		});
		
		label = new Label(root, SWT.NONE);
		// This number format is used for label and text field
		nf = new DecimalFormat();
		nf.setGroupingUsed(false);
		nf.setParseIntegerOnly(true);
		
		if (cmd != null) {
			updateState(cmd.isEnabled());
			cmd.addCommandListener(new ICommandListener() {

				/* (non-Javadoc)
				 * @see org.eclipse.core.commands.ICommandListener#commandChanged(org.eclipse.core.commands.CommandEvent)
				 */
				@Override
				public void commandChanged(CommandEvent commandEvent) {
					if (commandEvent.isEnabledChanged()) {
						updateState(commandEvent.getCommand().isEnabled());
					}
				}
			});
			getWorkbenchWindow().getPartService().addPartListener(new IPartListener() {

				/* (non-Javadoc)
				 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
				 */
				@Override
				public void partActivated(IWorkbenchPart part) {
					// nadda
				}

				/* (non-Javadoc)
				 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
				 */
				@Override
				public void partBroughtToTop(IWorkbenchPart part) {
					ILogViewEditor editor = (ILogViewEditor) part.getAdapter(ILogViewEditor.class);
					if (editor != null) {
						Integer selectedPage = (Integer) editor.getSelectedPage();
						updateWidgets(true, selectedPage.intValue(), editor.getPageCount());
						updateToolItemIfAvailable();
					}
				}

				/* (non-Javadoc)
				 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
				 */
				@Override
				public void partClosed(IWorkbenchPart part) {
					// nadda
				}

				/* (non-Javadoc)
				 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
				 */
				@Override
				public void partDeactivated(IWorkbenchPart part) {
					// nadda
				}

				/* (non-Javadoc)
				 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
				 */
				@Override
				public void partOpened(IWorkbenchPart part) {
					ILogViewEditor editor = (ILogViewEditor) part.getAdapter(ILogViewEditor.class);
					if (editor != null) {
						editor.addPageChangedListener(new IPageChangedListener() {

							/* (non-Javadoc)
							 * @see org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
							 */
							@Override
							public void pageChanged(PageChangedEvent event) {
								ILogViewEditor editor = (ILogViewEditor) event.getSource();
								Integer selectedPage = (Integer) event.getSelectedPage();
								updateWidgets(true, selectedPage.intValue(), editor.getPageCount());
								updateToolItemIfAvailable();
							}
						});
					}
				}
			});
		} else {
			updateState(false);
		}
		
		return root;
	}

	private void updateState(boolean enabled) {
		if (enabled) {
			ILogViewEditor editor = 
				(ILogViewEditor) getWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(ILogViewEditor.class);
			if (editor != null) {
				Integer selectedPage = (Integer) editor.getSelectedPage();
				updateWidgets(true, selectedPage.intValue(), editor.getPageCount());
			} else {
				// Fallback to disabled
				enabled = false;
			}
		}
		if (!enabled) {
			updateWidgets(false, 0, 0);
		}
		updateToolItemIfAvailable();
	}

	private void updateWidgets(boolean enabled, int selectedPage, int pageCount) {
		int maxLength = Integer.toString(pageCount).length();
		nf.setMaximumIntegerDigits(maxLength);
		nf.setMinimumIntegerDigits(maxLength);
		// Update text field
		text.setTextLimit(maxLength);
		text.setText(nf.format(selectedPage));
		label.setText("/ " + nf.format(pageCount)); //$NON-NLS-1$
		text.setEnabled(enabled);
		label.setEnabled(enabled);
	}

	private void updateToolItemIfAvailable() {
		boolean updated = false;
		for (ToolItem item : toolBar.getItems()) {
			if ((item.getControl() != null) && item.getControl().equals(root)) {
				// Found item to update
				int newWidth = computeWidth(root);
				if (item.getWidth() != newWidth) {
					item.setWidth(newWidth);
					updated = true;
				}
				break;
			}
		}
		if (updated) {
			for (CoolItem item : coolBar.getItems()) {
				if ((item.getControl() != null) && item.getControl().equals(toolBar)) {
					// Found item to update
					Point pt = item.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
					item.setSize(item.computeSize(pt.x, pt.y));
					break;
				}
			}
			
			// Update toolbar
			coolBar.update();
		}
	}
}
