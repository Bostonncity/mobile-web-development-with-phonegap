/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.rhino.transport;

import java.io.IOException;


/**
 * Describes a socket connection to a debugger
 * 
 * @since 1.0
 */
public interface Connection {

	/**
	 * Returns if the debug connection is open.
	 * 
	 * @return <code>true</code> if the connection is open <code>false</code> otherwise
	 */
	public abstract boolean isOpen();

	/**
	 * Closes the connection.
	 * 
	 * @throws IOException if the connection failed to close or is already closed
	 */
	public abstract void close() throws IOException;

	/**
	 * Writes the given packet to the connection
	 * 
	 * @param packet the packet to write, <code>null</code> is not accepted
	 * @throws IOException if the write failed
	 */
	public abstract void writePacket(Packet packet) throws IOException;

	/**
	 * Reads the next packet from the connection. This is non-blocking.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the next {@link Packet} from the connection
	 * @throws IOException if the connection is prematurely closed or the read failed
	 */
	public abstract Packet readPacket() throws IOException;

}