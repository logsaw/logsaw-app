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
package net.sf.logsaw.ui.views;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.ui.IHelpContexts;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Philipp Nanz
 */
public abstract class ALogEntryFieldView extends ViewPart {

	private ILogViewEditor activeEditor;
	private Text displayText;
	private Text noEntryText;
	private StackLayout stackLayout;
	private Composite root;
	private Composite noEntryArea;
	private Composite displayArea;
	private ISelectionListener selectionListener = new ISelectionListener() {

		/* (non-Javadoc)
		 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
		 */
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			ILogViewEditor editor = (ILogViewEditor) part.getAdapter(ILogViewEditor.class);
			if (editor == null) {
				// Not interested in this event
				return;
			}
			
			ILogResource log = editor.getLogResource();
			LogEntry entry = null;
			if (!selection.isEmpty()) {
				IStructuredSelection sel = (IStructuredSelection) selection;
				entry = (LogEntry) sel.getFirstElement();
			}
			updateContents(log, entry);
		}
	};
	private IPartListener partListener = new IPartListener() {

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
		 */
		@Override
		public void partOpened(IWorkbenchPart part) {
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
		 * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
		 */
		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part == activeEditor) {
				activeEditorChanged(null);
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
		 */
		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			// nadda
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
		 */
		@Override
		public void partActivated(IWorkbenchPart part) {
			ILogViewEditor editor = (ILogViewEditor) part.getAdapter(ILogViewEditor.class);
			if (editor != null) {
				activeEditorChanged(editor);
			}
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		root = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		root.setLayout(stackLayout);
		
		noEntryArea = new Composite(root, SWT.NONE);
		noEntryArea.setLayout(new GridLayout());
		noEntryText = new Text(noEntryArea, SWT.SINGLE);
		noEntryText.setBackground(noEntryArea.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		noEntryText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		noEntryText.setEditable(false);
		
		displayArea = new Composite(root, SWT.NONE);
		displayArea.setLayout(new FillLayout());
		displayText = new Text(displayArea, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		displayText.setBackground(noEntryArea.getDisplay().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND));
		displayText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		displayText.setEditable(false);
		
		IEditorPart activeEditor = getSite().getPage().getActiveEditor();
		ILogViewEditor editor = activeEditor != null ? 
				(ILogViewEditor) activeEditor.getAdapter(ILogViewEditor.class) : null;
		if (editor != null) {
			// Pick up editor that is already opened
			activeEditorChanged(editor);
		} else {
			activeEditorChanged(null);
		}
		
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().getPage().getWorkbenchWindow().getPartService().addPartListener(partListener);
		
		// Enable dynamic help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, 
				IHelpContexts.MESSAGE_AND_STACKTRACE_VIEWS);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		getSite().getPage().getWorkbenchWindow().getPartService().removePartListener(partListener);
		getSite().getPage().removeSelectionListener(selectionListener);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		root.setFocus();
	}

	/**
	 * To be called when the active editor changes.
	 * @param editor the active editor or <code>null</code>
	 */
	public void activeEditorChanged(ILogViewEditor editor) {
		activeEditor = editor;
		if (editor == null) {
			// No editor anymore
			updateContents(null, null);
		} else {
			// Update view
			updateContents(editor.getLogResource(), editor.getSelectedLogEntry());
		}
	}

	/**
	 * Subclasses must override this method to determine which field is to be displayed.
	 * @param dialect the log dialect
	 * @return the field to display or <code>null</code>
	 */
	protected abstract StringLogEntryField getFieldToDisplay(ILogDialect dialect);

	private void updateContents(ILogResource log, LogEntry entry) {
		if (log == null) {
			// No open editor
			noEntryText.setText(Messages.ALogEntryFieldView_label_noLogResourceOpened);
			stackLayout.topControl = noEntryArea;
			root.layout();
		} else if (getFieldToDisplay(log.getDialect()) == null) {
			// The field is not supported by this log resource
			noEntryText.setText(Messages.ALogEntryFieldView_label_fieldNotSupported);
			stackLayout.topControl = noEntryArea;
			root.layout();
		} else {
			// Field is available
			String content = null;
			if (entry != null) {
				content = entry.get(getFieldToDisplay(log.getDialect()));
			}
			displayText.setText(content != null ? content : ""); //$NON-NLS-1$
			stackLayout.topControl = displayArea;
			root.layout();
		}
	}
}
