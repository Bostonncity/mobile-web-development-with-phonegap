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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Set up the jQuery Mobile part of the Wizard page
 * 
 * @author paulb
 */
public class PageJqm extends WizardSection {

    // Set up storage for persistent initializers
    private final static String JQM_DIR = com.mds.apg.Activator.PLUGIN_ID + ".jqmdir";     //$NON-NLS-1$
    private final static String JQM_CHECK = com.mds.apg.Activator.PLUGIN_ID + ".jqmcheck"; //$NON-NLS-1$
    private final static String JQM_USE_INSTALLED = com.mds.apg.Activator.PLUGIN_ID + ".jqmuseinstalled"; //$NON-NLS-1$

    /** state variables */
    private String mJqmPathCache;
 
    // widgets
    Button mJqmCheck;
    private Composite mJqmGroup;
    private Button mUsePackagedJqmRadio;
    private Button mUseInstalledJqmRadio;
    private Button mJqmDemo;
    Text mJqmPathField;
    String mJqmVersion;

    PageJqm(AndroidPgProjectCreationPage wizardPage, Composite parent) {
        super(wizardPage);
        mJqmVersion = null;
        mJqmPathCache = doGetPreferenceStore().getString(JQM_DIR); 
        createGroup(parent);
    }

    /**
     * Creates the group for the Jqm options: [radio] Use Jqm
     * [check] Use default location
     * Location [text field] [browse button]
     * 
     * @param parent the parent composite
     */
    @Override
    protected void createGroup(Composite parent) {

        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        // Layout has 4 columns of non-equal size
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setFont(parent.getFont());
        group.setText("jQuery Mobile"); //$NON-NLS-1$
        
        // Check box for choosing to include Jqm Touch

        mJqmCheck = new Button(group, SWT.CHECK);
        mJqmCheck.setText(Messages.PageJqm_IncludeLibrary);
        mJqmCheck.setSelection(doGetPreferenceStore().getString(JQM_CHECK) != ""); //$NON-NLS-1$
        mJqmCheck.setToolTipText(Messages.PageJqm_URLforJQM);

        SelectionListener jqmListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                enableJqmWidgets(true);
                boolean selection = mJqmCheck.getSelection();
                if (selection) { // disable Sencha
                    mWizardPage.mSenchaDialog.mSenchaCheck.setSelection(false);
                    mWizardPage.mSenchaDialog.enableSenchaWidgets(false);
                }
                mWizardPage.validatePageComplete();  // otherwise unchecking jqm won't remove its errors
            }
        };
        mJqmCheck.addSelectionListener(jqmListener);
        
        // Choose whether to use packaged jqm or installed version
        
        boolean initialVal = doGetPreferenceStore().getString(JQM_USE_INSTALLED) != "";  //$NON-NLS-1$
        mUsePackagedJqmRadio = new Button(group, SWT.RADIO);
        mUsePackagedJqmRadio.setText(Messages.PageJqm_UseBuiltIn);
        mUsePackagedJqmRadio.setSelection(!initialVal);
        mUsePackagedJqmRadio.setToolTipText(Messages.PageJqm_UseBuiltInTooltip);
        
        mUseInstalledJqmRadio = new Button(group, SWT.RADIO);
        mUseInstalledJqmRadio.setText(Messages.PageJqm_UserSeparate);
        mUseInstalledJqmRadio.setToolTipText(Messages.PageJqm_UseSeparateTooltip);
        mUseInstalledJqmRadio.setSelection(initialVal);
        
        SelectionListener location_listener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                enableJqmWidgets(true);
                mWizardPage.validatePageComplete();
            }
        };
        mUsePackagedJqmRadio.addSelectionListener(location_listener);
        mUseInstalledJqmRadio.addSelectionListener(location_listener);
        
        // Directory chooser for local Jqm installation

        mJqmGroup = new Composite(group, SWT.NONE);
        mJqmGroup.setLayout(new GridLayout(3, /* num columns */
            false /* columns of not equal size */));
            mJqmGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            mJqmGroup.setFont(parent.getFont());
            
        Label locationLabel = new Label(mJqmGroup, SWT.NONE);
        locationLabel.setText(Messages.PageJqm_jQMLocation);
        locationLabel.setToolTipText(Messages.PageJqm_InstalledPath);

        mJqmPathField = new Text(mJqmGroup, SWT.BORDER);
        mJqmPathField.setText(getLocationSave());
        setupDirectoryBrowse(mJqmPathField, parent, mJqmGroup);
        
        // Check box to seed project with jQuery Mobile UI demo
        // This should eventually be a scroll box like the Android sample seeder, 
        // to enable other samples
        
        mJqmDemo = new Button(group, SWT.CHECK);
        mJqmDemo.setText(Messages.PageJqm_CreateDemo);
        mJqmDemo.setSelection(false);
        mJqmDemo.setToolTipText(Messages.PageJqm_CreateDemoTooltip);
        
        /**
         * Enables the Contents section based on the jQuery Mobile UI demo checkbox
         * Contents isn't needed if we're making a jQuery Mobile UI demo
         */

        SelectionListener jqmDemoListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                mWizardPage.validatePageComplete();
            }
        };
        mJqmDemo.addSelectionListener(jqmDemoListener);
                   
        enableJqmWidgets(false);  // to get the visibility and initial settings
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#getValue()
     */
    @Override
    String getValue() {
        return mJqmPathField == null ? "" : mJqmPathField.getText().trim(); //$NON-NLS-1$
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#getRawValue()
     */
    @Override
    Text getRawValue() {
        return mJqmPathField;
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#getStaticSave()
     */
    @Override
    String getLocationSave() {
        return mJqmPathCache;
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#setStaticSave(java.lang.String)
     */
    @Override
    void setLocationSave(String s) {
        mJqmPathCache = s;
    }
    
    /** Returns the value of the "Include Jqm ..." checkbox. */
    protected boolean jqmChecked() {
        return mJqmCheck.getSelection();
    }
    
    protected boolean useFromPackaged() {
        return mUsePackagedJqmRadio.getSelection();
    }
    
    /** Returns the value of the "Create project with JQM demo checkbox */
    protected boolean useJqmDemo() {
        return mJqmDemo.getSelection();
    }
    
    
    /**
     * Enables or disable the Jqm widgets depending on the user selection
     */
    void enableJqmWidgets(boolean doUpdate) {
        boolean jqmChecked = jqmChecked();
        boolean usePackaged = useFromPackaged();
        
        mUsePackagedJqmRadio.setVisible(jqmChecked);
        mUseInstalledJqmRadio.setVisible(jqmChecked);
        mJqmDemo.setVisible(jqmChecked);
        mJqmGroup.setVisible(jqmChecked && !usePackaged);

        if (!jqmChecked) {
            mJqmDemo.setSelection(false);  // clear demo as well
        } 
        doGetPreferenceStore().setValue(JQM_CHECK, jqmChecked ? "true" : ""); //$NON-NLS-1$ //$NON-NLS-2$

        if (doUpdate) {  
            update(null);
            doGetPreferenceStore().setValue(JQM_USE_INSTALLED, usePackaged ? "" : "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#validate()
     */
    @Override
    int validate() {
        if (!jqmChecked() || useFromPackaged()) return AndroidPgProjectCreationPage.MSG_NONE;

        File locationDir = new File(getValue());
        if (!locationDir.exists() || !locationDir.isDirectory()) {
            return mWizardPage.setStatus(Messages.PageJqm_ErrorInvalidDirectory,
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // If the directory exists, make sure it's not empty
        String[] l = locationDir.list();
        if (l.length == 0) {
            return mWizardPage.setStatus(
                    Messages.PageJqm_ErrorDirectoryEmpty,
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // make sure directory includes right js, css and images

        boolean foundJqmJs = false;
        boolean foundCss = false;
        boolean foundImages = false;
        String version = ""; //$NON-NLS-1$
        boolean versionError = false;

        for (String s : l) {    
            if (s.equals("images")) { //$NON-NLS-1$
                foundImages = true;
                continue;
            }
            final String jQm = "jquery.mobile"; //$NON-NLS-1$
            if (s.indexOf(jQm) == 0){
                String[] tokens = s.split("\\.(?=[^\\.]+$)"); //$NON-NLS-1$
                if (tokens.length <= 1)
                    continue;
                String extension = tokens[1];
                if (extension.equals("js")) { //$NON-NLS-1$
                    foundJqmJs = true;
                } else if (extension.equals("css")) { //$NON-NLS-1$
                    foundCss = true;
                } else {
                    continue;
                }
                // Get and Check version on found js or css file
                String baseName = tokens[0];
                // strip min
                int endIndex = baseName.length();
                if (baseName.substring(endIndex-4).equals(".min")) { //$NON-NLS-1$
                    endIndex -= 4;
                }
                String tempVersion = baseName.substring(baseName.indexOf('-'), endIndex);
                if (version == "") { //$NON-NLS-1$
                    version = tempVersion;
                } else if (!tempVersion.equals(version)) {
                    versionError = true; 
                }
            }
        }
        if (!foundJqmJs || !foundCss || !foundImages) {
            return mWizardPage.setStatus(String.format(Messages.PageJqm_ErrorNotFoundJQMFiles ,getValue()),
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }
        if (versionError) {
            return mWizardPage.setStatus(String.format(Messages.PageJqm_ErrorNotSameVersion ,getValue()),
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // TODO more validation
        
        // We now have a good directory, so set jqm path and save value
        mJqmVersion = version;
        doGetPreferenceStore().setValue(JQM_DIR, getValue());
        return AndroidPgProjectCreationPage.MSG_NONE;

    }
}
