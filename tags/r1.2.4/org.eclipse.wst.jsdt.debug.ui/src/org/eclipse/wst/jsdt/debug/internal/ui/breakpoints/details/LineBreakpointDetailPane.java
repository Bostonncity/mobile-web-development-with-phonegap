/*******************************************************************************
 *  Copyright (c) 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.details;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.Messages;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.AbstractJavaScriptBreakpointEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.CompositeBreakpointEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.JavaScriptBreakpointConditionEditor;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.editors.StandardJavaScriptBreakpointEditor;

/**
 * Detail pane for editing a line breakpoint.
 *
 * @since 1.0
 */
public class LineBreakpointDetailPane extends AbstractDetailPane {
	
	/**
	 * Identifier for this detail pane editor
	 */
	public static final String PANE_ID = JavaScriptDebugUIPlugin.PLUGIN_ID + ".line_breakpoint_detail_pane"; //$NON-NLS-1$
	
	public LineBreakpointDetailPane() {
		super(Messages.breakpoint_condition, Messages.breakpoint_condition, PANE_ID); 
		addAutosaveProperties(new int[]{
				JavaScriptBreakpointConditionEditor.PROP_CONDITION_ENABLED,
				JavaScriptBreakpointConditionEditor.PROP_CONDITION_SUSPEND_POLICY,
				StandardJavaScriptBreakpointEditor.PROP_HIT_COUNT_ENABLED,
				StandardJavaScriptBreakpointEditor.PROP_SUSPEND_POLICY});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.details.AbstractDetailPane#createEditor(org.eclipse.swt.widgets.Composite)
	 */
	protected AbstractJavaScriptBreakpointEditor createEditor(Composite parent) {
		return new CompositeBreakpointEditor(
			new AbstractJavaScriptBreakpointEditor[] {new StandardJavaScriptBreakpointEditor(), new JavaScriptBreakpointConditionEditor()}); 
	}

}
