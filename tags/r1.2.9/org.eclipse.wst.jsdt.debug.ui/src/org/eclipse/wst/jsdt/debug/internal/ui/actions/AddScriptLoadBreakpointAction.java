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

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.Messages;
import org.eclipse.wst.jsdt.debug.internal.ui.dialogs.ScriptSelectionDialog;

/**
 * Command in the breakpoints view to add script load breakpoints
 * @since 1.0
 */
public class AddScriptLoadBreakpointAction implements IViewActionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			ScriptSelectionDialog dialog = new ScriptSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					false, 
					ResourcesPlugin.getWorkspace().getRoot());
			dialog.setTitle(Messages.add_script_load_bp);
			if(dialog.open() == IDialogConstants.OK_ID) {
				final IFile file = (IFile) dialog.getFirstResult();
				final String scriptname = file.getName();
				final String scriptpath = file.getFullPath().makeRelative().toString();
				
				IJavaScriptLoadBreakpoint breakpoint = findBreakpoint(scriptpath, scriptname);
				if(breakpoint != null) {
					breakpoint.setEnabled(true);			
					return;
				}
				//spawn a job to create a new one
				Job job = new Job(Messages.creating_script_load_bp) {
					protected IStatus run(IProgressMonitor monitor) {
						HashMap attributes = new HashMap();
						attributes.put(IJavaScriptBreakpoint.TYPE_NAME, scriptname);
						attributes.put(IJavaScriptBreakpoint.SCRIPT_PATH, scriptpath);
						try {
							JavaScriptDebugModel.createScriptLoadBreakpoint(file, -1, -1, attributes, true);
						}
						catch(DebugException de) {
							JavaScriptDebugUIPlugin.log(de);
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}
				};
				job.setPriority(Job.INTERACTIVE);
				job.setSystem(true);
				job.schedule();
			}
		}
		catch(CoreException ce) {
			JavaScriptDebugUIPlugin.log(ce);
		}
	}
	
	/**
	 * Returns an existing script load breakpoint from the manager or <code>null</code> if one is not
	 * found.
	 * @param scriptpath
	 * @param scriptname
	 * @return the existing breakpoint or <code>null</code>
	 * @throws CoreException
	 */
	IJavaScriptLoadBreakpoint findBreakpoint(String scriptpath, String scriptname) throws CoreException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(JavaScriptDebugModel.MODEL_ID);
		IJavaScriptLoadBreakpoint breakpoint = null;
		for (int i = 0; i < breakpoints.length; i++) {
			if (breakpoints[i] instanceof IJavaScriptLoadBreakpoint) {
				breakpoint = (IJavaScriptLoadBreakpoint) breakpoints[i];
				if (breakpoint.getTypeName().equals(scriptname) &&
						breakpoint.getScriptPath().equals(scriptpath)) {
					return breakpoint;
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {}
}
