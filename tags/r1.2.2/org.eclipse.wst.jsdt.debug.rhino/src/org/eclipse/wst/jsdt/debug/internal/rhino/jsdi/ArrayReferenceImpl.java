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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.debug.core.jsdi.ArrayReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.Property;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;

/**
 * Rhino implementation of {@link ArrayReference}
 * 
 * @see ArrayReference
 * @see ObjectReferenceImpl
 * @since 1.0
 */
public class ArrayReferenceImpl extends ObjectReferenceImpl implements ArrayReference {

	/**
	 * Empty array
	 */
	protected static final Value[] NO_VALUES = new Value[0];

	/**
	 * Raw list of {@link Value}s
	 */
	private ArrayList values = null;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param stackFrameImpl
	 * @param body
	 */
	public ArrayReferenceImpl(VirtualMachineImpl vm, Map body, StackFrameImpl stackFrameImpl) {
		super(vm, body, stackFrameImpl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ArrayReference#getValue(int)
	 */
	public Value getValue(int index) throws IndexOutOfBoundsException {
		Value value = (Value) getValues().get(index);
		if (value == null) {
			return vm.mirrorOfNull();
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ArrayReference#getValues()
	 */
	public synchronized List getValues() {
		if (values == null) {
			Map members = new HashMap();
			int length = 0;
			for (Iterator iter = properties().iterator(); iter.hasNext();) {
				Property property = (Property) iter.next();
				if (Character.isDigit(property.name().charAt(0))) {
					members.put(Integer.valueOf(property.name()), property.value());
				} else if (property.name().equals("length")) { //$NON-NLS-1$
					length = Integer.parseInt(property.value().valueString());
				}
			}
			values = new ArrayList(length);
			for (int i = 0; i < length; i++) {
				Object value = members.get(new Integer(i));
				if (value == null) {
					value = new UndefinedValueImpl(vm);
				}
				values.add(value);
			}
		}
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ArrayReference#length()
	 */
	public int length() {
		return getValues().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.ObjectReferenceImpl#
	 * valueString()
	 */
	public String valueString() {
		return "Array"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.ObjectReferenceImpl#toString
	 * ()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(NLS.bind(Messages.ArrayReferenceImpl_array_count_, new String[] {Integer.toString(length()) }));
		return buffer.toString();
	}
}
