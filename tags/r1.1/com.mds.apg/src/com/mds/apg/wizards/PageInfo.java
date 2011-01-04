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
    protected final String mDestinationDirectory; // Place to put new files
    protected final IProject mAndroidProject;     // the new Android project
    protected final boolean mJqmChecked;          // Using jQueryMobile?
    protected final String mJqmDirectory;         // null if using packaged version
    protected final boolean mUseJqmDemo;          // use jQuery Mobile demo?
    protected final String mJqmVersion;
    protected final boolean mBundledExample;
    protected final String mSenchaDirectory;      // Sencha Touch install location
    protected final boolean mSenchaChecked;       // Using Sencha
    protected final boolean mSenchaKitchenSink;   // Do Sencha Kitchen Sink app
    
    public PageInfo(String sourceDirectory, String phonegapDirectory, String destinationDirectory, 
            IProject androidProject, boolean jqmChecked, String jqmDirectory, boolean useJqmDemo,
            String jqmVersion, boolean bundledExample,
            String senchaDirectory, boolean senchaChecked, boolean senchaKitchenSink) {
        mSourceDirectory = sourceDirectory;
        mPhonegapDirectory = phonegapDirectory;
        mDestinationDirectory = destinationDirectory;
        mAndroidProject = androidProject;
        mJqmChecked = jqmChecked;
        mJqmDirectory = jqmDirectory;
        mUseJqmDemo = useJqmDemo;
        mJqmVersion = jqmVersion;
        mBundledExample = bundledExample;
        mSenchaDirectory = senchaDirectory;
        mSenchaChecked = senchaChecked;
        mSenchaKitchenSink = senchaKitchenSink;
    }
}
