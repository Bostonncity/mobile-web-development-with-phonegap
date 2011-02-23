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
 * Abstract description of a thread
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ThreadReference extends Mirror {

	/**
	 * Constant indicating the state of the thread is unknown<br>
	 * <br>
	 * Value is: <code>-1</code> 
	 */
	public static final int THREAD_STATUS_UNKNOWN = -1;
	/**
	 * Constant indicating the thread is in a zombie state<br>
	 * <br>
	 * Value is: <code>0</code>
	 */
	public static final int THREAD_STATUS_ZOMBIE = 0;
	/**
	 * Constant indicating the thread is in a running state<br>
	 * <br>
	 * Value is: <code>1</code>
	 */
	public static final int THREAD_STATUS_RUNNING = 1;
	/**
	 * Constant indicating the thread is in a sleeping state<br>
	 * <br>
	 * Value is: <code>2</code>
	 */
	public static final int THREAD_STATUS_SLEEPING = 2;
	/**
	 * Constant indicating the thread is holding a monitor<br>
	 * <br>
	 * Value is: <code>3</code>
	 */
	public static final int THREAD_STATUS_MONITOR = 3;
	/**
	 * Constant indicating the thread is waiting for a monitor<br>
	 * <br>
	 * Value is: <code>4</code>
	 */
	public static final int THREAD_STATUS_WAIT = 4;
	/**
	 * Constant indicating the thread has not been started<br>
	 * <br>
	 * Value is: <code>5</code>
	 */
	public static final int THREAD_STATUS_NOT_STARTED = 5;

	/**
	 * Returns the total stack frame count for this thread
	 * 
	 * @return the total stack frame count
	 */
	public int frameCount();

	/**
	 * Returns the stack frame for the given index in this thread.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the stack frame at the given index or <code>null</code>
	 */
	public StackFrame frame(int index);

	/**
	 * Returns the live list of stack frames for this thread.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of stack frames from this thread or an empty list, never <code>null</code>
	 */
	public List /*<StackFrame>*/ frames();

	/**
	 * Send a request to interrupt this threads' execution.
	 */
	public void interrupt();

	/**
	 * Sends a request to resume this thread, iff it is in the suspended state.
	 */
	public void resume();

	/**
	 * Sends a request to suspend this thread, iff it is not already in a suspended state.
	 */
	public void suspend();

	/**
	 * Returns the status of this thread.
	 * 
	 * @see #THREAD_STATUS_MONITOR
	 * @see #THREAD_STATUS_NOT_STARTED
	 * @see #THREAD_STATUS_RUNNING
	 * @see #THREAD_STATUS_SLEEPING
	 * @see #THREAD_STATUS_UNKNOWN
	 * @see #THREAD_STATUS_WAIT
	 * @see #THREAD_STATUS_ZOMBIE
	 * 
	 * @return the status of this thread
	 */
	public int status();

	/**
	 * Returns whether or not this thread is currently suspended on a breakpoint.
	 * 
	 * @return <code>true</code> if the thread is suspended on a breakpoint <code>false</code> otherwise
	 */
	public boolean isAtBreakpoint();

	/**
	 * Returns if this thread is currently in a suspended state.
	 * 
	 * @return <code>true</code> if the thread is suspended <code>false</code> otherwise
	 */
	public boolean isSuspended();

	/**
	 * Returns the simple name of this thread.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the name of the thread or <code>null</code>
	 */
	public String name();
}
