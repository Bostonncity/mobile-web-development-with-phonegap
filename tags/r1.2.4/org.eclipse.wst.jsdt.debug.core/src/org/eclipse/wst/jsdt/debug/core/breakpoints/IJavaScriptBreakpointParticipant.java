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

import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptThread;

/**
 * Describes a participant that will be consulted during the 
 * suspending / resuming of an {@link IJavaScriptBreakpoint}.<br>
 * <br>
 * Every participant has a distinct vote to resume or suspend a breakpoint.
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IJavaScriptBreakpointParticipant {

	/**
	 * Return code in response to a "breakpoint hit" notification, indicating
	 * a vote to suspend the associated thread.
	 */
	public static int SUSPEND = 0x0001;
	/**
	 * Return code in response to a "breakpoint hit" notification, indicating
	 * a vote to not suspend (i.e. resume) the associated thread.
	 */
	public static int DONT_SUSPEND = 0x0002;
	/**
	 * Return code indicating that this listener should not be considered
	 * in a vote to suspend a thread or install a breakpoint.
	 */
	public static int DONT_CARE = 0x0000;
	
	/**
	 * Notification that the given breakpoint has been hit
	 * in the specified thread. Allows this listener to
	 * vote to determine if the given thread should be suspended in
	 * response to the breakpoint. If at least one listener votes to
	 * <code>SUSPEND</code>, the thread will suspend. If there
	 * are no votes to suspend the thread, there must be at least one
	 * <code>DONT_SUSPEND</code> vote to avoid the suspension (resume). If all
	 * listeners vote <code>DONT_CARE</code>, the thread will suspend by default.
	 * <p>
	 * The thread the breakpoint has been encountered in is now suspended. Listeners
	 * may query thread state and perform evaluations. All subsequent breakpoints
	 * in this thread will be ignored until voting has completed.
	 * </p>
	 * @param thread the JavaScript thread
	 * @param breakpoint the {@link IJavaScriptBreakpoint}
	 * @return whether the thread should suspend or whether this
	 *  listener doesn't care - one of <code>SUSPEND</code>, 
	 *  <code>DONT_SUSPEND</code>, or <code>DONT_CARE</code>
	 */
	public int breakpointHit(IJavaScriptThread thread, IJavaScriptBreakpoint breakpoint);
	
	/**
	 * Notification that the given {@link ScriptReference} has been loaded
	 * in the specified thread. Allows this listener to
	 * vote to determine if the given thread should be suspended in
	 * response to the breakpoint. If at least one listener votes to
	 * <code>SUSPEND</code>, the thread will suspend. If there
	 * are no votes to suspend the thread, there must be at least one
	 * <code>DONT_SUSPEND</code> vote to avoid the suspension (resume). If all
	 * listeners vote <code>DONT_CARE</code>, the thread will suspend by default.
	 * <p>
	 * The thread the breakpoint has been encountered in is now suspended. Listeners
	 * may query thread state and perform evaluations. All subsequent breakpoints
	 * in this thread will be ignored until voting has completed.
	 * </p>
	 * @param thread the JavaScript thread
	 * @param script the {@link ScriptReference} that has been loaded
	 * @param breakpoint the {@link IJavaScriptBreakpoint}
	 * @return whether the thread should suspend or whether this
	 *  listener doesn't care - one of <code>SUSPEND</code>, 
	 *  <code>DONT_SUSPEND</code>, or <code>DONT_CARE</code>
	 */
	public int scriptLoaded(IJavaScriptThread thread, ScriptReference script, IJavaScriptBreakpoint breakpoint);
}
