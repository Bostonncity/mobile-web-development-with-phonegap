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
package org.eclipse.wst.jsdt.core.compiler.libraries;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;


public class FireFoxLibInitializer extends JsGlobalScopeContainerInitializer implements IJsGlobalScopeContainerInitializer {
	protected static final String CONTAINER_ID = "org.eclipse.wst.jsdt.launching.FireFoxBrowserLibrary";
	protected static final String ContainerDescription = "FireFox Browser Support Library";
	protected static final char[][] LIBRARY_FILE_NAMES = {
		{'F', 'i', 'r', 'e', 'F', 'o', 'x', '2', '.', '0', '.', '0', '.', '3', '.', 'j', 's'},
		{'X', 'M', 'L', 'H', 't', 't', 'p', 'R', 'e', 'q', 'u', 'e', 's', 't', '.', 'j', 's'}
	};
	protected static final String PLUGIN_ID = "org.eclipse.wst.jsdt.support.firefox";


	static class FireFoxLibLocation extends SystemLibraryLocation {
		FireFoxLibLocation() {
			super();
		}


		public char[][] getLibraryFileNames() {
			return new char[][]{FireFoxLibInitializer.LIBRARY_FILE_NAMES[0]};
		}


		protected String getPluginId() {
			return FireFoxLibInitializer.PLUGIN_ID;
		}
		
		private static LibraryLocation fInstance;
		
		public static LibraryLocation getInstance(){
			if(fInstance== null){
				fInstance = new FireFoxLibLocation();
			}
			return fInstance;
		}
	}

	public LibraryLocation getLibraryLocation() {
		return FireFoxLibLocation.getInstance();
	}


	public String getDescription(IPath containerPath, IJavaScriptProject project) {
		return FireFoxLibInitializer.ContainerDescription;
	}

	public String getDescription() {
		return FireFoxLibInitializer.ContainerDescription;
	}

	
	public IPath getPath() {
		return new Path(FireFoxLibInitializer.CONTAINER_ID);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer#getKind()
	 */
	public int getKind() {


		return IJsGlobalScopeContainer.K_SYSTEM;
	}


	public boolean canUpdateJsGlobalScopeContainer(IPath containerPath, IJavaScriptProject project) {
		return true;


	}

	public String[] containerSuperTypes() {
		return new String[]{"window"};
	}

}
