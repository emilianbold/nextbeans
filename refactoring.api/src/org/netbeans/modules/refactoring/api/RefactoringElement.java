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
package org.netbeans.modules.refactoring.api;

import org.netbeans.modules.refactoring.spi.RefactoringElementImplementation;
import org.netbeans.modules.refactoring.spi.ui.TreeElement;
import org.netbeans.modules.refactoring.spi.ui.TreeElementFactory;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.Lookup;

/** Interface representing a refactoring element (object affected by a refactoring)
 * returned in a collection from {@link org.netbeans.modules.refactoring.api.AbstractRefactoring#prepare} operation.
 * <p>
 *
 * @see RefactoringElementImplementation
 * @author Martin Matula
 */
public final class RefactoringElement {
    /** Status corresponding to a normal element */
    public static final int NORMAL = 0;
    /** Status corresponding to an element that has a warning associated with it */
    public static final int WARNING = 1;
    /** Status flag that indicates that the element cannot be enabled (if a fatal
     * problem is associated with it) */
    public static final int GUARDED = 2;
    /** This element is in read-only file */
    public static final int READ_ONLY = 3;
    
    // delegate
    final RefactoringElementImplementation impl;
    
    RefactoringElement(RefactoringElementImplementation impl) {
        assert impl != null;
        this.impl = impl;
    }
    
    /** Returns text describing the refactoring element.
     * @return Text.
     */
    public String getText() {
        return impl.getText();
    }
    
    /** Returns text describing the refactoring formatted for display (using HTML tags).
     * @return Formatted text.
     */
    public String getDisplayText() {
        return impl.getDisplayText();
    }
    
    /** Indicates whether this refactoring element is enabled.
     * @return <code>true</code> if this element is enabled, otherwise <code>false</code>.
     */
    public boolean isEnabled() {
        return impl.isEnabled();
    }
    
    /** Enables/disables this element.
     * @param enabled If <code>true</code> the element is enabled, otherwise it is disabled.
     */
    public void setEnabled(boolean enabled) {
        impl.setEnabled(enabled);
    }
    
    /** 
     * Returns Lookup associated with this element.
     * Lookup items might be used by TreeElementFactories to build refactoring
     * preview trees.
     * @see org.netbeans.modules.refactoring.spi.ui.TreeElement
     * @see org.netbeans.modules.refactoring.spi.ui.TreeElementFactoryImplementation 
     * @return Lookup.
     */
    public Lookup getLookup() {
        return impl.getLookup();
    }
    
    /** Returns file that the element affects (relates to)
     * @return File
     */
    public FileObject getParentFile() {
        return impl.getParentFile();
    }
    
    /** Returns position bounds of the text to be affected by this refactoring element.
     * @return position bounds
     */
    public PositionBounds getPosition() {
        return impl.getPosition();
    }
    
    /** Returns the status of this refactoring element (whether it is a normal element,
     * or a warning.
     * @return Status of this element.
     */
    public int getStatus() {
        return impl.getStatus();
    }
    
    /**
     * Shows this element in refactoring preview are
     * @see org.netbeans.modules.refactoring.api.ui.UI#setComponentForRefactoringPreview
     */
    public void showPreview() {
        impl.showPreview();
    }
    
    /**
     * opens this RefactoringElement in the editor
     */
    public void openInEditor() {
        impl.openInEditor();
    }
}
