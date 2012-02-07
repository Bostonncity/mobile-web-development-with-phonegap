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

/*
 * References:
 * org.com.android.ide.eclipse.adt.internal.wizards.newproject
 */

package com.mds.apg.wizards;

import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * AndroidProjectPgCreationPage is a project creation page that provides the
 * following fields:
 * <ul>
 * <li> phonegap directory
 * <li> location directory (of populating sources) name
 * </ul>
 */
public class AndroidPgProjectCreationPage extends WizardPage {

    // constants
    private static final String MAIN_PAGE_NAME = "newAndroidPgProjectPage"; 

    protected final static int MSG_NONE = 0;
    protected final static int MSG_WARNING = 1;
    protected final static int MSG_ERROR = 2;

    // page sub-sections
 
    PagePhonegapPathSet mPhonegapDialog;
    PageJqm mJqmDialog;
    PageSencha mSenchaDialog;
    PageInitContents mInitContentsDialog;
    
    protected Group mContentsSection; // Manipulate Contents Section visibility

    /**
     * Creates a new project creation wizard page.
     */
    public AndroidPgProjectCreationPage() {
        super(MAIN_PAGE_NAME);
        setPageComplete(false);
        setTitle("Create a PhoneGap for Android Project");
        setDescription("Specify PhoneGap installation, UI frameworks, and populating sources");
        ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin("com.mds.apg", "icons/mds.png");
        setImageDescriptor(desc);
    }

    /**
     * Overrides @DialogPage.setVisible(boolean) to put the focus in the project name when
     * the dialog is made visible.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            mPhonegapDialog.mPhonegapPathField.setFocus();
            validatePageComplete();
        }
    }

    // --- UI creation ---

    /**
     * Creates the top level control for this dialog page under the given parent
     * composite.
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setFont(parent.getFont());
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        initializeDialogUnits(parent);

        final Composite composite = new Composite(scrolledComposite, SWT.NULL);
        composite.setFont(parent.getFont());
        scrolledComposite.setContent(composite);

        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        mPhonegapDialog = new PagePhonegapPathSet(this, composite);
        mJqmDialog = new PageJqm(this, composite);
        mSenchaDialog = new PageSencha(this, composite);
        mInitContentsDialog = new PageInitContents(this, composite);

        scrolledComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(composite.computeSize(r.width, SWT.DEFAULT));
            }
        });

        // Show description the first time
        setErrorMessage(null);
        setMessage(null);
        setControl(scrolledComposite);

        // Validate. This will complain about the first empty field.
        validatePageComplete();
    }
  

    // --- UI Callbacks ----

    /**
     * Returns whether this page's controls currently all contain valid values.
     *
     * @return <code>true</code> if all controls are valid, and
     *         <code>false</code> if at least one is invalid - disables finish button
     * @throws IOException 
     */
    private boolean validatePage() {
        
        // First handle cross-section logic updates that need to happen regardless of validation 
        
        boolean checkLocation = true;
        boolean settingLocation = mInitContentsDialog.getContentSelection().equals("user");
        boolean contentsVisible = !(mJqmDialog.useJqmDemo() || mSenchaDialog.useSenchaKitchenSink());
        mContentsSection.setVisible(contentsVisible); // and make sure Contents is visible
        mInitContentsDialog.enableLocationWidgets(contentsVisible && settingLocation); // and location right state
        if (contentsVisible) {
            String withString;
            if (mJqmDialog.jqmChecked()) {
                withString = "with jQuery Mobile";
                if (!settingLocation) {
                    checkLocation = false;
                }
            } else if (mSenchaDialog.senchaChecked()) {
                withString = "with Sencha Touch";
                if (!settingLocation) {
                    checkLocation = false;
                }
            } else {
                // needs to be seeded with blanks so that there's space when it needs to appear (Issue 3)
                withString = "                                 ";
                if (mInitContentsDialog.getContentSelection().equals("example") && !mPhonegapDialog.useFromPackaged()) {
                    String gitSampleSpot = mPhonegapDialog.gitSampleSpot();
                    if (gitSampleSpot != null) {
                        mInitContentsDialog.update(mPhonegapDialog.getValue() + gitSampleSpot);
                    } else {
                        mInitContentsDialog.update(mPhonegapDialog.getValue() + "/Android/Sample/assets/www");
                    }
                }
            }
            mInitContentsDialog.mWithLabel.setText(withString);
        }

        if (mPhonegapDialog.validate() != MSG_NONE)  return false;
        
        if (mJqmDialog.validate() != MSG_NONE) return false;

        if (mJqmDialog.useJqmDemo()) {
            checkLocation = false;
        }
        if (mSenchaDialog.validate() != MSG_NONE) return false;
        
        if (mSenchaDialog.useSenchaKitchenSink()) {
            mInitContentsDialog.update(mSenchaDialog.getValue() + "/examples/kitchensink");
        }

        if (checkLocation) { 
            if (mPhonegapDialog.useFromPackaged()) checkLocation = settingLocation;
        }      
            
        mInitContentsDialog.locationVisibility(checkLocation);
        if (checkLocation) {
            if (mInitContentsDialog.validate() != MSG_NONE) return false;
        }

        setStatus(null, MSG_NONE);
        return true;
    }
    
    /**
     * Validates the page and updates the Next/Finish buttons
     */
    protected void validatePageComplete() {
        setPageComplete(validatePage()); 
    }


    /**
     * Sets the error message for the wizard with the given message icon.
     *
     * @param message The wizard message type, one of MSG_ERROR or MSG_WARNING.
     * @return As a convenience, always returns messageType so that the caller can return
     *         immediately.
     */
    protected int setStatus(String message, int messageType) {
        if (message == null) {
            setErrorMessage(null);
            setMessage(null);
        } else if (!message.equals(getMessage())) {
            setMessage(message, messageType == MSG_WARNING ? WizardPage.WARNING : WizardPage.ERROR);
        }
        return messageType;
    }
    
    /**
     * Give access to super's private to helper functions
     *
     */

    protected GridData setButtonLayoutData(Button button) {
        return super.setButtonLayoutData(button);
    }

}
