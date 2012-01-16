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

package org.netbeans.modules.j2ee.ejbjarproject.ui.customizer;

import javax.swing.JPanel;
import org.openide.util.HelpCtx;

/** Customizer for general project attributes.
 *
 * @author  phrebejk
 */
public class CustomizerJar extends JPanel implements HelpCtx.Provider {
    private static final long serialVersionUID = 19686368769140701L;
    
    public CustomizerJar( EjbJarProjectProperties uiProperties ) {
        initComponents();
        
        jTextFieldDistDir.setDocument( uiProperties.DIST_JAR_MODEL );
        jTextFieldExcludes.setDocument( uiProperties.BUILD_CLASSES_EXCLUDES_MODEL );

        uiProperties.JAR_COMPRESS_MODEL.setMnemonic( jCheckBoxCommpress.getMnemonic() );
        jCheckBoxCommpress.setModel( uiProperties.JAR_COMPRESS_MODEL ); 
        uiProperties.INCLUDE_JARS_MODEL.setMnemonic( includeJarsCheckBox.getMnemonic() );
        includeJarsCheckBox.setModel(uiProperties.INCLUDE_JARS_MODEL);
    } 
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx( CustomizerJar.class );
    }
        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelDistDir = new javax.swing.JLabel();
        jTextFieldDistDir = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldExcludes = new javax.swing.JTextField();
        jCheckBoxCommpress = new javax.swing.JCheckBox();
        excludeMessage = new javax.swing.JLabel();
        includeJarsCheckBox = new javax.swing.JCheckBox();

        jLabelDistDir.setLabelFor(jTextFieldDistDir);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelDistDir, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_DistDir_JTextField")); // NOI18N

        jTextFieldDistDir.setEditable(false);

        jLabel2.setLabelFor(jTextFieldExcludes);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_Excludes_JTextField")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCommpress, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_Commpres_JCheckBox")); // NOI18N

        excludeMessage.setLabelFor(jTextFieldExcludes);
        org.openide.awt.Mnemonics.setLocalizedText(excludeMessage, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_ExcludeMessage_JLabel")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(includeJarsCheckBox, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_Package_Required")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabelDistDir))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(excludeMessage)
                            .addComponent(jTextFieldExcludes, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .addComponent(jTextFieldDistDir, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
                    .addComponent(jCheckBoxCommpress)
                    .addComponent(includeJarsCheckBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDistDir)
                    .addComponent(jTextFieldDistDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldExcludes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(excludeMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxCommpress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(includeJarsCheckBox)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        jTextFieldDistDir.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJar.class).getString("AD_jTextFieldDistDir")); // NOI18N
        jTextFieldExcludes.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJar.class).getString("AD_jTextFieldExcludes")); // NOI18N
        jCheckBoxCommpress.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJar.class).getString("AD_jCheckBoxCompress")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel excludeMessage;
    private javax.swing.JCheckBox includeJarsCheckBox;
    private javax.swing.JCheckBox jCheckBoxCommpress;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelDistDir;
    private javax.swing.JTextField jTextFieldDistDir;
    private javax.swing.JTextField jTextFieldExcludes;
    // End of variables declaration//GEN-END:variables
                
}
