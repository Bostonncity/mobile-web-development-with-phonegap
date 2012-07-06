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
package org.eclipse.wst.jsdt.debug.core.breakpoints;

/**
 * Abstract description of a JavaScript script load breakpoint
 * 
 * @since 1.0
 */
public interface IJavaScriptLoadBreakpoint extends IJavaScriptLineBreakpoint {

	/**
	 * Registered marker id for a JavaScript script load breakpoint
	 */
	public static final String MARKER_ID = "org.eclipse.wst.jsdt.debug.core.scriptload.breakpoint.marker"; //$NON-NLS-1$

}
