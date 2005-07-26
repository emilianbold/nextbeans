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

package org.netbeans.modules.versioning.system.cvss.ui.history;

import org.openide.windows.TopComponent;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.io.File;

/**
 * Search History component.
 *  
 * @author Maros Sandor
 */
public class SearchHistoryTopComponent extends TopComponent {
    
    private File[] roots;

    public SearchHistoryTopComponent() {
    }
    
    public SearchHistoryTopComponent(File [] roots) {
        this.roots = roots;
        initComponents();
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(SearchHistoryTopComponent.class, "ACSN_SearchHistoryT_Top_Component")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchHistoryTopComponent.class, "ACSD_SearchHistoryT_Top_Component")); // NOI18N
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        SearchCriteriaPanel scp = new SearchCriteriaPanel(roots);
        SearchHistoryPanel shp = new SearchHistoryPanel(roots, scp);
        shp.setTopPanel(scp);
        add(shp);
    }

    public int getPersistenceType(){
       return TopComponent.PERSISTENCE_NEVER;
    }
    
    protected void componentClosed() {
//       ((DiffMainPanel) getComponent(0)).componentClosed();
       super.componentClosed();
    }
    
    protected String preferredID(){
       return "SearchHistoryTopComponent";    //NOI18N       
    }
}
