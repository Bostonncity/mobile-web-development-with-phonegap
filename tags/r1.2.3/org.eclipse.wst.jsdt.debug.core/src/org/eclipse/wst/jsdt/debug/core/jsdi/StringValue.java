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
package org.eclipse.wst.jsdt.debug.core.jsdi;

/**
 * Abstract representation of a String literal with-respect-to JavaScript debugging
 * 
 * @see Value
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface StringValue extends Value {

	/**
	 * Returns the underlying value of the string.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the value of the string or <code>null</code>
	 */
	public String value();
}
