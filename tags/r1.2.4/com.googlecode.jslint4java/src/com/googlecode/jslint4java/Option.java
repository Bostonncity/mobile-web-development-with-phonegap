package com.googlecode.jslint4java;

import java.util.List;
import java.util.Locale;

/**
 * All available options for tuning the behaviour of JSLint.
 *
 * TODO Add a "Handler" class for each type, which knows whether it needs an
 * arg, how to parse it, etc.
 *
 * @author dom
 */
public enum Option {
    // BEGIN-OPTIONS
    /** If adsafe should be enforced */
    ADSAFE("If adsafe should be enforced", Boolean.class, true),

    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed", Boolean.class, true),

    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", Boolean.class, false),

    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", Boolean.class, false),

    /** If continue should be tolerated */
    CONTINUE("If continue should be tolerated", Boolean.class, false),
    
    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", Boolean.class, false),

    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", Boolean.class, false),

    /** If logging should be allowed (console, alert, etc.) */
    DEVEL("If logging should be allowed (console, alert, etc.)", Boolean.class, false),

    /** If === should be required */
    EQEQEQ("If === should be required", Boolean.class, true),

    /** If es5 syntax should be allowed */
    ES5("If es5 syntax should be allowed", Boolean.class, false),

    /** If eval should be allowed */
    EVIL("If eval should be allowed", Boolean.class, false),

    /** If for in statements must filter */
    FORIN("Tolerate unfiltered forin", Boolean.class, false),

    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", Boolean.class, false),

    /** The number of spaces used for indentation (default is 4) */
    INDENT("The number of spaces used for indentation (default is 4)", Integer.class),

    /** The maximum number of warnings reported (default is 50) */
    MAXERR("The maximum number of warnings reported (default is 50)", Integer.class),

    /** Maximum line length */
    MAXLEN("Maximum line length", Integer.class),
    
    /** If block is required with for and while statements */
    NEEDCURLY("If block { ... } is required with for and while statements", Boolean.class, true),

    /** If constructor names must be capitalized */
    NEWCAP("If constructor names must be capitalized", Boolean.class, true),

    /** If names should be checked */
    NOMEN("If names should be checked", Boolean.class, true),
    
    /** if using `new` for side-effects should be disallowed */
    NONEW("if using `new` for side-effects should be disallowed", Boolean.class, true),

    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", Boolean.class, false),

    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed", Boolean.class, true),

    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", Boolean.class, true),

    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed", Boolean.class, true),

    /** The names of predefined global variables. */
    PREDEF("The names of predefined global variables.", StringArray.class),

    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals", Boolean.class, true),

    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", Boolean.class, false),

    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", Boolean.class, true),

    /** if semicolons are required */
    SEMIREQ("If semicolons are required", Boolean.class, true),

    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma", Boolean.class, true),

    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", Boolean.class, false),

    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used", Boolean.class, true),
    
    /** If variables should be declared before used */
    VARSATTOP("Disallow var declarations in for statements", Boolean.class, true),

    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply", Boolean.class, true),

    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", Boolean.class, false),

    /** If ms windows-specific globals should be predefined */
    WINDOWS("If ms windows-specific globals should be predefined", Boolean.class, false),

    // END-OPTIONS
    ;

    private String description;
    private Class<?> type;
    private boolean stricter;
    
    private Option(String description, Class<?> type, boolean stricter) {
        this.description = description;
        this.type = type;
        this.stricter = stricter;
    }
    
    private Option(String description, Class<?> type) {
        this.description = description;
        this.type = type;
    }

    /**
     * Return a description of what this option affects.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the lowercase name of this option.
     */
    public String getLowerName() {
        return name().toLowerCase(Locale.getDefault());
    }

    /**
     * What type does the value of this option have?
     */
    public Class<?> getType() {
        return type;
    }
    
    /**
     * Does the option make jslint stricter or looser?
     */
    public boolean isStricter() {
        return stricter;
    }

    /**
     * Calculate the maximum length of all of the {@link Option} names.
     *
     * @return the length of the largest name.
     */
    public static int maximumNameLength() {
        int maxOptLen = 0;
        for (Option o : values()) {
            int len = o.name().length();
            if (len > maxOptLen) {
                maxOptLen = len;
            }
        }
        return maxOptLen;
    }

    /**
     * Show this option and its description.
     */
    @Override
    public String toString() {
        return getLowerName() + "[" + getDescription() + "]";
    }
    
    /* Store list of directories to exclude from JSLint */
    
    private static List<String> excludeDirectoryOptions;
    
    public static void setExcludeDirectoryOptions(List<String> l) {
        excludeDirectoryOptions = l;
    }
    public static List<String> getExcludeDirectoryOptions() {
        return excludeDirectoryOptions;
    }
}
