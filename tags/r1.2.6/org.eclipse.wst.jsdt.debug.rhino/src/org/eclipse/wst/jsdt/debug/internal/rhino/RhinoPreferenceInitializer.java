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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Initializes the preferences for Rhino
 * 
 * @since 1.0
 */
public class RhinoPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Constructor
	 */
	public RhinoPreferenceInitializer() {}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IEclipsePreferences defaultscope = new DefaultScope().getNode(RhinoDebugPlugin.PLUGIN_ID);
		defaultscope.putBoolean(Constants.SUSPEND_ON_STDIN_LOAD, false);
		try {
			defaultscope.flush();
		} catch (BackingStoreException e) {
			RhinoDebugPlugin.log(e);
		}
	}
}
