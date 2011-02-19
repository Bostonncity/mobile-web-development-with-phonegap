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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;


public class InternetExplorerLibInitializer extends JsGlobalScopeContainerInitializer implements IJsGlobalScopeContainerInitializer {
	protected static final String CONTAINER_ID = "org.eclipse.wst.jsdt.launching.InternetExplorer";
	protected static final String ContainerDescription = "Internet Explorer Support Library";
	//private static final char[][] LIBRARY_FILE_NAMES = { { 'I','n','t','e','r','n','e','t','E','x','p','l','o','r','e','r','.','j','s' } };
	protected static final String PLUGIN_ID = "org.eclipse.wst.jsdt.support.ie";


	static class IeLibLocation extends SystemLibraryLocation {
		IeLibLocation() {
			super();
		}


		public char[][] getLibraryFileNames() {
			return getAllFilesInPluginDirectory(getLibraryPathInPlugin().toString());
		}


		protected String getPluginId() {
			return InternetExplorerLibInitializer.PLUGIN_ID;
		}
		private static LibraryLocation fInstance;
		public static LibraryLocation getInstance(){
			if(fInstance== null){
				fInstance = new IeLibLocation();
			}
			return fInstance;
		}
	}

	public LibraryLocation getLibraryLocation() {
		return IeLibLocation.getInstance();
	}


	public String getDescription(IPath containerPath, IJavaScriptProject project) {
		return InternetExplorerLibInitializer.ContainerDescription;
	}

	public String getDescription() {
		return InternetExplorerLibInitializer.ContainerDescription;
	}

	public ImageDescriptor getImage(IPath containerPath, String element, IJavaScriptProject project) {

		if(containerPath==null) return null;
		/* Dont use the rino image for the individual files */
		String requestedContainerPath = new Path(element).getFileExtension();
		if(requestedContainerPath!=null && requestedContainerPath.equalsIgnoreCase("js")) return null;


//		char[][] allLibFiles = ( new IeLibLocation()).getLibraryFileNames();
//		for(int i = 0;i<allLibFiles.length;i++) {
//			String libName = new String(allLibFiles[i]);
//			if (element!=null && requestedContainerPath.equals(libName)) {
//				return null;
//			}
//		}
		return ImageDescriptor.createFromFile(this.getClass(),"ie_small.gif");
		//return ImageDescriptor.createFromFile(this.getClass(),"ie_small.gif");
	//	System.out.println("Unimplemented method:BasicBrowserLibraryJsGlobalScopeContainerInitializer.getImage");
		//return null;
	}
	public IPath getPath() {
		return new Path(InternetExplorerLibInitializer.CONTAINER_ID);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer#getKind()
	 */
	public int getKind() {


		return  IJsGlobalScopeContainer.K_SYSTEM;	}


	public boolean canUpdateJsGlobalScopeContainer(IPath containerPath, IJavaScriptProject project) {
		return true;


	}


	public String[] containerSuperTypes() {
		return new String[] {"window", "object", "array"};
	}


}
