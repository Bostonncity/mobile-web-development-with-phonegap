/*
 * Copyright (C) 2010-2011 Mobile Developer Solutions
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

package com.mds.apg.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import java.io.File;

public final class PagePhonegapPathSet extends WizardSection {

    // Set up storage for persistent initializers
    private final static String PHONEGAP_DIR = com.mds.apg.Activator.PLUGIN_ID + ".phonegap"; 
    
    /** Last user-browsed location, static so that it be remembered for the whole session */ 
    private static String sPhonegapPathCache = "";
    
    private boolean mFromGitHub;
    private String mPhonegapJs;
    private String mPhonegapJar;
    
    // widgets
    Text mPhonegapPathField;
    
    PagePhonegapPathSet(AndroidPgProjectCreationPage wizardPage, Composite parent) {
        super(wizardPage);
        sPhonegapPathCache = doGetPreferenceStore().getString(PHONEGAP_DIR);  
        createGroup(parent);
    }

    /**
     * Creates the group for the phonegap path:
     * 
     * @param parent the parent composite
     */
    protected final void createGroup(Composite parent) {

        // Set up layout for phonegap location entry
        Group phonegapGroup = new Group(parent, SWT.NONE);
        phonegapGroup.setLayout(new GridLayout(2, false /* columns of not equal size */));
        phonegapGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        phonegapGroup.setFont(parent.getFont());
        phonegapGroup.setText("phonegap-android Location");

        mPhonegapPathField = new Text(phonegapGroup, SWT.BORDER);
        mPhonegapPathField.setText(getLocationSave());
        mPhonegapPathField.setToolTipText("Should be the path to the unpacked phonegap-android installation");
        setupDirectoryBrowse(mPhonegapPathField, parent, phonegapGroup);
    }

    // --- Internal getters & setters ------------------

    final String getValue() {
        return mPhonegapPathField == null ? "" : mPhonegapPathField.getText().trim(); //$NON-NLS-1$
    }
    
    final Text getRawValue() {
        return mPhonegapPathField; 
    }
    
    final String getLocationSave() {
        return sPhonegapPathCache;
    }
    
    final void setLocationSave(String s) {
        sPhonegapPathCache = s;
    }
    
    final boolean isfromGit() {
        return mFromGitHub;
    }

    final String getPhonegapJsName() {
        return mPhonegapJs;
    }
    
    final String getPhonegapJarName() {
        return mPhonegapJar;
    }

    // --- UI Callbacks ----

    /**
     * Validates the phonegap path field.   There are two phonegap directory structures
     * 1. From github - should have an an example and framework sub-directory
     * 2. From Download on www.phonegap.com  - should have Android subdirectory with
     *              phonegap{version}.js phonegap{version}.jar and Samples directory
     * 
     * @return The wizard message type, one of MSG_ERROR, MSG_WARNING or
     *         MSG_NONE.
     */
    int validate() {
        String phonegapDirName = getValue();
        File phonegapDir = new File(phonegapDirName);
        if (!phonegapDir.exists() || !phonegapDir.isDirectory()) {
            return mWizardPage.setStatus("A phonegap directory name must be specified.",  AndroidPgProjectCreationPage.MSG_ERROR);
        } else {
            String[] l = phonegapDir.list();
            if (l.length == 0) {
                return mWizardPage.setStatus("The phonegap directory is empty.", AndroidPgProjectCreationPage.MSG_ERROR);
            }
            boolean foundFramework = false;
            boolean foundExample = false;
            boolean foundAndroid = false;

            for (String s : l) {
                if (s.equals("example")) {
                    foundExample = true;
                } else if (s.equals("framework")) {
                    foundFramework = true;
                } else if (s.equals("Android")) {
                    foundAndroid = true;
                }
            }
            if (foundAndroid) {   // First the www.phonegap.com download directory structure
                String androidDirName = phonegapDirName + "/Android";
                File androidDir = new File(androidDirName);
                
                String[] al = androidDir.list();
                if (al.length == 0) {
                    return mWizardPage.setStatus("The phonegap directory is empty.", AndroidPgProjectCreationPage.MSG_ERROR);
                }
                mPhonegapJs = null;
                mPhonegapJar = null;
                boolean foundSample = false;

                for (String s : al) {
                    if (s.indexOf("phonegap") == 0) {
                        if (s.substring(s.length()-3).equals(".js")) {
                            mPhonegapJs = s;
                        } else if (s.substring(s.length()-4).equals(".jar")) {
                            mPhonegapJar = s;
                        }
                    } else if (s.equals("Sample")) {
                        foundSample = true;
                    }
                }
                if ((mPhonegapJs == null) || (mPhonegapJar == null) || (!foundSample)) {
                    return mWizardPage.setStatus("Invalid phonegap-android location: " + androidDirName +
                            " must include a Samples directory, phonegap{version}.js and phonegap{version}.jar",
                            AndroidPgProjectCreationPage.MSG_ERROR);
                }
                mFromGitHub = false;
                
            } else {  // Second the github directory structure
                if ((!foundFramework) || (!foundExample)) {
                    return mWizardPage.setStatus(
                                "Invalid phonegap-android location. If it's from github," +
                                "it should have a framework and example subdirectory." +
                                "If it's from www.phonegap.com Download, it should have an Android subdirectory",
                                AndroidPgProjectCreationPage.MSG_ERROR);
                }
                mFromGitHub = true;
            }
            // TODO more validation

            // We now have a good directory, so set example path and save value
            doGetPreferenceStore().setValue(PHONEGAP_DIR, getValue());

            return AndroidPgProjectCreationPage.MSG_NONE;
        }
    }
}