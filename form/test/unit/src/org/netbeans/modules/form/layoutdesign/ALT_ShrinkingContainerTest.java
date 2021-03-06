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

public class ALT_ShrinkingContainerTest extends LayoutTestCase {

    public ALT_ShrinkingContainerTest(String name) {
        super(name);
        try {
	    className = this.getClass().getName();
	    className = className.substring(className.lastIndexOf('.') + 1, className.length());
            startingFormFile = FileUtil.toFileObject(new File(url.getFile() + goldenFilesPath + className + "-StartingForm.form").getCanonicalFile());
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    // Drag the right designer edge, shrink the form slightly. The button should
    // shrink accordingly, still above default size.
    public void doChanges0() {
        ld.externalSizeChangeHappened();
        // > UPDATE CURRENT STATE
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 270, 23));
        baselinePosition.put("jButton1-270-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 270, 23));
        baselinePosition.put("jButton1-270-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        ld.updateCurrentState();
        // < UPDATE CURRENT STATE
        // > START RESIZING
        baselinePosition.put("Form-400-300", new Integer(0));
        compMinSize.put("Form", new Dimension(203, 99));
        prefPaddingInParent.put("Form-jButton1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        {
            String[] compIds = new String[]{"Form"};
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 400, 300)};
            Point hotspot = new Point(404, 163);
            int[] resizeEdges = new int[]{1, -1};
            boolean inLayout = false;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
        // < START RESIZING
        // > MOVE
        // < START RESIZING
        // > MOVE
        {
            Point p = new Point(334, 176);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 330, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > MOVE
        // < MOVE
        // > MOVE
        {
            Point p = new Point(333, 176);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 329, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > END MOVING
        contInterior.put("Form", new Rectangle(0, 0, 329, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 199, 23));
        baselinePosition.put("jButton1-199-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        ld.endMoving(true);
        // < END MOVING
        ld.externalSizeChangeHappened();
        // > UPDATE CURRENT STATE
        contInterior.put("Form", new Rectangle(0, 0, 329, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 199, 23));
        baselinePosition.put("jButton1-199-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        contInterior.put("Form", new Rectangle(0, 0, 329, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 199, 23));
        baselinePosition.put("jButton1-199-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        ld.updateCurrentState();
        // < UPDATE CURRENT STATE
    }

    // Shrink the container so it snaps at button's default size. The gap on the
    // right still the same.
    public void doChanges1() {
        // > START RESIZING
        baselinePosition.put("Form-329-300", new Integer(0));
        compMinSize.put("Form", new Dimension(203, 99));
        prefPaddingInParent.put("Form-jButton1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        {
            String[] compIds = new String[]{"Form"};
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 329, 300)};
            Point hotspot = new Point(333, 151);
            int[] resizeEdges = new int[]{1, -1};
            boolean inLayout = false;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
        // < START RESIZING
        // > MOVE
        // < START RESIZING
        // > MOVE
        {
            Point p = new Point(207, 172);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 203, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > MOVE
        // < MOVE
        // > MOVE
        {
            Point p = new Point(206, 172);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 203, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > END MOVING
        contInterior.put("Form", new Rectangle(0, 0, 203, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        ld.endMoving(true);
        // < END MOVING
        ld.externalSizeChangeHappened();
        // > UPDATE CURRENT STATE
        contInterior.put("Form", new Rectangle(0, 0, 203, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        contInterior.put("Form", new Rectangle(0, 0, 203, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        ld.updateCurrentState();
        // < UPDATE CURRENT STATE
    }

    // Shrink the container so the gap is reduced, but still above its default size.
    public void doChanges2() {
        // > START RESIZING
        baselinePosition.put("Form-203-300", new Integer(0));
        compMinSize.put("Form", new Dimension(203, 99));
        prefPaddingInParent.put("Form-jButton1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        {
            String[] compIds = new String[]{"Form"};
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 203, 300)};
            Point hotspot = new Point(201, 160);
            int[] resizeEdges = new int[]{1, -1};
            boolean inLayout = false;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
        // < START RESIZING
        // > MOVE
        // < START RESIZING
        // > MOVE
        {
            Point p = new Point(141, 153);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 143, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > MOVE
        // < MOVE
        // > MOVE
        {
            Point p = new Point(140, 153);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 142, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > END MOVING
        contInterior.put("Form", new Rectangle(0, 0, 142, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        ld.endMoving(true);
        // < END MOVING
        ld.externalSizeChangeHappened();
        // > UPDATE CURRENT STATE
        contInterior.put("Form", new Rectangle(0, 0, 142, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        contInterior.put("Form", new Rectangle(0, 0, 142, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        ld.updateCurrentState();
        // < UPDATE CURRENT STATE
    }

    // Shrink the container to snap at the default size of the gap.
    public void doChanges3() {
        // > START RESIZING
        baselinePosition.put("Form-142-300", new Integer(0));
        compMinSize.put("Form", new Dimension(142, 99));
        prefPaddingInParent.put("Form-jButton1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        {
            String[] compIds = new String[]{"Form"};
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 142, 300)};
            Point hotspot = new Point(146, 148);
            int[] resizeEdges = new int[]{1, -1};
            boolean inLayout = false;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
        // < START RESIZING
        // > MOVE
        // < START RESIZING
        // > MOVE
        {
            Point p = new Point(95, 147);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 93, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > MOVE
        // < MOVE
        // > MOVE
        {
            Point p = new Point(96, 147);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 93, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > END MOVING
        contInterior.put("Form", new Rectangle(0, 0, 93, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        ld.endMoving(true);
        // < END MOVING
        ld.externalSizeChangeHappened();
        // > UPDATE CURRENT STATE
        contInterior.put("Form", new Rectangle(0, 0, 93, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        contInterior.put("Form", new Rectangle(0, 0, 93, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        ld.updateCurrentState();
        // < UPDATE CURRENT STATE
    }

    // Shrink the container so it snaps at size without a gap on the right.
    public void doChanges4() {
        // > START RESIZING
        baselinePosition.put("Form-93-300", new Integer(0));
        compMinSize.put("Form", new Dimension(93, 99));
        prefPaddingInParent.put("Form-jButton1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel1-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        // parentId-compId-dimension-compAlignment
        {
            String[] compIds = new String[]{"Form"};
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 93, 300)};
            Point hotspot = new Point(94, 139);
            int[] resizeEdges = new int[]{1, -1};
            boolean inLayout = false;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
        // < START RESIZING
        // > MOVE
        // < START RESIZING
        // > MOVE
        {
            Point p = new Point(87, 139);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 83, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > MOVE
        // < MOVE
        // > MOVE
        {
            Point p = new Point(86, 139);
            String containerId = null;
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{new Rectangle(0, 0, 83, 300)};
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
        // < MOVE
        // > END MOVING
        contInterior.put("Form", new Rectangle(0, 0, 83, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        ld.endMoving(true);
        // < END MOVING
        ld.externalSizeChangeHappened();
        // > UPDATE CURRENT STATE
        contInterior.put("Form", new Rectangle(0, 0, 83, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        compPrefSize.put("jButton1", new Dimension(73, 23));
        compPrefSize.put("jLabel1", new Dimension(34, 14));
        contInterior.put("Form", new Rectangle(0, 0, 83, 300));
        compBounds.put("jButton1", new Rectangle(10, 45, 73, 23));
        baselinePosition.put("jButton1-73-23", new Integer(15));
        compBounds.put("jLabel1", new Rectangle(10, 74, 34, 14));
        baselinePosition.put("jLabel1-34-14", new Integer(11));
        ld.updateCurrentState();
        // < UPDATE CURRENT STATE
    }

}
