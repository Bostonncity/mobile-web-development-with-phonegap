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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import java.io.File;

public final class PagePhonegapPathSet extends WizardSection {

    // Set up storage for persistent initializers
    private final static String PHONEGAP_DIR = com.mds.apg.Activator.PLUGIN_ID + ".phonegap";  //$NON-NLS-1$
    private final static String PG_USE_INSTALLED = com.mds.apg.Activator.PLUGIN_ID + ".pguseinstalled"; //$NON-NLS-1$
    
    /** Last user-browsed location, static so that it be remembered for the whole session */ 
    private static String sPhonegapPathCache = ""; //$NON-NLS-1$
    
    private String mSampleSpot;
    private boolean mFromGitHub;
    private boolean mIsCordova;
    private String mPhonegapJs;
    private String mPhonegapJar;
    private String mInstallAndroidDir = "";
    private String mInstallExampleDir = "";
    
    // widgets
    Text mPhonegapPathField;
    private Button mUsePackagedPgRadio;
    private Composite mPgGroup;
    
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
        phonegapGroup.setLayout(new GridLayout());
        phonegapGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        phonegapGroup.setFont(parent.getFont());
        phonegapGroup.setText(Messages.PagePhonegapPathSet_Configuration);
        
        // Choose whether to use packaged PhoneGap or installed version
        
        boolean initialVal = doGetPreferenceStore().getString(PG_USE_INSTALLED) != ""; //$NON-NLS-1$
        mUsePackagedPgRadio = new Button(phonegapGroup, SWT.RADIO);
        mUsePackagedPgRadio.setText(Messages.PagePhonegapPathSet_BuildIn);
        mUsePackagedPgRadio.setSelection(!initialVal);
        mUsePackagedPgRadio.setToolTipText(Messages.PagePhonegapPathSet_BuildInTooltip);
        
        Button useInstalledPgRadio = new Button(phonegapGroup, SWT.RADIO);
        useInstalledPgRadio.setText(Messages.PagePhonegapPathSet_EnterPhoneGapPath);
        useInstalledPgRadio.setToolTipText(Messages.PagePhonegapPathSet_EnterPhoneGapPathTooltip);
        useInstalledPgRadio.setSelection(initialVal);
        
        SelectionListener location_listener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                enablePgWidgets(true);
                mWizardPage.validatePageComplete();
            }
        };
        mUsePackagedPgRadio.addSelectionListener(location_listener);
        useInstalledPgRadio.addSelectionListener(location_listener);
        
        // Hideable directory chooser for local PhoneGap installation

        mPgGroup = new Composite(phonegapGroup, SWT.NONE);
        mPgGroup.setLayout(new GridLayout(3, /* num columns */
            false /* columns of not equal size */));
            mPgGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            mPgGroup.setFont(parent.getFont());

        mPhonegapPathField = new Text(mPgGroup, SWT.BORDER);
        mPhonegapPathField.setText(getLocationSave());
        mPhonegapPathField.setToolTipText(Messages.PagePhonegapPathSet_PhoneGapPath);
        setupDirectoryBrowse(mPhonegapPathField, parent, mPgGroup);
        
        enablePgWidgets(false);  // to get the visibility and initial settings
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
    
    final String sampleSpot() {
        return mSampleSpot;
    }
    
    final String getInstallAndroidDir() {
        return mInstallAndroidDir;
    }
    
    final String getInstallExampleDir() {
        return mInstallExampleDir;
    }

    final String getPhonegapJsName() {
        return mPhonegapJs;
    }
    
    final String getPhonegapJarName() {
        return mPhonegapJar;
    }
    
    final boolean useFromPackaged() {
        return mUsePackagedPgRadio.getSelection();
    }
    
    final boolean fromGitHub() {
        return mFromGitHub;
    }
    
    final boolean isCordova() {
        return mIsCordova;
    }
    
    /**
     * Enables or disable the PhoneGap location widgets depending on the user selection:
     * the location path is enabled/disabled based on the radio selection 
     */
    
    void enablePgWidgets(boolean doUpdate) {
        boolean usePackaged = useFromPackaged();
        
        mPgGroup.setVisible(!usePackaged);
        if (doUpdate) {  
            update(null);
            doGetPreferenceStore().setValue(PG_USE_INSTALLED, usePackaged ? "" : "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
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
        mFromGitHub = false;
        mIsCordova = false;
        if (useFromPackaged()) {  // no validation necessary
            return AndroidPgProjectCreationPage.MSG_NONE;
        }
        String phonegapDirName = getValue();
        File phonegapDir = new File(phonegapDirName);
        if (!phonegapDir.exists() || !phonegapDir.isDirectory()) {
            return mWizardPage.setStatus(Messages.PagePhonegapPathSet_ErrorDirNameEmpty,  AndroidPgProjectCreationPage.MSG_ERROR);
        } else {
            String[] l = phonegapDir.list();
            if (l.length == 0) {
                return mWizardPage.setStatus(Messages.PagePhonegapPathSet_StatusDirNameEmpty, AndroidPgProjectCreationPage.MSG_ERROR);
            }
            boolean foundFramework = false;
            boolean foundExample = false;
            boolean foundAndroid = false;
            boolean foundBin = false;
            boolean foundLib = false;
            String androidDirName = null;
            File androidDir = null; 

            for (String s : l) {
                if (s.equals("example")) { //$NON-NLS-1$
                    foundExample = true;
                } else if (s.equals("framework")) { //$NON-NLS-1$
                    foundFramework = true;
                } else if (s.equals("Android")) { // pre PhoneGap 1.4 //$NON-NLS-1$
                    foundAndroid = true;                
                } else if (s.equals("lib")) { // post PhoneGap 1.4 //$NON-NLS-1$
                    androidDirName = phonegapDirName + "/lib/android"; //$NON-NLS-1$
                    androidDir = new File(androidDirName);
                    if (androidDir.exists()) {
                        foundAndroid = true;
                        foundLib = true;
                    }
                } else if (s.equals("bin")) {    // Post PhoneGap 1.1 GitHub //$NON-NLS-1$
                    foundBin = true;
                }
            }
            
            if (foundAndroid) {   // Pre and post 1.4 download directory structure 
                if (foundLib == false) {
                    androidDirName = phonegapDirName + "/Android"; //$NON-NLS-1$
                    androidDir = new File(androidDirName);
                }
                
                String[] al = androidDir.list();
                if (al.length == 0) {
                    return mWizardPage.setStatus(Messages.PagePhonegapPathSet_StatusDirEmpty, AndroidPgProjectCreationPage.MSG_ERROR);
                }
                mPhonegapJs = null;
                mPhonegapJar = null;
                boolean foundSample = false;

                for (String s : al) {
                    if (s.indexOf("phonegap") == 0) { //$NON-NLS-1$
                        if (s.endsWith(".js")) { //$NON-NLS-1$
                            mPhonegapJs = s;
                        } else if (s.endsWith(".jar")) { //$NON-NLS-1$
                            mPhonegapJar = s;
                        }
                    } else if (s.equals(foundLib ? "example" : "Sample")) { //$NON-NLS-1$
                        foundSample = true;
                    }
                }
                if ((mPhonegapJs == null) || (mPhonegapJar == null) || (!foundSample)) {
                    return mWizardPage.setStatus(String.format(Messages.PagePhonegapPathSet_ErrorNotFoundSampleDir ,androidDirName),
                            AndroidPgProjectCreationPage.MSG_ERROR);
                }
                mInstallAndroidDir = foundLib ? "/lib/android/" : "/Android/" ; //$NON-NLS-1$ //$NON-NLS-2$
                mInstallExampleDir = foundLib ? "/lib/android/example/" : "/Android/Sample/" ; //$NON-NLS-1$ //$NON-NLS-2$
                mSampleSpot = mInstallExampleDir + "assets/www"; //$NON-NLS-1$
                
            } else {  // Second the old or new github directory structure. the new can be phonegap or cordova
                if (((!foundFramework) || (!foundExample)) && (!foundBin)) {
                    return mWizardPage.setStatus(
                                Messages.PagePhonegapPathSet_ErrorInvalidLocation,
                                AndroidPgProjectCreationPage.MSG_ERROR);
                }
                mFromGitHub = true;
                if (foundExample) {
                    mSampleSpot = "/example";   //$NON-NLS-1$
                } else {
                    File wwwDir = new File(phonegapDirName + "/bin/templates/project/phonegap/templates/project/assets/www");//$NON-NLS-1$
                    if (wwwDir.exists()) {
                        mSampleSpot = "/bin/templates/project/phonegap/templates/project/assets/www";//$NON-NLS-1$
                    } else {
                        mSampleSpot = "/bin/templates/project/cordova/templates/project/assets/www";//$NON-NLS-1$
                        mIsCordova = true;
                    }
                }
            }
            // TODO more validation

            // We now have a good directory, so set example path and save value
            doGetPreferenceStore().setValue(PHONEGAP_DIR, getValue());

            return AndroidPgProjectCreationPage.MSG_NONE;
        }
    }
}