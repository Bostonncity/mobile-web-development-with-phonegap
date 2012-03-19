/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.rhino.transport;


/**
 * A {@link DebugSession} controls a {@link PacketSendManager} and {@link PacketReceiveManager}
 * that is uses to communicate to a debug target using the underlying {@link Connection}
 * 
 * @since 1.0
 */
public class DebugSession {
	
	/**
	 * The default receive manager
	 */
	private final PacketReceiveManager packetReceiveManager;
	/**
	 * The default send manager
	 */
	private final PacketSendManager packetSendManager;

	/**
	 * Constructor
	 * 
	 * Starts the send / receive managers on the given connection.
	 * 
	 * @param connection
	 */
	public DebugSession(Connection connection) {
		packetReceiveManager = new PacketReceiveManager(connection);
		Thread receiveThread = new Thread(packetReceiveManager, "Debug Session - Receive Manager"); //$NON-NLS-1$
		receiveThread.setDaemon(true);

		packetSendManager = new PacketSendManager(connection);
		Thread sendThread = new Thread(packetSendManager, "Debug Session - Send Manager"); //$NON-NLS-1$
		sendThread.setDaemon(true);

		packetReceiveManager.setPartnerThread(sendThread);
		packetSendManager.setPartnerThread(receiveThread);

		receiveThread.start();
		sendThread.start();
	}

	/**
	 * Stops the debug sessions and disconnects the send / receive managers
	 */
	public void dispose() {
		packetReceiveManager.disconnect();
		packetSendManager.disconnect();
	}

	/**
	 * Sends the given {@link Request} using the underlying {@link PacketSendManager}.
	 * 
	 * @param request the request to send, <code>null</code> is not accepted
	 * @throws DisconnectedException if the {@link PacketSendManager} has been disconnected
	 */
	public void sendRequest(Request request) throws DisconnectedException {
		if(request == null) {
			throw new IllegalArgumentException("You cannot send a null request"); //$NON-NLS-1$
		}
		packetSendManager.sendPacket(request);
	}

	/**
	 * Sends the given {@link EventPacket} using the underlying {@link PacketSendManager}.
	 * 
	 * @param event the event to send, <code>null</code> is not accepted
	 * @throws DisconnectedException if the {@link PacketSendManager} has been disconnected
	 */
	public void sendEvent(EventPacket event) throws DisconnectedException {
		if(event == null) {
			throw new IllegalArgumentException("You cannot send a null event"); //$NON-NLS-1$
		}
		packetSendManager.sendPacket(event);
	}
	
	/**
	 * Sends the given {@link Response} using the underlying {@link PacketSendManager}.
	 * 
	 * @param response the response to send, <code>null</code> is not accepted
	 * @throws DisconnectedException if the {@link PacketSendManager} has been disconnected
	 */
	public void sendResponse(Response response) throws DisconnectedException {
		if(response == null) {
			throw new IllegalArgumentException("You cannot send a null response"); //$NON-NLS-1$
		}
		packetSendManager.sendPacket(response);
	}
	
	/**
	 * Waits for the given timeout for a {@link Response} response in the given sequence 
	 * from the {@link PacketReceiveManager}.<br>
	 * <br>
	 * This method does not return <code>null</code> - one of the listed exceptions will be thrown 
	 * if an {@link Response} cannot be returned.
	 * 
	 * @param timeout the amount of time in milliseconds to wait to a {@link Response}
	 * @return a new {@link Request} from the {@link PacketReceiveManager} never <code>null</code>
	 * @throws TimeoutException if the timeout lapses with no {@link Response} returned
	 * @throws DisconnectedException if the {@link PacketReceiveManager} has been disconnected
	 */
	public Response receiveResponse(int requestSequence, int timeout) throws TimeoutException, DisconnectedException {
		return packetReceiveManager.getResponse(requestSequence, timeout);
	}

	/**
	 * Waits for the given timeout for a {@link EventPacket} response from the {@link PacketReceiveManager}.<br>
	 * <br>
	 * This method does not return <code>null</code> - one of the listed exceptions will be thrown 
	 * if an {@link EventPacket} cannot be returned.
	 * 
	 * @param timeout the amount of time in milliseconds to wait to a {@link EventPacket}
	 * @return a new {@link Request} from the {@link PacketReceiveManager} never <code>null</code>
	 * @throws TimeoutException if the timeout lapses with no {@link EventPacket} returned
	 * @throws DisconnectedException if the {@link PacketReceiveManager} has been disconnected
	 */
	public EventPacket receiveEvent(int timeout) throws TimeoutException, DisconnectedException {
		return (EventPacket) packetReceiveManager.getCommand(EventPacket.TYPE, timeout);
	}
	
	/**
	 * Waits for the given timeout for a request response from the {@link PacketReceiveManager}.<br>
	 * <br>
	 * This method does not return <code>null</code> - one of the listed exceptions will be thrown 
	 * if a {@link Request} cannot be returned.
	 * 
	 * @param timeout the amount of time in milliseconds to wait to a {@link Request}
	 * @return a new {@link Request} from the {@link PacketReceiveManager} never <code>null</code>
	 * @throws TimeoutException if the timeout lapses with no {@link Request} returned
	 * @throws DisconnectedException if the {@link PacketReceiveManager} has been disconnected
	 */
	public Request receiveRequest(int timeout) throws TimeoutException, DisconnectedException {
		return (Request) packetReceiveManager.getCommand(JSONConstants.REQUEST, timeout);
	}

}
