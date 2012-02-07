/*
 * Copyright (C) 2010 Mobile Developer Solutions
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* PageInfo collects the wizard info needed to asynchronously to the 
 * project creation
 */

package com.mds.apg.wizards;

import org.eclipse.core.resources.IProject;

public class PageInfo {
    protected final String mSourceDirectory;      // The populating source
    protected final String mPhonegapDirectory;    // PhoneGap install location
    protected final boolean mFromGitHub;          // PhoneGap install from GitHub?
    protected final boolean mIsCordova;           // Is a Cordova GitHub
    protected final boolean mPackagedPhonegap;    // Using Phonegap packaged with plugin
    protected final String mInstallAndroidDirectory;    // Android directory from PhoneGap install
    protected final String mInstallExampleDirectory;    // Example (or Sample) directory from PhoneGap install
    protected final String mPhonegapJs;           // phonegap.js file name (non-github case)
    protected final String mPhonegapJar;          // phonegap.jar file name (non-github case)
    protected final String mContentSelection;          // content selection 
    protected final boolean mPureImport;          // use user's directory exactly
    protected final String mDestinationDirectory; // Place to put new files
    protected final IProject mAndroidProject;     // the new Android project
    protected final boolean mJqmChecked;          // Using jQueryMobile?
    protected final String mJqmDirectory;         // null if using packaged version
    protected final boolean mUseJqmDemo;          // use jQuery Mobile demo?
    protected final String mJqmVersion;
    protected final String mSenchaDirectory;      // Sencha Touch install location
    protected final boolean mSenchaChecked;       // Using Sencha
    protected final boolean mSenchaKitchenSink;   // Do Sencha Kitchen Sink app
    
    public PageInfo(String sourceDirectory, String phonegapDirectory, boolean fromGitHub, boolean isCordova, boolean packagedPhonegap,
            String installAndroidDirectory, String installExampleDirectory,
            String phonegapJs, String phonegapJar, String contentSelection, boolean pureImport, String destinationDirectory, 
            IProject androidProject, boolean jqmChecked, String jqmDirectory, boolean useJqmDemo,
            String jqmVersion, 
            String senchaDirectory, boolean senchaChecked, boolean senchaKitchenSink) {
        mSourceDirectory = sourceDirectory;
        mPhonegapDirectory = phonegapDirectory;
        mFromGitHub = fromGitHub;
        mIsCordova = isCordova;
        mPackagedPhonegap = packagedPhonegap;
        mInstallAndroidDirectory = installAndroidDirectory;
        mInstallExampleDirectory = installExampleDirectory;
        mPhonegapJs = phonegapJs;
        mPhonegapJar = phonegapJar;
        mContentSelection = contentSelection;
        mPureImport = pureImport;
        mDestinationDirectory = destinationDirectory;
        mAndroidProject = androidProject;
        mJqmChecked = jqmChecked;
        mJqmDirectory = jqmDirectory;
        mUseJqmDemo = useJqmDemo;
        mJqmVersion = jqmVersion;
        mSenchaDirectory = senchaDirectory;
        mSenchaChecked = senchaChecked;
        mSenchaKitchenSink = senchaKitchenSink;
    }
}
