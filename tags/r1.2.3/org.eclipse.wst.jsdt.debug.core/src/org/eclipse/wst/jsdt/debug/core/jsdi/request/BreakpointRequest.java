/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.core.jsdi.request;

import org.eclipse.wst.jsdt.debug.core.jsdi.Locatable;
import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;

/**
 * Description of a JSDI request for breakpoints.<br>
 * <br> 
 * A breakpoint request can be filtered by:
 * <ul>
 * <li>A {@link ThreadReference}</li>
 * <li>A {@link String} condition</li>
 * <li>A hit count</li>
 * </ul>
 * Or any combination of the above.
 * 
 * @see EventRequest
 * @see Locatable
 * @see ThreadReference
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface BreakpointRequest extends EventRequest, Locatable {

	/**
	 * Adds the given {@link ThreadReference} as a filter to the request.
	 * The {@link ThreadReference} is used to prune matching locations for the request.
	 * 
	 * @param thread a {@link ThreadReference} to add as a filter, <code>null</code> is not accepted
	 */
	public void addThreadFilter(ThreadReference thread);

	/**
	 * Adds the given condition filter to the request. 
	 * The condition is used to prune matching locations for the request.
	 * 
	 * @param condition the new condition to add as a filter, <code>null</code> is not accepted
	 */
	public void addConditionFilter(String condition);

	/**
	 * Adds the given hit count filter to the request.
	 * 
	 * @param hitcount the new hit count filter to set
	 */
	public void addHitCountFilter(int hitcount);
}
