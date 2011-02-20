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
package org.eclipse.wst.jsdt.debug.internal.ui;

/**
 * Describes images that are in the image registry
 * 
 * @since 1.0
 */
public interface ISharedImages {

	//ELCL
	/**
	 * Default script image
	 */
	public static final String IMG_SCRIPT = "IMG_SCRIPT"; //$NON-NLS-1$
	/**
	 * Default script group image
	 */
	public static final String IMG_SCRIPT_GRP = "IMG_SCRIPT_GRP"; //$NON-NLS-1$

	/**
	 * Image for a local variable
	 */
	public static final String IMG_LOCAL_VAR = "IMG_LOCAL_VAR"; //$NON-NLS-1$
	
	/**
	 * Image for a script load breakpoint
	 */
	public static final String IMG_THIS_VAR = "IMG_THIS_VAR"; //$NON-NLS-1$
	
	/**
	 * Image for the 'proto' variable
	 * @since 1.1
	 */
	public static final String IMG_PROTO_VAR = "IMG_PROTO_VAR"; //$NON-NLS-1$
	
	/**
	 * Image for a normal breakpoint
	 */
	public static final String IMG_BRKP = "IMG_BRKP"; //$NON-NLS-1$

	/**
	 * Image for the connect icon
	 */
	public static final String IMG_CONNECT = "IMG_CONNECT"; //$NON-NLS-1$
	/**
	 * Image for source / source lookup
	 * @since 1.1
	 */
	public static final String IMG_SOURCE = "IMG_SOURCE"; //$NON-NLS-1$
	
	//DLCL
	/**
	 * Image for a disabled breakpoint
	 */
	public static final String IMG_BRKP_DISABLED = "IMG_BRKP_DISABLED";  //$NON-NLS-1$
	
	//overlays
	/**
	 * Image for the conditional breakpoint overlay
	 */
	public static final String IMG_OVR_CONDITIONAL = "IMG_OVR_CONDITIONAL"; //$NON-NLS-1$
	
	/**
	 * Image for the disabled conditional breakpoint overlay 
	 */
	public static final String IMG_OVR_CONDITIONAL_DISABLED = "IMG_OVR_CONDITIONAL_DISABLED"; //$NON-NLS-1$
	
	/**
	 * Image for the function entry breakpoint overlay 
	 */
	public static final String IMG_OVR_ENTRY = "IMG_OVR_ENTRY"; //$NON-NLS-1$
	
	/**
	 * Image for the disabled function entry breakpoint overlay 
	 */
	public static final String IMG_OVR_ENTRY_DISABLED = "IMG_OVR_ENTRY_DISABLED"; //$NON-NLS-1$
	
	/**
	 * Image for the function exit breakpoint overlay
	 */
	public static final String IMG_OVR_EXIT = "IMG_OVR_EXIT"; //$NON-NLS-1$
	
	/**
	 * Image for the disabled function exit breakpoint overlay
	 */
	public static final String IMG_OVR_EXIT_DISABLED = "IMG_OVR_EXIT_DISABLED"; //$NON-NLS-1$
	
	/**
	 * Image for the installed breakpoint overlay
	 */
	public static final String IMG_OVR_INSTALLED = "IMG_OVR_INSTALLED"; //$NON-NLS-1$
	
	/**
	 * Image for the disabled installed breakpoint overlay
	 */
	public static final String IMG_OVR_INSTALLED_DISABLED = "IMG_OVR_INSTALLED_DISABLED"; //$NON-NLS-1$
	
	/**
	 * Image for the scoped breakpoint overlay
	 */
	public static final String IMG_OVR_SCOPED = "IMG_OVR_SCOPED"; //$NON-NLS-1$
	
	/**
	 * Image for the disabled scoped breakpoint overlay
	 */
	public static final String IMG_OVR_SCOPED_DISABLED = "IMG_OVR_SCOPED_DISABLED"; //$NON-NLS-1$
}
