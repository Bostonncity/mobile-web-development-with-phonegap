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

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
/**
 * @author childsb
 *
 */
public class ElementInfo {

	public static final int CLASS = 1;
	public static final int METHOD = 2;
	public static final int PROPERTY = 3;

	public static final int EVENT = 4;
	public static final int COLLECTION = 5;

	protected String name;
	protected String baseUrl;
	protected  ElementInfo parent;
	protected  ElementInfo[] children;
	protected int type=-5;
	protected static ArrayList nodes;
	protected boolean visited=false;
	protected static int instances = 0;
	protected static boolean DEBUG=false;
	protected static boolean useCache;
	protected static boolean keepCache;

	public boolean shouldUseCache() {
		return useCache;
	}

	public boolean shouldKeepCache() {
		return keepCache;
	}
	public static void setUseCache(boolean shouldUseCache) {
		useCache=shouldUseCache;
	}

	public static void setKeepCache(boolean shouldKeepCache) {
		keepCache = shouldKeepCache;
	}

	public boolean visit() {
		boolean ov = visited;
		visited = true;
		return ov;

	}

	public boolean equals(Object o) {

			try {
				ElementInfo other = (ElementInfo)o;
				boolean equal = other.getUrl().trim().equalsIgnoreCase(this.getUrl().trim());
//				boolean equal =  other.getName().equals(this.getName()) &&
//								 other.getType()==this.getType() &&
//								 (
//								 other.getParent().getName().equals(this.getParent().getName()) &&
//							     other.getParent().getType()== (this.getParent().getType()) ||
//							     other.getParent()==null && this.getParent()==null
//							     );
				return equal;
			} catch (Exception ex) {}

		return false;
	}

	public static void freeObject(ElementInfo element) {
		for(int i = 0;i<nodes.size();i++) {
			ElementInfo temp = (ElementInfo)nodes.get(i);
			if(temp.getUrl().equalsIgnoreCase(element.getUrl())) {
				nodes.remove(i);
				break;
			}
		}
	}

	public ElementInfo[] getFoundObjects() {
		ArrayList found = new ArrayList();
		ElementInfo[] children = getChildren();

		//found.add(this);
		for(int i = 0;i<children.length;i++) {

			if(children[i].getType() == COLLECTION || children[i].getType()==EVENT) {
				children[i].clearVisit();
				found.add(children[i]);
			}
		}

		return (ElementInfo[])found.toArray(new ElementInfo[found.size()]);
	}
	public void clearVisit() {
		visited=false;
	}

	{
		nodes = new ArrayList();
	}

	public ElementInfo(String baseUrl,ElementInfo parent) {
		this.baseUrl = baseUrl;
		this.parent = parent;
		//addNode(this);
		if(DEBUG) {
			System.out.println("Creating new instance for total of : " + ++instances);
		}

	}

	public void finalize() {
		if(DEBUG) {
			System.out.println("Destroying instance for total of : " + --instances);
		}
	}

	public void addNode(ElementInfo element) {

			nodes.add(element);
	}

	public static ElementInfo findChild(String baseUrl) {
		for(int i = 0;i<nodes.size();i++) {
			ElementInfo temp = (ElementInfo)nodes.get(i);
			if(temp.getUrl().equalsIgnoreCase(baseUrl)) return temp;
		}
		return null;
	}

	public boolean isDefined(String baseUrl) {
		return findChild(baseUrl)!=null;
	}

	public ElementInfo getParent() {
		return this.parent;
	}

	public ElementInfo[] getChildren() {
		return this.children;
	}

	public boolean hasChildren() { return this.children!=null && this.children.length>0;}

	public String getName() { return name;}

	public String getUrl() { return baseUrl;}



	public String getJsDoc(String parentName) { return null; }

	public String getJsStructure() { return null;}

	public String getTypeName() {
		switch(getType()) {
			case ElementInfo.PROPERTY:
				return "Property";
			case ElementInfo.METHOD:
				return "Method";
			case ElementInfo.CLASS:
				return "Class";


		}
		return "Unknown Type";
	}

	public int getType() {
		return -1;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("name : " + getName() + Util.NEW_LINE);
		buff.append("Type : " + getTypeName() + "Util.NEW_LINE");
		if(getParent()!=null) {
			buff.append("\tParent Name : " + getParent().getName() + Util.NEW_LINE);
			buff.append("\tParent Type : " + getParent().getTypeName() + Util.NEW_LINE);
		}else {
			buff.append("No Parent" + Util.NEW_LINE);
		}
		buff.append("baseUrl : " + baseUrl + Util.NEW_LINE);
		//buff.append("translation : " + translation + "\n");
		buff.append("----------------Children--------------" + Util.NEW_LINE + "Name\t\t\t\tType"+ Util.NEW_LINE);
		if(hasChildren()) {

			ElementInfo[] children = getChildren();
			for(int i = 0;i<children.length;i++) {
				buff.append(children[i].getName() + "\t\t\t" + children[i].getTypeName() + Util.NEW_LINE);
			}
		}else {
			buff.append(Util.NEW_LINE + "No Children" + Util.NEW_LINE);
		}
		buff.append("--------------------------------------");
		return buff.toString();
	}

	protected String getPageText() {
		try {
			return Util.retrieveFromUrl(getUrl(), shouldUseCache(), !shouldKeepCache());
		} catch (IOException ex) {

		}
		return null;
//		if(pageText!=null) return pageText;
//		try {
//			pageText =  Util.retrieveFromUrl(getUrl());
//		} catch (IOException ex) {
//			// TODO Auto-generated catch block
//			//ex.printStackTrace();
//		}
//		return pageText;
	}

	public String getBaseUrl() {
		return Util.getBaseUrl(getUrl());
	}

	public String getDeclarationString() {
		return null;
	}
	public String getJsStructure(String parent) {
		return "NOT DEFINED " + parent;
	}

	public boolean isStatic() { return false; }
}
