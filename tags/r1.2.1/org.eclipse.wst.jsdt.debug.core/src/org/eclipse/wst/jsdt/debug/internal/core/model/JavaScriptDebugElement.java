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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequestManager;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;

/**
 * Default implementation of {@link DebugElement}
 * 
 * @since 1.0
 */
public class JavaScriptDebugElement extends DebugElement implements IDisconnect {

	/**
	 * Constructs a new element for the Rhino debug client.
	 * 
	 * @param target
	 */
	public JavaScriptDebugElement(IJavaScriptDebugTarget target) {
		super(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return JavaScriptDebugModel.MODEL_ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.DebugElement#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(IDebugTarget.class == adapter) {
			return getDebugTarget();
		}
		if(IJavaScriptDebugTarget.class == adapter) {
			return getJavaScriptDebugTarget();
		}
		if(IDisconnect.class == adapter) {
			return this;
		}
		return super.getAdapter(adapter);
	}
	
	/**
	 * Returns this element's target.
	 * 
	 * @return debug target
	 */
	protected JavaScriptDebugTarget getJavaScriptDebugTarget() {
		return (JavaScriptDebugTarget) getDebugTarget();
	}

	/**
	 * @return the {@link VirtualMachine} associated with this debug element
	 */
	protected VirtualMachine getVM() {
		return getJavaScriptDebugTarget().getVM();
	}

	/**
	 * @return the {@link EventRequestManager} associated with these model elements
	 */
	public EventRequestManager getEventRequestManager() {
		return getVM().eventRequestManager();
	}

	/**
	 * Adds the given listener to this target's event dispatcher's table of listeners for the specified event request. The listener will be notified each time the event occurs.
	 * 
	 * @param listener
	 *            the listener to register
	 * @param request
	 *            the event request
	 */
	public void addJSDIEventListener(IJavaScriptEventListener listener, EventRequest request) {
		EventDispatcher dispatcher = ((JavaScriptDebugTarget) getDebugTarget()).getEventDispatcher();
		if (dispatcher != null) {
			dispatcher.addEventListener(listener, request);
		}
	}

	/**
	 * Removes the given listener from this target's event dispatcher's table of listeners for the specified event request. The listener will no longer be notified when the event occurs. Listeners are responsible for deleting the event request if desired.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @param request
	 *            the event request
	 */
	public void removeJSDIEventListener(IJavaScriptEventListener listener, EventRequest request) {
		EventDispatcher dispatcher = ((JavaScriptDebugTarget) getDebugTarget()).getEventDispatcher();
		if (dispatcher != null) {
			dispatcher.removeEventListener(listener, request);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() {
		return getJavaScriptDebugTarget().canDisconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
		getJavaScriptDebugTarget().disconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return getJavaScriptDebugTarget().isDisconnected();
	}
}
