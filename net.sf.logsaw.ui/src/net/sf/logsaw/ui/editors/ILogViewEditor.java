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

import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.field.LogEntry;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictable;
import net.sf.logsaw.ui.parts.IRefreshablePart;

import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.ui.IEditorPart;

/**
 * This interface defines the methods provided by a log view editor.
 */
public interface ILogViewEditor extends IRefreshablePart, IEditorPart, IPageChangeProvider {

	/**
	 * Clears the query context.
	 */
	void clearQueryContext();

	/**
	 * Sets the column configuration to use for this editor.
	 * @param config the new column configuration
	 */
	void setColumnConfig(LogViewEditorColumnConfiguration config);

	/**
	 * Navigates to the previous page.
	 */
	void previousPage();

	/**
	 * Navigates to the next page.
	 */
	void nextPage();

	/**
	 * Returns <code>true</code> if previous page exists.
	 * @return <code>true</code> if previous page exists
	 */
	boolean isPreviousPageAllowed();

	/**
	 * Returns <code>true</code> if next page exists.
	 * @return <code>true</code> if next page exists
	 */
	boolean isNextPageAllowed();

	/**
	 * Returns the log entry field of the focused cell or <code>null</code>.
	 * @return the log entry field of the focused cell or <code>null</code>
	 */
	ALogEntryField<?, ?> getFocusCellLogEntryField();
	
	/**
	 * Returns the text of the focused cell.
	 * @return the text of the focused cell; not <code>null</code>
	 */
	String getFocusCellText();

	/**
	 * Returns the log entry backing the selected row or <code>null</code>.
	 * @return the log entry backing the selected row or <code>null</code>
	 */
	LogEntry getSelectedLogEntry();

	/**
	 * Navigates to the page with the given number.
	 * @param pageNumber the number of the page to navigate to
	 */
	void goToPage(int pageNumber);

	/**
	 * Returns the page count.
	 * @return the number of pages
	 */
	int getPageCount();

	/**
	 * Returns the log resource.
	 * @return the log resource
	 */
	ILogResource getLogResource();

	/**
	 * Returns the restrictable.
	 * @return the restrictable
	 */
	IRestrictable getRestrictable();
}
