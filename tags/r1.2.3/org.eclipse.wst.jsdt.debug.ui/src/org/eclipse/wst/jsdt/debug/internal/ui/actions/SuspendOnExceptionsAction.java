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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.jsdt.debug.internal.core.Constants;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Handles suspending the debugger when an exception is thrown
 * 
 * @since 1.1
 */
public class SuspendOnExceptionsAction implements IViewActionDelegate {
	
	boolean initialized = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IEclipsePreferences prefs = new InstanceScope().getNode(JavaScriptDebugPlugin.PLUGIN_ID);
		if(prefs != null) {
			prefs.putBoolean(Constants.SUSPEN_ON_THROWN_EXCEPTION, action.isChecked());
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				JavaScriptDebugUIPlugin.log(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if(!initialized) {
			boolean checked = Platform.getPreferencesService().getBoolean(
								JavaScriptDebugPlugin.PLUGIN_ID,
								Constants.SUSPEN_ON_THROWN_EXCEPTION, 
								false, 
								null);
			action.setChecked(checked);
			initialized = true;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {}
}
