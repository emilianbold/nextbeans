// <editor-fold defaultstate="collapsed" desc=" License Header ">
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
//</editor-fold>

package org.netbeans.modules.glassfish.common.ui;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import org.openide.util.NbBundle;

public class JdbcResourceCustomizer extends BasePanel {

    /** Creates new form JdbcResourceCustomizer */
    public JdbcResourceCustomizer() {
        initComponents();
        String val = NbBundle.getMessage(JdbcResourceCustomizer.class, "MSG_FETCHING_DATA");
        poolNameCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { val }));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        poolNameLabel = new javax.swing.JLabel();
        resourceEnabledCB = new javax.swing.JCheckBox();
        poolNameCombo = new javax.swing.JComboBox();

        poolNameLabel.setLabelFor(poolNameCombo);
        org.openide.awt.Mnemonics.setLocalizedText(poolNameLabel, org.openide.util.NbBundle.getMessage(JdbcResourceCustomizer.class, "JdbcResourceCustomizer.poolNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(resourceEnabledCB, org.openide.util.NbBundle.getMessage(JdbcResourceCustomizer.class, "JdbcResourceCustomizer.enabled.text")); // NOI18N
        resourceEnabledCB.setName("enabled"); // NOI18N

        poolNameCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "fetching data...." }));
        poolNameCombo.setActionCommand("resources\\.jdbc-connection-pool\\..*\\.name"); // NOI18N
        poolNameCombo.setName("pool-name"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(resourceEnabledCB)
                    .add(layout.createSequentialGroup()
                        .add(poolNameLabel)
                        .add(2, 2, 2)
                        .add(poolNameCombo, 0, 260, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(poolNameLabel)
                    .add(poolNameCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(resourceEnabledCB)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox poolNameCombo;
    private javax.swing.JLabel poolNameLabel;
    private javax.swing.JCheckBox resourceEnabledCB;
    // End of variables declaration//GEN-END:variables

    protected String getPrefix() {
        return "resources.jdbc-resource."; // NOI18N
    }

    protected List<Component> getDataComponents() {
        return Arrays.asList(new Component[] {poolNameCombo,resourceEnabledCB});
    }

}
