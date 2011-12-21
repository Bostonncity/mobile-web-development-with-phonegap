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
package org.eclipse.wst.jsdt.debug.internal.core.launching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector;
import org.eclipse.wst.jsdt.debug.internal.core.Constants;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;

/**
 * Manages all of the contributed {@link Connector}s for JSDI
 * 
 * @since 1.0
 */
public class ConnectorsManager implements IRegistryEventListener {

	private static final String ID = "id"; //$NON-NLS-1$
	private HashMap connectors;

	/**
	 * Constructor
	 */
	public ConnectorsManager() {
		RegistryFactory.getRegistry().addListener(this, Constants.LAUNCHING_CONNECTORS);
	}

	/**
	 * Returns the live collection of {@link Connector}s the manager currently knows about sorted by name
	 * 
	 * @return the collection of {@link Connector}s in the manager
	 */
	public synchronized List /*<Connector>*/ getConnectors() {
		initializeConnectors();
		ArrayList list = new ArrayList(connectors.values());
		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				if (arg0 instanceof Connector && arg1 instanceof Connector) {
					return ((Connector) arg0).name().compareTo(((Connector) arg1).name());
				}
				return -1;
			}
		});
		return Collections.unmodifiableList(list);
	}

	/**
	 * Returns the {@link Connector} with the given id or <code>null</code>
	 * 
	 * @param connectorid
	 * @return the connector with the given id or <code>null</code>
	 */
	public synchronized Connector getConnector(String connectorid) {
		initializeConnectors();
		return (Connector) connectors.get(connectorid);
	}

	/**
	 * Initializes the connector collection
	 */
	private synchronized void initializeConnectors() {
		if (connectors == null) {
			connectors = new HashMap();
			IExtensionPoint point = RegistryFactory.getRegistry().getExtensionPoint(JavaScriptDebugPlugin.PLUGIN_ID, Constants.LAUNCHING_CONNECTORS);
			if(point != null) {
				IExtension[] extensions = point.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IExtension extension = extensions[i];
					try {
						IConfigurationElement[] elements = extension.getConfigurationElements();
						for (int j = 0; j < elements.length; j++) {
							IConfigurationElement element = elements[j];
							if (!Constants.CONNECTOR.equalsIgnoreCase(element.getName())) {
								// log it
								continue;
							}
							connectors.put(element.getAttribute(ID), element.createExecutableExtension(Constants.CLASS));
						}
					} catch (InvalidRegistryObjectException e) {
						JavaScriptDebugPlugin.log(e);
					} catch (CoreException e) {
						JavaScriptDebugPlugin.log(e);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtension[])
	 */
	public void added(IExtension[] extensions) {
		connectors = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IRegistryEventListener#added(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	public void added(IExtensionPoint[] extensionPoints) {
		connectors = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtension[])
	 */
	public void removed(IExtension[] extensions) {
		connectors = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IRegistryEventListener#removed(org.eclipse.core.runtime.IExtensionPoint[])
	 */
	public void removed(IExtensionPoint[] extensionPoints) {
		connectors = null;
	}
	
	/**
	 * Dispose the manager and cleans up resources
	 */
	public void dispose() {
		if(this.connectors != null) {
			this.connectors.clear();
			this.connectors = null;
		}
	}
}
