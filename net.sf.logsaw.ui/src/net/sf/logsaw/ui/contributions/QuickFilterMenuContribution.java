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
package net.sf.logsaw.ui.contributions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.field.ALogEntryField;
import net.sf.logsaw.core.query.Operator;
import net.sf.logsaw.ui.editors.ILogViewEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * @author Philipp Nanz
 */
public class QuickFilterMenuContribution extends CompoundContributionItem {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		// Get the view
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		ILogViewEditor editor = 
			(ILogViewEditor) page.getActiveEditor().getAdapter(ILogViewEditor.class);
		if (editor != null) {
			return doGetContributionItems(editor);
		}
		return new IContributionItem[0];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IContributionItem[] doGetContributionItems(ILogViewEditor editor) {
		List<CommandContributionItem> ret = new ArrayList<CommandContributionItem>();
		ALogEntryField<?, ?> fld = editor.getFocusCellLogEntryField();
		List<Operator> ops = new ArrayList<Operator>(
				CorePlugin.getDefault().getRestrictionFactory().getOperators(fld));
		Collections.sort(ops, new Comparator<Operator>() {

			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Operator o1, Operator o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		
		for (Operator op : ops) {
			CommandContributionItemParameter param = 
				new CommandContributionItemParameter(
						PlatformUI.getWorkbench(), null, 
						"net.sf.logsaw.ui.commands.ShowFilterSettingsCommand",  //$NON-NLS-1$
						CommandContributionItem.STYLE_PUSH);
			param.label = op.getLabel();
			Map map = new HashMap();
			map.put("net.sf.logsaw.ui.commands.ShowFilterSettingsCommand.newField", fld.getKey()); //$NON-NLS-1$
			map.put("net.sf.logsaw.ui.commands.ShowFilterSettingsCommand.newOperator", Integer.toString(op.getId())); //$NON-NLS-1$
			map.put("net.sf.logsaw.ui.commands.ShowFilterSettingsCommand.newValue", editor.getFocusCellText()); //$NON-NLS-1$
			param.parameters = map;
			ret.add(new CommandContributionItem(param));
		}
		
	    return ret.toArray(new IContributionItem[ret.size()]);
	}
}
