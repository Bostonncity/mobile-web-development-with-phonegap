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
package org.eclipse.wst.jsdt.debug.internal.ui.actions;


import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.Messages;
 
/**
 * Toggles whether a breakpoint suspends a VM or only
 * the event thread.
 * 
 * @since 1.0
 */
public class BreakpointSuspendPolicyToggleAction extends BreakpointToggleAction {

	/**
	 * What the current policy of the action is
	 */
	private int fCurrentPolicy = IJavaScriptBreakpoint.SUSPEND_THREAD;
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#doAction(org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	public void doAction(IJavaScriptBreakpoint breakpoint) throws CoreException {
		if(breakpoint.getSuspendPolicy() != fCurrentPolicy) {
			breakpoint.setSuspendPolicy(fCurrentPolicy);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#getToggleState(org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	protected boolean getToggleState(IJavaScriptBreakpoint breakpoint) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#isEnabledFor(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public boolean isEnabledFor(IStructuredSelection selection) {
		Iterator iter= selection.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (!(element instanceof IJavaScriptBreakpoint)) {
				return false;
			}
			
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		if (action.isEnabled()) {
			IJavaScriptBreakpoint bp = (IJavaScriptBreakpoint)((IStructuredSelection)selection).getFirstElement();
			update(action, bp);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#isToggleAction()
	 */
	protected boolean isToggleAction() {
		return false;
	}

	/**
	 * @param action
	 * @param breakpoint
	 */
	public void update(IAction action, IJavaScriptBreakpoint breakpoint) {
		try {
			if (breakpoint.getSuspendPolicy() == IJavaScriptBreakpoint.SUSPEND_THREAD) {
				action.setText(Messages.suspend_target); 
				fCurrentPolicy = IJavaScriptBreakpoint.SUSPEND_TARGET;
			} else {
				action.setText(Messages.suspend_thread); 
				fCurrentPolicy = IJavaScriptBreakpoint.SUSPEND_THREAD;
			}
		} catch (CoreException e) {
			 JavaScriptDebugUIPlugin.log(e);
		}
	}	
}
