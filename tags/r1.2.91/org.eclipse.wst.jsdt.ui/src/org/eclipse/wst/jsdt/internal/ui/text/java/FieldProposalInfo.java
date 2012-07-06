/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.internal.ui.text.java;

import org.eclipse.wst.jsdt.core.CompletionProposal;
import org.eclipse.wst.jsdt.core.IField;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.corext.template.java.SignatureUtil;


/**
 * Proposal info that computes the javadoc lazily when it is queried.
 *
 * 
 */
public final class FieldProposalInfo extends MemberProposalInfo {

	/**
	 * Creates a new proposal info.
	 *
	 * @param project the java project to reference when resolving types
	 * @param proposal the proposal to generate information for
	 */
	public FieldProposalInfo(IJavaScriptProject project, CompletionProposal proposal) {
		super(project, proposal);
	}

	/**
	 * Resolves the member described by the receiver and returns it if found.
	 * Returns <code>null</code> if no corresponding member can be found.
	 *
	 * @return the resolved member or <code>null</code> if none is found
	 * @throws JavaScriptModelException if accessing the java model fails
	 */
	protected IMember resolveMember() throws JavaScriptModelException {
		char[] declarationSignature= fProposal.getDeclarationSignature();
		// for synthetic fields on arrays, declaration signatures may be null
		// TODO remove when https://bugs.eclipse.org/bugs/show_bug.cgi?id=84690 gets fixed
		if (declarationSignature == null)
			return null;
		String typeName= SignatureUtil.stripSignatureToFQN(String.valueOf(declarationSignature));
		IType[] types = this.fJavaProject.findTypes(typeName);
		if(types != null && types.length > 0) {
			for(int i = 0; i < types.length; ++i) {
				IType type = types[i];
				if (type != null) {
					String name= String.valueOf(fProposal.getName());
					IField field= type.getField(name);
					if (field.exists())
						return field;
				}
			}
		}
		
		return null;
	}
}
