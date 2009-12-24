/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints.jackpot.file.conditionapi;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import org.netbeans.api.annotations.common.NonNull;

/**
 *
 * @author lahvac
 */
public final class DefaultRuleUtilities {

    private final Context context;
    private final Matcher matcher;

    DefaultRuleUtilities(Context context, Matcher matcher) {
        this.context = context;
        this.matcher = matcher;
    }
    
    public boolean referencedIn(Variable variable, Variable in) {
        return matcher.referencedIn(variable, in);
    }

    public boolean sourceVersionGE(SourceVersion source) {
        return context.sourceVersion().compareTo(source) >= 0;
    }

    public boolean hasModifier(Variable variable, Modifier modifier) {
        return context.modifiers(variable).contains(modifier);
    }

    public boolean parentMatches(String pattern) {
        Variable parent = context.parent(context.variableForName("$_"));
        
        if (parent == null) {
            return false;
        }
        
        return matcher.matchesAny(parent, pattern); //XXX: $_ currently not part of variables map, so this won't work!!!
    }

    public boolean elementKindMatches(Variable variable, ElementKind kind) {
        return kind == context.elementKind(variable);
    }

    public boolean isNullLiteral(@NonNull Variable var) {
        return context.isNullLiteral(var);
    }
    
    public boolean matches(String pattern) {
        Variable current = context.variableForName("$_");

        assert current != null;

        return matchesAny(current, pattern); //XXX: $_ currently not part of variables map, so this won't work!!!
    }

    public boolean matchesAny(Variable var, String... patterns) {
        return matcher.matchesAny(var, patterns);
    }

    public boolean containsAny(Variable var, String... patterns) {
        return matcher.containsAny(var, patterns);
    }
}
