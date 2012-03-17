/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.jsdoc;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.wst.jsdt.jsdoc.msdn.IeFromMsdn;

// import com.sun.org.apache.xpath.internal.FoundIndex;

/**
 *
 */
/**
 * @author childsb
 *
 */
public class GenerateJsDoc {




	static final String outDirectory = "./libraries";
	static final String outFileName = "JScptBrowserObj.js";
	static final int REPLACE_FILE = 0;
	static final int SKIP_FILE = 1;
	static final int APPEND = 2;
	static final int LIB_FILE_ACTION = SKIP_FILE;
	static final boolean CLEAR_CACHE_ON_EXIT = false;
	static final boolean USE_CACHE=true;
	static final IJsDocSource source = new IeFromMsdn(USE_CACHE, !CLEAR_CACHE_ON_EXIT);
	static final String JS_PREFIX="IE_";


	public static String getOutFile(String fileName) {
		File outDir = new File(outDirectory);
		if(!outDir.exists()) outDir.mkdir();
		File outFile = new File(outDir.getAbsolutePath() + "/" + fileName);
		return outFile.getAbsolutePath();
	}

	public static void main(String[] args) {

		ElementInfo[] tops = source.getTopObjects();
		ArrayList allFoundNodes= new ArrayList();

		boolean workDone = true;
		while(workDone) {
			workDone = false;
			//allFoundNodes = new ArrayList();
			for(int i = 0;i<tops.length;i++) {
				String fileName = getOutFile(JS_PREFIX + tops[i].getName().toLowerCase().trim() + ".js");
				//String fileName = getOutFile(outFileName);
				File theFile = new File(fileName);
				switch(LIB_FILE_ACTION) {
					case SKIP_FILE:

						if(theFile.exists()) {
							System.out.println("Skipping output to file : " + theFile.getAbsolutePath());
							ElementInfo[] foundObjects = tops[i].getFoundObjects();
							for(int k = 0;k<foundObjects.length;k++) {
								if(!allFoundNodes.contains(foundObjects[k])) {
									workDone = true;
									allFoundNodes.add(foundObjects[k]);
								}
							}
							ElementInfo.freeObject(tops[i]);
							tops[i]=null;
							System.gc();
							continue;
						}
						break;
					case REPLACE_FILE:
					{

						if(theFile.exists()) theFile.delete();
						break;
					}
					case APPEND:


				}
				workDone = true;
				System.out.println("Writing Class '" + tops[i].getName() + "' to disk in " + fileName );
				tops[i].getChildren();
				String jsSctureture = tops[i].getJsStructure();
				Util.stringToFile(jsSctureture, fileName, true, false);

				ElementInfo[] foundObjects = tops[i].getFoundObjects();
				for(int k = 0;k<foundObjects.length;k++) {
					if(!allFoundNodes.contains(foundObjects[k])) {
						allFoundNodes.add(foundObjects[k]);
						workDone = true;
					}
				}
				ElementInfo.freeObject(tops[i]);
				tops[i]=null;
				System.gc();
			}
			System.out.println("Writing Collections and Events....");

			tops = (ElementInfo[])allFoundNodes.toArray(new ElementInfo[allFoundNodes.size()]);
		}

	}




}
