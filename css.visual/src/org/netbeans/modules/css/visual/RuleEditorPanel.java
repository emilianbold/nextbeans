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
package org.netbeans.modules.css.visual;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.JPanel;
import org.netbeans.modules.css.model.api.Model;
import org.netbeans.modules.css.model.api.Rule;
import org.netbeans.modules.css.visual.RuleNode;
import org.netbeans.modules.css.visual.api.RuleEditorListener;
import org.netbeans.modules.css.visual.api.RuleEditorController;
import org.netbeans.modules.css.visual.api.ViewMode;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;

/**
 * Rule editor panel is a {@link JPanel} component which can be embedded in 
 * the client's UI.
 * 
 * It can be controlled and observed via {@link RuleEditorPanelController} 
 * and {@link RuleEditorListener}.
 *
 * @author marekfukala
 */
public class RuleEditorPanel extends JPanel {

    private PropertySheet sheet;
    
    private Model model;
    private Rule rule;
    
    private Collection<RuleEditorListener> LISTENERS
            = Collections.synchronizedCollection(new ArrayList<RuleEditorListener>());
    
    /**
     * Creates new form RuleEditorPanel
     */
    public RuleEditorPanel() {
        initComponents();

        sheet = new PropertySheet();
        add(sheet, BorderLayout.CENTER);
    }
    
    public void setModel(Model model) {
        this.model = model;
        this.rule = null;
    }
    
    public void setRule(Rule rule) {
        this.rule = rule;
        sheet.setNodes(new Node[]{new RuleNode(model, rule)});
    }
    
    public void setNoRuleState() {
        sheet.setNodes(null);
        //TODO - show some 'no rule selected' message
    }
    
    public void setViewMode(ViewMode mode) {
        //TODO
    }
    
    /**
     * Registers an instance of {@link RuleEditorListener} to the component.
     * @param listener
     * @return true if the listeners list changed
     */
    public boolean addRuleEditorListener(RuleEditorListener listener) {
        return LISTENERS.add(listener);
    }
    /**
     * Unregisters an instance of {@link RuleEditorListener} from the component.
     * @param listener
     * @return true if the listeners list changed (listener removed)
     */
    public boolean removeRuleEditorListener(RuleEditorListener listener) {
        return LISTENERS.remove(listener);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
