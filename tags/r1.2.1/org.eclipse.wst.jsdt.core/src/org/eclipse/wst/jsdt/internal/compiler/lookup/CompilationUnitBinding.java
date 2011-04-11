/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.internal.compiler.lookup;

import java.io.File;

import org.eclipse.wst.jsdt.core.Signature;
import org.eclipse.wst.jsdt.core.compiler.CharOperation;
import org.eclipse.wst.jsdt.core.infer.InferredType;
import org.eclipse.wst.jsdt.internal.compiler.ASTVisitor;
import org.eclipse.wst.jsdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.wst.jsdt.internal.compiler.ast.Assignment;
import org.eclipse.wst.jsdt.internal.compiler.ast.FunctionExpression;
import org.eclipse.wst.jsdt.internal.compiler.ast.Javadoc;
import org.eclipse.wst.jsdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.wst.jsdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.wst.jsdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.wst.jsdt.internal.compiler.ast.ProgramElement;
import org.eclipse.wst.jsdt.internal.compiler.ast.ThisReference;


public class CompilationUnitBinding  extends SourceTypeBinding {
//	public char[] sourceName;
//
//	private FieldBinding[] fields;
//
//	private FunctionBinding[] methods;
//	public long tagBits = 0; // See values in the interface TagBits below
	CompilationUnitScope compilationUnitScope;
	private char[]shortName;

	char [] path;

	public CompilationUnitBinding(CompilationUnitScope scope,PackageBinding fPackage,char [] path) {
		this(scope,fPackage,path,null);
	}

	public CompilationUnitBinding(CompilationUnitScope scope,PackageBinding fPackage,char [] path,ReferenceBinding superType ) {
		super(new char [][]{scope.referenceContext.getFileName()}, fPackage, scope);
		this.compilationUnitScope=scope;
		this.memberTypes=Binding.NO_MEMBER_TYPES;
		this.sourceName=this.fileName;
		setShortName(this.fileName);
		this.path=path;
		/* bc - allows super type of 'Window' (and other types) for a compilation unit */
		this.superclass = superType;

	}

	private void setShortName(char[] fileName) {
		for (int i=fileName.length-1;i>=0;i--)
		{
			if (fileName[i]==File.separatorChar || fileName[i]=='/')
			{
				shortName=new char[fileName.length-1-i];
				System.arraycopy(fileName, i+1, shortName, 0, shortName.length);
				return;
			}
		}
		shortName=fileName;
	}

	public int kind() {
		return COMPILATION_UNIT;
	}

	public char[] signature() /* Ljava/lang/Object; */ {
		if (this.signature != null)
			return this.signature;

		return this.signature = CharOperation.concat(Signature.C_COMPILATION_UNIT, constantPoolName(), ';');
	}

//	public char[] readableName() {
//		return sourceName;
//	}
//
////	NOTE: the return type, arg & exception types of each method of a source type are resolved when needed
////	searches up the hierarchy as long as no potential (but not exact) match was found.
//	public FunctionBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
//		// sender from refScope calls recordTypeReference(this)
//		int argCount = argumentTypes.length;
//		boolean foundNothing = true;
//
//		if ((this.tagBits & TagBits.AreMethodsComplete) != 0) { // have resolved all arg types & return type of the methods
//			long range;
//			if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
//				nextMethod: for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; imethod++) {
//					FunctionBinding method = this.methods[imethod];
//					foundNothing = false; // inner type lookups must know that a method with this name exists
//					if (method.parameters.length == argCount) {
//						TypeBinding[] toMatch = method.parameters;
//						for (int iarg = 0; iarg < argCount; iarg++)
//							if (toMatch[iarg] != argumentTypes[iarg])
//								continue nextMethod;
//						return method;
//					}
//				}
//			}
//		} else {
//			// lazily sort methods
//			if ((this.tagBits & TagBits.AreMethodsSorted) == 0) {
//				int length = this.methods.length;
//				if (length > 1)
//					ReferenceBinding.sortMethods(this.methods, 0, length);
//				this.tagBits |= TagBits.AreMethodsSorted;
//			}
//
//			long range;
//			if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
//				// check unresolved method
//				int start = (int) range, end = (int) (range >> 32);
//				for (int imethod = start; imethod <= end; imethod++) {
//					FunctionBinding method = this.methods[imethod];
//					if (resolveTypesFor(method) == null || method.returnType == null) {
//						methods();
//						return getExactMethod(selector, argumentTypes, refScope); // try again since the problem methods have been removed
//					}
//				}
//				// check dup collisions
////				boolean isSource15 = this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
//				for (int i = start; i <= end; i++) {
//					FunctionBinding method1 = this.methods[i];
//					for (int j = end; j > i; j--) {
//						FunctionBinding method2 = this.methods[j];
//						boolean paramsMatch =
////							isSource15
////							? method1.areParameterErasuresEqual(method2)
////							:
//								method1.areParametersEqual(method2);
//						if (paramsMatch) {
//							methods();
//							return getExactMethod(selector, argumentTypes, refScope); // try again since the problem methods have been removed
//						}
//					}
//				}
//				nextMethod: for (int imethod = start; imethod <= end; imethod++) {
//					FunctionBinding method = this.methods[imethod];
//					TypeBinding[] toMatch = method.parameters;
//					if (toMatch.length == argCount) {
//						for (int iarg = 0; iarg < argCount; iarg++)
//							if (toMatch[iarg] != argumentTypes[iarg])
//								continue nextMethod;
//						return method;
//					}
//				}
//			}
//		}
//
//		if (foundNothing) {
////			if (isInterface()) {
////				 if (this.superInterfaces.length == 1) {
////					if (refScope != null)
////						refScope.recordTypeReference(this.superInterfaces[0]);
////					return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
////				 }
////			} else if (this.superclass != null) {
////				if (refScope != null)
////					refScope.recordTypeReference(this.superclass);
////				return this.superclass.getExactMethod(selector, argumentTypes, refScope);
////			}
//		}
//		return null;
//	}
//
////	NOTE: the type of a field of a source type is resolved when needed
//	public FieldBinding getField(char[] fieldName, boolean needResolve) {
//
//		if ((this.tagBits & TagBits.AreFieldsComplete) != 0)
//			return ReferenceBinding.binarySearch(fieldName, this.fields);
//
//		// lazily sort fields
//		if ((this.tagBits & TagBits.AreFieldsSorted) == 0) {
//			int length = this.fields.length;
//			if (length > 1)
//				ReferenceBinding.sortFields(this.fields, 0, length);
//			this.tagBits |= TagBits.AreFieldsSorted;
//		}
//		// always resolve anyway on source types
//		FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
//		if (field != null) {
//			FieldBinding result = null;
//			try {
//				result = resolveTypeFor(field);
//				return result;
//			} finally {
//				if (result == null) {
//					// ensure fields are consistent reqardless of the error
//					int newSize = this.fields.length - 1;
//					if (newSize == 0) {
//						this.fields = Binding.NO_FIELDS;
//					} else {
//						FieldBinding[] newFields = new FieldBinding[newSize];
//						int index = 0;
//						for (int i = 0, length = this.fields.length; i < length; i++) {
//							FieldBinding f = this.fields[i];
//							if (f == field) continue;
//							newFields[index++] = f;
//						}
//						this.fields = newFields;
//					}
//				}
//			}
//		}
//		return null;
//	}
//
//
////	 NOTE: the return type, arg & exception types of each method of a source type are resolved when needed
//	public FunctionBinding[] methods() {
//		if ((this.tagBits & TagBits.AreMethodsComplete) != 0)
//			return this.methods;
//
//		// lazily sort methods
//		if ((this.tagBits & TagBits.AreMethodsSorted) == 0) {
//			int length = this.methods.length;
//			if (length > 1)
//				ReferenceBinding.sortMethods(this.methods, 0, length);
//			this.tagBits |= TagBits.AreMethodsSorted;
//		}
//
//		int failed = 0;
//		try {
//			for (int i = 0, length = this.methods.length; i < length; i++) {
//				if (resolveTypesFor(this.methods[i]) == null) {
//					this.methods[i] = null; // unable to resolve parameters
//					failed++;
//				}
//			}
//
//			// find & report collision cases
//			boolean complyTo15 = this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
//			for (int i = 0, length = this.methods.length; i < length; i++) {
//				FunctionBinding method = this.methods[i];
//				if (method == null)
//					continue;
//				char[] selector = method.selector;
//				AbstractMethodDeclaration methodDecl = null;
//				nextSibling: for (int j = i + 1; j < length; j++) {
//					FunctionBinding method2 = this.methods[j];
//					if (method2 == null)
//						continue nextSibling;
//					if (!CharOperation.equals(selector, method2.selector))
//						break nextSibling; // methods with same selector are contiguous
//
//					if (complyTo15 && method.returnType != null && method2.returnType != null) {
//						// 8.4.2, for collision to be detected between m1 and m2:
//						// signature(m1) == signature(m2) i.e. same arity, same type parameter count, can be substituted
//						// signature(m1) == erasure(signature(m2)) or erasure(signature(m1)) == signature(m2)
//						TypeBinding[] params1 = method.parameters;
//						TypeBinding[] params2 = method2.parameters;
//						int pLength = params1.length;
//						if (pLength != params2.length)
//							continue nextSibling;
//
//						TypeVariableBinding[] vars = method.typeVariables;
//						TypeVariableBinding[] vars2 = method2.typeVariables;
//						boolean equalTypeVars = vars == vars2;
//						FunctionBinding subMethod = method2;
//						if (!equalTypeVars) {
//							FunctionBinding temp = method.computeSubstitutedMethod(method2, this.scope.environment());
//							if (temp != null) {
//								equalTypeVars = true;
//								subMethod = temp;
//							}
//						}
//						boolean equalParams = method.areParametersEqual(subMethod);
//						if (equalParams && equalTypeVars) {
//							// duplicates regardless of return types
//						} else if (method.returnType.erasure() == subMethod.returnType.erasure() && (equalParams || method.areParameterErasuresEqual(method2))) {
//							// name clash for sure if not duplicates, report as duplicates
//						} else if (!equalTypeVars && vars != Binding.NO_TYPE_VARIABLES && vars2 != Binding.NO_TYPE_VARIABLES) {
//							// type variables are different so we can distinguish between methods
//							continue nextSibling;
//						} else if (pLength > 0) {
//							// check to see if the erasure of either method is equal to the other
//							int index = pLength;
//							for (; --index >= 0;) {
//								if (params1[index] != params2[index].erasure())
//									break;
//								if (params1[index] == params2[index]) {
//									TypeBinding type = params1[index].leafComponentType();
//									if (type instanceof SourceTypeBinding && type.typeVariables() != Binding.NO_TYPE_VARIABLES) {
//										index = pLength; // handle comparing identical source types like X<T>... its erasure is itself BUT we need to answer false
//										break;
//									}
//								}
//							}
//							if (index >= 0 && index < pLength) {
//								for (index = pLength; --index >= 0;)
//									if (params1[index].erasure() != params2[index])
//										break;
//							}
//							if (index >= 0)
//								continue nextSibling;
//						}
//					} else if (!method.areParametersEqual(method2)) { // prior to 1.5, parameter identity meant a collision case
//						continue nextSibling;
//					}
////					boolean isEnumSpecialMethod = isEnum() && (CharOperation.equals(selector,TypeConstants.VALUEOF) || CharOperation.equals(selector,TypeConstants.VALUES));
//					// report duplicate
//					if (methodDecl == null) {
//						methodDecl = method.sourceMethod(); // cannot be retrieved after binding is lost & may still be null if method is special
//						if (methodDecl != null && methodDecl.binding != null) { // ensure its a valid user defined method
////							if (isEnumSpecialMethod) {
////								this.scope.problemReporter().duplicateEnumSpecialMethod(this, methodDecl);
////							} else {
//								this.scope.problemReporter().duplicateMethodInType(this, methodDecl);
////							}
//							methodDecl.binding = null;
//							this.methods[i] = null;
//							failed++;
//						}
//					}
//					AbstractMethodDeclaration method2Decl = method2.sourceMethod();
//					if (method2Decl != null && method2Decl.binding != null) { // ensure its a valid user defined method
////						if (isEnumSpecialMethod) {
////							this.scope.problemReporter().duplicateEnumSpecialMethod(this, method2Decl);
////						} else {
//							this.scope.problemReporter().duplicateMethodInType(this, method2Decl);
////						}
//						method2Decl.binding = null;
//						this.methods[j] = null;
//						failed++;
//					}
//				}
////				if (/*method.returnType == null && */ methodDecl == null) { // forget method with invalid return type... was kept to detect possible collisions
////					method.sourceMethod().binding = null;
////					this.methods[i] = null;
////					failed++;
////				}
//			}
//		} finally {
//			if (failed > 0) {
//				int newSize = this.methods.length - failed;
//				if (newSize == 0) {
//					this.methods = Binding.NO_METHODS;
//				} else {
//					FunctionBinding[] newMethods = new FunctionBinding[newSize];
//					for (int i = 0, j = 0, length = this.methods.length; i < length; i++)
//						if (this.methods[i] != null)
//							newMethods[j++] = this.methods[i];
//					this.methods = newMethods;
//				}
//			}
//
//			// handle forward references to potential default abstract methods
////			addDefaultAbstractMethods();
//			this.tagBits |= TagBits.AreMethodsComplete;
//		}
//		return this.methods;
//	}
//
//	private FieldBinding resolveTypeFor(FieldBinding field) {
//		if ((field.modifiers & ExtraCompilerModifiers.AccUnresolved) == 0)
//			return field;
//
////		if (this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
////			if ((field.getAnnotationTagBits() & TagBits.AnnotationDeprecated) != 0)
////				field.modifiers |= ClassFileConstants.AccDeprecated;
////		}
////		if (isViewedAsDeprecated() && !field.isDeprecated())
////			field.modifiers |= ExtraCompilerModifiers.AccDeprecatedImplicitly;
////		if (hasRestrictedAccess())
////			field.modifiers |= ExtraCompilerModifiers.AccRestrictedAccess;
//		FieldDeclaration[] fieldDecls = this.scope.referenceContext.fields;
//		for (int f = 0, length = fieldDecls.length; f < length; f++) {
//			if (fieldDecls[f].binding != field)
//				continue;
//
//				MethodScope initializationScope = field.isStatic()
//					? this.scope.referenceContext.staticInitializerScope
//					: this.scope.referenceContext.initializerScope;
//				FieldBinding previousField = initializationScope.initializedField;
//				try {
//					initializationScope.initializedField = field;
//					FieldDeclaration fieldDecl = fieldDecls[f];
//					TypeBinding fieldType =
//						fieldDecl.getKind() == AbstractVariableDeclaration.ENUM_CONSTANT
//							? initializationScope.environment().convertToRawType(this) // enum constant is implicitly of declaring enum type
//							: fieldDecl.type.resolveType(initializationScope, true /* check bounds*/);
//					field.type = fieldType;
//					field.modifiers &= ~ExtraCompilerModifiers.AccUnresolved;
//					if (fieldType == null) {
//						fieldDecls[f].binding = null;
//						return null;
//					}
//					if (fieldType == TypeBinding.VOID) {
//						this.scope.problemReporter().variableTypeCannotBeVoid(fieldDecls[f]);
//						fieldDecls[f].binding = null;
//						return null;
//					}
//					if (fieldType.isArrayType() && ((ArrayBinding) fieldType).leafComponentType == TypeBinding.VOID) {
//						this.scope.problemReporter().variableTypeCannotBeVoidArray(fieldDecls[f]);
//						fieldDecls[f].binding = null;
//						return null;
//					}
//					TypeBinding leafType = fieldType.leafComponentType();
//					if (leafType instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0) {
//						field.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
//					}
//				} finally {
//				    initializationScope.initializedField = previousField;
//				}
//			return field;
//		}
//		return null; // should never reach this point
//	}
//	private FunctionBinding resolveTypesFor(FunctionBinding method) {
//		if ((method.modifiers & ExtraCompilerModifiers.AccUnresolved) == 0)
//			return method;
//
////		if (this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
////			if ((method.getAnnotationTagBits() & TagBits.AnnotationDeprecated) != 0)
////				method.modifiers |= ClassFileConstants.AccDeprecated;
////		}
////		if (isViewedAsDeprecated() && !method.isDeprecated())
////			method.modifiers |= ExtraCompilerModifiers.AccDeprecatedImplicitly;
////		if (hasRestrictedAccess())
////			method.modifiers |= ExtraCompilerModifiers.AccRestrictedAccess;
//
//		AbstractMethodDeclaration methodDecl = method.sourceMethod();
//		if (methodDecl == null) return null; // method could not be resolved in previous iteration
//
////		TypeParameter[] typeParameters = methodDecl.typeParameters();
////		if (typeParameters != null) {
////			methodDecl.scope.connectTypeVariables(typeParameters, true);
////			// Perform deferred bound checks for type variables (only done after type variable hierarchy is connected)
////			for (int i = 0, paramLength = typeParameters.length; i < paramLength; i++)
////				typeParameters[i].checkBounds(methodDecl.scope);
////		}
////		TypeReference[] exceptionTypes = methodDecl.thrownExceptions;
////		if (exceptionTypes != null) {
////			int size = exceptionTypes.length;
////			method.thrownExceptions = new ReferenceBinding[size];
////			int count = 0;
////			ReferenceBinding resolvedExceptionType;
////			for (int i = 0; i < size; i++) {
////				resolvedExceptionType = (ReferenceBinding) exceptionTypes[i].resolveType(methodDecl.scope, true /* check bounds*/);
////				if (resolvedExceptionType == null)
////					continue;
////				if (resolvedExceptionType.isBoundParameterizedType()) {
////					methodDecl.scope.problemReporter().invalidParameterizedExceptionType(resolvedExceptionType, exceptionTypes[i]);
////					continue;
////				}
////				if (resolvedExceptionType.findSuperTypeErasingTo(TypeIds.T_JavaLangThrowable, true) == null) {
////					methodDecl.scope.problemReporter().cannotThrowType(this, methodDecl, exceptionTypes[i], resolvedExceptionType);
////					continue;
////				}
////			    if ((resolvedExceptionType.modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0)
////					method.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
////				method.thrownExceptions[count++] = resolvedExceptionType;
////			}
////			if (count < size)
////				System.arraycopy(method.thrownExceptions, 0, method.thrownExceptions = new ReferenceBinding[count], 0, count);
////		}
////
//		boolean foundArgProblem = false;
//		Argument[] arguments = methodDecl.arguments;
//		if (arguments != null) {
//			int size = arguments.length;
//			method.parameters = new TypeBinding[size];
//			for (int i = 0; i < size; i++) {
//				Argument arg = arguments[i];
//				if (arg.type==null)
//					continue;
//				TypeBinding parameterType = arg.type.resolveType(methodDecl.scope, true /* check bounds*/);
//				if (parameterType == null) {
//					foundArgProblem = true;
////				}
////				else if (parameterType == TypeBinding.VOID) {
////					methodDecl.scope.problemReporter().argumentTypeCannotBeVoid(this, methodDecl, arg);
////					foundArgProblem = true;
////				} else if (parameterType.isArrayType() && ((ArrayBinding) parameterType).leafComponentType == TypeBinding.VOID) {
////					methodDecl.scope.problemReporter().argumentTypeCannotBeVoidArray(this, methodDecl, arg);
////					foundArgProblem = true;
//				} else {
//					TypeBinding leafType = parameterType.leafComponentType();
//				    if (leafType instanceof ReferenceBinding && (((ReferenceBinding) leafType).modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0)
//						method.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
//					method.parameters[i] = parameterType;
//				}
//			}
//		}
//
//		boolean foundReturnTypeProblem = false;
//		if (!method.isConstructor()) {
//			TypeReference returnType = methodDecl instanceof FunctionDeclaration
//				? ((FunctionDeclaration) methodDecl).returnType
//				: null;
//			if (returnType != null) {
////				methodDecl.scope.problemReporter().missingReturnType(methodDecl);
////				method.returnType = null;
////				foundReturnTypeProblem = true;
////			} else {
//			    TypeBinding methodType = returnType.resolveType(methodDecl.scope, true /* check bounds*/);
//				if (methodType == null) {
//					foundReturnTypeProblem = true;
//				}
////				else if (methodType.isArrayType() && ((ArrayBinding) methodType).leafComponentType == TypeBinding.VOID) {
////					methodDecl.scope.problemReporter().returnTypeCannotBeVoidArray(this, (FunctionDeclaration) methodDecl);
////					foundReturnTypeProblem = true;
////				}
//				else {
//					method.returnType = methodType;
//					TypeBinding leafType = methodType.leafComponentType();
//					if (leafType instanceof ReferenceBinding && (((ReferenceBinding) leafType).modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0)
//						method.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
//				}
//			}
//		}
//		if (foundArgProblem) {
//			methodDecl.binding = null;
//			method.parameters = Binding.NO_PARAMETERS; // see 107004
//			// nullify type parameter bindings as well as they have a backpointer to the method binding
//			// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=81134)
////			if (typeParameters != null)
////				for (int i = 0, length = typeParameters.length; i < length; i++)
////					typeParameters[i].binding = null;
//			return null;
//		}
//		if (foundReturnTypeProblem)
//			return method; // but its still unresolved with a null return type & is still connected to its method declaration
//
//		method.modifiers &= ~ExtraCompilerModifiers.AccUnresolved;
//		return method;
//	}
//
//
//	public void setFields(FieldBinding[] fields) {
//		this.fields = fields;
//	}
//	public void setMethods(FunctionBinding[] methods) {
//		this.methods = methods;
//	}

	public AbstractMethodDeclaration sourceMethod(MethodBinding binding) {
		  ProgramElement[] statements = compilationUnitScope.referenceContext.statements;
		  for (int i = 0; i < statements.length; i++) {
			if (statements[i] instanceof AbstractMethodDeclaration && ((AbstractMethodDeclaration)statements[i]).binding==binding)
				return (AbstractMethodDeclaration)statements[i];
			else if (statements[i] instanceof Assignment && (((Assignment)statements[i]).expression instanceof FunctionExpression)) {
				FunctionExpression functionExpression = (FunctionExpression) ((Assignment)statements[i]).expression;
				if (functionExpression.methodDeclaration !=null && functionExpression.methodDeclaration.binding==binding)
					return functionExpression.methodDeclaration;
			}
		  }

		  class  MethodFinder extends ASTVisitor
		  {
			  MethodBinding binding;
			  MethodDeclaration method;
			  MethodFinder(MethodBinding binding)
			  {this.binding=binding;}
			  
				public boolean visit(MethodDeclaration methodDeclaration, Scope scope) {
					if (methodDeclaration.binding==this.binding)
					{
						method=methodDeclaration;
						return false;
					}
					return true;
				}

				public boolean visit(InferredType inferredType, BlockScope scope) {	// not possible to contain method
					return false;
				}

				public boolean visit(Javadoc javadoc, BlockScope scope) {	// not possible to contain method
					return false;
				}

				public boolean visit(Javadoc javadoc, ClassScope scope) { // not possible to contain method
					return false;
				}

				public boolean visit(PostfixExpression postfixExpression,  // not possible to contain method
						BlockScope scope) {
					return false;
				}

				public boolean visit(PrefixExpression prefixExpression,	// not possible to contain method
						BlockScope scope) {
					return false;
				}

				public boolean visit(ThisReference thisReference,	// not possible to contain method
						BlockScope scope) {
					return false;
				}

				public boolean visit(ThisReference thisReference,	// not possible to contain method
						ClassScope scope) {
					return false;
				}
				
				
				
		  }
		  MethodFinder visitor=new MethodFinder(binding);
		  compilationUnitScope.referenceContext.traverse(visitor, compilationUnitScope,true);
		  return visitor.method;
	}

	public char[] qualifiedSourceName() {
		return CharOperation.concatWith(compoundName, '.');
	}

	public char[] qualifiedPackageName() {
		return this.path;
	}

	public void cleanup()
	{
		super.cleanup();
		if (this.methods!=null)
			for (int i = 0; i < this.methods.length; i++) {
				this.methods[i].cleanup();
			}
		this.compilationUnitScope=null;
	}
}
