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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame;
import org.eclipse.wst.jsdt.debug.core.model.IScript;
import org.eclipse.wst.jsdt.debug.internal.core.Constants;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.model.Script;

/**
 * Default source lookup participant
 * 
 * @since 1.0
 */
public class JavaScriptSourceLookupParticipant extends AbstractSourceLookupParticipant {

	static final Object[] NO_SOURCE = new Object[0];

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof IJavaScriptStackFrame) {
			return ((IJavaScriptStackFrame) object).getSourceName();
		}
		if(object instanceof IScript) {
			return URIUtil.lastSegment(((IScript)object).sourceURI());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant#findSourceElements(java.lang.Object)
	 */
	public Object[] findSourceElements(Object object) throws CoreException {
		URI sourceURI = getSourceURI(object);
		if (sourceURI != null) {
			if (!sourceURI.isAbsolute() || "file".equals(sourceURI.getScheme())) {//$NON-NLS-1$			
				IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
				URI workspaceURI = workspaceRoot.getRawLocationURI();			
				URI workspaceRelativeURI = workspaceURI.relativize(sourceURI);
				if (! workspaceRelativeURI.isAbsolute()) {
					IFile file = (IFile) workspaceRoot.findMember(new Path(workspaceRelativeURI.getPath()), false);
					if (file != null) {
						return new IFile[] { file };
					}
				}
			}
			//else show the temp source
			return showExternalSource(getSource(object), sourceURI);
		}
		return NO_SOURCE;
	}

	/**
	 * Returns the raw element source to use to display an external editor
	 * @param object
	 * @return the raw source or <code>null</code>
	 */
	private String getSource(Object object) {
		if(object instanceof IJavaScriptStackFrame) {
			IJavaScriptStackFrame jframe = (IJavaScriptStackFrame) object;
			return jframe.getSource();
		}
		if(object instanceof IScript) {
			IScript script = (IScript) object;
			return script.source();
		}
		return null;
	}
	
	/**
	 * Returns the URI to use to look up source
	 * @param object
	 * @return the URI or <code>null</code>
	 * @since 1.1
	 */
	private URI getSourceURI(Object object) {
		if(object instanceof IJavaScriptStackFrame) {
			IJavaScriptStackFrame jframe = (IJavaScriptStackFrame) object;
			try {
				return URIUtil.fromString(jframe.getSourcePath());
			} catch (URISyntaxException e) {
				JavaScriptDebugPlugin.log(e);
			}
		}
		if(object instanceof IScript) {
			IScript script = (IScript) object;
			return script.sourceURI();
		}
		return null;
	}
	
	/**
	 * Shows the source in an external editor
	 * 
	 * @param source
	 * @param path
	 * @return the collection of files to show in external editors
	 */
	private Object[] showExternalSource(String source, URI uri) {
		if(source == null) {
			return NO_SOURCE;
		}
		try {
			IProject project = JavaScriptDebugPlugin.getExternalSourceProject(true);
			String filename = Script.resolveName(uri);
			String uriHash =  Integer.toString(uri.toString().hashCode());
			String sourceHash = Integer.toString(source.hashCode());
			IPath path = new Path(uriHash).append(sourceHash).append(filename);
			String ext = path.getFileExtension(); 
			if(ext == null || !Constants.JS_EXTENSION.equals(ext)) {
				path = path.addFileExtension(Constants.JS_EXTENSION);
			}
			IFile file = project.getFile(path);
			if(!file.isAccessible()) {
				IContainer folder = project;
				for (int i = 0; i < path.segmentCount()-1; i++) {
					IFolder f = folder.getFolder(new Path(path.segment(i)));
					if(!f.exists()) {
						f.create(true, true, null);
					}
					folder = f;
				}
				file.create(new ByteArrayInputStream(source.getBytes()), true, null);
			}
			
			if (JavaScriptDebugPlugin.getExternalScriptPath(path) == null) {
				JavaScriptDebugPlugin.addExternalScriptPath(path, uri.toString());
			}	
			return new Object[] {file};

		} catch (CoreException ce) {
			JavaScriptDebugPlugin.log(ce);
			return NO_SOURCE;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant#isFindDuplicates()
	 */
	public boolean isFindDuplicates() {
		return true;
	}
}
