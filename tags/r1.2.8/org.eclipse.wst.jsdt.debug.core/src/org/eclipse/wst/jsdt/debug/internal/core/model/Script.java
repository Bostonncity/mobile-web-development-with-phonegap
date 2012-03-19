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
package org.eclipse.wst.jsdt.debug.internal.core.model;

import java.net.URI;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.model.IScript;

/**
 * Wrapper for a {@link ScriptReference} to display it in the debug view
 * 
 * @since 1.0
 */
public class Script extends JavaScriptDebugElement implements IScript, Comparable {

	private ScriptReference script = null;
	
	/**
	 * Constructor
	 */
	public Script(JavaScriptDebugTarget target, ScriptReference script) {
		super(target);
		this.script  = script;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IScript#source()
	 */
	public String source() {
		return this.script.source();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IScript#sourceURI()
	 */
	public URI sourceURI() {
		return this.script.sourceURI();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(o instanceof Script) {
			Script s2 = (Script) o;
			return sourceURI().toString().compareTo(s2.sourceURI().toString());
		}
		return 0;
	}
	
	/**
	 * Helper method to compute a usable name in the event the source URI has a path of "/"
	 * 
	 * @param sourceuri
	 * @return the name to use from the URI
	 */
	public static String resolveName(URI sourceuri) {
		String name = URIUtil.lastSegment(sourceuri);
		if(name == null) {
			name = new Path(sourceuri.getSchemeSpecificPart()).lastSegment();
		}
		return name;
	}
}
