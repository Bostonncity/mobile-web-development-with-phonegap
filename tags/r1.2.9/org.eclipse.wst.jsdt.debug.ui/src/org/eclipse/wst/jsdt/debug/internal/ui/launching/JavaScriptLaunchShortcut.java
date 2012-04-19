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
package org.eclipse.wst.jsdt.debug.internal.ui.launching;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.debug.internal.core.Constants;

/**
 * Default reusable launch shortcut for JavaScript debug consumers
 * @since 1.0
 */
public class JavaScriptLaunchShortcut implements ILaunchShortcut2 {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchConfigurations(org.eclipse.jface.viewers.ISelection)
	 */
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchConfigurations(org.eclipse.ui.IEditorPart)
	 */
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchableResource(org.eclipse.jface.viewers.ISelection)
	 */
	public IResource getLaunchableResource(ISelection selection) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut2#getLaunchableResource(org.eclipse.ui.IEditorPart)
	 */
	public IResource getLaunchableResource(IEditorPart editorpart) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, String mode) {
		openLaunchDialogOnSelection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode) {
		openLaunchDialogOnSelection();
	}

	/**
	 * Default behavior to open the launch configuration dialog on the {@link ILaunchConfigurationType}
	 * for JavaScript
	 */
	void openLaunchDialogOnSelection() {
		DebugUITools.openLaunchConfigurationDialogOnGroup(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				new StructuredSelection(getSelection()), 
				IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP);
	}
	
	/**
	 * Returns the type of the default launch configuration
	 * @return the default item to select when opening the dialog
	 */
	Object getSelection() {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(Constants.LAUNCH_CONFIG_ID);
		if(type != null) {
			try {
				ILaunchConfiguration[] configs = manager.getLaunchConfigurations(type);
				if(configs != null && configs.length > 0) {
					LaunchConfigurationManager cmanager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
					ILaunchGroup group = cmanager.getLaunchGroup(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP);
					if(group != null) {
						ILaunchConfiguration config = cmanager.getMRUConfiguration(
								Arrays.asList(configs), 
								group, 
								null);
						if(config != null) {
							return config;
						}
					}
				}
			}
			catch (CoreException ce) {
				//do nothing, just return type
			}
			return type;
		}
		return null;
	}
	
}
