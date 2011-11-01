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

import org.eclipse.wst.jsdt.debug.core.jsdi.StackFrame;
import org.eclipse.wst.jsdt.debug.core.jsdi.Variable;

/**
 * Rhino implementation of a {@link Variable}
 * 
 * @since 1.0
 */
public class VariableImpl extends PropertyImpl implements Variable {

	/**
	 * Constructor
	 * @param vm
	 * @param frame
	 * @param name
	 * @param ref
	 */
	public VariableImpl(VirtualMachineImpl vm, StackFrameImpl frame, String name, Number ref) {
		super(vm, frame, name, ref);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Variable#isArgument()
	 */
	public boolean isArgument() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Variable#isVisible(org.eclipse.wst.jsdt.debug.core.jsdi.StackFrame)
	 */
	public boolean isVisible(StackFrame frame) {
		StackFrameImpl frameImpl = (StackFrameImpl) frame;
		return frameImpl.isVisible(this);
	}
}