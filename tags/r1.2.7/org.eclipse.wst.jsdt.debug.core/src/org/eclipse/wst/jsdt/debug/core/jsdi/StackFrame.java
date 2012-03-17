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
 * Abstract description of a stack frame
 * 
 * @see Location
 * @see Mirror
 * @see Value
 * @see Variable
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface StackFrame extends Mirror {

	/**
	 * Returns the value of <code>this</code> for the current frame.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the 'this' ObjectReference never <code>null</code>
	 */
	public Variable thisObject();

	/**
	 * The visible variables for this {@link StackFrame}.<br>
	 * <br>
	 * This method cannot return <code>null</code>.
	 * 
	 * @return the list of {@link Variable} objects for this {@link StackFrame} or an empty list, never <code>null</code>
	 */
	public List/* <Variable> */variables();

	/**
	 * The current {@link Location} associated with this {@link StackFrame}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the {@link Location} associated with this {@link StackFrame} or <code>null</code>
	 */
	public Location location();

	/**
	 * Evaluates the given expression in the context of this {@link StackFrame}
	 * and returns the resulting {@link Value}.<br>
	 * <br>
	 * This method cannot return <code>null</code> but can return the {@link NullValue}
	 * 
	 * @return the {@link Value} result of evaluating the expression never <code>null</code>
	 */
	public Value evaluate(String expression);
}
