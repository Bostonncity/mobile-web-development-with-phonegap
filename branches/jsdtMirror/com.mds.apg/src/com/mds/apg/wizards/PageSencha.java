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
 * @author paulb
 */
public class PageSencha extends WizardSection {

    // Set up storage for persistent initializers
    private final static String SENCHA_DIR = com.mds.apg.Activator.PLUGIN_ID + ".senchadir";    
    private final static String SENCHA_CHECK = com.mds.apg.Activator.PLUGIN_ID + ".senchacheck";

    /** State variables */
    private String mSenchaPathCache = "";
 
    // widgets
    Button mSenchaCheck;
    private Button mSenchaKitchenSink;
    private Composite mSenchaGroup;
    Text mSenchaPathField;

    PageSencha(AndroidPgProjectCreationPage wizardPage, Composite parent) {
        super(wizardPage);
        mSenchaPathCache = doGetPreferenceStore().getString(SENCHA_DIR);        
        createGroup(parent);
    }

    /**
     * Creates the group for the Sencha options: [radio] Use Sencha
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
        group.setText("Sencha Touch");
        
        // Check box for choosing to include Sencha Touch

        mSenchaCheck = new Button(group, SWT.CHECK);
        mSenchaCheck.setText("Include Sencha Touch libraries in project");
        mSenchaCheck.setSelection(doGetPreferenceStore().getString(SENCHA_CHECK) != "");
        mSenchaCheck.setToolTipText("Check to use Sencha Touch mobile JavaScript framework\n"
                + "Note, you must already have downloaded Sencha Touch separately\n"
                + "See http://www.sencha.com/products/touch for more details");

        SelectionListener senchaListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                enableSenchaWidgets(true);
                boolean selection = mSenchaCheck.getSelection();
                if (selection) { // disable Sencha
                    mWizardPage.mJqmDialog.mJqmCheck.setSelection(false);
                    mWizardPage.mJqmDialog.enableJqmWidgets(false);
                }
                mWizardPage.validatePageComplete();
            }
        };
        mSenchaCheck.addSelectionListener(senchaListener);
        
        // Directory chooser for local Sencha installation

        mSenchaGroup = new Composite(group, SWT.NONE);
        mSenchaGroup.setLayout(new GridLayout(3, /* num columns */
            false /* columns of not equal size */));
            mSenchaGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            mSenchaGroup.setFont(parent.getFont());

        Label label = new Label(mSenchaGroup, SWT.NONE);
        label.setText("Sencha Location:");
        label.setToolTipText("Path where Sencha Touch is installed\n");

        mSenchaPathField = new Text(mSenchaGroup, SWT.BORDER);
        mSenchaPathField.setText(getLocationSave());
        setupDirectoryBrowse(mSenchaPathField, parent, mSenchaGroup);
        
        // Check box to seed project with Sencha Kitchen Sink app.
        // This should eventually be a scroll box like the Android sample seeder, 
        // But many of the other Sencha examples specific to tablets.
        
        mSenchaKitchenSink = new Button(group, SWT.CHECK);
        mSenchaKitchenSink.setText("Create project with Sencha Touch Kitchen Sink app");
        mSenchaKitchenSink.setSelection(false);
        mSenchaKitchenSink.setToolTipText("Create an Sencha"
                + " kitchen sink app populated from the Sencha installation");
        
        /**
         * Enables the Contents section based on the Kitchen Sink checkbox
         * Contents isn't needed if we're making a Sencha Kitchen Sink
         */

        SelectionListener senchaKsListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                mWizardPage.validatePageComplete();
            }
        };
        mSenchaKitchenSink.addSelectionListener(senchaKsListener);
        
        enableSenchaWidgets(false);  // to get the visibility and initial settings
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#getValue()
     */
    @Override
    String getValue() {
        return mSenchaPathField == null ? "" : mSenchaPathField.getText().trim();
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#getRawValue()
     */
    @Override
    Text getRawValue() {
        return mSenchaPathField;
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#getStaticSave()
     */
    @Override
    String getLocationSave() {
        return mSenchaPathCache;
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#setStaticSave(java.lang.String)
     */
    @Override
    void setLocationSave(String s) {
        mSenchaPathCache = s;
    }
    
    /** Returns the value of the "Include Sencha ..." checkbox. */
    protected boolean senchaChecked() {
        return mSenchaCheck.getSelection();
    }
    
    /** Returns the value of the "Create project with Sencha kitchen Sink checkbox */
    protected boolean useSenchaKitchenSink() {
        return mSenchaKitchenSink.getSelection();
    }

     
    /**
     * Enables or disable the Sencha widgets depending on the user selection:
     * the location path is enabled when using the "existing source" mode (i.e. not new project)
     * or in new project mode with the "use default location" turned off.
     */
    void enableSenchaWidgets(boolean doUpdate) {
        boolean senchaChecked = senchaChecked();
        mSenchaGroup.setVisible(senchaChecked);
        mSenchaKitchenSink.setVisible(senchaChecked);
        doGetPreferenceStore().setValue(SENCHA_CHECK, senchaChecked ? "true" : "");
        if (!senchaChecked) {
            mSenchaKitchenSink.setSelection(false);  // clear ks as well
        }  
        if (doUpdate) {          
            update(null);
        }
    }

    /*
     * @see com.mds.apg.wizards.WizardSection#validate()
     */
    @Override
    int validate() {
        if (!senchaChecked())
            return AndroidPgProjectCreationPage.MSG_NONE;

        File locationDir = new File(getValue());
        if (!locationDir.exists() || !locationDir.isDirectory()) {
            return mWizardPage.setStatus("A valid directory name that includes an uncompressed " +
                    "Sencha Touch installation should be specified in the 'Sencha Location' field.",
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // If the directory exists, make sure it's not empty
        String[] l = locationDir.list();
        if (l.length == 0) {
            return mWizardPage.setStatus(
                    "The directory is empty. It should be the location of your Sencha download",
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // make sure directory includes sencha-touch.js and resources.
        // If kitchen sink box is checked, make sure kitchen sink is in examples

        boolean foundSenchaJs = false;
        boolean foundResources = false;
        boolean foundExamples = false;

        for (String s : l) {
            if (s.equals("sencha-touch.js")) {
                foundSenchaJs = true;
            } else if (s.equals("resources")) {
                foundResources = true;
            } else if (s.equals("examples")) {
                foundExamples = true;
            }
        }
        if (!foundSenchaJs || !foundResources) {
            return mWizardPage.setStatus(getValue() + " must include a sencha-touch.js "
                    + "and resources directory", AndroidPgProjectCreationPage.MSG_ERROR);
        }
        if (!foundExamples && useSenchaKitchenSink()) {
            return mWizardPage.setStatus(
                    getValue() + " must include a kitchensink subdirectory "
                            + "in the examples directory", AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // TODO more validation

        // We now have a good directory, so set example path and save value
        doGetPreferenceStore().setValue(SENCHA_DIR, getValue());
        return AndroidPgProjectCreationPage.MSG_NONE;
    }
}
