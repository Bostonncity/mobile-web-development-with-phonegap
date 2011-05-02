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
package org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.details;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.IDetailPane;
import org.eclipse.debug.ui.IDetailPaneFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.Messages;

/**
 * Detail pane factory for Java breakpoints.
 * 
 * @since 1.0
 */
public class BreakpointDetailPaneFactory implements IDetailPaneFactory {
	
	/**
	 * Maps pane IDs to names
	 */
	private Map fNameMap;

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDetailPaneTypes(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public Set getDetailPaneTypes(IStructuredSelection selection) {
		HashSet set = new HashSet();
		if (selection.size() == 1) {
			IBreakpoint b = (IBreakpoint) selection.getFirstElement();
			try {
				String type = b.getMarker().getType();
				if (IJavaScriptLineBreakpoint.MARKER_ID.equals(type)) {
					set.add(LineBreakpointDetailPane.PANE_ID);
				} else if (IJavaScriptFunctionBreakpoint.MARKER_ID.equals(type)) {
					set.add(FunctionBreakpointDetailPane.PANE_ID);
				} else {
					set.add(StandardBreakpointDetailPane.PANE_ID);
				}
			} catch (CoreException e) {}
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDefaultDetailPane(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public String getDefaultDetailPane(IStructuredSelection selection) {
		if (selection.size() == 1) {
			IBreakpoint b = (IBreakpoint) selection.getFirstElement();
			try {
				String type = b.getMarker().getType();
				if (IJavaScriptLineBreakpoint.MARKER_ID.equals(type)) {
					return LineBreakpointDetailPane.PANE_ID;
				} else if (IJavaScriptFunctionBreakpoint.MARKER_ID.equals(type)) {
					return FunctionBreakpointDetailPane.PANE_ID;
				} else {
					return StandardBreakpointDetailPane.PANE_ID;
				}
			} catch (CoreException e) {}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#createDetailPane(java.lang.String)
	 */
	public IDetailPane createDetailPane(String paneID) {
		if (LineBreakpointDetailPane.PANE_ID.equals(paneID)) {
			return new LineBreakpointDetailPane();
		}
		if (StandardBreakpointDetailPane.PANE_ID.equals(paneID)) {
			return new StandardBreakpointDetailPane();
		}
		if (FunctionBreakpointDetailPane.PANE_ID.equals(paneID)) {
			return new FunctionBreakpointDetailPane();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDetailPaneName(java.lang.String)
	 */
	public String getDetailPaneName(String paneID) {
		return (String) getNameMap().get(paneID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDetailPaneDescription(java.lang.String)
	 */
	public String getDetailPaneDescription(String paneID) {
		return (String) getNameMap().get(paneID);
	}
	
	private Map getNameMap() {
		if (fNameMap == null) {
			fNameMap = new HashMap();
			fNameMap.put(LineBreakpointDetailPane.PANE_ID, Messages.line_breakpoint_settings);
			fNameMap.put(FunctionBreakpointDetailPane.PANE_ID, Messages.function_breakpoint_settings);
			fNameMap.put(StandardBreakpointDetailPane.PANE_ID, Messages.breakpoint_settings);
		}
		return fNameMap;
	}

}
