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

import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ThreadExitEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ThreadExitRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;

/**
 * Rhino implementation of {@link ThreadExitEvent}
 * 
 * @since 1.0
 */
public final class ThreadExitEventImpl extends LocatableEventImpl implements ThreadExitEvent {

	/**
	 * Constructor
	 * @param vm
	 * @param thread
	 * @param request
	 */
	public ThreadExitEventImpl(VirtualMachineImpl vm, ThreadReference thread, ThreadExitRequest request) {
		super(vm, thread, null, request);
	}
}