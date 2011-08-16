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
package org.eclipse.wst.jsdt.debug.core.jsdi;

import java.util.List;

import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventQueue;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager;

/**
 * Abstract description of a VM. 
 * This interface is used to abstract the platform model from the underlying connection / debugger protocol.
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface VirtualMachine {
	
	public final int DEFAULT_TIMEOUT = 30000;
	
	/**
	 * Sends a resume request to the VM
	 */
	public void resume();

	/**
	 * Sends a suspend request to the VM
	 */
	public void suspend();

	/**
	 * Terminates and disconnects the VM
	 */
	public void terminate();

	/**
	 * Returns the name of the {@link VirtualMachine}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the name of the {@link VirtualMachine} or <code>null</code>
	 */
	public String name();

	/**
	 * Returns the description of the {@link VirtualMachine}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the description of the {@link VirtualMachine} or <code>null</code>
	 */
	public String description();

	/**
	 * Returns the version string of the {@link VirtualMachine}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the version string of the {@link VirtualMachine} or <code>null</code>
	 */
	public String version();

	/**
	 * Returns the live list of {@link ThreadReference}s in the {@link VirtualMachine} 
	 * or an empty list.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the live list of {@link ThreadReference}s in the {@link VirtualMachine} 
	 * or an empty list, never <code>null</code>
	 */
	public List /*<ThreadReference>*/ allThreads();

	/**
	 * Returns the live list of {@link ScriptReference}s loaded in the {@link VirtualMachine} 
	 * or an empty list.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the live list of loaded {@link ScriptReference}s 
	 * or an empty list, never <code>null</code>
	 */
	public List /*<ScriptReference>*/ allScripts();

	/**
	 * Disposes the {@link VirtualMachine} and cleans up held objects
	 */
	public void dispose();

	/**
	 * Returns the {@link UndefinedValue} value.
	 * 
	 * @return the {@link UndefinedValue} value
	 */
	public UndefinedValue mirrorOfUndefined();

	/**
	 * Returns the {@link NullValue} value.
	 * 
	 * @return the {@link NullValue} value.
	 */
	public NullValue mirrorOfNull();

	/**
	 * Returns a new {@link BooleanValue}.
	 * 
	 * @param bool the boolean primitive to mirror in the {@link VirtualMachine}
	 * 
	 * @return a new {@link BooleanValue}
	 */
	public BooleanValue mirrorOf(boolean bool);

	/**
	 * Returns a new {@link NumberValue}.
	 * 
	 * @param number the {@link Number} to mirror in the {@link VirtualMachine}
	 * 
	 * @return a new {@link NumberValue}
	 */
	public NumberValue mirrorOf(Number number);

	/**
	 * Returns a {@link StringValue} initialized to the given {@link String}.
	 * 
	 * @param string the initial value for the mirrored {@link StringValue}
	 * 
	 * @return a new {@link StringValue}
	 */
	public StringValue mirrorOf(String string);

	/**
	 * Returns the {@link EventRequestManager} associated with this {@link VirtualMachine}.<br>
	 * <br>
	 * This method can return <code>null</code> if the {@link VirtualMachine} has been disposed or terminated.
	 * 
	 * @return the {@link EventRequestManager} for this {@link VirtualMachine} or <code>null</code>
	 */
	public EventRequestManager eventRequestManager();

	/**
	 * Returns the {@link EventQueue} associated with this {@link VirtualMachine}.<br>
	 * <br>
	 * This method can return <code>null</code> if the {@link VirtualMachine} has been disposed or terminated.
	 * 
	 * @return the {@link EventQueue} for this {@link VirtualMachine} or <code>null</code>
	 */
	public EventQueue eventQueue();
}
