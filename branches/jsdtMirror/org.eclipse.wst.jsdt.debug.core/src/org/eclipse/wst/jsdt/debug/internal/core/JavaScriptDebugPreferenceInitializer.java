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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Preference Initializer
 * 
 * @since 1.0
 */
public class JavaScriptDebugPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Constructor
	 */
	public JavaScriptDebugPreferenceInitializer() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs = new DefaultScope().getNode(JavaScriptDebugPlugin.PLUGIN_ID);
		if(prefs != null) {
			prefs.putBoolean(Constants.SUSPEND_ON_ALL_SCRIPT_LOADS, false);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				JavaScriptDebugPlugin.log(e);
			}
		}
	}

}
