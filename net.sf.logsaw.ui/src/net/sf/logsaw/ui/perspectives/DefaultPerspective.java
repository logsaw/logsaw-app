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
package net.sf.logsaw.ui.perspectives;

import net.sf.logsaw.ui.views.LogResourcesView;
import net.sf.logsaw.ui.views.MessageFieldView;
import net.sf.logsaw.ui.views.StacktraceFieldView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Philipp Nanz
 */
public class DefaultPerspective implements IPerspectiveFactory {

	public static final String ID = "net.sf.logsaw.ui.perspectives.DefaultPerspective"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.addShowViewShortcut(LogResourcesView.ID);
		
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.33f, editorArea); //$NON-NLS-1$
		IFolderLayout rightBottom = layout.createFolder("rightBottom", IPageLayout.BOTTOM, 0.66f, editorArea); //$NON-NLS-1$ 
		
		left.addView(LogResourcesView.ID);
		rightBottom.addView(MessageFieldView.ID);
		rightBottom.addView(StacktraceFieldView.ID);
		rightBottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
	}

}
