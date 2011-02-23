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

import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;

/**
 * Description of a JSDI event indicating a {@link VirtualMachine} has been disconnected.
 * 
 * @see Event
 * @see EventQueue
 * @see EventSet
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface VMDisconnectEvent extends Event {

}
