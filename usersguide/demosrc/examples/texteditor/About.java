/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package examples.texteditor;

public class About extends javax.swing.JDialog {

    /** Initializes the Form */
    public About(java.awt.Frame parent) {
        super (parent, true);
        initComponents ();
        pack ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        jMenuBar1 = new javax.swing.JMenuBar ();
        jMenu1 = new javax.swing.JMenu ();
        jMenuItem1 = new javax.swing.JMenuItem ();
        jTextField1 = new javax.swing.JTextField ();

        jMenu1.setText ("About");

        jMenuItem1.setText ("Close");
        jMenuItem1.addActionListener (new java.awt.event.ActionListener () {
                                          public void actionPerformed (java.awt.event.ActionEvent evt) {
                                              jMenuItem1ActionPerformed (evt);
                                          }
                                      }
                                     );

        jMenu1.add (jMenuItem1);
        jMenuBar1.add (jMenu1);
        setTitle ("About");
        addWindowListener (new java.awt.event.WindowAdapter () {
                               public void windowClosing (java.awt.event.WindowEvent evt) {
                                   closeDialog (evt);
                               }
                           }
                          );

        jTextField1.setText ("Ted the Text Editor.");
        jTextField1.setEditable (false);


        getContentPane ().add (jTextField1, java.awt.BorderLayout.CENTER);

        setJMenuBar (jMenuBar1);

    }//GEN-END:initComponents

    private void jMenuItem1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Add your handling code here:
        closeDialog(null);
    }//GEN-LAST:event_jMenuItem1ActionPerformed


    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible (false);
        dispose ();
    }//GEN-LAST:event_closeDialog


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables



}
