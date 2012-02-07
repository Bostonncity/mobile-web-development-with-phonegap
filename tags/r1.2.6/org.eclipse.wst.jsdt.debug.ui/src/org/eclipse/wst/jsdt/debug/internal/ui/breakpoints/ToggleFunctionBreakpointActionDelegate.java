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
package org.eclipse.wst.jsdt.debug.internal.ui.breakpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.actions.breakpoints.ToggleBreakpointObjectActionDelegate;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Toggle action delegate for popup menus to set function breakpoints
 * 
 * @since 1.0
 */
public class ToggleFunctionBreakpointActionDelegate extends ToggleBreakpointObjectActionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.actions.breakpoints.ToggleBreakpointObjectActionDelegate#performAction(org.eclipse.debug.ui.actions.IToggleBreakpointsTarget, org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	protected void performAction(IToggleBreakpointsTarget target, IWorkbenchPart part, ISelection selection) throws CoreException {
		((IToggleBreakpointsTargetExtension)target).toggleBreakpoints(part, selection);
	}
}
