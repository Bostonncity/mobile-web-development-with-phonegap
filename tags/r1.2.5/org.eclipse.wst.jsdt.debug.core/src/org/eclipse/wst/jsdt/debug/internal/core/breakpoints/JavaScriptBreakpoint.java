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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.BreakpointEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.Event;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ScriptLoadEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ScriptLoadRequest;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.core.Constants;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.model.IJavaScriptEventListener;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptThread;
import org.eclipse.wst.jsdt.debug.internal.core.model.Script;

/**
 * Abstract representation of a JSDI breakpoint
 * 
 * @since 1.0
 */
public abstract class JavaScriptBreakpoint extends Breakpoint implements IJavaScriptBreakpoint, IJavaScriptEventListener {

	/**
	 * The total count of all of the targets this breakpoint is installed in
	 */
	public static final String INSTALL_COUNT = JavaScriptDebugPlugin.PLUGIN_ID + ".install_count"; //$NON-NLS-1$

	private HashSet targets = null;
	private HashMap requestspertarget = new HashMap(4);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.Breakpoint#setMarker(org.eclipse.core.resources.IMarker)
	 */
	public void setMarker(IMarker marker) throws CoreException {
		super.setMarker(marker);
		// reset the install count
		setAttribute(INSTALL_COUNT, 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.Breakpoint#setAttribute(java.lang.String, boolean)
	 */
	protected void setAttribute(final String attributeName, final boolean value) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				ensureMarker().setAttribute(attributeName, value);
			}
		};
		workspace.run(runnable, getMarkerRule(), IWorkspace.AVOID_UPDATE, null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.Breakpoint#setAttribute(java.lang.String, java.lang.Object)
	 */
	protected void setAttribute(final String attributeName, final Object value) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				ensureMarker().setAttribute(attributeName, value);
			}
		};
		workspace.run(runnable, getMarkerRule(), IWorkspace.AVOID_UPDATE, null);
	}
	
	/**
	 * Add this breakpoint to the breakpoint manager, or sets it as unregistered.
	 */
	protected void register(boolean register) throws CoreException {
		DebugPlugin plugin = DebugPlugin.getDefault();
		if (plugin != null && register) {
			plugin.getBreakpointManager().addBreakpoint(this);
		} else {
			setRegistered(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return JavaScriptDebugModel.MODEL_ID;
	}

	/**
	 * Returns whether this breakpoint should be "skipped". Breakpoints are skipped if the breakpoint manager is disabled and the breakpoint is registered with the manager
	 * 
	 * @return whether this breakpoint should be skipped
	 */
	public boolean shouldSkipBreakpoint() throws CoreException {
		DebugPlugin plugin = DebugPlugin.getDefault();
		return plugin != null && isRegistered() && !plugin.getBreakpointManager().isEnabled();
	}

	/**
	 * Adds the given debug target to the listing of targets that this breakpoint cares about
	 * 
	 * @param target
	 * @throws CoreException
	 */
	public void addToTarget(JavaScriptDebugTarget target) throws CoreException {
		if (target.isTerminated() || shouldSkipBreakpoint()) {
			return;
		}
		// Add script load handler
		ScriptLoadRequest request = target.getEventRequestManager().createScriptLoadRequest();
		request.setEnabled(isEnabled());
		registerRequest(target, request);

		// Add to all loaded scripts
		String scriptPath = getScriptPath();
		if (scriptPath == null) {
			return;
		}
		
		try {
			List/* ScriptReference */scripts = target.underlyingScripts(Script.resolveName(URIUtil.fromString(scriptPath)));
			boolean success = true;
			for (Iterator iter = scripts.iterator(); iter.hasNext();) {
				ScriptReference script = (ScriptReference) iter.next();
				if (scriptPathMatches(script))
					success &= createRequest(target, script);
			}
			if (success) {
				if (this.targets == null) {
					this.targets = new HashSet();
				}
				this.targets.add(target);
			}
		}
		catch(URISyntaxException urise) {
			JavaScriptDebugPlugin.log(urise);
		}
	}

	/**
	 * Creates a request for the given script in the given target
	 * 
	 * @param target
	 *            the target to register with
	 * @param script
	 *            the script we want to set the breakpoint in
	 * @return true if a new request was created false otherwise
	 * @throws CoreException
	 */
	protected abstract boolean createRequest(JavaScriptDebugTarget target, ScriptReference script) throws CoreException;

	/**
	 * Configures the request with attributes from the breakpoint
	 * 
	 * @param request
	 * @throws CoreException
	 */
	protected void configureRequest(BreakpointRequest request) throws CoreException {
		request.addHitCountFilter(getHitCount());
	}

	/**
	 * Removes the given debug target from the listing of targets this breakpoint cares about
	 * 
	 * @param target
	 */
	public void removeFromTarget(JavaScriptDebugTarget target) {
		List requests = getRequests(target);
		if (requests != null) {
			for (Iterator iter = requests.iterator(); iter.hasNext();) {
				EventRequest request = null;
				try {
					request = (EventRequest) iter.next();
					if (target.isAvailable()) {
						target.getEventRequestManager().deleteEventRequest(request);
					}
				} finally {
					deregisterRequest(target, request);
				}
			}
		}
		synchronized (this.requestspertarget) {
			this.requestspertarget.remove(target);
		}
		if (this.targets == null) {
			return;
		}
		this.targets.remove(target);
	}

	/**
	 * Recreate this breakpoint in all targets
	 * 
	 * @throws CoreException
	 */
	protected void recreateBreakpoint() throws CoreException {
		DebugPlugin plugin = DebugPlugin.getDefault();
		if (plugin != null) {
			IDebugTarget[] targets = plugin.getLaunchManager().getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (targets[i].getModelIdentifier().equals(JavaScriptDebugModel.MODEL_ID)) {
					recreateBreakpointFor((JavaScriptDebugTarget) targets[i]);
				}
			}
		}
	}

	/**
	 * Recreates this breakpoint in the given debug target
	 * 
	 * @param target
	 * @throws CoreException
	 */
	void recreateBreakpointFor(JavaScriptDebugTarget target) throws CoreException {
		if (target.isAvailable() && target.getBreakpoints().contains(this)) {
			removeFromTarget(target);
			addToTarget(target);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.Breakpoint#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) throws CoreException {
		if(isEnabled() != enabled) {
			setAttribute(ENABLED, enabled);
			recreateBreakpoint();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#setSuspendPolicy(int)
	 */
	public void setSuspendPolicy(int policy) throws CoreException {
		if(getSuspendPolicy() != policy) {
			setAttribute(SUSPEND_POLICY, policy);
			recreateBreakpoint();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#getSuspendPolicy()
	 */
	public int getSuspendPolicy() throws CoreException {
		return ensureMarker().getAttribute(SUSPEND_POLICY, SUSPEND_THREAD);
	}

	/**
	 * Returns if this breakpoint is currently installed in any targets
	 * 
	 * @return true if this breakpoint is installed in any targets, false otherwise
	 * @throws CoreException
	 */
	public boolean isInstalled() throws CoreException {
		return ensureMarker().getAttribute(INSTALL_COUNT, 0) > 0;
	}

	/**
	 * Increments the install count of this breakpoint
	 */
	protected void incrementInstallCount() throws CoreException {
		int count = getInstallCount();
		ensureMarker().setAttribute(INSTALL_COUNT, count + 1);
	}

	/**
	 * Returns the <code>INSTALL_COUNT</code> attribute of this breakpoint or 0 if the attribute is not set.
	 */
	public int getInstallCount() throws CoreException {
		return ensureMarker().getAttribute(INSTALL_COUNT, 0);
	}

	/**
	 * Decrements the install count of this breakpoint.
	 */
	protected void decrementInstallCount() throws CoreException {
		int count = getInstallCount();
		if (count > 0) {
			ensureMarker().setAttribute(INSTALL_COUNT, count - 1);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#setHitCount(int)
	 */
	public void setHitCount(int count) throws CoreException, IllegalArgumentException {
		if (count != getHitCount()) {
			setAttribute(HIT_COUNT, count);
			recreateBreakpoint();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#getHitCount()
	 */
	public int getHitCount() throws CoreException {
		return ensureMarker().getAttribute(HIT_COUNT, -1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#getScriptPath()
	 */
	public String getScriptPath() throws CoreException {
		return ensureMarker().getAttribute(SCRIPT_PATH, Constants.EMPTY_STRING);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#getTypeName()
	 */
	public String getTypeName() throws CoreException {
		return ensureMarker().getAttribute(TYPE_NAME, null);
	}

	/**
	 * Registers the given request for this breakpoint for the given target
	 * 
	 * @param target
	 * @param request
	 */
	protected void registerRequest(JavaScriptDebugTarget target, EventRequest request) {
		addRequestForTarget(target, request);
		if (!(request instanceof ScriptLoadRequest)) {
			try {
				incrementInstallCount();
			} catch (CoreException ce) {
				JavaScriptDebugPlugin.log(ce);
			}
		}
	}

	/**
	 * Adds the given request to the list of requests for the given target
	 * @param target
	 * @param request
	 */
	protected synchronized void addRequestForTarget(JavaScriptDebugTarget target, EventRequest request) {
		ArrayList requests = getRequests(target);
		if (requests == null) {
			synchronized (this.requestspertarget) {
				requests = new ArrayList(2);
				this.requestspertarget.put(target, requests);
			}
		}
		requests.add(request);
		target.addJSDIEventListener(this, request);
	}

	/**
	 * Returns any existing requests associated with the given target
	 * 
	 * @param target
	 * @return list of requests for the given target
	 */
	protected ArrayList getRequests(JavaScriptDebugTarget target) {
		ArrayList list = null;
		synchronized (this.requestspertarget) {
			list = (ArrayList) this.requestspertarget.get(target);
		}
		return list;
	}

	/**
	 * Remove this breakpoint as an event listener
	 * 
	 * @param target
	 * @param request
	 */
	protected void deregisterRequest(JavaScriptDebugTarget target, EventRequest request) {
		target.removeJSDIEventListener(this, request);
		if (!(request instanceof ScriptLoadRequest)) {
			try {
				decrementInstallCount();
			} catch (CoreException ce) {
				JavaScriptDebugPlugin.log(ce);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.model.IJavaScriptEventListener#handleEvent(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public boolean handleEvent(Event event, JavaScriptDebugTarget target, boolean suspendVote, EventSet eventSet) {
		// get the thread and suspend it
		if (event instanceof BreakpointEvent) {
			JavaScriptThread thread = target.findThread(((BreakpointEvent) event).thread());
			if (thread != null) {
				return !thread.suspendForBreakpoint(this, suspendVote);
			}
		}
		if (event instanceof ScriptLoadEvent) {
			ScriptLoadEvent sevent = (ScriptLoadEvent) event;
			ScriptReference script = sevent.script();
			
			try {
				if (scriptPathMatches(script)) 
					createRequest(target, script);
			} catch (CoreException ce) {
				JavaScriptDebugPlugin.log(ce);
			}
		}
		return true;
	}

	protected boolean scriptPathMatches(ScriptReference script) throws CoreException {
		URI sourceURI = script.sourceURI();
		if ("file".equals(sourceURI.getScheme())) {//$NON-NLS-1$			
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			URI workspaceURI = workspaceRoot.getRawLocationURI();			
			sourceURI = workspaceURI.relativize(sourceURI);
		}
		return getScriptPath().equals(sourceURI.toString());
	}

	/**
	 * Returns if the type names for the breakpoint are equal or not. Two <code>null</code> type names are considered to be equal.
	 * 
	 * @param tname1
	 * @param tname2
	 * @return true if the type names are equal, false otherwise
	 */
	boolean typeNamesEqual(String tname1, String tname2) {
		if (tname1 == null && tname2 == null) {
			return true;
		}
		return tname1 != null && tname1.equals(tname2);
	}

	/**
	 * Custom comparison to avoid the leading separator issue from saying the paths are not equal
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	boolean pathsEqual(IPath p1, IPath p2) {
		if (p1.segmentCount() == p2.segmentCount()) {
			String[] segments = p1.segments();
			for (int i = 0; i < segments.length; i++) {
				if (!segments[i].equals(p2.segment(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.model.IJavaScriptEventListener#eventSetComplete(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public void eventSetComplete(Event event, JavaScriptDebugTarget target, boolean suspend, EventSet eventSet) {
		if (event instanceof BreakpointEvent) {
			JavaScriptThread thread = target.findThread(((BreakpointEvent) event).thread());
			if (thread != null) {
				thread.suspendForBreakpointComplete(this, suspend, eventSet);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#setJavaScriptElementHandle(java.lang.String)
	 */
	public void setJavaScriptElementHandle(String handle) throws CoreException {
		ensureMarker().setAttribute(ELEMENT_HANDLE, handle);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint#getJavaScriptElementHandle()
	 */
	public String getJavaScriptElementHandle() throws CoreException {
		return ensureMarker().getAttribute(ELEMENT_HANDLE, null);
	}
}
