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

import java.util.Map;

import org.eclipse.wst.jsdt.debug.core.jsdi.FunctionReference;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.JSONConstants;

/**
 * Rhino implementation of {@link FunctionReference}
 * 
 * @see FunctionReference
 * @see ObjectReferenceImpl
 * @since 1.0
 */
public class FunctionReferenceImpl extends ObjectReferenceImpl implements FunctionReference {

	private String functionName;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param body
	 * @param stackFrameImpl
	 */
	public FunctionReferenceImpl(VirtualMachineImpl vm, Map body, StackFrameImpl stackFrameImpl) {
		super(vm, body, stackFrameImpl);
		this.functionName = (String) body.get(JSONConstants.NAME);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.FunctionReference#functionName()
	 */
	public String functionName() {
		return functionName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.ObjectReferenceImpl#valueString()
	 */
	public String valueString() {
		return "Function"; //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.FunctionReference#functionBody()
	 */
	public String functionBody() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.ObjectReferenceImpl#toString()
	 */
	public String toString() {
		return valueString();
	}
}
