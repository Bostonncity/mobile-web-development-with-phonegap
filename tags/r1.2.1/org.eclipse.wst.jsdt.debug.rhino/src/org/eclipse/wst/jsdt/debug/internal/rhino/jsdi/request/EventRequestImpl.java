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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.request;

import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.MirrorImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;

/**
 * Rhino implementation of {@link EventRequest}
 * 
 * @since 1.0
 */
public class EventRequestImpl extends MirrorImpl implements EventRequest {

	private boolean deleted = false;
	protected boolean enabled = false;

	/**
	 * Constructor
	 * @param vm
	 */
	public EventRequestImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest#isEnabled()
	 */
	public synchronized boolean isEnabled() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest#setEnabled(boolean)
	 */
	public synchronized void setEnabled(boolean enabled) {
		checkDeleted();
		this.enabled = enabled;
	}

	/**
	 * If the delete flag is set throw an {@link IllegalStateException}
	 * @throws IllegalStateException
	 */
	protected void checkDeleted() throws IllegalStateException {
		if (deleted) {
			throw new IllegalStateException("deleted"); //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Mirror#virtualMachine()
	 */
	public VirtualMachine virtualMachine() {
		return vm;
	}

	/**
	 * delete the request by setting it disabled and setting the delet flag 
	 */
	public synchronized void delete() {
		setEnabled(false);
		deleted = true;
	}
}
