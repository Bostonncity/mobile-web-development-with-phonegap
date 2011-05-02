/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.core.compiler.libraries;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.internal.ui.IJsGlobalScopeContainerInitializerExtension;

/**
 * @author childsb
 *
 */
public class FireFoxUiInitializer  implements IJsGlobalScopeContainerInitializerExtension {
	public ImageDescriptor getImage(IPath containerPath, String element, IJavaScriptProject project) {

		if (containerPath == null) {
			return null;
		}
		/* Dont use the rino image for the individual files */
		String requestedContainerPath = new Path(element).lastSegment();
		if ((element != null) && requestedContainerPath.equals(new String(FireFoxLibInitializer.LIBRARY_FILE_NAMES[0]))) {
			return null;
		}

		return ImageDescriptor.createFromFile(this.getClass(), "FireFoxSmall.gif");
		// System.out.println("Unimplemented
		// method:BasicBrowserLibraryJsGlobalScopeContainerInitializer.getImage");
		// return null;
	}

}
