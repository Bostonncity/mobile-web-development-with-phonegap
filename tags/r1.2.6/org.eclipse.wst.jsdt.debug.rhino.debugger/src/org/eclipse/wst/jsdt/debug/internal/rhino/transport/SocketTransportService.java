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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of a transport service that using a {@link Socket} for communication
 * 
 * @since 1.0
 */
public class SocketTransportService implements TransportService {

	static final Class serverSocketClass = ServerSocket.class; // temporary used to pre-load the ServerSocket.class

	/**
	 * Key implementation
	 */
	public static class SocketListenerKey implements ListenerKey {

		private String address;

		public SocketListenerKey(String address) {
			this.address = address;
		}

		public String address() {
			return address;
		}
	}

	/**
	 * Map of {@link ListenerKey} to {@link ServerSocket}s
	 */
	Map listeners = new HashMap();

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.TransportService#startListening(java.lang.String)
	 */
	public synchronized ListenerKey startListening(String address) throws IOException {
		String host = null;
		int port = 0;
		if (address != null) {
			String[] strings = address.split(Constants.COLON);
			if (strings.length == 2) {
				host = strings[0];
				port = Integer.parseInt(strings[1]);
			} else {
				port = Integer.parseInt(strings[0]);
			}
		}
		if (host == null) {
			host = LOCALHOST;
		}
		if (!host.equals(LOCALHOST)) {
			throw new IllegalArgumentException("Only localhost is supported."); //$NON-NLS-1$
		}
		ListenerKey key = new SocketListenerKey(host + Constants.COLON + port);
		ServerSocket serverSocket = new ServerSocket(port);
		listeners.put(key, serverSocket);
		return key;

	};

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.TransportService#stopListening(org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.TransportService.ListenerKey)
	 */
	public void stopListening(ListenerKey key) throws IOException {
		ServerSocket serverSocket = (ServerSocket) listeners.remove(key);
		if (serverSocket != null) {
			serverSocket.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.TransportService#accept(org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.TransportService.ListenerKey, long, long)
	 */
	public Connection accept(ListenerKey key, long attachTimeout, long handshakeTimeout) throws IOException {
		ServerSocket serverSocket = (ServerSocket) listeners.get(key);
		if (serverSocket == null) {
			throw new IllegalStateException("Accept failed. Not listening for address: key.address()"); //$NON-NLS-1$
		}
		int timeout = (int) attachTimeout;
		if (timeout > 0) {
			if (timeout > Integer.MAX_VALUE) {
				timeout = Integer.MAX_VALUE; // approximately 25 days!
			}
			serverSocket.setSoTimeout(timeout);
		}
		Connection connection = new SocketConnection(serverSocket.accept());
		Packet packet = connection.readPacket();
		if (!(packet instanceof Request)) {
			throw new IOException("failure establishing_connection"); //$NON-NLS-1$
		}
		Request request = (Request) packet;
		if (!request.getCommand().equals(JSONConstants.CONNECT)) {
			throw new IOException("failure establishing connection"); //$NON-NLS-1$
		}
		Response response = new Response(request.getSequence(), request.getCommand());
		connection.writePacket(response);
		return connection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.TransportService#attach(java.lang.String, long, long)
	 */
	public Connection attach(String address, long attachTimeout, long handshakeTimeout) throws IOException {

		String host = null;
		int port = 0;
		if (address != null) {
			String[] strings = address.split(Constants.COLON);
			if (strings.length == 2) {
				host = strings[0];
				port = Integer.parseInt(strings[1]);
			} else {
				port = Integer.parseInt(strings[0]);
			}
		}
		if (host == null) {
			host = LOCALHOST;
		}
		Connection connection = new SocketConnection(new Socket(host, port));
		Request request = new Request(JSONConstants.CONNECT);
		connection.writePacket(request);

		Packet packet = connection.readPacket();
		if (!(packet instanceof Response)) {
			throw new IOException("failure establishing connection"); //$NON-NLS-1$
		}

		Response response = (Response) packet;
		if (!response.getCommand().equals(JSONConstants.CONNECT) || response.getRequestSequence() != request.getSequence() || !response.isSuccess() || !response.isRunning()) {
			throw new IOException("failure establishing connection"); //$NON-NLS-1$
		}
		return connection;
	}
}
