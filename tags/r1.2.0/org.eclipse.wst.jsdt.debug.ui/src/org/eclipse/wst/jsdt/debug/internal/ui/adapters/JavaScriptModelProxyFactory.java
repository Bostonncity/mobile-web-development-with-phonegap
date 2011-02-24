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
package org.eclipse.wst.jsdt.debug.internal.ui.adapters;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxy;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory2;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget;

/**
 * Model proxy factory for creating {@link IModelProxy}s for our custom debug
 * elements in the Debug view only
 * 
 * @since 1.0
 */
public class JavaScriptModelProxyFactory implements IModelProxyFactory2 {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory2#createTreeModelProxy(java.lang.Object, org.eclipse.jface.viewers.TreePath, org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext)
	 */
	public IModelProxy createTreeModelProxy(Object input, TreePath path, IPresentationContext context) {
		if(IDebugUIConstants.ID_DEBUG_VIEW.equals(context.getId())) {
			Object last = path.getLastSegment();
			if(last instanceof IJavaScriptDebugTarget) {
				return new JavaScriptDebugTargetProxy((IDebugTarget) last);
			}
		}
		return null;
	}
}
