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

package org.netbeans.modules.java.j2seproject.ui.customizer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import org.openide.util.HelpCtx;

/** Customizer for general project attributes.
 *
 * @author  phrebejk
 */
public class CustomizerJavadoc extends JPanel implements HelpCtx.Provider {

    private J2SEProjectProperties j2seProperties = null;

    public CustomizerJavadoc( J2SEProjectProperties uiProperties ) {

        initComponents();

        uiProperties.JAVADOC_PRIVATE_MODEL.setMnemonic( jCheckBoxPrivate.getMnemonic() ); 
        jCheckBoxPrivate.setModel( uiProperties.JAVADOC_PRIVATE_MODEL );
        
        uiProperties.JAVADOC_NO_TREE_MODEL.setMnemonic( jCheckBoxTree.getMnemonic() );
        jCheckBoxTree.setModel( uiProperties.JAVADOC_NO_TREE_MODEL );
        
        uiProperties.JAVADOC_USE_MODEL.setMnemonic( jCheckBoxUsages.getMnemonic() );
        jCheckBoxUsages.setModel( uiProperties.JAVADOC_USE_MODEL );
        
        uiProperties.JAVADOC_NO_NAVBAR_MODEL.setMnemonic( jCheckBoxNavigation.getMnemonic() ); 
        jCheckBoxNavigation.setModel( uiProperties.JAVADOC_NO_NAVBAR_MODEL ); 
        
        uiProperties.JAVADOC_NO_INDEX_MODEL.setMnemonic( jCheckBoxIndex.getMnemonic() );
        jCheckBoxIndex.setModel( uiProperties.JAVADOC_NO_INDEX_MODEL ); 
        
        uiProperties.JAVADOC_SPLIT_INDEX_MODEL.setMnemonic( jCheckBoxSplitIndex.getMnemonic() );
        jCheckBoxSplitIndex.setModel( uiProperties.JAVADOC_SPLIT_INDEX_MODEL ); 
        
        uiProperties.JAVADOC_AUTHOR_MODEL.setMnemonic( jCheckBoxAuthor.getMnemonic() );
        jCheckBoxAuthor.setModel( uiProperties.JAVADOC_AUTHOR_MODEL ); 
        
        uiProperties.JAVADOC_VERSION_MODEL.setMnemonic( jCheckBoxVersion.getMnemonic() );
        jCheckBoxVersion.setModel( uiProperties.JAVADOC_VERSION_MODEL );
        
        jTextFieldWinTitle.setDocument( uiProperties.JAVADOC_WINDOW_TITLE_MODEL );
        
        uiProperties.JAVADOC_PREVIEW_MODEL.setMnemonic( jCheckBoxPreview.getMnemonic() );
        jCheckBoxPreview.setModel( uiProperties.JAVADOC_PREVIEW_MODEL ); 
        
        jTextFieldAddOptions.setDocument( uiProperties.JAVADOC_ADDITIONALPARAM_MODEL );
        
        reenableSplitIndex( null );
        
        // XXX Temporarily removing some controls
        remove( jLabelPackage );
        remove( jTextFieldPackage );
        remove( jButtonPackage );       
        remove( jCheckBoxSubpackages );
        jPanel1.remove( jLabelEncoding );
        jPanel1.remove( jTextFieldEncoding );
        
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx( CustomizerJavadoc.class );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelPackage = new javax.swing.JLabel();
        jTextFieldPackage = new javax.swing.JTextField();
        jButtonPackage = new javax.swing.JButton();
        jCheckBoxSubpackages = new javax.swing.JCheckBox();
        jCheckBoxPrivate = new javax.swing.JCheckBox();
        jLabelGenerate = new javax.swing.JLabel();
        jCheckBoxTree = new javax.swing.JCheckBox();
        jCheckBoxUsages = new javax.swing.JCheckBox();
        jCheckBoxNavigation = new javax.swing.JCheckBox();
        jCheckBoxIndex = new javax.swing.JCheckBox();
        jCheckBoxSplitIndex = new javax.swing.JCheckBox();
        jLabelTags = new javax.swing.JLabel();
        jCheckBoxAuthor = new javax.swing.JCheckBox();
        jCheckBoxVersion = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabelWinTitle = new javax.swing.JLabel();
        jTextFieldWinTitle = new javax.swing.JTextField();
        jLabelEncoding = new javax.swing.JLabel();
        jTextFieldEncoding = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabelAddOptions = new javax.swing.JLabel();
        jTextFieldAddOptions = new javax.swing.JTextField();
        jLabelAddOptionsInfo = new javax.swing.JLabel();
        jCheckBoxPreview = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        jLabelPackage.setLabelFor(jTextFieldPackage);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPackage, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Package_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        add(jLabelPackage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        add(jTextFieldPackage, gridBagConstraints);
        jTextFieldPackage.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jTextFieldPackage"));

        org.openide.awt.Mnemonics.setLocalizedText(jButtonPackage, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Package_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jButtonPackage, gridBagConstraints);
        jButtonPackage.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jButtonPackage"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxSubpackages, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Subpackages_JCheckBox"));
        jCheckBoxSubpackages.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jCheckBoxSubpackages, gridBagConstraints);
        jCheckBoxSubpackages.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxSubpackages"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxPrivate, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Private_JCheckBox"));
        jCheckBoxPrivate.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxPrivate, gridBagConstraints);
        jCheckBoxPrivate.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxPrivate"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabelGenerate, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Generate_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabelGenerate, gridBagConstraints);
        jLabelGenerate.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jLabelGenerate"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxTree, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Tree_JCheckBox"));
        jCheckBoxTree.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxTree, gridBagConstraints);
        jCheckBoxTree.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxTree"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxUsages, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Usages_JCheckBox"));
        jCheckBoxUsages.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxUsages, gridBagConstraints);
        jCheckBoxUsages.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxUsages"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxNavigation, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Navigation_JCheckBox"));
        jCheckBoxNavigation.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxNavigation, gridBagConstraints);
        jCheckBoxNavigation.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxNavigation"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxIndex, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Index_JCheckBox"));
        jCheckBoxIndex.setMargin(new java.awt.Insets(0, 0, 0, 2));
        jCheckBoxIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reenableSplitIndex(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxIndex, gridBagConstraints);
        jCheckBoxIndex.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxIndex"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxSplitIndex, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_SplitIndex_JCheckBox"));
        jCheckBoxSplitIndex.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 12, 0);
        add(jCheckBoxSplitIndex, gridBagConstraints);
        jCheckBoxSplitIndex.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxSplitIndex"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabelTags, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Tags_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabelTags, gridBagConstraints);
        jLabelTags.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jLabelTags"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxAuthor, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Author_JCheckBox"));
        jCheckBoxAuthor.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 5, 0);
        add(jCheckBoxAuthor, gridBagConstraints);
        jCheckBoxAuthor.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxAuthor"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxVersion, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Version_JCheckBox"));
        jCheckBoxVersion.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 12, 0);
        add(jCheckBoxVersion, gridBagConstraints);
        jCheckBoxVersion.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxVersion"));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelWinTitle.setLabelFor(jTextFieldWinTitle);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelWinTitle, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_WinTitle_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        jPanel1.add(jLabelWinTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel1.add(jTextFieldWinTitle, gridBagConstraints);
        jTextFieldWinTitle.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jTextFieldWinTitle"));

        jLabelEncoding.setLabelFor(jTextFieldEncoding);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelEncoding, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Encoding_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(jLabelEncoding, gridBagConstraints);

        jTextFieldEncoding.setMinimumSize(new java.awt.Dimension(150, 22));
        jTextFieldEncoding.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jTextFieldEncoding, gridBagConstraints);
        jTextFieldEncoding.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jTextFieldEncoding"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabelAddOptions.setLabelFor(jTextFieldAddOptions);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelAddOptions, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_AddOptions_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabelAddOptions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel2.add(jTextFieldAddOptions, gridBagConstraints);
        jTextFieldAddOptions.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "AN_Javadoc_AdditionalOptions"));
        jTextFieldAddOptions.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "AD_Javadoc_AdditionalOptions"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabelAddOptionsInfo, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_AddOptionsInfo_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel2.add(jLabelAddOptionsInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jPanel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxPreview, org.openide.util.NbBundle.getMessage(CustomizerJavadoc.class, "LBL_CustomizeJavadoc_Preview_JCheckBox"));
        jCheckBoxPreview.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(jCheckBoxPreview, gridBagConstraints);
        jCheckBoxPreview.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CustomizerJavadoc.class).getString("AD_jCheckBoxPreview"));

    }
    // </editor-fold>//GEN-END:initComponents

    private void reenableSplitIndex(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reenableSplitIndex
        jCheckBoxSplitIndex.setEnabled( jCheckBoxIndex.isSelected() );
    }//GEN-LAST:event_reenableSplitIndex
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPackage;
    private javax.swing.JCheckBox jCheckBoxAuthor;
    private javax.swing.JCheckBox jCheckBoxIndex;
    private javax.swing.JCheckBox jCheckBoxNavigation;
    private javax.swing.JCheckBox jCheckBoxPreview;
    private javax.swing.JCheckBox jCheckBoxPrivate;
    private javax.swing.JCheckBox jCheckBoxSplitIndex;
    private javax.swing.JCheckBox jCheckBoxSubpackages;
    private javax.swing.JCheckBox jCheckBoxTree;
    private javax.swing.JCheckBox jCheckBoxUsages;
    private javax.swing.JCheckBox jCheckBoxVersion;
    private javax.swing.JLabel jLabelAddOptions;
    private javax.swing.JLabel jLabelAddOptionsInfo;
    private javax.swing.JLabel jLabelEncoding;
    private javax.swing.JLabel jLabelGenerate;
    private javax.swing.JLabel jLabelPackage;
    private javax.swing.JLabel jLabelTags;
    private javax.swing.JLabel jLabelWinTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldAddOptions;
    private javax.swing.JTextField jTextFieldEncoding;
    private javax.swing.JTextField jTextFieldPackage;
    private javax.swing.JTextField jTextFieldWinTitle;
    // End of variables declaration//GEN-END:variables
        
}
