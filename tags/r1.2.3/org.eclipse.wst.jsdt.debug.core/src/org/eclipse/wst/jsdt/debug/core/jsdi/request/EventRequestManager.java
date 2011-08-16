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
package org.eclipse.wst.jsdt.debug.core.jsdi.request;

import java.util.List;

import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;

/**
 * Description of a manager for creating {@link EventRequest}s
 * 
 * @see EventRequest
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface EventRequestManager {

	/**
	 * Creates a new {@link BreakpointRequest} for the given {@link Location}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @param location the {@link Location} to create the breakpoint for, <code>null</code> is not accepted
	 * 
	 * @return a new {@link BreakpointRequest} for the given {@link Location}, never <code>null</code>
	 */
	public BreakpointRequest createBreakpointRequest(Location location);

	/**
	 * Returns the live immutable list of {@link BreakpointRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link BreakpointRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<BreakpointRequest>*/ breakpointRequests();

	/**
	 * Creates a new {@link DebuggerStatementRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link DebuggerStatementRequest} never <code>null</code>
	 */
	public DebuggerStatementRequest createDebuggerStatementRequest();

	/**
	 * Returns the live immutable list of {@link DebuggerStatementRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link DebuggerStatementRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<DebuggerStatementRequest>*/ debuggerStatementRequests();

	/**
	 * Creates a new {@link ExceptionRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link ExceptionRequest} never <code>null</code>
	 */
	public ExceptionRequest createExceptionRequest();

	/**
	 * Returns the live immutable list of {@link ExceptionRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link ExceptionRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<ExceptionRequest>*/ exceptionRequests();

	/**
	 * Creates a new {@link ScriptLoadRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link ScriptLoadRequest} never <code>null</code>
	 */
	public ScriptLoadRequest createScriptLoadRequest();

	/**
	 * Returns the live immutable list of {@link ScriptLoadRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link ScriptLoadRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<ScriptLoadRequest>*/ scriptLoadRequests();

	/**
	 * Creates a new {@link StepRequest} for the specified {@link ThreadReference} of the given kind.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @param thread the {@link ThreadReference} to perform the step in
	 * @param step the kind of step
	 * @see StepRequest for a complete listing of step kinds
	 * 
	 * @return a new {@link StepRequest} never <code>null</code>
	 */
	public StepRequest createStepRequest(ThreadReference thread, int step);

	/**
	 * Returns the live immutable list of {@link StepRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link StepRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<StepRequest>*/ stepRequests();

	/**
	 * Creates a new {@link SuspendRequest} for the specified {@link ThreadReference}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @param thread the {@link ThreadReference} to perform the suspend on
	 * 
	 * @return a new {@link SuspendRequest} never <code>null</code>
	 */
	public SuspendRequest createSuspendRequest(ThreadReference thread);

	/**
	 * Returns the live immutable list of {@link SuspendRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link SuspendRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<SuspendRequest>*/ suspendRequests();

	/**
	 * Creates a new {@link ThreadEnterRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link ThreadEnterRequest} never <code>null</code>
	 */
	public ThreadEnterRequest createThreadEnterRequest();

	/**
	 * Returns the live immutable list of {@link ThreadEnterRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link ThreadEnterRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<ThreadEnterRequest>*/ threadEnterRequests();

	/**
	 * Creates a new {@link ThreadExitRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link ThreadExitRequest} never <code>null</code>
	 */
	public ThreadExitRequest createThreadExitRequest();

	/**
	 * Returns the live immutable list of {@link ThreadExitRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link ThreadExitRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<ThreadExitRequest>*/ threadExitRequests();

	/**
	 * Deletes the given {@link EventRequest}
	 * 
	 * @param eventRequest the {@link EventRequest} to delete, <code>null</code> is not accepted
	 */
	public void deleteEventRequest(EventRequest eventRequest);

	/**
	 * Deletes the list of {@link EventRequest}s
	 * 
	 * @param eventRequests the {@link EventRequest}s to delete, <code>null</code> is not accepted
	 */
	public void deleteEventRequest(List /*<EventRequest>*/ eventRequests);

	/**
	 * Creates a new {@link VMDeathRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link VMDeathRequest} never <code>null</code>
	 */
	public VMDeathRequest createVMDeathRequest();

	/**
	 * Returns the live immutable list of {@link VMDeathRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link VMDeathRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<VMDeathRequest>*/ vmDeathRequests();

	/**
	 * Creates a new {@link VMDisconnectRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return a new {@link VMDisconnectRequest} never <code>null</code>
	 */
	public VMDisconnectRequest createVMDisconnectRequest();

	/**
	 * Returns the live immutable list of {@link VMDisconnectRequest}s currently queued in the manager.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the list of {@link VMDisconnectRequest}s or an empty list, never <code>null</code>
	 */
	public List /*<VMDisconnectRequest>*/ vmDisconnectRequests();
}
