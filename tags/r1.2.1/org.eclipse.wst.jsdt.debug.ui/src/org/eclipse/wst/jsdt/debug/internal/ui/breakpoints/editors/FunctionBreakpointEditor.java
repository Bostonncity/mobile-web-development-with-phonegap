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
package org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.SWTFactory;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.Messages;

/**
 * Editor for method fEntry/fExit breakpoint.
 * 
 * @since 1.0
 */
public class FunctionBreakpointEditor extends StandardJavaScriptBreakpointEditor {
	
	// Method fEntry/fExit editors
	private Button fEntry;
	private Button fExit;

	public static final int PROP_ENTRY = 0x1012;
	public static final int PROP_EXIT = 0x1013;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.StandardJavaScriptBreakpointEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 2, 1, 0, 0, 0);
		// add standard controls
		super.createStandardControls(composite);
		Composite watchComp = SWTFactory.createComposite(composite, parent.getFont(), 3, 1, 0, 0, 0);
		fEntry = createSusupendPropertyEditor(watchComp, processMnemonics(Messages.entry), PROP_ENTRY);
		fExit = createSusupendPropertyEditor(watchComp, processMnemonics(Messages.exit), PROP_EXIT); 
		return composite;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.StandardJavaScriptBreakpointEditor#setBreakpoint(org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint)
	 */
	protected void setBreakpoint(IJavaScriptBreakpoint breakpoint) throws CoreException {
		super.setBreakpoint(breakpoint);
		if (breakpoint instanceof IJavaScriptFunctionBreakpoint) {
			IJavaScriptFunctionBreakpoint fbp = (IJavaScriptFunctionBreakpoint) breakpoint;
			fEntry.setEnabled(true);
			fExit.setEnabled(true);
			fEntry.setSelection(fbp.isEntry());
			fExit.setSelection(fbp.isExit());
		} else {
			fEntry.setEnabled(false);
			fExit.setEnabled(false);
			fEntry.setSelection(false);
			fExit.setSelection(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.StandardJavaScriptBreakpointEditor#doSave()
	 */
	public void doSave() throws CoreException {
		super.doSave();
		IJavaScriptBreakpoint breakpoint = getBreakpoint();
		if (breakpoint instanceof IJavaScriptFunctionBreakpoint) {
			IJavaScriptFunctionBreakpoint fpb = (IJavaScriptFunctionBreakpoint) breakpoint;
			fpb.setEntry(fEntry.getSelection());
			fpb.setExit(fExit.getSelection());
		}
	}
}
