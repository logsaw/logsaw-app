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
package net.sf.logsaw.ui.propertyPages;

import java.util.List;

import net.sf.logsaw.core.config.IConfigOption;
import net.sf.logsaw.core.config.IConfigOptionVisitor;
import net.sf.logsaw.core.config.IConfigurableObject;
import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.index.IndexPlugin;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author Philipp Nanz
 */
public class ResourcePropertyPage extends PropertyPage {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		// No default and apply button
		noDefaultAndApplyButton();
		
		final Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(2, false));
		
		ILogResource log = (ILogResource) getElement().getAdapter(ILogResource.class);
		
		Label label = new Label(root, SWT.NONE);
		label.setText(Messages.ResourcePropertyPage_label_type);
		
		Text text = new Text(root, SWT.SINGLE);
		text.setBackground(root.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		text.setText(log.getFactory().getName());
		
		label = new Label(root, SWT.NONE);
		label.setText(Messages.ResourcePropertyPage_label_indexSize);
		
		text = new Text(root, SWT.SINGLE);
		text.setBackground(root.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		text.setText(IndexPlugin.getDefault().getIndexService().size(log));
		
		label = new Label(root, SWT.NONE);
		label.setText(Messages.ResourcePropertyPage_label_identifier);
		
		text = new Text(root, SWT.SINGLE);
		text.setBackground(root.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		text.setText(log.getPK());
		
		// Spacer
		new Label(root, SWT.NONE);
		
		final IConfigurableObject co = (IConfigurableObject) log.getAdapter(IConfigurableObject.class);
		if (co != null) {
			List<IConfigOption<?>> options = co.getAllConfigOptions();
			if (!options.isEmpty()) {
				final Group group = new Group(root, SWT.NONE);
				group.setText(Messages.ResourcePropertyPage_label_config);
				GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
				gridData.horizontalSpan = 2;
				group.setLayoutData(gridData);
				group.setLayout(new GridLayout(2, false));
				
				for (IConfigOption<?> option : options) {
					if (!option.isVisible()) {
						// Skip
						continue;
					}
					try {
						option.visit(new IConfigOptionVisitor() {
							/* (non-Javadoc)
							 * @see net.sf.logsaw.core.config.IConfigOptionVisitor#visit(net.sf.logsaw.core.config.StringConfigOption, java.lang.String)
							 */
							@Override
							public void visit(StringConfigOption opt, String value)
									throws CoreException {
								Label label = new Label(group, SWT.NONE);
								label.setText(opt.getLabel() + ":"); //$NON-NLS-1$
								
								Text text = new Text(group, SWT.SINGLE);
								text.setBackground(group.getDisplay().getSystemColor(
										SWT.COLOR_WIDGET_BACKGROUND));
								text.setText(co.getConfigValue(opt));
							}
						}, null);
					} catch (CoreException e) {
						// Log and show error
						UIPlugin.logAndShowError(e, true);
					}
				}	
			}
		}
		
		return root;
	}
}
