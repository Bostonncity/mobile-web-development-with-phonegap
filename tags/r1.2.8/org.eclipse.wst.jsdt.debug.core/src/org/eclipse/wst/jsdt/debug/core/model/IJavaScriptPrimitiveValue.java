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
package org.eclipse.wst.jsdt.debug.core.model;


/**
 * Describes a primitive value 
 * 
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IJavaScriptPrimitiveValue extends IJavaScriptValue {

	/**
	 * Returns the integer for the value.
	 * 
	 * @return the integer value
	 */
	public int intValue();
	
	/**
	 * Returns the double for the value.
	 * 
	 * @return the double value
	 */
	public double doubleValue();
	
	/**
	 * Returns the boolean for the value.
	 * 
	 * @return the boolean value
	 */
	public boolean booleanValue();
	
	/**
	 * Returns the String value for the value.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the {@link String} value
	 */
	public String stringValue();
}
