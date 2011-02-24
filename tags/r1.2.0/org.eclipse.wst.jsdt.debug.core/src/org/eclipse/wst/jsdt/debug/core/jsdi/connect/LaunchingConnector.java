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
package org.eclipse.wst.jsdt.debug.core.jsdi.connect;

import java.io.IOException;
import java.util.Map;

import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;

/**
 * Description of a {@link Connector} that launches a {@link VirtualMachine}.
 * 
 * Clients can implement or extend this interface.
 * 
 * @see Argument
 * @see Connector
 * @see VirtualMachine
 * @since 1.0
 */
public interface LaunchingConnector extends Connector {
	
	/**
	 * Launches a {@link VirtualMachine} with the given map of arguments.<br>
	 * <br>
	 * This method cannot return <code>null</code>. If a {@link VirtualMachine} cannot
	 * be launched an {@link IOException} is thrown.
	 * 
	 * @param arguments the map of connection arguments which cannot be <code>null</code>
	 * 
	 * @return the {@link VirtualMachine} that has been launched never <code>null</code>
	 * @throws IOException if the {@link Connector} failed to launch
	 */
	public VirtualMachine launch(Map arguments) throws IOException;
}
