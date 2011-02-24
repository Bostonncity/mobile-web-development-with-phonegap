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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;

/**
 * JavaScript function breakpoint
 * 
 * @since 1.0
 */
public class JavaScriptFunctionBreakpoint extends JavaScriptLineBreakpoint implements IJavaScriptFunctionBreakpoint {

	/**
	 * If this is a function entry breakpoint
	 */
	private static final String ENTRY = JavaScriptDebugPlugin.PLUGIN_ID + ".entry"; //$NON-NLS-1$

	/**
	 * if this is a function exit breakpoint
	 */
	private static final String EXIT = JavaScriptDebugPlugin.PLUGIN_ID + ".exit"; //$NON-NLS-1$

	/**
	 * Constructor
	 */
	public JavaScriptFunctionBreakpoint() {
		// needed for restoring breakpoints via extension point contributions
	}

	/**
	 * Constructor
	 * 
	 * @param resource
	 * @param name
	 * @param signature
	 * @param charstart
	 * @param charend
	 * @param attributes
	 * @param register
	 * @throws DebugException
	 */
	public JavaScriptFunctionBreakpoint(final IResource resource, final String name, final String signature, final int charstart, final int charend, final Map attributes, final boolean register) throws DebugException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {

				// create the marker
				setMarker(resource.createMarker(IJavaScriptFunctionBreakpoint.MARKER_ID));

				// add attributes
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(true));
				attributes.put(FUNCTION_NAME, name);
				attributes.put(FUNCTION_SIGNAURE, signature);
				attributes.put(IMarker.CHAR_START, new Integer(charstart));
				attributes.put(IMarker.CHAR_END, new Integer(charend));
				attributes.put(ENTRY, Boolean.valueOf(true));

				ensureMarker().setAttributes(attributes);

				// add to breakpoint manager if requested
				register(register);
			}
		};
		run(getMarkerRule(resource), wr);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint#getFunctionName()
	 */
	public String getFunctionName() throws CoreException {
		return ensureMarker().getAttribute(FUNCTION_NAME, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint#getSignature()
	 */
	public String getSignature() throws CoreException {
		return ensureMarker().getAttribute(FUNCTION_SIGNAURE, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptLineBreakpoint#createRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference)
	 */
	protected boolean createRequest(JavaScriptDebugTarget target, ScriptReference script) throws CoreException {
		Location loc = script.functionLocation(getFunctionName());
		if (loc == null) {
			return false;
		}
		BreakpointRequest request = target.getEventRequestManager().createBreakpointRequest(loc);
		registerRequest(target, request);
		configureRequest(request);
		request.setEnabled(isEnabled());
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint#isEntry()
	 */
	public boolean isEntry() throws CoreException {
		return ensureMarker().getAttribute(ENTRY, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint#setEntry(boolean)
	 */
	public void setEntry(boolean isentry) throws CoreException {
		if (isentry != isEntry()) {
			if(isentry && !isEnabled()) {
				setAttributes(
					new String[] {ENTRY, ENABLED}, 
					new Object[] {Boolean.valueOf(isentry), Boolean.TRUE});
			}
			else if(!isExit()) {
				setAttributes(
					new String[] {ENTRY, ENABLED}, 
					new Object[] {Boolean.valueOf(isentry), Boolean.FALSE});
			}
			else {
				setAttribute(ENTRY, isentry);
			}	
			recreateBreakpoint();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint#isExit()
	 */
	public boolean isExit() throws CoreException {
		return ensureMarker().getAttribute(EXIT, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint#setExit(boolean)
	 */
	public void setExit(boolean isexit) throws CoreException {
		if (isexit != isExit()) {
			if(isexit && !isEnabled()) {
				setAttributes(
					new String[] {EXIT, ENABLED}, 
					new Object[] {Boolean.valueOf(isexit), Boolean.TRUE});
			}
			else if(!isEntry()) {
				setAttributes(
					new String[] {EXIT, ENABLED}, 
					new Object[] {Boolean.valueOf(isexit), Boolean.FALSE});
			}
			else {
				setAttribute(EXIT, isexit);
			}
		}
	}
}
