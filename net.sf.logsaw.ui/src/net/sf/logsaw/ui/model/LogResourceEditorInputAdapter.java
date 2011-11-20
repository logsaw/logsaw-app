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
package net.sf.logsaw.ui.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.logsaw.core.CorePlugin;
import net.sf.logsaw.core.config.model.StringConfigOption;
import net.sf.logsaw.core.logresource.ILogResource;
import net.sf.logsaw.core.query.IRestrictable;
import net.sf.logsaw.core.query.IRestrictionVisitor;
import net.sf.logsaw.core.query.model.DateRestriction;
import net.sf.logsaw.core.query.model.LevelRestriction;
import net.sf.logsaw.core.query.model.StringRestriction;
import net.sf.logsaw.core.query.support.ARestriction;
import net.sf.logsaw.ui.Messages;
import net.sf.logsaw.ui.UIPlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @author Philipp Nanz
 */
public class LogResourceEditorInputAdapter implements IEditorInput, IRestrictable {
	
	private static final String ELEM_ROOT = "filter"; //$NON-NLS-1$
	private static final String ELEM_CLAUSE = "clause"; //$NON-NLS-1$
	private static final String ATTRIB_FIELD = "field"; //$NON-NLS-1$
	private static final String ATTRIB_OPERATOR = "operator"; //$NON-NLS-1$
	private static final StringConfigOption OPTION_FILTER = 
		new StringConfigOption("filter", "Filter", false); //$NON-NLS-1$ //$NON-NLS-2$

	private ILogResource log;
	private List<ARestriction<?>> restrictions;

	/**
	 * Constructor.
	 * @param log the log resource
	 */
	public LogResourceEditorInputAdapter(ILogResource log) {
		Assert.isNotNull(log, "log must not be null"); //$NON-NLS-1$
		this.log = log;
		try {
			init();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, true);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictable#getRestrictions()
	 */
	@Override
	public List<ARestriction<?>> getRestrictions() {
		return new ArrayList<ARestriction<?>>(restrictions);
	}

	/* (non-Javadoc)
	 * @see net.sf.logsaw.core.query.IRestrictable#setRestrictions(java.util.List)
	 */
	@Override
	public void setRestrictions(List<ARestriction<?>> restrictions) {
		Assert.isNotNull(restrictions, "restrictions"); //$NON-NLS-1$
		this.restrictions = restrictions;
		try {
			save();
		} catch (CoreException e) {
			// Log and show error
			UIPlugin.logAndShowError(e, true);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		IWorkbenchAdapter adapter = (IWorkbenchAdapter) log.getAdapter(IWorkbenchAdapter.class);
		if (adapter != null) {
			return adapter.getImageDescriptor(null);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return log.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class clazz) {
		if (IEditorInput.class.equals(clazz)) {
			return this;
		}
		if (IRestrictable.class.equals(clazz)) {
			return this;
		}
		return log.getAdapter(clazz);
	}

	private void save() throws CoreException {
		XMLMemento rootElem = XMLMemento.createWriteRoot(ELEM_ROOT);
		for (ARestriction<?> rest : restrictions) {
			final IMemento clauseElem = rootElem.createChild(ELEM_CLAUSE);
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
		try {
			StringWriter w = new StringWriter();
			// Save to byte buffer first
			rootElem.save(w);
			// Update log resource
			log.configure(OPTION_FILTER, w.toString());
		} catch (IOException e) {
			// Unexpected exception; wrap with CoreException
			throw new CoreException(new Status(IStatus.ERROR, UIPlugin.PLUGIN_ID, 
					NLS.bind(Messages.LogViewEditorColumnConfiguration_error_failedToSave, 
							new Object[] {e.getLocalizedMessage()}), e));
		}
	}

	private void init() throws CoreException {
		String str = log.getConfigValue(OPTION_FILTER);
		restrictions = new ArrayList<ARestriction<?>>();
		if (str != null) {
			// Read memento
			IMemento rootElem = XMLMemento.createReadRoot(new StringReader(str));
			IMemento[] clauses = rootElem.getChildren(ELEM_CLAUSE);
			for (final IMemento clauseElem : clauses) {
				restrictions.add(CorePlugin.getDefault().createRestriction(
						log, clauseElem.getString(ATTRIB_FIELD), 
						clauseElem.getInteger(ATTRIB_OPERATOR), 
						clauseElem.getTextData()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogResourceEditorInputAdapter other = (LogResourceEditorInputAdapter) obj;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}
}
