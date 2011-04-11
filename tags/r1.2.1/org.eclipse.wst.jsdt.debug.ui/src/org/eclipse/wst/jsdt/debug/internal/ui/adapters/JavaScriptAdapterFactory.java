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
package org.eclipse.wst.jsdt.debug.internal.ui.adapters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory2;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.core.model.IScriptGroup;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.ToggleBreakpointAdapter;

/**
 * Adapter factory
 * 
 * @since 1.0
 */
public class JavaScriptAdapterFactory implements IAdapterFactory {

	class WorkbenchAdapter implements IWorkbenchAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object o) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
		 */
		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
		 */
		public String getLabel(Object o) {
			if(o instanceof IJavaScriptBreakpoint) {
				try {
					return ((IJavaScriptBreakpoint)o).getScriptPath();
				}
				catch(CoreException ce) {
					JavaScriptDebugUIPlugin.log(ce);
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
		 */
		public Object getParent(Object o) {
			return null;
		}
	}
	
	static IWorkbenchAdapter wadapter = null;
	static ToggleBreakpointAdapter tbadapter = null;
	static JavaScriptAsyncContentProvider jscontent = null;
	static JavaScriptModelProxyFactory jsproxyfactory = null;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType.equals(IToggleBreakpointsTarget.class)) {
			return getToggleBreakpointAdapter();
		}
		if(adapterType.equals(IWorkbenchAdapter.class) && adaptableObject instanceof IJavaScriptBreakpoint) {
			return getWorkbenchAdapter();
		}
		if (adapterType.equals(IElementContentProvider.class)) {
			if (adaptableObject instanceof IJavaScriptDebugTarget ||
					adaptableObject instanceof IScriptGroup) {
				return getJSContentProvider();
			}
		}
		if(adapterType.equals(IModelProxyFactory2.class)) {
			return getJSProxyFactory();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] {IToggleBreakpointsTarget.class, 
				IWorkbenchAdapter.class, 
				IElementContentProvider.class,
				IModelProxyFactory2.class};
	}
	
	/**
	 * @return the singleton {@link JavaScriptModelProxyFactory}
	 */
	synchronized JavaScriptModelProxyFactory getJSProxyFactory() {
		if(jsproxyfactory == null) {
			jsproxyfactory = new JavaScriptModelProxyFactory();
		}
		return jsproxyfactory;
	}
	
	/**
	 * @return the singleton {@link JavaScriptAsyncContentProvider}
	 */
	synchronized JavaScriptAsyncContentProvider getJSContentProvider() {
		if(jscontent == null) {
			jscontent = new JavaScriptAsyncContentProvider();
		}
		return jscontent;
	}
	
	/**
	 * @return the singleton {@link IWorkbenchAdapter}
	 */
	synchronized IWorkbenchAdapter getWorkbenchAdapter() {
		if(wadapter == null) {
			wadapter = new WorkbenchAdapter();
		}
		return wadapter;
	}
	
	/**
	 * @return the singleton {@link ToggleBreakpointAdapter}
	 */
	synchronized ToggleBreakpointAdapter getToggleBreakpointAdapter() {
		if(tbadapter == null) {
			tbadapter = new ToggleBreakpointAdapter();
		}
		return tbadapter;
	}
}
