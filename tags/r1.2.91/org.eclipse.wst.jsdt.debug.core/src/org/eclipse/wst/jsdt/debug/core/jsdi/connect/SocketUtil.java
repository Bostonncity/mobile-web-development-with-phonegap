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
import java.net.ServerSocket;

import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;

/**
 * Utility class to find a port to debug on.
 * 
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class SocketUtil {
	
	/**
	 * Constructor
	 * No instantiation
	 */
	private SocketUtil() {}
	
	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free port
	 */
	public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} 
		catch (IOException e) {
			JavaScriptDebugPlugin.log(e);
		} 
		finally {
			if (socket != null) {
				try {
					socket.close();
				} 
				catch (IOException e) {
					JavaScriptDebugPlugin.log(e);
				}
			}
		}
		return -1;		
	}
	
	/**
	 * Returns a free port number encoded as a string
	 * 
	 * @return a free port number encoded as a string
	 */
	public static String findFreePortString() {
		return Integer.toString(findFreePort());
	}
}
