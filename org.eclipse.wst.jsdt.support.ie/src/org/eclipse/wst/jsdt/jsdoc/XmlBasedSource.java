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

/**
 *
 */
/**
 * @author childsb
 *
 */
public abstract class XmlBasedSource implements IJsDocSource{


	public String toStringObjectTree() {
		ElementInfo[] tops = getTopObjects();
		if(tops==null) return "No Top Level Objects Found in " + this.getClass().getName();
		StringBuffer buff = new StringBuffer();
		buff.append("Top Level Object tree for : " + getClass().getName() + Util.NEW_LINE);
		for(int i = 0;i<tops.length;i++) {
			buff.append(tops[i]);
		}
		return buff.toString();
	}

	public String getXsl() {

		return Util.XSL_HEADER + "" + Util.XSL_FOOTER;
	}

	public String getUrl() {
		return null;
	}

	public String toString() {
		return toStringObjectTree();
	}

	public String pageToString() {
		StringBuffer buff = new StringBuffer();
		String page="error retrieving page";
		try {
			page = Util.retrieveFromUrlFixEncode(getUrl(), false, false);
		} catch (Exception ex) {
			return "Error retrieving page " + ex.toString();
		}
		buff.append("------------------------- Page ----------------------" + Util.NEW_LINE );
		buff.append(page+ Util.NEW_LINE);
//		buff.append("------------------------- Translated Page ----------------------\n" );
//		File tempTran = Util.dataToTempFile(getXsl());
//		try {
//			buff.append(Util.applyTranslation(page, tempTran) + "\n");
//		} catch (Exception ex) {
//			// TODO Auto-generated catch block
//			buff.append(ex.toString() + "\n");
//		}
		buff.append("================================================================================" + Util.NEW_LINE );
		return buff.toString();
	}
}


