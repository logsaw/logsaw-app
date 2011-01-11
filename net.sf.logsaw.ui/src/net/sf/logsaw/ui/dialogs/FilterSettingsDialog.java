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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictionFactory;
import net.sf.logsaw.core.query.IRestrictionVisitor;
import net.sf.logsaw.core.query.Operator;
import net.sf.logsaw.core.query.Operators;
import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;
import net.sf.logsaw.core.query.support.ARestriction;
import net.sf.logsaw.ui.IHelpContexts;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.model.NamedFilter;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;

/**
 * @author Philipp Nanz
 */
public class FilterSettingsDialog extends TitleAreaDialog {

	private static final String ELEM_ROOT = "namedFilters"; //$NON-NLS-1$
	private static final String ELEM_FILTER = "filter"; //$NON-NLS-1$
	private static final String ATTRIB_NAME = "name"; //$NON-NLS-1$
	private static final String ELEM_CLAUSE = "clause"; //$NON-NLS-1$
	private static final String ATTRIB_FIELD = "field"; //$NON-NLS-1$
	private static final String ATTRIB_OPERATOR = "operator"; //$NON-NLS-1$
	private static final StringConfigOption OPTION_NAMED_FILTERS = 
		new StringConfigOption("namedFilters", "Named Filters", false); //$NON-NLS-1$ //$NON-NLS-2$

	private boolean focusLast;
	private List<FilterClauseComposite> clauses = new ArrayList<FilterClauseComposite>();
	private List<ARestriction<?>> restrictions;
	private Set<NamedFilter> namedFilters;
	private ILogResource log;
	private ComboViewer namedFiltersViewer;
	private Button saveButton;
	private Button deleteButton;
	private Button clearButton;
	private ScrolledComposite scrolledComposite;
	private Composite buttonComposite;
	private Composite rootComposite;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param log the log resource
	 * @param restrictions the restrictions to restore
	 * @param focusLast whether to focus the last entry
	 */
	public FilterSettingsDialog(Shell parentShell, ILogResource log, 
			List<ARestriction<?>> restrictions, boolean focusLast) {
		super(parentShell);
		Assert.isNotNull(log, "log"); //$NON-NLS-1$
		Assert.isNotNull(restrictions, "restrictions"); //$NON-NLS-1$
		this.focusLast = focusLast;
		this.log = log;
		this.restrictions = restrictions;
		try {
			init();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, true);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);
		
		Composite namedFilterComposite = new Composite(parentComposite, SWT.NONE);
		namedFilterComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		namedFilterComposite.setLayout(new GridLayout());
		Group group = new Group(namedFilterComposite, SWT.NONE);
		group.setText(Messages.FilterSettingsDialog_label_saveLoad);
		group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		group.setLayout(new GridLayout(4, false));
		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.FilterSettingsDialog_label_filterName);
		namedFiltersViewer = new ComboViewer(group, SWT.BORDER);
		namedFiltersViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		namedFiltersViewer.setContentProvider(new ArrayContentProvider());
		namedFiltersViewer.setLabelProvider(new LabelProvider() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				NamedFilter flt = (NamedFilter) element;
				return flt.getName();
			}
		});
		namedFiltersViewer.setInput(namedFilters.toArray());
		namedFiltersViewer.setComparator(new ViewerComparator());
		namedFiltersViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				IStructuredSelection sel = (IStructuredSelection) e.getSelection();
				NamedFilter flt = (NamedFilter) sel.getFirstElement();
				if (flt != null) {
					// Delete button is only enabled when a fresh filter is selected
					deleteButton.setEnabled(true);
					clearContents();
					// Set restrictions to load
					restrictions = new ArrayList<ARestriction<?>>(flt.getRestrictions());
					updateContents(false);
				}
			}
		});
		namedFiltersViewer.getCombo().addModifyListener(new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				updateSaveButton();
				// Delete button is being disabled as soon as the text is modified
				deleteButton.setEnabled(false);
				try {
					save();
				} catch (CoreException ce) {
					// Log and show error
					UIPlugin.logAndShowError(ce, true);
				}
			}
		});
		saveButton = new Button(group, SWT.NONE);
		saveButton.setText(Messages.FilterSettingsDialog_label_save);
		saveButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				NamedFilter flt = doFindNamedFilter();
				if (flt != null) {
					if (MessageDialog.openQuestion(getShell(), 
							Messages.FilterSettingsDialog_confirm_title, 
							Messages.FilterSettingsDialog_confirm_question)) {
						// Remove old
						namedFilters.remove(flt);
					} else {
						// Abort saving
						return;
					}
				}
				flt = new NamedFilter();
				flt.setName(namedFiltersViewer.getCombo().getText().trim());
				flt.setRestrictions(doGetRestrictions());
				namedFilters.add(flt);
				namedFiltersViewer.setInput(namedFilters.toArray());
				try {
					save();
				} catch (CoreException ce) {
					// Log and show error
					UIPlugin.logAndShowError(ce, true);
				}
			}
		});
		deleteButton = new Button(group, SWT.NONE);
		deleteButton.setText(Messages.FilterSettingsDialog_label_delete);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				NamedFilter flt = doFindNamedFilter();
				namedFilters.remove(flt);
				namedFiltersViewer.setInput(namedFilters.toArray());
			}
		});
		deleteButton.setEnabled(false);
		
		label = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		scrolledComposite = new ScrolledComposite(parentComposite, SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 250;
		gridData.widthHint = 600;
		scrolledComposite.setLayoutData(gridData);
		scrolledComposite.setExpandHorizontal(true);
		rootComposite = new Composite(scrolledComposite, SWT.NONE);
		rootComposite.setLayout(new GridLayout());
		
		label = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		setTitle(Messages.FilterSettingsDialog_title);
		setMessage(Messages.FilterSettingsDialog_message);
		
		updateContents(true);
		
		// Enable help button
		setHelpAvailable(true);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), 
				IHelpContexts.FILTER_SETTINGS_DIALOG);
		
		return rootComposite;
	}

	private <T> String getInputValue(ARestriction<T> restriction) {
		return restriction.getField().toInputValue(restriction.getValue(), log);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		// Trigger re-evaluation
		updateOKButton();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		restrictions = doGetRestrictions();
		super.okPressed();
	}

	/**
	 * @return the restrictions
	 */
	public List<ARestriction<?>> getRestrictions() {
		return restrictions;
	}

	private NamedFilter doFindNamedFilter() {
		String name = namedFiltersViewer.getCombo().getText().trim();
		Iterator<NamedFilter> it = namedFilters.iterator();
		while (it.hasNext()) {
			NamedFilter flt = it.next();
			if (name.equals(flt.getName())) {
				return flt;
			}
		}
		return null;
	}

	private List<ARestriction<?>> doGetRestrictions() {
		List<ARestriction<?>> ret = new ArrayList<ARestriction<?>>();
		for (FilterClauseComposite clause : clauses) {
			ret.add(clause.getRestriction());
		}
		return ret;
	}

	private void refreshScrolledComposite() {
		// Necessary for scrolled composite to work
		scrolledComposite.setContent(rootComposite);
		rootComposite.setSize(rootComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private FilterClauseComposite createFilterClauseRow() {
		final FilterClauseComposite newClause = 
			new FilterClauseComposite(rootComposite, SWT.NONE, log);
		newClause.addFilterClauseListener(new IFilterClauseListener() {
			
			@Override
			public void removeButtonPressed(FilterClauseEvent e) {
				clauses.remove(newClause);
				newClause.dispose();
				refreshScrolledComposite();
				updateSaveButton();
				updateClearButton();
				updateOKButton();
				setDefaultButton();
			}
			
			@Override
			public void inputChanged(FilterClauseEvent e) {
				updateSaveButton();
				updateOKButton();
			}
		});
		clauses.add(newClause);
		return newClause;
	}

	private void updateClearButton() {
		clearButton.setEnabled(!clauses.isEmpty());
	}

	private void updateSaveButton() {
		boolean allValid = true;
		for (FilterClauseComposite clause : clauses) {
			if (!clause.isValid()) {
				allValid = false;
				break;
			}
		}
		saveButton.setEnabled(allValid && !doGetRestrictions().isEmpty() && 
				(namedFiltersViewer.getCombo().getText().trim().length() > 0));
	}

	private void updateOKButton() {
		boolean allValid = true;
		for (FilterClauseComposite clause : clauses) {
			if (!clause.isValid()) {
				allValid = false;
				break;
			}
		}
		if (getButton(OK) != null) {
			getButton(OK).setEnabled(allValid);
		}
	}

	private void setDefaultButton() {
		getShell().setDefaultButton(getButton(OK));
	}

	private void createAddClearButtonRow() {
		buttonComposite = new Composite(rootComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		
		final Button addButton = new Button(buttonComposite, SWT.NONE);
		addButton.setText(Messages.FilterSettingsDialog_label_add);
		addButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Dispose button row
				buttonComposite.dispose();
				// Create new filter row, then new button row
				createFilterClauseRow();
				createAddClearButtonRow();
				refreshScrolledComposite();
				updateSaveButton();
				updateClearButton();
				updateOKButton();
				setDefaultButton();
			}
		});
		
		clearButton = new Button(buttonComposite, SWT.NONE);
		clearButton.setText(Messages.FilterSettingsDialog_label_clear);
		clearButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearContents();
				updateContents(false);
			}
		});
	}

	private void clearContents() {
		buttonComposite.dispose();
		for (FilterClauseComposite clause : clauses) {
			clause.dispose();
		}
		clauses.clear();
		restrictions.clear();
	}

	private void updateContents(boolean addRowIfEmpty) {
		if (restrictions.isEmpty()) {
			if (addRowIfEmpty) {
				// Create initial row if requested
				createFilterClauseRow();
			}
			createAddClearButtonRow();
			refreshScrolledComposite();
		} else {
			// Create empty filter clauses
			for (int i = 0; i < restrictions.size(); i++) {
				createFilterClauseRow();
			}
			createAddClearButtonRow();
			refreshScrolledComposite();
			// Initialize values
			int i = 0;
			for (ARestriction<?> restriction : restrictions) {
				clauses.get(i).setField(restriction.getField());
				clauses.get(i).setOperator(restriction.getOperator());
				clauses.get(i).setValue(getInputValue(restriction));
				i++;
			}
			if (focusLast) {
				// This was added using Quick Filter
				clauses.get(i - 1).setFocus();
				scrolledComposite.getVerticalBar().setSelection(
						scrolledComposite.getVerticalBar().getMaximum());
				focusLast = false;
			}
		}
		updateSaveButton();
		updateClearButton();
		updateOKButton();
		setDefaultButton();
	}

	private void save() throws CoreException {
		XMLMemento rootElem = XMLMemento.createWriteRoot(ELEM_ROOT);
		for (NamedFilter flt : namedFilters) {
			final IMemento filterElem = rootElem.createChild(ELEM_FILTER);
			filterElem.putString(ATTRIB_NAME, flt.getName());
			for (ARestriction<?> rest : flt.getRestrictions()) {
				final IMemento clauseElem = filterElem.createChild(ELEM_CLAUSE);
				clauseElem.putString(ATTRIB_FIELD, rest.getField().getKey());
				clauseElem.putInteger(ATTRIB_OPERATOR, rest.getOperator().getId());
				rest.visit(new IRestrictionVisitor() {
					/* (non-Javadoc)
					 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.LevelRestriction)
					 */
					@Override
					public void visit(LevelRestriction restriction) {
						clauseElem.putTextData(restriction.getField().toInputValue(restriction.getValue(), log));
					}

					/* (non-Javadoc)
					 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.StringRestriction)
					 */
					@Override
					public void visit(StringRestriction restriction) {
						clauseElem.putTextData(restriction.getField().toInputValue(restriction.getValue(), log));
					}

					/* (non-Javadoc)
					 * @see net.sf.logsaw.core.query.IRestrictionVisitor#visit(net.sf.logsaw.core.query.DateRestriction)
					 */
					@Override
					public void visit(DateRestriction restriction) {
						clauseElem.putTextData(restriction.getField().toInputValue(restriction.getValue(), log));
					}
				});
			}	
		}
		try {
			StringWriter w = new StringWriter();
			// Save to byte buffer first
			rootElem.save(w);
			// Update log resource
			log.configure(OPTION_NAMED_FILTERS, w.toString());
		} catch (IOException e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LogViewEditorColumnConfiguration_error_failedToSave, 
							new Object[] {e.getLocalizedMessage()}), e));
		}
	}

	private void init() throws CoreException {
		String str = log.getConfigValue(OPTION_NAMED_FILTERS);
		namedFilters = new HashSet<NamedFilter>();
		if (str != null) {
			// Read memento
			IMemento rootElem = XMLMemento.createReadRoot(new StringReader(str));
			IMemento[] filters = rootElem.getChildren(ELEM_FILTER);
			for (final IMemento filterElem : filters) {
				String name = filterElem.getString(ATTRIB_NAME);
				final List<ARestriction<?>> restrictions = new ArrayList<ARestriction<?>>();
				IMemento[] clauses = filterElem.getChildren(ELEM_CLAUSE);
				for (final IMemento clauseElem : clauses) {
					ALogEntryField<?, ?> fld = log.getDialect().getFieldProvider().findField(
							clauseElem.getString(ATTRIB_FIELD));
					final Operator op = Operators.getOperator(clauseElem.getInteger(ATTRIB_OPERATOR));
					final IRestrictionFactory rf = CorePlugin.getDefault().getRestrictionFactory();
					fld.visit(new ILogEntryFieldVisitor() {
						/* (non-Javadoc)
						 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
						 */
						@Override
						public void visit(StringLogEntryField fld) {
							String val = fld.fromInputValue(clauseElem.getTextData(), log);
							restrictions.add(rf.newRestriction(fld, op, val));
						}

						/* (non-Javadoc)
						 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
						 */
						@Override
						public void visit(LevelLogEntryField fld) {
							Level val = fld.fromInputValue(clauseElem.getTextData(), log);
							restrictions.add(rf.newRestriction(fld, op, val));
						}

						/* (non-Javadoc)
						 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
						 */
						@Override
						public void visit(DateLogEntryField fld) {
							Date val = fld.fromInputValue(clauseElem.getTextData(), log);
							restrictions.add(rf.newRestriction(fld, op, val));
						}
					});
				}
				NamedFilter flt = new NamedFilter();
				flt.setName(name);
				flt.setRestrictions(restrictions);
				namedFilters.add(flt);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getShellStyle()
	 */
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.FilterSettingsDialog_title);
	}
}
