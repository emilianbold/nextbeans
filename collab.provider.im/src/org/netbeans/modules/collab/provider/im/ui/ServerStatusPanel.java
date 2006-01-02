/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.collab.provider.im.ui;

/**
 *
 * @author  Alexandr Scherbatiy
 */


import org.netbeans.modules.collab.core.Debug;
import org.openide.util.NbBundle;


import org.netbeans.modules.collab.provider.im.IMReconnect;
import java.awt.Dialog;
import java.awt.Frame;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.windows.WindowManager;

public class ServerStatusPanel extends javax.swing.JPanel {
    
    
    //private boolean showDialog;
    //private boolean doReconnect;
    
    private  IMReconnect reconnectClass;
    private boolean alive;
    private String server;
    
    
    
    
    /** Creates new form NewJPanel */
    public ServerStatusPanel(IMReconnect reconnectClass, String server) {
        super();
        this.server = server;
        initComponents();

        this.reconnectClass = reconnectClass;
        alive = true;
        createDialog();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        northPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        labelPanel = new javax.swing.JPanel();
        connectionInterruptedLabel = new javax.swing.JLabel();
        attemptToReconnectLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();
        progress = new javax.swing.JProgressBar();
        workPanel = new javax.swing.JPanel();
        checkBoxesPanel = new javax.swing.JPanel();
        doNotReconnect = new javax.swing.JCheckBox();
        doNotShow = new javax.swing.JCheckBox();
        buttonPanel = new javax.swing.JPanel();
        startStopPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButtton = new javax.swing.JButton();
        westPanel = new javax.swing.JPanel();
        eastPanel = new javax.swing.JPanel();
        southPanel = new javax.swing.JPanel();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        setPreferredSize(new java.awt.Dimension(350, 175));
        northPanel.setPreferredSize(new java.awt.Dimension(400, 15));
        add(northPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setLayout(new java.awt.BorderLayout());

        labelPanel.setLayout(new javax.swing.BoxLayout(labelPanel, javax.swing.BoxLayout.Y_AXIS));

        connectionInterruptedLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(connectionInterruptedLabel, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_ConnectionInterrupted"));
        labelPanel.add(connectionInterruptedLabel);

        org.openide.awt.Mnemonics.setLocalizedText(attemptToReconnectLabel, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_AttemptingToReconnect", new Object[] {server}));
        labelPanel.add(attemptToReconnectLabel);

        centerPanel.add(labelPanel, java.awt.BorderLayout.NORTH);

        progress.setPreferredSize(new java.awt.Dimension(250, 20));
        progress.setStringPainted(true);
        progressPanel.add(progress);

        centerPanel.add(progressPanel, java.awt.BorderLayout.CENTER);

        workPanel.setLayout(new java.awt.BorderLayout());

        checkBoxesPanel.setLayout(new javax.swing.BoxLayout(checkBoxesPanel, javax.swing.BoxLayout.Y_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(doNotReconnect, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_DoNotReconnect"));
        checkBoxesPanel.add(doNotReconnect);

        org.openide.awt.Mnemonics.setLocalizedText(doNotShow, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_DoNotShow"));
        checkBoxesPanel.add(doNotShow);

        workPanel.add(checkBoxesPanel, java.awt.BorderLayout.CENTER);

        buttonPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(startButton, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_Start"));
        startButton.addActionListener(formListener);

        startStopPanel.add(startButton);

        org.openide.awt.Mnemonics.setLocalizedText(stopButton, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_Stop"));
        stopButton.addActionListener(formListener);

        startStopPanel.add(stopButton);

        buttonPanel.add(startStopPanel, java.awt.BorderLayout.WEST);

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_OK"));
        okButton.addActionListener(formListener);

        okCancelPanel.add(okButton);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButtton, org.openide.util.NbBundle.getMessage(ServerStatusPanel.class, "LBL_Cancel"));
        cancelButtton.addActionListener(formListener);

        okCancelPanel.add(cancelButtton);

        buttonPanel.add(okCancelPanel, java.awt.BorderLayout.EAST);

        workPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        centerPanel.add(workPanel, java.awt.BorderLayout.SOUTH);

        add(centerPanel, java.awt.BorderLayout.CENTER);

        westPanel.setPreferredSize(new java.awt.Dimension(15, 150));
        add(westPanel, java.awt.BorderLayout.WEST);

        eastPanel.setPreferredSize(new java.awt.Dimension(15, 150));
        add(eastPanel, java.awt.BorderLayout.EAST);

        southPanel.setPreferredSize(new java.awt.Dimension(400, 15));
        add(southPanel, java.awt.BorderLayout.SOUTH);

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == startButton) {
                ServerStatusPanel.this.startButtonActionPerformed(evt);
            }
            else if (evt.getSource() == stopButton) {
                ServerStatusPanel.this.stopButtonActionPerformed(evt);
            }
            else if (evt.getSource() == okButton) {
                ServerStatusPanel.this.okButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cancelButtton) {
                ServerStatusPanel.this.cancelButttonActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButttonActionPerformed
        cancelAction();
    }//GEN-LAST:event_cancelButttonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        okAction();
    }//GEN-LAST:event_okButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        stopAction();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        startAction();
    }//GEN-LAST:event_startButtonActionPerformed
    
    
    public boolean isAlive(){
        return alive;
    }
    
    public void createDialog(){
        //SyncWaitPanel panel=new SyncWaitPanel();
        DialogDescriptor descriptor=new DialogDescriptor(this,
                NbBundle.getMessage(ServerStatusPanel.class,
                "LBL_CollaborationServerReconnect")); // NOI18N
        
        final Object[] OPTIONS=new Object[0];
        descriptor.setOptions(OPTIONS);
        descriptor.setClosingOptions(OPTIONS);
        descriptor.setModal(false);
        descriptor.setOptionsAlign(DialogDescriptor.BOTTOM_ALIGN);
        
        dialog=DialogDisplayer.getDefault().createDialog(descriptor);
        
        Frame mainWindow=WindowManager.getDefault().getMainWindow();
        int windowX=mainWindow.getX();
        int windowY=mainWindow.getY();
        int windowWidth=mainWindow.getWidth();
        int windowHeight=mainWindow.getHeight();
        int dialogWidth=dialog.getWidth();
        int dialogHeight=dialog.getHeight();
        int dialogX=windowX+windowWidth-dialogWidth-25;
        int dialogY=windowY+500;
        
        dialog.setLocation(dialogX,dialogY);
        dialog.setVisible(true);
    }
    
    public void startAction(){
        progress.setString(NbBundle.getMessage(ServerStatusPanel.class, "LBL_StartConnecting"));
        progress.setIndeterminate(true);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        reconnectClass.startAction();
    }
    
    public void stopAction(){
        progress.setString(NbBundle.getMessage(ServerStatusPanel.class, "LBL_StopConnecting"));
        progress.setIndeterminate(false);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        reconnectClass.stopAction();
        
    }
    
    
    public void okAction(){
        reconnectClass.setDoReconnect(!doNotReconnect.isSelected());
        reconnectClass.setShowStatusDialog(!doNotShow.isSelected());
        reconnectClass.okAction();
        
        endAction();
    }
    
    public void cancelAction(){
        reconnectClass.cancelAction();
        endAction();
    }
    
    public void endAction(){
        alive = false;
        setVisible(false);
        dialog.dispose();
    }
    
    private static Dialog dialog;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attemptToReconnectLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButtton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel checkBoxesPanel;
    private javax.swing.JLabel connectionInterruptedLabel;
    private javax.swing.JCheckBox doNotReconnect;
    private javax.swing.JCheckBox doNotShow;
    private javax.swing.JPanel eastPanel;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.JProgressBar progress;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JPanel southPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel startStopPanel;
    private javax.swing.JButton stopButton;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel workPanel;
    // End of variables declaration//GEN-END:variables
    
}
