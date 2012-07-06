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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.jsdt.debug.internal.core.launching.ConnectorsManager;
import org.eclipse.wst.jsdt.debug.internal.core.model.BreakpointParticipantManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @since 1.0
 */
public class JavaScriptDebugPlugin extends Plugin {

	/**
	 * Status code indicating an unexpected internal error.
	 */
	public static final int INTERNAL_ERROR = 120;
	/**
	 * The ID of the bundle<br>
	 * <br>
	 * value is: <code>org.eclipse.wst.jsdt.debug.core</code>
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.jsdt.debug.core"; //$NON-NLS-1$

	/**
	 * The singleton instance
	 */
	private static JavaScriptDebugPlugin plugin;

	/**
	 * The singleton {@link ConnectorsManager}
	 */
	private static ConnectorsManager connectionmanager = null;
	/**
	 * Singleton {@link BreakpointParticipantManager}
	 */
	private static BreakpointParticipantManager participantmanager = null;
	/**
	 * Singleton {@link JavaScriptPreferencesManager}
	 */
	private static JavaScriptPreferencesManager prefmanager = null;
	/**
	 * Handle to the 'External JavaScript Source' project
	 */
	private static IProject extSrcProject = null;
	private static Map externalScriptPaths = new HashMap();
	
	/**
	 * Returns the singleton {@link ConnectorsManager} instance
	 * 
	 * @return the {@link ConnectorsManager}
	 */
	public static synchronized ConnectorsManager getConnectionsManager() {
		if (connectionmanager == null) {
			connectionmanager = new ConnectorsManager();
		}
		return connectionmanager;
	}
	
	/**
	 * Returns the singleton {@link BreakpointParticipantManager}
	 * 
	 * @return the {@link BreakpointParticipantManager}
	 */
	public static synchronized BreakpointParticipantManager getParticipantManager() {
		if(participantmanager == null) {
			participantmanager = new BreakpointParticipantManager();
		}
		return participantmanager;
	}
	
	/**
	 * Returns the current handle to the 'External JavaScript Source' project or <code>null</code>. If the project
	 * is not accessible it can be created by passing <code>true</code> in for the create parameter.
	 * 
	 * @param create
	 * @return the handle to the external source project or <code>null</code>
	 */
	public static synchronized IProject getExternalSourceProject(boolean create) {
		if(extSrcProject == null) {
			try {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(Messages.external_javascript_source);
				if(project.isAccessible()) {
					extSrcProject = project;
				}
				if(project.exists()) {
					project.open(null);
					extSrcProject = project;
				}
				else if(create) {
					project.create(null);
					project.open(null);
					extSrcProject = project;
				}
			}
			catch(CoreException ce) {
				log(ce);
			}
		}
		return extSrcProject;
	}
	
	/**
	 * Returns if the given path is for the 'External JavaScript Source' project
	 * @param path
	 * @return <code>true</code> if the path is in the external source project, <code>false</code> otherwise
	 */
	public static boolean isExternalSource(IPath path) {
		return path.segment(0).equals(Messages.external_javascript_source);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		prefmanager = new JavaScriptPreferencesManager();
		prefmanager.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		try {
			plugin = null;
			if(connectionmanager != null) {
				connectionmanager.dispose();
			}
			if(participantmanager != null) {
				participantmanager.dispose();
			}
			if(prefmanager != null) {
				prefmanager.stop();
			}
			if(extSrcProject != null && extSrcProject.exists()) {
				extSrcProject.delete(true, null);
			}
		}
		finally {
			super.stop(context);
		}
	}

	/**
	 * Returns the singleton instance
	 *
	 * @return the singleton instance
	 */
	public static JavaScriptDebugPlugin getDefault() {
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
		log(newErrorStatus("Error logged from JSDT Debug Core: ", t)); //$NON-NLS-1$
	}
	
	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message the error message to log
	 */
	public static void logErrorMessage(String message) {
		log(newErrorStatus("Internal message logged from JavaScript Debug Core: " + message, null)); //$NON-NLS-1$	
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
	 * Caches the original {@link URI} script path for the given external source path
	 *  
	 * @param path
	 * @param scriptPath
	 */
	public static synchronized void addExternalScriptPath(IPath path, String scriptPath) {
		externalScriptPaths.put(path.makeAbsolute(), scriptPath);
	}
	
	/**
	 * Fetches the original {@link URI} script path for the given external source path
	 * 
	 * @param path
	 * @return
	 */
	public static synchronized String getExternalScriptPath(IPath path) {
		return (String) externalScriptPaths.get(path.makeAbsolute());
	}
}
