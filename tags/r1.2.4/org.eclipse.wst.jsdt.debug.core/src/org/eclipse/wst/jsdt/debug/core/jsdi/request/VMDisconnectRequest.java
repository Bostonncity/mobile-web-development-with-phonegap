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

import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;

/**
 * Description of a JSDI request to disconnect a {@link VirtualMachine}.
 * 
 * @see EventRequest
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface VMDisconnectRequest extends EventRequest {

}
