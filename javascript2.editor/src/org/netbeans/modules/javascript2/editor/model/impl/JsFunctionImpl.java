/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.model.impl;

import java.util.*;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.javascript2.editor.model.DeclarationScope;
import org.netbeans.modules.javascript2.editor.model.Identifier;
import org.netbeans.modules.javascript2.editor.model.JsElement;
import org.netbeans.modules.javascript2.editor.model.JsFunction;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Petr Pisl
 */
public class JsFunctionImpl extends DeclarationScopeImpl implements JsFunction {

    final private List <? extends Identifier> parameters;
    private boolean isAnonymous;
    
    public JsFunctionImpl(DeclarationScope scope, JsObject parentObject, Identifier name, List<Identifier> parameters, OffsetRange offsetRange) {
        super(scope, parentObject, name, offsetRange);
        this.parameters = parameters; 
        this.isAnonymous = false;
        setDeclared(true);
    }
    
    public static JsFunctionImpl createGlobal(FileObject file) {
        Identifier ident = new IdentifierImpl(file.getName(), new OffsetRange(0, (int)file.getSize()));
        return new JsFunctionImpl(file, ident);
    }
    
    private JsFunctionImpl(FileObject file, Identifier name) {
        this(null, null, name, Collections.EMPTY_LIST, name.getOffsetRange());
        this.setFileObject(file);
    }
    
    @Override
    public Collection<? extends Identifier> getParameters() {
        return new ArrayList(parameters);
    }

    @Override
    public Kind getJSKind() {
        if (getParent() == null) {
            // global function
            return JsElement.Kind.FILE;
        }
        for (JsObject property : getProperties().values()) {
            if (property instanceof JsFunctionImpl
                    || property.getJSKind() == JsElement.Kind.PROPERTY) {
                return JsElement.Kind.CONSTRUCTOR;
            }
        }

        JsElement.Kind result = JsElement.Kind.FUNCTION;

        if (getParent().getJSKind() != JsElement.Kind.FILE) {
            result = JsElement.Kind.METHOD;
        }
        return result;
    }

    @Override
    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    
    
    
}
