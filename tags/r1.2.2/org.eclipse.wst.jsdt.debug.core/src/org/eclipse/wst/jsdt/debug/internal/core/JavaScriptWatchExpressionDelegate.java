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
package org.eclipse.wst.jsdt.debug.internal.core;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptStackFrame;

/**
 * Default watch expression delegate for the JavaScript debugging
 * 
 * @since 1.0
 */
public class JavaScriptWatchExpressionDelegate implements IWatchExpressionDelegate {

	/**
	 * Default result to return from the delegate
	 */
	class WatchResult implements IWatchExpressionResult {
		String expression = null;
		IValue value = null;
		String[] errormessages = null;
		DebugException exception = null;
		
		public WatchResult(String expression) {
			this.expression = expression;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getValue()
		 */
		public IValue getValue() {
			return this.value;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#hasErrors()
		 */
		public boolean hasErrors() {
			return this.errormessages != null || this.exception != null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getErrorMessages()
		 */
		public String[] getErrorMessages() {
			return this.errormessages;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getExpressionText()
		 */
		public String getExpressionText() {
			return this.expression;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.model.IWatchExpressionResult#getException()
		 */
		public DebugException getException() {
			return this.exception;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IWatchExpressionDelegate#evaluateExpression(java.lang.String, org.eclipse.debug.core.model.IDebugElement, org.eclipse.debug.core.model.IWatchExpressionListener)
	 */
	public void evaluateExpression(String expression, IDebugElement context, IWatchExpressionListener listener) {
		WatchResult result = new WatchResult(expression);
		try {
			IStackFrame fcontext  = getFrameContext(context);
			if(fcontext instanceof IJavaScriptStackFrame) {
				IJavaScriptStackFrame frame = (IJavaScriptStackFrame) fcontext;
				result.value = frame.evaluate(expression);
			}
		}
		catch(DebugException de) {
			result.exception = de;
			result.errormessages = new String[] {de.getMessage()};
		}
		finally {
			listener.watchEvaluationFinished(result);
		}
	}
	
	/**
	 * Returns the {@link IStackFrame} context to evaluate the expression with
	 * or <code>null</code> if the element is not an {@link IStackFrame} or an {@link IThread}, or 
	 * if the call to {@link IThread#getTopStackFrame()} fails.
	 * 
	 * @param element the element to get the {@link IStackFrame} context from
	 * @return the {@link IStackFrame} context or <code>null</code>
	 * @throws DebugException
	 */
	IStackFrame getFrameContext(IDebugElement element) throws DebugException {
		if(element instanceof IStackFrame) {
			return (IStackFrame) element;
		}
		if(element instanceof IThread) {
			return ((IThread)element).getTopStackFrame();
		}
		return null;
	}
}
