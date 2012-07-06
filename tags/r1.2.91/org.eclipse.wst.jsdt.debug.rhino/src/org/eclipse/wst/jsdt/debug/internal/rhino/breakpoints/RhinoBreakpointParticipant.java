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
package org.eclipse.wst.jsdt.debug.internal.rhino.breakpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpointParticipant;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.BooleanValue;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.Value;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptPrimitiveValue;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptThread;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptValue;
import org.eclipse.wst.jsdt.debug.internal.rhino.Constants;
import org.eclipse.wst.jsdt.debug.internal.rhino.RhinoPreferencesManager;

/**
 * Participant for Rhino
 * 
 * @since 1.0
 */
public class RhinoBreakpointParticipant implements IJavaScriptBreakpointParticipant {

	/**
	 * Constructor
	 */
	public RhinoBreakpointParticipant() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpointParticipant#breakpointHit(org.eclipse.wst.jsdt.debug.core.model.IJavaScriptThread, org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	public int breakpointHit(IJavaScriptThread thread, IJavaScriptBreakpoint breakpoint) {
		try {
			if(breakpoint instanceof IJavaScriptLineBreakpoint) {
				IJavaScriptLineBreakpoint lbp = (IJavaScriptLineBreakpoint) breakpoint;
				String condition = lbp.getCondition();
				if (condition != null) {
					// evaluate it
					if (thread.getFrameCount() < 1) {
						return DONT_SUSPEND;
					}
					IJavaScriptValue value = thread.evaluate(condition);
					if (lbp.isConditionSuspendOnTrue()) {
						if(suspendForValue(value)) {
							return SUSPEND;
						}
					} 
					if(!suspendForValue(value)) {
						return SUSPEND; 
					}
				}
				return SUSPEND;
			}
		} 
		catch (CoreException ce) {
			//ignore, just don't suspend
		}
		return DONT_SUSPEND;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpointParticipant#scriptLoaded(org.eclipse.wst.jsdt.debug.core.model.IJavaScriptThread, org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference, org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	public int scriptLoaded(IJavaScriptThread thread, ScriptReference script, IJavaScriptBreakpoint breakpoint) {
		if(URIUtil.lastSegment(script.sourceURI()).equals(Constants.STD_IN) &&
				!RhinoPreferencesManager.suspendOnStdinLoad()) {
			return DONT_SUSPEND;
		}
		return SUSPEND;
	}
	
	/**
	 * If the thread should suspend based on the given {@link Value}. Currently only suspend when the value is non-null and a {@link BooleanValue} that has the value <code>true</code>
	 * 
	 * @param value
	 * @return if we should suspend
	 */
	private boolean suspendForValue(IJavaScriptValue value) {
		if(value instanceof IJavaScriptPrimitiveValue) {
			return ((IJavaScriptPrimitiveValue)value).booleanValue();
		}
		return false;
	}
}
