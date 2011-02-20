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
package org.eclipse.wst.jsdt.debug.internal.ui.source.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.ITypeRoot;
import org.eclipse.wst.jsdt.internal.corext.template.java.JavaContextType;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.internal.ui.text.java.JavaParameterListValidator;
import org.eclipse.wst.jsdt.internal.ui.text.template.contentassist.TemplateEngine;
import org.eclipse.wst.jsdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.wst.jsdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.wst.jsdt.ui.text.java.IJavaCompletionProposal;

/**
 * Completion processor for the JavaScript debugger. This completion processor
 * operates on a client provided context.
 * 
 * @since 1.0
 */
public class JavaScriptContentAssistProcessor implements IContentAssistProcessor {
		
	private CompletionProposalCollector fCollector;
	private IContextInformationValidator fValidator;
	private TemplateEngine fJavaEngine;
	private TemplateEngine fStatementEngine;
    private String fErrorMessage = null;
	
	private char[] fProposalAutoActivationSet;
	private CompletionProposalComparator fComparator;
	private ScriptContext fContext;
		
	/**
	 * Constructor
	 * @param context
	 */
	public JavaScriptContentAssistProcessor(ScriptContext context) {
		fContext = context;
		TemplateContextType contextType = JavaScriptPlugin.getDefault().getTemplateContextRegistry().getContextType(JavaContextType.NAME);
		if (contextType != null) {
			fJavaEngine = new TemplateEngine(contextType);
		}
		contextType = JavaScriptPlugin.getDefault().getTemplateContextRegistry().getContextType(JavaContextType.NAME);
		if (contextType != null) {
			fStatementEngine = new TemplateEngine(contextType);
		}
		
		fComparator = new CompletionProposalComparator();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
        if (fErrorMessage != null) {
            return fErrorMessage;
        }
        if (fCollector != null) {
            return fCollector.getErrorMessage();
        }
        return null;
	}
    
    /**
     * Sets the error message for why completions could not be resolved.
     * Clients should clear this before computing completions.
     * 
     * @param string message
     */
    private void setErrorMessage(String string) {
    	String error = string;
    	if (error != null && error.length() == 0) {
			error = null;
		}
        fErrorMessage = error;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		if (fValidator == null) {
			fValidator= new JavaParameterListValidator();
		}
		return fValidator;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
        setErrorMessage(null);
		try {
			List total = new ArrayList();
			ITypeRoot root = fContext.getJavaScriptRoot();
			if(root != null) {
				IJavaScriptProject project = root.getJavaScriptProject();
				ITextSelection selection= (ITextSelection)viewer.getSelectionProvider().getSelection();
				configureResultCollector(project, selection);	
				root.codeComplete(documentOffset, fCollector);
				total.addAll(Arrays.asList(fCollector.getJavaCompletionProposals()));
			}
			if (fJavaEngine != null) {
				fJavaEngine.reset();
				fJavaEngine.complete(viewer, documentOffset, null);
				total.addAll(Arrays.asList(fJavaEngine.getResults()));
			}
			
			if (fStatementEngine != null) {
				fStatementEngine.reset();
				fStatementEngine.complete(viewer, documentOffset, null);
				total.addAll(Arrays.asList(fStatementEngine.getResults()));
			}
			return order((IJavaCompletionProposal[])total.toArray(new IJavaCompletionProposal[total.size()]));	
		} catch (CoreException x) {
			setErrorMessage(x.getStatus().getMessage());
		} finally {
			releaseCollector();
		}
		
		return null;
	}
	
	/**
	 * Order the given proposals.
	 */
	private IJavaCompletionProposal[] order(IJavaCompletionProposal[] proposals) {
		Arrays.sort(proposals, fComparator);
		return proposals;	
	}	
	
	/**
	 * Configures the display result collection for the current code assist session
	 */
	private void configureResultCollector(IJavaScriptProject project, ITextSelection selection) {
		fCollector = new CompletionProposalCollector(project);
		if (selection.getLength() != 0) {
			fCollector.setReplacementLength(selection.getLength());
		} 
	}
	
	/**
	 * Tells this processor to order the proposals alphabetically.
	 * 
	 * @param order <code>true</code> if proposals should be ordered.
	 */
	public void orderProposalsAlphabetically(boolean order) {
		fComparator.setOrderAlphabetically(order);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return fProposalAutoActivationSet;
	}
	
	/**
	 * Sets this processor's set of characters triggering the activation of the
	 * completion proposal computation.
	 * 
	 * @param activationSet the activation set
	 */
	public void setCompletionProposalAutoActivationCharacters(char[] activationSet) {
		fProposalAutoActivationSet= activationSet;
	}
	
	/**
	 * Clears reference to result proposal collector.
	 */
	private void releaseCollector() {
		if (fCollector != null && fCollector.getErrorMessage().length() > 0 && fErrorMessage != null) {
			setErrorMessage(fCollector.getErrorMessage());
		}		
		fCollector = null;
	}
}
