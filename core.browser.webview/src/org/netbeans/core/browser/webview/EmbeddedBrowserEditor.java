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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * EmbeddedBrowserEditor.java
 *
 * Created on 11.8.2009, 13:32:26
 */

package org.netbeans.core.browser.webview;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.HtmlBrowser.Factory;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author S. Aubrecht
 */
public class EmbeddedBrowserEditor extends javax.swing.JPanel implements ItemListener {
    
    private static final String BROWSERS_FOLDER = "Services/Browsers"; // NOI18N

    private final List<? extends DataObject> browsers;
    private final PropertyEditor property;

    /** Creates new form EmbeddedBrowserEditor */
    public EmbeddedBrowserEditor( PropertyEditor prop ) {
        this.property = prop;
        initComponents();
        browsers = loadBrowserList();
        checkAnotherBrowser.setSelected(true);
        String[] names = new String[browsers.size()];
        for( int i=0; i<browsers.size(); i++ ) {
            names[i] = browsers.get( i ).getNodeDelegate().getDisplayName();
        }
        checkAnotherBrowser.setSelected( prop.getValue() != null );
        comboBrowsers.setModel( new DefaultComboBoxModel(names));
        if( browsers.isEmpty() ) {
            checkAnotherBrowser.setEnabled(false);
            checkAnotherBrowser.setSelected(false);
            comboBrowsers.setSelectedIndex(-1);
        } else if( null != prop.getValue() ) {
            selectBrowser( (org.openide.awt.HtmlBrowser.Factory)prop.getValue() );
        }
        checkAnotherBrowser.addItemListener(this);
        comboBrowsers.addItemListener(this);
        txtRuntimeLocation.setText( new DefaultJFXRuntimeProvider().getJFXRuntimePath() );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkAnotherBrowser = new javax.swing.JCheckBox();
        comboBrowsers = new javax.swing.JComboBox();
        lblRuntimeLocation = new javax.swing.JLabel();
        txtRuntimeLocation = new javax.swing.JTextField();
        btnBrowser = new javax.swing.JButton();

        checkAnotherBrowser.setText(NbBundle.getMessage(EmbeddedBrowserEditor.class, "EmbeddedBrowserEditor.checkAnotherBrowser.text")); // NOI18N

        lblRuntimeLocation.setText(NbBundle.getMessage(EmbeddedBrowserEditor.class, "EmbeddedBrowserEditor.lblRuntimeLocation.text")); // NOI18N

        txtRuntimeLocation.setEditable(false);

        btnBrowser.setText(NbBundle.getMessage(EmbeddedBrowserEditor.class, "EmbeddedBrowserEditor.btnBrowser.text")); // NOI18N
        btnBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(comboBrowsers, 0, 398, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtRuntimeLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowser))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkAnotherBrowser)
                            .addComponent(lblRuntimeLocation))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkAnotherBrowser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBrowsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRuntimeLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRuntimeLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowser))
                .addContainerGap(26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowserActionPerformed( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_btnBrowserActionPerformed
        File f = RuntimePathPanel.browseRuntimeFolder();
        if( null != f ) {
            txtRuntimeLocation.setText( f.getAbsolutePath() );
            DefaultJFXRuntimeProvider.setJFXRuntimePath( f );
        }
    }//GEN-LAST:event_btnBrowserActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowser;
    private javax.swing.JCheckBox checkAnotherBrowser;
    private javax.swing.JComboBox comboBrowsers;
    private javax.swing.JLabel lblRuntimeLocation;
    private javax.swing.JTextField txtRuntimeLocation;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        comboBrowsers.setEnabled(checkAnotherBrowser.isSelected());
        storeSettings();
    }

    private void storeSettings() {
        if( checkAnotherBrowser.isSelected() ) {
            DataObject dob = browsers.get(comboBrowsers.getSelectedIndex());
            HtmlBrowser.Factory newBrowser = null;
            try {
                newBrowser = (Factory) dob.getLookup().lookup(InstanceCookie.class).instanceCreate();
                Lookup.Result<HtmlBrowser.Factory> res = Lookup.getDefault ().lookupResult (HtmlBrowser.Factory.class);
                java.util.Iterator<? extends HtmlBrowser.Factory> it = res.allInstances ().iterator ();
                while (it.hasNext ()) {
                    HtmlBrowser.Factory brow = it.next ();
                    if( brow.equals(newBrowser) ) {
                        newBrowser = brow;
                        break;
                    }
                }
                property.setValue(newBrowser);
            } catch( IOException ex ) {
                Exceptions.printStackTrace(ex);
            } catch( ClassNotFoundException ex ) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            property.setValue(null);
        }
    }

    private List<? extends DataObject> loadBrowserList() {
        ArrayList<DataObject> res = new ArrayList<DataObject>(20);

        FileObject servicesBrowsers = FileUtil.getConfigFile(BROWSERS_FOLDER);

        if (servicesBrowsers != null) {

            DataFolder folder = DataFolder.findFolder(servicesBrowsers);
            DataObject[] browserSettings = folder.getChildren();

            for (DataObject browserSetting : browserSettings) {

                InstanceCookie cookie = browserSetting.getLookup().lookup(InstanceCookie.class);
                FileObject primaryFile = browserSetting.getPrimaryFile();
                if( "EmbeddedBrowser.settings".equals(primaryFile.getNameExt()) ) {
                    continue;
                }

                if (cookie != null && !Boolean.TRUE.equals(primaryFile.getAttribute("hidden"))) {
                    res.add(browserSetting);
                }
            }
        }
        return res;
    }

    private void selectBrowser( HtmlBrowser.Factory browser ) {
        int selIndex = -1;
        for( int i=0; i<browsers.size(); i++ ) {
            DataObject dob = browsers.get(i);
            InstanceCookie cookie = dob.getLookup().lookup(InstanceCookie.class);
            try {
                if( browser.equals(cookie.instanceCreate()) ) {
                    selIndex = i;
                    break;
                }
            } catch( IOException ex ) {
                Exceptions.printStackTrace(ex);
            } catch( ClassNotFoundException ex ) {
                Exceptions.printStackTrace(ex);
            }
        }
        comboBrowsers.setSelectedIndex(selIndex);
    }
}