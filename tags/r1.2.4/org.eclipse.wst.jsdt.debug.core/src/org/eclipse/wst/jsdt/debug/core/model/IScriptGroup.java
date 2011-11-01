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
package org.eclipse.wst.jsdt.debug.core.model;

import java.util.List;

import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;

/**
 * Container for a grouping of {@link ScriptReference}s
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IScriptGroup {

	/**
	 * Returns the array of all {@link ScriptReference}s from the backing {@link IJavaScriptDebugTarget}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the complete list of {@link ScriptReference}s from the backing {@link IJavaScriptDebugTarget} 
	 * or an empty array, never <code>null</code>
	 */
	public List/*<IScript>*/ allScripts();
}
