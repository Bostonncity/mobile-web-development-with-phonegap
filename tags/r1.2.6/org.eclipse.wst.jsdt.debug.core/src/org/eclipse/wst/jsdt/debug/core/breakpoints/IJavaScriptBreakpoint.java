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
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;

/**
 * General JavaScript breakpoint
 * 
 * @since 1.0
 */
public interface IJavaScriptBreakpoint extends IBreakpoint {

	/**
	 * The suspend policy for the breakpoint
	 */
	public static final String SUSPEND_POLICY = JavaScriptDebugPlugin.PLUGIN_ID + ".suspend_policy"; //$NON-NLS-1$
	/**
	 * The type name within the script
	 */
	public static final String TYPE_NAME = JavaScriptDebugPlugin.PLUGIN_ID + ".type_name"; //$NON-NLS-1$
	/**
	 * Breakpoint attribute for the path of the script
	 */
	public static final String SCRIPT_PATH = JavaScriptDebugPlugin.PLUGIN_ID + ".script_path"; //$NON-NLS-1$
	/**
	 * The hit count set in the breakpoint
	 */
	public static final String HIT_COUNT = JavaScriptDebugPlugin.PLUGIN_ID + ".hit_count"; //$NON-NLS-1$
	
	/**
	 * Suspend policy for suspending the current thread of execution
	 */
	public static final int SUSPEND_THREAD = 1;
	/**
	 * Suspend policy for suspending the current target
	 */
	public static final int SUSPEND_TARGET = 2;
	/**
	 * JSDT member handle
	 */
	public static final String ELEMENT_HANDLE = JavaScriptDebugPlugin.PLUGIN_ID + ".handle"; //$NON-NLS-1$
	
	/**
	 * Returns the path of the script as it was set when the breakpoint was created.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the path of the script this breakpoint was created on never <code>null</code>
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public String getScriptPath() throws CoreException;
	
	/**
	 * Returns the type name that the breakpoint is set within.<br>
	 * <br>
	 * This method can return <code>null</code> when the breakpoint is set
	 * on a top-level type i.e. set on the root source
	 * 
	 * @return the type name or <code>null</code>
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public String getTypeName() throws CoreException;
	
	/**
	 * Returns the hit count set for this breakpoint 
	 * or -1 if no hit count has been set.
	 * 
	 * @return the hit count
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public int getHitCount() throws CoreException;
	
	/**
	 * Sets the given hit count for the breakpoint, 
	 * throws an {@link IllegalArgumentException} if the given count is less than 1.
	 * 
	 * @param count the count to set, must be &gt; 0
	 * @throws CoreException
	 * @throws IllegalArgumentException if count &lt; 1
	 */
	public void setHitCount(int count) throws CoreException, IllegalArgumentException;
	
	/**
	 * Returns the suspend policy for this breakpoint, 
	 * default suspend policy is to suspend the thread
	 * 
	 * @see #SUSPEND_THREAD
	 * @see #SUSPEND_TARGET
	 * @return the suspend policy
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public int getSuspendPolicy() throws CoreException;
	
	/**
	 * Sets the suspend policy for this breakpoint. Anything 
	 * other than {@link #SUSPEND_THREAD} or {@link #SUSPEND_TARGET}
	 * will be ignored.
	 * 
	 * @see #SUSPEND_THREAD
	 * @see #SUSPEND_TARGET
	 * @param policy the policy to set
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setSuspendPolicy(int policy) throws CoreException;
	
	/**
	 * Returns the {@link IJavaScriptElement} handle for the member
	 * this breakpoint is set on.<br>
	 * <br>
	 * This method can return <code>null</code> if no element handle has been set
	 * 
	 * @return the {@link IJavaScriptElement} handle or <code>null</code>
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public String getJavaScriptElementHandle() throws CoreException;
	
	/**
	 * Allows the {@link IJavaScriptElement} handle to be set for this breakpoint.
	 * 
	 * @param handle the new handle to set, <code>null</code> will remove any existing handle
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setJavaScriptElementHandle(String handle) throws CoreException;
	
	/**
	 * Returns if the breakpoint is currently installed or not.
	 * 
	 * @return <code>true</code> if the breakpoint is installed <code>false</code> otherwise
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public boolean isInstalled() throws CoreException;
}
