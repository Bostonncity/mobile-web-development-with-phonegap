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
    private final static String SENCHA_DIR = com.mds.apg.Activator.PLUGIN_ID + ".senchadir"; //$NON-NLS-1$
    private final static String SENCHA_CHECK = com.mds.apg.Activator.PLUGIN_ID + ".senchacheck"; //$NON-NLS-1$

    /** State variables */
    private String mSenchaPathCache = ""; //$NON-NLS-1$
 
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
        group.setText("Sencha Touch"); //$NON-NLS-1$
        
        // Check box for choosing to include Sencha Touch

        mSenchaCheck = new Button(group, SWT.CHECK);
        mSenchaCheck.setText(Messages.PageSencha_Include);
        mSenchaCheck.setSelection(doGetPreferenceStore().getString(SENCHA_CHECK) != ""); //$NON-NLS-1$
        mSenchaCheck.setToolTipText(Messages.PageSencha_IncludeTooltop);

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
        label.setText(Messages.PageSencha_Location);
        label.setToolTipText(Messages.PageSencha_LocationTooltip);

        mSenchaPathField = new Text(mSenchaGroup, SWT.BORDER);
        mSenchaPathField.setText(getLocationSave());
        setupDirectoryBrowse(mSenchaPathField, parent, mSenchaGroup);
        
        // Check box to seed project with Sencha Kitchen Sink app.
        // This should eventually be a scroll box like the Android sample seeder, 
        // But many of the other Sencha examples specific to tablets.
        
        mSenchaKitchenSink = new Button(group, SWT.CHECK);
        mSenchaKitchenSink.setText(Messages.PageSencha_KitchenSink);
        mSenchaKitchenSink.setSelection(false);
        mSenchaKitchenSink.setToolTipText(Messages.PageSencha_KitchenSinkTooltip);
        
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
        return mSenchaPathField == null ? "" : mSenchaPathField.getText().trim(); //$NON-NLS-1$
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
        doGetPreferenceStore().setValue(SENCHA_CHECK, senchaChecked ? "true" : ""); //$NON-NLS-1$ //$NON-NLS-2$
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
            return mWizardPage.setStatus(Messages.PageSencha_ErrorInvalidDirectory,
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // If the directory exists, make sure it's not empty
        String[] l = locationDir.list();
        if (l.length == 0) {
            return mWizardPage.setStatus(
                    Messages.PageSencha_ErrorDirectoryEmpty,
                    AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // make sure directory includes sencha-touch.js and resources.
        // If kitchen sink box is checked, make sure kitchen sink is in examples

        boolean foundSenchaJs = false;
        boolean foundResources = false;
        boolean foundExamples = false;

        for (String s : l) {
            if (s.equals("sencha-touch.js")) { //$NON-NLS-1$
                foundSenchaJs = true;
            } else if (s.equals("resources")) { //$NON-NLS-1$
                foundResources = true;
            } else if (s.equals("examples")) { //$NON-NLS-1$
                foundExamples = true;
            }
        }
        if (!foundSenchaJs || !foundResources) {
            return mWizardPage.setStatus(getValue() + Messages.PageSencha_ErrorNotFoundSenchaJS, AndroidPgProjectCreationPage.MSG_ERROR);
        }
        if (!foundExamples && useSenchaKitchenSink()) {
            return mWizardPage.setStatus(
                    getValue() + Messages.PageSencha_ErrorNotFoundKitchensink, AndroidPgProjectCreationPage.MSG_ERROR);
        }

        // TODO more validation

        // We now have a good directory, so set example path and save value
        doGetPreferenceStore().setValue(SENCHA_DIR, getValue());
        return AndroidPgProjectCreationPage.MSG_NONE;
    }
}
