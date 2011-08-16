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
package org.eclipse.wst.jsdt.debug.core.jsdi;

/**
 * Description of a JSDI element that has a location
 * 
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface Locatable {

	/**
	 * Returns the {@link Location}.<br>
	 * <br>
	 * This method can return <code>null</code>
	 * 
	 * @return the {@link Location} or <code>null</code> if the location could not be computed
	 */
	public Location location();
}
