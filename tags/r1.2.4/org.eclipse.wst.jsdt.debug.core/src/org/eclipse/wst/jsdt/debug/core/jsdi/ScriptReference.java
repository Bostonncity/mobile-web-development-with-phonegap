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

import java.net.URI;
import java.util.List;

/**
 * Describes a JavaScript script object.<br>
 * <br>
 * A script object has {@link Location} information for all of the lines and {@link FunctionReference}s contained within it.
 * 
 * @see FunctionReference
 * @see Location
 * @see Mirror
 * @since 1.0
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ScriptReference extends Mirror {

	/**
	 * Returns the complete live list of line locations in this script. <br>
	 * <br>
	 * This method cannot return <code>null</code>.
	 * 
	 * @return the line locations in this script or an empty list, never <code>null</code>
	 * @see Location
	 * @see #lineLocation(int)
	 */
	public List/* <Location> */allLineLocations();

	/**
	 * Returns the {@link Location} information for the given line number in this script.<br>
	 * <br>
	 * This method can return <code>null</code>.
	 * 
	 * @param lineNumber
	 * @return the {@link Location} information for the given line number or <code>null</code>.
	 * @see Location
	 * @see #allLineLocations()
	 */
	public Location lineLocation(int lineNumber);

	/**
	 * Returns the complete live list of function locations in this script.<br>
	 * <br>
	 * This method can return <code>null</code>.
	 * 
	 * @return the function locations in this script
	 * @see Location
	 * @see #functionLocation(String)
	 */
	public List/* <Location> */allFunctionLocations();

	/**
	 * Returns the {@link Location} information for a function with the given name in this script.<br>
	 * <br>
	 * This method can return <code>null</code>.
	 * 
	 * @param functionName
	 * @return the {@link Location} information for a function with the given name
	 * @see Location
	 * @see #allFunctionLocations()
	 */
	public Location functionLocation(String functionName);

	/**
	 * Returns the entire known source for this script at the time this method is called.<br>
	 * <br>
	 * This method can return <code>null</code>.
	 * 
	 * @return the source for this script
	 */
	public String source();

	/**
	 * Returns the {@link URI} to the source of this script.<br>
	 * <br>
	 * This method will not return <code>null</code>.
	 * 
	 * @return the {@link URI} to the source of this script
	 */
	public URI sourceURI();
}
