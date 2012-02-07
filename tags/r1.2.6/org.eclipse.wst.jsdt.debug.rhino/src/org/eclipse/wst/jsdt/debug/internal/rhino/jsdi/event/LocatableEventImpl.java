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

import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.LocatableEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;

/**
 * Rhino implementation of {@link LocatableEvent}
 * 
 * @since 1.0
 */
public class LocatableEventImpl extends EventImpl implements LocatableEvent {

	protected ThreadReference thread = null;
	protected Location location = null;
	
	/**
	 * Constructor
	 * @param vm
	 * @param request
	 */
	public LocatableEventImpl(VirtualMachineImpl vm, ThreadReference thread, Location location, EventRequest request) {
		super(vm, request);
		this.thread = thread;
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Locatable#location()
	 */
	public Location location() {
		return this.location;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.event.LocatableEvent#thread()
	 */
	public ThreadReference thread() {
		return this.thread;
	}
}