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

package org.netbeans.modules.j2ee.deployment.impl.sharability;

import java.awt.Color;
import java.awt.Component;
import java.beans.Customizer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Arrays;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.project.ant.FileChooser;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.netbeans.spi.project.libraries.LibraryStorageArea;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  tom
 */
public class ServerVolumeCustomizer extends javax.swing.JPanel implements Customizer {
    
    private String volumeType;
    private LibraryImplementation impl;
    private LibraryStorageArea area;
    private ServerVolumeContentModel model;

    /** Creates new form J2SEVolumeCustomizer */
    ServerVolumeCustomizer(String volumeType) {
        this.volumeType = volumeType;
        initComponents();
        postInitComponents();
        this.setName(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_"+volumeType));
    }


    public void addNotify() {
        super.addNotify();
        this.addButton.requestFocus();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.addButton.setEnabled(enabled);
        if (this.addURLButton != null) {
            this.addURLButton.setEnabled(enabled);
        }
        int[] indices = content.getSelectedIndices();
        this.removeButton.setEnabled(enabled && indices.length > 0);
        this.downButton.setEnabled(enabled && indices.length > 0 && indices[indices.length-1]<model.getSize()-1);
        this.upButton.setEnabled(enabled && indices.length>0 && indices[0]>0);
    }


    private void postInitComponents () {
        this.content.setCellRenderer(new ContentRenderer());
        this.upButton.setEnabled (false);
        this.downButton.setEnabled (false);
        this.removeButton.setEnabled (false);
        if (!this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_JAVADOC)
                && !this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_SOURCE)) {
            this.addButton.setText (NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_AddClassPath"));
            this.addButton.setMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_AddClassPath").charAt(0));
            this.message.setText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_ContentClassPath"));
            this.message.setDisplayedMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_ContentClassPath").charAt(0));
            this.addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.AD_AddClassPath"));
            this.message.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.AD_ContentClassPath"));
        } else if (this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_JAVADOC)) {
            this.addButton.setText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_AddJavadoc"));
            this.addButton.setMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_AddJavadoc").charAt(0));
            this.message.setText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_ContentJavadoc"));
            this.message.setDisplayedMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_ContentJavadoc").charAt(0));
            this.addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.AD_AddJavadoc"));
            this.message.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.AD_ContentJavadoc"));
        } else if (this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_SOURCE)) {
            this.addButton.setText (NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_AddSources"));
            this.addButton.setMnemonic (NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_AddSources").charAt(0));
            this.message.setText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_ContentSources"));
            this.message.setDisplayedMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_ContentSources").charAt(0));
            this.addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.AD_AddSources"));
            this.message.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.AD_ContentSources"));
        }
        this.content.addListSelectionListener(new ListSelectionListener () {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                int[] indices = content.getSelectedIndices();
                removeButton.setEnabled(indices.length > 0);
                downButton.setEnabled(indices.length > 0 && indices[indices.length-1]<model.getSize()-1);
                upButton.setEnabled(indices.length>0 && indices[0]>0);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        message = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        content = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        message.setLabelFor(content);
        org.openide.awt.Mnemonics.setLocalizedText(message, org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.CTL_ContentMessage")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        add(message, gridBagConstraints);

        jScrollPane1.setViewportView(content);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.CTL_AddContent")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addResource(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription("null");

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.CTL_RemoveContent")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeResource(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 6, 6);
        add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.AD_RemoveContent")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(upButton, org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.CTL_UpContent")); // NOI18N
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upResource(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 6);
        add(upButton, gridBagConstraints);
        upButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.AD_UpContent")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(downButton, org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.CTL_DownContent")); // NOI18N
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downResource(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 6, 6);
        add(downButton, gridBagConstraints);
        downButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.AD_DownContent")); // NOI18N

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ServerVolumeCustomizer.class).getString("ServerVolumeCustomizer.AD_J2SEVolumeCustomizer")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void downResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downResource
        int[] indices = this.content.getSelectedIndices();
        if (indices.length == 0 || indices[0] < 0 || indices[indices.length-1]>=model.getSize()-1) {
            return;
        }
        this.model.moveDown(indices);
        for (int i=0; i< indices.length; i++) {
            indices[i] = indices[i] + 1;
        }
        this.content.setSelectedIndices (indices);
    }//GEN-LAST:event_downResource

    private void upResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upResource
        int[] indices = this.content.getSelectedIndices();
        if (indices.length == 0 || indices[0] <= 0) {
            return;
        }
        this.model.moveUp(indices);
        for (int i=0; i< indices.length; i++) {
            indices[i] = indices[i] - 1;
        }
        this.content.setSelectedIndices(indices);
    }//GEN-LAST:event_upResource

    private void removeResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeResource
        int[] indices =this.content.getSelectedIndices();
        if (indices.length == 0) {
            return;
        }
        this.model.removeResources(indices);
        if (indices[indices.length-1]-indices.length+1 < this.model.getSize()) {
            this.content.setSelectedIndex(indices[indices.length-1]-indices.length+1);
        }
        else if (indices[0]  >= 1) {
            this.content.setSelectedIndex (indices[0]-1);
        }
        //XXX don't know
//        if (this.volumeType.equals(J2SELibraryTypeProvider.VOLUME_TYPE_CLASSPATH)) {
//            impl.setContent(J2SELibraryTypeProvider.VOLUME_TYPE_MAVEN_POM, Collections.<URL>emptyList());
//        }
    }//GEN-LAST:event_removeResource

    private void addResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResource
        // TODO add your handling code here:
        File baseFolder = null;
        if (area != null) {
            baseFolder = new File(URI.create(area.getLocation().toExternalForm())).getParentFile();
        }
        FileChooser chooser = new FileChooser(baseFolder, baseFolder);
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setAcceptAllFileFilterUsed(false);
        if (!this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_JAVADOC)
                && !this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_SOURCE)) {
            chooser.setMultiSelectionEnabled (true);
            chooser.setDialogTitle(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_OpenClasses"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter (new SimpleFileFilter(NbBundle.getMessage(
                    ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_Classpath"),new String[] {"ZIP","JAR"}));   //NOI18N
            chooser.setApproveButtonText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_SelectCP"));
            chooser.setApproveButtonMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_SelectCP").charAt(0));
        } else if (this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_JAVADOC)) {
            chooser.setMultiSelectionEnabled (true);
            chooser.setDialogTitle(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_OpenJavadoc"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter (new SimpleFileFilter(NbBundle.getMessage(
                    ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_Javadoc"),new String[] {"ZIP","JAR"}));     //NOI18N
            chooser.setApproveButtonText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_SelectJD"));
            chooser.setApproveButtonMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_SelectJD").charAt(0));
        } else if (this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_SOURCE)) {
            chooser.setMultiSelectionEnabled (true);
            chooser.setDialogTitle(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_OpenSources"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter (new SimpleFileFilter(NbBundle.getMessage(
                    ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_Sources"),new String[] {"ZIP","JAR"}));     //NOI18N
            chooser.setApproveButtonText(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.CTL_SelectSRC"));
            chooser.setApproveButtonMnemonic(NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.MNE_SelectSRC").charAt(0));
        }
        if (lastFolder != null) {
            chooser.setCurrentDirectory (lastFolder);
        } else if (baseFolder != null) {
            chooser.setCurrentDirectory (baseFolder);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                lastFolder = chooser.getCurrentDirectory();
                addFiles (chooser.getSelectedPaths(), area != null ? area.getLocation() : null);
            } catch (MalformedURLException mue) {
                ErrorManager.getDefault().notify(mue);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
    }//GEN-LAST:event_addResource


//    private void addURLResource () {
//        DialogDescriptor.InputLine input = new DialogDescriptor.InputLine (
//                NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_AddJavadocURLMessage"),
//                NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_AddJavadocURLTitle"));
//        if (DialogDisplayer.getDefault().notify(input) == DialogDescriptor.OK_OPTION) {
//            try {
//                String value = input.getInputText();
//                URL url = new URL (value);
//                this.model.addResource(url);
//                this.content.setSelectedIndex(this.model.getSize()-1);
//            } catch (MalformedURLException mue) {
//                DialogDescriptor.Message message = new DialogDescriptor.Message (
//                        NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_InvalidURLFormat"),
//                        DialogDescriptor.ERROR_MESSAGE
//                );
//                DialogDisplayer.getDefault().notify(message);
//            }
//        }
//    }


    private void addFiles (String[] files, URL libraryLocation) throws MalformedURLException {
        int firstIndex = this.model.getSize();
        for (int i = 0; i < files.length; i++) {
            File f = new File(files[i]);
            //mkleint: issue 5075580 was fixed in 1.4 and 5.0u7(b01).
//            //XXX: JFileChooser workaround (JDK bug #5075580), double click on folder returns wrong file
//            // E.g. for /foo/src it returns /foo/src/src
//            // Try to convert it back by removing last invalid name component
//            if (!f.exists()) {
//                File parent = f.getParentFile();
//                if (parent != null && f.getName().equals(parent.getName()) && parent.exists()) {
//                    f = parent;
//                }
//            }
            URL url = LibrariesSupport.convertFilePathToURL(files[i]);
            File realFile = f;
            if (!f.isAbsolute()) {
                assert area != null;
                if (area != null) {
                    realFile = FileUtil.normalizeFile(new File(
                        new File(URI.create(area.getLocation().toExternalForm())).getParentFile(), f.getPath()));
                }
            }
            if (FileUtil.isArchiveFile(realFile.toURI().toURL())) {
                url = FileUtil.getArchiveRoot(url);
            }
            else if (!url.toExternalForm().endsWith("/")){
                try {
                    url = new URL (url.toExternalForm()+"/");
                } catch (MalformedURLException mue) {
                    ErrorManager.getDefault().notify(mue);
                }
            }

            if (this.volumeType.equals(ServerLibraryTypeProvider.VOLUME_JAVADOC)
                && !JavadocForBinaryQueryImpl.isValidLibraryJavadocRoot (
                    LibrariesSupport.resolveLibraryEntryURL(libraryLocation, url))) {
                
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_InvalidJavadocRoot", f.getPath()),
                    NotifyDescriptor.ERROR_MESSAGE));
                continue;
            }
            this.model.addResource(url);
        }        
        int lastIndex = this.model.getSize()-1;
        if (firstIndex<=lastIndex) {
            int[] toSelect = new int[lastIndex-firstIndex+1];
            for (int i = 0; i < toSelect.length; i++) {
                toSelect[i] = firstIndex+i;
            }
            this.content.setSelectedIndices(toSelect);
        }
        //XXX don't know        
//        if (this.volumeType.equals(J2SELibraryTypeProvider.VOLUME_TYPE_CLASSPATH)) {
//            if (impl != null) {
//                impl.setContent(J2SELibraryTypeProvider.VOLUME_TYPE_MAVEN_POM, Collections.<URL>emptyList());
//            }
//        }
    }
    
    public void setObject(Object bean) {
        if (bean instanceof LibraryStorageArea) {
            this.area = (LibraryStorageArea)bean;
        } else {
            this.area = null;
        }
        if (bean instanceof LibraryImplementation) {
            this.impl = (LibraryImplementation) bean;
            this.model = new ServerVolumeContentModel(this.impl, this.area, this.volumeType);
            this.content.setModel(model);
            if (this.model.getSize()>0) {
                this.content.setSelectedIndex(0);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }        
    
    
    private static class SimpleFileFilter extends FileFilter {
        
        private String description;
        private Collection extensions;
        
        
        public SimpleFileFilter(String description, String[] extensions) {
            this.description = description;
            this.extensions = Arrays.asList(extensions);
        }
        
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            String name = f.getName();
            int index = name.lastIndexOf('.');   //NOI18N
            if (index <= 0 || index==name.length()-1)
                return false;
            String extension = name.substring(index+1).toUpperCase();
            return this.extensions.contains(extension);
        }
        
        public String getDescription() {
            return this.description;
        }
    }
    
    
    private static File lastFolder = null;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList content;
    private javax.swing.JButton downButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel message;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
    private JButton addURLButton;
    
    private static class ContentRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String displayName = null;
            Color color = null;
            String toolTip = null;
            
            if (value instanceof URL) {
                URL url = (URL) value;                
                if ("jar".equals(url.getProtocol())) {   //NOI18N
                    url = FileUtil.getArchiveFile (url);
                }
                boolean broken = false;
                ServerVolumeContentModel model = (ServerVolumeContentModel)list.getModel();
                LibraryStorageArea area = model.getArea();
                FileObject fo = LibrariesSupport.resolveLibraryEntryFileObject(area != null ? area.getLocation() : null, url);
                if (fo == null) {
                    broken = true;
                    if ("file".equals(url.getProtocol())) { //NOI18N
                        displayName = LibrariesSupport.convertURLToFilePath(url);
                    } else {
                        displayName = url.toExternalForm();
                    }
                } else {
                    if (LibrariesSupport.isAbsoluteURL(url)) {
                        displayName = FileUtil.getFileDisplayName(fo);
                    } else {
                        displayName = LibrariesSupport.convertURLToFilePath(url);
                        toolTip = FileUtil.getFileDisplayName(fo);
                    }
                }
                if (broken) {
                    color = new Color (164,0,0);
                    toolTip = NbBundle.getMessage (ServerVolumeCustomizer.class,"ServerVolumeCustomizer.TXT_BrokenFile");                    
                }
            }
            Component c = super.getListCellRendererComponent(list, displayName, index, isSelected, cellHasFocus);
            if (c instanceof JComponent) {
                if (color != null) {
                    ((JComponent)c).setForeground (color);
                }
                if (toolTip != null) {
                    ((JComponent)c).setToolTipText(toolTip);
                } else {
                    ((JComponent)c).setToolTipText(null);
                }
            }
            return c;
        }

    }

}
