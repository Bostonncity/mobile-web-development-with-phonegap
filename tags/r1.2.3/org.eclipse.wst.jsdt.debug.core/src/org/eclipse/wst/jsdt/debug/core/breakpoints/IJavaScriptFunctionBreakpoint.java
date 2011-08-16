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
package org.eclipse.wst.jsdt.debug.core.breakpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;

/**
 * Abstract description of a JavaScript function breakpoint
 * 
 * @since 1.0
 */
public interface IJavaScriptFunctionBreakpoint extends IJavaScriptLineBreakpoint {

	/**
	 * The id of the attribute for a method signature
	 */
	public static final String FUNCTION_SIGNAURE = JavaScriptDebugPlugin.PLUGIN_ID + ".function_signature"; //$NON-NLS-1$
	/**
	 * The id of the attribute for the function name
	 */
	public static final String FUNCTION_NAME = JavaScriptDebugPlugin.PLUGIN_ID + ".function_name"; //$NON-NLS-1$
	/**
	 * Registered marker id for a JavaScript function breakpoint
	 */
	public static final String MARKER_ID = "org.eclipse.wst.jsdt.debug.core.function.breakpoint.marker"; //$NON-NLS-1$

	/**
	 * Returns if the breakpoint will suspend when the function is entered.
	 * 
	 * @return <code>true</code> if the breakpoint is set to suspend when entering the function <code>false</code> otherwise
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public boolean isEntry() throws CoreException;
	
	/**
	 * Sets if this will be a function entry breakpoint.
	 * 
	 * @param isentry if the breakpoint should suspend when the function is entered
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setEntry(boolean isentry) throws CoreException;
	
	/**
	 * Returns if the breakpoint will suspend when the function is exited.
	 * 
	 * @return <code>true</code> if the breakpoint is set to suspend when exiting the function <code>false</code> otherwise
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public boolean isExit() throws CoreException;
	
	/**
	 * Sets if this will be a function exit breakpoint.
	 * 
	 * @param isexit if the breakpoint should suspend when the function is exited
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setExit(boolean isexit) throws CoreException;
	
	/**
	 * Returns the function name set in the breakpoint.
	 * 
	 * @return the function name or <code>null</code> if none
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public String getFunctionName() throws CoreException;
	
	/**
	 * Returns the function signature set in the breakpoint.
	 * 
	 * @return the function signature or <code>null</code> if none
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public String getSignature() throws CoreException;
}
