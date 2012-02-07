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
package org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.ui.JavaPluginImages;
import org.eclipse.wst.jsdt.internal.ui.wizards.FirefoxMessages;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.wst.jsdt.ui.wizards.IJsGlobalScopeContainerPage;
import org.eclipse.wst.jsdt.ui.wizards.IJsGlobalScopeContainerPageExtension;
import org.eclipse.wst.jsdt.ui.wizards.IJsGlobalScopeContainerPageExtension2;
import org.eclipse.wst.jsdt.ui.wizards.NewElementWizardPage;

/**
 * Wizard page for adding Firefox library support to the project.
 */
public class FireFoxLibraryWizardPage extends NewElementWizardPage implements IJsGlobalScopeContainerPage, IJsGlobalScopeContainerPageExtension, IJsGlobalScopeContainerPageExtension2 {

	private static final String CONTAINER_ID = "org.eclipse.wst.jsdt.launching.FireFoxBrowserLibrary";

	public FireFoxLibraryWizardPage() {
		super("FireFoxBrowserLib");
		setTitle(FirefoxMessages.FirefoxLibraryWizardPage_title);
		setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
	}

	public boolean finish() {
		return true;
	}

	public IIncludePathEntry getSelection() {
		System.out.println("Unimplemented method:BaseLibraryWizardPage.getSelection");
		return null;
	}

	public void setSelection(IIncludePathEntry containerEntry) {
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		DialogField field = new DialogField();

		field.setLabelText(FirefoxMessages.FirefoxLibraryWizardPage_FirefoxLibraryAdded);
		LayoutUtil.doDefaultLayout(composite, new DialogField[]{field}, false, SWT.DEFAULT, SWT.DEFAULT);
		Dialog.applyDialogFont(composite);
		setControl(composite);
		setDescription(FirefoxMessages.FirefoxLibraryWizardPage_BrowserSupport);
	}

	public void initialize(IJavaScriptProject project, IIncludePathEntry[] currentEntries) {
		// nothing to initialize
	}

	public IIncludePathEntry[] getNewContainers() {
		IIncludePathEntry library = JavaScriptCore.newContainerEntry(new Path(CONTAINER_ID));
		return new IIncludePathEntry[]{library};
	}
}
