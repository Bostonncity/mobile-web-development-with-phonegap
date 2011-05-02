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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.DebuggerStatementEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.Event;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ScriptLoadEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ThreadEnterEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.ThreadExitEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.VMDeathEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.event.VMDisconnectEvent;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.DebuggerStatementRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ScriptLoadRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ThreadEnterRequest;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.ThreadExitRequest;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.core.model.IScript;
import org.eclipse.wst.jsdt.debug.core.model.IScriptGroup;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptPreferencesManager;
import org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptBreakpoint;

/**
 * JavaScript debug target
 * 
 * @since 1.0
 */
public class JavaScriptDebugTarget extends JavaScriptDebugElement implements IJavaScriptDebugTarget, IDebugEventSetListener, ILaunchListener, IJavaScriptEventListener {

	static final String DEFAULT_NAME = ModelMessages.JSDIDebugTarget_jsdi_debug_target;
	
	/**
	 * The cached collection of scripts, sorted by name
	 */
	private ArrayList iScriptCache = null;
	
	private final IProcess process;
	private final VirtualMachine vm;
	private final ILaunch launch;
	private final boolean supportsTerminate;
	private final boolean supportsDisconnect;
	private final String name;
	private final EventDispatcher eventDispatcher;

	private ArrayList threads = new ArrayList();
	private ArrayList breakpoints = new ArrayList();
	
	private IScriptGroup scriptgroup = null;

	private boolean disconnected = false;
	private boolean terminating = false;
	private boolean terminated = false;
	private boolean suspended = false;

	private ThreadEnterRequest threadEnterRequest;
	private ThreadExitRequest threadExitRequest;
	private ScriptLoadRequest scriptLoadrequest;

	private DebuggerStatementRequest debuggerStatementRequest;
	
	/**
	 * Constructor
	 * 
	 * @param vm
	 * @param process
	 * @param launch
	 * @param name
	 * @param supportsTerminate
	 * @param supportsDisconnect
	 */
	public JavaScriptDebugTarget(VirtualMachine vm, IProcess process, ILaunch launch,
			String name, boolean supportsTerminate, boolean supportsDisconnect) {
		super(null);
		this.vm = vm;
		this.process = process;
		this.launch = launch;
		this.supportsTerminate = supportsTerminate;
		this.supportsDisconnect = supportsDisconnect;
		if (name != null) {
			this.name = name;
		} else if (vm.name() != null) {
			this.name = vm.name();
		} else {
			this.name = DEFAULT_NAME;
		}
		this.eventDispatcher = new EventDispatcher(this);
		this.scriptgroup = new ScriptGroup(this);
		// TODO: consider calling this outside of constructor
		initialize();
	}

	/**
	 * Initialize any threads and breakpoints existing at the time this target
	 * has been created
	 */
	public synchronized void initialize() {
		// perform initializations
		initializeThreads();
		initializeBreakpoints();

		//register listening for script load request
		scriptLoadrequest = getEventRequestManager().createScriptLoadRequest();
		scriptLoadrequest.setEnabled(true);
		addJSDIEventListener(this, scriptLoadrequest);
		
		getLaunch().addDebugTarget(this);

		DebugPlugin plugin = DebugPlugin.getDefault();
		plugin.addDebugEventListener(this);
		plugin.getLaunchManager().addLaunchListener(this);
		fireCreationEvent();
		// begin handling/dispatching events after the creation event is handled
		// by all listeners
		plugin.asyncExec(new Runnable() {
			public void run() {
				Thread t = new Thread(eventDispatcher, "JavaScriptDebugModel.EventDispatcher"); //$NON-NLS-1$
				t.setDaemon(true);
				t.start();
			}
		});
	}

	/**
	 * Shuts down the target
	 */
	public synchronized void shutdown() {
		try {
			if (supportsTerminate) {
				terminate();
			} else if (supportsDisconnect) {
				disconnect();
			}
		} catch (DebugException e) {
			JavaScriptDebugPlugin.log(e);
		} finally {
			cleanup();
			fireTerminateEvent();
		}
	}

	/**
	 * Cleans up the state of the target
	 */
	void cleanup() {
		DebugPlugin plugin = DebugPlugin.getDefault();
		plugin.getLaunchManager().removeLaunchListener(this);
		plugin.removeDebugEventListener(this);
		getEventDispatcher().shutdown();
		removeAllThreads();
		removeAllBreakpoints();
		removeJSDIEventListener(this, scriptLoadrequest);
		this.scriptgroup = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.DebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.DebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return launch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess() {
		return process;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return supportsTerminate && isAvailable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return terminated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() {
		return supportsDisconnect && isAvailable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return disconnected;
	}

	/**
	 * Returns the collection of underlying {@link ScriptReference}s
	 * whose name matches the given name
	 * @param name
	 * @return the complete list of {@link ScriptReference}s matching the given name
	 * or an empty collection, never <code>null</code>
	 */
	public synchronized List underlyingScripts(String name) {
		List scripts = getVM().allScripts();
		if(!scripts.isEmpty()) {
			List byname = new ArrayList();
			ScriptReference script = null;
			for (Iterator iter = scripts.iterator(); iter.hasNext();) {
				script = (ScriptReference) iter.next();
				if (Script.resolveName(script.sourceURI()).equals(name)) {
					byname.add(script);
				}
			}
			return byname;
		}
		return Collections.EMPTY_LIST;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget#allScriptsByName(java.lang.String)
	 */
	public synchronized List allScriptsByName(String name) {
		List scripts = allScripts();
		if(scripts.size() > 0) {
			List byname = new ArrayList();
			IScript script = null;
			for (Iterator iter = scripts.iterator(); iter.hasNext();) {
				script = (IScript) iter.next();
				if (Script.resolveName(script.sourceURI()).equals(name)) {
					byname.add(script);
				}
			}
			return byname;
		}
		return Collections.EMPTY_LIST;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget#allScripts()
	 */
	public synchronized List allScripts() {
		if(iScriptCache == null) {
			ArrayList all = (ArrayList) getVM().allScripts();
			if(all.size() > 0) { 
				iScriptCache = new ArrayList(all.size());
				for (int i = 0; i < all.size(); i++) {
					iScriptCache.add(new Script(this, (ScriptReference) all.get(i)));
				}
				Collections.sort(iScriptCache);
			}
		}
		if(iScriptCache == null) {
			return Collections.EMPTY_LIST;
		}
		return iScriptCache;
	}
	
	/**
	 * Collects all of the current threads from the {@link VirtualMachine} and
	 * adds them to the cached list
	 */
	private synchronized void initializeThreads() {
		threadEnterRequest = vm.eventRequestManager().createThreadEnterRequest();
		threadEnterRequest.setEnabled(true);
		eventDispatcher.addEventListener(this, threadEnterRequest);

		threadExitRequest = vm.eventRequestManager().createThreadExitRequest();
		threadExitRequest.setEnabled(true);
		eventDispatcher.addEventListener(this, threadExitRequest);

		List allThreads = vm.allThreads();
		ThreadReference threadReference = null;
		for (Iterator iterator = allThreads.iterator(); iterator.hasNext();) {
			threadReference = (ThreadReference) iterator.next();
			createThread(threadReference, false);
		}
	}

	/**
	 * Removes all threads from the target
	 */
	private synchronized void removeAllThreads() {
		Iterator iter = getThreadIterator();
		while (iter.hasNext()) {
			JavaScriptThread thread = (JavaScriptThread) iter.next();
			thread.terminated();
		}
		threads.clear();
		removeJSDIEventListener(this, threadEnterRequest);
		removeJSDIEventListener(this, threadExitRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public synchronized IThread[] getThreads() throws DebugException {
		return (IThread[]) threads.toArray(new IThread[this.threads.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public synchronized boolean hasThreads() throws DebugException {
		return !threads.isEmpty();
	}

	/**
	 * Installs all JavaScript breakpoints that currently exist in the
	 * breakpoint manager
	 */
	synchronized void initializeBreakpoints() {
		debuggerStatementRequest = getEventRequestManager().createDebuggerStatementRequest();
		debuggerStatementRequest.setEnabled(true);
		addJSDIEventListener(this, debuggerStatementRequest);

		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		manager.addBreakpointListener(this);
		IBreakpoint[] managerBreakpoints = manager.getBreakpoints(JavaScriptDebugModel.MODEL_ID);
		for (int i = 0; i < managerBreakpoints.length; i++) {
			breakpointAdded(managerBreakpoints[i]);
		}
		//add the managed breakpoints
		IJavaScriptBreakpoint[] breakpoints = JavaScriptPreferencesManager.getAllManagedBreakpoints();
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}

	/**
	 * Removes all breakpoints from this target
	 */
	private synchronized void removeAllBreakpoints() {
		Iterator iter = ((ArrayList)this.breakpoints.clone()).iterator();
		JavaScriptBreakpoint breakpoint = null;
		while (iter.hasNext()) {
			breakpoint = (JavaScriptBreakpoint) iter.next();
			breakpoint.removeFromTarget(this);
		}
		this.breakpoints.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return JavaScriptDebugModel.MODEL_ID.equals(breakpoint.getModelIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		if ((isSuspended() || canResumeThreads()) && isAvailable()) {
			if (threads.size() == 0) {
				return true;
			}
			Iterator iter = getThreadIterator();
			while (iter.hasNext()) {
				IThread thread = (IThread) iter.next();
				if (thread.canResume()) {
					// if at least 1 thread can resume the target can be resumed
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return true if any one of the threads in the target can be resumed,
	 *         false otherwise
	 */
	private boolean canResumeThreads() {
		Iterator iter = getThreadIterator();
		IThread thread = null;
		while (iter.hasNext()) {
			thread = (IThread) iter.next();
			if (thread.canResume()) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		if (!isSuspended() && isAvailable()) {
			Iterator iter = getThreadIterator();
			while (iter.hasNext()) {
				IThread thread = (IThread) iter.next();
				if (thread.isSuspended()) {
					// do not allow the target to suspend if there is already a
					// suspended thread
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns an iterator over the collection of threads. The returned iterator
	 * is made on a copy of the thread list so that it is thread safe. This
	 * method should always be used instead of getThreadList().iterator()
	 * 
	 * @return an iterator over the collection of threads
	 */
	private Iterator getThreadIterator() {
		List threadList;
		synchronized (this.threads) {
			threadList = (List) this.threads.clone();
		}
		return threadList.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return this.suspended;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		if (!isAvailable()) {
			// no-op if the target is not ready
			return;
		}
		// if we are resuming the target resume all of the threads before
		// resuming the target
		// this gives the threads a chance to save state, etc before the VM is
		// resumed
		Iterator iter = getThreadIterator();
		JavaScriptThread thread = null;
		while (iter.hasNext()) {
			thread = (JavaScriptThread) iter.next();
			if (thread.isSuspended()) {
				thread.targetResume();
			}
		}
		this.suspended = false;
		resumeVM(false);
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		if (isSuspended() || !isAvailable()) {
			// no-op if the target is suspended or not ready
			return;
		}
		try {
			suspended = true;
			if (this.vm != null) {
				this.vm.suspend();
			}
			// set all owned, unsuspended threads as suspended if we suspend the
			// target
			Iterator iter = getThreadIterator();
			while (iter.hasNext()) {
				JavaScriptThread thread = (JavaScriptThread) iter.next();
				if (!thread.isSuspended()) {
					thread.markSuspended();
				}
			}
		} finally {
			fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptDebugTarget#getScriptGroup()
	 */
	public IScriptGroup getScriptGroup() {
		return scriptgroup;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
		if (!isAvailable()) {
			// already done
			return;
		}
		if (!this.supportsDisconnect) {
			notSupported(NLS.bind(
					ModelMessages.JSDIDebugTarget_not_support_disconnect,
					getName()), null);
		}
		try {
			// first resume the VM, do not leave it in a suspended state
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=304574
			resumeVM(true);
		} 
		catch(RuntimeException rte) {}
		finally {
			cleanup();
			disposeVM(true);
			this.disconnected = true;
			fireTerminateEvent();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (!isAvailable()) {
			// already done
			return;
		}
		if (!this.supportsTerminate) {
			notSupported(NLS.bind(
					ModelMessages.JSDIDebugTarget_not_support_terminate,
					getName()), null);
		}
		this.terminating = true;
		try {
			// first resume the VM, do not leave it in a suspended state
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=304574
			resumeVM(true);
			// next terminate the underlying process
			if (this.process != null) {
				this.process.terminate();
			}
			this.terminated = true;
		} finally {
			disposeVM(true);
			cleanup();
			this.terminating = false;
			fireTerminateEvent();
		}
	}

	/**
	 * disposes the underlying {@link VirtualMachine}
	 * 
	 * @param shutdown
	 * @throws DebugException
	 */
	void disposeVM(boolean shutdown) throws DebugException {
		if(this.vm != null) {
			try {
				this.vm.dispose();
			}
			catch(RuntimeException rte) {
				if(!shutdown) {
					disconnect();
				}
			}
		}
	}
	
	/**
	 * resumes the underlying {@link VirtualMachine}
	 * 
	 * @param shutdown if the method is being called during a terminating call
	 * @throws DebugException 
	 */
	void resumeVM(boolean shutdown) throws DebugException {
		if(this.vm != null) {
			try {
				this.vm.resume();
			}
			catch(RuntimeException rte) {
				if(!shutdown) {
					disconnect();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long,
	 * long)
	 */
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		notSupported(ModelMessages.JSDIDebugTarget_unsupported_operation, null);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval
	 * ()
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugElement#getVM()
	 */
	public VirtualMachine getVM() {
		return this.vm;
	}

	/**
	 * @return if the target is available to be disconnected or terminated
	 */
	public boolean isAvailable() {
		return !(this.terminated || this.terminating || this.disconnected);
	}

	/**
	 * @return the event dispatcher
	 */
	EventDispatcher getEventDispatcher() {
		return this.eventDispatcher;
	}

	/**
	 * Delegate method to create a new {@link JavaScriptThread} and add it to the list
	 * of threads
	 * 
	 * @param thread the underlying {@link ThreadReference}
	 * @return a new {@link JavaScriptThread}
	 */
	private synchronized JavaScriptThread createThread(ThreadReference thread, boolean fireEvent) {
		if (isDisconnected()) {
			return null;
		}
		JavaScriptThread jsdiThread = findThread(thread);
		if (jsdiThread != null) {
			return jsdiThread;
		}
		jsdiThread = new JavaScriptThread(this, thread);
		this.threads.add(jsdiThread);
		if (fireEvent) {
			jsdiThread.fireCreationEvent();
		}
		return jsdiThread;
	}

	/**
	 * Terminates the given {@link ThreadReference} if the target is not
	 * disconnected
	 * 
	 * @param thread
	 */
	private synchronized void terminateThread(ThreadReference thread) {
		if (isDisconnected()) {
			return;
		}
		JavaScriptThread jsdiThread = findThread(thread);
		if (jsdiThread == null) {
			return;
		}
		threads.remove(jsdiThread);
		jsdiThread.markTerminated();
		jsdiThread.fireTerminateEvent();
	}

	/**
	 * Finds the {@link JavaScriptThread} mapped to the given {@link ThreadReference}
	 * 
	 * @param thread
	 * @return the mapped {@link JavaScriptThread} or <code>null</code>
	 */
	public synchronized JavaScriptThread findThread(ThreadReference thread) {
		for (Iterator iterator = threads.iterator(); iterator.hasNext();) {
			JavaScriptThread jsdiThread = (JavaScriptThread) iterator.next();
			if (jsdiThread.matches(thread)) {
				return jsdiThread;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.DebugElement#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == JavaScriptDebugTarget.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public synchronized void breakpointAdded(IBreakpoint breakpoint) {
		if (!isAvailable() || !supportsBreakpoint(breakpoint)) {
			// no-op either not ready or we don't care about the given
			// breakpoint
			return;
		}
		try {
			((JavaScriptBreakpoint) breakpoint).addToTarget(this);
			synchronized (this.breakpoints) {
				this.breakpoints.add(breakpoint);
			}
		} catch (CoreException ce) {
			JavaScriptDebugPlugin.log(ce);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public synchronized void breakpointChanged(IBreakpoint breakpoint,IMarkerDelta delta) {
		breakpointRemoved(breakpoint, delta);
		breakpointAdded(breakpoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public synchronized void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (!isAvailable() || !supportsBreakpoint(breakpoint)) {
			// no-op either not ready or we don't care about the breakpoint
			return;
		}
		((JavaScriptBreakpoint) breakpoint).removeFromTarget(this);
		synchronized (this.breakpoints) {
			this.breakpoints.remove(breakpoint);
		}
		// remove cached breakpoints from threads
		if (this.threads != null) {
			for (Iterator iter = this.threads.iterator(); iter.hasNext();) {
				((JavaScriptThread) iter.next()).removeBreakpoint((JavaScriptBreakpoint) breakpoint);
			}
		}
	}

	/**
	 * Returns the live list of breakpoints currently set in this target
	 * 
	 * @return the live list of breakpoints
	 */
	public List getBreakpoints() {
		return this.breakpoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse
	 * .debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		if (events.length == 1) {
			DebugEvent event = events[0];
			switch(event.getKind()) {
				case DebugEvent.TERMINATE: {
					if(event.getSource().equals(getProcess())) {
						shutdown();
					}
					break;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug.core.ILaunch)
	 */
	public void launchRemoved(ILaunch launch) {
		if (!isAvailable()) {
			return;
		}
		if (launch.equals(getLaunch())) {
			// This target has been unregistered, but it hasn't successfully
			// terminated.
			// Update internal state to reflect that it is disconnected
			disconnected();
		}
	}

	/**
	 * delegate to clean up if the target has been disconnected and a framework
	 * method has been called
	 */
	protected void disconnected() {
		if (!isDisconnected()) {
			this.disconnected = true;
			shutdown();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug.core.ILaunch)
	 */
	public void launchAdded(ILaunch launch) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug.core.ILaunch)
	 */
	public void launchChanged(ILaunch launch) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJSDIEventListener#eventSetComplete(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.core.model.JSDIDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public void eventSetComplete(Event event, JavaScriptDebugTarget target, boolean suspend, EventSet eventSet) {
		// thread enter
		// thread exit
		// script?

		if (event instanceof DebuggerStatementEvent) {
			DebuggerStatementEvent debuggerStatementEvent = (DebuggerStatementEvent) event;
			ThreadReference threadReference = debuggerStatementEvent.thread();
			JavaScriptThread thread = findThread(threadReference);
			thread.fireSuspendEvent(DebugEvent.BREAKPOINT);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJSDIEventListener#handleEvent(org.eclipse.wst.jsdt.debug.core.jsdi.event.Event, org.eclipse.wst.jsdt.debug.core.model.JSDIDebugTarget, boolean, org.eclipse.wst.jsdt.debug.core.jsdi.event.EventSet)
	 */
	public synchronized boolean handleEvent(Event event, JavaScriptDebugTarget target, boolean suspendVote, EventSet eventSet) {
		if (event instanceof ThreadEnterEvent) {
			ThreadEnterEvent threadEnterEvent = (ThreadEnterEvent) event;
			createThread(threadEnterEvent.thread(), true);
			return false;
		}
		if (event instanceof ThreadExitEvent) {
			ThreadExitEvent threadExitEvent = (ThreadExitEvent) event;
			terminateThread(threadExitEvent.thread());
			return false;
		}
		if (event instanceof ScriptLoadEvent) {
			if(iScriptCache != null) {
				iScriptCache.clear();
				iScriptCache = null;
			}
			fireEvent(new DebugEvent(this.scriptgroup, DebugEvent.MODEL_SPECIFIC, EventDispatcher.EVENT_SCRIPT_LOADED));
			return true;
		}

		if (event instanceof DebuggerStatementEvent) {
			DebuggerStatementEvent debuggerStatementEvent = (DebuggerStatementEvent) event;
			ThreadReference threadReference = debuggerStatementEvent.thread();
			JavaScriptThread thread = findThread(threadReference);
			if(!thread.isSuspended()) {
				thread.markSuspended();
			}
			return false;
		}

		// handle VM events i.e. death / disconnect
		if (event instanceof VMDeathEvent) {
			try {
				if (!this.terminated) {
					eventCleanup();
				}
			} finally {
				shutdown();
			}
			return false;
		}
		if (event instanceof VMDisconnectEvent) {
			try {
				if (!this.disconnected) {
					eventCleanup();
				}
			} finally {
				shutdown();
			}
			return false;
		}
		throw new IllegalArgumentException(NLS.bind(
				ModelMessages.JSDIDebugTarget_recieved_unknown_event, event
						.toString()));
	}

	/**
	 * Cleans up the target
	 */
	void eventCleanup() {
		try {
			cleanup();
		} finally {
			this.disconnected = true;
			this.terminated = true;
			fireTerminateEvent();
		}
	}
}
