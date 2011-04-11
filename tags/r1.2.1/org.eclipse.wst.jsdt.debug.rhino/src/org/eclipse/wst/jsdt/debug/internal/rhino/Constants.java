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
package org.eclipse.wst.jsdt.debug.internal.rhino;

/**
 * Collection of constants used in Rhino
 * 
 * @since 1.0
 */
public interface Constants {

//############## GENERAL ####################
	public static final String SPACE = " "; //$NON-NLS-1$
	public static String URI_FILE_SCHEME = "file"; //$NON-NLS-1$
	public static String STD_IN = "stdin"; //$NON-NLS-1$
	public static String STD_IN_URI = "<stdin>"; //$NON-NLS-1$
	
//############## PREFERENCES ################
	public static String SUSPEND_ON_STDIN_LOAD = RhinoDebugPlugin.PLUGIN_ID + ".suspend_on_stdin_load"; //$NON-NLS-1$
	
}
