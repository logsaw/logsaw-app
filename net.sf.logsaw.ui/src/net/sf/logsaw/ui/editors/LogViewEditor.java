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
package net.sf.logsaw.ui.editors;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.ILogEntryFieldVisitor;
import net.sf.logsaw.core.field.Level;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.field.model.DateLogEntryField;
import net.sf.logsaw.core.field.model.LevelLogEntryField;
import net.sf.logsaw.core.field.model.StringLogEntryField;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictable;
import net.sf.logsaw.index.IQueryContext;
import net.sf.logsaw.index.IndexPlugin;
import net.sf.logsaw.index.ResultPage;
import net.sf.logsaw.ui.IHelpContexts;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;
import net.sf.logsaw.ui.viewers.LogEntryTableLabelProvider;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Philipp Nanz
 */
public class LogViewEditor extends EditorPart implements ILogViewEditor {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sf.logsaw.ui.editors.LogViewEditor"; //$NON-NLS-1$

	private TableViewer viewer;
	private TableViewerFocusCellManager focusCellManager;
	private LogEntryTableLabelProvider labelProvider;
	private Label resultLabel;
	private ResultPage currentPage;
	private LogViewEditorColumnConfiguration columnConfig;
	private List<IPageChangedListener> listeners = 
		new ArrayList<IPageChangedListener>();
	private int currentPageNumber;
	private int pageSize = 1000;
	private IEditorInput editorInput;
	private IQueryContext queryContext;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		root.setLayout(gridLayout);
		
		Composite top = new Composite(root, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		resultLabel = new Label(top, SWT.NONE);
		resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		Label label = new Label(root, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		viewer = new TableViewer(root, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		labelProvider = new LogEntryTableLabelProvider();
		labelProvider.setLog(getLogResource());
		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(new ArrayContentProvider());
		getSite().setSelectionProvider(viewer);
		
		// Enable table cell navigation
		focusCellManager = 
			new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return (event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) 
						|| (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) 
						|| ((event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) && (event.keyCode == SWT.CR)) 
						|| (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC);
			}
		};
		
		TableViewerEditor.create(viewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL 
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR 
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
		// Setup popup menu
		hookContextMenu();
		
		// Setup columns
		setColumnConfig(new LogViewEditorColumnConfiguration(getLogResource()));
		
		goToPage(1);
		
		// Enable dynamic help
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), 
				IHelpContexts.LOG_VIEWER);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// Other plug-ins can contribute their actions here
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#clearQueryContext()
	 */
	@Override
	public synchronized void clearQueryContext() {
		queryContext = null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#setColumnConfig(net.sf.logsaw.ui.editors.LogViewEditorColumnConfiguration)
	 */
	@Override
	public void setColumnConfig(LogViewEditorColumnConfiguration config) {
		Assert.isNotNull(config, "config"); //$NON-NLS-1$
		columnConfig = config;
		updateColumns(config.getFields(), config.getWidths());
	}

	private void updateColumns(List<ALogEntryField<?, ?>> newFields, int[] newWidths) {
		// Prevent flickering
		viewer.getTable().setRedraw(false);
		labelProvider.setFields(newFields.toArray(new ALogEntryField[newFields.size()]));
		// Dispose old columns
		TableColumn[] oldColumns = viewer.getTable().getColumns();
		for (int i = 0; i < oldColumns.length; i++) {
			oldColumns[i].dispose();
		}
		
		// Create icon column
		TableColumn col = new TableColumn(viewer.getTable(), SWT.NONE);
		col.setWidth(20);
		col.setResizable(false);
		
		int i = 0;
		for (ALogEntryField<?, ?> newField : newFields) {
			col = new TableColumn(viewer.getTable(), SWT.NONE);
			col.setText(newField.getLabel());
			// Set order mark
			if (newField.equals(getLogResource().getDialect().getFieldProvider().getTimestampField())) {
				viewer.getTable().setSortColumn(col);
				viewer.getTable().setSortDirection(SWT.UP);
			}
			// Set width
			int newWidth = newWidths[i++];
			if (newWidth > 0) {
				col.setWidth(newWidth);
			} else {
				col.setWidth(100);
			}
			col.addListener(SWT.Resize, new Listener() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
				 */
				@Override
				public void handleEvent(Event event) {
					saveColumnConfig();
				}
			});
		}
		viewer.refresh(true);
		viewer.getTable().setRedraw(true);
	}

	private void saveColumnConfig() {
		// Save column widths
		TableColumn[] columns = viewer.getTable().getColumns();
		int[] newWidths = new int[columns.length - 1];
		int i = 0;
		boolean first = true;
		for (TableColumn col : columns) {
			if (first) {
				// Ignore icon column
				first = false;
				continue;
			}
			newWidths[i++] = col.getWidth();
		}
		columnConfig.update(columnConfig.getFields(), newWidths);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#previousPage()
	 */
	@Override
	public void previousPage() {
		Assert.isNotNull(currentPage, "Current page must not be null"); //$NON-NLS-1$
		Assert.isTrue(isPreviousPageAllowed(), "Current page number must be greater than 1"); //$NON-NLS-1$
		goToPage(currentPageNumber - 1);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#nextPage()
	 */
	@Override
	public void nextPage() {
		Assert.isNotNull(currentPage, "Current page must not be null"); //$NON-NLS-1$
		Assert.isTrue(isNextPageAllowed(), "There must exist more items to display"); //$NON-NLS-1$
		goToPage(currentPageNumber + 1);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#isPreviousPageAllowed()
	 */
	@Override
	public boolean isPreviousPageAllowed() {
		return currentPageNumber > 1;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#isNextPageAllowed()
	 */
	@Override
	public boolean isNextPageAllowed() {
		return (currentPage != null) && (currentPage.getTotalHits() > (currentPageNumber * pageSize));
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#getFocusCellLogEntryField()
	 */
	@Override
	public ALogEntryField<?, ?> getFocusCellLogEntryField() {
		ViewerCell cell = focusCellManager.getFocusCell();
		if ((cell != null) && (cell.getColumnIndex() > 0)) {
			return columnConfig.getFields().get(cell.getColumnIndex() - 1);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#getFocusCellText()
	 */
	@Override
	public String getFocusCellText() {
		ALogEntryField<?, ?> fld = getFocusCellLogEntryField();
		final LogEntry entry = getSelectedLogEntry();
		if ((fld == null) || (entry == null)) {
			return ""; //$NON-NLS-1$
		}
		final String[] ret = new String[1];
		// Setup visitor
		ILogEntryFieldVisitor visitor = new ILogEntryFieldVisitor() {
			
			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.StringLogEntryField)
			 */
			@Override
			public void visit(StringLogEntryField fld) {
				String value = entry.get(fld);
				if (value != null) {
					ret[0] = fld.toInputValue(value, getLogResource());
				}
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.LevelLogEntryField)
			 */
			@Override
			public void visit(LevelLogEntryField fld) {
				Level value = entry.get(fld);
				if (value != null) {
					ret[0] = fld.toInputValue(value, getLogResource());
				}
			}

			/* (non-Javadoc)
			 * @see net.sf.logsaw.core.model.ILogEntryFieldVisitor#visit(net.sf.logsaw.core.model.DateLogEntryField)
			 */
			@Override
			public void visit(DateLogEntryField fld) {
				Date value = entry.get(fld);
				if (value != null) {
					ret[0] = fld.toInputValue(value, getLogResource());
				}
			}
		};
		fld.visit(visitor);
		if ((ret[0] == null) || (ret[0].length() == 0)) {
			return ""; //$NON-NLS-1$
		}
		return ret[0];
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#getSelectedLogEntry()
	 */
	@Override
	public LogEntry getSelectedLogEntry() {
		ViewerCell cell = focusCellManager.getFocusCell();
		if ((cell != null) && (cell.getColumnIndex() > 0)) {
			return (LogEntry) cell.getElement();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.parts.IRefreshablePart#refresh()
	 */
	@Override
	public void refresh() {
		goToPage(((Integer) getSelectedPage()).intValue());
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#goToPage(int)
	 */
	@Override
	public void goToPage(int pageNumber) {
		// Sanity checks
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		if (currentPage == null) {
			Assert.isTrue(pageNumber == 1, "pageNumber must initially be set to 1"); //$NON-NLS-1$
		} else {
			// Fallback to last page if page does not exist
			int pageCount = getPageCount();
			if (pageNumber > pageCount) {
				pageNumber = pageCount;
			}
		}
		// Obtain fresh query context - if necessary
		synchronized (this) {
			if (queryContext == null) {
				queryContext = IndexPlugin.getDefault().getIndexService().createQueryContext(
						getLogResource());	
			}
		}
		try {
			currentPage = IndexPlugin.getDefault().getIndexService().query(queryContext, 
					getRestrictable().getRestrictions(), (pageNumber - 1) * pageSize, pageSize);
			currentPageNumber = pageNumber;
			NumberFormat fmt = DecimalFormat.getInstance();
			String header = currentPage.getTotalHits() == 0 ? 
					Messages.LogViewEditor_pageHeader_empty : 
					NLS.bind(Messages.LogViewEditor_pageHeader, 
					new Object[] {fmt.format(currentPage.getOffset() + 1), 
						fmt.format(currentPage.getOffset() + currentPage.getItems().size()), 
						fmt.format(currentPage.getTotalHits())});
			if (!getRestrictable().getRestrictions().isEmpty()) {
				// Append suffix to show that filter is active
				header += Messages.LogViewEditor_pageHeader_filterSuffix;
			}
			resultLabel.setText(header);
			viewer.setInput(currentPage.getItems().toArray());
			firePageChanged();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, false);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#getPageCount()
	 */
	@Override
	public int getPageCount() {
		if (currentPage == null) {
			// Avoid NPE
			return 1;
		}
		return (int) Math.max(Math.ceil(currentPage.getTotalHits() / (double) pageSize), 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IPageChangeProvider#getSelectedPage()
	 */
	@Override
	public Object getSelectedPage() {
		return Integer.valueOf(currentPageNumber);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IPageChangeProvider#addPageChangedListener(org.eclipse.jface.dialogs.IPageChangedListener)
	 */
	@Override
	public void addPageChangedListener(IPageChangedListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IPageChangeProvider#removePageChangedListener(org.eclipse.jface.dialogs.IPageChangedListener)
	 */
	@Override
	public void removePageChangedListener(IPageChangedListener listener) {
		listeners.remove(listener);
	}

	private void firePageChanged() {
		PageChangedEvent e = new PageChangedEvent(this, getSelectedPage());
		for (IPageChangedListener listener : listeners) {
			listener.pageChanged(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// n/a
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// n/a
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setPartName(input.getName());
		setSite(site);
		setInput(input);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		editorInput = input;
		super.setInput(input);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public IEditorInput getEditorInput() {
		// Return a fresh instance of IEditorInput
		return (IEditorInput) getLogResource().getAdapter(IEditorInput.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (queryContext != null) {
			try {
				queryContext.close();
			} catch (IOException e) {
				// Log error
				StatusManager.getManager().handle(
						new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
						e.getLocalizedMessage(), e), StatusManager.LOG);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#getLogResource()
	 */
	@Override
	public ILogResource getLogResource() {
		return (ILogResource) editorInput.getAdapter(ILogResource.class);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.ui.editors.ILogViewEditor#getRestrictable()
	 */
	@Override
	public IRestrictable getRestrictable() {
		return (IRestrictable) editorInput.getAdapter(IRestrictable.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
