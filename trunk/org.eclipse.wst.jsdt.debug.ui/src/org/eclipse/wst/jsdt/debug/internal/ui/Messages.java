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

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.jsdt.debug.internal.ui.messages"; //$NON-NLS-1$
	public static String add_script_load_bp;
	public static String bp_conditional;
	public static String bp_conditonal;
	public static String bp_entry_and_exit;
	public static String bp_entry_only;
	public static String bp_exit_only;
	public static String bp_hit_count;
	public static String bp_line_number;
	public static String bp_suspend_vm;
	public static String breakpoint_configuration;
	public static String connect;
	public static String connector;
	public static String connector_properties;
	public static String creating_script_load_bp;
	public static String enable_hit_count;
	public static String enter_new_hit_count;
	public static String exception_occurred_setting_bp_properties;
	public static String external_javascript_source;
	public static String hit_count_must_be_positive;
	public static String no_description_provided;
	public static String scripts;
	public static String select_javascript_file;
	public static String set_bp_hit_count;
	public static String suspend_target;
	public static String suspend_thread;
	public static String the_argument_0_is_not_valid;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
