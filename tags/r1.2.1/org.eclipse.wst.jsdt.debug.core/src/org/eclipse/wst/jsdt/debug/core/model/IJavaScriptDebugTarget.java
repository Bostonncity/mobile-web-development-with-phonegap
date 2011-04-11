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

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;

/**
 * Description of our debug target
 * 
 * @see ScriptReference
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaScriptDebugTarget extends IDebugTarget {

	/**
	 * Returns the entire collection of {@link IScript}s currently known to the {@link VirtualMachine}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the complete collection of {@link IScript}s from the {@link VirtualMachine} or an 
	 * empty collection, never <code>null</code>
	 */
	public List/*<IScript>*/ allScripts();
	
	/**
	 * Returns all of the {@link IScript}s currently loaded in the {@link VirtualMachine} that have the
	 * matching name.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @param name
	 * @return the complete list of {@link IScript}s loaded in the {@link VirtualMachine} with the given name
	 * or an empty collection, never <code>null</code>
	 */
	public List/*<IScript>*/ allScriptsByName(String name);
	
	/**
	 * Returns the singleton {@link IScriptGroup} for this target.<br>
	 * <br>
	 * This method can return <code>null</code> if the target has been disconnected or terminated
	 * 
	 * @return the singleton {@link IScriptGroup} for this target
	 */
	public IScriptGroup getScriptGroup();
	
}
