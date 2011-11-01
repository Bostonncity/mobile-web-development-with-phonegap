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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.wst.jsdt.debug.internal.ui.SWTFactory;

/**
 * Combines fEditors.
 * 
 * @since 1.0
 */
public class CompositeBreakpointEditor extends AbstractJavaScriptBreakpointEditor {
	
	private AbstractJavaScriptBreakpointEditor[] fEditors;
	
	/**
	 * Constructor
	 * @param fEditors
	 */
	public CompositeBreakpointEditor(AbstractJavaScriptBreakpointEditor[] editors) {
		this.fEditors = editors;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#addPropertyListener(org.eclipse.ui.IPropertyListener)
	 */
	public void addPropertyListener(IPropertyListener listener) {
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].addPropertyListener(listener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#removePropertyListener(org.eclipse.ui.IPropertyListener)
	 */
	public void removePropertyListener(IPropertyListener listener) {
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].removePropertyListener(listener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#dispose()
	 */
	protected void dispose() {
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].dispose();
		}
		fEditors = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH, 0, 0);
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].createControl(comp);
		}
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#setFocus()
	 */
	public void setFocus() {
		fEditors[0].setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#doSave()
	 */
	public void doSave() throws CoreException {
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].doSave();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#isDirty()
	 */
	public boolean isDirty() {
		for (int i = 0; i < fEditors.length; i++) {
			if (fEditors[i].isDirty()) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#getStatus()
	 */
	public IStatus getStatus() {
		for (int i = 0; i < fEditors.length; i++) {
			IStatus status = fEditors[i].getStatus();
			if (!status.isOK()) {
				return status;
			}
		}
		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#getInput()
	 */
	public Object getInput() {
		return fEditors[0].getInput();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#setInput(java.lang.Object)
	 */
	public void setInput(Object breakpoint) throws CoreException {
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].setInput(breakpoint);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.AbstractJavaScriptBreakpointEditor#setMnemonics(boolean)
	 */
	public void setMnemonics(boolean mnemonics) {
		super.setMnemonics(mnemonics);
		for (int i = 0; i < fEditors.length; i++) {
			fEditors[i].setMnemonics(mnemonics);
		}
	}
}
