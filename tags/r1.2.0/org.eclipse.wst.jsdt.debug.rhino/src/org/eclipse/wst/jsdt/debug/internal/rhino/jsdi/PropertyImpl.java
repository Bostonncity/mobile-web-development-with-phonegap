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

import org.eclipse.wst.jsdt.debug.core.jsdi.Property;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;

/**
 * Rhino implementation of {@link Property}
 * 
 * @see MirrorImpl
 * @see Property
 * @see Value
 * @since 1.0
 */
public class PropertyImpl extends MirrorImpl implements Property {

	/**
	 * The name of the property
	 */
	private String name;
	/**
	 * The reference used to look up the property value in the stackframe
	 * context
	 */
	private Number ref;
	private StackFrameImpl frame;
	private Value value;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param name
	 * @param ref
	 */
	public PropertyImpl(VirtualMachineImpl vm, StackFrameImpl frame,
			String name, Number ref) {
		super(vm);
		this.frame = frame;
		this.name = name;
		this.ref = ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Property#name()
	 */
	public String name() {
		return name;
	}

	/**
	 * The reference id of this property
	 * 
	 * @return the reference id of this property
	 */
	public Number getRef() {
		return ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.StringValue#value()
	 */
	public Value value() {
		synchronized (this.frame) {
			if (this.value == null) {
				this.value = frame.lookupValue(ref);
			}
		}
		return this.value;
	}
}
