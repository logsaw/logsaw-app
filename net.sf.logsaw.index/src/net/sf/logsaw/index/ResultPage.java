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
package net.sf.logsaw.index;

import java.util.LinkedList;
import java.util.List;

import net.sf.logsaw.core.field.LogEntry;

/**
 * This class defines the result page for an index query.
 * 
 * @author Philipp Nanz
 */
public class ResultPage {

	private List<LogEntry> items;
	private int offset;
	private int totalHits;

	/**
	 * Constructor.
	 */
	public ResultPage() {
		items = new LinkedList<LogEntry>();
	}

	/**
	 * Constructor.
	 * @param items the items
	 * @param offset the offset of this page
	 * @param totalHits the number of total hits for the query
	 */
	public ResultPage(List<LogEntry> items, int offset, int totalHits) {
		this.items = items;
		this.offset = offset;
		this.totalHits = totalHits;
	}

	/**
	 * @return the items
	 */
	public List<LogEntry> getItems() {
		return items;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the totalHits
	 */
	public int getTotalHits() {
		return totalHits;
	}
}
