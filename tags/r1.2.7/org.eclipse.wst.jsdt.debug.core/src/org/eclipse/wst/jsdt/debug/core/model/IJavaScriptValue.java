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

import org.eclipse.debug.core.model.IValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.NullValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;

/**
 * Abstract definition of a {@link Value} wrt JavaScript debugging
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaScriptValue extends IValue {

	
	public static final String UNDEFINED = "undefined"; //$NON-NLS-1$
	public static final String UNKNOWN = "unknown"; //$NON-NLS-1$
	public static final String NULL = "null"; //$NON-NLS-1$
	public static final String BOOLEAN = "boolean"; //$NON-NLS-1$
	public static final String NUMBER = "number"; //$NON-NLS-1$
	public static final String STRING = "string"; //$NON-NLS-1$
	public static final String OBJECT = "object"; //$NON-NLS-1$
	public static final String ARRAY = "array"; //$NON-NLS-1$
	public static final String FUNCTION = "function"; //$NON-NLS-1$

	/**
	 * Returns the string to use for details display.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the string to use for details display or <code>null</code>
	 */
	public String getDetailString();
	
	/**
	 * Returns if the value is {@link NullValue}
	 * 
	 * @return if the value is {@link NullValue}
	 */
	public boolean isNull();
}
