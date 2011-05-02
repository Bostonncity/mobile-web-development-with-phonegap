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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.Messages;

public class BreakpointHitCountAction implements IObjectActionDelegate, IActionDelegate2 {

	private static final String INITIAL_VALUE= "1"; //$NON-NLS-1$

	/**
	 * A dialog that sets the focus to the text area.
	 */
	class HitCountDialog extends InputDialog {
		
		private boolean fHitCountEnabled;
		
		/**
		 * Constructor
		 * @param parentShell
		 * @param initialValue
		 */
		protected  HitCountDialog(Shell parentShell, String initialValue) {
			super(parentShell, Messages.set_bp_hit_count, Messages.enter_new_hit_count, initialValue, validator);
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.InputDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			Composite area= (Composite)super.createDialogArea(parent);
			final Button checkbox = new Button(area, SWT.CHECK);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			checkbox.setLayoutData(data);
			checkbox.setFont(parent.getFont());
			checkbox.setText(Messages.enable_hit_count); 
			checkbox.setSelection(true);
			fHitCountEnabled = true;
			checkbox.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					fHitCountEnabled = checkbox.getSelection();
					getText().setEnabled(fHitCountEnabled);
					if (fHitCountEnabled) {
						validateInput();
					} else {
						setErrorMessage(null); 
					}
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			
			return area;
		}

		protected boolean isHitCountEnabled() {
			return fHitCountEnabled;
		}
	}

	
	/**
	 * Default input validator
	 */
	static IInputValidator validator = new IInputValidator() {
		int hitCount= -1;
		public String isValid(String value) {
			try {
				hitCount= Integer.valueOf(value.trim()).intValue();
			} catch (NumberFormatException nfe) {
				hitCount= -1;
			}
			if (hitCount < 1) {
				return Messages.hit_count_must_be_positive; 
			}
			//no error
			return null;
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection selection = getCurrentSelection();
		if (selection == null) {
			return;
		}
		Iterator itr= selection.iterator();
		if (!itr.hasNext()) {
			return;
		}

		while (itr.hasNext()) {
			IJavaScriptBreakpoint breakpoint= (IJavaScriptBreakpoint)itr.next();
			try {
				int oldHitCount= breakpoint.getHitCount();
				int newHitCount= hitCountDialog(breakpoint);
				if (newHitCount != -1) {					
					if (oldHitCount == newHitCount && newHitCount == 0) {
						return;
					}
					breakpoint.setHitCount(newHitCount);
				}
			} catch (CoreException ce) {
				JavaScriptDebugUIPlugin.log(ce); 
			}
		}
	}
	
	/**
	 * Returns the currently selected item(s) from the current workbench page or <code>null</code>
	 * if the current active page could not be resolved.
	 * @return the currently selected item(s) or <code>null</code>
	 */
	protected IStructuredSelection getCurrentSelection() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			ISelection selection= page.getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection)selection;
			}	
		}
		return null;
	}
	
	/**
	 * Presents the hit count dialog to the user to change the current hit count, or to set a new one
	 * @param breakpoint
	 * @return the dialog status
	 */
	protected int hitCountDialog(IJavaScriptBreakpoint breakpoint) {
		int currentHitCount = 0;
		try {
			currentHitCount = breakpoint.getHitCount();
		} catch (CoreException e) {
			JavaScriptDebugUIPlugin.log(e);
		}
		String initialValue = INITIAL_VALUE;
		if (currentHitCount > 0) {
			initialValue = Integer.toString(currentHitCount);
		}
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		HitCountDialog dialog = new HitCountDialog(activeShell, initialValue);
		if (dialog.open() == Window.OK && dialog.isHitCountEnabled()) {
			return Integer.parseInt(dialog.getValue().trim());
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
