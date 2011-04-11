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

import org.eclipse.wst.jsdt.debug.core.jsdi.Mirror;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;

/**
 * Abstract description of a JSDI event
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface Event extends Mirror {

	/**
	 * Returns the underlying {@link EventRequest}.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the underlying {@link EventRequest} never <code>null</code>
	 */
	public EventRequest request();
}
