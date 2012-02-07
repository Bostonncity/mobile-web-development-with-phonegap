// thanks to http://www.java2s.com/Code/Java/File-Input-Output/CopyfilesusingJavaIOAPI.htm

package com.mds.apg.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileCopy {

    public static void recursiveCopy(String fromFileName, String toFileName) throws IOException {
        copy (fromFileName, toFileName, true, false, null, false);
    }
    
    public static void recursiveCopySkipSuffix(String fromFileName, String toFileName, String skip) throws IOException {
        copy (fromFileName, toFileName, true, false, skip, false);
    }

    public static void recursiveForceCopy(String fromFileName, String toFileName) throws IOException {
        copy (fromFileName, toFileName, true, true, null, false);
    }

    public static void forceCopy(String fromFileName, String toFileName) throws IOException{
        copy (fromFileName, toFileName, false, true, null, false);
    }    

    public static void copy(String fromFileName, String toFileName) throws IOException{
        copy (fromFileName, toFileName, false, false, null, false);
    }

    public static void copyDontOverwrite(String fromFileName, String toFileName) throws IOException{
        copy (fromFileName, toFileName, false, false, null, true);      
    }

    private static void copy(String fromFileName, String toFileName, boolean isRecursive, 
            boolean force, String skipSuffix, boolean dontOverwrite)   throws IOException {
        
        if (skipSuffix != null) {
            int dotSpot = fromFileName.lastIndexOf('.');
            if (dotSpot > 0 && fromFileName.substring(dotSpot).equals(skipSuffix)) {
                return;
            }
        }
        
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists())
            throw new IOException("FileCopy: " + "no such source file: "
                    + fromFileName);

        if (isRecursive && fromFile.isDirectory()) {
            if (toFile.exists()) {
                if (!toFile.isDirectory()) {
                    throw new IOException("FileCopy: " + "Cannot copy directory to non-directory: "
                            + toFileName);
                }
            } else { // create the directory
                toFile = new File(toFileName);
                if (!toFile.mkdir()) {
                    throw new IOException("FileCopy: " + "directory Creation Failed: "
                            + toFileName);
                }
            }                
            String fList[] = fromFile.list();
            for (String s : fList) {
                copy(fromFileName + "/" + s, toFileName + "/" + s, true, force, skipSuffix, dontOverwrite);
            }
            return;
        }

        if (!fromFile.isFile())
            throw new IOException("FileCopy: " + "can't copy directory: "
                    + fromFileName);
        if (!fromFile.canRead())
            throw new IOException("FileCopy: " + "source file is unreadable: "
                    + fromFileName);

        if (toFile.isDirectory()) 
            toFile = new File(toFile, fromFile.getName());

        if (toFile.exists()) {
            if (!toFile.canWrite())
                throw new IOException("FileCopy: "
                        + "destination file is unwriteable: " + toFileName);
            if (dontOverwrite) {
                return;
            }
            if (!force) {
                throw new IOException("FileCopy: "
                        + "trying to overwrite an existing file" + toFileName);
            }
        } else {
            String parent = toFile.getParent();
            if (parent == null)
                parent = System.getProperty("user.dir");
            File dir = new File(parent);
            if (!dir.exists())
                throw new IOException("FileCopy: "
                        + "destination directory doesn't exist: " + parent);
            if (dir.isFile())
                throw new IOException("FileCopy: "
                        + "destination is not a directory: " + parent);
            if (!dir.canWrite())
                throw new IOException("FileCopy: "
                        + "destination directory is unwriteable: " + parent);
        }
        coreStreamCopy(new FileInputStream(fromFile), toFile);
    }
    
    public static void coreStreamCopy(InputStream from, File toFile) throws IOException {
        FileOutputStream to = null;
        try {
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
                if (to != null)
                    try {
                        to.close();
                    } catch (IOException e) {
                        ;
                    }
        }
    }
    
    
    public static void createPhonegapJs(String fromDirName, String toFileName)
    throws IOException {
        File fromFile = new File(fromDirName);
        File toFile = new File(toFileName);

        if (!fromFile.exists()) {
            throw new IOException("createPhonegapJs: " + "no such source file: "
                    + fromDirName);
        }
        FileOutputStream to = new FileOutputStream(toFile);
        try {
            to = new FileOutputStream(toFile);
            File base = new File(fromDirName + "/" + "phonegap.js.base");
            if (!base.exists()) base = new File(fromDirName + "/" + "cordova.js.base");
            to = appendStream(base, to);

            String fList[] = fromFile.list();
            for (String s : fList) {   // append the .js files
                int i = s.lastIndexOf(".js");
                if (i > 0 && i == s.length() - 3) {
                    to = appendStream(new File(fromDirName + "/" + s), to);
                }
            }
        } finally {
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }
    
    public static boolean exists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
    
    private static FileOutputStream appendStream(File fromFile, FileOutputStream to) throws IOException {
        
        FileInputStream from = null;
        try {
            from = new FileInputStream(fromFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
        return to;
    }
}




