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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.debug.core.jsdi.Location;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.StackFrame;
import org.eclipse.wst.jsdt.debug.core.jsdi.Variable;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptValue;

/**
 * A JavaScript stack frame
 * 
 * @since 1.0
 */
public final class JavaScriptStackFrame extends JavaScriptDebugElement implements IJavaScriptStackFrame {

	/**
	 * Owning thread.
	 */
	private JavaScriptThread thread;

	/**
	 * The underlying {@link StackFrameReference}
	 */
	private StackFrame stackFrame = null;

	private String name = null;
	private ArrayList variables;

	/**
	 * Constructs a Rhino stack frame in the given thread.
	 * 
	 * @param thread
	 */
	public JavaScriptStackFrame(JavaScriptThread thread, StackFrame stackFrame) {
		super(thread.getJavaScriptDebugTarget());
		this.thread = thread;
		this.stackFrame = stackFrame;
	}

	/**
	 * Returns the underlying JSDI {@link StackFrame}
	 * 
	 * @return the underlying JSDI {@link StackFrame}
	 */
	StackFrame getUnderlyingStackFrame() {
		return this.stackFrame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharEnd()
	 */
	public int getCharEnd() throws DebugException {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getCharStart()
	 */
	public int getCharStart() throws DebugException {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getLineNumber()
	 */
	public int getLineNumber() throws DebugException {
		return (stackFrame.location() != null ? stackFrame.location().lineNumber() : -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getName()
	 */
	public String getName() throws DebugException {
		if (this.name == null) {
			this.name = NLS.bind(ModelMessages.JSDIStackFrame_stackframe_name, new String[] {
									this.stackFrame.location().scriptReference().sourceURI().toString(), 
									Integer.toString(stackFrame.location().lineNumber()) 
									});
		}
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getRegisterGroups()
	 */
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getThread()
	 */
	public IThread getThread() {
		return this.thread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#getVariables()
	 */
	public synchronized IVariable[] getVariables() throws DebugException {
		if (this.variables == null) {
			List underlyingVariables = this.stackFrame.variables();
			this.variables = new ArrayList(underlyingVariables.size() + 1);


	
			for (Iterator iterator = underlyingVariables.iterator(); iterator.hasNext();) {
				Variable variable = (Variable) iterator.next();
				JavaScriptVariable jsdiVariable = new JavaScriptVariable(this, variable);
				this.variables.add(jsdiVariable);
			}
			
			Collections.sort(variables, new Comparator() {		
				public int compare(Object arg0, Object arg1) {
					
					IVariable var0 = (IVariable) arg0;
					IVariable var1 = (IVariable) arg1;
					try {
						return var0.getName().compareToIgnoreCase(var1.getName());
					} catch (DebugException e) {
						return 0;
					}
				}
			});
			
			// add the "this" object at the front
			variables.add(0, new JavaScriptVariable(this, stackFrame.thisObject()));			
		}
		return (IVariable[]) this.variables.toArray(new IVariable[this.variables.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#hasRegisterGroups()
	 */
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStackFrame#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		return this.stackFrame.variables().size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return this.thread.canStepInto();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return this.thread.canStepOver();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return this.thread.canStepReturn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return this.thread.isStepping();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		this.thread.stepInto();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		this.thread.stepOver();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		this.thread.stepReturn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return this.thread.canResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return this.thread.canSuspend();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return this.thread.isSuspended();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		this.thread.resume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		this.thread.suspend();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return this.thread.canTerminate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return this.thread.isTerminated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		if (this.thread.canTerminate()) {
			this.thread.terminate();
		} else {
			getJavaScriptDebugTarget().terminate();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame#evaluate(java.lang.String)
	 */
	public IJavaScriptValue evaluate(String expression) {
		return this.thread.evaluate(expression);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame#getSourceName()
	 */
	public String getSourceName() {
		Location loc = getUnderlyingStackFrame().location();
		if(loc != null) {
			ScriptReference script = loc.scriptReference();
			if(script != null) {
				return URIUtil.lastSegment(script.sourceURI());
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame#getSourcePath()
	 */
	public String getSourcePath() {
		Location loc = getUnderlyingStackFrame().location();
		if(loc != null) {
			ScriptReference script = loc.scriptReference();
			if(script != null) {
				return script.sourceURI().toString();
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame#getSource()
	 */
	public String getSource() {
		Location loc = getUnderlyingStackFrame().location();
		if(loc != null) {
			ScriptReference script = loc.scriptReference();
			if(script != null) {
				return script.source();
			}
		}
		return null;
	}

}
