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
package net.sf.logsaw.core.query;

import java.util.List;

import net.sf.logsaw.core.query.support.ARestriction;

/**
 * This interface defines the contract for an object onto which restrictions 
 * can be applied.
 * 
 * @author Philipp Nanz
 */
public interface IRestrictable {

	/**
	 * @return the restrictions
	 */
	List<ARestriction<?>> getRestrictions();

	/**
	 * @param restrictions the restrictions to set
	 */
	void setRestrictions(List<ARestriction<?>> restrictions);
}
