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
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;

/**
 * Default implementation of a line breakpoint
 * 
 * @since 1.0
 */
public class JavaScriptLineBreakpoint extends JavaScriptBreakpoint implements IJavaScriptLineBreakpoint {

	/**
	 * The condition for the breakpoint
	 */
	public static final String CONDITION = JavaScriptDebugPlugin.PLUGIN_ID + ".condition"; //$NON-NLS-1$
	/**
	 * If the condition is enabled for the breakpoint or not - allows us to keep conditions even if they are not used
	 */
	public static final String CONDITION_ENABLED = JavaScriptDebugPlugin.PLUGIN_ID + ".condition_enabled"; //$NON-NLS-1$
	/**
	 * If the breakpoint should suspend when the condition evaluates to true
	 */
	public static final String CONDITION_SUSPEND_ON_TRUE = JavaScriptDebugPlugin.PLUGIN_ID + ".condition_suspend_on_true"; //$NON-NLS-1$

	/**
	 * Constructor
	 */
	public JavaScriptLineBreakpoint() {
		// needed for restoring breakpoints via extension point contributions
	}

	/**
	 * Constructor
	 * 
	 * @param resource
	 * @param linenumber
	 * @param charstart
	 * @param charend
	 * @param attributes
	 * @param register
	 * @throws DebugException
	 */
	public JavaScriptLineBreakpoint(final IResource resource, final int linenumber, final int charstart, final int charend, final Map attributes, final boolean register) throws DebugException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {

				// create the marker
				setMarker(resource.createMarker(IJavaScriptLineBreakpoint.MARKER_ID));

				// add attributes
				attributes.put(IBreakpoint.ID, getModelIdentifier());
				attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(true));
				attributes.put(IMarker.LINE_NUMBER, new Integer(linenumber));
				attributes.put(IMarker.CHAR_START, new Integer(charstart));
				attributes.put(IMarker.CHAR_END, new Integer(charend));

				ensureMarker().setAttributes(attributes);

				// add to breakpoint manager if requested
				register(register);
			}
		};
		run(getMarkerRule(resource), wr);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#createRequest(org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget, org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference)
	 */
	protected boolean createRequest(JavaScriptDebugTarget target, ScriptReference script) throws CoreException {
		Location loc = script.lineLocation(getLineNumber());
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
	 * @see org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint#configureRequest(org.eclipse.wst.jsdt.debug.core.jsdi.request.BreakpointRequest)
	 */
	protected void configureRequest(BreakpointRequest request) throws CoreException {
		super.configureRequest(request);
		request.addConditionFilter(getCondition());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILineBreakpoint#getLineNumber()
	 */
	public int getLineNumber() throws CoreException {
		return ensureMarker().getAttribute(IMarker.LINE_NUMBER, -1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILineBreakpoint#getCharStart()
	 */
	public int getCharStart() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_START, -1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILineBreakpoint#getCharEnd()
	 */
	public int getCharEnd() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_END, -1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint#isConditionEnabled()
	 */
	public boolean isConditionEnabled() throws CoreException {
		return ensureMarker().getAttribute(CONDITION_ENABLED, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint#isConditionSuspendOnTrue()
	 */
	public boolean isConditionSuspendOnTrue() throws CoreException {
		return ensureMarker().getAttribute(CONDITION_SUSPEND_ON_TRUE, true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint#setConditionSuspendOnTrue(boolean)
	 */
	public void setConditionSuspendOnTrue(boolean suspendontrue) throws CoreException {
		if (suspendontrue != isConditionSuspendOnTrue()) {
			setAttribute(CONDITION_SUSPEND_ON_TRUE, suspendontrue);
			recreateBreakpoint();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint#setCondition(java.lang.String)
	 */
	public void setCondition(String condition) throws CoreException {
		if (condition != null && condition.length() == 0) {
			setAttribute(CONDITION, null);
		}
		else {
			setAttribute(CONDITION, condition);
		}
		recreateBreakpoint();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint#getCondition()
	 */
	public String getCondition() throws CoreException {
		return ensureMarker().getAttribute(CONDITION, null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint#setConditionEnabled(boolean)
	 */
	public void setConditionEnabled(boolean enabled) throws CoreException {
		if(isConditionEnabled() != enabled) {
			setAttribute(CONDITION_ENABLED, enabled);
			recreateBreakpoint();
		}
	}
}
