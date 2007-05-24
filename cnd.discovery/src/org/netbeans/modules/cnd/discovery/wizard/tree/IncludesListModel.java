/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.discovery.wizard.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Alexander Simon
 */
public class IncludesListModel implements ListModel {
    private NodeConfigurationImpl configuration;
    private List<String> names;
    
    public IncludesListModel(NodeConfigurationImpl configuration, boolean resulting){
        this.configuration = configuration;
        names = new ArrayList<String>(configuration.getUserInludePaths(resulting));
        Collections.<String>sort(names);
    }
    
    public int getSize() {
        return names.size();
    }
    
    public Object getElementAt(int index) {
        return names.get(index);
    }
    
    public NodeConfigurationImpl getNodeConfiguration(){
        return configuration;
    }
    
    public void addListDataListener(ListDataListener l) {
    }
    
    public void removeListDataListener(ListDataListener l) {
    }
}
