package org.eclipse.wst.jsdt.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class IEMessages extends NLS {
	private static final String BUNDLE_NAME= "org.eclipse.wst.jsdt.internal.ui.wizards.IEMessages";//$NON-NLS-1$

	private IEMessages() {
		// Do not instantiate
	}
	
	public static String IELibraryWizardPage_title;
	public static String IELibraryWizardPage_IELibraryAdded;
	public static String IELibraryWizardPage_BrowserSupport;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, IEMessages.class);
	}

}
