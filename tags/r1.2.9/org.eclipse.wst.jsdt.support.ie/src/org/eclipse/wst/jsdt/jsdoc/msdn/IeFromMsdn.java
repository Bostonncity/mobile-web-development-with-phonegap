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
package org.eclipse.wst.jsdt.jsdoc.msdn;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.wst.jsdt.jsdoc.ElementInfo;
import org.eclipse.wst.jsdt.jsdoc.Util;
import org.eclipse.wst.jsdt.jsdoc.XmlBasedSource;

/**
 *
 */
/**
 * @author childsb
 *
 */
public class IeFromMsdn extends XmlBasedSource {

	//String topUrl = "http://msdn2.microsoft.com/en-us/library/ms535873.aspx#";
	String topUrl = "http://msdn2.microsoft.com/en-us/library/ms533054.aspx";

	boolean useCache = false;
	boolean keepCache = false;

	public IeFromMsdn(boolean useCache, boolean keepCache) {
		this.useCache=useCache;
		this.keepCache=keepCache;
		ElementInfo.setUseCache(useCache);
		ElementInfo.setKeepCache(keepCache);
	}

	public ElementInfo[] getTopObjects() {
		String[] allUrls = parseTopObjectPage();

		MsdnElement[] parents = new MsdnElement[allUrls.length];

		for(int i = 0;i<allUrls.length;i++) {
			parents[i] = new MsdnElement(allUrls[i]);
		}

		return parents;
	}


	private String[] parseTopObjectPage() {
		ArrayList allObject = new ArrayList();

		String pageText=null;
		try {
			pageText = Util.retrieveFromUrl(getUrl(), useCache, keepCache);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		String p1  = "<h2>Objects</h2><blockquote>";
		String end = "</blockquote>";
		int i1 = pageText.indexOf(p1);

		if(i1<0) return null;

		i1 = i1 + p1.length() ;
		int indexOfEnd = pageText.indexOf(end, i1);



		String p2 = "href=\"";
		String p3 = "\"";
		String baseUrl = Util.getBaseUrl(getUrl());

		//System.out.println(pageText.substring(i1, indexOfEnd));
		int i2 =0;

		while(i2<indexOfEnd && i2>=0) {
			i2 =  pageText.indexOf(p2, i1) + p2.length();
			if(i2>=indexOfEnd) break;
			int i3 = pageText.indexOf(p3,i2);
			String href = baseUrl + "/" +  pageText.substring(i2,i3);
			allObject.add(href);
			i1=i3 + p3.length();
		}



		return (String[])allObject.toArray(new String[allObject.size()]);

	}

	public String getUrl() {
		return topUrl;
	}

}
