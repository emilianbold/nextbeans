/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.subversion.remote.client;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import org.netbeans.modules.subversion.remote.util.SvnUtils;

/**
 * @author Tomas Stupka
 * @author Jan Stola
 */
public class MissingClientPanel extends javax.swing.JPanel {

    /** Creates new form MissingClientPanel */
    public MissingClientPanel() {
        initComponents();
        tipLabel.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel1.unix.text")); // NOI18N
        String text = org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.text"); // NOI18N
        textPane.setText(text);
        Document doc = textPane.getDocument();
        if (doc instanceof HTMLDocument) { // Issue 185505
            HTMLDocument htmlDoc = (HTMLDocument)doc;
            Font font = UIManager.getFont("Label.font"); // NOI18N
            String bodyRule = "body { font-family: " + font.getFamily() + "; " // NOI18N
                + "color: " + SvnUtils.getColorString(textPane.getForeground()) + "; " //NOI18N
                + "font-size: " + font.getSize() + "pt; }"; // NOI18N
            htmlDoc.getStyleSheet().addRule(bodyRule);
        }
        textPane.setOpaque(false);
        textPane.setBackground(new Color(0,0,0,0)); // windows and nimbus workaround see issue 145826
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tipLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel2.text_1")); // NOI18N

        textPane.setEditable(false);
        textPane.setBackground(jLabel1.getBackground());
        textPane.setBorder(null);
        textPane.setContentType(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.contentType")); // NOI18N
        textPane.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.browseButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(tipLabel, org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel1.unix.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.cliRadioButton.text")); // NOI18N
        jLabel2.setToolTipText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.cliRadioButton.AccessibleContext.accessibleDescription")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(executablePathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(tipLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(executablePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipLabel)
                .addContainerGap(114, Short.MAX_VALUE))
        );

        textPane.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.AccessibleContext.accessibleName")); // NOI18N
        textPane.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton browseButton = new javax.swing.JButton();
    private javax.swing.ButtonGroup buttonGroup1;
    final javax.swing.JTextField executablePathTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    final javax.swing.JTextPane textPane = new javax.swing.JTextPane();
    private javax.swing.JLabel tipLabel;
    // End of variables declaration//GEN-END:variables

}
