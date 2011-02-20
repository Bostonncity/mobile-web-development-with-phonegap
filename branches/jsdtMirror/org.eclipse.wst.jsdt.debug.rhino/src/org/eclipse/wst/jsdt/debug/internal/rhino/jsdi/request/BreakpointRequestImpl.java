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

import java.util.Map;

import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.RhinoDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.ScriptReferenceImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.DisconnectedException;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.JSONConstants;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.Request;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.Response;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.TimeoutException;

/**
 * Rhino implementation of {@link BreakpointRequest}
 * 
 * @since 1.0
 */
public class BreakpointRequestImpl extends EventRequestImpl implements BreakpointRequest {

	private final Location location;
	private ThreadReference thread;
	private String condition;
	private Long breakpointId;
	private int hitcount = 0;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param location
	 */
	public BreakpointRequestImpl(VirtualMachineImpl vm, Location location) {
		super(vm);
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest#addThreadFilter(org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference)
	 */
	public synchronized void addThreadFilter(ThreadReference thread) {
		checkDeleted();
		this.thread = thread;
	}

	/**
	 * Returns the underlying {@link ThreadReference} this request applies to
	 * 
	 * @return the underlying {@link ThreadReference}
	 */
	public synchronized ThreadReference thread() {
		return this.thread;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest#addConditionFilter(java.lang.String)
	 */
	public synchronized void addConditionFilter(String condition) {
		checkDeleted();
		this.condition = condition;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest#addHitCountFilter(int)
	 */
	public void addHitCountFilter(int hitcount) {
		checkDeleted();
		this.hitcount = hitcount;
	}

	/**
	 * Returns the condition for this breakpoint
	 * 
	 * @return the condition
	 */
	public synchronized String condition() {
		return condition;
	}

	/**
	 * Returns the hit count for the breakpoint
	 * 
	 * @return the hit count for the breakpoint
	 */
	public synchronized int hitcount() {
		return this.hitcount;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Locatable#location()
	 */
	public Location location() {
		return this.location;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.request.EventRequestImpl#setEnabled(boolean)
	 */
	public synchronized void setEnabled(boolean enabled) {
		checkDeleted();
		if (this.enabled == enabled) {
			return;
		}
		if (enabled) {
			ScriptReferenceImpl scriptReferenceImpl = (ScriptReferenceImpl) this.location.scriptReference();
			Long scriptId = scriptReferenceImpl.getScriptId();
			Request request = new Request(JSONConstants.SETBREAKPOINT);
			request.getArguments().put(JSONConstants.SCRIPT_ID, scriptId);
			request.getArguments().put(JSONConstants.CONDITION, this.condition);
			if (this.location.functionName() != null) {
				request.getArguments().put(JSONConstants.FUNCTION, this.location.functionName());
			}
			else{
				request.getArguments().put(JSONConstants.LINE, new Integer(this.location.lineNumber()));
			}
			try {
				Response response = this.vm.sendRequest(request);
				Map body = (Map) response.getBody().get(JSONConstants.BREAKPOINT);
				Number id = (Number) body.get(JSONConstants.BREAKPOINT_ID);
				this.breakpointId = new Long(id.longValue());
			} catch (TimeoutException e) {
				RhinoDebugPlugin.log(e);
			} catch (DisconnectedException e) {
				handleException(e.getMessage(), e);
			}
		} else {
			Request request = new Request(JSONConstants.CLEARBREAKPOINT);
			request.getArguments().put(JSONConstants.BREAKPOINT_ID, breakpointId);
			try {
				vm.sendRequest(request);
			} catch (TimeoutException e) {
				RhinoDebugPlugin.log(e);
			} catch (DisconnectedException e) {
				handleException(e.getMessage(), e);
			}
			breakpointId = null;
		}
		this.enabled = enabled;
	}

	/**
	 * Returns the id reported back from the underlying {@link VirtualMachine} for this breakpoint
	 * 
	 * @return the id of the breakpoint
	 */
	public Long breakpointId() {
		return this.breakpointId;
	}
}
