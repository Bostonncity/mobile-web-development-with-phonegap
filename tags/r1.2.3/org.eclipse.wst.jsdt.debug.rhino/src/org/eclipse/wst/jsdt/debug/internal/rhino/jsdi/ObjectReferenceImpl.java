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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.Property;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.JSONConstants;

/**
 * Rhino implementation of {@link ObjectReference}
 * 
 * @see MirrorImpl
 * @see ObjectReference
 * @see Property
 * @since 1.0
 */
public class ObjectReferenceImpl extends MirrorImpl implements ObjectReference {

	protected static final ArrayList NO_PROPERTIES = new ArrayList(0);

	/**
	 * The name of the class backing this object
	 */
	private final String className;
	/**
	 * The reference id to the constructor for this object
	 */
	private final Number constructorRef;
	/**
	 * The reference id to the prototype for this object
	 */
	private final Number prototypeRef;
	/**
	 * The reference id for this object
	 */
	private final Number id;
	/**
	 * The stackframe context for this object
	 */
	protected StackFrameImpl frame;
	/**
	 * The JSDI handle to the constructor for this object - lazily computed in {@link #constructor()}
	 */
	private Value constructor = null;
	/**
	 * The JSDI handle to the prototype for this object - lazily computed in {@link #prototype()}
	 */
	private Value prototype = null;
	/**
	 * The backing list of {@link PropertyReference}s for this object
	 */
	private List properties = null;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param body
	 * @param frame
	 */
	public ObjectReferenceImpl(VirtualMachineImpl vm, Map body, StackFrameImpl frame) {
		super(vm);
		this.frame = frame;
		this.className = (String) body.get(JSONConstants.CLASS_NAME);
		this.id = (Number) body.get(JSONConstants.HANDLE);
		this.constructorRef = (Number) ((Map) body.get(JSONConstants.CONSTRUCTOR_FUNCTION)).get(JSONConstants.REF);
		this.prototypeRef = (Number) ((Map) body.get(JSONConstants.PROTOTYPE_OBJECT)).get(JSONConstants.REF);
		List proplist = (List) body.get(JSONConstants.PROPERTIES);
		if (proplist != null) {
			this.properties = new ArrayList(proplist.size());
			Map props = null;
			for (Iterator iter = proplist.iterator(); iter.hasNext();) {
				props = (Map) iter.next();
				this.properties.add(new PropertyImpl(vm, frame, props.get(JSONConstants.NAME).toString(), (Number) props.get(JSONConstants.REF)));
			}
		} else {
			this.properties = NO_PROPERTIES;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference#className()
	 */
	public String className() {
		return this.className;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference#constructor()
	 */
	public Value constructor() {
		synchronized (this.frame) {
			if (this.constructor == null) {
				this.constructor = this.frame.lookupValue(constructorRef);
				if (this.constructor == null) {
					this.constructor = this.vm.mirrorOfNull();
				}
			}
		}
		return this.constructor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference#prototype()
	 */
	public Value prototype() {
		synchronized (this.frame) {
			if (this.prototype == null) {
				this.prototype = this.frame.lookupValue(prototypeRef);
				if (this.prototype == null) {
					this.prototype = this.vm.mirrorOfNull();
				}
			}
		}
		return this.prototype;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference#properties()
	 */
	public List properties() {
		return this.properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Object Reference\n"); //$NON-NLS-1$
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Value#valueString()
	 */
	public String valueString() {
		return "Object"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference#id()
	 */
	public Number id() {
		return this.id;
	}
}
