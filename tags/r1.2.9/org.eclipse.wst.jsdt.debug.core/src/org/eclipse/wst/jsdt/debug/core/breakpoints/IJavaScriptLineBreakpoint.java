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
import org.eclipse.debug.core.model.ILineBreakpoint;

/**
 * Abstract description of a line breakpoint for JavaScript
 * 
 * @since 1.0
 */
public interface IJavaScriptLineBreakpoint extends ILineBreakpoint, IJavaScriptBreakpoint {

	/**
	 * Registered marker id for a JavaScript line breakpoint
	 */
	public static final String MARKER_ID = "org.eclipse.wst.jsdt.debug.core.line.breakpoint.marker"; //$NON-NLS-1$
	
	/**
	 * Returns the condition set for this breakpoint or <code>null</code>.
	 * 
	 * @return the condition or <code>null</code> if one has not been set
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public String getCondition() throws CoreException;
	
	/**
	 * Sets the given condition for the breakpoint.
	 * 
	 * @param condition the new condition to set. <code>null</code> has the effect of erasing the current condition
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setCondition(String condition) throws CoreException;
	
	/**
	 * Returns if the condition for this breakpoint is enabled or not
	 * 
	 * @return if the condition is enabled
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public boolean isConditionEnabled() throws CoreException;
	
	/**
	 * Allows the enabled state of the condition to be set
	 * 
	 * @param enabled if the condition should be enabled. Disabling a condition does not remove it.
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setConditionEnabled(boolean enabled) throws CoreException;
	
	/**
	 * Returns if the breakpoint should suspend when the assigned condition evaluates to <code>true</code>.
	 * 
	 * @return true if the breakpoint should suspend when the condition is true
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public boolean isConditionSuspendOnTrue() throws CoreException;
	
	/**
	 * Sets if the condition for this breakpoint will suspend when it evaluates to <code>true</code>.
	 * 
	 * @param suspendontrue if the condition should suspend when it evaluates to <code>true</code>
	 * @throws CoreException if the breakpoint cannot be accessed
	 */
	public void setConditionSuspendOnTrue(boolean suspendontrue) throws CoreException;
}
