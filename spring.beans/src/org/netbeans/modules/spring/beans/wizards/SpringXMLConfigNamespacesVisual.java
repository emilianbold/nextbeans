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
 *
 * Portions Copyrighted 2008 Craig MacKay.
 */

package org.netbeans.modules.spring.beans.wizards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.spring.api.SpringUtilities;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

public final class SpringXMLConfigNamespacesVisual extends JPanel {

    private Library springLibrary;
    private ClassPath classPath;
    private boolean addSpringToClassPath;

    public SpringXMLConfigNamespacesVisual() {
        initComponents();
        // set the color of the table's JViewport
        includesTable.getParent().setBackground(includesTable.getBackground());
        TableColumn col1 = includesTable.getColumnModel().getColumn(0);
        col1.setMaxWidth(0);
        includesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        includesTable.revalidate();
//        springLibrary = SpringUtilities.findSpringLibrary();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SpringXMLConfigNamespacesVisual.class, "LBL_Namespaces_Include_Step");
    }

    public void setClassPath(ClassPath classPath) {
        this.classPath = classPath;
        updateClassPathWarning();
    }

    private void updateClassPathWarning() {
        boolean alreadyAdded = classPath != null && SpringUtilities.containsSpring(classPath);
        boolean needToAdd = !(alreadyAdded || addSpringToClassPath);
        springNotOnClassPathLabel.setVisible(needToAdd);
        addSpringButton.setVisible(needToAdd);
        cbSpringVersion.setVisible(needToAdd);
        if (needToAdd) {
            initLibraries();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        includesScrollPane = new javax.swing.JScrollPane();
        includesTable = new javax.swing.JTable();

        addSpringButton = new javax.swing.JButton();
        springNotOnClassPathLabel = new javax.swing.JLabel();
        cbSpringVersion = new javax.swing.JComboBox();

        includesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "aop - http://www.springframework.org/schema/aop"},
                {null, "c - http://www.springframework.org/schema/c"},
                {null, "context - http://www.springframework.org/schema/context"},
                {null, "flow - http://www.springframework.org/schema/webflow-config"},
                {null, "jee - http://www.springframework.org/schema/jee"},
                {null, "jms - http://www.springframework.org/schema/jms"},
                {null, "lang - http://www.springframework.org/schema/lang"},
                {null, "osgi - http://www.springframework.org/schema/osgi"},
                {null, "p - http://www.springframework.org/schema/p"},
                {null, "tx - http://www.springframework.org/schema/tx"},
                {null, "util - http://www.springframework.org/schema/util"}
            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        includesTable.setShowHorizontalLines(false);
        includesTable.setShowVerticalLines(false);
        includesTable.setShowGrid(false);
        includesTable.setDragEnabled(false);
        includesTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        includesScrollPane.setViewportView(includesTable);

        org.openide.awt.Mnemonics.setLocalizedText(addSpringButton, org.openide.util.NbBundle.getMessage(SpringXMLConfigNamespacesVisual.class, "LBL_AddSpringFramework")); // NOI18N
        addSpringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSpringButtonActionPerformed(evt);
            }
        });

        springNotOnClassPathLabel.setIcon(new ImageIcon(ImageUtilities.loadImage("org/netbeans/modules/spring/beans/resources/warning.gif"))   );
        org.openide.awt.Mnemonics.setLocalizedText(springNotOnClassPathLabel, org.openide.util.NbBundle.getMessage(SpringXMLConfigNamespacesVisual.class, "LBL_SpringNotOnClassPath")); // NOI18N

        cbSpringVersion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Library found" }));
        cbSpringVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSpringVersionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(includesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addSpringButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSpringVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(springNotOnClassPathLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(includesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(springNotOnClassPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addSpringButton)
                    .addComponent(cbSpringVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void addSpringButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSpringButtonActionPerformed
    addSpringToClassPath = true;
    updateClassPathWarning();
}//GEN-LAST:event_addSpringButtonActionPerformed

private void cbSpringVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSpringVersionActionPerformed
    springLibrary = springLibs.get(cbSpringVersion.getSelectedIndex());

}//GEN-LAST:event_cbSpringVersionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSpringButton;
    private javax.swing.JComboBox cbSpringVersion;
    private javax.swing.JScrollPane includesScrollPane;
    private javax.swing.JTable includesTable;
    private javax.swing.JLabel springNotOnClassPathLabel;
    // End of variables declaration//GEN-END:variables

    public String[] getIncludedNamespaces() {
        List<String> incs = new ArrayList<String>();
        TableModel model = includesTable.getModel();

        for(int i = 0; i < model.getRowCount(); i++) {
            Boolean selected = (Boolean) model.getValueAt(i, 0);
            if(selected != null && selected == Boolean.TRUE) {
                String namespace = (String) model.getValueAt(i, 1);
                incs.add(namespace);
            }
        }
        return incs.toArray(new String[0]);
    }

    public boolean getAddSpringToClassPath() {
        return addSpringToClassPath;
    }

    public Library getSpringLibrary() {
        return springLibrary;
    }

    private boolean libsInitialized = false;
    private List<Library> springLibs = new ArrayList<Library>();

    private Set<String> usedPrefixes = new HashSet<String>();

    private String generatePrefix(String namespace) {
        String prefix = namespace.substring(namespace.lastIndexOf("/")+1).toLowerCase();
        int i = 1;
        String newPrefix = prefix;
        while (usedPrefixes.contains(newPrefix)) {
            newPrefix = prefix + (i++);
        }
        usedPrefixes.add(newPrefix);
        return newPrefix;
    }


    private void initLibraries() {
        if (!libsInitialized) {
            Vector<String> items = new Vector<String>();
            springLibs.clear();

            for (Library library : SpringUtilities.getJavaLibraries()) {
                if (SpringUtilities.isSpringLibrary(library)) {
                    items.add(library.getDisplayName());
                    springLibs.add(library);
                }
            }
            cbSpringVersion.setModel(new DefaultComboBoxModel(items));
            springLibrary = springLibs.get(cbSpringVersion.getSelectedIndex());
            libsInitialized = true;
            repaint();
        }
    }
}
