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

import org.eclipse.wst.jsdt.debug.core.jsdi.UndefinedValue;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.JSONConstants;

/**
 * Rhino implementation of {@link UndefinedValue}
 * 
 * @see MirrorImpl
 * @see UndefinedValue
 * @since 1.0
 */
public class UndefinedValueImpl extends MirrorImpl implements UndefinedValue {

	static final String UNDEFINED_VALUE = "Undefined"; //$NON-NLS-1$

	/**
	 * Constructor
	 * 
	 * @param vm
	 */
	public UndefinedValueImpl(VirtualMachineImpl vm) {
		super(vm);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.StringValue#value()
	 */
	public String value() {
		return JSONConstants.UNDEFINED;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Value#valueString()
	 */
	public String valueString() {
		return UNDEFINED_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return valueString();
	}
}
