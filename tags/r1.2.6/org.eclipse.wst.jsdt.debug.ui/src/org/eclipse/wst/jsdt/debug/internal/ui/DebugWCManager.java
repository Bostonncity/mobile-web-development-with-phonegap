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
package org.eclipse.wst.jsdt.debug.internal.ui;

import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.WorkingCopyManager;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;

/**
 * Delegate class to get working copies from the {@link JavaScriptUI} {@link WorkingCopyManager}.
 * 
 * @since 1.1
 */
public class DebugWCManager {

	/**
	 * Returns the working copy for the given editor input
	 * 
	 * @param input
	 * @param primaryOnly
	 * @return the given {@link IJavaScriptUnit} for the editor input or <code>null</code> is none
	 */
	public static IJavaScriptUnit getCompilationUnit(IEditorInput input, boolean primaryOnly) {
		return ((WorkingCopyManager)JavaScriptUI.getWorkingCopyManager()).getWorkingCopy(input, primaryOnly);
	}
}
