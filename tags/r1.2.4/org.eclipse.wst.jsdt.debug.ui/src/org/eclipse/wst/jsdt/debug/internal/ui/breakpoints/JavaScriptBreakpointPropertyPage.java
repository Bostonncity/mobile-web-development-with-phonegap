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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.IHelpContextIds;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.SWTFactory;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.AbstractJavaScriptBreakpointEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.CompositeBreakpointEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.FunctionBreakpointEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.JavaScriptBreakpointConditionEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.StandardJavaScriptBreakpointEditor;
import org.eclipse.wst.jsdt.ui.JavaScriptElementLabelProvider;

/**
 * Abstract class for breakpoint property pages
 * 
 * @since 1.0
 */
public class JavaScriptBreakpointPropertyPage extends PropertyPage {

	public static String PAGE_ID = "org.eclipse.wst.jsdt.debug.ui.breakpoints.propertypage"; //$NON-NLS-1$
	
	protected Button enabledbutton;
	protected Button hitcountbutton;
	protected Text hitcounttext;
	protected Combo suspendpolicycombo;
	protected List errors = new ArrayList();
	protected String fPrevMessage = null;
	AbstractJavaScriptBreakpointEditor editor = null;
	JavaScriptElementLabelProvider labelprovider = new JavaScriptElementLabelProvider(JavaScriptElementLabelProvider.SHOW_DEFAULT);
	
	/**
	 * @return the underlying {@link IJavaScriptBreakpoint} element this page was opened on
	 */
	protected IJavaScriptBreakpoint getBreakpoint() {
		return (IJavaScriptBreakpoint) getElement();
	}
	
	/**
	 * Allows subclasses to add type specific labels to the common Java
	 * breakpoint page.
	 * @param parent
	 */
	protected void createTypeSpecificLabels(Composite parent) {
		IJavaScriptBreakpoint jb = getBreakpoint();
		if (jb instanceof IJavaScriptLineBreakpoint) {
			IJavaScriptLineBreakpoint breakpoint = (IJavaScriptLineBreakpoint) jb;
			StringBuffer lineNumber = new StringBuffer(4);
			try {
				int lNumber = breakpoint.getLineNumber();
				if (lNumber > 0) {
					lineNumber.append(lNumber);
				}
			} catch (CoreException ce) {
				JavaScriptDebugUIPlugin.log(ce);
			}
			if (lineNumber.length() > 0) {
				SWTFactory.createLabel(parent, Messages.line_number, 1); 
				Text text = SWTFactory.createSingleText(parent, SWT.READ_ONLY, 1, lineNumber.toString());
				GridData gd = (GridData) text.getLayoutData();
				gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
				gd.grabExcessHorizontalSpace = false;
				gd.widthHint = 50;
				text.setBackground(parent.getBackground());
			}
			try {
				IMember member = BreakpointHelper.getMember(breakpoint);
				if (member == null) {
					return;
				}
				String label = Messages.member; 
				if (breakpoint instanceof IJavaScriptFunctionBreakpoint) {
					label = Messages.fuction; 
				}
				SWTFactory.createLabel(parent, label, 1);
				Text text = SWTFactory.createSingleText(parent, SWT.READ_ONLY, 1, labelprovider.getText(member));
				text.setBackground(parent.getBackground());
			} 
			catch (CoreException exception) {
				JavaScriptDebugUIPlugin.log(exception);
			}
		}
	}
	
	/**
	 * Allows subclasses to add type specific editors to the common Java
	 * breakpoint page.
	 * @param parent
	 */
	protected void createTypeSpecificEditors(Composite parent) {
		try {
			String type = getBreakpoint().getMarker().getType();
			if (IJavaScriptLoadBreakpoint.MARKER_ID.equals(type)) {
				setTitle(Messages.script_load_breakpoint);
				this.editor = new StandardJavaScriptBreakpointEditor();
			} else if (IJavaScriptLineBreakpoint.MARKER_ID.equals(type)) {
				setTitle(Messages.line_breakpoint);
				this.editor = new CompositeBreakpointEditor(new AbstractJavaScriptBreakpointEditor[]
				    {new StandardJavaScriptBreakpointEditor(), new JavaScriptBreakpointConditionEditor()}); 
			} else if (IJavaScriptFunctionBreakpoint.MARKER_ID.equals(type)) {
				setTitle(Messages.function_breakpoint);
				this.editor = new CompositeBreakpointEditor(new AbstractJavaScriptBreakpointEditor[] 
				    {new FunctionBreakpointEditor(), new JavaScriptBreakpointConditionEditor()});
			}
			this.editor.createControl(parent);
			this.editor.addPropertyListener(new IPropertyListener() {
				public void propertyChanged(Object source, int propId) {
					IStatus status = JavaScriptBreakpointPropertyPage.this.editor.getStatus();
					if (status.isOK()) {
						if (fPrevMessage != null) {
							removeErrorMessage(fPrevMessage);
							fPrevMessage = null;
						}
					} else {
						fPrevMessage = status.getMessage();
						addErrorMessage(fPrevMessage);
					}
				}
			});
			this.editor.setInput(getBreakpoint());
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}
	
	/**
	 * Stores the values configured in this page. This method
	 * should be called from within a workspace runnable to
	 * reduce the number of resource deltas.
	 */
	protected void doStore() throws CoreException {
		IJavaScriptBreakpoint breakpoint = getBreakpoint();
		breakpoint.setEnabled(enabledbutton.getSelection());
		if(this.editor != null) {
			this.editor.doSave();
		}
	}
	
	/**
	 * Creates the button to toggle enablement of the breakpoint
	 * @param parent
	 */
	protected void createEnabledButton(Composite parent)  {
		enabledbutton = SWTFactory.createCheckButton(parent, Messages.enabled, null, false, 1); 
		try {
			enabledbutton.setSelection(getBreakpoint().isEnabled());
		}
		catch(CoreException ce) {
			JavaScriptDebugUIPlugin.log(ce);
		}
	}
	
	/**
	 * Creates the labels displayed for the breakpoint.
	 * @param parent
	 */
	protected void createLabels(Composite parent) {
		Composite labelComposite = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_HORIZONTAL);
		try {
			String name = getBreakpoint().getTypeName();
			if (name != null) {
				SWTFactory.createLabel(labelComposite, Messages.type_name, 1); 
				Text text = SWTFactory.createText(labelComposite, SWT.READ_ONLY | SWT.SINGLE, 1, GridData.FILL_HORIZONTAL);
				text.setText(name);
				text.setBackground(parent.getBackground());
			}
			name = getBreakpoint().getScriptPath();
			if(name != null) {
				SWTFactory.createLabel(labelComposite, Messages.script_path, 1);
				Text text = SWTFactory.createText(labelComposite, SWT.READ_ONLY, 1, GridData.FILL_HORIZONTAL);
				text.setText(name);
				text.setBackground(parent.getBackground());
			}
			createTypeSpecificLabels(labelComposite);
		} catch (CoreException ce) {
			JavaScriptDebugUIPlugin.log(ce);
		}
	}
	
	/**
	 * Adds the given error message to the errors currently displayed on this page.
	 * The page displays the most recently added error message.
	 * Clients should retain messages that are passed into this method as the
	 * message should later be passed into removeErrorMessage(String) to clear the error.
	 * This method should be used instead of setErrorMessage(String).
	 * @param message the error message to display on this page.
	 */
	protected void addErrorMessage(String message) {
		errors.remove(message);
		errors.add(message);
		setErrorMessage(message);
		setValid(message == null);
	}
	
	/**
	 * Removes the given error message from the errors currently displayed on this page.
	 * When an error message is removed, the page displays the error that was added
	 * before the given message. This is akin to popping the message from a stack.
	 * Clients should call this method instead of setErrorMessage(null).
	 * @param message the error message to clear
	 */
	protected void removeErrorMessage(String message) {
		errors.remove(message);
		if (errors.isEmpty()) {
			addErrorMessage(null);
		} else {
			addErrorMessage((String) errors.get(errors.size() - 1));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		setTitle(Messages.script_load_bp);
		noDefaultAndApplyButton();
		Composite comp = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH);
		createLabels(comp);
		SWTFactory.createHorizontalSpacer(comp, 1);
		createEnabledButton(comp);
		createTypeSpecificEditors(comp);
		setValid(true);
		setControl(parent);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				doStore();
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null, IWorkspace.AVOID_UPDATE, null);
		} 
		catch (CoreException e) {
			JavaScriptDebugUIPlugin.log(e);
		}
		return super.performOk();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.JAVASCRIPT_BREAKPOINT_PROPERTY_PAGE);
	}
}
