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

package org.netbeans.core.projects;

import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.InstanceCookie;
import org.openide.nodes.*;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.netbeans.beaninfo.editors.ListImageEditor;

import java.awt.Image;
import java.beans.PropertyEditor;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.openide.actions.CustomizeBeanAction;
import org.openide.util.actions.SystemAction;

/** Filters nodes under the session node (displayed in Options dialog), adds special
 * properties to Nodes of particular settings to show/edit positions wher the
 * setting is defined on DefaultFileSystem.
 *
 * @author  Vitezslav Stejskal
 */
public final class SettingChildren extends FilterNode.Children {
    /** Name of Node.Property indicating if setting is defined on Project layer */
    public static final String PROP_INDICATOR = "Indicator"; // NOI18N
    /** Name of Node.Property showing status of Project layer according to the setting */
    public static final String PROP_LAYER_PROJECT = "Project-Layer"; // NOI18N
    /** Name of Node.Property showing status of Session layer according to the setting */
    public static final String PROP_LAYER_SESSION = "Session-Layer"; // NOI18N
    /** Name of Node.Property showing status of Modules layer according to the setting */
    public static final String PROP_LAYER_MODULES = "Modules-Layer"; // NOI18N

    public SettingChildren (Node original) {
        super (original);
    }

    protected Node copyNode (Node node) {
        boolean filter = false;
        try {
            DataObject d = (DataObject) node.getCookie (DataObject.class);
            if (d != null) {
                InstanceCookie.Of inst = (InstanceCookie.Of)d.getCookie(InstanceCookie.Of.class);
                if (inst != null && (inst.instanceOf(Node.class) || inst.instanceOf(Node.Handle.class))) {
                    // This is just a node, not a real setting. E.g. ModuleNode, LoaderPoolNode. As such,
                    // it itself should not display any origin information, it would make no sense. However
                    // its children might have a legitimate DataObject cookie from the SFS.
                    d = null;
                }
            }
            DataFolder folder = (DataFolder) node.getCookie (DataFolder.class);
            FileSystem fs = d == null || folder != null ? null : d.getPrimaryFile ().getFileSystem ();
            filter = fs == null ? false : fs.equals (Repository.getDefault ().getDefaultFileSystem ());
        } catch (FileStateInvalidException e) {
            // ignore
        }

        return filter ? new SettingFilterNode (node) : 
            node.isLeaf() ? node.cloneNode() : new TrivialFilterNode(node);
    }
    
    private static final class TrivialFilterNode extends FilterNode {
        public TrivialFilterNode(Node n) {
            super(n, new SettingChildren(n));
        }
        // #17920: Index cookie works only when equality works
        public boolean equals(Object o) {
            return this == o || getOriginal().equals(o) || (o != null && o.equals(getOriginal()));
        }
        public int hashCode() {
            return getOriginal().hashCode();
        }
    }

    /** Property allowing display/manipulation of setting status for one specific layer. */
    public static class FileStateProperty extends PropertySupport {
        static final int ACTION_DEFINE = 1;
        static final int ACTION_REVERT = 2;
        static final int ACTION_DELETE = 3;
        
        private FileObject primaryFile = null;
        private int layer;

        public FileStateProperty (String name) {
            this (null, 0, name, true);
        }

        public FileStateProperty (FileObject primaryFile, int layer, String name, boolean readonly) {
            super (name, Integer.class,
                NbBundle.getMessage (FileStateProperty.class, "LBL_FSP_" + name), // NOI18N
                NbBundle.getMessage (FileStateProperty.class, "LBL_FSP_Desc_" + name), // NOI18N
                true, !readonly);
            
            this.primaryFile = primaryFile;
            this.layer = layer;

            setValue (ListImageEditor.PROP_VALUES, new Integer [] {
                new Integer (FileStateManager.FSTATE_DEFINED),
                new Integer (FileStateManager.FSTATE_IGNORED),
                new Integer (FileStateManager.FSTATE_INHERITED),
                new Integer (FileStateManager.FSTATE_UNDEFINED)
            });

            setValue (ListImageEditor.PROP_IMAGES, new Image [] {
                Utilities.loadImage ("org/netbeans/core/resources/setting-defined.gif"), // NOI18N
                Utilities.loadImage ("org/netbeans/core/resources/setting-ignored.gif"), // NOI18N
                Utilities.loadImage ("org/netbeans/core/resources/setting-inherited.gif"), // NOI18N
                Utilities.loadImage ("org/netbeans/core/resources/empty.gif") // NOI18N
            });
        }

        public boolean canWrite () {
            if (!super.canWrite ())
                return false;
            
            Integer val = null;
            try {
                val = (Integer) getValue ();
            } catch (Exception e) {
                // ignore it, will be handled later
            }
            
            return val != null &&
                val.intValue () != FileStateManager.FSTATE_DEFINED &&
                (layer != FileStateManager.LAYER_MODULES || val.intValue () != FileStateManager.FSTATE_UNDEFINED);
        }

        public Object getValue () throws IllegalAccessException, InvocationTargetException {
            FileStateManager fsm = FileStateManager.getDefault ();
            return new Integer (fsm.getFileState (primaryFile, layer));
        }

        public void setValue (Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            FileStateManager fsm = FileStateManager.getDefault ();
            int action = ((Integer) val).intValue ();
            
            try {
                switch (action) {
                    case ACTION_DEFINE:
                    case ACTION_REVERT:
                        boolean above = false;
                        boolean go = true;

                        for (int i = 0; i < layer; i++) {
                            int state = fsm.getFileState (primaryFile, i);
                            if (state == FileStateManager.FSTATE_DEFINED) {
                                // warn user, that above defined files will be removed

                                NotifyDescriptor nd = new NotifyDescriptor.Confirmation (
                                    NbBundle.getMessage (SettingChildren.class, "MSG_ask_remove_above_defined_files"), // NOI18N
                                    NotifyDescriptor.YES_NO_OPTION);

                                Object answer = org.openide.DialogDisplayer.getDefault ().notify (nd);
                                if (answer.equals (NotifyDescriptor.NO_OPTION))
                                    go = false;

                                break;
                            }
                        }

                        if (go)
                            fsm.define (primaryFile, layer, action == ACTION_REVERT);

                        break;

                    case ACTION_DELETE:
                        fsm.delete (primaryFile, layer);
                        break;

                    default:
                        throw new IllegalArgumentException ("Required file state change isn't allowed. Action=" + action); // NOI18N
                }
            } catch (java.io.IOException e) {
                ErrorManager.getDefault ().notify (e);
            }
        }

        public PropertyEditor getPropertyEditor () {
            return new FileStateEditor ();
        }
        
        public String getShortDescription () {
            Integer val = null;
            String s = null;

            if (primaryFile != null) {
                try {
                    val = (Integer) getValue ();
                } catch (Exception e) {
                    // ignore it, will be handled later
                }

                switch (val == null ? -1 : val.intValue()) {
                    case FileStateManager.FSTATE_DEFINED:
                        s = NbBundle.getMessage (SettingChildren.class, "LBL_fstate_defined");
                        break;
                    case FileStateManager.FSTATE_IGNORED:
                        s = NbBundle.getMessage (SettingChildren.class, "LBL_fstate_ignored");
                        break;
                    case FileStateManager.FSTATE_INHERITED:
                        s = NbBundle.getMessage (SettingChildren.class, "LBL_fstate_inherited");
                        break;
                    case FileStateManager.FSTATE_UNDEFINED:
                        s = NbBundle.getMessage (SettingChildren.class, "LBL_fstate_undefined");
                        break;
                }
            }
            else {
                s = super.getShortDescription ();
            }
            return s == null || s.length () == 0 ? null : s;
        }
    }

    /** Read-only property indicating if specific setting is defined on Project layer. */
    public static class IndicatorProperty extends PropertySupport {
        private FileObject primaryFile = null;

        public IndicatorProperty () {
            this (null);
        }

        public IndicatorProperty (FileObject primaryFile) {
            super (PROP_INDICATOR, Integer.class,
                NbBundle.getMessage (IndicatorProperty.class, "LBL_IndicatorProperty_Name"), // NOI18N
                NbBundle.getMessage (IndicatorProperty.class, "LBL_IndicatorProperty_Description"), // NOI18N
                true, false);

            this.primaryFile = primaryFile;

            setValue (ListImageEditor.PROP_VALUES, new Integer [] {
                new Integer (0),
                new Integer (1)
            });

            setValue (ListImageEditor.PROP_IMAGES, new Image [] {
                Utilities.loadImage ("org/netbeans/core/resources/empty.gif"), // NOI18N
                Utilities.loadImage ("org/netbeans/core/resources/project.gif") // NOI18N
            });
        }

        public Object getValue () throws IllegalAccessException, InvocationTargetException {
            FileStateManager fsm = FileStateManager.getDefault ();
            if (FileStateManager.FSTATE_DEFINED == fsm.getFileState (primaryFile, 
                    FileStateManager.LAYER_PROJECT)) {
                return new Integer (1);
            }

            return new Integer (0);
        }

        public void setValue (Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            throw new IllegalAccessException ();
        }

        public PropertyEditor getPropertyEditor () {
            return new ListImageEditor ();
        }

        public String getShortDescription () {
            String s = null;
            if (primaryFile != null) {
                Integer val = null;
                try {
                    val = (Integer) getValue ();
                } catch (Exception e) {
                    // ignore it, will be handled later
                }
                switch (val == null ? -1 : val.intValue ()) {
                    case 0: // session setting
                        s = NbBundle.getMessage (SettingChildren.class, "LBL_indicator_session");
                        break; 
                    case 1: // project opetion
                        s = NbBundle.getMessage (SettingChildren.class, "LBL_indicator_project");
                        break; 
                }
            } else {
                s = super.getShortDescription ();
            }
            return s == null || s.length () == 0 ? null : s; // no description for empty strings
        }
    }

    /** Filter node used for adding special status related properties to setting nodes. */
    private static final class SettingFilterNode extends FilterNode {
        private FSL weakL = null;
        
        public SettingFilterNode (Node original) {
            super (original);
	    // need to keep the values in this FilterNode, not delegates
	    disableDelegation(DELEGATE_SET_VALUE | DELEGATE_GET_VALUE);

            FileObject pf = ((DataObject) getCookie (DataObject.class)).getPrimaryFile ();
            weakL = new FSL (this);
            FileStateManager.getDefault ().addFileStatusListener (weakL, pf);

            specialProp (new IndicatorProperty (pf));
            specialProp (new FileStateProperty (pf, FileStateManager.LAYER_PROJECT, PROP_LAYER_PROJECT, false));
            specialProp (new FileStateProperty (pf, FileStateManager.LAYER_SESSION, PROP_LAYER_SESSION, false));
            specialProp (new FileStateProperty (pf, FileStateManager.LAYER_MODULES, PROP_LAYER_MODULES, false));
        }
        
        /** Registers special property.
         */
        private void specialProp (Node.Property p) {
            setValue (p.getName (), p);
        }
     
        // #17920: Index cookie works only when equality works
        public boolean equals(Object o) {
            return this == o || getOriginal().equals(o) || (o != null && o.equals(getOriginal()));
        }
        public int hashCode() {
            return getOriginal().hashCode();
        }

        // #24766 Exclude Customize Bean action.
        /** Overrides superclass method, excludes the CustomizeBeanAction from the node. */
        public SystemAction[] getActions() {
            SystemAction[] as = super.getActions();
            List actions = java.util.Arrays.asList(as);
            SystemAction customizeBean = SystemAction.get(CustomizeBeanAction.class);
            if(actions.contains(customizeBean)) {
                actions = new ArrayList(actions); // to be mutable
                actions.remove(customizeBean);
                return (SystemAction[])actions.toArray(new SystemAction[0]);
            } else {
                return as;
            }
        }

        private static class FSL implements FileStateManager.FileStatusListener {
            WeakReference node = null;
            public FSL (SettingFilterNode sfn) {
                node = new WeakReference (sfn);
            }
            public void fileStatusChanged (FileObject mfo) {
                SettingFilterNode n = (SettingFilterNode) node.get ();
                if (n == null) {
                    FileStateManager.getDefault ().removeFileStatusListener (this, null);
                    return;
                }
                
                n.firePropertyChange (PROP_LAYER_PROJECT, null, null);
                n.firePropertyChange (PROP_LAYER_SESSION, null, null);
                n.firePropertyChange (PROP_LAYER_MODULES, null, null);
                n.firePropertyChange (PROP_INDICATOR, null, null);
            }
        }
    }
}
