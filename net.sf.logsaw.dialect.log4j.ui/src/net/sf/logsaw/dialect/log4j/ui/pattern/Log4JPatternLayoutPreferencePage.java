/*******************************************************************************
 * Copyright (c) 2011 LogSaw project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    LogSaw project committers - initial API and implementation
 *******************************************************************************/
package net.sf.logsaw.dialect.log4j.ui.pattern;

import java.io.IOException;
import java.util.Set;

import net.sf.logsaw.dialect.log4j.Log4JDialectPlugin;
import net.sf.logsaw.dialect.log4j.ui.Log4JDialectUIPlugin;
import net.sf.logsaw.dialect.log4j.ui.Messages;
import net.sf.logsaw.dialect.pattern.APatternDialect;
import net.sf.logsaw.ui.UIPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Philipp Nanz
 */
public class Log4JPatternLayoutPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private APatternDialect patternDialect;
	private Set<String> patternSet;
	private Button editButton;
	private Button removeButton;
	private ListViewer listViewer;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		try {
			patternSet = Log4JDialectUIPlugin.getDefault().loadKnownPatterns();
			patternDialect = (APatternDialect) Log4JDialectPlugin.getDefault()
					.getPatternLayoutDialectFactory().createLogDialect();
		} catch (IOException e) {
			// Log and show error
			UIPlugin.logAndShowError(new CoreException(new Status(IStatus.ERROR, 
					UIPlugin.PLUGIN_ID, e.getLocalizedMessage(), e)), false);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		try {
			Log4JDialectUIPlugin.getDefault().saveKnownPatterns(patternSet);
		} catch (IOException e) {
			// Log and show error
			UIPlugin.logAndShowError(new CoreException(new Status(IStatus.ERROR, 
					UIPlugin.PLUGIN_ID, e.getLocalizedMessage(), e)), false);
		}
		return super.performOk();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, false));
		
		listViewer = new ListViewer(root, SWT.BORDER | SWT.SINGLE | 
				SWT.H_SCROLL | SWT.V_SCROLL);
		listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				editButton.setEnabled(!sel.isEmpty());
				removeButton.setEnabled(!sel.isEmpty());
			}
		});
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setLabelProvider(new LabelProvider());
		listViewer.setSorter(new ViewerSorter());
		
		Composite buttonArea = new Composite(root, SWT.NONE);
		buttonArea.setLayout(new GridLayout());
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.BEGINNING;
		buttonArea.setLayoutData(gridData);
		
		Button addButton = new Button(buttonArea, SWT.PUSH);
		addButton.setText(Messages.Log4JPatternLayoutPreferencePage_label_add);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PatternDialog dlg = new PatternDialog(getShell(), patternDialect, null);
				if (dlg.open() == Window.OK) {
					patternSet.add(dlg.getPattern());
					refreshList();
				}
			}
		});
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		editButton = new Button(buttonArea, SWT.PUSH);
		editButton.setText(Messages.Log4JPatternLayoutPreferencePage_label_edit);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
				String oldPattern = (String) sel.getFirstElement();
				PatternDialog dlg = new PatternDialog(getShell(), patternDialect, oldPattern);
				if (dlg.open() == Window.OK) {
					patternSet.remove(oldPattern);
					patternSet.add(dlg.getPattern());
					refreshList();
				}
			}
		});
		editButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		removeButton = new Button(buttonArea, SWT.PUSH);
		removeButton.setText(Messages.Log4JPatternLayoutPreferencePage_label_remove);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				IStructuredSelection sel = (IStructuredSelection) listViewer.getSelection();
				patternSet.remove(sel.getFirstElement());
				listViewer.setSelection(StructuredSelection.EMPTY);
				refreshList();
			}
		});
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		
		refreshList();
		listViewer.setSelection(StructuredSelection.EMPTY);
		
		return root;
	}

	private void refreshList() {
		listViewer.setInput(patternSet.toArray());
	}
}
