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
package org.eclipse.wst.jsdt.debug.internal.ui.filters;

import org.eclipse.osgi.util.NLS;

class FilterMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.jsdt.debug.internal.ui.messages"; //$NON-NLS-1$
	public static String external_javascript_source;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FilterMessages.class);
	}

	private FilterMessages() {
	}
}
