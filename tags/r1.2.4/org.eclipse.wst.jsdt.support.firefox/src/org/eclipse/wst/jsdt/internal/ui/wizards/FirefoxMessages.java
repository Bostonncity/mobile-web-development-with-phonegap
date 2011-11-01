package org.eclipse.wst.jsdt.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class FirefoxMessages extends NLS {
	
	private static final String BUNDLE_NAME= "org.eclipse.wst.jsdt.internal.ui.wizards.FirefoxMessages";//$NON-NLS-1$

	private FirefoxMessages() {
		// Do not instantiate
	}
	
	public static String FirefoxLibraryWizardPage_title;
	public static String FirefoxLibraryWizardPage_FirefoxLibraryAdded;
	public static String FirefoxLibraryWizardPage_BrowserSupport;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, FirefoxMessages.class);
	}

}
