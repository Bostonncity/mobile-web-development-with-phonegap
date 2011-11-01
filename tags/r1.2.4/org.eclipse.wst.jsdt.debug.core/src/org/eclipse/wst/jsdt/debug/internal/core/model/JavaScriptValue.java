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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.debug.core.jsdi.ArrayReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.BooleanValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.FunctionReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.NullValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.NumberValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.ObjectReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.Property;
import org.eclipse.wst.jsdt.debug.core.jsdi.StringValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.UndefinedValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptValue;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;

/**
 * Default implementation of {@link IValue}
 * 
 * @since 1.0
 */
public class JavaScriptValue extends JavaScriptDebugElement implements IJavaScriptValue {

	/**
	 * the '[proto]' value
	 */
	public static final String PROTO = "[proto]"; //$NON-NLS-1$
	/**
	 * The 'this' value
	 */
	public static final String THIS = "this"; //$NON-NLS-1$
	
	private Value value;
	private List properties;

	/**
	 * Constructor
	 * 
	 * @param target
	 * @param value
	 */
	public JavaScriptValue(JavaScriptDebugTarget target, Value value) {
		super(target);
		this.value = value;
	}
	
	/**
	 * Hook to return the underlying value for this {@link JavaScriptValue}
	 * 
	 * @return the underlying {@link Value}
	 */
	protected Value getUnderlyingValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptValue#getDetailString()
	 */
	public String getDetailString() {
		if (this.value instanceof ArrayReference) {
			ArrayReference array = (ArrayReference) this.value;
			List values = array.getValues();
			Iterator iterator = values.iterator();
			if (! iterator.hasNext())
			    return "[]"; //$NON-NLS-1$
			StringBuffer buffer = new StringBuffer();
			buffer.append('[');
			for (;;) {
				Value jsValue = (Value) iterator.next();
				buffer.append(getValueString(jsValue));
				if (! iterator.hasNext())
					return buffer.append(']').toString();
				buffer.append(", "); //$NON-NLS-1$				
			}
		}

		if (this.value instanceof JavaScriptPrimitiveValue) {
			JavaScriptPrimitiveValue nvalue = (JavaScriptPrimitiveValue)this.value;
			return nvalue.stringValue();
		}
		return this.value.valueString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		if (this.value != null) {
			if (this.value instanceof ObjectReference) {
				if (this.value instanceof NullValue) {
				return NULL;
				} else if (this.value instanceof ArrayReference) {
					return ARRAY;
				}else if (this.value instanceof FunctionReference) {
					return FUNCTION;
				}
				return OBJECT;
			} else if (this.value instanceof BooleanValue) {
				return BOOLEAN;
			} else if (this.value instanceof UndefinedValue) {
				return UNDEFINED;
			} else if (this.value instanceof NumberValue) {
				return NUMBER;
			} else if (this.value instanceof StringValue) {
				return STRING;
			}
		}
		return UNKNOWN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException {
		return getValueString(value);
	}
	
	private static String getValueString(Value jsValue) {
		if (jsValue instanceof ObjectReference) {
			ObjectReference ref = (ObjectReference) jsValue;
			StringBuffer buffer = new StringBuffer();
			buffer.append(NLS.bind(ModelMessages.JavaScriptValue_object_value_label, new String[] {jsValue.valueString(), ref.id().toString()}));
			return buffer.toString();
		}
		if (jsValue instanceof NumberValue) {
			NumberValue nvalue = (NumberValue) jsValue;
			if (!nvalue.isNaN()) {
				return JavaScriptDebugModel.numberToString(nvalue.value());
			}
		}
		
		if (jsValue instanceof StringValue) {
			StringBuffer buffer = new StringBuffer();
			buffer.append('"');
			buffer.append(jsValue.valueString().replaceAll("\"", "\\\\\"")); //$NON-NLS-1$//$NON-NLS-2$
			buffer.append('"');
			return buffer.toString();
		}
		
		return jsValue.valueString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public synchronized IVariable[] getVariables() throws DebugException {
		if (!hasVariables()) {
			return null;
		}
		if (this.properties == null) {
			final ObjectReference reference = (ObjectReference) this.value;
			List underlyingProperties = reference.properties();
			this.properties = new ArrayList(underlyingProperties.size()+1);
			for (Iterator iterator = underlyingProperties.iterator(); iterator.hasNext();) {
				Property property = (Property) iterator.next();
				JavaScriptProperty jsdiProperty = new JavaScriptProperty(this, property);
				this.properties.add(jsdiProperty);
			}
			
			// sort - array values (e.g integer names) first then alphabetically
			Collections.sort(properties, new Comparator() {		
				public int compare(Object arg0, Object arg1) {
					try {		
					String name0 = ((IVariable) arg0).getName();
					String name1 = ((IVariable) arg1).getName();
					
					if (Character.isDigit(name0.charAt(0))) {
						if (Character.isDigit(name1.charAt(0))) 
							return Integer.valueOf(name0).compareTo(Integer.valueOf(name1));
						return -1;
					}
					if (Character.isDigit(name1.charAt(0)))
						return 1;

					return name0.compareToIgnoreCase(name1);
					} catch (DebugException e) {
						return 0;
					}
				}
			});
			
			// add the prototype
			Property prototype = new Property() {
				public VirtualMachine virtualMachine() {
					return reference.virtualMachine();
				}

				public String name() {
					return PROTO;
				}

				public Value value() {
					return reference.prototype();
				}
			};
			properties.add(0, new JavaScriptProperty(this, prototype));			
		}
		return (IVariable[]) this.properties.toArray(new IVariable[this.properties.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		if (this.value instanceof ObjectReference) {
			return !(this.value instanceof NullValue);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return this.properties != null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptValue#isNull()
	 */
	public boolean isNull() {
		return this.value instanceof NullValue;
	}
	
	/**
	 * Delegate method to create the proper kind of {@link IJavaScriptValue}
	 * 
	 * @param target the target to create the value for
	 * @param value the value to wrap
	 * @return a new {@link IJavaScriptValue}
	 */
	public static IJavaScriptValue createValue(JavaScriptDebugTarget target, Value value) {
		if(value instanceof BooleanValue ||
				value instanceof NullValue ||
				value instanceof NumberValue) {
			return new JavaScriptPrimitiveValue(target, value);
		}
		return new JavaScriptValue(target, value);
	}
}
