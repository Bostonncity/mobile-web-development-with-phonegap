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

import java.util.Set;

import org.eclipse.wst.jsdt.debug.core.jsdi.Mirror;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;

/**
 * Describes a heterogeneous set of {@link Event}s.
 * 
 * @see Event
 * @see EventQueue
 * @see VirtualMachine
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface EventSet extends Set, Mirror {

	/**
	 * Returns <code>true</code> if processing of the {@link EventSet} is currently suspended.
	 * 
	 * @return <code>true</code> if the {@link EventSet} is suspended <code>false</code> otherwise
	 */
	public boolean suspended();

	/**
	 * Resumes processing of the {@link EventSet}.
	 */
	public void resume();
}
