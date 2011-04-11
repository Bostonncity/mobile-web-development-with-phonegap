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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;

/**
 * Action to toggle the entry attribute of an {@link IJavaScriptFunctionBreakpoint}
 * 
 * @since 1.0
 */
public class EntryToggleAction extends BreakpointToggleAction {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#getToggleState(org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	protected boolean getToggleState(IJavaScriptBreakpoint breakpoint) throws CoreException {
		return ((IJavaScriptFunctionBreakpoint)breakpoint).isEntry();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#doAction(org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	public void doAction(IJavaScriptBreakpoint breakpoint) throws CoreException {
		((IJavaScriptFunctionBreakpoint)breakpoint).setEntry(!((IJavaScriptFunctionBreakpoint)breakpoint).isEntry());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.BreakpointToggleAction#isEnabledFor(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public boolean isEnabledFor(IStructuredSelection selection) {
		Iterator iter= selection.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (!(element instanceof IJavaScriptFunctionBreakpoint)) {
				return false;
			}
		}
		return true;
	}
}

