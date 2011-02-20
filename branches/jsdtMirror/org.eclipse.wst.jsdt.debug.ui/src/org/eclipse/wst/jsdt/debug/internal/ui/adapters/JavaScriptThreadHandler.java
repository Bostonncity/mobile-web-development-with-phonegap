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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ModelDelta;
import org.eclipse.debug.internal.ui.viewers.provisional.AbstractModelProxy;
import org.eclipse.debug.internal.ui.viewers.update.ThreadEventHandler;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptThread;
import org.eclipse.wst.jsdt.debug.internal.ui.PreferencesManager;

/**
 * Handler for thread changes
 * 
 * @since 1.0
 */
public class JavaScriptThreadHandler extends ThreadEventHandler {

	/**
	 * Constructor
	 * @param proxy
	 */
	public JavaScriptThreadHandler(AbstractModelProxy proxy) {
		super(proxy);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.ThreadEventHandler#handlesEvent(org.eclipse.debug.core.DebugEvent)
	 */
	protected boolean handlesEvent(DebugEvent event) {
		return event.getSource() instanceof IJavaScriptThread;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.ThreadEventHandler#indexOf(org.eclipse.debug.core.model.IThread)
	 */
	protected int indexOf(IThread thread) {
		if(thread.isTerminated()) {
			return -1;
		}
		return super.indexOf(thread) + getOffset(thread);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.ThreadEventHandler#addPathToThread(org.eclipse.debug.internal.ui.viewers.model.provisional.ModelDelta, org.eclipse.debug.core.model.IThread)
	 */
	protected ModelDelta addPathToThread(ModelDelta delta, IThread thread) {
		ILaunch launch = thread.getLaunch();
		Object[] children = launch.getChildren();
		ModelDelta newdelta = delta.addNode(launch, indexOf(getLaunchManager().getLaunches(), launch), IModelDelta.NO_CHANGE, children.length);
		IDebugTarget debugTarget = thread.getDebugTarget();
		int numThreads = -1;
		int offset = getOffset(thread);
		try {
			numThreads = debugTarget.getThreads().length + offset;
		} catch (DebugException e) {
		}
		return newdelta.addNode(debugTarget, indexOf(children, debugTarget), IModelDelta.NO_CHANGE, numThreads);
	}
	
	/**
	 * @param thread
	 * @return the thread offset
	 */
	int getOffset(IThread thread) {
		return (PreferencesManager.getManager().showLoadedScripts() ? 1 : 0);
	}
}
