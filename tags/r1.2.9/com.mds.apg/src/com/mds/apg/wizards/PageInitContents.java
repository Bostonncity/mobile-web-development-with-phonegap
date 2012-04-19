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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import java.io.File;

public final class PageInitContents extends WizardSection{
    
    // Set up storage for persistent initializers

    private final static String SOURCE_DIR = com.mds.apg.Activator.PLUGIN_ID + ".source";  //$NON-NLS-1$
    private final static String CONTENT_SELECTION = com.mds.apg.Activator.PLUGIN_ID + ".contentselection"; //$NON-NLS-1$
    private final static String PURE_IMPORT = com.mds.apg.Activator.PLUGIN_ID + ".pureimport"; //$NON-NLS-1$

    /** Last user-browsed location */
    private String mLocationCache;  
    private String mContentSelection;  // example, minimal, or user
    private boolean mPureImport;
    
    // widgets

    private Button mBrowseButton;
    private Label mLocationLabel;
    private Text mLocationPathField;
    Label mWithLabel;

    PageInitContents(AndroidPgProjectCreationPage wizardPage, Composite parent) {
        super(wizardPage);
        mLocationCache = doGetPreferenceStore().getString(SOURCE_DIR);  
        mContentSelection = doGetPreferenceStore().getString(CONTENT_SELECTION); 
        if (mContentSelection.equals("")) mContentSelection = "example"; //$NON-NLS-1$ //$NON-NLS-2$
        createGroup(parent);
    }
    
    /**
     * Creates the group for the Project options:
     * [radio] Use example source from phonegap directory
     * [radio] Create project from existing sources
     * Location [text field] [browse button]
     *
     * @param parent the parent composite
     */
    protected final void createGroup(Composite parent) {
        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setLayout(new GridLayout(2, /* num columns */ false /* columns of not equal size */));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setFont(parent.getFont());
        group.setText(Messages.PageInitContents_ProjectContents);
        mWizardPage.mContentsSection = group; // Visibility can be adjusted by other widgets

        final Button createFromExampleRadio = new Button(group, SWT.RADIO);
        createFromExampleRadio.setText(Messages.PageInitContents_UseExample);
        createFromExampleRadio.setSelection(mContentSelection.equals("example")); //$NON-NLS-1$
        createFromExampleRadio.setToolTipText(Messages.PageInitContents_UseExampleTooltip);
        
        // Label for showing the example is with JQM or Sencha
        mWithLabel = new Label(group, SWT.NONE);
        
        final Button minimalProject = new Button(group, SWT.RADIO);
        minimalProject.setText(Messages.PageInitContents_MnimalProject);
        minimalProject.setToolTipText(Messages.PageInitContents_MinimalProjectTooltip);
        minimalProject.setSelection(mContentSelection.equals("minimal")); //$NON-NLS-1$
        
        new Label(group, SWT.NONE); // dummy to force new line
        
        final Button existing_project_radio = new Button(group, SWT.RADIO);
        existing_project_radio.setText(Messages.PageInitContents_SpecifiedSourceDir);
        existing_project_radio.setToolTipText(Messages.PageInitContents_SpecifiedSourceDirTooltip);
        boolean doSetLocation = mContentSelection.equals("user"); //$NON-NLS-1$
        existing_project_radio.setSelection(doSetLocation);
        
        // Check box to do a pure import (versus adding in phonegap.js,etc. and making changes to it)

        final Button pureImport = new Button(group, SWT.CHECK);
        pureImport.setText(Messages.PageInitContents_PureImport);
        boolean initPure = doGetPreferenceStore().getString(PURE_IMPORT) != ""; //$NON-NLS-1$
        pureImport.setSelection(initPure);
        pureImport.setToolTipText(Messages.PageInitContents_PureImportTooltip);
        pureImport.setVisible(doSetLocation);
        mPureImport = doSetLocation && initPure;
        
        SelectionListener location_listener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                mContentSelection = createFromExampleRadio.getSelection() ? "example" : minimalProject.getSelection() ? "minimal" : "user"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                boolean pureImportVal = pureImport.getSelection();
                pureImport.setVisible(mContentSelection.equals("user")); //$NON-NLS-1$
                doGetPreferenceStore().setValue(CONTENT_SELECTION, mContentSelection);
                mPureImport = pureImportVal && mContentSelection.equals("user"); //$NON-NLS-1$
                doGetPreferenceStore().setValue(PURE_IMPORT, mPureImport ? "true" : ""); //$NON-NLS-1$ //$NON-NLS-2$
                mWizardPage.validatePageComplete();
            }
        };

        createFromExampleRadio.addSelectionListener(location_listener);
        minimalProject.addSelectionListener(location_listener);
        existing_project_radio.addSelectionListener(location_listener);
        pureImport.addSelectionListener(location_listener);
        
        Composite location_group = new Composite(parent, SWT.NONE);
        location_group.setLayout(new GridLayout(3, /* num columns */
                false /* columns of not equal size */));
        location_group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        location_group.setFont(parent.getFont());

        mLocationLabel = new Label(location_group, SWT.NONE);
        mLocationLabel.setText(Messages.PageInitContents_Location);
        mLocationPathField = new Text(location_group, SWT.BORDER);  
        mLocationPathField.setText(getLocationSave());
        mBrowseButton = setupDirectoryBrowse(mLocationPathField, parent, location_group);
        enableLocationWidgets(doSetLocation);  
    }

    // --- Internal getters & setters ------------------

    @Override
    final String getValue() {
        return mLocationPathField == null ? "" : mLocationPathField.getText().trim(); //$NON-NLS-1$
    }
    
    @Override
    final Text getRawValue() {
        return mLocationPathField; 
    }
    
    @Override
    final String getLocationSave() {
        return mLocationCache;
    }
    
    @Override
    final void setLocationSave(String s) {
        mLocationCache = s;
    }

    /** Returns the value of the "Create from Existing Sample" radio. */
    /* TODO - Simplify like senchaChecked */
    
    protected String getContentSelection() {
        return mContentSelection;
    }
    
    protected boolean isPureImport() {
        return mPureImport;
    }
    
    void locationVisibility(boolean v) {
        mLocationLabel.setVisible(v);
        mLocationPathField.setVisible(v);
    }

    // --- UI Callbacks ----
    
    /**
     * Enables or disable the location widgets depending on the user selection:
     * the location path is enabled when using the "existing source" mode (i.e. not new project)
     * or in new project mode with the "use default location" turned off.
     */
    void enableLocationWidgets(boolean locationEnabled) {
        mLocationLabel.setEnabled(locationEnabled);
        mLocationPathField.setEnabled(locationEnabled);
        mBrowseButton.setVisible(locationEnabled);
    }
    
    protected void enableLocationWidgets() {
        enableLocationWidgets(mContentSelection.equals("user")); //$NON-NLS-1$
    }
        
    /**
     * Validates the location path field.
     *
     * @return The wizard message type, one of MSG_ERROR, MSG_WARNING or MSG_NONE.
     */
    protected int validate() {
        File locationDir = new File(getValue());
        if (!locationDir.exists() || !locationDir.isDirectory()) {
            return mWizardPage.setStatus(Messages.PageInitContents_ErrorLocationInvalid,
                AndroidPgProjectCreationPage.MSG_ERROR);
        } else {
            String[] l = locationDir.list();
            if (l.length == 0) {
                return mWizardPage.setStatus(Messages.PageInitContents_ErrorLocationEmpty,
                        AndroidPgProjectCreationPage.MSG_ERROR);
            }

            boolean foundIndexHtml = false;
            boolean foundSencha = false;
            boolean foundJqm = false;
            boolean foundPhonegapJs = false;

            for (String s : l) {
                if (s.equals("index.html")) { //$NON-NLS-1$
                    foundIndexHtml = true;
                } else if (s.equals("sencha")) { //$NON-NLS-1$
                    foundSencha = true;
                } else if (s.equals("jquery.mobile")) { //$NON-NLS-1$
                    foundJqm = true;
                } else if (s.equals("phonegap.js")) { //$NON-NLS-1$
                    foundPhonegapJs = true;
                }
            }
            if (!foundIndexHtml && !mPureImport) {
                return mWizardPage.setStatus(Messages.PageInitContents_ErrorIndexHTMLNotFound,
                        AndroidPgProjectCreationPage.MSG_ERROR);
            }
            if (foundSencha && mWizardPage.mSenchaDialog.senchaChecked()) {
                return mWizardPage.setStatus(Messages.PageInitContents_ErrorNotFoundSenchaDir,
                        AndroidPgProjectCreationPage.MSG_ERROR);
            }
            if (foundJqm && mWizardPage.mJqmDialog.jqmChecked()) {
                return mWizardPage.setStatus(Messages.PageInitContents_ErrorNotFoundJQMDir,
                        AndroidPgProjectCreationPage.MSG_ERROR);
            }
            if (foundPhonegapJs && !mPureImport) {
                return mWizardPage.setStatus(Messages.PageInitContents_Location + getValue() +
                        Messages.PageInitContents_ErrorNotFoundPhoneGapJSFile,
                        AndroidPgProjectCreationPage.ERROR);
            }
                
            // TODO more validation
            
            // We now have a good directory, so set example path and save value
            doGetPreferenceStore().setValue(SOURCE_DIR, getValue()); 
            
            return AndroidPgProjectCreationPage.MSG_NONE;
        }
    }
}