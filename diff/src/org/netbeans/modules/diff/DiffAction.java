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

package org.netbeans.modules.diff;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.options.SystemOption;
import org.openide.util.RequestProcessor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.TopComponent;

//import org.netbeans.modules.diff.cmdline.DiffCommand;
//import org.netbeans.modules.vcscore.diff.AbstractDiff;

import org.netbeans.api.diff.*;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;

//import org.netbeans.modules.diff.builtin.provider.BuiltInDiffProvider;
//import org.netbeans.modules.diff.cmdline.CmdlineDiffProvider;
//import org.netbeans.modules.diff.builtin.visualizer.GraphicalDiffVisualizer;

//import org.netbeans.modules.diff.io.diff.*;

/**
 * Diff Action. It gets the default diff visualizer and diff provider if needed
 * and display the diff visual representation of two files selected in the IDE.
 *
 * @author  Martin Entlicher
 */
public class DiffAction extends NodeAction {

    /** Creates new DiffAction */
    public DiffAction() {
    }
    
    public String getName() {
        return NbBundle.getMessage(DiffAction.class, "CTL_DiffActionName");
    }
    
    public boolean enable(Node[] nodes) {
        //System.out.println("DiffAction.enable() = "+(nodes.length == 2));
        if (nodes.length == 2) {
            DataObject do1 = (DataObject) nodes[0].getCookie(DataObject.class);
            DataObject do2 = (DataObject) nodes[1].getCookie(DataObject.class);
            if (do1 != null && do2 != null) {
                if (do1.getPrimaryFile().isData() && do2.getPrimaryFile().isData()) {
                    Diff d = Diff.getDefault();
                    return d != null;
                }
            }
        }
        return false;
    }
    
    /**
     * This action should not be run in AWT thread, because it opens streams
     * to files.
     * @return true not to run in AWT thread!
     */
    protected boolean asynchronous() {
        return true;
    }
    
    public void performAction(Node[] nodes) {
        ArrayList fos = new ArrayList();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
            if (dd != null) fos.add(dd.getPrimaryFile());
        }
        if (fos.size() < 2) return ;
        final FileObject fo1 = (FileObject) fos.get(0);
        final FileObject fo2 = (FileObject) fos.get(1);
        performAction(fo1, fo2);
    }
    
    /**
     * Shows the diff between two FileObject objects.
     * This is expected not to be called in AWT thread.
     */
    public static void performAction(FileObject fo1, FileObject fo2) {
        //System.out.println("performAction("+fo1+", "+fo2+")");
        //doDiff(fo1, fo2);
        Diff diff = Diff.getDefault();
        //System.out.println("dv = "+dv);
        if (diff == null) {
            DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(NbBundle.getMessage(DiffAction.class,
                    "MSG_NoDiffVisualizer")));
            return ;
        }
        Component tp;
        try {
            String path1 = null;
            String path2 = null;
            // XXX should be using FileUtil.getFileDisplayName here
            File file1 = FileUtil.toFile(fo1);
            if (file1 != null) {
                path1 = file1.getAbsolutePath();
            }
            if (path1 == null) {
                path1 = fo1.getPath();
            }
            File file2 = FileUtil.toFile(fo2);
            if (file2 != null) {
                path2 = file2.getAbsolutePath();
            }
            if (path2 == null) {
                path2 = fo2.getPath();
            }
            tp = diff.createDiff(fo1.getNameExt(), path1,
                                 new InputStreamReader(fo1.getInputStream()),
                                 fo2.getNameExt(), path2,
                                 new InputStreamReader(fo2.getInputStream()), fo1.getMIMEType());
        } catch (IOException ioex) {
            ErrorManager.getDefault().notify(ioex);
            return ;
        }
        //System.out.println("tp = "+tp);
        if (tp != null) {
            final Component ftp = tp;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (ftp instanceof TopComponent) {
                        ((TopComponent) ftp).open();
                        ((TopComponent) ftp).requestActive();
                    } else {
                        ftp.setVisible(true);
                        ftp.requestFocusInWindow();
                    }
                }
            });
        }
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(DiffAction.class);
    }

}
