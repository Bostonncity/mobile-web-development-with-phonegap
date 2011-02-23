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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Handles all of the core Rhino preferences - allows easy access to a variety of preferences
 * and tracks changes to those preferences.
 * 
 * @since 1.0
 */
public class RhinoPreferencesManager implements IPreferenceChangeListener {

	/**
	 * Starts the manager
	 */
	public void start() {
		IEclipsePreferences node = new InstanceScope().getNode(RhinoDebugPlugin.PLUGIN_ID);
		if(node != null) {
			node.addPreferenceChangeListener(this);
		}
		else {
			//our preference node is bogus report the status
			RhinoDebugPlugin.logErrorMessage("The Rhino core preference node could not loaded"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Stops the manager and frees any resources held
	 */
	public void stop() {
		IEclipsePreferences node = new InstanceScope().getNode(RhinoDebugPlugin.PLUGIN_ID);
		if(node != null) {
			node.removePreferenceChangeListener(this);
		}
		else {
			//our preference node is bogus report the status
			RhinoDebugPlugin.logErrorMessage("The Rhino core preference node could not loaded"); //$NON-NLS-1$
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	public void preferenceChange(PreferenceChangeEvent event) {
	}

	/**
	 * Returns if we should be suspending on script load events from stdin - i.e. from the Rhino console.
	 * 
	 * @return <code>true</code> if we should suspend on a script load for stdin, <code>false</code> otherwise
	 */
	public static boolean suspendOnStdinLoad() {
		return Platform.getPreferencesService().getBoolean(RhinoDebugPlugin.PLUGIN_ID, Constants.SUSPEND_ON_STDIN_LOAD, false, null);
	}
}
