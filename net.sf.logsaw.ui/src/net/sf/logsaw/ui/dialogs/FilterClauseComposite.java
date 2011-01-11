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
package net.sf.logsaw.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.Operator;
import net.sf.logsaw.core.query.support.ARestriction;
import net.sf.logsaw.ui.Messages;

import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Philipp Nanz
 */
public class FilterClauseComposite extends Composite {

	private Button removeButton;
	private ComboViewer fieldComboViewer;
	private ComboViewer operatorComboViewer;
	private WritableList operatorModel;
	private IFilterClauseRenderer renderer;
	private List<IFilterClauseListener> listeners = 
		new ArrayList<IFilterClauseListener>();
	private ILogResource log;

	/**
	 * Constructor.
	 * @param parent the parent composite
	 * @param style the style flags
	 * @param log the log resource
	 */
	public FilterClauseComposite(Composite parent, int style, 
			ILogResource log) {
		super(parent, style);
		this.log = log;
		
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		fieldComboViewer = new ComboViewer(this, SWT.BORDER | SWT.READ_ONLY);
		WritableList model = new WritableList(log.getDialect().getFieldProvider().getAllFields(), 
				ALogEntryField.class);
		ViewerSupport.bind(fieldComboViewer, model, 
				PojoProperties.values(new String[] {"label"})); //$NON-NLS-1$
		fieldComboViewer.setComparator(new ViewerComparator());
		fieldComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				IStructuredSelection sel = (IStructuredSelection) e.getSelection();
				ALogEntryField<?, ?> fld = (ALogEntryField<?, ?>) sel.getFirstElement();
				updateOperatorCombo(fld);
				recreateRenderer(fld);
				renderer.validateInput(); // Revalidate
			}
		});
		
		operatorComboViewer = new ComboViewer(this, SWT.BORDER | SWT.READ_ONLY);
		operatorModel = WritableList.withElementType(Operator.class);
		ViewerSupport.bind(operatorComboViewer, operatorModel, 
				PojoProperties.values(new String[] {"label"})); //$NON-NLS-1$
		operatorComboViewer.setComparator(new ViewerComparator());
		
		removeButton = new Button(this, SWT.NONE);
		removeButton.setText(Messages.FilterClauseComposite_label_remove);
		removeButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				fireRemoveButtonPressed();
			}
		});
		
		// Init selection
		fieldComboViewer.setSelection(
				new StructuredSelection(fieldComboViewer.getElementAt(0)));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return renderer.setFocus();
	}

	private void recreateRenderer(ALogEntryField<?, ?> fld) {
		// Cleanup
		if (renderer != null) {
			renderer.dispose();
			renderer = null;
		}
		
		ILogEntryFieldVisitor visitor = new ILogEntryFieldVisitor() {

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
			 */
			@Override
			public void visit(DateLogEntryField fld) {
				renderer = new DefaultFilterClauseRenderer(fld);
				renderer.createControl(FilterClauseComposite.this);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
			 */
			@Override
			public void visit(LevelLogEntryField fld) {
				renderer = new LevelFilterClauseRenderer(fld);
				renderer.createControl(FilterClauseComposite.this);
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
			 */
			@Override
			public void visit(StringLogEntryField fld) {
				renderer = new DefaultFilterClauseRenderer(fld);
				renderer.createControl(FilterClauseComposite.this);
			}
		};
		fld.visit(visitor);
		
		// Add each listener to new renderer
		for (IFilterClauseListener listener : listeners) {
			renderer.addFilterClauseListener(listener);
		}
		// Move the new value Text before the remove button
		renderer.getControl().moveAbove(removeButton);
		
		layout();
	}

	/**
	 * Adds the given listener to the internal list of listeners.
	 * @param listener the listener to add
	 */
	public void addFilterClauseListener(IFilterClauseListener listener) {
		listeners.add(listener);
		renderer.addFilterClauseListener(listener);
	}

	/**
	 * Removes the given listener from the internal list of listeners.
	 * @param listener the listener to remove
	 */
	public void removeFilterClauseListener(IFilterClauseListener listener) {
		listeners.remove(listener);
		renderer.removeFilterClauseListener(listener);
	}

	private void fireRemoveButtonPressed() {
		FilterClauseEvent e = new FilterClauseEvent(this, false);
		for (IFilterClauseListener listener : listeners) {
			listener.removeButtonPressed(e);
		}
	}

	/**
	 * Returns whether this clause is valid.
	 * @return <code>true</code> if valid
	 */
	public boolean isValid() {
		return renderer.isValid();
	}

	/**
	 * Sets the value of this filter clause to the specified <code>String</String>.
	 * @param val the value to set
	 */
	public void setValue(String val) {
		Assert.isNotNull(val, "val"); //$NON-NLS-1$
		renderer.setValue(val);
	}

	/**
	 * Sets the operator of this filter clause to the specified value.
	 * @param op the operator to set
	 */
	public void setOperator(Operator op) {
		Assert.isNotNull(op, "op"); //$NON-NLS-1$
		operatorComboViewer.setSelection(new StructuredSelection(op));
	}

	/**
	 * Sets the field of this filter clause to the specified value.
	 * @param fld the field to set
	 */
	public void setField(ALogEntryField<?, ?> fld) {
		Assert.isNotNull(fld, "fld"); //$NON-NLS-1$
		fieldComboViewer.setSelection(new StructuredSelection(fld));
	}

	/**
	 * Returns the restriction representing this filter clause.
	 * @return the restriction
	 */
	public ARestriction<?> getRestriction() {
		IStructuredSelection sel = (IStructuredSelection) fieldComboViewer.getSelection();
		ALogEntryField<?, ?> fld = (ALogEntryField<?, ?>) sel.getFirstElement();
		sel = (IStructuredSelection) operatorComboViewer.getSelection();
		final Operator op = (Operator) sel.getFirstElement();
		final ARestriction<?>[] ret = new ARestriction<?>[1];
		// Use visitor pattern to call restriction factory
		fld.visit(new ILogEntryFieldVisitor() {

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
			 */
			@Override
			public void visit(StringLogEntryField fld) {
				ret[0] = CorePlugin.getDefault().getRestrictionFactory().newRestriction(
						fld, op, fld.fromInputValue(renderer.getValue(), log));
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
			 */
			@Override
			public void visit(LevelLogEntryField fld) {
				ret[0] = CorePlugin.getDefault().getRestrictionFactory().newRestriction(
						fld, op, fld.fromInputValue(renderer.getValue(), log));
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
			 */
			@Override
			public void visit(DateLogEntryField fld) {
				ret[0] = CorePlugin.getDefault().getRestrictionFactory().newRestriction(
						fld, op, fld.fromInputValue(renderer.getValue(), log));
			}
		});
		return ret[0];
	}

	private void updateOperatorCombo(ALogEntryField<?, ?> selectedField) {
		operatorModel.clear();
		operatorModel.addAll(
				CorePlugin.getDefault().getRestrictionFactory().getOperators(selectedField));
		// Init selection
		operatorComboViewer.setSelection(
				new StructuredSelection(operatorComboViewer.getElementAt(0)));
	}
}
