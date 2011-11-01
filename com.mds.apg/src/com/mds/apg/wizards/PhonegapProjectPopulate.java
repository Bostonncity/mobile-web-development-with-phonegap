/*
 * Copyright (C) 2010-11 Mobile Developer Solutions
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
 * Platform Plug-in Developer Guide > Programmer's Guide > Dialogs and wizards
 */

package com.mds.apg.wizards;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

class PhonegapProjectPopulate {

    /**
     * Creates the actual project(s). This is run asynchronously in a different
     * thread.
     * 
     * @param monitor An existing monitor.
     * @param mainData Data for main project. Can be null.
     * @throws InvocationTargetException to wrap any unmanaged exception and
     *             return it to the calling thread. The method can fail if it
     *             fails to create or modify the project or if it is canceled by
     *             the user.
     */
    static void createProjectAsync(IProgressMonitor monitor, PageInfo pageInfo)
           throws InvocationTargetException {
        monitor.beginTask("Create Android Project", 100);
        try {
            updateProjectWithPhonegap(monitor, pageInfo);

        } catch (CoreException e) {
            throw new InvocationTargetException(e);
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        } catch (URISyntaxException e) {
            throw new InvocationTargetException(e);
        } finally {
            monitor.done();
        }
    }

    /**
     * Driver for the various tasks to add phonegap. 
     * 1. Update the main java file to load index.html 
     * 2. Get the phonegap.jar and update the classpath
     * 3. Get the user's sources into the android project 
     * 4. Handle project add-ins like Sencha and JQuery Mobile
     * 5. Update the AndroidManifest file 
     * 6. Fill the res directory with drawables and layout
     * 7. Update the project nature so that JavaScript files are recognize 
     * 8. Refresh the project with the updated disc files 
     * 9. Do a clean build - TODO is clean build still necessary with ADT 8.0.1?
     * 
     * @param monitor An existing monitor.
     * @throws InvocationTargetException to wrap any unmanaged exception and
     *             return it to the calling thread. The method can fail if it
     *             fails to create or modify the project or if it is canceled by
     *             the user.
     */

    static private void updateProjectWithPhonegap(IProgressMonitor monitor, PageInfo pageInfo)
            throws CoreException, IOException, URISyntaxException {

        updateJavaMain(pageInfo.mDestinationDirectory);
        getPhonegapJar(monitor, pageInfo);
        getWWWSources(monitor, pageInfo);
        if (pageInfo.mJqmChecked)
            setupJqm(monitor, pageInfo);
        if (pageInfo.mSenchaChecked)
            setupSencha(monitor, pageInfo);
        phonegapizeAndroidManifest(pageInfo);
        getResFiles(monitor, pageInfo);
        IProject newAndroidProject = pageInfo.mAndroidProject;
        addJsNature(monitor, newAndroidProject);
        newAndroidProject.refreshLocal(2 /* DEPTH_INFINITE */, monitor);
        newAndroidProject.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
    }

    /**
     * Find and update the main java file to kick off phonegap
     * 
     * @throws IOException
     */
    static private void updateJavaMain(String destDir) throws IOException {
        String javaFile = findJavaFile(destDir + "src");
        String javaFileContents = StringIO.read(javaFile);

        // Import com.phonegap instead of Activity
        javaFileContents = javaFileContents.replace("import android.app.Activity;",
                "import com.phonegap.*;");

        // Change superclass to DroidGap instead of Activity
        javaFileContents = javaFileContents.replace("extends Activity", "extends DroidGap");

        // Change to start with index.html
        javaFileContents = javaFileContents.replace("setContentView(R.layout.main);",
                "super.loadUrl(\"file:///android_asset/www/index.html\");");

        // Write out the file
        StringIO.write(javaFile, javaFileContents);
    }

    // Recursively search for java file. Assuming there is only one in the new
    // Android project

    static private String findJavaFile(String dir) {
        String retVal;
        File f = new File(dir);
        if (f.isDirectory()) {
            String fList[] = f.list();
            for (String s : fList) {
                if (s.length() > 5 && s.indexOf(".java") == s.length() - 5) {
                    return dir + '/' + s;
                } else {
                    retVal = findJavaFile(dir + '/' + s);
                    if (retVal != null)
                        return retVal;
                }
            }
        }
        return null;
    }

    /**
     * If using github install, phonegap.jar does not yet exist in a raw phonegap
     * installation It needs to be built with the Android installation. So
     * instead, we'll get the sources, so that it just gets build with our
     * product. We also need to reference /framework/libs/commons-codec-1.3.jar upon
     * which the sources depend
     * 
     * For a non-github install, just point at phonegap.jar
     * 
     * @throws URISyntaxException
     */
    static private void getPhonegapJar(IProgressMonitor monitor, PageInfo pageInfo) throws CoreException,
            IOException, URISyntaxException {
        
        if (pageInfo.mPackagedPhonegap) {
            addDefaultDirectories(pageInfo.mAndroidProject, "", new String[] { "libs"  }, monitor);
            String libsDir = pageInfo.mDestinationDirectory + "/libs/";
            bundleCopy("/resources/phonegap/jar", libsDir);
            updateClasspath(monitor,
                    pageInfo.mAndroidProject, 
                    libsDir + "/phonegap.jar",
                    null); 
            
        } else if (pageInfo.mFromGitHub) {  // TODO - make phonegap.jar come from a separate project in user's install
            String toDir = pageInfo.mDestinationDirectory + "/src";
            FileCopy.recursiveCopy(pageInfo.mPhonegapDirectory + "/framework/src", toDir);
            updateClasspath(monitor,
                    pageInfo.mAndroidProject, 
                    pageInfo.mPhonegapDirectory + "/framework/libs/commons-codec-1.3.jar",
                    new Path(toDir));        
        } else { // not from github
            updateClasspath(monitor, 
                    pageInfo.mAndroidProject, 
                    pageInfo.mPhonegapDirectory + "/Android/" + pageInfo.mPhonegapJar,
                    null);      
        }
    }
    
    /**
     * Update the classpath with thanks to Larry Isaacs in  
     * http://dev.eclipse.org/newslists/news.eclipse.webtools/msg10002.html
     * @throws CoreException 
     * 
     * @throws URISyntaxException
     */
    
    static private void updateClasspath(IProgressMonitor monitor, IProject androidProject, String jarFile, Path srcLoc) throws CoreException {
        
        IJavaProject javaProject = (IJavaProject) androidProject.getNature(JavaCore.NATURE_ID);

        IClasspathEntry[] classpathList = javaProject.readRawClasspath();
        IClasspathEntry[] newClasspaths = new IClasspathEntry[classpathList.length + 1];
        System.arraycopy(classpathList, 0, newClasspaths, 0, classpathList.length);

        // Create the new Classpath entry

        IClasspathEntry newPath = JavaCore.newLibraryEntry(new Path(jarFile), srcLoc, null);
        newClasspaths[classpathList.length] = newPath;

        // write it back out 
        javaProject.setRawClasspath(newClasspaths, monitor);
    }

    /**
     * Get the sources from the example directory or alternative specified
     * directory Place them in assets/www Also get phonegap.js from framework
     * assets
     * 
     * @throws URISyntaxException
     */
    static private void getWWWSources(IProgressMonitor monitor, PageInfo pageInfo) throws CoreException,
            IOException, URISyntaxException {

        addDefaultDirectories(pageInfo.mAndroidProject, "assets/", new String[] {
            "www"  }, monitor);
        String wwwDir = pageInfo.mDestinationDirectory + "/assets/www/";

        boolean doCopy = true;
        if (pageInfo.mUseJqmDemo) {
            bundleCopy("/resources/jqm/demo2", wwwDir);
            doCopy = false;
        } else if (pageInfo.mJqmChecked) {
            if (pageInfo.mUseExample) {
                bundleCopy("/resources/jqm/phonegapExample", wwwDir);
                doCopy = false;
            }
        } else if (pageInfo.mSenchaChecked) {
            if (pageInfo.mUseExample && !pageInfo.mSenchaKitchenSink) {
                bundleCopy("/resources/sencha/phonegapExample", wwwDir);
                doCopy = false;
            }
        } 
        
        if (doCopy) {
            if (pageInfo.mPackagedPhonegap && pageInfo.mUseExample && !pageInfo.mSenchaKitchenSink) {
                bundleCopy("resources/phonegap/Sample", wwwDir);
            } else {
                FileCopy.recursiveCopy(pageInfo.mSourceDirectory, wwwDir);
            }
        }
        
        String phonegapJsFileName;
        
        class isPhoneGapFile implements FileFilter {
            public boolean accept(File f) {
                String name = f.getName();
                return name.indexOf("phonegap") >= 0 && name.indexOf("phonegapdemo") < 0;
            }
        }
        
        if (pageInfo.mPackagedPhonegap) {
            bundleCopy("resources/phonegap/js", wwwDir);
            phonegapJsFileName = (new File(wwwDir)).listFiles(new isPhoneGapFile())[0].getName();
            
        } else if (pageInfo.mFromGitHub) {

            // Even though there is a phonegap.js file in the directory
            // framework/assets/www, it is WRONG!!
            // phonegap.js must be constructed from the files in
            // framework/assets/js

            FileCopy.createPhonegapJs(pageInfo.mPhonegapDirectory + "/framework/"
                    + "assets/js", wwwDir + "phonegap.js");
            phonegapJsFileName = "phonegap.js";

        } else { // www.phonegap.com/download
            phonegapJsFileName = pageInfo.mPhonegapJs;
            if (pageInfo.mUseExample && !pageInfo.mSenchaKitchenSink) { 
                // copy phonegap{version}.js to phonegap.js
                if (pageInfo.mJqmChecked || pageInfo.mSenchaChecked) {  // otherwise already there
                    FileCopy.copy(pageInfo.mPhonegapDirectory + "/Android/" + pageInfo.mPhonegapJs,
                            wwwDir + pageInfo.mPhonegapJs);
                }
            } else { // otherwise keep the name, since the user controls the
                     // index.html (and don't overwrite if user supplied the phonegap.js)
                FileCopy.copyDontOverwrite(pageInfo.mPhonegapDirectory + "/Android/"
                        + pageInfo.mPhonegapJs, wwwDir + pageInfo.mPhonegapJs);
            }
        }
        
        // Make sure index.html has the right phonegap.js
        String indexHtmlContents = StringIO.read(wwwDir + "index.html");
        indexHtmlContents = indexHtmlContents.replaceFirst("src=\"phonegap[a-zA-Z-.0-9]*js\"",
                "src=\"" + phonegapJsFileName + "\"");  
        if (indexHtmlContents.indexOf("src=\"phonegap") < 0) {   // no phonegap*.js in file
            int index = indexHtmlContents.lastIndexOf("</head>");
            if (index > 0) {
                index = indexHtmlContents.lastIndexOf("</script>", index);
                if (index > 0) {
                    index += 9; 
                    indexHtmlContents = indexHtmlContents.substring(0,index) + 
                            "\n\t<!-- Uncomment following line to access PhoneGap APIs (not necessary to use PhoneGap to package web app) -->\n" + 
                            "\t<!-- <script type=\"text/javascript\" charset=\"utf-8\" src=\"" + phonegapJsFileName + "\"></script>-->\n" +
                            indexHtmlContents.substring(index) ;
                }
            }
        }        
        StringIO.write(wwwDir + "index.html", indexHtmlContents);

        if (pageInfo.mSenchaKitchenSink) { // delete the confusing index_android.html
            try {
                File f = new File(wwwDir + "index_android.html");
                f.delete();
            } catch (Exception e) { // Ignore any exceptions here
            }
        }
    }

    /**
     * Set up contents if jQuery Mobile is selected
     * 
     * @throws URISyntaxException
     */
    static private void setupJqm(IProgressMonitor monitor, PageInfo pageInfo) throws CoreException,
            IOException, URISyntaxException {

        addDefaultDirectories(pageInfo.mAndroidProject, "assets/www/", new String[] {
            "jquery.mobile"}, monitor);
        
        String jqmDir = pageInfo.mDestinationDirectory + "assets/www/jquery.mobile/";
        String fromJqmDir = pageInfo.mJqmDirectory;
        String version;
        if (fromJqmDir == null) {  // get from plugin installation
            version = "-1.0rc2";  // TODO - do this programmatically
            bundleCopy("/resources/jqm/jquery.mobile", jqmDir);
        } else {
            version = pageInfo.mJqmVersion;
            FileCopy.recursiveCopy(fromJqmDir, jqmDir);
        }

        bundleCopy("/resources/jqm/supplements", jqmDir);

        // Update the index.html with path to the js and css files
        
        String file = pageInfo.mDestinationDirectory + "/" + "assets/www/index.html";
        String fileContents = FileStringReplace.replace(file, "\\{\\$jqmversion\\}", version);
        
        fileContents = updatePathInHtml(fileContents, "jquery.mobile" + version, 
                ".css\"", "\"jquery.mobile/", pageInfo.mSourceDirectory, null);
        fileContents = updatePathInHtml(fileContents, "jquery.mobile" + version, 
                ".js\"", "\"jquery.mobile/", pageInfo.mSourceDirectory, null);
        
        // and jquery file
        fileContents = updatePathInHtml(fileContents, "jquery-1.6.4", 
                ".js\"", "\"jquery.mobile/", pageInfo.mSourceDirectory, ".min\"");
        
        // Add CDN comments for jQuery Mobile
        fileContents = fileContents.replace("</head>",  "\n\t<!-- CDN Respositories: For production, replace lines above with these uncommented minified versions -->\n" +
                "\t<!-- <link rel=\"stylesheet\" href=\"http://code.jquery.com/mobile/1.0rc2/jquery.mobile-1.0rc2.min.css\" />-->\n" +
                "\t<!-- <script src=\"http://code.jquery.com/jquery-1.6.4.min.js\"></script>-->\n" +
                "\t<!-- <script src=\"http://code.jquery.com/mobile/1.0rc2/jquery.mobile-1.0rc2.min.js\"></script>-->\n\t</head>");
        
        // Write out the file
        StringIO.write(file, fileContents);
    }
        

    /**
     * Get sencha-touch.js and resources directory. Add references to them in
     * index.html If kitchen sink is selected, so other copies TBD
     * 
     * @throws URISyntaxException
     */
    static private void setupSencha(IProgressMonitor monitor, PageInfo pageInfo) throws CoreException,
            IOException, URISyntaxException {

        addDefaultDirectories(pageInfo.mAndroidProject, "assets/www/", new String[] {
            "sencha"  }, monitor);
        addDefaultDirectories(pageInfo.mAndroidProject, "assets/www/sencha/", new String[] {
            "resources" }, monitor);
        
        String senchaDir = pageInfo.mDestinationDirectory + "/" + "assets/www/sencha/";

        // The .scss files confuse JSDT on Linux
        FileCopy.recursiveCopySkipSuffix(pageInfo.mSenchaDirectory + "/resources", senchaDir + "resources",".scss");

        // Now copy the sencha-touch*.js
        FileCopy.copy(pageInfo.mSenchaDirectory + "/sencha-touch.js", senchaDir);
        FileCopy.copy(pageInfo.mSenchaDirectory + "/sencha-touch-debug.js", senchaDir);
        FileCopy.copy(pageInfo.mSenchaDirectory + "/sencha-touch-debug-w-comments.js", senchaDir);

        // Update the index.html with path to sencha-touch.css and sencha-touch.js
        String file = pageInfo.mDestinationDirectory + "/" + "assets/www/index.html";
        String fileContents = StringIO.read(file);

        fileContents = updatePathInHtml(fileContents, "sencha-touch", ".css\"", "\"sencha/resources/css/", pageInfo.mSourceDirectory, null);
        fileContents = updatePathInHtml(fileContents, "sencha-touch", ".js\"", "\"sencha/", pageInfo.mSourceDirectory, null);

        // Write out the file
        StringIO.write(file, fileContents);
    }
    

    /**
     * Get the Android Manifest file and tweak it for phonegap
     * 
     * @throws URISyntaxException
     */
    static private void phonegapizeAndroidManifest(PageInfo pageInfo) throws CoreException,
            IOException, URISyntaxException {

        String destFile = pageInfo.mDestinationDirectory + "AndroidManifest.xml";
        String sourceFileContents;
        if (pageInfo.mPackagedPhonegap) {
            sourceFileContents = bundleGetFileAsString("/resources/phonegap/AndroidManifest.xml");
        } else {
            String sourceFile;
            if (pageInfo.mFromGitHub) {
                sourceFile = pageInfo.mPhonegapDirectory + "/framework/AndroidManifest.xml";
            } else {
                sourceFile = pageInfo.mPhonegapDirectory + "/Android/Sample/AndroidManifest.xml";
            }
            sourceFileContents = StringIO.read(sourceFile);
        }
        String manifestInsert = getManifestScreensAndPermissions(sourceFileContents);
        String destFileContents = StringIO.read(destFile);

        // Add phonegap screens, permissions and turn on debuggable
        destFileContents = destFileContents.replaceFirst("<application\\s+android:", manifestInsert
                + "<application" + " android:debuggable=\"true\" android:");

        // Add android:configChanges="orientation|keyboardHidden" to the activity
        destFileContents = destFileContents.replaceFirst("<activity\\s+android:",
                "<activity android:configChanges=\"orientation|keyboardHidden\" android:");
        
        // Copy additional activities from source to destination - especially the DroidGap activity
        int activityIndex = sourceFileContents.indexOf("<activity");
        int secondActivityIndex = sourceFileContents.indexOf("<activity", activityIndex + 1);
        if (secondActivityIndex > 0) {
            int endIndex = sourceFileContents.lastIndexOf("</activity>");
            destFileContents = destFileContents.replace("</activity>", "</activity>\n\t\t" + 
                    sourceFileContents.substring(secondActivityIndex, endIndex + 11));
        }

        if (destFileContents.indexOf("<uses-sdk") < 0) {
            // User did not set min SDK, so use the phonegap template manifest version
            int startIndex = sourceFileContents.indexOf("<uses-sdk");
            int endIndex = sourceFileContents.indexOf("<", startIndex + 1);
            destFileContents = destFileContents.replace("</manifest>",
                    sourceFileContents.substring(startIndex, endIndex) + "</manifest>");
        }
        // Write out the file
        StringIO.write(destFile, destFileContents);
    }

    /**
     * Helper Function for phonegapizeAndroidManifest It finds the big middle
     * section that needs to be added to the manifest for phonegap
     */

    static private String getManifestScreensAndPermissions(String manifest) {
        int startIndex;
        startIndex = manifest.indexOf("<supports-screens");
        if (startIndex == -1)
            startIndex = manifest.indexOf("<uses-permissions");
        if (startIndex == -1)
            return null;
        int index = startIndex;
        int lastIndex;
        do {
            lastIndex = index;
            index = manifest.indexOf("<uses-permission", lastIndex + 1);
            if (index < 0) {  // <uses-feature added in PhoneGap 1.0.0 manifest
                index = manifest.indexOf("<uses-feature", lastIndex + 1);
            }
        } while (index > 0);
        lastIndex = manifest.indexOf('<', lastIndex + 1);
        return manifest.substring(startIndex, lastIndex);
    }

    /**
     * Copy anything in res/layout Copy drawable to drawable* Leave values alone
     * since string maps to app name
     * 
     * @throws URISyntaxException
     */
    static private void getResFiles(IProgressMonitor monitor, PageInfo pageInfo) throws CoreException,
            IOException, URISyntaxException {
        String destResDir = pageInfo.mDestinationDirectory + "res" + "/";

        if (pageInfo.mPackagedPhonegap) {
            bundleCopy("/resources/phonegap/layout", pageInfo.mDestinationDirectory + "/res/layout/");
            bundleCopy("/resources/phonegap/res", pageInfo.mDestinationDirectory + "/res/");  // xml directory

            // Copy resource drawable to all of the project drawable* directories
            File destDir = new File(destResDir);
            String fList[] = destDir.list();
            for (String s : fList) {
                if (s.indexOf("drawable") == 0) {
                    File drawableDir = new File(destResDir + s);
                    String fList2[] = drawableDir.list();
                    for (String f : fList2) {
                        if (f.endsWith(".png")) {  // SDK Tools 14 moved default image from icon.png to ic_launcher.png, so this code is now more generic
                            InputStream sourceDrawable = bundleGetFileAsStream("/resources/phonegap/icons/mdspgicon.png");
                            FileCopy.coreStreamCopy(sourceDrawable, new File(destResDir + s + "/" + f));
                        }
                     }
                }
            }
            return;
        }
        String sourceResDir;
        if (pageInfo.mFromGitHub) {
            sourceResDir = pageInfo.mPhonegapDirectory + "/framework/res/";
        } else {
            sourceResDir = pageInfo.mPhonegapDirectory + "/Android/Sample/res/";
        }

        FileCopy.recursiveForceCopy(sourceResDir + "layout/", destResDir + "layout/");
        if (FileCopy.exists(sourceResDir + "xml/")) {
            FileCopy.recursiveCopy(sourceResDir + "xml/", destResDir + "xml/");
        }

        if (pageInfo.mFromGitHub) {
            // Copy source drawable to all of the project drawable* directories
            String sourceDrawableDir = sourceResDir + "drawable" + "/";
            File destFile = new File(destResDir);
            String fList[] = destFile.list();
            for (String s : fList) {
                if (s.indexOf("drawable") == 0) {
                    FileCopy.recursiveForceCopy(sourceDrawableDir, destResDir + s);
                }
            }
        } else { // the drawables are already in the final directories
            FileCopy.recursiveForceCopy(sourceResDir + "drawable-hdpi", destResDir + "drawable-hdpi");
            FileCopy.recursiveForceCopy(sourceResDir + "drawable-ldpi", destResDir + "drawable-ldpi");
            FileCopy.recursiveForceCopy(sourceResDir + "drawable-mdpi", destResDir + "drawable-mdpi");
        }
    }

    /**
     * Adds default directories to the project. Unchanged from private version
     * in parent class
     * 
     * @param project The Java Project to update.
     * @param parentFolder The path of the parent folder. Must end with a
     *            separator.
     * @param folders Folders to be added.
     * @param monitor An existing monitor.
     * @throws CoreException if the method fails to create the directories in
     *             the project.
     */
    static private void addDefaultDirectories(IProject project, String parentFolder, String[] folders,
            IProgressMonitor monitor) throws CoreException {
        for (String name : folders) {
            if (name.length() > 0) {
                IFolder folder = project.getFolder(parentFolder + name);
                if (!folder.exists()) {
                    folder.create(true /* force */, true /* local */, new SubProgressMonitor(
                            monitor, 10));
                }
            }
        }
    }

    /**
     * Add JavaScript nature to the project. It gets added last after Android
     * and Java ones.
     * 
     * @throws CoreException
     */
    static private void addJsNature(IProgressMonitor monitor, IProject project) throws CoreException {

        final String JS_NATURE = "org.eclipse.wst.jsdt.core.jsNature";
        if (!project.hasNature(JS_NATURE)) {

            IProjectDescription description = project.getDescription();
            String[] natures = description.getNatureIds();
            String[] newNatures = new String[natures.length + 1];

            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[natures.length] = JS_NATURE;

            description.setNatureIds(newNatures);
            project.setDescription(description, new SubProgressMonitor(monitor, 10));
        }
    }
    
    static private String updatePathInHtml(String fileContents, String fileName, 
            String suffix, String prepend, String indexHtmlDirectory, String suffixOverride) throws IOException {

        String fullName = fileName + ".min"+ suffix;  
        int fileNameIndex = fileContents.indexOf(fullName);
        if (fileNameIndex <= 0) {
            fullName = fileName + ".min"; // No .js ok for min to get around eclipse issues with min files
            fileNameIndex = fileContents.indexOf(fullName);
        }
        if (fileNameIndex <= 0) {
            fullName = fileName + "-debug" + suffix;
            fileNameIndex = fileContents.indexOf(fullName);
        }
        if (fileNameIndex <= 0) {
            fullName = fileName + suffix;
            fileNameIndex = fileContents.indexOf(fullName);
        }
        if (fileNameIndex > 0) {   // Found it
            int startIncludeIndex = fileContents.lastIndexOf("\"", fileNameIndex);
            fileContents = fileContents.substring(0, startIncludeIndex) + prepend
                    + fileContents.substring(fileNameIndex);
        } else { // must add a new line.  Bug if indexOf finds stuff inside comments
            int firstJsIndex = fileContents.indexOf(suffix);
            int insertSpot;
            if (firstJsIndex > 0) {
                insertSpot = fileContents.lastIndexOf('<', firstJsIndex);
            } else {
                insertSpot = fileContents.indexOf("</head>");
            }
            if (insertSpot <= 0) {
                throw new IOException("Supplied index.html in " + indexHtmlDirectory + 
                        "  is missing the </head> tag");
            }
            // adjust insertSpot back to end of last line
            while (Character.isWhitespace(fileContents.charAt(--insertSpot))) ;
            insertSpot++;

            if (suffix.equals(".js\"")) {
                fileContents = fileContents.substring(0, insertSpot)
                    + "\n      <script type=\"text/javascript\" src=" + prepend + 
                    (suffixOverride != null ? (fileName + suffixOverride) : fullName) + "></script>"  + 
                    fileContents.substring(insertSpot);
            } else if (suffix.equals(".css\"")) {
                fileContents = fileContents.substring(0, insertSpot)
                    + "\n      <link rel=\"stylesheet\" href=" + prepend + 
                    fullName + " type=\"text/css\">"
                    + fileContents.substring(insertSpot);              
            } else {
                throw new IllegalArgumentException("updatePathInHtml called with unsupported suffix");
            }
        }
        return fileContents;
    }
    
    static private void bundleCopy(String dir, String destination) 
        throws IOException, URISyntaxException {
        
        Bundle bundle = com.mds.apg.Activator.getDefault().getBundle();
        Enumeration<URL> en = bundle.findEntries(dir, "*", true);
        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            String pathFromBase = url.getPath().substring(dir.length()+1);
            String toFileName = destination + pathFromBase;
            if (toFileName.indexOf("/.svn/") >= 0) continue;
            File toFile = new File(toFileName);

            if (pathFromBase.lastIndexOf('/') == pathFromBase.length() - 1) {
                // This is a directory - create it
                if (!toFile.mkdir()) {
                    throw new IOException("bundleCopy: " + "directory Creation Failed: " + toFileName);
                }
            } else {
                // This is a file - copy it
                FileCopy.coreStreamCopy(url.openStream(), toFile);      
            }        
        }
    }
    
    static private InputStream bundleGetFileAsStream(String fileName) throws IOException,
            URISyntaxException {

        Bundle bundle = com.mds.apg.Activator.getDefault().getBundle();
        URL url = bundle.getEntry(fileName);
        return url.openStream();
    }
    
    static private String bundleGetFileAsString(String fileName) throws IOException,
            URISyntaxException {

        return StringIO.convertStreamToString(bundleGetFileAsStream(fileName));
    }   
}