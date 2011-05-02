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
 * A queue for JSDI {@link EventSet}s
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface EventQueue {

	/**
	 * Removes an {@link EventSet} from the queue waiting for a default timeout if needed.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the top {@link EventSet} on the queue or <code>null</code> if an {@link EventSet} 
	 * is not available or there was an exception retrieving the top {@link EventSet}
	 */
	public EventSet remove();

	/**
	 * Removes an {@link EventSet} from the queue waiting for the specified timeout if needed.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @param timeout the amount of time (in milliseconds) to wait when trying to remove the next {@link EventSet}
	 * @return the top {@link EventSet} on the queue or <code>null</code> if an {@link EventSet} 
	 * is not available or there was an exception retrieving the top {@link EventSet}
	 */
	public EventSet remove(int timeout);
}
