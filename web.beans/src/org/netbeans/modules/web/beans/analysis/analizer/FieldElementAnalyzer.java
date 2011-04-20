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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.web.beans.analysis.analizer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.web.beans.analysis.analyzer.field.DelegateFieldAnalizer;
import org.netbeans.modules.web.beans.analysis.analyzer.field.InjectionPointAnalyzer;
import org.netbeans.modules.web.beans.analysis.analyzer.field.ScopedFieldAnalyzer;
import org.netbeans.modules.web.beans.analysis.analyzer.field.TypedFieldAnalyzer;
import org.netbeans.spi.editor.hints.ErrorDescription;


/**
 * @author ads
 *
 */
public class FieldElementAnalyzer implements ElementAnalyzer {

    /* (non-Javadoc)
     * @see org.netbeans.modules.web.beans.analysis.analizer.ElementAnalyzer#analyze(javax.lang.model.element.Element, javax.lang.model.element.TypeElement, org.netbeans.api.java.source.CompilationInfo, java.util.List, java.util.concurrent.atomic.AtomicBoolean)
     */
    @Override
    public void analyze( Element element, TypeElement parent,
            CompilationInfo compInfo, List<ErrorDescription> descriptions, 
            AtomicBoolean cancel )
    {
        VariableElement var = (VariableElement) element;
        TypeMirror varType = compInfo.getTypes().asMemberOf( 
                (DeclaredType)parent.asType(),  var );
        for (FieldAnalyzer analyzer : ANALIZERS) {
            analyzer.analyze(var, varType, parent, compInfo, descriptions);
        }
    }
    
    public interface FieldAnalyzer {
        void analyze( VariableElement element , TypeMirror elementType,
                TypeElement parent, CompilationInfo compInfo,
                List<ErrorDescription> descriptions);
    }
    
    private static final List<FieldAnalyzer> ANALIZERS= new LinkedList<FieldAnalyzer>(); 
    
    static {
        ANALIZERS.add( new TypedFieldAnalyzer() );
        ANALIZERS.add( new ScopedFieldAnalyzer() );
        ANALIZERS.add( new InjectionPointAnalyzer());
        ANALIZERS.add( new DelegateFieldAnalizer());
    }
}
