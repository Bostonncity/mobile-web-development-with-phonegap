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

import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.wst.jsdt.debug.core.jsdi.NullValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;

/**
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IJavaScriptStackFrame extends IStackFrame {

	/**
	 * Allows the given expression to be evaluated and returns the {@link Value}
	 * of the evaluation.<br>
	 * <br>
	 * This method cannot return <code>null</code> and will return a value with {@link NullValue} instead.
	 * 
	 * @param expression the expression to evaluate, <code>null</code> is accepted and will cause a value
	 * backed by {@link NullValue} to be returned
	 * @return the {@link IJavaScriptValue} from the evaluation never <code>null</code>
	 */
	public IJavaScriptValue evaluate(String expression);
	
	/**
	 * Returns the name of the source backing the stack frame.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the name of the source or <code>null</code>
	 */
	public String getSourceName();
	
	/**
	 * Returns the path to the source.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the path to the source or <code>null</code>
	 */
	public String getSourcePath();
	
	/**
	 * Returns the underlying source for the frame.<br>
	 * <br>
	 * This method can return <code>null</code> if the source cannot be acquired
	 * 
	 * @return the underlying source or <code>null</code>
	 */
	public String getSource();
}
