/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seplatform.libraries;

import java.beans.Customizer;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Arrays;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.openide.ErrorManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.netbeans.spi.project.libraries.LibraryImplementation;




import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  tom
 */
public class J2SEVolumeCustomizer extends javax.swing.JPanel implements Customizer {
    
    private String volumeType;
    private LibraryImplementation impl;
    private VolumeContentModel model;

    /** Creates new form J2SEVolumeCustomizer */
    J2SEVolumeCustomizer (String volumeType) {
        this.volumeType = volumeType;
        initComponents();
        postInitComponents ();
        this.setName (NbBundle.getMessage(J2SEVolumeCustomizer.class,"TXT_"+volumeType));
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
        this.removeButton.setEnabled(enabled);
        this.downButton.setEnabled(enabled);
        this.upButton.setEnabled(enabled);
    }


    private void postInitComponents () {
        this.upButton.setEnabled (false);
        this.downButton.setEnabled (false);
        this.removeButton.setEnabled (false);
        if (this.volumeType.equals("classpath")) {  //NOI18N
            this.addButton.setText (NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_AddClassPath"));
            this.addButton.setMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_AddClassPath").charAt(0));
            this.message.setText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_ContentClassPath"));
            this.message.setDisplayedMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_ContentClassPath").charAt(0));
            this.addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEVolumeCustomizer.class,"AD_AddClassPath"));
            this.message.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEVolumeCustomizer.class,"AD_ContentClassPath"));
        }
        else if (this.volumeType.equals("javadoc")) {  //NOI18N
            this.addButton.setText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_AddJavadoc"));
            this.addButton.setMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_AddJavadoc").charAt(0));
            this.message.setText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_ContentJavadoc"));
            this.message.setDisplayedMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_ContentJavadoc").charAt(0));
            this.addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEVolumeCustomizer.class,"AD_AddJavadoc"));
            this.message.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEVolumeCustomizer.class,"AD_ContentJavadoc"));
//            this.addURLButton = new JButton ();
//            this.addURLButton.setText(NbBundle.getMessage (J2SEVolumeCustomizer.class,"CTL_AddJavadocURL"));
//            this.addURLButton.setMnemonic(NbBundle.getMessage (J2SEVolumeCustomizer.class,"MNE_AddJavadocURL").charAt(0));
//            this.addURLButton.addActionListener (new ActionListener () {
//                public void actionPerformed(ActionEvent e) {
//                    addURLResource ();
//                }
//            });
//            GridBagConstraints c = new GridBagConstraints();
//            c.gridx = 1;
//            c.gridy = 2;
//            c.gridwidth = GridBagConstraints.REMAINDER;
//            c.gridheight = 1;
//            c.fill = GridBagConstraints.HORIZONTAL;
//            c.anchor = GridBagConstraints.NORTHWEST;
//            c.insets = new Insets (0,6,5,6);
//            ((GridBagLayout)this.getLayout()).setConstraints(this.addURLButton,c);
//            this.add (this.addURLButton);
        }
        else if (this.volumeType.equals("src")) {  //NOI18N
            this.addButton.setText (NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_AddSources"));
            this.addButton.setMnemonic (NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_AddSources").charAt(0));
            this.message.setText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_ContentSources"));
            this.message.setDisplayedMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_ContentSources").charAt(0));
            this.addButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEVolumeCustomizer.class,"AD_AddSources"));
            this.message.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2SEVolumeCustomizer.class,"AD_ContentSources"));
        }
        this.content.addListSelectionListener(new ListSelectionListener () {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                if (content.getSelectedIndex()==-1) {
                    removeButton.setEnabled(false);
                    upButton.setEnabled(false);
                    downButton.setEnabled(false);
                }
                else {
                    removeButton.setEnabled(true);
                    upButton.setEnabled(content.getSelectedIndex()!=0);
                    downButton.setEnabled(content.getSelectedIndex()!=model.getSize()-1);
                }
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        message = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        content = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("AD_J2SEVolumeCustomizer"));
        message.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("MNE_ContentMessage").charAt(0));
        message.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("CTL_ContentMessage"));
        message.setLabelFor(content);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        add(message, gridBagConstraints);

        content.setPrototypeCellValue("0123456789012345678912345");
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

        addButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("CTL_AddContent"));
        addButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("MNE_AddContent").charAt(0));
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
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 5, 6);
        add(addButton, gridBagConstraints);

        removeButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("CTL_RemoveContent"));
        removeButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("MNE_RemoveContent").charAt(0));
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
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("AD_RemoveContent"));

        upButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("CTL_UpContent"));
        upButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("MNE_UpContent").charAt(0));
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
        upButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("AD_UpContent"));

        downButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("CTL_DownContent"));
        downButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("MNE_DownContent").charAt(0));
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
        downButton.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seplatform/libraries/Bundle").getString("AD_DownContent"));

    }//GEN-END:initComponents

    private void downResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downResource
        int index = this.content.getSelectedIndex();
        this.model.moveDown(index);
        this.content.setSelectedIndex (index+1);
    }//GEN-LAST:event_downResource

    private void upResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upResource
        int index = this.content.getSelectedIndex();
        this.model.moveUp(index);
        this.content.setSelectedIndex(index-1);
    }//GEN-LAST:event_upResource

    private void removeResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeResource
        int index =this.content.getSelectedIndex();
        this.model.removeResource(index);
        if (index < this.model.getSize()) {
            this.content.setSelectedIndex(index);
        }
        else if (index  >= 1) {
            this.content.setSelectedIndex (index-1);
        }
    }//GEN-LAST:event_removeResource

    private void addResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResource
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        if (this.volumeType.equalsIgnoreCase("classpath")) {        //NOI18N
            chooser.setMultiSelectionEnabled (true);
            chooser.setDialogTitle(NbBundle.getMessage(J2SEVolumeCustomizer.class,"TXT_OpenClasses"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter (new SimpleFileFilter(NbBundle.getMessage(
                    J2SEVolumeCustomizer.class,"TXT_Classpath"),new String[] {"ZIP","JAR"}));   //NOI18N
            chooser.setApproveButtonText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_SelectCP"));
            chooser.setApproveButtonMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_SelectCP").charAt(0));
        }
        else if (this.volumeType.equalsIgnoreCase("javadoc")) {     //NOI18N
            chooser.setDialogTitle(NbBundle.getMessage(J2SEVolumeCustomizer.class,"TXT_OpenJavadoc"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter (new SimpleFileFilter(NbBundle.getMessage(
                    J2SEVolumeCustomizer.class,"TXT_Javadoc"),new String[] {"ZIP","JAR"}));     //NOI18N
            chooser.setApproveButtonText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_SelectJD"));
            chooser.setApproveButtonMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_SelectJD").charAt(0));
        }
        else if (this.volumeType.equalsIgnoreCase("src")) {         //NOI18N
            chooser.setDialogTitle(NbBundle.getMessage(J2SEVolumeCustomizer.class,"TXT_OpenSources"));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter (new SimpleFileFilter(NbBundle.getMessage(
                    J2SEVolumeCustomizer.class,"TXT_Sources"),new String[] {"ZIP","JAR"}));     //NOI18N
            chooser.setApproveButtonText(NbBundle.getMessage(J2SEVolumeCustomizer.class,"CTL_SelectSRC"));
            chooser.setApproveButtonMnemonic(NbBundle.getMessage(J2SEVolumeCustomizer.class,"MNE_SelectSRC").charAt(0));
        }
        if (lastFolder != null) {
            chooser.setCurrentDirectory (lastFolder);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                lastFolder = chooser.getCurrentDirectory();
                if (chooser.isMultiSelectionEnabled()) {
                    addFiles (chooser.getSelectedFiles());
                }
                else {
                    addFiles (new File[] {chooser.getSelectedFile()});
                }
            } catch (MalformedURLException mue) {
                ErrorManager.getDefault().notify(mue);
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


    private void addFiles (File[] files) throws MalformedURLException {
        for (int i = 0; i < files.length; i++) {
            URL url = files[i].toURI().toURL();
            this.model.addResource(url);
        }
        this.content.setSelectedIndex(this.model.getSize()-1);
    }

    public void setObject(Object bean) {
        if (bean instanceof LibraryImplementation) {
            this.impl = (LibraryImplementation) bean;
            this.model = new VolumeContentModel(this.impl,this.volumeType);
            this.content.setModel (model);
            if (this.model.getSize()>0) {
                this.content.setSelectedIndex (0);
            }
        }
        else {
            throw new IllegalArgumentException ();
        }
    }


    private static class SimpleFileFilter extends FileFilter {

        private String description;
        private Collection extensions;


        public SimpleFileFilter (String description, String[] extensions) {
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
            String extension = name.substring (index+1).toUpperCase();
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

}
