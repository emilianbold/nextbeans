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

package org.netbeans.modules.j2ee.ddloaders.multiview;

import org.netbeans.modules.xml.multiview.ui.SectionView;

/**
 * @author pfiala
 */
public class CmpRelationshipsMultiViewElement extends EjbJarMultiviewElement {

    public CmpRelationshipsMultiViewElement(EjbJarMultiViewDataObject dataObject) {
        super(dataObject);
    }

    protected SectionView createView() {
        return new CmpRelationshipsView(dataObject);
    }
}
