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
package org.eclipse.wst.jsdt.debug.internal.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.wst.jsdt.debug.core.jsdi.PrimitiveValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptPrimitiveValue;

/**
 * {@link IJavaScriptPrimitiveValue} implementation
 * @since 1.0
 */
public class JavaScriptPrimitiveValue extends JavaScriptValue implements IJavaScriptPrimitiveValue {

	/**
	 * Constructor
	 * @param target
	 * @param value
	 */
	public JavaScriptPrimitiveValue(JavaScriptDebugTarget target, Value value) {
		super(target, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptPrimitiveValue#getNumberValue()
	 */
	public int intValue() {
		Value value = getUnderlyingValue();
		if(value instanceof PrimitiveValue) {
			return ((PrimitiveValue)value).intValue();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptPrimitiveValue#doubleValue()
	 */
	public double doubleValue() {
		Value value = getUnderlyingValue();
		if(value instanceof PrimitiveValue) {
			return ((PrimitiveValue)value).doubleValue();
		}
		return intValue();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptPrimitiveValue#getBooleanValue()
	 */
	public boolean booleanValue() {
		Value value = getUnderlyingValue();
		if(value instanceof PrimitiveValue) {
			return ((PrimitiveValue)value).booleanValue();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptPrimitiveValue#getStringValue()
	 */
	public String stringValue() {
		Value value = getUnderlyingValue();
		if(value instanceof PrimitiveValue) {
			return ((PrimitiveValue)value).valueString();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return stringValue();
	}
}
