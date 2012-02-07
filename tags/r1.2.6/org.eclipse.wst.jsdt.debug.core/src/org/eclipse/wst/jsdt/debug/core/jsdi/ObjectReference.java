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

import java.util.List;

/**
 * Abstract representation of an object with-respect-to JavaScript debugging
 * 
 * @see Value
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ObjectReference extends Value {

	/**
	 * The name of the class represented by the {@link ObjectReference} or <code>null</code> if one cannot be determined.<br>
	 * <br>
	 * In ECMA this will typically be the same as {@link prototype().className()}.
	 * 
	 * @return the name of the class or <code>null</code>
	 */
	public String className();

	/**
	 * The {@link FunctionReference} that can be used to create a reflected instance of the underlying class for this object or {@link NullValue} if one cannot be determined.
	 * 
	 * @return the {@link FunctionReference} used to create a reflected instance of the underlying class or {@link NullValue}
	 */
	public Value constructor();

	/**
	 * The prototype object for this {@link ObjectReference} or {@link NullValue} if one cannot be determined.<br>
	 * <br>
	 * 
	 * The prototype should be the smallest enclosing prototype that parents this {@link ObjectReference}. If no prototype can be determined <code>Object.prototype</code> should be returned, but {@link NullValue} is allowed.
	 * 
	 * @return the prototype for this {@link ObjectReference} or {@link NullValue}
	 */
	public Value prototype();

	/**
	 * The properties for this {@link ObjectReference} or an empty list, never <code>null</code>
	 * 
	 * @return the list of {@link Property} objects for this {@link ObjectReference} or an empty listing.
	 */
	public List/* <PropertyReference> */properties();

	/**
	 * The object reference id - this value should be unique and never <code>null</code>
	 * 
	 * @return the id of this {@link ObjectReference} never <code>null</code>
	 */
	public Number id();
}
