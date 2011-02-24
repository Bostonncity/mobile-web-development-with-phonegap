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

import org.eclipse.wst.jsdt.debug.core.jsdi.NumberValue;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.JSONConstants;

/**
 * Rhino implementation of {@link NumberValue}
 * 
 * @since 1.0
 */
public class NumberValueImpl extends MirrorImpl implements NumberValue {

	/**
	 * The underlying value
	 */
	private Number value;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param body
	 */
	public NumberValueImpl(VirtualMachineImpl vm, Map body) {
		this(vm, (Number) body.get(JSONConstants.VALUE));
	}

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param number
	 */
	public NumberValueImpl(VirtualMachineImpl vm, Number number) {
		super(vm);
		this.value = number;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.NumberValue#value()
	 */
	public Number value() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.NumberValue#isNaN()
	 */
	public boolean isNaN() {
		return value == null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Value#valueString()
	 */
	public String valueString() {
		return JavaScriptDebugModel.numberToString(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.PrimitiveValue#intValue()
	 */
	public int intValue() {
		return value.intValue();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.PrimitiveValue#doubleValue()
	 */
	public double doubleValue() {
		return value.doubleValue();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.PrimitiveValue#booleanValue()
	 */
	public boolean booleanValue() {
		return (value.intValue() > 0 ? true : false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.PrimitiveValue#stringValue()
	 */
	public String stringValue() {
		return JavaScriptDebugModel.numberToString(value);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return stringValue();
	}
}
