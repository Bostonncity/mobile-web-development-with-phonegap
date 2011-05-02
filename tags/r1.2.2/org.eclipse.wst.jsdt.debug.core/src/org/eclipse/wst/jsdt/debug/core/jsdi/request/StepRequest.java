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
 * Description of a JSDI request for a step operation.
 * 
 * @see EventRequest
 * @see ThreadReference
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface StepRequest extends EventRequest {

	/**
	 * Kind for a step into request
	 */
	public static final int STEP_INTO = 1;
	/**
	 * Kind for a step over request
	 */
	public static final int STEP_OVER = 2;
	/**
	 * Kind for a step out request
	 */
	public static final int STEP_OUT = 3;

	/**
	 * Returns the kind of the {@link StepRequest}
	 * @see #STEP_INTO
	 * @see #STEP_OUT
	 * @see #STEP_OVER
	 * 
	 * @return the kind of the {@link StepRequest}
	 */
	public int step();

	/**
	 * The underlying {@link ThreadReference} this {@link StepRequest} is for.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the underlying {@link ThreadReference} never <code>null</code>
	 */
	public ThreadReference thread();
}
