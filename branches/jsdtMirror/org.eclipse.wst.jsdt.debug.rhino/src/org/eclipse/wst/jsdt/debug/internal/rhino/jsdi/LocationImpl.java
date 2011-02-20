/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi;

import java.util.Comparator;

import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;

/**
 * Rhino implementation of {@link Location}
 * 
 * @since 1.0
 */
public class LocationImpl extends MirrorImpl implements Location {
	
	/**
	 * Comparator that orders {@link Location}s by line number - useful for debugging
	 */
	static class LocationComparator implements Comparator {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			int value = -1;
			if(o1 instanceof Location && o2 instanceof Location) {
				int first = ((Location)o1).lineNumber();
				int second = ((Location)o2).lineNumber(); 
				if(first == second) {
					value = 0;
				}
				else if(first > second) {
					value = 1;
				}
			}
			return value;
		}
		
	}
	private static LocationComparator comparator = new LocationComparator();
	private String functionName;
	private int lineNumber;
	private ScriptReferenceImpl scriptReference;

	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param functionName
	 * @param lineNumber
	 * @param scriptReference
	 */
	public LocationImpl(VirtualMachineImpl vm, String functionName, int lineNumber, ScriptReferenceImpl scriptReference) {
		super(vm);
		this.functionName = functionName;
		this.lineNumber = lineNumber;
		this.scriptReference = scriptReference;
	}

	static LocationComparator getLocationComparator() {
		return comparator;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Location#functionName()
	 */
	public String functionName() {
		return functionName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Location#lineNumber()
	 */
	public int lineNumber() {
		return lineNumber;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.Location#scriptReference()
	 */
	public ScriptReference scriptReference() {
		return scriptReference;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("LocationImpl: "); //$NON-NLS-1$
		buffer.append("[script - ").append(scriptReference.sourceURI()).append("] "); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append("[function - ").append(functionName).append("] "); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append("[line - ").append(lineNumber).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return buffer.toString();
	}
}
