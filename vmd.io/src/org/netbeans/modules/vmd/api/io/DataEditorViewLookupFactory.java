/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vmd.api.io;

import java.util.Collection;

/**
 * This interface is used for obtaining objects that should be added into the lookup of a TopComponent related to a particular view.
 *
 * @author David Kaspar
 */
public interface DataEditorViewLookupFactory {

    /**
     * This method is called to obtains a collection of objects.
     * These objects will be automatically added into the lookup of a TopComponent which is related to a particular view
     * For example: it could a PaletteController instance or NavigatorLookupHint.
     * @param context the data object context
     * @param view the view
     * @return the collection of objects
     */
    public Collection<? extends Object> getLookupObjects (DataObjectContext context, DataEditorView view);

}
