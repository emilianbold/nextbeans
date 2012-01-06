/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.web.beans.impl.model;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;


/**
 * @author ads
 *
 */
abstract class AbstractAssignabilityChecker  implements Checker {
    
    enum AssignabilityType {
        PLAIN,      // Assignability for typesafe resolution for bean types by the web beans spec
        EVENT,      // Assignability for observer/event resolution
        DECORATOR   // Assignability  for decorator resolution
    }
    
    static AbstractAssignabilityChecker get( AssignabilityType type) {
        switch ( type ){
            case PLAIN :
                return new AssignabilityChecker();
            case EVENT:
                return new EventAssignabilityChecker();
            case DECORATOR :
                return new DelegateAssignabilityChecker();
            default:
                // no other types are handled
                assert false;
                return null;
        }
    }
    
    void init( ReferenceType varType, ReferenceType checkedType, 
            Element originalElement, WebBeansModelImplementation impl)
    {
        myVarType = varType;
        myType = checkedType;
        myImpl = impl;
        myOriginalElement = originalElement;
    }
    
    void init( ReferenceType varType, ReferenceType checkedType, 
            WebBeansModelImplementation impl)
    {
        init( varType , checkedType , null, impl );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.beans.impl.model.Checker#check()
     */
    @Override
    public boolean check(){
        boolean check = checkAssignability( getVarType(), getType(), 
                myOriginalElement);
        return check;
    }
    
    public boolean checkAssignability( ReferenceType variableType , 
            ReferenceType refType , Element originalElement ) 
    {
        boolean isDeclaredType = variableType instanceof DeclaredType && 
            variableType.getKind()!=TypeKind.ERROR ;
        if ( !isDeclaredType ){
            return checkParameter(refType, variableType);
        }
        Element variableElement = ((DeclaredType)variableType).asElement();
        if ( !( variableElement instanceof TypeElement ) ){
            return false;
        }
        
        if ( !( refType instanceof DeclaredType ) || 
                refType.getKind() == TypeKind.ERROR)
        {
            return false;
        }
        DeclaredType type = (DeclaredType)refType;

        Element refElement = getImplementation().getHelper().
            getCompilationController().getTypes().asElement( type );
        if ( !( refElement instanceof TypeElement ) ){
            return false;
        }

        if ( !hasBeanType( originalElement, variableType) ){
            return false;
        }
        
        /*
         * Find ancestor of refType with the same raw type.
         * Raw types should be identical for parameterized type by the spec.
         * It means that inheritance by parameterized types are not allowed.  
         */
        Types types = getImplementation().getHelper().getCompilationController().getTypes();
        if ( !types.isSameType( types.erasure( variableType ) , types.erasure(type)) ){
            TypeMirror ancestor = getAncestor(type ,types.erasure( variableType ) , 
                    types );
            // no appropriate type 
            if ( !(ancestor instanceof DeclaredType)){
                return false;
            }
            type = (DeclaredType) ancestor;
        }

        List<? extends TypeMirror> typeArguments = type.getTypeArguments();
        
        TypeElement objectElement = getImplementation().getHelper().
                getCompilationController().getElements().getTypeElement(
                        Object.class.getCanonicalName());
        
        List<? extends TypeMirror> varTypeArguments = ((DeclaredType)variableType).
            getTypeArguments();
        if ( varTypeArguments.size() == 0 || types.isSameType( variableType,
                types.erasure( variableType )  ))
        /*
         *  I'm not sure how to detect variable declaration with generic type:
         *  - it is unclear how many arguments has such type as result
         *  - probably such type could have typevar argument ( the same as generic
         *  declaration ). In the letter case this type mirror should be the same
         *  as generic declaration type mirror. So I put here comparison with 
         *  type after erasure.             
         */
        {
            return handleRequiredRawType(types, typeArguments, objectElement);
        }
        if ( typeArguments.size() == 0 || types.isSameType( type,
                types.erasure( type )  ))
        {
            return handleBeanRawType(types, varTypeArguments, objectElement);
        }
        if ( varTypeArguments.size() != typeArguments.size() ){
            /*
             *  This should not happen because raw types are checked before.
             *  So generic type is the same. As consequence size of parameters
             *  and arguments should be the same.
             */
            return false;
        }
        for ( int i=0; i< varTypeArguments.size() ; i++ ){
            TypeMirror argType = typeArguments.get(i);
            if ( !checkParameter( argType , varTypeArguments.get(i)  ) ){
                return false;
            }
        }
        return true;
    }

    protected abstract boolean handleBeanRawType( Types types,
            List<? extends TypeMirror> varTypeArguments, TypeElement objectElement );

    protected abstract boolean hasBeanType(Element element,ReferenceType variableType);

    protected abstract boolean handleRequiredRawType( Types types,
            List<? extends TypeMirror> typeArguments, TypeElement objectElement );
    
    public boolean checkAssignability( ReferenceType variableType , 
            ReferenceType refType ) 
    {
        return checkAssignability(variableType, refType, null);
    }
    
    protected boolean checkParameter( TypeMirror argType, TypeMirror varTypeArg )
    {
        if ( argType == null || varTypeArg == null ){
            return false;
        }
        Types types = getImplementation().getHelper().getCompilationController().
            getTypes();

        /*
         * Implementation of spec item :
         * the required type parameter and the bean type parameter are actual 
         * types with identical raw type, and, if the type is
         * parameterized, the bean type parameter is assignable to the required 
         * type parameter according to these rules
         */
        if ( argType.getKind()!= TypeKind.TYPEVAR && 
                varTypeArg.getKind()!= TypeKind.TYPEVAR &&
                (argType instanceof ReferenceType) && 
                (varTypeArg instanceof ReferenceType) )
        {
            return checkIsAssignable(getImplementation().getHelper().
                    getCompilationController().getTypes(), argType, varTypeArg);
        }
        
        if ( varTypeArg.getKind() == TypeKind.WILDCARD  ){
            return handleWildCard(argType, (WildcardType)varTypeArg, types);
        }
        
        if ( argType.getKind() == TypeKind.TYPEVAR &&
                varTypeArg.getKind() == TypeKind.TYPEVAR)
        {
            return handleBothTypeVars(argType, varTypeArg, types);
        }
        
        if (varTypeArg.getKind() != TypeKind.TYPEVAR
                && argType.getKind() == TypeKind.TYPEVAR)
        {
            return handleBeanTypeVar(argType, varTypeArg, types);
        }
        if (argType.getKind() != TypeKind.TYPEVAR
                && varTypeArg.getKind() == TypeKind.TYPEVAR)
        {
            return handleRequiredTypeVar(argType, varTypeArg, types);
        }
        
        return false;
    }

    protected abstract boolean handleRequiredTypeVar( TypeMirror argType,
            TypeMirror varTypeArg, Types types );

    protected abstract boolean handleBeanTypeVar( TypeMirror argType, TypeMirror varTypeArg,
            Types types );

    protected abstract boolean handleBothTypeVars( TypeMirror argType,
            TypeMirror varTypeArg, Types types );

    protected boolean handleWildCard( TypeMirror argType, WildcardType varTypeArg,
            Types types )
    {
        TypeMirror upperBound = varTypeArg.getExtendsBound();
        TypeMirror lowerBound = varTypeArg.getSuperBound();

        if ( argType instanceof ReferenceType && 
                argType.getKind()!=TypeKind.TYPEVAR)
        {
            return handleWildCardActualType(argType, types, upperBound,
                    lowerBound);
        }            
        
        if ( argType.getKind() == TypeKind.TYPEVAR ){
            return handleWildCardTypeVar(argType, types, upperBound, lowerBound);
        }
        
        return false;
    }
    
    protected boolean handleWildCardActualType( TypeMirror argType, Types types,
            TypeMirror upperBound, TypeMirror lowerBound ){
        /*
         * Implementation of spec item : the required type parameter is
         * a wildcard, the bean type parameter is an actual type and the
         * actual type is assignable to the upper bound, if any, of the
         * wildcard and assignable from the lower bound, if any, of the
         * wildcard
         */
        if ( upperBound == null || upperBound.getKind() == TypeKind.NULL){
            if ( lowerBound == null || lowerBound.getKind() == TypeKind.NULL){
                return true;
            }
            else {
                return checkIsAssignable(types, lowerBound, argType);
            }
        }
        else {
            if ( lowerBound == null || lowerBound.getKind() == TypeKind.NULL){
                return checkIsAssignable(types, argType, upperBound);
            }
            else {
                return checkIsAssignable(types, argType, upperBound) &&
                    checkIsAssignable(types, lowerBound, argType);
            }
        }
    }

    protected abstract boolean handleWildCardTypeVar( TypeMirror argType, Types types,
            TypeMirror upperBound, TypeMirror lowerBound );


    protected boolean checkIsAssignable( Types types, TypeMirror from,
            TypeMirror to )
    {
        if ( isAssignable(from, to, types)){
            return true;
        }
        else if( to instanceof ReferenceType  && from instanceof ReferenceType )
        {
            return checkAssignability( (ReferenceType)to, 
                    (ReferenceType)from);
        }
        else {
            return false;
        }
    }
    
    protected boolean isAssignable( TypeMirror from, TypeMirror to, Types types )
    {
        Element element = types.asElement(to);
        boolean isGeneric = (element instanceof TypeElement)
                && ((TypeElement) element).getTypeParameters().size() != 0;
        return  !isGeneric && ( to instanceof DeclaredType );
    }
    
    private TypeMirror getAncestor( TypeMirror subject , TypeMirror rawType ,
            Types types)
    {
        List<? extends TypeMirror> directSupertypes = types.directSupertypes(subject);
        for (TypeMirror typeMirror : directSupertypes) {
            if ( types.isSameType(types.erasure( typeMirror), rawType)){
                return typeMirror;
            }
            TypeMirror found = getAncestor(typeMirror, rawType, types);
            if ( found != null ){
                return found;
            }
        }
        return null;
    }
    
    protected ReferenceType getVarType(){
        return myVarType;
    }
    
    protected WebBeansModelImplementation getImplementation(){
        return myImpl;
    }
    
    protected ReferenceType getType(){
        return myType;
    }
    
    private Element myOriginalElement;
    private ReferenceType myVarType;
    private WebBeansModelImplementation myImpl;
    private ReferenceType myType;
}
