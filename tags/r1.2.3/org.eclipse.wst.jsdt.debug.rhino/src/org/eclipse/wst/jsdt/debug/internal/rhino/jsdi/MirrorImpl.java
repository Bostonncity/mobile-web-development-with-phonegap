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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi;

import org.eclipse.wst.jsdt.debug.core.jsdi.Mirror;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;

/**
 * Rhino implementation of {@link Mirror}
 * 
 * @since 1.0
 */
public class MirrorImpl implements Mirror {

	protected final VirtualMachineImpl vm;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param description
	 */
	public MirrorImpl(VirtualMachineImpl vm) {
		this.vm = vm;
	}

	/**
	 * This constructor is only to be used by {@link VirtualMachineImpl}. The name 
	 * field is not store anywhere or used in any way.
	 * 
	 * @param name
	 * @noreference This constructor is not intended to be referenced by clients.
	 */
	public MirrorImpl(String name) {
		vm = (VirtualMachineImpl)this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Mirror#virtualMachine()
	 */
	public VirtualMachine virtualMachine() {
		return this.vm;
	}
	
	/**
	 * Re-throws the given exception as a {@link RuntimeException} with the given message
	 * @param message
	 * @param t
	 * @since 1.1
	 */
	protected void handleException(String message, Throwable t) {
		throw new RuntimeException(message, t);
	}
}
