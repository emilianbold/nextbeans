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
 *
 */

package org.netbeans.modules.vmd.midp.screen;

import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.presenters.InfoPresenter;
import org.netbeans.modules.vmd.api.model.presenters.InfoPresenter.NameType;
import org.netbeans.modules.vmd.api.screen.resource.ScreenResourceCategoryDescriptor;
import org.netbeans.modules.vmd.api.screen.resource.ScreenResourceItemPresenter;

/**
 * @author Karol Harezlak
 */
public class ItemCommandSRItemPresenter extends ScreenResourceItemPresenter {
    
    public ItemCommandSRItemPresenter() {
    }
    
    @Override
    public ScreenResourceCategoryDescriptor getCategoryDescriptor() {
        return ScreenResourceCategoryDescriptorSupport.ASSIGNED_ITEM_COMMANDS;
    }
    
    @Override
    public boolean isActiveFor(DesignComponent component) {
        DesignComponent thisComponent = getComponent();
        
        if (thisComponent == null || component == null)
            return false;
        
        for (DesignComponent c : component.getComponents()) {
            if (thisComponent.getParentComponent() == c)
                return true;
        }
        
        return false;
    }
    
    public InfoPresenter.NameType getNameType() {
        return InfoPresenter.NameType.TERTIARY;
    }

}
