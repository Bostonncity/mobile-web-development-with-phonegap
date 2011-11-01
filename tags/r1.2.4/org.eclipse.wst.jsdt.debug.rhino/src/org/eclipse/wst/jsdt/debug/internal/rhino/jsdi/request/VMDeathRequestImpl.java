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

import org.eclipse.wst.jsdt.debug.core.jsdi.request.VMDeathRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;

/**
 * Rhino implementation for a {@link VMDeathRequest}
 * 
 * @since 1.0
 */
public class VMDeathRequestImpl extends EventRequestImpl implements VMDeathRequest {

	/**
	 * Constructor 
	 * @param vm
	 */
	public VMDeathRequestImpl(VirtualMachineImpl vm) {
		super(vm);
	}
}