package com.googlecode.jslint4java.eclipse.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.eclipse.JSLintLog;
import com.googlecode.jslint4java.eclipse.JSLintPlugin;

/**
 * Provide a fully configured instance of {@link JSLint} on demand.
 */
public class JSLintProvider {

    private final JSLintBuilder builder = new JSLintBuilder();

    private JSLint jsLint;
    private IEclipsePreferences usePreferenceStore;

    /**
     * Set up a listener for preference changes. This will ensure that the instance of
     * {@link JSLint} that we have is kept in sync with the users choices. We do this by ensuring
     * that a new JSLint will be created and configured on the next request.
     */
    public void init() {
        // This code doesn't work because InstanceScope is different than ProjectScope
        // If IProject were available here, it would be good, but not obvious how to get
        // it. Thus doing initialization with IProject in getJsLint
        
//        IEclipsePreferences x = new InstanceScope().getNode(JSLintPlugin.PLUGIN_ID);
//        x.addPreferenceChangeListener(new IPreferenceChangeListener() {
//            public void preferenceChange(PreferenceChangeEvent ev) {
//                jsLint = null;
//                JSLintLog.info("pref %s changed; nulling jsLint", ev.getKey());
//            }
//        });
    }

    /**
     * Return a fully configured instance of JSLint. This should not be cached; each use should call
     * this method.
     * @param project 
     */
    public JSLint getJsLint(IProject project) {
        if (jsLint == null) {
            // TODO: Allow for non-default versions of fulljslint.js.
            jsLint = builder.fromDefault();
            if (usePreferenceStore == null) {
                usePreferenceStore = new ProjectScope(project).getNode(JSLintPlugin.PLUGIN_ID);

                usePreferenceStore.addPreferenceChangeListener(new IPreferenceChangeListener() {
                    public void preferenceChange(PreferenceChangeEvent ev) {
                        jsLint = null;
                        JSLintLog.info("pref %s changed; nulling jsLint", ev.getKey());
                    }
                });
            }
            configure();
        }
        return jsLint;
    }
    
    public String getOptionFromPref(String opt) {
        String ret;
        IEclipsePreferences prefStore = usePreferenceStore;
        ret = prefStore.get(opt, null);
        if (ret != null) {
            return ret;
        } else {
            IPreferencesService prefService = Platform.getPreferencesService();
            return prefService.getString(JSLintPlugin.PLUGIN_ID, opt, null, null);
        } 
    }

    /** Set up the current instance of JSLint using the current preferences. */
    public void configure() {
        JSLint lint = jsLint;
        lint.resetOptions();
        IEclipsePreferences prefStore = usePreferenceStore;
        IPreferencesService prefService = Platform.getPreferencesService();

        for (Option o : Option.values()) {
            String value = prefStore.get(o.getLowerName(), null);
            if (value != null) {
//                JSLintLog.info("addOption %s is %s", o.getLowerName(), value);
                lint.addOption(o, value);
            } else { // Check if default to set
                value = prefService.getString(JSLintPlugin.PLUGIN_ID, o.getLowerName(), null, null);
                if (value != null) {
                    lint.addOption(o, value);
//                    JSLintLog.info("addOption from Service %s is %s", o.getLowerName(), value);
                }
            }
        }
    }
}
