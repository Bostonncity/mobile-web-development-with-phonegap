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
/**
 *
 */
package org.eclipse.wst.jsdt.jsdoc.msdn;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.wst.jsdt.jsdoc.ElementInfo;
import org.eclipse.wst.jsdt.jsdoc.Util;

/**
 * @author childsb
 *
 * parsers msdn web pages into a JSDoc and JavaScript prototype structure for library purposes.
 *
 */
public class MsdnElement extends ElementInfo{


	String typeName;
	String[][] parsedParams = null;


	public String getJsTypeName() {
		if(typeName!=null) return typeName;

		switch(getType()) {
			case ElementInfo.PROPERTY:
				String pageText = getPageText();
				String p1  = "<p class=\"clsRef\">Possible Values</p>";
				String end = "</blockquote>";
				int i1 = pageText.indexOf(p1);

				if(i1<0) return null;

				i1 = i1 + p1.length() ;
				int indexOfEnd = pageText.indexOf(end, i1);
				String p2 = "<b>";
				String p3 = "</b>";


				//System.out.println(pageText.substring(i1, indexOfEnd));
				int i2 = pageText.indexOf(p2, i1) + p2.length();

				int i3 = pageText.indexOf(p3,i2);
				if(i2>indexOfEnd || i3>indexOfEnd) return null;

				typeName = pageText.substring(i2,i3);
			break;
			case ElementInfo.COLLECTION:
				typeName="Array";
				break;
			case METHOD:
				pageText = getPageText();
				p1  = "<p class=\"clsRef\">Return Value</p>";
				end = "</blockquote>";
				i1 = pageText.indexOf(p1);

				if(i1<0) return null;

				i1 = i1 + p1.length() ;
				indexOfEnd = pageText.indexOf(end, i1);
				p2 = "<b>";
				p3 = "</b>";


				//System.out.println(pageText.substring(i1, indexOfEnd));
				i2 = pageText.indexOf(p2, i1);
				if(i2<0) return null;
				i2 = i2+ p2.length();

				i3 = pageText.indexOf(p3,i2);
				if(i2>indexOfEnd || i3>indexOfEnd) return null;

				typeName = pageText.substring(i2,i3);
				break;

		}
		return typeName;
	}



	public String getJsDoc(String parentName) {
		StringBuffer jsDoc = new StringBuffer();
		jsDoc.append("/**" + Util.NEW_LINE);
		String dec = "  * ";
		String endDec = Util.NEW_LINE;

		switch(getType()) {
			case COLLECTION:
			case EVENT:
			case ElementInfo.CLASS:
				if(parentName!=null) {
					jsDoc.append(dec + "Property " + getName() + endDec);
					jsDoc.append(dec + "@type " + getName() + endDec);
					jsDoc.append(dec + "@return " + getName() + endDec);
					jsDoc.append(dec + "@class " + parentName + endDec);
					jsDoc.append(dec + "@since " + getSince() + endDec);
					break;
				}
				jsDoc.append(dec + "Object " + getName() + "()" + endDec);
				jsDoc.append(dec + "@type " + getName() + endDec);
				jsDoc.append(dec + "@super "+ getSuperType() + endDec);
				jsDoc.append(dec + "@class " + getName()  + endDec);
				jsDoc.append(dec + "@since " + getSince() +  endDec);
			break;
			case ElementInfo.METHOD:
				String superType = getSuperType();
				jsDoc.append(dec + "function " + getName() + "(" + getParamString() + ")" + endDec);
				String params[][] = getParamaters();
				for(int i = 0;i<params.length;i++) {
					jsDoc.append(dec + "@param " + params[i][0] + " " + params[i][1] + endDec);
				}
				if(superType!=null) jsDoc.append(dec + "@type " + superType + endDec);
				if(superType!=null) jsDoc.append(dec + "@return " + superType + endDec);

				jsDoc.append(dec + "@class " + parentName + endDec);
				jsDoc.append(dec + "@since " + getSince() +  endDec);
			break;
			case ElementInfo.PROPERTY:
				jsDoc.append(dec + "Property " + getName() + endDec);
				jsDoc.append(dec + "@type " + getSuperType() + endDec);
				jsDoc.append(dec + "@return " + getSuperType() + endDec);
				jsDoc.append(dec + "@class " + parentName + endDec);
				jsDoc.append(dec + "@since " + getSince() + endDec);
			break;
		}
		jsDoc.append(dec + "@link " + getUrl() + endDec);
		jsDoc.append("*/" + Util.NEW_LINE);
		return jsDoc.toString();
	}

	public String[][] getParamaters() {

		if(parsedParams!=null) return parsedParams;

		String[][] noParams = new String[0][];
		//

		String pageText = getPageText();
		String p1  = "<p class=\"clsRef\">Parameters</p>";
		String end = "</blockquote>";
		int i1 = pageText.indexOf(p1);

		if(i1<0) {
			parsedParams = noParams;
			return parsedParams;
		}

		i1 = i1 + p1.length() ;
		int indexOfEnd = pageText.indexOf(end, i1);

		int paramStart = i1;

		ArrayList params = new ArrayList();
		ArrayList types = new ArrayList();

		String paramI = "<i>";
		String paramIend = "</i>";
		String p2 = "<b>";
		String p3 = "</b>";
		String paramTypeEendFallback1 = "<td>";
		String paramTypeEendFallback1End = "</td>";
		while(paramStart<indexOfEnd) {
				paramStart = pageText.indexOf(paramI, paramStart);
				if(paramStart<0 || paramStart>indexOfEnd) break;
				paramStart =paramStart + paramI.length();
				int paramEnd = pageText.indexOf(paramIend, paramStart);
				String paramName = pageText.substring(paramStart,paramEnd);

				int typeStart = pageText.indexOf(p2,paramEnd);
				int typeEnd = -1;

				if(typeStart>0) {
					typeStart = typeStart + p2.length();
					typeEnd = pageText.indexOf(p3, typeStart);

				}else {
					typeStart =pageText.indexOf(paramTypeEendFallback1,paramEnd + paramI.length()) + paramTypeEendFallback1.length() ;
					typeEnd = pageText.indexOf(paramTypeEendFallback1End, typeStart);
				}
				String typeName=null;
				try {
					typeName = pageText.substring(typeStart,typeEnd);
				} catch (RuntimeException ex) {
					// TODO Auto-generated catch block
					//ex.printStackTrace();
					break;
				}

				paramStart = pageText.indexOf(paramTypeEendFallback1End, typeStart);
				//paramStart = typeEnd;

				if(contains(typeName, "integer")) typeName = "Number";

				params.add(paramName);
				types.add(typeName);
		}

		//int paramTypeStart = i1;


//		while(paramTypeStart<indexOfEnd) {
//			paramTypeStart = pageText.indexOf(p2, paramTypeStart);
//			if(paramTypeStart<0) break;
//			paramTypeStart =paramTypeStart + p2.length();
//			int paramEnd = pageText.indexOf(p3, paramTypeStart);
//			String paramName = pageText.substring(paramTypeStart,paramEnd);
//			types.add(paramName.trim());
//		}

		if(params.size()==0) return noParams;

		parsedParams = new String[params.size()][2];

		for(int i = 0;i<params.size();i++) {
			parsedParams[i][0]=(String)params.get(i);
			parsedParams[i][1]=(String)types.get(i);
		}

		return parsedParams;
	}

	public String getSuperType() {
		if(getType()==COLLECTION) return "Array";
		if(getType()==EVENT) return "Object";
		if(getType()==PROPERTY) {
			String jsTypeName = getJsTypeName();
			if(jsTypeName==null) return "Object";
			jsTypeName = jsTypeName.trim();
			if(contains(jsTypeName, "variant")) return "Object";
			if(contains(jsTypeName, "integer")) return "Number";
			return jsTypeName ;
		}
		if(getType()==CLASS) {

			return "Object";

		}
		if(getType()==METHOD) {
			String jsTypeName = getJsTypeName();
			if(jsTypeName==null) return null;
			jsTypeName = jsTypeName.trim();
			if(contains(jsTypeName, "variant")) return "Object";
			if(contains(jsTypeName, "integer")) return "Number";
			return jsTypeName ;

		}

		return "Object";
	}

	public String getSince() {
		return "JScript 5.6";
	}

	public String getJsStructure() {
		StringBuffer structure = new StringBuffer();
		structure.append(getJsDoc(null) + Util.NEW_LINE);
		ElementInfo[] children = getChildren();
		String myName = getName();
		structure.append("function " + myName + "(){};" + Util.NEW_LINE);
		structure.append(myName + ".prototype= new " + getSuperType() + "();" + Util.NEW_LINE);

		for(int i = 0;i<children.length;i++) {

			//if( !children[i].visit())
				structure.append(children[i].getJsStructure(myName));
		}
		return structure.toString();
	}

	public String getParamString() {
		String[][] params = getParamaters();
		String paramString="";

		for(int i = 0;i<params.length;i++) {
			paramString = paramString + params[i][0] + (((i+1)<params.length) ?",":"");
		}
		return paramString;
	}

	public String getJsStructure(String parent) {
		StringBuffer structure = new StringBuffer();

		switch(getType()) {
			case ElementInfo.PROPERTY:
				structure.append(getJsDoc(parent)+ Util.NEW_LINE);

				if(!isStatic()) {
					structure.append(parent + ".prototype." + getName() + "=" + getInitializer(getSuperType()) + ";" + Util.NEW_LINE );
				}else {
					structure.append(parent + "." +  getName() + "=" + getInitializer(getSuperType()) + ";" + Util.NEW_LINE);
				}
				break;
			case ElementInfo.METHOD:
				structure.append(getJsDoc(parent)+ Util.NEW_LINE);
				if(!isStatic()) {
					structure.append(parent + ".prototype." + getName() + "=function("+getParamString()+"){};" + Util.NEW_LINE);
				}else {
					structure.append(parent +  getName()+ "=function("+getParamString()+"){};" + Util.NEW_LINE );
				}

			break;
			case CLASS:
			case EVENT:
			case COLLECTION:
				structure.append(getJsDoc(parent)+ Util.NEW_LINE);

				if(!isStatic()) {
					structure.append(parent + ".prototype." + getName() + "= new " + getName() + "();" + Util.NEW_LINE );
				}else {
					structure.append(parent + "." +  getName() + "= new " + getName() + "();" + Util.NEW_LINE);
				}
				//System.out.println("adding complex structure : \n" + structure.toString());
			break;

		}
		return structure.toString();
	}

	public boolean isStatic() {
		return false;
	}

	public MsdnElement(String baseUrl,ElementInfo parent) {

		super(baseUrl,parent);
	}

	public MsdnElement(String baseUrl) {

		super(baseUrl,null);
	}

	private ElementInfo getMsdnElement(String baseUrl) {
		ElementInfo temp = findChild(baseUrl);
		if(temp!=null) return temp;

		MsdnElement element = new MsdnElement(baseUrl, this);
		nodes.add(element);
		return element;
	}


	public String getName() {
		if(super.name!=null) return super.name;
		String pageText = getPageText();
		if(pageText==null) return null;

		String p1  = "<div class=\"stat\"><strong>&nbsp;";

		int i1 = pageText.indexOf(p1) + p1.length() ;

		String p2 = " ";

		int i2 = pageText.indexOf(p2,i1);
		super.name = pageText.substring(i1,i2);
		return super.name;
	}



	public boolean contains(String s1, String searchFor) {
		String temp = s1.toLowerCase();
		String temp2 = searchFor.toLowerCase();

		return temp.indexOf(temp2)>-1;
	}

	public int getType() {

		String typeName=null;

		if(super.type>-1) return super.type;

		String pageText;
		int i1;
		int i2;
		try {
			pageText = getPageText();

			String p1  = "<div class=\"stat\"><strong>&nbsp;" + getName();

			i1 = pageText.indexOf(p1) + p1.length();

			String p2 = "</strong>";

			i2 = pageText.indexOf(p2,i1);
		} catch (RuntimeException ex) {
			return -1;
		}



		try {
			typeName = pageText.substring(i1,i2).trim();
		} catch (Exception ex) {
			return -1;
			// TODO Auto-generated catch block
			//ex.printStackTrace();
		}

		if(contains(typeName,("object")) || contains(typeName,"Collection")){
			super.type = ElementInfo.CLASS;
		}

		if(contains(typeName,"Property")){
			super.type = ElementInfo.PROPERTY;
		}

		if(contains(typeName,"Method")){
			super.type = ElementInfo.METHOD;
		}
		if(contains(typeName,"Event")){
			super.type = ElementInfo.EVENT;
		}
		if(contains(typeName,"Collection")){
			super.type = ElementInfo.COLLECTION;
		}
		return super.type;
	}

	public String getTypeName() {
		switch(getType()) {
			case ElementInfo.EVENT:
				return "Event";
			case ElementInfo.COLLECTION:
				return "Collection";
			default:
				return super.getTypeName();
		}
	}

	public ElementInfo[] getChildren() {
		if(super.children!=null && super.children.length!=0) return super.children;
		ArrayList children = new ArrayList();

		ElementInfo[] elemnts;

		elemnts = getProperties();
		children.addAll(Arrays.asList(elemnts));
		if(DEBUG) {
			System.out.println("Found the following in getProperties()");
			for(int i = 0;i<elemnts.length;i++) {
				String name = elemnts[i].getName();
				System.out.println("\t" + name);
			}
		}
		elemnts =getMethods();
		children.addAll(Arrays.asList(elemnts));

		if(DEBUG) {
			System.out.println("Found the following in getMethods()");
			for(int i = 0;i<elemnts.length;i++) {
				String name = elemnts[i].getName();
				System.out.println("\t" + name);
			}
		}
		elemnts =getObjects();
		children.addAll(Arrays.asList(elemnts));
		if(DEBUG) {
			System.out.println("Found the following in getObjects()");
			for(int i = 0;i<elemnts.length;i++) {
				String name = elemnts[i].getName();
				System.out.println("\t" + name);
			}
		}
		elemnts =getEvents();
		children.addAll(Arrays.asList(elemnts));

		if(DEBUG) {
			System.out.println("Found the following in getEvents()");
			for(int i = 0;i<elemnts.length;i++) {
				String name = elemnts[i].getName();
				System.out.println("\t" + name);
			}
		}
		elemnts =getCollections();
		children.addAll(Arrays.asList(elemnts));
		if(DEBUG) {
			System.out.println("Found the following in getCollections()");
			for(int i = 0;i<elemnts.length;i++) {
				String name = elemnts[i].getName();
				System.out.println("\t" + name);
			}
		}
		super.children =  (ElementInfo[])children.toArray(new ElementInfo[children.size()]);


		return super.children;
	}



	public boolean hasChildren() {
		ElementInfo[] children = getChildren();
		return children!=null && children.length>0;
	}

	public ElementInfo[] getProperties() {
		ArrayList foundProps = new ArrayList();
		String baseUrl = getBaseUrl();
		String pageText = getPageText();
		String p1  = "<tr><th>Property</th><th>Description</th></tr>";
		String end = "</table>";
		int i1 = pageText.indexOf(p1);

		if(i1<0) {
			p1 = "<th>Attribute</th><th>Property</th><th>Description</th>";
			i1 = pageText.indexOf(p1);
		}
		if(i1<0) {
			//System.out.println("No properties");
			return new ElementInfo[0];
		}
		i1 = i1 + p1.length() ;



		int indexOfEnd = pageText.indexOf(end, i1);
		String p2 = "href=\"";
		int i2 = i1;
		String p3 = "\"";
		while(i2<indexOfEnd) {
			i2 = pageText.indexOf(p2,i2) + p2.length();

			int i3 = pageText.indexOf(p3,i2);
			String urlName = pageText.substring(i2,i3);



			ElementInfo info = getMsdnElement(baseUrl + "/" + urlName);
			if(!foundProps.contains(info)) foundProps.add(info);
		}
		return (ElementInfo[])foundProps.toArray(new ElementInfo[foundProps.size()]);
	}

	public ElementInfo[] getMethods() {

		ArrayList foundProps = new ArrayList();
		String baseUrl = getBaseUrl();
		String pageText = getPageText();
		String p1  = "<tr><th>Method</th><th>Description</th></tr>";
		String end = "</table>";
		int i1 = pageText.indexOf(p1);

		if(i1<0) {
			//System.out.println("no methods");
			return new ElementInfo[0];
		}

		i1 = i1 + p1.length() ;
		int indexOfEnd = pageText.indexOf(end, i1);
		String p2 = "href=\"";
		int i2 = i1;
		String p3 = "\"";
		while(i2<indexOfEnd) {
			i2 = pageText.indexOf(p2,i2) + p2.length();
			if(i2>indexOfEnd) break;
			int i3 = pageText.indexOf(p3,i2);
			String urlName = pageText.substring(i2,i3);


			ElementInfo info = getMsdnElement(baseUrl + "/" + urlName);
			if(!foundProps.contains(info)) foundProps.add(info);
		}
		return (ElementInfo[])foundProps.toArray(new ElementInfo[foundProps.size()]);
	}

	public ElementInfo[] getObjects() {
		ArrayList foundProps = new ArrayList();
		String baseUrl = getBaseUrl();
		String pageText = getPageText();
		String p1  = "<tr><th>Object</th><th>Description</th></tr>";
		String end = "</table>";
		int i1 = pageText.indexOf(p1);
		if(i1<0) {
			p1 = "<tr><th>Element</th><th>Object</th><th>Description</th>";
			i1 = pageText.indexOf(p1);
		}

		if(i1<0) {
			//System.out.println("no Objects");
			return new ElementInfo[0];
		}

		i1 = i1 + p1.length() ;

		int indexOfEnd = pageText.indexOf(end, i1);
		String p2 = "href=\"";
		int i2 = i1;
		String p3 = "\"";
		while(i2<indexOfEnd) {
			i2 = pageText.indexOf(p2,i2) + p2.length();
			if(i2>indexOfEnd) break;
			int i3 = pageText.indexOf(p3,i2);
			String urlName = pageText.substring(i2,i3);


			ElementInfo info = getMsdnElement(baseUrl + "/" + urlName);
			//System.out.println("Adding object: " + baseUrl + "/" + urlName);
			if(!foundProps.contains(info)) {
				//String objectName = info.getName();
				//System.out.println("Adding " + objectName);
				foundProps.add(info);
			}
		}
		return (ElementInfo[])foundProps.toArray(new ElementInfo[foundProps.size()]);
	}

	public ElementInfo[] getEvents() {

		ArrayList foundProps = new ArrayList();
		String baseUrl = getBaseUrl();
		String pageText = getPageText();
		String p1  = "<tr><th>Event</th><th>Description</th></tr>";
		String end = "</table>";
		int i1 = pageText.indexOf(p1);

		if(i1<0) {
			//System.out.println("No events");
			return new ElementInfo[0];
		}

		i1 = i1 + p1.length() ;
		int indexOfEnd = pageText.indexOf(end, i1);
		String p2 = "href=\"";
		int i2 = i1;
		String p3 = "\"";
		while(i2<indexOfEnd) {
			i2 = pageText.indexOf(p2,i2) + p2.length();
			if(i2>indexOfEnd) break;
			int i3 = pageText.indexOf(p3,i2);
			String urlName = pageText.substring(i2,i3);


			ElementInfo info = getMsdnElement(baseUrl + "/" + urlName);

			if(!foundProps.contains(info)) foundProps.add(info);
		}
		return (ElementInfo[])foundProps.toArray(new ElementInfo[foundProps.size()]);
	}

	public ElementInfo[] getCollections() {

		ArrayList foundProps = new ArrayList();
		String baseUrl = getBaseUrl();
		String pageText = getPageText();
		String p1  = "<tr><th>Collection</th><th>Description</th></tr>";
		String end = "</table>";
		int i1 = pageText.indexOf(p1);
		if(i1<0) {
			//System.out.println("No Collections");
			return new ElementInfo[0];
		}
		i1 = i1 + p1.length() ;
		int indexOfEnd = pageText.indexOf(end, i1);
		String p2 = "href=\"";
		int i2 = i1;
		String p3 = "\"";
		while(i2<indexOfEnd) {
			i2 = pageText.indexOf(p2,i2) + p2.length();
			if(i2>indexOfEnd) break;
			int i3 = pageText.indexOf(p3,i2);
			String urlName = pageText.substring(i2,i3);


			ElementInfo info = getMsdnElement(baseUrl + "/" + urlName);

			if(!foundProps.contains(info)) foundProps.add(info);
		}
		return (ElementInfo[])foundProps.toArray(new ElementInfo[foundProps.size()]);
	}

	private static String getInitializer(String className) {
		if(className.trim().equalsIgnoreCase("Integer")) return "0";
		if(className.trim().equalsIgnoreCase("Boolean")) return "false";
		if(className.trim().equalsIgnoreCase("String")) return "\"\"";
		if(className.trim().equalsIgnoreCase("Number")) return "0";

		return "new " + className + "()";


	}

	public String toString() {
		if(name!=null) return name + " URL " + baseUrl;
		return baseUrl;
	}
}
