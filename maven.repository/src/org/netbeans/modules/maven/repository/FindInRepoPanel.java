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

package org.netbeans.modules.maven.repository;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.maven.indexer.api.QueryField;

/**
 *
 * @author  mkleint
 */
public class FindInRepoPanel extends javax.swing.JPanel {

    /** Creates new form FindInRepoPanel */
    public FindInRepoPanel() {
        initComponents();
    }
    
    List<QueryField> getQuery() {
        List<QueryField> fq = new ArrayList<QueryField>();
        String q = txtFind.getText().trim();
        String[]  splits = q.split(" ");
        List<String> fields = new ArrayList<String>();
        fields.add(QueryField.FIELD_GROUPID);
        fields.add(QueryField.FIELD_ARTIFACTID);
        fields.add(QueryField.FIELD_VERSION);
        if (cbName.isSelected()) {
            fields.add(QueryField.FIELD_NAME);
        }
        if (cbDescription.isSelected()) {
            fields.add(QueryField.FIELD_DESCRIPTION);
        }
        if (cbClasses.isSelected()) {
            fields.add(QueryField.FIELD_CLASSES);
        }
        for (String one : splits) {
            for (String fld : fields) {
                QueryField f = new QueryField();
                f.setField(fld);
                f.setValue(one);
                fq.add(f);
            }
        }
        return fq;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFind = new javax.swing.JLabel();
        txtFind = new javax.swing.JTextField();
        pnlIncludes = new javax.swing.JPanel();
        cbName = new javax.swing.JCheckBox();
        cbDescription = new javax.swing.JCheckBox();
        cbClasses = new javax.swing.JCheckBox();

        lblFind.setText(org.openide.util.NbBundle.getMessage(FindInRepoPanel.class, "FindInRepoPanel.lblFind.text")); // NOI18N

        pnlIncludes.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FindInRepoPanel.class, "FindInRepoPanel.pnlIncludes.border.title"))); // NOI18N

        cbName.setText(org.openide.util.NbBundle.getMessage(FindInRepoPanel.class, "FindInRepoPanel.cbName.text")); // NOI18N

        cbDescription.setText(org.openide.util.NbBundle.getMessage(FindInRepoPanel.class, "FindInRepoPanel.cbDescription.text")); // NOI18N

        cbClasses.setText(org.openide.util.NbBundle.getMessage(FindInRepoPanel.class, "FindInRepoPanel.cbClasses.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlIncludesLayout = new org.jdesktop.layout.GroupLayout(pnlIncludes);
        pnlIncludes.setLayout(pnlIncludesLayout);
        pnlIncludesLayout.setHorizontalGroup(
            pnlIncludesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIncludesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlIncludesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbName)
                    .add(cbDescription)
                    .add(cbClasses))
                .addContainerGap(273, Short.MAX_VALUE))
        );
        pnlIncludesLayout.setVerticalGroup(
            pnlIncludesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlIncludesLayout.createSequentialGroup()
                .addContainerGap()
                .add(cbName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDescription)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbClasses)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlIncludes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(lblFind)
                        .add(18, 18, 18)
                        .add(txtFind, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblFind)
                    .add(txtFind, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(pnlIncludes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbClasses;
    private javax.swing.JCheckBox cbDescription;
    private javax.swing.JCheckBox cbName;
    private javax.swing.JLabel lblFind;
    private javax.swing.JPanel pnlIncludes;
    private javax.swing.JTextField txtFind;
    // End of variables declaration//GEN-END:variables

}

