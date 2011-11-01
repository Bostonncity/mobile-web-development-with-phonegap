/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.ui;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.IDebugModelPresentationExtension;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.jsdt.core.Signature;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptFunctionBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.core.model.IJavaScriptValue;
import org.eclipse.wst.jsdt.debug.core.model.IScript;
import org.eclipse.wst.jsdt.debug.core.model.IScriptGroup;
import org.eclipse.wst.jsdt.debug.internal.core.TextUtils;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptValue;

/**
 * Default model presentation for JSDI model elements
 * 
 * @since 0.9
 */
public class JavaScriptModelPresentation extends LabelProvider implements IDebugModelPresentationExtension {

	/**
	 * Qualified names presentation property (value <code>"DISPLAY_QUALIFIED_NAMES"</code>).
	 * When <code>DISPLAY_QUALIFIED_NAMES</code> is set to <code>True</code>,
	 * this label provider should use fully qualified type names when rendering elements.
	 * When set to <code>False</code>, this label provider should use simple
	 * names when rendering elements.
	 * @see #setAttribute(String, Object)
	 */
	static final String DISPLAY_QUALIFIED_NAMES = "DISPLAY_QUALIFIED_NAMES"; //$NON-NLS-1$
	
	/**
	 * Map of attributes set from the debug model - i.e. things like if qualified names are being shown or not
	 */
	HashMap attributes = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentationExtension#requiresUIThread(java.lang.Object)
	 */
	public boolean requiresUIThread(Object element) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(IValue value, IValueDetailListener listener) {
		if(value instanceof IJavaScriptValue) {
			listener.detailComputed(value, ((IJavaScriptValue)value).getDetailString());	
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attribute, Object value) {
		if(this.attributes == null) {
			this.attributes = new HashMap();
		}
		this.attributes.put(attribute, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	public void dispose() {
		if(this.attributes != null) {
			this.attributes.clear();
			this.attributes = null;
		}
		super.dispose();
	}
	
	/**
	 * @return true is qualified names are being shown in the various debug views
	 */
	boolean showQualifiedNames() {
		if(this.attributes != null) {
			Boolean show = (Boolean) this.attributes.get(DISPLAY_QUALIFIED_NAMES);
			if(show != null) {
				return show.booleanValue();
			}
		}
		//TODO hack to always return qualified names until the toggle action is platform available
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		try {
			if(element instanceof IDebugTarget) {
				return ((IDebugTarget)element).getName();
			}
			if(element instanceof IStackFrame) {
				return ((IStackFrame)element).getName();
			}
			if(element instanceof IThread) {
				return ((IThread)element).getName();
			}
			if(element instanceof IVariable) {
				return ((IVariable)element).getName();
			}
			if(element instanceof IValue) {
				return ((IValue)element).getValueString();
			}
			if(element instanceof IJavaScriptFunctionBreakpoint) {
				return getFunctionBreakpointText((IJavaScriptFunctionBreakpoint) element);
			}
			if(element instanceof IJavaScriptLoadBreakpoint) {
				return getScriptLoadBreakpointText((IJavaScriptLoadBreakpoint) element);
			}
			if(element instanceof IJavaScriptLineBreakpoint) {
				return getLineBreakpointText((IJavaScriptLineBreakpoint) element);
			}
			if(element instanceof IScriptGroup) {
				return Messages.scripts;
			}
			if(element instanceof IScript) {
				return getScriptText((IScript) element);
			}
		}
		catch(CoreException ce) {
			JavaScriptDebugUIPlugin.log(ce);
		}
		return element.toString();
	}
	
	/**
	 * Returns the display text for the given script element
	 * @param script
	 * @return the display text for the given script
	 */
	String getScriptText(IScript script) {
		String uri = script.sourceURI().toString();
		return TextUtils.shortenText(uri, 100);
	}

	/**
	 * Returns the text for a line breakpoint
	 * @param breakpoint
	 * @return the breakpoint text
	 * @throws CoreException
	 */
	String getLineBreakpointText(IJavaScriptLineBreakpoint breakpoint) throws CoreException {
		String path = getElementPath(breakpoint.getScriptPath());
		StringBuffer buffer = new StringBuffer();
		buffer.append(path).append(NLS.bind(Messages.bp_line_number, new String[] {Integer.toString(breakpoint.getLineNumber())}));
		int hitcount = breakpoint.getHitCount();
		if(hitcount > 0) {
			buffer.append(NLS.bind(Messages.bp_hit_count, new String[] {Integer.toString(hitcount)}));
		}
		if(breakpoint.isConditionEnabled()) {
			buffer.append(Messages.bp_conditonal);
		}
		if(breakpoint.getSuspendPolicy() == IJavaScriptBreakpoint.SUSPEND_TARGET) {
			buffer.append(Messages.bp_suspend_vm);
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the text for a method breakpoint
	 * @param breakpoint
	 * @return the breakpoint text
	 * @throws CoreException
	 */
	String getFunctionBreakpointText(IJavaScriptFunctionBreakpoint breakpoint) throws CoreException {
		String path = getElementPath(breakpoint.getScriptPath());
		StringBuffer buffer = new StringBuffer();
		buffer.append(path);
		if(breakpoint.isEntry()) {
			if(breakpoint.isExit()) {
				buffer.append(Messages.bp_entry_and_exit);
			}
			else {
				buffer.append(Messages.bp_entry_only);
			}
		}
		else if(breakpoint.isExit()) {
			buffer.append(Messages.bp_exit_only);
		}
		int hitcount = breakpoint.getHitCount();
		if(hitcount > 0) {
			buffer.append(NLS.bind(Messages.bp_hit_count, new String[] {Integer.toString(hitcount)}));
		}
		if(breakpoint.isConditionEnabled()) {
			buffer.append(Messages.bp_conditional);
		}
		if(breakpoint.getSuspendPolicy() == IJavaScriptBreakpoint.SUSPEND_TARGET) {
			buffer.append(Messages.bp_suspend_vm);
		}
		String method = Signature.toString(breakpoint.getSignature(), breakpoint.getFunctionName(), null, false, false);
		buffer.append(" - ").append(method); //$NON-NLS-1$
		return buffer.toString();
	}
	
	/**
	 * Returns the text for a script load breakpoint
	 * @param breakpoint
	 * @return the breakpoint text
	 * @throws CoreException
	 */
	String getScriptLoadBreakpointText(IJavaScriptLoadBreakpoint breakpoint) throws CoreException {
		String path = getElementPath(breakpoint.getScriptPath());
		StringBuffer buffer = new StringBuffer();
		buffer.append(path);
		int hitcount = breakpoint.getHitCount();
		if(hitcount > 0) {
			buffer.append(NLS.bind(Messages.bp_hit_count, new String[] {Integer.toString(hitcount)}));
		}
		if(breakpoint.getSuspendPolicy() == IJavaScriptBreakpoint.SUSPEND_TARGET) {
			buffer.append(Messages.bp_suspend_vm);
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the path of the element based on if qualified names are being shown
	 * @param path
	 * @return
	 */
	String getElementPath(String path) {
		if(! showQualifiedNames()) {
			try {
				return URIUtil.lastSegment(URIUtil.fromString(path));
			}
			catch(URISyntaxException urise) {
				JavaScriptDebugUIPlugin.log(urise);
			}
		}
		return path;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		try {
			if(element instanceof IVariable) {
				IVariable var = (IVariable) element;
				return getImageFromType(var);
			}
			if(element instanceof IJavaScriptLineBreakpoint || element instanceof IJavaScriptFunctionBreakpoint) {
				IJavaScriptBreakpoint breakpoint = (IJavaScriptBreakpoint) element;
				int flags = computeBreakpointAdornmentFlags(breakpoint);
				if(breakpoint.isEnabled()) {
					return JavaScriptImageRegistry.getImage(new JavaScriptImageDescriptor(JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_BRKP), flags));
				}
				return JavaScriptImageRegistry.getImage(new JavaScriptImageDescriptor(JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_BRKP_DISABLED), flags));
			}
			if(element instanceof IScript) {
				return JavaScriptImageRegistry.getImage(new JavaScriptImageDescriptor(JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_SCRIPT), 0));
			}
			if(element instanceof IScriptGroup) {
				return JavaScriptImageRegistry.getImage(new JavaScriptImageDescriptor(JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_SCRIPT_GRP), 0));
			}
		}
		catch(DebugException de) {
			JavaScriptDebugUIPlugin.log(de);
		}
		catch(CoreException ce) {
			JavaScriptDebugUIPlugin.log(ce);
		}
		return null;
	}
	
	/**
	 * Computes the flags for overlay adornments for a breakpoint image for the given breakpoint
	 * @param breakpoint
	 * @return the or'd set of flags describing the required overlays for the given breakpoint
	 * @see {@link JavaScriptImageDescriptor} for the complete list of accepted flags
	 */
	int computeBreakpointAdornmentFlags(IJavaScriptBreakpoint breakpoint)  {
		int flags = 0;
		try {
			if (breakpoint.isEnabled()) {
				flags |= JavaScriptImageDescriptor.ENABLED;
			}
			if (breakpoint.isInstalled()) {
				flags |= JavaScriptImageDescriptor.INSTALLED;
			}
			if (breakpoint instanceof IJavaScriptLineBreakpoint) {
				if (((IJavaScriptLineBreakpoint)breakpoint).isConditionEnabled()) {
					flags |= JavaScriptImageDescriptor.CONDITIONAL;
				}
			}
			if (breakpoint instanceof IJavaScriptFunctionBreakpoint) {
				IJavaScriptFunctionBreakpoint mBreakpoint= (IJavaScriptFunctionBreakpoint)breakpoint;
				if (mBreakpoint.isEntry()) {
					flags |= JavaScriptImageDescriptor.ENTRY;
				}
				if (mBreakpoint.isExit()) {
					flags |= JavaScriptImageDescriptor.EXIT;
				}
			}
		} 
		catch (CoreException e) {}
		return flags;
	}
	
	/**
	 * Returns the specific image for the given type
	 * @param variable the variable to get the image for
	 * @return the image
	 * @throws DebugException 
	 */
	Image getImageFromType(IVariable variable) throws DebugException {
		String name = variable.getName();
		if(JavaScriptValue.THIS.equals(name)) {
			return JavaScriptImageRegistry.getImage(new JavaScriptImageDescriptor(JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_THIS_VAR), 0));
		}
		if(JavaScriptValue.PROTO.equals(name)) {
			return JavaScriptImageRegistry.getImage(new JavaScriptImageDescriptor(JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_PROTO_VAR), 0));
		}
		return JavaScriptImageRegistry.getSharedImage(ISharedImages.IMG_LOCAL_VAR);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		try {
			IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.getName());
			return descriptor.getId();
		} catch (PartInitException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	public IEditorInput getEditorInput(Object element) {
		if(element instanceof File) {
			return new FileStoreEditorInput(EFS.getLocalFileSystem().fromLocalFile((File) element));
		}
		if(element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		}
		if(element instanceof IJavaScriptLoadBreakpoint) {
			try {
				IJavaScriptLoadBreakpoint bp = (IJavaScriptLoadBreakpoint) element;
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(bp.getScriptPath()));
				if(resource.getType() == IResource.FILE) {
					return new FileEditorInput((IFile) resource);
				}
			}
			catch(CoreException ce) {
				JavaScriptDebugUIPlugin.log(ce);
			}
		}
		if(element instanceof IJavaScriptBreakpoint) {
			IResource resource = ((IJavaScriptBreakpoint)element).getMarker().getResource();
			if(resource.getType() == IResource.FILE) {
				return new FileEditorInput((IFile) resource);
			}
		}
		return null;
	}

}
