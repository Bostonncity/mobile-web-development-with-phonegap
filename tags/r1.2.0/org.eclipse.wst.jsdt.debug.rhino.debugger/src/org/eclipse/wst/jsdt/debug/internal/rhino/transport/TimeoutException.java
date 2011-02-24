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
 * This exception is thrown if the {@link PacketSendManager} or {@link PacketReceiveManager}
 * times out while waiting for a response
 * 
 * @since 1.0
 */
public final class TimeoutException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message the message for the exception
	 */
	public TimeoutException(String message) {
		super(message);
	}

}
