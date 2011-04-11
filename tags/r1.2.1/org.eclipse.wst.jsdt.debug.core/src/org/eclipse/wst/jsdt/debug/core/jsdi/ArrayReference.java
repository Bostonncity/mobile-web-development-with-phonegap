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
 * Abstract representation of an array.
 * 
 * @see ObjectReference
 * @see Value
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ArrayReference extends ObjectReference {

	/**
	 * Returns the current number of {@link Value}s in this array.
	 * 
	 * @return the number of {@link Value}s in the array
	 */
	public int length();

	/**
	 * Returns the {@link Value} at the given index in this array. If the bounds are outside of the
	 * array bounds an {@link ArrayIndexOutOfBoundsException} is thrown. If there is a <code>null</code> entry
	 * in the array {@link NullValue} is returned.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @param index the index of the {@link Value} to retrieve
	 * 
	 * @return the {@link Value} at the given index
	 * @throws IndexOutOfBoundsException if the index is outside the bounds of this array. 
	 * For example if <code>index &lt; 0</code> or <code>index &gt; {@link #length()}</code>
	 */
	public Value getValue(int index) throws IndexOutOfBoundsException;

	/**
	 * Returns the live list of {@link Value}s in the array or an empty list.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the live list of {@link Value}s in this array or an empty list, never <code>null</code>
	 */
	public List/* <Value> */getValues();
}
