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
package org.netbeans.modules.subversion.ui.checkout;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.subversion.FileStatusCache;
import org.netbeans.modules.subversion.Subversion;
import org.netbeans.modules.subversion.settings.HistorySettings;
import org.netbeans.modules.subversion.ui.browser.RepositoryPathNode;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.lookup.Lookups;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 *
 * @author Tomas Stupka
 */
public final class CheckoutAction extends CallableSystemAction {
    
    public void performAction() {
        CheckoutWizard wizard = new CheckoutWizard();
        if (!wizard.show()) return;
        
        final SVNUrl[] svnUrls = wizard.getSelectedUrls();
        final File file = wizard.getWorkdir();        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {                
                checkout(svnUrls, file);
            }
        });
    }
    
    public String getName() {
        return NbBundle.getMessage(CheckoutAction.class, "CTL_CheckoutAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

/**
     * Perform asynchronous checkout action with preconfigured values.
     * On succesfull finish shows open project dialog.
     *
     */
    public void checkout(final SVNUrl svnUrls[], final File workingFolder) {
                
        Executor.Command cmd = new Executor.Command () {
            protected void executeCommand(ISVNClientAdapter client) throws SVNClientException {                                
                for (int i = 0; i < svnUrls.length; i++) {                                        
                    File destination = new File(workingFolder.getAbsolutePath() + "/" + svnUrls[i].getLastPathSegment());
                    destination.mkdir();                    
                    
                    client.checkout(svnUrls[i], destination, SVNRevision.HEAD, true);    
                }                    
            }
            // XXX onError
        };
        
        ProgressHandle progressHandle = 
            ProgressHandleFactory.createHandle(org.openide.util.NbBundle.getMessage(CheckoutAction.class, "BK0001"));       // NOI18N
        progressHandle.start();
                
        
        try {
            Executor.getInstance().execute(cmd);
        } catch (SVNClientException ex) {
            ex.printStackTrace();
            // XXX notify me!
            // return; XXX ???            
        }
        
        progressHandle.finish();
        
        if (HistorySettings.getFlag(HistorySettings.PROP_SHOW_CHECKOUT_COMPLETED, -1) != 0) {
            //group.addBarrier(new CheckoutCompletedController(/*executor, */workingFolder, scanProject));
            CheckoutCompletedController ccc = new CheckoutCompletedController(/*executor, */ workingFolder, true);
            ccc.run(); // :)
        }                
    }

    /** On task finish shows next steps UI.*/
    // XXX is just a workaround version until there will be a kind of executor support in subversion ....
    private class CheckoutCompletedController implements Runnable, ActionListener {

        //private final CheckoutExecutor executor;
        private final File workingFolder;
        private final boolean openProject;

        private CheckoutCompletedPanel panel;
        private Dialog dialog;
        private Project projectToBeOpened;

        public CheckoutCompletedController(/*CheckoutExecutor executor, */ File workingFolder, boolean openProject) {
            //this.executor = executor;
            this.workingFolder = workingFolder;
            this.openProject = openProject;
        }

        public void run() {

//            if (executor.isSuccessful() == false) {
//                return;
//            }

            List checkedOutProjects = new LinkedList();
            File normalizedWorkingFolder = FileUtil.normalizeFile(workingFolder);
            // checkout creates new folders and cache must be aware of them
            refreshRecursively(normalizedWorkingFolder);
            FileObject fo = FileUtil.toFileObject(normalizedWorkingFolder);
            if (fo != null) {
                // XXX test me! 
                checkedOutProjects = ProjectUtilities.scanForProjects(fo);
                
//                //String name = NbBundle.getMessage(CheckoutAction.class, "BK3007");
//                //executor.getGroup().progress(name);
//                Iterator it = executor.getExpandedModules().iterator();
//                while (it.hasNext()) {
//                    String module = (String) it.next();
//                    if (".".equals(module)) {  // NOI18N
//                        checkedOutProjects = ProjectUtilities.scanForProjects(fo);
//                        break;
//                    } else {
//                        FileObject subfolder = fo.getFileObject(module);
//                        if (subfolder != null) {
//                            executor.getGroup().progress(name);
//                            checkedOutProjects.addAll(ProjectUtilities.scanForProjects(subfolder));
//                        }
//                    }
//                }
            }

            panel = new CheckoutCompletedPanel();
            panel.openButton.addActionListener(this);
            panel.createButton.addActionListener(this);
            panel.closeButton.addActionListener(this);
            panel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            panel.againCheckBox.setVisible(openProject == false);
            String title = NbBundle.getMessage(CheckoutAction.class, "BK3008");
            DialogDescriptor descriptor = new DialogDescriptor(panel, title);
            descriptor.setModal(true);

            // move buttons from dialog to descriptor
            panel.remove(panel.openButton);
            panel.remove(panel.createButton);
            panel.remove(panel.closeButton);

            Object[] options = null;
            if (checkedOutProjects.size() > 1) {
                String msg = NbBundle.getMessage(CheckoutAction.class, "BK3009", new Integer(checkedOutProjects.size()));
                panel.jLabel1.setText(msg);
                options = new Object[] {
                    panel.openButton,
                    panel.closeButton
                };
            } else if (checkedOutProjects.size() == 1) {
                Project project = (Project) checkedOutProjects.iterator().next();
                projectToBeOpened = project;
                ProjectInformation projectInformation = ProjectUtils.getInformation(project);
                String projectName = projectInformation.getDisplayName();
                String msg = NbBundle.getMessage(CheckoutAction.class, "BK3011", projectName);
                panel.jLabel1.setText(msg);
                panel.openButton.setText(NbBundle.getMessage(CheckoutAction.class, "BK3012"));
                options = new Object[] {
                    panel.openButton,
                    panel.closeButton
                };
            } else {
                String msg = NbBundle.getMessage(CheckoutAction.class, "BK3010");
                panel.jLabel1.setText(msg);
                options = new Object[] {
                    panel.createButton,
                    panel.closeButton
                };

            }

            descriptor.setMessageType(DialogDescriptor.INFORMATION_MESSAGE);
            descriptor.setOptions(options);
            descriptor.setClosingOptions(options);
            descriptor.setHelpCtx(new HelpCtx(CheckoutCompletedPanel.class));
            dialog = DialogDisplayer.getDefault().createDialog(descriptor);
            dialog.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CheckoutAction.class, "ACSD_CheckoutCompleted_Dialog"));

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dialog.setVisible(true);
                }
            });
        }

        /**
         * Refreshes statuses of this folder and all its parent folders up to filesystem root.
         * 
         * @param folder folder to refresh
         */ 
        private void refreshRecursively(File folder) {
            if (folder == null) return;
            refreshRecursively(folder.getParentFile());
            Subversion.getInstance().getStatusCache().refresh(folder, FileStatusCache.REPOSITORY_STATUS_UNKNOWN);
        }

        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            dialog.setVisible(false);
            if (panel.openButton.equals(src)) {
                // show project chooser
                if (projectToBeOpened == null) {
                    JFileChooser chooser = ProjectChooser.projectChooser();
                    chooser.setCurrentDirectory(workingFolder);
                    chooser.setMultiSelectionEnabled(true);
                    chooser.showOpenDialog(null);
                    File [] projectDirs = chooser.getSelectedFiles();
                    for (int i = 0; i < projectDirs.length; i++) {
                        File projectDir = projectDirs[i];
                        FileObject projectFolder = FileUtil.toFileObject(projectDir);
                        if (projectFolder != null) {
                            try {
                                Project p = ProjectManager.getDefault().findProject(projectFolder);
                                if (p != null) {
                                    openProject(p);
                                }
                            } catch (IOException e1) {
                                ErrorManager err = ErrorManager.getDefault();
                                err.annotate(e1, NbBundle.getMessage(CheckoutAction.class, "BK3014", projectFolder));
                                err.notify(e1);
                            }
                        }
                    }
                } else {
                    if (projectToBeOpened == null) return; 
                    openProject(projectToBeOpened);
                }

            } else if (panel.createButton.equals(src)) {
                ProjectUtilities.newProjectWizard(workingFolder);
            }
            if (panel.againCheckBox.isSelected()) {
               HistorySettings.setFlag(HistorySettings.PROP_SHOW_CHECKOUT_COMPLETED, 0);
            }
        }

        private void openProject(Project p) {
            Project[] projects = new Project[] {p};
            OpenProjects.getDefault().open(projects, false);

            // set as main project and expand
            ContextAwareAction action = (ContextAwareAction) CommonProjectActions.setAsMainProjectAction();
            Lookup ctx = Lookups.singleton(p);
            Action ctxAction = action.createContextAwareInstance(ctx);
            ctxAction.actionPerformed(null);
            ProjectUtilities.selectAndExpandProject(p);
        }
    }    
}
