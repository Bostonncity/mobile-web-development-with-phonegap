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
package org.eclipse.wst.jsdt.debug.core.jsdi.event;

/**
 * Description of a JSDI event indicating an exception has occurred.
 * 
 * @see EventQueue
 * @see EventSet
 * @see LocatableEvent
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ExceptionEvent extends LocatableEvent {

	/**
	 * Returns the message from the exception.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the message from the exception or <code>null</code>
	 */
	public String message();
}
