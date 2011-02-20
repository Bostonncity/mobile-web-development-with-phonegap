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
package org.eclipse.wst.jsdt.debug.internal.ui.dialogs;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.debug.internal.ui.Messages;

/**
 * Customized {@link FilteredResourcesSelectionDialog} for selecting 
 * JavaScript script files
 * 
 * @since 1.0
 */
public class ScriptSelectionDialog extends FilteredResourcesSelectionDialog {

	/**
	 * Default extension
	 */
	public static final String JS_EXTENSION_PATTERN = "*.js"; //$NON-NLS-1$

	class ScriptFilter extends ResourceFilter {
		/* (non-Javadoc)
		 * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog.ResourceFilter#matchItem(java.lang.Object)
		 */
		public boolean matchItem(Object item) {
			return super.matchItem(item) && isJavaScriptContent((IFile) item);
		}
		
		boolean isJavaScriptContent(IFile file) {
			IContentType filetype = IDE.getContentType(file);
			if(filetype == null) {
				return false;
			}
    		IContentType scripttype = Platform.getContentTypeManager().getContentType(JavaScriptCore.JAVA_SOURCE_CONTENT_TYPE);
    		return filetype.equals(scripttype);
		}
	}
	
	/**
	 * Constructor
	 * @param shell
	 * @param multi
	 * @param container
	 */
	public ScriptSelectionDialog(Shell shell, boolean multi, IContainer container) {
		super(shell, multi, container, IResource.FILE);
		setInitialPattern(JS_EXTENSION_PATTERN);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.SelectionDialog#getMessage()
	 */
	protected String getMessage() {
		return Messages.select_javascript_file;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog#createFilter()
	 */
	protected ItemsFilter createFilter() {
		return new ScriptFilter();
	}
}
