/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.registry.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JButton;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author  david
 */
public class ResultViewerDialog extends javax.swing.JPanel {
    private JButton okButton = new JButton(NbBundle.getMessage(this.getClass(), "OPTION_OK"));
    /** Creates new form ResultViewerDialog */
    public ResultViewerDialog() {
        super();
        initComponents();
//        setSize(200,200);
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        Dimension frameSize = this.getSize();
//        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        resultEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        resultEditorPane.setEditable(false);
        jScrollPane1.setViewportView(resultEditorPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    }
    
    
    public void setText(String inText) {
        this.resultEditorPane.setText(inText);
    }
    
    public String getText() {
        return this.resultEditorPane.getText();
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("projrave_ui_elements_server_nav_add_websvcdb");
    }
    
    public JButton getOkButton() {
        return okButton;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JEditorPane resultEditorPane;
    // End of variables declaration//GEN-END:variables
    
}
