/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.project.libraries.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import org.netbeans.modules.project.libraries.ui.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.project.libraries.LibraryAccessor;
import org.netbeans.spi.project.libraries.ArealLibraryProvider;
import org.netbeans.spi.project.libraries.LibraryStorageArea;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 */
class AllLibrariesCustomizer extends javax.swing.JPanel {
    
    private org.netbeans.modules.project.libraries.ui.LibrariesCustomizer librariesCustomizer;
    /** Creates new form AllLibrariesCustomizer */
    public AllLibrariesCustomizer() {
        initComponents();
        librariesCustomizer = new org.netbeans.modules.project.libraries.ui.LibrariesCustomizer(null);
        placeholder.add(librariesCustomizer);
        initModel();
    }

    public boolean apply() {
        return librariesCustomizer.apply();
    }
    
    private void initModel() {
        List<String> items = new ArrayList<String>();
        items.add(NbBundle.getMessage(AllLibrariesCustomizer.class, "LABEL_Global_Libraries"));
        for (LibraryManager man : LibraryManager.getOpenManagers()) {
            if (man.getLocation() == null) {
                continue;
            }
            items.add(LibrariesSupport.convertURIToFilePath(URI.create(man.getLocation().toExternalForm())));
        }
        libraryManagerComboBox.setModel(new DefaultComboBoxModel(items.toArray(new String[items.size()])));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        libraryManagerComboBox = new javax.swing.JComboBox();
        placeholder = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        libraryManagerComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libraryManagerComboBoxActionPerformed(evt);
            }
        });

        placeholder.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(java.text.MessageFormat.format(org.openide.util.NbBundle.getMessage(AllLibrariesCustomizer.class, "AllLibrariesCustomizer.jLabel1.text"), new Object[] {})); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, placeholder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(libraryManagerComboBox, 0, 289, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(libraryManagerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(placeholder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void libraryManagerComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryManagerComboBoxActionPerformed
        int index = libraryManagerComboBox.getSelectedIndex();
        if (index == -1) {
            return;
        } else if (index == 0) {
            librariesCustomizer.setLibraryStorageArea(null);
        } else if (index > 0) {
            URL u = null;
            try {
                //#131452 prevent space in path problem when converting to URL.
                File loc = FileUtil.normalizeFile(new File((String) libraryManagerComboBox.getModel().getSelectedItem()));
                u = loc.toURI().to`URL();
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
            librariesCustomizer.setLibraryStorageArea(findLibraryStorageArea(u));
        }
    }//GEN-LAST:event_libraryManagerComboBoxActionPerformed
    
    private LibraryStorageArea findLibraryStorageArea(URL u) {
        for (ArealLibraryProvider alp : Lookup.getDefault().lookupAll(ArealLibraryProvider.class)) {
            for (LibraryStorageArea area : LibraryAccessor.getOpenAreas(alp)) {
                if (u.equals(area.getLocation())) {
                    return area;
                }
            }
        }
        return null;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox libraryManagerComboBox;
    private javax.swing.JPanel placeholder;
    // End of variables declaration//GEN-END:variables
    
}
