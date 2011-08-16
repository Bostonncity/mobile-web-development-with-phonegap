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
package org.eclipse.wst.jsdt.debug.core.jsdi;

import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;

/**
 * Abstract representation of an object that can be mirrored in a {@link VirtualMachine}.
 * 
 * @see Event
 * @see EventRequest
 * @see EventSet
 * @see Property
 * @see ScriptReference
 * @see StackFrame
 * @see ThreadReference
 * @see Value
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface Mirror {

	/**
	 * Returns the handle to the {@link VirtualMachine} that this mirror object was spawned from
	 * 
	 * @return the {@link VirtualMachine} handle that this mirror was spawned from
	 */
	public VirtualMachine virtualMachine();
}
