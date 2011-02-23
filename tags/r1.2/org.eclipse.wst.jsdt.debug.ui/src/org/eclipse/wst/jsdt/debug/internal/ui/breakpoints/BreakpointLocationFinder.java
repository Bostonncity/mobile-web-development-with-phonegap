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
package org.eclipse.wst.jsdt.debug.internal.ui.breakpoints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.wst.jsdt.core.dom.ArrayAccess;
import org.eclipse.wst.jsdt.core.dom.ArrayCreation;
import org.eclipse.wst.jsdt.core.dom.ArrayInitializer;
import org.eclipse.wst.jsdt.core.dom.ArrayType;
import org.eclipse.wst.jsdt.core.dom.Assignment;
import org.eclipse.wst.jsdt.core.dom.Block;
import org.eclipse.wst.jsdt.core.dom.BlockComment;
import org.eclipse.wst.jsdt.core.dom.BodyDeclaration;
import org.eclipse.wst.jsdt.core.dom.BooleanLiteral;
import org.eclipse.wst.jsdt.core.dom.BreakStatement;
import org.eclipse.wst.jsdt.core.dom.CatchClause;
import org.eclipse.wst.jsdt.core.dom.CharacterLiteral;
import org.eclipse.wst.jsdt.core.dom.ClassInstanceCreation;
import org.eclipse.wst.jsdt.core.dom.ConditionalExpression;
import org.eclipse.wst.jsdt.core.dom.ConstructorInvocation;
import org.eclipse.wst.jsdt.core.dom.ContinueStatement;
import org.eclipse.wst.jsdt.core.dom.DoStatement;
import org.eclipse.wst.jsdt.core.dom.EmptyStatement;
import org.eclipse.wst.jsdt.core.dom.EnhancedForStatement;
import org.eclipse.wst.jsdt.core.dom.Expression;
import org.eclipse.wst.jsdt.core.dom.ExpressionStatement;
import org.eclipse.wst.jsdt.core.dom.FieldAccess;
import org.eclipse.wst.jsdt.core.dom.FieldDeclaration;
import org.eclipse.wst.jsdt.core.dom.ForInStatement;
import org.eclipse.wst.jsdt.core.dom.ForStatement;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.FunctionExpression;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.FunctionRef;
import org.eclipse.wst.jsdt.core.dom.FunctionRefParameter;
import org.eclipse.wst.jsdt.core.dom.IVariableBinding;
import org.eclipse.wst.jsdt.core.dom.IfStatement;
import org.eclipse.wst.jsdt.core.dom.ImportDeclaration;
import org.eclipse.wst.jsdt.core.dom.InferredType;
import org.eclipse.wst.jsdt.core.dom.InfixExpression;
import org.eclipse.wst.jsdt.core.dom.Initializer;
import org.eclipse.wst.jsdt.core.dom.InstanceofExpression;
import org.eclipse.wst.jsdt.core.dom.JSdoc;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.LabeledStatement;
import org.eclipse.wst.jsdt.core.dom.LineComment;
import org.eclipse.wst.jsdt.core.dom.ListExpression;
import org.eclipse.wst.jsdt.core.dom.MemberRef;
import org.eclipse.wst.jsdt.core.dom.Modifier;
import org.eclipse.wst.jsdt.core.dom.Name;
import org.eclipse.wst.jsdt.core.dom.NullLiteral;
import org.eclipse.wst.jsdt.core.dom.NumberLiteral;
import org.eclipse.wst.jsdt.core.dom.ObjectLiteral;
import org.eclipse.wst.jsdt.core.dom.ObjectLiteralField;
import org.eclipse.wst.jsdt.core.dom.PackageDeclaration;
import org.eclipse.wst.jsdt.core.dom.ParenthesizedExpression;
import org.eclipse.wst.jsdt.core.dom.PostfixExpression;
import org.eclipse.wst.jsdt.core.dom.PrefixExpression;
import org.eclipse.wst.jsdt.core.dom.PrimitiveType;
import org.eclipse.wst.jsdt.core.dom.QualifiedName;
import org.eclipse.wst.jsdt.core.dom.QualifiedType;
import org.eclipse.wst.jsdt.core.dom.RegularExpressionLiteral;
import org.eclipse.wst.jsdt.core.dom.ReturnStatement;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.core.dom.SimpleType;
import org.eclipse.wst.jsdt.core.dom.SingleVariableDeclaration;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.eclipse.wst.jsdt.core.dom.SuperConstructorInvocation;
import org.eclipse.wst.jsdt.core.dom.SuperFieldAccess;
import org.eclipse.wst.jsdt.core.dom.SuperMethodInvocation;
import org.eclipse.wst.jsdt.core.dom.SwitchCase;
import org.eclipse.wst.jsdt.core.dom.SwitchStatement;
import org.eclipse.wst.jsdt.core.dom.TagElement;
import org.eclipse.wst.jsdt.core.dom.TextElement;
import org.eclipse.wst.jsdt.core.dom.ThisExpression;
import org.eclipse.wst.jsdt.core.dom.ThrowStatement;
import org.eclipse.wst.jsdt.core.dom.TryStatement;
import org.eclipse.wst.jsdt.core.dom.TypeDeclaration;
import org.eclipse.wst.jsdt.core.dom.TypeDeclarationStatement;
import org.eclipse.wst.jsdt.core.dom.TypeLiteral;
import org.eclipse.wst.jsdt.core.dom.UndefinedLiteral;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationExpression;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationFragment;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationStatement;
import org.eclipse.wst.jsdt.core.dom.WhileStatement;
import org.eclipse.wst.jsdt.core.dom.WithStatement;

/**
 * AST visitor to find the (proposed) location in source to put a breakpoint
 * 
 * @since 1.0
 */
public class BreakpointLocationFinder extends ASTVisitor {

	/**
	 * <code>debugger</code> code statement
	 */
	public static final String DEBUGGER = "debugger"; //$NON-NLS-1$
	public static final int UNKNOWN = -1;
	public static final int LINE = 0;
	public static final int FUNCTION = 1;
	
	/**
	 * found location, initially {@link #UNKNOWN}
	 */
	private int location = UNKNOWN;
	private JavaScriptUnit jsunit = null;
	/**
	 * The name of the function
	 * @since 1.1
	 */
	private String functionName = null;
	/**
	 * The computed offset to the start of the element
	 * @since 1.1
	 */
	private int offset = -1;
	private boolean found = false;
	private boolean resolvedbindings = false;
	private ArrayList labels = new ArrayList();
	private int linenumber = UNKNOWN;
	
	/**
	 * Constructor
	 * @param jsunit
	 * @param linenumber
	 * @param resolvedbindings
	 */
	public BreakpointLocationFinder(JavaScriptUnit jsunit, int linenumber, boolean resolvedbindings) {
		this.jsunit = jsunit;
		this.linenumber = linenumber;
		this.resolvedbindings = resolvedbindings;
	}
	
	/**
	 * Gets the line number from the backing {@link JavaScriptUnit} from
	 * the given offset
	 * @param offset
	 * @return the line number from the given offset
	 */
	int lineNumber(int offset) {
        int lineNumber = jsunit.getLineNumber(offset);
        return lineNumber < 1 ? 1 : lineNumber;
    }
	
	String getLabel() {
        if (labels == null || labels.isEmpty()) {
            return null;
        }
        return (String) labels.get(labels.size() - 1);
    }
    
    void nestLabel(String label) {
        if (labels == null) {
            labels = new ArrayList();
        }
        labels.add(label);
    }
    
    void popLabel() {
        if (labels == null || labels.isEmpty()) {
            return;
        }
        labels.remove(labels.size() - 1);
    }
	
	/**
	 * Return <code>true</code> if this node children may contain a valid location
	 * for the breakpoint.
	 * @param node the node.
	 * @param isCode true indicated that the first line of the given node always
	 *	contains some executable code, even if split in multiple lines.
	 */
	boolean visit(ASTNode node, boolean isCode) {
		if (this.found) {
			return false;
		}
		int startPosition = node.getStartPosition();
		int endLine = lineNumber(startPosition + node.getLength() - 1);
		if (endLine < this.linenumber) {
			return false;
		}
		int startLine = lineNumber(startPosition);
		if (isCode && (this.linenumber <= startLine)) {
			this.linenumber = startLine;
			this.found= true;
			this.offset = startPosition;
			this.location = LINE;
			//TODO compute a type name for the script?
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the proposed location for the breakpoint
	 * @return the proposed location for the breakpoint
	 */
	public int getLocation() {
		return this.location;
	}

	/**
	 * Returns the computed offset into the AST at the start location of the location
	 * element. The AST parser computes this value, we derive it from the source start location
	 * of the ASTNode found as the valid location.
	 * 
	 * @return the source start position for the found location or -1 if no position was found
	 * @since 1.1
	 */
	public int getOffset() {
		return this.offset;
	}
	
	/**
	 * Returns the name of the function for the found location iff the 
	 * found location is a function.
	 * 
	 *  @return the name of the function for the found location or <code>null</code>
	 *  @since 1.1
	 */
	public String getFunctionName() {
		return this.functionName;
	}
	
	/**
	 * Returns the proposed line number for this breakpoint
	 * @return the proposed line number
	 */
	public int getLineNumber() {
		return this.linenumber;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.AnonymousClassDeclaration)
	 */
	public boolean visit(AnonymousClassDeclaration node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ArrayAccess)
	 */
	public boolean visit(ArrayAccess node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ArrayCreation)
	 */
	public boolean visit(ArrayCreation node) {
		return visit(node, node.getInitializer() == null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ArrayInitializer)
	 */
	public boolean visit(ArrayInitializer node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ArrayType)
	 */
	public boolean visit(ArrayType node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.Assignment)
	 */
	public boolean visit(Assignment node) {
		if (visit(node, false)) {
			Expression leftHandSide = node.getLeftHandSide();
			if (leftHandSide instanceof Name) {
				int startLine = lineNumber(node.getStartPosition());
				if (this.linenumber < startLine) {
					if (this.resolvedbindings) {
						IVariableBinding binding= (IVariableBinding)((org.eclipse.wst.jsdt.core.dom.Name)leftHandSide).resolveBinding();
						if (binding != null && (!binding.isField() || Modifier.isStatic(binding.getModifiers())))  {
							node.getRightHandSide().accept(this);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.Block)
	 */
	public boolean visit(Block node) {
		if(visit(node, false)) {
			if(node.statements().isEmpty() && node.getParent().getNodeType() == ASTNode.FUNCTION_DECLARATION) {
				int offset = node.getStartPosition() + node.getLength() - 1;
				this.linenumber = lineNumber(offset);
				this.found = true;
				this.location = LINE;
				this.offset = offset;
				//TODO compute a type name for the script?
				return false;
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.BlockComment)
	 */
	public boolean visit(BlockComment node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.BooleanLiteral)
	 */
	public boolean visit(BooleanLiteral node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.BreakStatement)
	 */
	public boolean visit(BreakStatement node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.CatchClause)
	 */
	public boolean visit(CatchClause node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.CharacterLiteral)
	 */
	public boolean visit(CharacterLiteral node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ClassInstanceCreation)
	 */
	public boolean visit(ClassInstanceCreation node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ConditionalExpression)
	 */
	public boolean visit(ConditionalExpression node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ConstructorInvocation)
	 */
	public boolean visit(ConstructorInvocation node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ContinueStatement)
	 */
	public boolean visit(ContinueStatement node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.DoStatement)
	 */
	public boolean visit(DoStatement node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.EmptyStatement)
	 */
	public boolean visit(EmptyStatement node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.EnhancedForStatement)
	 */
	public boolean visit(EnhancedForStatement node) {
		if (visit(node, false)) {
			node.getExpression().accept(this);
			node.getBody().accept(this);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ExpressionStatement)
	 */
	public boolean visit(ExpressionStatement node) {
		if(this.found) {
			return false;
		}
		ASTNode child = node.getExpression();
		if(child.getNodeType() == ASTNode.SIMPLE_NAME) {
			SimpleName name = (SimpleName) child;
			if(name.getFullyQualifiedName().equals(DEBUGGER)) {
				location = UNKNOWN;
				return false;
			}
		}
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FieldAccess)
	 */
	public boolean visit(FieldAccess node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FieldDeclaration)
	 */
	public boolean visit(FieldDeclaration node) {
		if(visit(node, false)) {
			List frags = node.fragments();
			if(frags.size() == 1) {
				int offset = ((VariableDeclarationFragment)frags.get(0)).getName().getStartPosition();
				if (lineNumber(offset) == this.linenumber) {
					this.location = LINE;
					this.offset = offset;
					this.found = true;
					return false;
				}
			}
			else {
				for (Iterator iter = frags.iterator(); iter.hasNext();) {
					((VariableDeclarationFragment)iter.next()).accept(this);
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ForInStatement)
	 */
	public boolean visit(ForInStatement node) {
		if(visit(node, true)) {
			node.getBody().accept(this);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ForStatement)
	 */
	public boolean visit(ForStatement node) {
		// in case on a "for(;;)", the breakpoint can be set on the first token of the node.
		return visit(node, node.initializers().isEmpty() && node.getExpression() == null && node.updaters().isEmpty());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FunctionDeclaration)
	 */
	public boolean visit(FunctionDeclaration node) {
		int nameOffset = node.getStartPosition();
		if (lineNumber(nameOffset) == this.linenumber) {
			this.location = FUNCTION;
			this.found = true;
			this.functionName = node.getName() == null ? null : node.getName().getIdentifier();
			this.offset = nameOffset;
			return false;
		}
		Block body = node.getBody();
		if(body != null) {
			body.accept(this);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FunctionExpression)
	 */
	public boolean visit(FunctionExpression node) {
		node.getMethod().accept(this);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FunctionInvocation)
	 */
	public boolean visit(FunctionInvocation node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FunctionRef)
	 */
	public boolean visit(FunctionRef node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.FunctionRefParameter)
	 */
	public boolean visit(FunctionRefParameter node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.IfStatement)
	 */
	public boolean visit(IfStatement node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ImportDeclaration)
	 */
	public boolean visit(ImportDeclaration node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.InferredType)
	 */
	public boolean visit(InferredType node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.InfixExpression)
	 */
	public boolean visit(InfixExpression node) {
		// TODO visit all operands?
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.Initializer)
	 */
	public boolean visit(Initializer node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.InstanceofExpression)
	 */
	public boolean visit(InstanceofExpression node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.JavaScriptUnit)
	 */
	public boolean visit(JavaScriptUnit node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.JSdoc)
	 */
	public boolean visit(JSdoc node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.LabeledStatement)
	 */
	public boolean visit(LabeledStatement node) {
		nestLabel(node.getLabel().getFullyQualifiedName());
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#endVisit(org.eclipse.wst.jsdt.core.dom.LabeledStatement)
	 */
	public void endVisit(LabeledStatement node) {
		popLabel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.LineComment)
	 */
	public boolean visit(LineComment node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ListExpression)
	 */
	public boolean visit(ListExpression node) {
		//TODO inspect elements of the list explicitly?
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.MemberRef)
	 */
	public boolean visit(MemberRef node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.Modifier)
	 */
	public boolean visit(Modifier node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.NullLiteral)
	 */
	public boolean visit(NullLiteral node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.NumberLiteral)
	 */
	public boolean visit(NumberLiteral node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ObjectLiteral)
	 */
	public boolean visit(ObjectLiteral node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ObjectLiteralField)
	 */
	public boolean visit(ObjectLiteralField node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.PackageDeclaration)
	 */
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ParenthesizedExpression)
	 */
	public boolean visit(ParenthesizedExpression node) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.PostfixExpression)
	 */
	public boolean visit(PostfixExpression node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.PrefixExpression)
	 */
	public boolean visit(PrefixExpression node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.PrimitiveType)
	 */
	public boolean visit(PrimitiveType node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.QualifiedName)
	 */
	public boolean visit(QualifiedName node) {
		visit(node, true);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.QualifiedType)
	 */
	public boolean visit(QualifiedType node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.RegularExpressionLiteral)
	 */
	public boolean visit(RegularExpressionLiteral node) {
		//TODO is there more to visit here?
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ReturnStatement)
	 */
	public boolean visit(ReturnStatement node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SimpleName)
	 */
	public boolean visit(SimpleName node) {
		return visit(node, !node.getFullyQualifiedName().equals(getLabel()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SimpleType)
	 */
	public boolean visit(SimpleType node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SingleVariableDeclaration)
	 */
	public boolean visit(SingleVariableDeclaration node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.StringLiteral)
	 */
	public boolean visit(StringLiteral node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SuperConstructorInvocation)
	 */
	public boolean visit(SuperConstructorInvocation node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SuperFieldAccess)
	 */
	public boolean visit(SuperFieldAccess node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SuperMethodInvocation)
	 */
	public boolean visit(SuperMethodInvocation node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SwitchCase)
	 */
	public boolean visit(SwitchCase node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.SwitchStatement)
	 */
	public boolean visit(SwitchStatement node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.TagElement)
	 */
	public boolean visit(TagElement node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.TextElement)
	 */
	public boolean visit(TextElement node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ThisExpression)
	 */
	public boolean visit(ThisExpression node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.ThrowStatement)
	 */
	public boolean visit(ThrowStatement node) {
		return visit(node, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.TryStatement)
	 */
	public boolean visit(TryStatement node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.TypeDeclaration)
	 */
	public boolean visit(TypeDeclaration node) {
		if(visit(node, false)) {
			List bdecls = node.bodyDeclarations();
			for (Iterator iter = bdecls.iterator(); iter.hasNext();) {
				((BodyDeclaration)iter.next()).accept(this);
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.TypeDeclarationStatement)
	 */
	public boolean visit(TypeDeclarationStatement node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.TypeLiteral)
	 */
	public boolean visit(TypeLiteral node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.UndefinedLiteral)
	 */
	public boolean visit(UndefinedLiteral node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.VariableDeclarationExpression)
	 */
	public boolean visit(VariableDeclarationExpression node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.VariableDeclarationFragment)
	 */
	public boolean visit(VariableDeclarationFragment node) {
		Expression init = node.getInitializer();
		//TODO should we visit the init first?
		//the common pattern for function declarations is:
		//var func = function() {..}
		if(visit(node, false) && init != null) {
			init.accept(this);
			if(!this.found) {
				int offset = node.getName().getStartPosition();
				int startLine = lineNumber(offset);
				if (this.linenumber == startLine) {
					this.linenumber = startLine;
					this.found = true;
					this.location = LINE;
					this.offset = offset;
					//TODO resolve script name?
					return false;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.VariableDeclarationStatement)
	 */
	public boolean visit(VariableDeclarationStatement node) {
		return visit(node, node.getParent().getNodeType() == ASTNode.BLOCK);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.WhileStatement)
	 */
	public boolean visit(WhileStatement node) {
		return visit(node, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.core.dom.ASTVisitor#visit(org.eclipse.wst.jsdt.core.dom.WithStatement)
	 */
	public boolean visit(WithStatement node) {
		return visit(node, false);
	}
}