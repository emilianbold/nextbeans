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
package org.netbeans.modules.php.nette2.ui.wizards;

import java.awt.Cursor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.php.nette2.options.Nette2Options;
import org.netbeans.modules.php.nette2.ui.options.Nette2OptionsPanelController;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class NewNette2ProjectPanel extends javax.swing.JPanel implements ChangeListener {
    private ChangeSupport changeSupport = new ChangeSupport(this);

    public NewNette2ProjectPanel() {
        initComponents();
    }

    public String getErrorMessage() {
        String sandboxMessage = Nette2OptionsPanelController.validateSandbox(Nette2Options.getInstance().getSandbox());
        if (sandboxMessage != null) {
            return sandboxMessage;
        }
        if (copyNetteCheckBox.isSelected()) {
            return Nette2OptionsPanelController.validateNetteDirectory(Nette2Options.getInstance().getNetteDirectory());
        }
        return null;
    }

    public boolean isCopyNetteCheckboxSelected() {
        return copyNetteCheckBox.isSelected();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    @Override
    public void addNotify() {
        Nette2Options.getInstance().addChangeListener(this);
        super.addNotify();
    }

    @Override
    public void removeNotify() {
        Nette2Options.getInstance().removeChangeListener(this);
        super.removeNotify();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionsLabel = new javax.swing.JLabel();
        copyNetteCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(optionsLabel, org.openide.util.NbBundle.getMessage(NewNette2ProjectPanel.class, "NewNette2ProjectPanel.optionsLabel.text")); // NOI18N
        optionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                optionsLabelMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                optionsLabelMouseReleased(evt);
            }
        });

        copyNetteCheckBox.setMnemonic('C');
        org.openide.awt.Mnemonics.setLocalizedText(copyNetteCheckBox, org.openide.util.NbBundle.getMessage(NewNette2ProjectPanel.class, "NewNette2ProjectPanel.copyNetteCheckBox.text")); // NOI18N
        copyNetteCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyNetteCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(optionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(copyNetteCheckBox)
                .addGap(0, 52, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(optionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(copyNetteCheckBox)
                .addContainerGap(55, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void optionsLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsLabelMouseReleased
        OptionsDisplayer.getDefault().open(Nette2OptionsPanelController.getOptionsPath());
    }//GEN-LAST:event_optionsLabelMouseReleased

    private void optionsLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_optionsLabelMouseEntered

    private void copyNetteCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyNetteCheckBoxActionPerformed
        changeSupport.fireChange();
    }//GEN-LAST:event_copyNetteCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox copyNetteCheckBox;
    private javax.swing.JLabel optionsLabel;
    // End of variables declaration//GEN-END:variables

}
