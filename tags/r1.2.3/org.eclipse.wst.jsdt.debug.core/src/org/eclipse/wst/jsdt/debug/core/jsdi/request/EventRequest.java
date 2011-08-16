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
package org.eclipse.wst.jsdt.debug.core.jsdi.request;

import org.eclipse.wst.jsdt.debug.core.jsdi.Mirror;

/**
 * Description of a JSDI request.
 * 
 * @see Mirror
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface EventRequest extends Mirror {

	/**
	 * Returns if the request is enabled
	 * @return <code>true</code> if the request is enabled <code>false</code> otherwise
	 */
	public boolean isEnabled();

	/**
	 * Allows the enabled state of the request to be changed.
	 * 
	 * @param enabled the new enabled state of the request
	 */
	public void setEnabled(boolean enabled);
}
