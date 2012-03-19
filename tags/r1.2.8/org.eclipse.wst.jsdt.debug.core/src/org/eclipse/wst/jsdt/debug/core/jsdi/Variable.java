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
 * Abstract description of a variable.
 * 
 * @see Property
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface Variable extends Property {

	/**
	 * Returns <code>true</code> if this variable is an argument <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this variable is an argument <code>false</code> otherwise
	 */
	public boolean isArgument();

	/**
	 * Returns <code>true</code> if this variable is externally visible in the context of the given {@link StackFrame}.
	 * 
	 * @param frame the {@link StackFrame} context
	 * @return <code>true</code> if this variable is externally visible within the given {@link StackFrame} <code>false</code> otherwise
	 */
	public boolean isVisible(StackFrame frame);

}
