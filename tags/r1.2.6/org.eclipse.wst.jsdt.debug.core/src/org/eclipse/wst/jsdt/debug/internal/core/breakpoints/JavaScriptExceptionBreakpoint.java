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

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.Event;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ExceptionEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ExceptionRequest;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptThread;


/**
 * Implementation of an exception breakpoint
 * 
 * @since 1.0
 */
public class JavaScriptExceptionBreakpoint extends JavaScriptBreakpoint {
	
	public static final String MESSAGE = JavaScriptDebugPlugin.PLUGIN_ID + ".exception_message"; //$NON-NLS-1$
	
	private ExceptionRequest request = null;
	
	/**
	 * Constructor
	 * 
	 * Required for persistence / restore
	 */
	public JavaScriptExceptionBreakpoint() {}
	
	/**
	 * Constructor
	 * 
	 * @param attributes
	 * @throws DebugException
	 */
	public JavaScriptExceptionBreakpoint(final Map attributes) throws DebugException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IJavaScriptLoadBreakpoint.MARKER_ID);
				// create the marker
				setMarker(marker);

				// add attributes
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(true));
				Integer nochar = new Integer(-1);
				attributes.put(IMarker.CHAR_START, nochar);
				attributes.put(IMarker.CHAR_END, nochar);

				ensureMarker().setAttributes(attributes);

				// add to breakpoint manager if requested
				register(false);
			}
		};
		run(getMarkerRule(ResourcesPlugin.getWorkspace().getRoot()), wr);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IBreakpoint#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return JavaScriptDebugModel.MODEL_ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#addToTarget(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget)
	 */
	public void addToTarget(JavaScriptDebugTarget target) throws CoreException {
		if (target.isTerminated() || shouldSkipBreakpoint()) {
			return;
		}
		if(request == null) {
			request = target.getVM().eventRequestManager().createExceptionRequest();
			request.setEnabled(true);
			addRequestForTarget(target, request);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#deregisterRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest)
	 */
	protected void deregisterRequest(JavaScriptDebugTarget target, EventRequest request) {
		target.removeJSDIEventListener(this, request);
		this.request = null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#createRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference)
	 */
	protected boolean createRequest(JavaScriptDebugTarget target, ScriptReference script) throws CoreException {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#handleEvent(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public boolean handleEvent(Event event, JavaScriptDebugTarget target, boolean suspendVote, EventSet eventSet) {
		if(event instanceof ExceptionEvent) {
			try {
				ExceptionEvent eevent = (ExceptionEvent) event;
				setAttribute(MESSAGE, eevent.message());
				JavaScriptThread thread = target.findThread(eevent.thread());
				if (thread != null) {
					thread.addBreakpoint(this);
					return false;
				}
			}
			catch(CoreException ce) {
				JavaScriptDebugPlugin.log(ce);
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#eventSetComplete(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public void eventSetComplete(Event event, JavaScriptDebugTarget target, boolean suspend, EventSet eventSet) {
		if(event instanceof ExceptionEvent) {
			JavaScriptThread thread = target.findThread(((ExceptionEvent) event).thread());
			if (thread != null) {
				thread.suspendForException(this);
			}
		}
	}
}
