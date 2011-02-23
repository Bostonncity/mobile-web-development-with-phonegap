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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.connect;

import org.eclipse.osgi.util.NLS;

/**
 * Rhino messages
 * 
 * @since 1.0
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.connect.messages"; //$NON-NLS-1$
	public static String HostArgument_description;
	public static String HostArgument_label;
	public static String PortArgument_description;
	public static String PortArgument_label;
	public static String RhinoAttachingConnector_description;
	public static String RhinoAttachingConnector_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
