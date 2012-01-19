/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.form.layoutdesign;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.openide.filesystems.FileUtil;

public class ALT_Bug204814Test extends LayoutTestCase {

    public ALT_Bug204814Test(String name) {
        super(name);
        try {
            className = this.getClass().getName();
            className = className.substring(className.lastIndexOf('.') + 1, className.length());
            startingFormFile = FileUtil.toFileObject(new File(url.getFile() + goldenFilesPath + className + "-StartingForm.form").getCanonicalFile());
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    /**
     * Move the splitpane so it left-aligns with the radiobuttons and checkboxes,
     * and top-aligns with jButton3 and jTextField3.
     */
    public void doChanges0() {
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 980, 637));
        contInterior.put("Form", new Rectangle(0, 0, 980, 637));
        compBounds.put("jPanel1", new Rectangle(12, 13, 952, 611));
        baselinePosition.put("jPanel1-952-611", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(13, 14, 950, 609));
        compBounds.put("jScrollPane1", new Rectangle(25, 27, 171, 136));
        baselinePosition.put("jScrollPane1-171-136", new Integer(0));
        compBounds.put("jButton1", new Rectangle(25, 239, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jTextField1", new Rectangle(116, 240, 69, 22));
        baselinePosition.put("jTextField1-69-22", new Integer(16));
        compBounds.put("jButton2", new Rectangle(25, 271, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(116, 272, 69, 22));
        baselinePosition.put("jTextField2-69-22", new Integer(16));
        compBounds.put("jButton3", new Rectangle(25, 303, 79, 25));
        baselinePosition.put("jButton3-79-25", new Integer(17));
        compBounds.put("jTextField3", new Rectangle(116, 304, 69, 22));
        baselinePosition.put("jTextField3-69-22", new Integer(16));
        compBounds.put("jRadioButton3", new Rectangle(204, 44, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(204, 74, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        compBounds.put("jRadioButton2", new Rectangle(204, 271, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(211, 113, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(322, 557, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jRadioButton1", new Rectangle(204, 239, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jSplitPane1", new Rectangle(340, 108, 150, 139));
        baselinePosition.put("jSplitPane1-150-139", new Integer(-1));
        compMinSize.put("jPanel1", new Dimension(491, 570));
        compBounds.put("jPanel1", new Rectangle(12, 13, 952, 611));
        compPrefSize.put("jPanel1", new Dimension(952, 611));
        prefPaddingInParent.put("jPanel1-jRadioButton3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        compMinSize.put("Form", new Dimension(976, 596));
        compBounds.put("Form", new Rectangle(0, 0, 980, 637));
        prefPaddingInParent.put("Form-jPanel1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jPanel1", new Dimension(952, 611));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
// > START MOVING
        baselinePosition.put("jSplitPane1-150-139", new Integer(-1));
        {
            String[] compIds = new String[]{
                "jSplitPane1"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(340, 108, 150, 139)
            };
            Point hotspot = new Point(371, 201);
            ld.startMoving(compIds, bounds, hotspot);
        }
// < START MOVING
        prefPaddingInParent.put("jPanel1-jSplitPane1-1-0", new Integer(13)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-1-1", new Integer(13)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jRadioButton3-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox2-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton1-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField1-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton2-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField2-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton2-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField2-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton2-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-0", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jButton3-jSplitPane1-0-0-1", new Integer(12)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField3-jSplitPane1-0-0-1", new Integer(12)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jSplitPane1-0-0-2", new Integer(24)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
// > MOVE
        {
            Point p = new Point(234, 398);
            String containerId = "jPanel1";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(204, 303, 150, 139)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("jPanel1-jSplitPane1-1-0", new Integer(13)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-1-1", new Integer(13)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jRadioButton3-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox2-jSplitPane1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox2-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox3-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox1-jSplitPane1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton1-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField1-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-1", new Integer(13)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-2", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton2-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField2-jSplitPane1-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jButton2-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jTextField2-1-0-0", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jRadioButton2-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-0", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jButton3-jSplitPane1-0-0-1", new Integer(12)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField3-jSplitPane1-0-0-1", new Integer(12)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jSplitPane1-0-0-2", new Integer(24)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
// > MOVE
        {
            Point p = new Point(235, 398);
            String containerId = "jPanel1";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(204, 303, 150, 139)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPaddingInParent.put("jPanel1-jRadioButton3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jCheckBox3-jSplitPane1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 980, 637));
        contInterior.put("Form", new Rectangle(0, 0, 980, 637));
        compBounds.put("jPanel1", new Rectangle(12, 13, 956, 611));
        baselinePosition.put("jPanel1-956-611", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(13, 14, 954, 609));
        compBounds.put("jScrollPane1", new Rectangle(25, 27, 171, 136));
        baselinePosition.put("jScrollPane1-171-136", new Integer(0));
        compBounds.put("jButton1", new Rectangle(25, 239, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jTextField1", new Rectangle(116, 240, 69, 22));
        baselinePosition.put("jTextField1-69-22", new Integer(16));
        compBounds.put("jButton2", new Rectangle(25, 271, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(116, 272, 69, 22));
        baselinePosition.put("jTextField2-69-22", new Integer(16));
        compBounds.put("jButton3", new Rectangle(25, 303, 79, 25));
        baselinePosition.put("jButton3-79-25", new Integer(17));
        compBounds.put("jTextField3", new Rectangle(116, 304, 69, 22));
        baselinePosition.put("jTextField3-69-22", new Integer(16));
        compBounds.put("jRadioButton3", new Rectangle(208, 44, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(208, 74, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        compBounds.put("jRadioButton2", new Rectangle(208, 271, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(215, 113, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(326, 557, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jRadioButton1", new Rectangle(208, 239, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jSplitPane1", new Rectangle(208, 303, 150, 139));
        baselinePosition.put("jSplitPane1-150-139", new Integer(-1));
        compMinSize.put("jPanel1", new Dimension(416, 458));
        compBounds.put("jPanel1", new Rectangle(12, 13, 956, 611));
        compPrefSize.put("jPanel1", new Dimension(956, 611));
        prefPaddingInParent.put("jPanel1-jRadioButton3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        compMinSize.put("Form", new Dimension(980, 484));
        compBounds.put("Form", new Rectangle(0, 0, 980, 637));
        prefPaddingInParent.put("Form-jPanel1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jPanel1", new Dimension(956, 611));
        compBounds.put("Form", new Rectangle(0, 0, 980, 637));
        contInterior.put("Form", new Rectangle(0, 0, 980, 637));
        compBounds.put("jPanel1", new Rectangle(12, 13, 956, 611));
        baselinePosition.put("jPanel1-956-611", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(13, 14, 954, 609));
        compBounds.put("jScrollPane1", new Rectangle(25, 27, 171, 136));
        baselinePosition.put("jScrollPane1-171-136", new Integer(0));
        compBounds.put("jButton1", new Rectangle(25, 239, 79, 25));
        baselinePosition.put("jButton1-79-25", new Integer(17));
        compBounds.put("jTextField1", new Rectangle(116, 240, 69, 22));
        baselinePosition.put("jTextField1-69-22", new Integer(16));
        compBounds.put("jButton2", new Rectangle(25, 271, 79, 25));
        baselinePosition.put("jButton2-79-25", new Integer(17));
        compBounds.put("jTextField2", new Rectangle(116, 272, 69, 22));
        baselinePosition.put("jTextField2-69-22", new Integer(16));
        compBounds.put("jButton3", new Rectangle(25, 303, 79, 25));
        baselinePosition.put("jButton3-79-25", new Integer(17));
        compBounds.put("jTextField3", new Rectangle(116, 304, 69, 22));
        baselinePosition.put("jTextField3-69-22", new Integer(16));
        compBounds.put("jRadioButton3", new Rectangle(208, 44, 107, 25));
        baselinePosition.put("jRadioButton3-107-25", new Integer(17));
        compBounds.put("jCheckBox2", new Rectangle(208, 74, 93, 25));
        baselinePosition.put("jCheckBox2-93-25", new Integer(17));
        compBounds.put("jRadioButton2", new Rectangle(208, 271, 107, 25));
        baselinePosition.put("jRadioButton2-107-25", new Integer(17));
        compBounds.put("jCheckBox3", new Rectangle(215, 113, 93, 25));
        baselinePosition.put("jCheckBox3-93-25", new Integer(17));
        compBounds.put("jCheckBox1", new Rectangle(326, 557, 93, 25));
        baselinePosition.put("jCheckBox1-93-25", new Integer(17));
        compBounds.put("jRadioButton1", new Rectangle(208, 239, 107, 25));
        baselinePosition.put("jRadioButton1-107-25", new Integer(17));
        compBounds.put("jSplitPane1", new Rectangle(208, 303, 150, 139));
        baselinePosition.put("jSplitPane1-150-139", new Integer(-1));
        compMinSize.put("jPanel1", new Dimension(416, 458));
        compBounds.put("jPanel1", new Rectangle(12, 13, 956, 611));
        prefPaddingInParent.put("jPanel1-jRadioButton3-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton2-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jRadioButton1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jSplitPane1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel1-jCheckBox1-0-1", new Integer(8)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-0", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-1", new Integer(9)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-2", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSplitPane1-jCheckBox1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compMinSize.put("Form", new Dimension(980, 484));
        compBounds.put("Form", new Rectangle(0, 0, 980, 637));
        prefPaddingInParent.put("Form-jPanel1-0-1", new Integer(12)); // parentId-compId-dimension-compAlignment
        compPrefSize.put("jPanel1", new Dimension(956, 611));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }
}
