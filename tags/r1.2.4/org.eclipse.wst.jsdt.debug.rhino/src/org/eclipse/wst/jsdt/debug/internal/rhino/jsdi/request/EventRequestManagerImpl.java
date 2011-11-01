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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.DebuggerStatementRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ExceptionRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ScriptLoadRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.StepRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.SuspendRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ThreadEnterRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ThreadExitRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.VMDeathRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.VMDisconnectRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;

/**
 * Rhino implementation of {@link EventRequestManager}
 * 
 * @since 1.0
 */
public final class EventRequestManagerImpl implements EventRequestManager {

	private final Map eventsMap = new HashMap();
	private final List breakpointRequests = new ArrayList();
	private final List exceptionRequests = new ArrayList();
	private final List debuggerStatementRequests = new ArrayList();
	private final List scriptLoadRequests = new ArrayList();
	private final List stepRequests = new ArrayList();
	private final List suspendRequests = new ArrayList();
	private final List threadEnterRequests = new ArrayList();
	private final List threadExitRequests = new ArrayList();
	private final List vmDeathRequests = new ArrayList();
	private final List vmDisconnectRequests = new ArrayList();
	private final VirtualMachineImpl vm;

	/**
	 * Constructor
	 * 
	 * @param vm
	 */
	public EventRequestManagerImpl(VirtualMachineImpl vm) {
		this.vm = vm;
		eventsMap.put(BreakpointRequestImpl.class, breakpointRequests);
		eventsMap.put(DebuggerStatementRequestImpl.class, debuggerStatementRequests);
		eventsMap.put(ExceptionRequestImpl.class, exceptionRequests);
		eventsMap.put(ScriptLoadRequestImpl.class, scriptLoadRequests);
		eventsMap.put(StepRequestImpl.class, stepRequests);
		eventsMap.put(SuspendRequestImpl.class, suspendRequests);
		eventsMap.put(ThreadEnterRequestImpl.class, threadEnterRequests);
		eventsMap.put(ThreadExitRequestImpl.class, threadExitRequests);
		eventsMap.put(VMDeathRequestImpl.class, vmDeathRequests);
		eventsMap.put(VMDisconnectRequestImpl.class, vmDisconnectRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createBreakpointRequest(org.eclipse.wst.jsdt.debug.core.jsdi.Location)
	 */
	public synchronized BreakpointRequest createBreakpointRequest(Location location) {
		BreakpointRequestImpl request = new BreakpointRequestImpl(vm, location);
		breakpointRequests.add(request);
		return request;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#debuggerStatementRequests()
	 */
	public synchronized List debuggerStatementRequests() {
		return Collections.unmodifiableList(debuggerStatementRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createDebuggerStatementRequest()
	 */
	public synchronized DebuggerStatementRequest createDebuggerStatementRequest() {
		DebuggerStatementRequestImpl request = new DebuggerStatementRequestImpl(vm);
		debuggerStatementRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#breakpointRequests()
	 */
	public synchronized List breakpointRequests() {
		return Collections.unmodifiableList(breakpointRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createExceptionRequest()
	 */
	public synchronized ExceptionRequest createExceptionRequest() {
		ExceptionRequest request = new ExceptionRequestImpl(vm);
		exceptionRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#exceptionRequests()
	 */
	public synchronized List exceptionRequests() {
		return Collections.unmodifiableList(exceptionRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createScriptLoadRequest()
	 */
	public synchronized ScriptLoadRequest createScriptLoadRequest() {
		ScriptLoadRequest request = new ScriptLoadRequestImpl(vm);
		scriptLoadRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#scriptLoadRequests()
	 */
	public synchronized List scriptLoadRequests() {
		return Collections.unmodifiableList(scriptLoadRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createStepRequest(org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference, int)
	 */
	public synchronized StepRequest createStepRequest(ThreadReference thread, int step) {
		StepRequest request = new StepRequestImpl(vm, thread, step);
		stepRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#stepRequests()
	 */
	public synchronized List stepRequests() {
		return Collections.unmodifiableList(stepRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createSuspendRequest(org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference)
	 */
	public synchronized SuspendRequest createSuspendRequest(ThreadReference thread) {
		SuspendRequest request = new SuspendRequestImpl(vm, thread);
		suspendRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#suspendRequests()
	 */
	public synchronized List suspendRequests() {
		return Collections.unmodifiableList(suspendRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createThreadEnterRequest()
	 */
	public synchronized ThreadEnterRequest createThreadEnterRequest() {
		ThreadEnterRequest request = new ThreadEnterRequestImpl(vm);
		threadEnterRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#threadEnterRequests()
	 */
	public List threadEnterRequests() {
		return Collections.unmodifiableList(threadEnterRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createThreadExitRequest()
	 */
	public synchronized ThreadExitRequest createThreadExitRequest() {
		ThreadExitRequest request = new ThreadExitRequestImpl(vm);
		threadExitRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#threadExitRequests()
	 */
	public List threadExitRequests() {
		return Collections.unmodifiableList(threadExitRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#deleteEventRequest(org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest)
	 */
	public synchronized void deleteEventRequest(EventRequest eventRequest) {
		Class clazz = eventRequest.getClass();
		List eventList = (List) eventsMap.get(clazz);
		if (eventList == null) {
			throw new IllegalArgumentException(
					"bad event request class - " + clazz.getName()); //$NON-NLS-1$
		}
		eventList.remove(eventRequest);
		EventRequestImpl eventRequestImpl = (EventRequestImpl) eventRequest;
		eventRequestImpl.delete();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#deleteEventRequest(java.util.List)
	 */
	public synchronized void deleteEventRequest(List eventRequests) {
		for (Iterator iterator = eventRequests.iterator(); iterator.hasNext();) {
			EventRequest eventRequest = (EventRequest) iterator.next();
			deleteEventRequest(eventRequest);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createVMDeathRequest()
	 */
	public VMDeathRequest createVMDeathRequest() {
		VMDeathRequest request = new VMDeathRequestImpl(this.vm);
		this.vmDeathRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#vmDeathRequests()
	 */
	public List vmDeathRequests() {
		return Collections.unmodifiableList(vmDeathRequests);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#createVMDisconnectRequest()
	 */
	public VMDisconnectRequest createVMDisconnectRequest() {
		VMDisconnectRequest request = new VMDisconnectRequestImpl(this.vm);
		this.vmDisconnectRequests.add(request);
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager#vmDisconnectRequests()
	 */
	public List vmDisconnectRequests() {
		return Collections.unmodifiableList(vmDisconnectRequests);
	}
}
