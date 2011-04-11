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
package org.eclipse.wst.jsdt.debug.core.model;

import java.net.URI;

import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugElement;

/**
 * Description of a {@link JavaScriptDebugElement} for a {@link ScriptReference}
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IScript {

	/**
	 * Returns the source for this script.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the source or <code>null</code>
	 */
	public String source();
	
	/**
	 * Returns the {@link URI} for this script.<br>
	 * <br>
	 * This method cannot return <code>null</code>
	 * 
	 * @return the {@link URI} for this script never <code>null</code>
	 */
	public URI sourceURI();
}
