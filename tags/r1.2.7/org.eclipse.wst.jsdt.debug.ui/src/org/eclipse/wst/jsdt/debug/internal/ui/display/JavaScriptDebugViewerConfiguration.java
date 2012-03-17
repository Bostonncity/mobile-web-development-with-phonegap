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
package org.eclipse.wst.jsdt.debug.internal.ui.display;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.wst.jsdt.debug.internal.ui.JavaScriptDebugUIPlugin;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.jsdt.ui.text.JavaScriptSourceViewerConfiguration;

/**
 *  The source viewer configuration for snippets.
 *  
 *  @since 1.0
 */
public class JavaScriptDebugViewerConfiguration extends JavaScriptSourceViewerConfiguration {
		
	public JavaScriptDebugViewerConfiguration() {
		super(JavaScriptPlugin.getDefault().getJavaTextTools().getColorManager(), 
				new ChainedPreferenceStore(new IPreferenceStore[] {
						PreferenceConstants.getPreferenceStore(),
						EditorsUI.getPreferenceStore()}),
				null, null);
	}
	
	/**
	 * Returns the preference store this source viewer configuration is associated with.
	 * 
	 * @return the preference store
	 */
	public IPreferenceStore getTextPreferenceStore() {
		return fPreferenceStore;
	}
	
	/**
	 * @return the {@link IContentAssistProcessor} to use
	 */
	public IContentAssistProcessor getProcessor() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(getProcessor(),	IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return assistant;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		ITextDoubleClickStrategy clickStrat = new ITextDoubleClickStrategy() {
			// Highlight the whole line when double clicked. See Bug#45481 
			public void doubleClicked(ITextViewer viewer) {
				try {
					IDocument doc = viewer.getDocument();
					int caretOffset = viewer.getSelectedRange().x;
					int lineNum = doc.getLineOfOffset(caretOffset);
					int start = doc.getLineOffset(lineNum);
					int length = doc.getLineLength(lineNum);
					viewer.setSelectedRange(start, length);
				} catch (BadLocationException e) {
					JavaScriptDebugUIPlugin.log(e);
				}
			}
		};
		return clickStrat;
	}	
}
