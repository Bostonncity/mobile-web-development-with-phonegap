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
package org.eclipse.wst.jsdt.debug.internal.ui.actions;


import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.jsdt.debug.internal.core.Constants;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * An action delegate that toggles the state of the "suspend on all script loads"
 * 
 * @since 1.0
 */
public class SuspendOnAllScriptLoadsAction extends ViewFilterAction {
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.ViewFilterAction#getPreferenceKey()
	 */
	protected String getPreferenceKey() {
		return Constants.SUSPEND_ON_ALL_SCRIPT_LOADS;
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IEclipsePreferences prefs = new InstanceScope().getNode(JavaScriptDebugPlugin.PLUGIN_ID);
		if(prefs != null) {
			prefs.putBoolean(Constants.SUSPEND_ON_ALL_SCRIPT_LOADS, getValue());
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				JavaScriptDebugUIPlugin.log(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.ui.actions.ViewFilterAction#getPreferenceValue()
	 */
	protected boolean getPreferenceValue() {
		return Platform.getPreferencesService().getBoolean(
				JavaScriptDebugPlugin.PLUGIN_ID,
				Constants.SUSPEND_ON_ALL_SCRIPT_LOADS, 
				false, 
				null);
	}
}
