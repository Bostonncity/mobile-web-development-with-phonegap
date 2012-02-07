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
package org.eclipse.wst.jsdt.debug.internal.core.breakpoints;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpointParticipant;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.Event;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ScriptLoadEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ScriptLoadRequest;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptPreferencesManager;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptThread;

/**
 * Breakpoint that suspends on {@link ScriptLoadEvent}s
 * 
 * @since 1.0
 */
public class JavaScriptLoadBreakpoint extends JavaScriptLineBreakpoint implements IJavaScriptLoadBreakpoint {

	/**
	 * Attribute flag to tag a script load breakpoint as a global suspend load breakpoint
	 */
	public static final String GLOBAL_SUSPEND = JavaScriptDebugPlugin.PLUGIN_ID + ".global_suspend"; //$NON-NLS-1$
	
	/**
	 * Constructor
	 */
	public JavaScriptLoadBreakpoint() {
		// used for persistence / restoration
	}

	/**
	 * Constructor
	 * 
	 * @param resource
	 * @param charstart
	 * @param charend
	 * @param attributes
	 * @param register
	 * @throws DebugException
	 */
	public JavaScriptLoadBreakpoint(final IResource resource, final int charstart, final int charend, final Map attributes, final boolean register) throws DebugException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = null;
				if(resource == null) {
					marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IJavaScriptLoadBreakpoint.MARKER_ID);
				}
				else {
					marker = resource.createMarker(IJavaScriptLoadBreakpoint.MARKER_ID);
				}
				// create the marker
				setMarker(marker);

				// add attributes
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(true));
				attributes.put(IMarker.CHAR_START, new Integer(charstart));
				attributes.put(IMarker.CHAR_END, new Integer(charend));

				ensureMarker().setAttributes(attributes);

				// add to breakpoint manager if requested
				register(register);
			}
		};
		run(getMarkerRule(ResourcesPlugin.getWorkspace().getRoot()), wr);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#handleEvent(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public boolean handleEvent(Event event, JavaScriptDebugTarget target, boolean suspendVote, EventSet eventSet) {
		if (event instanceof ScriptLoadEvent) {
			ScriptLoadEvent sevent = (ScriptLoadEvent) event;
			ScriptReference script = sevent.script();
			JavaScriptThread thread = target.findThread((sevent).thread());
			if (thread != null) {
				if(isGlobalLoadSuspend()) {
					JavaScriptPreferencesManager.setGlobalSuspendOn(script.sourceURI().toString());
					thread.addBreakpoint(this);
					return false;
				} else if(isMatchedScriptLoadSuspend(script, thread, suspendVote)){
					thread.addBreakpoint(this);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns if this breakpoint matches this script and should suspend
	 * 
	 * @param script
	 * @param thread
	 * @param suspendVote
	 * 
	 * @return <code>true</code> if we should suspend on this script load <code>false</code> otherwise
	 */
	private boolean isMatchedScriptLoadSuspend(ScriptReference script, JavaScriptThread thread, boolean suspendVote) {
		try {
			if (scriptPathMatches(script)) {
				int vote = thread.suspendForScriptLoad(this, script, suspendVote);
				return (vote & IJavaScriptBreakpointParticipant.SUSPEND) > 0 || vote == IJavaScriptBreakpointParticipant.DONT_CARE;
			}
		}
		catch(CoreException ce) {
			JavaScriptDebugPlugin.log(ce);
		}
		return false;
	}
	
	/**
	 * Returns if this breakpoint supports global suspend
	 * 
	 * @return <code>true</code> if we should suspend on all script loads <code>false</code> otherwise
	 */
	private boolean isGlobalLoadSuspend() {
		try {
			return ensureMarker().getAttribute(GLOBAL_SUSPEND, false);
		}
		catch(CoreException ce) {
			JavaScriptDebugPlugin.log(ce);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#registerRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest)
	 */
	protected void registerRequest(JavaScriptDebugTarget target, EventRequest request) {
		ArrayList requests = getRequests(target);
		if (requests == null || requests.isEmpty()) {
			// only add it once per target
			addRequestForTarget(target, request);
			try {
				if (isRegistered()) {
					incrementInstallCount();
				}
			} catch (CoreException ce) {
				JavaScriptDebugPlugin.log(ce);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#deregisterRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest)
	 */
	protected void deregisterRequest(JavaScriptDebugTarget target, EventRequest request) {
		target.removeJSDIEventListener(this, request);
		try {
			decrementInstallCount();
		} catch (CoreException ce) {
			JavaScriptDebugPlugin.log(ce);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#eventSetComplete(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public void eventSetComplete(Event event, JavaScriptDebugTarget target, boolean suspend, EventSet eventSet) {
		if (event instanceof ScriptLoadEvent) {
			JavaScriptThread thread = target.findThread(((ScriptLoadEvent) event).thread());
			if (thread != null) {
				ScriptLoadEvent sevent = (ScriptLoadEvent) event;
				thread.suspendForScriptLoadComplete(this, sevent.script(), suspend, eventSet);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptLineBreakpoint#createRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference)
	 */
	protected boolean createRequest(JavaScriptDebugTarget target, ScriptReference script) throws CoreException {
		ScriptLoadRequest request = target.getEventRequestManager().createScriptLoadRequest();
		registerRequest(target, request);
		request.setEnabled(isEnabled());
		return false;
	}
}
