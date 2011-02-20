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

import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;

/**
 * Description of a JSDI request for a <code>debugger;</code> statement.<br>
 * <br> 
 * A <code>debugger;</code> statement request can be filtered by:
 * <ul>
 * <li>A {@link ThreadReference}</li>
 * </ul>
 * 
 * @see EventRequest
 * @see ThreadReference
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface DebuggerStatementRequest extends EventRequest {

	/**
	 * Adds the given {@link ThreadReference} as a filter to the request.
	 * The {@link ThreadReference} is used to prune matching locations for the request.
	 * 
	 * @param thread a {@link ThreadReference} to add as a filter, <code>null</code> is not accepted
	 */
	public void addThreadFilter(ThreadReference thread);
}
