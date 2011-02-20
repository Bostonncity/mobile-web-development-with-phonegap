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
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.ITypeRoot;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;

/**
 * Collection of utility methods to help get additional information
 * from breakpoints
 * 
 * @since 1.0
 */
public class BreakpointHelper {

	/**
	 * Returns the member associated with the line number of
	 * the given breakpoint.
	 * 
	 * @param breakpoint JavaScript line breakpoint
	 * @return member at the given line number in the type 
	 *  associated with the breakpoint
	 * @exception CoreException if an exception occurs accessing
	 *  the breakpoint
	 */
	public static IMember getMember(IJavaScriptLineBreakpoint breakpoint) throws CoreException {
		if (breakpoint instanceof IJavaScriptFunctionBreakpoint) {
			return getMethod((IJavaScriptFunctionBreakpoint)breakpoint);
		}		
		int start = breakpoint.getCharStart();
		int end = breakpoint.getCharEnd();
		
		IType type = getType(breakpoint);
		if (start == -1 && end == -1) {
			start = breakpoint.getCharStart();
			end = breakpoint.getCharEnd();
		}
		
		IMember member = null;
		if ((type != null && type.exists()) && (end >= start) && (start >= 0)) {
			member = binSearch(type, start, end);
		}
		if (member == null) {
			member= type;
		}
		return member;
	}
	
	/**
	 * Returns the function associated with the function
	 * breakpoint.
	 * 
	 * @param breakpoint JavaScript function breakpoint
	 * @return function
	 */
	public static IFunction getMethod(IJavaScriptFunctionBreakpoint breakpoint) {	
		String handle = breakpoint.getModelIdentifier();
		if (handle != null) {
			IJavaScriptElement jse = JavaScriptCore.create(handle);
			if (jse != null) {
				if (jse instanceof IFunction) {
					return (IFunction)jse;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the type that the given JavaScript breakpoint refers to
	 * 
	 * @param breakpoint JavaScript breakpoint
	 * @return the type the breakpoint is associated with
	 */
	public static IType getType(IJavaScriptBreakpoint breakpoint) {
		String handle = breakpoint.getModelIdentifier();
		if (handle != null) {
			IJavaScriptElement jse = JavaScriptCore.create(handle);
			if (jse != null) {
				if (jse instanceof IType) {
					return (IType)jse;
				}
				if (jse instanceof IMember) {
					return ((IMember)jse).getDeclaringType();
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the type that the given JavaScript breakpoint refers to
	 * 
	 * @param breakpoint JavaScript breakpoint
	 * @return the type the breakpoint is associated with
	 */
	public static ITypeRoot getTypeRoot(IJavaScriptBreakpoint breakpoint) {
		try {
			String handle = breakpoint.getJavaScriptElementHandle();
			if (handle != null) {
				IJavaScriptElement jse = JavaScriptCore.create(handle);
				if (jse != null && jse.exists()) {
					if (jse instanceof IType) {
						return ((IType)jse).getTypeRoot();
					}
					if (jse instanceof IMember) {
						return ((IMember)jse).getTypeRoot();
					}
				}
			}
		}
		catch(CoreException ce) {
			JavaScriptDebugUIPlugin.log(ce);
		}
		return null;
	}
	
	/**
	 * Searches the given source range of the container for a member that is
	 * not the same as the given type.
	 */
	static IMember binSearch(IType type, int start, int end) throws JavaScriptModelException {
		IJavaScriptElement je = getElementAt(type, start);
		if (je != null && !je.equals(type)) {
			return asMember(je);
		}
		if (end > start) {
			je = getElementAt(type, end);
			if (je != null && !je.equals(type)) {
				return asMember(je);
			}
			int mid = ((end - start) / 2) + start;
			if (mid > start) {
				je = binSearch(type, start + 1, mid);
				if (je == null) {
					je = binSearch(type, mid + 1, end - 1);
				}
				return asMember(je);
			}
		}
		return null;
	}
	
	/**
	 * Returns the given JavaScript element if it is an
	 * <code>IMember</code>, otherwise <code>null</code>.
	 * 
	 * @param element JavaScript element
	 * @return the given element if it is a type member,
	 * 	otherwise <code>null</code>
	 */
	static IMember asMember(IJavaScriptElement element) {
		if (element instanceof IMember) {
			return (IMember)element;
		}
		return null;		
	}
	
	/**
	 * Returns the element at the given position in the given type
	 */
	static IJavaScriptElement getElementAt(IType type, int pos) throws JavaScriptModelException {
		if (type.isBinary()) {
			return type.getClassFile().getElementAt(pos);
		}
		return type.getJavaScriptUnit().getElementAt(pos);
	}
}
