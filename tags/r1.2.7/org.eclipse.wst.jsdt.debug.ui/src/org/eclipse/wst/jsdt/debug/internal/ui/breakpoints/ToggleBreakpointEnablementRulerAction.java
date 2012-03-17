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
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.RulerBreakpointAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;

/**
 * Action that allows the enabled state of the breakpoint to be toggled from the compilation
 * unit editor
 * 
 * @since 1.0
 */
public class ToggleBreakpointEnablementRulerAction extends RulerBreakpointAction implements IUpdate {

	private IJavaScriptBreakpoint breakpoint = null;
	
	/**
	 * Constructor
	 * @param editor
	 * @param info
	 */
	public ToggleBreakpointEnablementRulerAction(ITextEditor editor, IVerticalRulerInfo info) {
		super(editor, info);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	public String getText() {
		if(this.breakpoint != null) {
			try {
				return (this.breakpoint.isEnabled() ? Messages.disable_breakpoint : Messages.enable_breakpoint);
			} catch (CoreException e) {
				JavaScriptDebugUIPlugin.log(e);
			}
		}
		return super.getText();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if(this.breakpoint != null) {
			try {
				this.breakpoint.setEnabled(!this.breakpoint.isEnabled());
			} catch (CoreException e) {
				JavaScriptDebugUIPlugin.log(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		this.breakpoint = null;
		IBreakpoint bp = getBreakpoint();
		if (bp != null && (bp instanceof IJavaScriptBreakpoint)) {
			this.breakpoint = (IJavaScriptBreakpoint) bp;
			try {
				if(this.breakpoint.isEnabled()) {
					setText(Messages.disable_breakpoint);
				}
				else {
					setText(Messages.enable_breakpoint);
				}
			} catch (CoreException e) {
				JavaScriptDebugUIPlugin.log(e);
				setText(Messages.disable_breakpoint);
				setEnabled(false);
			}
		}
		else {
			setText(Messages.disable_breakpoint);
		}
		setEnabled(this.breakpoint != null);
	}
}
