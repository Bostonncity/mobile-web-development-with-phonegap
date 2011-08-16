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
package org.eclipse.wst.jsdt.debug.internal.ui.actions;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.debug.core.model.IScript;
import org.eclipse.wst.jsdt.debug.internal.core.model.Script;

/**
 * Context menu action to show the source for a selected {@link IScript}
 * 
 * @since 1.1
 */
public class OpenSourceAction implements IObjectActionDelegate, IActionDelegate2, IHandler2 {

	private Script script = null;
	
	/**
	 * Constructor
	 */
	public OpenSourceAction() {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if(script != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			ISourceLookupResult result = DebugUITools.lookupSource(script, script.getDebugTarget().getLaunch().getSourceLocator());
			DebugUITools.displaySource(result, page);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if(ss.isEmpty()) {
				script = null;
			}
			else {
				script = (Script) ss.getFirstElement();
			}
			return;
		}
		script = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose() {
		script = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		run(null);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 */
	public boolean isEnabled() {
		return script != null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#isHandled()
	 */
	public boolean isHandled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler2#setEnabled(java.lang.Object)
	 */
	public void setEnabled(Object evaluationContext) {		
		if(evaluationContext instanceof EvaluationContext) {
			Object o = ((EvaluationContext)evaluationContext).getDefaultVariable();
			if(o instanceof IScript) {
				script = (Script) o;
			}
			else if(o instanceof List) {
				List vars = (List) o;
				if(!vars.isEmpty() && vars.get(0) instanceof IScript) {
					script = (Script) vars.get(0);
				}
			}
			return;
		}
		script = null;
	}
}
