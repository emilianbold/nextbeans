/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include the
 * License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by Oracle
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only
 * the GPL Version 2, indicate your decision by adding "[Contributor] elects to
 * include this software in this distribution under the [CDDL or GPL Version 2]
 * license." If you do not indicate a single choice of license, a recipient has
 * the option to distribute your version of this file under either the CDDL, the
 * GPL Version 2 or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 */
package org.netbeans.modules.form.layoutdesign;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.openide.filesystems.FileUtil;

public class ALT_ParallelPosition25Test extends LayoutTestCase {

    public ALT_ParallelPosition25Test(String name) {
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
     * Move jRadioButton1 slightly to the left to align with the other radio
     * buttons and bottom align with jPanel1.
     * (The alignment with radio buttons is not possible in the end, this tests
     *  that a small gap is added before jRadioButton1 to keep its position.)
     */
    public void doChanges0() {
        lm.setChangeRecording(true);
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 473, 534));
        contInterior.put("Form", new Rectangle(0, 0, 473, 534));
        compBounds.put("jPanel2", new Rectangle(10, 11, 453, 512));
        baselinePosition.put("jPanel2-453-512", new Integer(0));
        contInterior.put("jPanel2", new Rectangle(11, 12, 451, 510));
        compBounds.put("jSeparator1", new Rectangle(11, 23, 451, 13));
        baselinePosition.put("jSeparator1-451-13", new Integer(-1));
        compBounds.put("jProgressBar1", new Rectangle(21, 449, 431, 14));
        baselinePosition.put("jProgressBar1-431-14", new Integer(-1));
        compBounds.put("jScrollPane4", new Rectangle(21, 83, 231, 96));
        baselinePosition.put("jScrollPane4-231-96", new Integer(0));
        compBounds.put("jComboBox1", new Rectangle(21, 197, 231, 20));
        baselinePosition.put("jComboBox1-231-20", new Integer(14));
        compBounds.put("jPanel1", new Rectangle(21, 266, 100, 100));
        baselinePosition.put("jPanel1-100-100", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(21, 266, 100, 100));
        compMinSize.put("jPanel1", new Dimension(0, 0));
        compBounds.put("jPanel1", new Rectangle(21, 266, 100, 100));
        compPrefSize.put("jPanel1", new Dimension(100, 100));
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        compBounds.put("jScrollPane3", new Rectangle(270, 83, 122, 233));
        baselinePosition.put("jScrollPane3-122-233", new Integer(0));
        compBounds.put("jSpinner1", new Rectangle(410, 83, 42, 20));
        baselinePosition.put("jSpinner1-42-20", new Integer(14));
        compBounds.put("jRadioButton1", new Rectangle(270, 343, 93, 23));
        baselinePosition.put("jRadioButton1-93-23", new Integer(15));
        compBounds.put("jButton2", new Rectangle(21, 42, 73, 23));
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compBounds.put("jButton1", new Rectangle(112, 42, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jButton3", new Rectangle(203, 42, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jSlider1", new Rectangle(21, 474, 441, 23));
        baselinePosition.put("jSlider1-441-23", new Integer(0));
        compBounds.put("jRadioButton3", new Rectangle(257, 396, 93, 23));
        baselinePosition.put("jRadioButton3-93-23", new Integer(15));
        compBounds.put("jRadioButton2", new Rectangle(257, 373, 93, 23));
        baselinePosition.put("jRadioButton2-93-23", new Integer(15));
        compMinSize.put("jPanel2", new Dimension(453, 484));
        compBounds.put("jPanel2", new Rectangle(10, 11, 453, 512));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        compPrefSize.put("jSeparator1", new Dimension(0, 2));
        compPrefSize.put("jProgressBar1", new Dimension(146, 14));
        compPrefSize.put("jScrollPane4", new Dimension(166, 96));
        compPrefSize.put("jComboBox1", new Dimension(56, 20));
        compPrefSize.put("jSpinner1", new Dimension(29, 20));
        compPrefSize.put("jSlider1", new Dimension(200, 23));
        hasExplicitPrefSize.put("jPanel2", new Boolean(false));
        prefPadding.put("jComboBox1-jPanel1-1-0-0", new Integer(6)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-1", new Integer(11)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-2", new Integer(6)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        hasExplicitPrefSize.put("jPanel2", new Boolean(false));
        compMinSize.put("Form", new Dimension(473, 506));
        compBounds.put("Form", new Rectangle(0, 0, 473, 534));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
// > START MOVING
        baselinePosition.put("jRadioButton1-93-23", new Integer(15));
        {
            String[] compIds = new String[]{
                "jRadioButton1"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(270, 343, 93, 23)
            };
            Point hotspot = new Point(327, 354);
            ld.startMoving(compIds, bounds, hotspot);
        }
// < START MOVING
        prefPaddingInParent.put("jPanel2-jRadioButton1-1-0", new Integer(7)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel2-jRadioButton1-1-1", new Integer(7)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jSeparator1-jRadioButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton2-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton1-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton3-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jButton2-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jButton3-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton1-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-1", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-1", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton3-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jProgressBar1-jRadioButton1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPaddingInParent.put("jPanel2-jRadioButton1-0-0", new Integer(6)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel2-jRadioButton1-0-1", new Integer(6)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jPanel1-jRadioButton1-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
// > MOVE
        {
            Point p = new Point(320, 355);
            String containerId = "jPanel2";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(257, 343, 93, 23)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("jPanel2-jRadioButton1-1-0", new Integer(7)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel2-jRadioButton1-1-1", new Integer(7)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jSeparator1-jRadioButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSeparator1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton2-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton1-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jButton3-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jButton2-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jButton3-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane3-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton2-jRadioButton1-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton2-1-0-1", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-1", new Integer(3)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-2", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jRadioButton3-1-0-0", new Integer(0)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jProgressBar1-jRadioButton1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jProgressBar1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jSlider1-jRadioButton1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton1-jSlider1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPaddingInParent.put("jPanel2-jRadioButton1-0-0", new Integer(6)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("jPanel2-jRadioButton1-0-1", new Integer(6)); // parentId-compId-dimension-compAlignment
        prefPadding.put("jPanel1-jRadioButton1-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
// > MOVE
        {
            Point p = new Point(319, 355);
            String containerId = "jPanel2";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(257, 343, 93, 23)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        compPrefSize.put("jSeparator1", new Dimension(0, 2));
        compPrefSize.put("jProgressBar1", new Dimension(146, 14));
        compPrefSize.put("jScrollPane4", new Dimension(166, 96));
        compPrefSize.put("jComboBox1", new Dimension(56, 20));
        compPrefSize.put("jSpinner1", new Dimension(29, 20));
        compPrefSize.put("jSlider1", new Dimension(200, 23));
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 473, 534));
        contInterior.put("Form", new Rectangle(0, 0, 473, 534));
        compBounds.put("jPanel2", new Rectangle(10, 11, 453, 512));
        baselinePosition.put("jPanel2-453-512", new Integer(0));
        contInterior.put("jPanel2", new Rectangle(11, 12, 451, 510));
        compBounds.put("jSeparator1", new Rectangle(11, 23, 451, 13));
        baselinePosition.put("jSeparator1-451-13", new Integer(-1));
        compBounds.put("jProgressBar1", new Rectangle(21, 449, 431, 14));
        baselinePosition.put("jProgressBar1-431-14", new Integer(-1));
        compBounds.put("jScrollPane4", new Rectangle(21, 83, 231, 96));
        baselinePosition.put("jScrollPane4-231-96", new Integer(0));
        compBounds.put("jComboBox1", new Rectangle(21, 197, 231, 20));
        baselinePosition.put("jComboBox1-231-20", new Integer(14));
        compBounds.put("jPanel1", new Rectangle(21, 266, 100, 100));
        baselinePosition.put("jPanel1-100-100", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(21, 266, 100, 100));
        compMinSize.put("jPanel1", new Dimension(0, 0));
        compBounds.put("jPanel1", new Rectangle(21, 266, 100, 100));
        compPrefSize.put("jPanel1", new Dimension(100, 100));
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        hasExplicitPrefSize.put("jPanel1", new Boolean(false));
        compBounds.put("jScrollPane3", new Rectangle(270, 83, 122, 233));
        baselinePosition.put("jScrollPane3-122-233", new Integer(0));
        compBounds.put("jSpinner1", new Rectangle(410, 83, 42, 20));
        baselinePosition.put("jSpinner1-42-20", new Integer(14));
        compBounds.put("jRadioButton1", new Rectangle(257, 343, 93, 23));
        baselinePosition.put("jRadioButton1-93-23", new Integer(15));
        compBounds.put("jButton2", new Rectangle(21, 42, 73, 23));
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compBounds.put("jButton1", new Rectangle(112, 42, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jButton3", new Rectangle(203, 42, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jSlider1", new Rectangle(21, 474, 441, 23));
        baselinePosition.put("jSlider1-441-23", new Integer(0));
        compBounds.put("jRadioButton3", new Rectangle(257, 396, 93, 23));
        baselinePosition.put("jRadioButton3-93-23", new Integer(15));
        compBounds.put("jRadioButton2", new Rectangle(257, 373, 93, 23));
        baselinePosition.put("jRadioButton2-93-23", new Integer(15));
        compMinSize.put("jPanel2", new Dimension(440, 484));
        compBounds.put("jPanel2", new Rectangle(10, 11, 453, 512));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        prefPadding.put("jPanel1-jScrollPane3-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane4-jScrollPane3-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jScrollPane3-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jSeparator1", new Dimension(0, 2));
        compPrefSize.put("jProgressBar1", new Dimension(146, 14));
        compPrefSize.put("jScrollPane4", new Dimension(166, 96));
        compPrefSize.put("jComboBox1", new Dimension(56, 20));
        compPrefSize.put("jSpinner1", new Dimension(29, 20));
        compPrefSize.put("jSlider1", new Dimension(200, 23));
        hasExplicitPrefSize.put("jPanel2", new Boolean(false));
        prefPadding.put("jComboBox1-jPanel1-1-0-0", new Integer(6)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-1", new Integer(11)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-2", new Integer(6)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        hasExplicitPrefSize.put("jPanel2", new Boolean(false));
        compMinSize.put("Form", new Dimension(460, 506));
        compBounds.put("Form", new Rectangle(0, 0, 473, 534));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        compBounds.put("Form", new Rectangle(0, 0, 473, 534));
        contInterior.put("Form", new Rectangle(0, 0, 473, 534));
        compBounds.put("jPanel2", new Rectangle(10, 11, 453, 512));
        baselinePosition.put("jPanel2-453-512", new Integer(0));
        contInterior.put("jPanel2", new Rectangle(11, 12, 451, 510));
        compBounds.put("jSeparator1", new Rectangle(11, 23, 451, 13));
        baselinePosition.put("jSeparator1-451-13", new Integer(-1));
        compBounds.put("jProgressBar1", new Rectangle(21, 449, 431, 14));
        baselinePosition.put("jProgressBar1-431-14", new Integer(-1));
        compBounds.put("jScrollPane4", new Rectangle(21, 83, 231, 96));
        baselinePosition.put("jScrollPane4-231-96", new Integer(0));
        compBounds.put("jComboBox1", new Rectangle(21, 197, 231, 20));
        baselinePosition.put("jComboBox1-231-20", new Integer(14));
        compBounds.put("jPanel1", new Rectangle(21, 266, 100, 100));
        baselinePosition.put("jPanel1-100-100", new Integer(0));
        contInterior.put("jPanel1", new Rectangle(21, 266, 100, 100));
        compMinSize.put("jPanel1", new Dimension(0, 0));
        compBounds.put("jPanel1", new Rectangle(21, 266, 100, 100));
        compBounds.put("jScrollPane3", new Rectangle(270, 83, 122, 233));
        baselinePosition.put("jScrollPane3-122-233", new Integer(0));
        compBounds.put("jSpinner1", new Rectangle(410, 83, 42, 20));
        baselinePosition.put("jSpinner1-42-20", new Integer(14));
        compBounds.put("jRadioButton1", new Rectangle(257, 343, 93, 23));
        baselinePosition.put("jRadioButton1-93-23", new Integer(15));
        compBounds.put("jButton2", new Rectangle(21, 42, 73, 23));
        baselinePosition.put("jButton2-73-23", new Integer(15));
        compBounds.put("jButton1", new Rectangle(112, 42, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jButton3", new Rectangle(203, 42, 73, 23));
        baselinePosition.put("jButton3-73-23", new Integer(15));
        compBounds.put("jSlider1", new Rectangle(21, 474, 441, 23));
        baselinePosition.put("jSlider1-441-23", new Integer(0));
        compBounds.put("jRadioButton3", new Rectangle(257, 396, 93, 23));
        baselinePosition.put("jRadioButton3-93-23", new Integer(15));
        compBounds.put("jRadioButton2", new Rectangle(257, 373, 93, 23));
        baselinePosition.put("jRadioButton2-93-23", new Integer(15));
        compMinSize.put("jPanel2", new Dimension(440, 484));
        compBounds.put("jPanel2", new Rectangle(10, 11, 453, 512));
        prefPadding.put("jPanel1-jScrollPane3-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jScrollPane4-jScrollPane3-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jScrollPane3-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jSeparator1", new Dimension(0, 2));
        compPrefSize.put("jProgressBar1", new Dimension(146, 14));
        compPrefSize.put("jScrollPane4", new Dimension(166, 96));
        compPrefSize.put("jComboBox1", new Dimension(56, 20));
        compPrefSize.put("jSpinner1", new Dimension(29, 20));
        compPrefSize.put("jSlider1", new Dimension(200, 23));
        prefPadding.put("jComboBox1-jPanel1-1-0-0", new Integer(6)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-1", new Integer(11)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-2", new Integer(6)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jComboBox1-jPanel1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-0", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-1", new Integer(7)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-2", new Integer(2)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jRadioButton3-jProgressBar1-1-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compMinSize.put("Form", new Dimension(460, 506));
        compBounds.put("Form", new Rectangle(0, 0, 473, 534));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        compPrefSize.put("jPanel2", new Dimension(453, 512));
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }
}
