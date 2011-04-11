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

import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;

/**
 * Description of a JSDI event indicating a script has been loaded.
 * 
 * @see EventQueue
 * @see EventSet
 * @see LocatableEvent
 * @see ScriptReference
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ScriptLoadEvent extends LocatableEvent {

	/**
	 * Returns the {@link ScriptReference} that has been loaded.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the {@link ScriptReference} that has been loaded never <code>null</code>
	 */
	public ScriptReference script();
}
