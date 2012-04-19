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
 * Description of a {@link VirtualMachine} {@link Connector} that attaches to a 
 * running {@link VirtualMachine}.
 * 
 * Clients can implement or extend this interface.
 * 
 * @see Argument
 * @see Connector
 * @see VirtualMachine
 * @since 1.0
 */
public interface AttachingConnector extends Connector {
	
	/**
	 * Attaches to a {@link VirtualMachine} described by the
	 * given map of arguments.<br>
	 * <br>
	 * This method cannot return <code>null</code>. If a connection cannot be made 
	 * an {@link IOException} is thrown.
	 * 
	 * @param arguments the map of connection arguments which cannot be <code>null</code>
	 * @see BooleanArgument
	 * @see IntegerArgument
	 * @see StringArgument
	 * @see SelectedArgument
	 * 
	 * @return the {@link VirtualMachine} that has been connected to never <code>null</code>
	 * @throws IOException if the {@link Connector} failed to connect
	 */
	public VirtualMachine attach(Map arguments) throws IOException;
}
