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
package org.eclipse.wst.jsdt.debug.internal.ui.source.contentassist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.jsdt.core.ITypeRoot;

/**
 * Code completion for a JavaScript {@link ITypeRoot} (script)
 * 
 * @since 1.0
 */
public class ScriptContext {
	
	private ITypeRoot fUnit;
	private int fOffset;
	
	/**
	 * Constructs a completion context on the given type.
	 * 
	 * @param root the JavaScript root in which to perform completions
	 * @param offset position in source to perform completions or -1
	 */
	public ScriptContext(ITypeRoot root, int offset) {
		fUnit = root;
		fOffset = offset;
	}

	/**
	 * Returns the {@link ITypeRoot} for this context.
	 * An {@link ITypeRoot} can be a class file (binary) or 
	 * a compilation unit (source). This method can return
	 * <code>null</code> if not 
	 * 
	 * @return the {@link ITypeRoot} backing this context or <code>null</code>
	 * @throws CoreException
	 */
	public ITypeRoot getJavaScriptRoot() throws CoreException {
		return fUnit;
	}

	/**
	 * Returns the computed insertion position for this context.<br>
	 * <br>
	 * This is typically the offset into the backing root
	 * @return the 
	 */
	public int getOffset() {
		return fOffset;
	}
}
