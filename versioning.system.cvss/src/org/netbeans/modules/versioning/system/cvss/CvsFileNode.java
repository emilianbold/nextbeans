/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.versioning.system.cvss;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.InstanceContent;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents real or virtual (non-local) file.
 * 
 * @author Maros Sandor
 */
public class CvsFileNode {

    private final File file;

    public CvsFileNode(File file) {
        this.file = file;
    }

    public String getName() {
        return file.getName();
    }

    
    public FileInformation getInformation() {
        return CvsVersioningSystem.getInstance().getStatusCache().getStatus(file); 
    }

    public File getFile() {
        return file;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof CvsFileNode && file.equals(((CvsFileNode) o).file);
    }

    public int hashCode() {
        return file.hashCode();
    }

    public FileObject getFileObject() {
        return FileUtil.toFileObject(file);
    }

    public Object[] getLookupObjects() {
        List list = new ArrayList(2);
        list.add(this);
        FileObject fo = getFileObject();
        if (fo != null) {
            list.add(fo);
        }
        return list.toArray(new Object[list.size()]);
    }


}
