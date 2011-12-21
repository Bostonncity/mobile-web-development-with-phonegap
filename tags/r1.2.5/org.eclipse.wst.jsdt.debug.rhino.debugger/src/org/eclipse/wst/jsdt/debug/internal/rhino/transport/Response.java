/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.rhino.transport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link Response} implementation using JSON
 * 
 * @since 1.0
 */
public class Response extends Packet {

	private final String command;
	private final int requestSequence;
	private final Map body = Collections.synchronizedMap(new HashMap());
	private volatile boolean success = true;
	private volatile boolean running = true;
	private volatile String message;

	/**
	 * Constructor
	 * 
	 * @param requestSequence the sequence
	 * @param command the command, <code>null</code> is not accepted
	 */
	public Response(int requestSequence, String command) {
		super(JSONConstants.RESPONSE);
		if(command == null) {
			throw new IllegalArgumentException("The command string for a response packet cannot be null"); //$NON-NLS-1$
		}
		this.requestSequence = requestSequence;
		this.command = command.intern();
	}

	/**
	 * Constructor
	 * 
	 * @param json the JSON map for a response, <code>null</code> is not accepted
	 */
	public Response(Map json) {
		super(json);
		Number packetRequestSeq = (Number) json.get(JSONConstants.REQUEST_SEQ);
		requestSequence = packetRequestSeq.intValue();

		String packetCommand = (String) json.get(JSONConstants.COMMAND);
		command = packetCommand.intern();

		Map packetBody = (Map) json.get(JSONConstants.BODY);
		body.putAll(packetBody);

		Boolean packetSuccess = (Boolean) json.get(JSONConstants.SUCCESS);
		success = packetSuccess.booleanValue();

		Boolean packetRunning = (Boolean) json.get(JSONConstants.RUNNING);
		running = packetRunning.booleanValue();

		message = (String) json.get(JSONConstants.MESSAGE);
	}

	/**
	 * Returns the request sequence
	 * 
	 * @return the request sequence
	 */
	public int getRequestSequence() {
		return requestSequence;
	}

	/**
	 * Returns the underlying command.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the underlying command, never <code>null</code>
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the body of the command.<br>
	 * <br>
	 * This method cannot return <code>null</code>, if there is no body
	 * an empty {@link Map} is returned.
	 *  
	 * @return the body of the JSON response or an empty {@link Map} never <code>null</code>
	 */
	public Map getBody() {
		return body;
	}

	/**
	 * Returns <code>true</code> if the {@link Request} was successful
	 * 
	 * @return <code>true</code> if the {@link Request} was successful, <code>false</code> otherwise
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Set the success flag for the response
	 * 
	 * @param success the new success flag
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Returns <code>true</code> if the underlying command is currently running.
	 * 
	 * @return <code>true</code> if the underlying command is running, <code>false</code> otherwise
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Sets the running state of the underlying command
	 * 
	 * @param running the new running state for the underlying command
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Returns the status message for this {@link Response}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the status message for this {@link Response} or <code>null</code>
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the status message for this {@link Response}
	 * 
	 * @param message the new message, <code>null</code> is accepted
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.jsdi.connect.Packet#toJSON()
	 */
	public Map toJSON() {
		Map json = super.toJSON();
		json.put(JSONConstants.REQUEST_SEQ, new Integer(requestSequence));
		json.put(JSONConstants.COMMAND, command);
		json.put(JSONConstants.BODY, body);
		json.put(JSONConstants.SUCCESS, new Boolean(success));
		json.put(JSONConstants.RUNNING, new Boolean(running));
		if (message != null) {
			json.put(JSONConstants.MESSAGE, message);
		}
		return json;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Response Packet: ").append(JSONUtil.write(toJSON())); //$NON-NLS-1$
		return buffer.toString();
	}
}
