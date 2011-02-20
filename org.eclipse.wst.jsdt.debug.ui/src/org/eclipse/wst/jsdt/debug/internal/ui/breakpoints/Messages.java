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
package org.eclipse.wst.jsdt.debug.internal.ui.breakpoints;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.jsdt.debug.internal.ui.breakpoints.messages"; //$NON-NLS-1$
	public static String breakpoint_condition;
	public static String breakpoint_properties;
	public static String breakpoint_settings;
	public static String conditional;
	public static String disable_breakpoint;
	public static String enable_breakpoint;
	public static String enabled;
	public static String enter_condition;
	public static String entry;
	public static String exit;
	public static String failed_to_create_function_bp;
	public static String failed_to_create_line_bp;
	public static String fuction;
	public static String function_bp;
	public static String function_breakpoint;
	public static String function_breakpoint_settings;
	public static String hit_count;
	public static String hit_count_must_be_positive;
	public static String line_breakpoint;
	public static String line_breakpoint_settings;
	public static String line_number;
	public static String line_number_bp;
	public static String member;
	public static String no_valid_location;
	public static String script_load_bp;
	public static String script_load_breakpoint;
	public static String script_path;
	public static String suspend_policy;
	public static String suspend_thread_option;
	public static String suspend_thread;
	public static String suspend_target;
	public static String suspend_when_changed;
	public static String suspend_when_entering;
	public static String suspend_when_exiting;
	public static String suspend_when_true;
	public static String type_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
