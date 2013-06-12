/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.html.knockout;

import java.awt.Color;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.netbeans.modules.html.editor.api.completion.HtmlCompletionItem;
import org.netbeans.modules.html.editor.api.gsf.CustomAttribute;
import org.netbeans.modules.html.knockout.model.Binding;

/**
 *
 * @author marekfukala
 */
public class KOAttributeCompletionItem extends HtmlCompletionItem.Attribute {

    private static final String DOC_URL = "http://knockoutjs.com/documentation/binding-syntax.html"; //NOI18N
    
    private static WeakReference<String> HELP_CACHE = new WeakReference<>(null);
    
    private static final String CANNOT_LOAD_HELP = Bundle.cannot_load_help();
    
    private final boolean isInKnockoutFile;
    
    public KOAttributeCompletionItem(CustomAttribute ca, int offset, boolean isInKnockoutFile) {
        super(ca.getName(), offset, false, ca.getHelp());
        this.isInKnockoutFile = isInKnockoutFile;
    }

    @Override
    protected ImageIcon getIcon() {
        return null;
    }

    @Override
    protected Color getAttributeColor() {
        return isInKnockoutFile ? KOUtils.KO_COLOR : super.getAttributeColor();
    }

//    //move the ko results to the top of the completion list if the page
//    //already contains ko stuff
//    @Override
//    public int getSortPriority() {
//        return super.getSortPriority() - (isInKnockoutFile ? 10 : 0);
//    }

    @Override
    public boolean hasHelp() {
        return isInKnockoutFile; //do not show KO's help  for generic data-bind attribute outside KO app
    }
    
       @Override
    public URL getHelpURL() {
        try {
            return new URL(DOC_URL);
        } catch (MalformedURLException ex) {
            Logger.getLogger(KOBindingCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getHelp() {
        String helpContent = HELP_CACHE.get();
        if(helpContent != null) {
            return helpContent;
        }
        try {
            URL url = getHelpURL();
            if(url == null) {
                return CANNOT_LOAD_HELP;
            } else {
                helpContent = HelpSupport.getKnockoutDocumentationContent(HelpSupport.loadURLContent(url, Binding.DOC_CHARSET));
                HELP_CACHE = new WeakReference<>(helpContent);
                return helpContent;
            }
        } catch (IOException ex) {
            Logger.getLogger(KOBindingCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return CANNOT_LOAD_HELP;
    }
    
}
