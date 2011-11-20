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
package net.sf.logsaw.ui.wizards;

import java.util.Collection;

import net.sf.logsaw.core.dialect.ILogDialect;
import net.sf.logsaw.core.dialect.ILogDialectFactory;
import net.sf.logsaw.ui.IHelpContexts;
import net.sf.logsaw.ui.Messages;

import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author Philipp Nanz
 */
public class DialectWizardPage extends WizardPage {

	private Collection<ILogDialectFactory> dialectFactories;
	private TableViewer tableViewer;
	private Text detailsText;
	private String[] columnNames = new String[] {"Name", "Bundle ID"}; //$NON-NLS-1$ //$NON-NLS-2$
	private String[] columnProperties = new String[] {"name", "contributor"}; //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Constructor.
	 * @param dialectFactories the dialect factories available
	 */
	public DialectWizardPage(Collection<ILogDialectFactory> dialectFactories) {
		super("selectDialect", Messages.DialectWizardPage_title, null); //$NON-NLS-1$
		Assert.isNotNull(dialectFactories, "dialectFactories"); //$NON-NLS-1$
		this.dialectFactories = dialectFactories;
		setDescription(Messages.DialectWizardPage_description);
		setPageComplete(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(root, SWT.BORDER | SWT.SINGLE | 
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				if (sel.isEmpty()) {
					detailsText.setText(""); //$NON-NLS-1$
					setPageComplete(false);
				} else {
					ILogDialectFactory dialect = (ILogDialectFactory) sel.getFirstElement();
					detailsText.setText(dialect.getDescription() != null ? 
							dialect.getDescription() : ""); //$NON-NLS-1$
					setPageComplete(true);
				}
			}
		});
		tableViewer.setComparator(new ViewerComparator());
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		// Setup columns
		for (String columnName : columnNames) {
			TableColumn col = new TableColumn(tableViewer.getTable(), SWT.NONE);
			col.setText(columnName);
		}
		
		// Setup data binding
		ViewerSupport.bind(tableViewer, new WritableList(dialectFactories, ILogDialect.class), 
				PojoProperties.values(columnProperties));
		
		for (TableColumn col : tableViewer.getTable().getColumns()) {
			col.pack();
		}
		
		Group group = new Group(root, SWT.NONE);
		group.setText(Messages.DialectWizardPage_label_details);
		group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		group.setLayout(new GridLayout());
		
		detailsText = new Text(group, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		GC gc = new GC(detailsText);
		FontMetrics fm = gc.getFontMetrics();
		gc.dispose();
		int cols = 80;
		int rows = 3;
		int width = cols * fm.getAverageCharWidth();
		int height = rows * fm.getHeight();
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.widthHint = width;
		gridData.heightHint = height;
		detailsText.setLayoutData(gridData);
		
		setControl(root);
		
		// Bind help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), 
				IHelpContexts.DIALECT_WIZARD_PAGE);
	}

	/**
	 * Returns the selected log dialect factory.
	 * @return the selected log dialect factory
	 */
	public ILogDialectFactory getSelectedDialectFactory() {
		IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
		return (ILogDialectFactory) sel.getFirstElement();
	}
}
