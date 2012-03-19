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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.event;

import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.Event;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;

/**
 * Rhino implementation of {@link Event}
 * 
 * @since 1.0
 */
public class EventImpl implements Event {

	private final VirtualMachineImpl vm;
	private final EventRequest request;

	/**
	 * Constructor
	 * @param vm
	 * @param request
	 */
	public EventImpl(VirtualMachineImpl vm, EventRequest request) {
		this.vm = vm;
		this.request = request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.event.Event#request()
	 */
	public EventRequest request() {
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Mirror#virtualMachine()
	 */
	public VirtualMachine virtualMachine() {
		return vm;
	}
}