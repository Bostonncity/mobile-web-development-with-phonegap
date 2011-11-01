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
package org.eclipse.wst.jsdt.debug.internal.core;

/**
 * Common constants
 * 
 * @since 1.0
 */
public interface Constants {

//#########  MISC  ############
	/**
	 * Constant representing a space
	 */
	public static final String SPACE = " "; //$NON-NLS-1$
	/**
	 * Constant representing a colon.<br>
	 * <br>
	 * Value is: <code>:</code>
	 */
	public static final String COLON = ":"; //$NON-NLS-1$
	/**
	 * Constant representing the empty string
	 */
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	/**
	 * UTF-8 encoding constant <br>
	 * <br>
	 * Value is: <code>UTF-8</code>
	 */
	public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
	/**
	 * The JavaScript file extension<br>
	 * <br>
	 * Value is <code>js</code>
	 * @since 1.1
	 */
	public static final String JS_EXTENSION = "js"; //$NON-NLS-1$
	
//#########  LAUNCHING  ############
	/**
	 * The ID of the default JavaScript launch configuration type
	 */
	public static final String LAUNCH_CONFIG_ID = "org.eclipse.wst.jsdt.debug.core.launchConfigurationType"; //$NON-NLS-1$
	
//#########  EXTENSION POINTS  ############
	/**
	 * the class attribute name
	 */
	public static final String CLASS = "class"; //$NON-NLS-1$
	/**
	 * the kind attribute
	 */
	public static final String KIND = "kind"; //$NON-NLS-1$
	/**
	 * The connector attribute
	 */
	static final String CONNECTOR = "connector"; //$NON-NLS-1$
	/**
	 * the name of the breakpoint participants extension point
	 */
	public static final String BREAKPOINT_PARTICIPANTS = "breakpointParticipants"; //$NON-NLS-1$
	/**
	 * The name of the launching connectors extension point
	 */
	static final String LAUNCHING_CONNECTORS = "launchingConnectors"; //$NON-NLS-1$
	
//#########  PREFERENCES  ############
	/**
	 * Preference to suspend execution when any script loads, i.e. for every script load event
	 */
	public static final String SUSPEND_ON_ALL_SCRIPT_LOADS = JavaScriptDebugPlugin.PLUGIN_ID + ".suspend_on_all_script_loads"; //$NON-NLS-1$
	
	/**
	 * Preference to suspend execution when a thrown exception is encountered
	 * @since 1.1
	 */
	public static final String SUSPEN_ON_THROWN_EXCEPTION = JavaScriptDebugPlugin.PLUGIN_ID + ".suspend_on_thrown_exceptions"; //$NON-NLS-1$
}
