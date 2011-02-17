package com.googlecode.jslint4java.eclipse.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.eclipse.ui.JSLintUIPlugin;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace
 * that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 * <p>
 * TODO: trigger a rebuild on change.
 */
public class OptionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String PAGE_TITLE = "jslint4java";

    private CheckboxTableViewer checkboxViewerStricter;
    private CheckboxTableViewer checkboxViewerLaxer;
    private CheckboxTableViewer checkboxViewerDir;

    private final List<FieldEditor> fieldEditors = new ArrayList<FieldEditor>();
    private final List<Option> booleanOptions = booleanOptions();
    private final List<String> directoryOptions = directoryOptions();

    public OptionsPreferencePage() {
        super(PAGE_TITLE);
        Option.setExcludeDirectoryOptions(directoryOptions);
        setPreferenceStore(JSLintUIPlugin.getDefault().getPreferenceStore());
    }

    /** Add a {@link FieldEditor}. */
    private void addFieldEditor(FieldEditor fieldEditor) {
        fieldEditor.setPage(this);
        fieldEditor.setPreferenceStore(getPreferenceStore());
        fieldEditors.add(fieldEditor);
    }

    /** Create and add an {@link IntegerFieldEditor}. */
    private void addIntegerFieldEditor(Composite parent, Option o) {
        addFieldEditor(new IntegerFieldEditor(nameOfPref(o), o.getDescription(), parent));
    }

    /** Create and add a {@link StringFieldEditor}. */
    private void addStringFieldEditor(Composite parent, Option o) {
        addFieldEditor(new StringFieldEditor(nameOfPref(o), o.getDescription(), parent));
    }

    /** Return a list of Options whose type is {@link Boolean}. */
    private List<Option> booleanOptions() {
        List<Option> options = new ArrayList<Option>();
        for (Option o : Option.values()) {
            if (o.getType() == Boolean.class) {
                options.add(o);
            }
        }
        return options;
    }
    
    /** Create Directory Options whose type is {@link Boolean}. */
    private List<String> directoryOptions() {
        List<String> dirOptions = new ArrayList<String>();
        dirOptions.add("phonegap");
        dirOptions.add("jquery.mobile");
        dirOptions.add("sencha");
        return dirOptions;
    }

    private CheckboxTableViewer createBooleansArea(Composite main, String title) {
        Font mainFont = main.getFont();
        Composite booleansParent = new Composite(main, SWT.NONE);
        booleansParent.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout decoratorsLayout = new GridLayout();
        decoratorsLayout.marginWidth = 0;
        decoratorsLayout.marginHeight = 0;
        booleansParent.setLayout(decoratorsLayout);
        booleansParent.setFont(mainFont);

        Label decoratorsLabel = new Label(booleansParent, SWT.NONE);
        decoratorsLabel.setText(title);
        decoratorsLabel.setFont(mainFont);

        CheckboxTableViewer checkboxViewer = CheckboxTableViewer.newCheckList(booleansParent, SWT.SINGLE | SWT.TOP
                | SWT.BORDER);
        checkboxViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        checkboxViewer.getTable().setFont(booleansParent.getFont());
        checkboxViewer.getTable().setFont(mainFont);
        return checkboxViewer;
    }
    
    private void createBooleanProviders(CheckboxTableViewer checkboxViewer) {
      
        checkboxViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return labelForOption((Option) element);
            }
        });

        checkboxViewer.setContentProvider(new IStructuredContentProvider() {
            public void dispose() {
                // Nothing to do on dispose
            }

            // Make an entry for each option
            public Object[] getElements(Object inputElement) {
                @SuppressWarnings("unchecked")
                List<Option> elements = (List<Option>) inputElement;
                return elements.toArray();
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });
    }
    
    private void createDirectoryProviders(CheckboxTableViewer checkboxViewer) {
        
        checkboxViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return (String)element;
            }
        });

        checkboxViewer.setContentProvider(new IStructuredContentProvider() {
            public void dispose() {
                // Nothing to do on dispose
            }

            // Make an entry for each option
            public Object[] getElements(Object inputElement) {
                @SuppressWarnings("unchecked")
                List<String> elements = (List<String>) inputElement;
                return elements.toArray();
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });
    }

    @Override
    protected Control createContents(Composite parent) {
        Font font = parent.getFont();

        Composite main = new Composite(parent, SWT.NULL);
        main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        main.setLayout(layout);
        main.setFont(font);

        checkboxViewerStricter = createBooleansArea(main, "Make JSLint STRICTER:");
        createBooleanProviders(checkboxViewerStricter);
        checkboxViewerLaxer = createBooleansArea(main, "Make JSLint LAXER:");
        createBooleanProviders(checkboxViewerLaxer);
        createOtherPrefsArea(main);
        checkboxViewerDir = createBooleansArea(main, "Exclude directories from JSLint:");
        createDirectoryProviders(checkboxViewerDir);
        populateBooleansArea();
        populateOtherPrefsArea();
        populateExcludeDirectoryArea();

        return main;
    }

    private void createOtherPrefsArea(Composite main) {
        Composite parent = new Composite(main, SWT.NONE);
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        parent.setLayout(new GridLayout());

        addIntegerFieldEditor(parent, Option.INDENT);
        addIntegerFieldEditor(parent, Option.MAXERR);
        addIntegerFieldEditor(parent, Option.MAXLEN);
        addStringFieldEditor(parent, Option.PREDEF);
    }

    public void init(IWorkbench workbench) {
    }

    /** Provide a label for a given option. */
    private String labelForOption(Option o) {
        return String.format("%s [%s]", o.getDescription(), o.getLowerName());
    }

    /** Read the value of a boolean pref. */
    private boolean loadBooleanPref(Option option) {
        return getPreferenceStore().getBoolean(nameOfPref(option));
    }
    
    /** Read the value of a boolean pref. */
    private boolean loadDirectoryPref(String option) {
        return getPreferenceStore().getBoolean(option);
    }

    /** The preference name an option should use. */
    private String nameOfPref(Option option) {
        return option.getLowerName();
    }

    /** Set each checkbox to its default value. */
    private void performBooleanDefaults() {
        for (Option o : booleanOptions) {
            boolean enabled = getPreferenceStore().getDefaultBoolean(nameOfPref(o));
            if (o.isStricter()) {
                checkboxViewerStricter.setChecked(o, enabled);
            } else {
                checkboxViewerLaxer.setChecked(o, enabled);
            }
        }
    }
    /** Set each checkbox to its default value. */
    private void performDirectoryDefaults() {
        for (String o : directoryOptions) {
            boolean enabled = getPreferenceStore().getDefaultBoolean(o);
            checkboxViewerDir.setChecked(o, enabled);
        }
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        performBooleanDefaults();
        performOtherDefaults();
        performDirectoryDefaults();
    }

    @Override
    public boolean performOk() {
        if (super.performOk()) {
            storeBooleanPrefs();
            storeOtherPrefs();
            storeDirectoryPrefs();
            return true;
        } else {
            return false;
        }
    }

    /** Load the default value for each non-boolean option. */
    private void performOtherDefaults() {
        for (FieldEditor fieldEditor : fieldEditors) {
            fieldEditor.loadDefault();
        }
    }

    /** Update checkboxes according to the values in the preference store. */
    private void populateBooleansArea() {
        List<Option> strictOptions = new ArrayList<Option>();;
        List<Option> laxOptions = new ArrayList<Option>();;
        for (Option option : booleanOptions) {
            if (option.isStricter()) {
                strictOptions.add(option);
            } else {
                laxOptions.add(option);
            }
        }
        checkboxViewerStricter.setInput(strictOptions);
        checkboxViewerLaxer.setInput(laxOptions);
        for (Option option : booleanOptions) {
            if (option.isStricter()) {
                checkboxViewerStricter.setChecked(option, loadBooleanPref(option));
            } else {
                checkboxViewerLaxer.setChecked(option, loadBooleanPref(option));
            }
        }
    }
    
    /** Update checkboxes according to the values in the preference store. */
    private void populateExcludeDirectoryArea() {
        checkboxViewerDir.setInput(directoryOptions);
        for (String option : directoryOptions) {
            checkboxViewerDir.setChecked(option, loadDirectoryPref(option));
        }
    }

    /** Fill in the values of the non-boolean preferences from the preferences store. */
    private void populateOtherPrefsArea() {
        for (FieldEditor fieldEditor : fieldEditors) {
            fieldEditor.load();
        }
    }

    /** Store a single option value in the preference store. */
    private void storeBooleanPref(Option option, boolean enabled) {
        getPreferenceStore().setValue(nameOfPref(option), enabled);
    }

    /** Store the values of each checkbox in the preferences store. */
    private void storeBooleanPrefs() {
        for (Option option : booleanOptions) {
            if (option.isStricter()) {
                storeBooleanPref(option, checkboxViewerStricter.getChecked(option));
            } else {
                storeBooleanPref(option, checkboxViewerLaxer.getChecked(option));
            }
        }
    }
    
    /** Store the values of each checkbox in the preferences store. */
    private void storeDirectoryPrefs() {
        for (String s : directoryOptions) {
            getPreferenceStore().setValue(s, checkboxViewerDir.getChecked(s));
        }
    }

    /** Store the values of the non-boolean preferences in the preferences store. */
    private void storeOtherPrefs() {
        for (FieldEditor fieldEditor : fieldEditors) {
            fieldEditor.store();
        }
    }

}