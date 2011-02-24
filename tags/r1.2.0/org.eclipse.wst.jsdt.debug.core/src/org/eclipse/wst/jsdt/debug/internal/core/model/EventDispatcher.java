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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.debug.core.DebugException;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.Event;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventQueue;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.EventRequest;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;

/**
 * Event dispatcher that notifies registered model elements
 * 
 * @see IJavaScriptEventListener
 * @since 1.0
 */
public final class EventDispatcher implements Runnable {

	/**
	 * Custom debug event kind for when a script is loaded
	 */
	public static final int EVENT_SCRIPT_LOADED = 0x0001;
	
	private HashMap /*<EventRequest, IJSDIEventListener>*/ listeners = null;
	private boolean shutdown = false;
	private JavaScriptDebugTarget target = null;

	/**
	 * Constructor
	 * 
	 * @param target
	 */
	public EventDispatcher(JavaScriptDebugTarget target) {
		this.target = target;
	}

	/**
	 * Registers the given listener for the specified request.
	 * 
	 * @param listener
	 * @param request
	 */
	public synchronized void addEventListener(IJavaScriptEventListener listener, EventRequest request) {
		if(this.listeners == null) {
			this.listeners = new HashMap(4);
		}
		this.listeners.put(request, listener);
	}

	/**
	 * Removes the given listener for the given request and returns the status of the removal
	 * as per the contract of {@link Collection#remove(Object)}
	 * 
	 * @param listener
	 * @param request
	 */
	public synchronized boolean removeEventListener(IJavaScriptEventListener listener, EventRequest request) {
		if(this.listeners != null) {
			return this.listeners.remove(request) != null;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (shutdown) {
			return; // no-op
		}
		VirtualMachine vm = target.getVM();
		if (vm != null) {
			EventQueue queue = vm.eventQueue();
			EventSet eventset = null;
			while (!shutdown) {
				try {
					eventset = queue.remove();
					if (eventset != null) {
						dispatch(eventset);
					}
				}
				catch(RuntimeException rte) {
					try {
						this.target.terminate();
					} catch (DebugException e) {
						JavaScriptDebugPlugin.log(e);
					}
				}
			}
		}
	}

	/**
	 * performs the re-firing of JSDI events up to any model elements listening
	 * 
	 * @param eventset
	 */
	void dispatch(EventSet eventSet) {
		Event event = null;
		boolean resume = true;
		for (Iterator iter = eventSet.iterator(); iter.hasNext();) {
			event = (Event) iter.next();
			if (event == null) {
				continue;
			}
			IJavaScriptEventListener listener = (IJavaScriptEventListener) this.listeners.get(event.request());
			if(listener != null) {
				resume &= listener.handleEvent(event, target, false, eventSet);
			}
		}

		for (Iterator iter = eventSet.iterator(); iter.hasNext();) {
			event = (Event) iter.next();
			if (event == null) {
				continue;
			}
			IJavaScriptEventListener listener = (IJavaScriptEventListener) this.listeners.get(event.request());
			if(listener != null) {
				listener.eventSetComplete(event, target, !resume, eventSet);
			}
		}
		if (resume) {
			eventSet.resume();
		}
	}

	/**
	 * Shut down and clean up the dispatcher
	 */
	public void shutdown() {
		shutdown = true;
		listeners.clear();
	}
}
