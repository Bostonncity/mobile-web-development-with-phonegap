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

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @since 1.0
 */
public class JavaScriptDebugUIPlugin extends AbstractUIPlugin implements IWorkbenchListener {

	/**
	 * Status code indicating an unexpected internal error.
	 */
	public static final int INTERNAL_ERROR = 120;
	/**
	 * The ID of the bundle<br>
	 * <br>
	 * value is: <code>org.eclipse.wst.jsdt.debug.ui</code>
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.jsdt.debug.ui"; //$NON-NLS-1$

	// The shared instance
	private static JavaScriptDebugUIPlugin plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		PlatformUI.getWorkbench().addWorkbenchListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		try {
			plugin = null;
			PlatformUI.getWorkbench().removeWorkbenchListener(this);
			super.stop(context);
		}
		finally {
			JavaScriptImageRegistry.dispose();
		}
	}

	/**
	 * Returns the singleton instance
	 *
	 * @return the singleton instance
	 */
	public static JavaScriptDebugUIPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public static void log(IStatus status) {
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}

	/**
	 * Logs the specified throwable with this plug-in's log.
	 * 
	 * @param t throwable to log 
	 */
	public static void log(Throwable t) {
		log(newErrorStatus("Error logged from JavaScript Debug UI: ", t)); //$NON-NLS-1$
	}
	
	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message the error message to log
	 */
	public static void logErrorMessage(String message) {
		// this message is intentionally not internationalized, as an exception may
		// be due to the resource bundle itself
		log(newErrorStatus("Internal message logged from JavaScript Debug UI: " + message, null)); //$NON-NLS-1$	
	}
	
	/**
	 * Returns a new error status for this plug-in with the given message
	 * @param message the message to be included in the status
	 * @param exception the exception to be included in the status or <code>null</code> if none
	 * @return a new error status
	 */
	public static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, message, exception);
	}

	/**
	 * Closes any editors open on sources in the external JavaScript source project
	 * @throws PartInitException 
	 * 
	 * @since 1.1
	 */
	protected void closeEditors() throws PartInitException {
		IProject project = JavaScriptDebugPlugin.getExternalSourceProject(false);
		if(project != null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			ArrayList editors = new ArrayList(8);
			for(int l = 0; l < windows.length; l++) {
				IWorkbenchPage[] pages = windows[l].getPages();
				for(int i = 0; i < pages.length; i++) {
					IEditorReference[] erefs = pages[i].getEditorReferences();
					for(int j = 0; j < erefs.length; j++) {
						IFile file = ResourceUtil.getFile(erefs[j].getEditorInput());
						if(file != null) {
							if(project.equals(file.getProject())) {
								editors.add(erefs[j]);
							}
						}
					}
					if(editors.size() > 0) {
						pages[i].closeEditors((IEditorReference[]) editors.toArray(new IEditorReference[editors.size()]), false);
						editors.clear();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
	 */
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		try {
			closeEditors();
		} catch (PartInitException e) {
			log(e);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
	 */
	public void postShutdown(IWorkbench workbench) {
	}
}