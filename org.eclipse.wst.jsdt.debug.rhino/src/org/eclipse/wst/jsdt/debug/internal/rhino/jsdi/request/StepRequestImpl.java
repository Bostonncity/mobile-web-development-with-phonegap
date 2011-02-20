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
package org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.request;

import org.eclipse.wst.jsdt.debug.core.jsdi.ThreadReference;
import org.eclipse.wst.jsdt.debug.core.jsdi.request.StepRequest;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.ThreadReferenceImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.VirtualMachineImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.JSONConstants;

/**
 * Rhino implementation of {@link StepRequest}
 * 
 * @since 1.0
 */
public class StepRequestImpl extends EventRequestImpl implements StepRequest {

	private final ThreadReferenceImpl thread;
	private final int step;

	/**
	 * Constructor
	 * @param vm
	 * @param thread
	 * @param step
	 */
	public StepRequestImpl(VirtualMachineImpl vm, ThreadReference thread, int step) {
		super(vm);
		this.thread = (ThreadReferenceImpl) thread;
		this.step = step;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.StepRequest#step()
	 */
	public int step() {
		return step;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.core.jsdi.request.StepRequest#thread()
	 */
	public ThreadReference thread() {
		return thread;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.request.EventRequestImpl#setEnabled(boolean)
	 */
	public synchronized void setEnabled(boolean enabled) {
		checkDeleted();
		if (this.enabled == enabled) {
			return;
		}
		if (enabled) {
			String stepType = null;
			if (step == STEP_INTO) {
				stepType = JSONConstants.STEP_IN;
			}
			else if (step == STEP_OVER) {
				stepType = JSONConstants.STEP_NEXT;
			}
			else if (step == STEP_OUT) {
				stepType = JSONConstants.STEP_OUT;
			}
			if (thread.getStep() != null) {
				throw new IllegalStateException("duplicate step"); //$NON-NLS-1$
			}
			thread.setStep(stepType);
		} else {
			thread.setStep(null);
		}
		this.enabled = enabled;
	}
}