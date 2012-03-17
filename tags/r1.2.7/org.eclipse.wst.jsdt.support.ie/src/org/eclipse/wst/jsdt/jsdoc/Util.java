/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.jsdoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

/**
 *
 */
/**
 * @author childsb
 *
 */
public class Util {
	public static final String XSL_HEADER = "<?xml version=\"1.0\"?> <xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">";
	public static final String XSL_FOOTER = "</xsl:stylesheet>";
	public static final String NEW_LINE = System.getProperty("line.separator");
	private static final String BUFFER_DIR = "./webCache";
	public static final boolean VERBOSE = true;

	private static File getTempDir() {
		File tempDir = new File(BUFFER_DIR);
		if (tempDir.exists()) return tempDir;
		tempDir.mkdir();
		return tempDir;
	}

	public static String retrieveFromUrl(String url, boolean useCache, boolean deleteOnExit) throws IOException {
		System.gc();
		String buffFile = getTempDir().getAbsolutePath() + "\\" + toUniqueFileName(url);
		String text = null;
		if (useCache) {
			try {
				text = fileToString(buffFile);
			} catch (IOException e) {
			}
			if (text != null) {
				// System.out.println("Found file in cache..");
				return text;
			}
		}
		StringBuffer pageText = new StringBuffer();
		URL location = new URL(url);
		URLConnection yc = location.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			pageText.append(inputLine);
		in.close();
		if (useCache) {
			if (VERBOSE) System.out.println("Caching URL.. " + url);
			stringToFile(pageText.toString(), buffFile, false, deleteOnExit);
		}
		return pageText.toString();
	}

	public static String toUniqueFileName(String url) {
		String temp = url.replace('/', '_');
		temp = temp.replace('\\', '_');
		temp = temp.replace(':', '_');
		temp = temp.replace('#', '_');
		temp = temp.replace('?', '_');
		temp = temp.replace('%', '_');
		temp = temp.replace('=', '_');
		temp = temp.replace('&', '_');
		temp = temp.replace(';', '_');
		temp = temp.replace('(', '_');
		temp = temp.replace(')', '_');
		temp = temp.replace('\'', '_');
		temp = temp.replace(',', '_');
		temp = temp.replace('$', '_');
		return temp;
	}

	public static String retrieveFromUrlFixEncode(String url, boolean useBuffer, boolean deleteOnExit) throws IOException {
		String encoding = "ISO-8859-1";
		/* Changes all UTF-8 and UTF-16 encoding to stated encoding string */
		String text = retrieveFromUrl(url, useBuffer, deleteOnExit);
		text = text.replaceAll("UTF-8", encoding);
		text = text.replaceAll("UTF-16", encoding);
		return text;
	}

	public static String applyTranslation(String text, File translation) throws MappingException {
		XSLTMap map = new XSLTMap(translation);
		return map.applyMap(text);
	}

	public static File dataToTempFile(String data) {
		File temp = null;
		try {
			// Create temp file.
			temp = File.createTempFile(System.currentTimeMillis() + "_tmp", ".tmp");
			// Delete temp file when program exits.
			temp.deleteOnExit();
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(data);
			out.close();
		} catch (IOException e) {
		}
		return temp;
	}

	public static String fileToString(String fileName) throws IOException {
		System.gc();
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		//BufferedInputStream bis = null;
		BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
		// dis.available() returns 0 if the file does not have more lines.
		StringBuffer buff = new StringBuffer();
		String line = null;
		while ((line = dis.readLine()) != null) {
			buff.append(line);
		}
		// dispose all the resources after using them.
		fis.close();
		dis.close();
		return buff.toString();
	}

	public static File stringToFile(String data, String fileName, boolean appendToEnd, boolean deleteOnExit) {
		File temp = null;
		System.gc();
		try {
			// Create temp file.
			temp = new File(fileName);
			if(deleteOnExit) temp.deleteOnExit();
			// Delete temp file when program exits.
			// temp.deleteOnExit();
			// Write to temp file
			FileWriter writer = new FileWriter(temp, appendToEnd);
			BufferedWriter out = new BufferedWriter(writer);
			StringTokenizer st = new StringTokenizer(data, "\n");
			String fullLine = null;
			while (st.hasMoreTokens()) {
				fullLine = st.nextToken();
				if (appendToEnd) {
					out.write(fullLine /* + Util.NEW_LINE */);
				} else {
					out.write(fullLine /* + Util.NEW_LINE */);
				}
			}
			out.close();
		} catch (IOException e) {
		}
		return temp;
	}

	public static String getBaseUrl(String url) {
		int last = url.lastIndexOf('/');
		if (last < 0) return url;
		return url.substring(0, last);
	}
}
