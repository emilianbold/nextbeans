/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.zend.util;

import java.io.File;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * @author Tomas Mysik
 */
public final class ZendUtils {
    public static final String ACTION_FILE_SUFIX = "Controller"; // NOI18N
    public static final String ACTION_CLASS_SUFFIX = "Controller"; // NOI18N
    public static final String ACTION_METHOD_SUFIX = "Action"; // NOI18N

    private static final String DIR_CONTROLLERS = "controllers"; // NOI18N
    private static final String DIR_SCRIPTS = "scripts"; // NOI18N
    private static final String DIR_VIEWS = "views"; // NOI18N
    private static final String FILE_VIEW_EXT = "phtml"; // NOI18N

    private static final String FILE_ACTION_RELATIVE = "../../../" + DIR_CONTROLLERS + "/%sController.php"; // NOI18N

    private static final String FILE_VIEW_RELATIVE = "../" + DIR_VIEWS + "/" + DIR_SCRIPTS + "/%s/%s." + FILE_VIEW_EXT; // NOI18N
    private static final String FILE_DEFAULT_VIEW = "index"; // NOI18N

    private ZendUtils() {
    }

    public static boolean isView(FileObject fo) {
        if (!fo.isData() || !fo.getExt().equals(FILE_VIEW_EXT)) {
            return false;
        }
        File file = FileUtil.toFile(fo);
        File parent = file.getParentFile(); // controller
        if (parent == null) {
            return false;
        }
        parent = parent.getParentFile(); // scripts
        if (!DIR_SCRIPTS.equals(parent.getName())) {
            return false;
        }
        parent = parent.getParentFile(); // scripts
        return parent != null && DIR_VIEWS.equals(parent.getName()); // views
    }

    public static boolean isViewWithAction(FileObject fo) {
        return isView(fo) && getAction(fo) != null;
    }

    public static boolean isAction(FileObject fo) {
        return fo.isData() && fo.getName().endsWith(ACTION_FILE_SUFIX);
    }

    public static FileObject getAction(FileObject fo) {
        File parent = FileUtil.toFile(fo).getParentFile();
        File action = PropertyUtils.resolveFile(parent, String.format(FILE_ACTION_RELATIVE, getControllerName(parent.getName())));
        if (action.isFile()) {
            return FileUtil.toFileObject(action);
        }
        return null;
    }

    public static FileObject getView(FileObject fo, PhpBaseElement phpElement) {
        FileObject view = null;
        if (phpElement instanceof PhpClass.Method) {
            String methodName = phpElement.getName();
            if (methodName.endsWith(ACTION_METHOD_SUFIX)) {
                String partName = methodName.replace(ACTION_METHOD_SUFIX, ""); // NOI18N
                view = getView(fo, partName);
            }
        }
        if (view == null) {
            view = getDefaultView(fo);
        }
        return view;
    }

    private static FileObject getDefaultView(FileObject fo) {
        return getView(fo, FILE_DEFAULT_VIEW);
    }

    private static FileObject getView(FileObject fo, String viewName) {
        File parent = FileUtil.toFile(fo).getParentFile();
        File view = PropertyUtils.resolveFile(parent, String.format(FILE_VIEW_RELATIVE, getControllerName(fo), viewName));
        if (view.isFile()) {
            return FileUtil.toFileObject(view);
        }
        return null;
    }

    static String getControllerName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static String getControllerName(FileObject fo) {
        String controller = fo.getName().replace(ACTION_FILE_SUFIX, ""); // NOI18N
        return controller.substring(0, 1).toLowerCase() + controller.substring(1);
    }

    public static String getActionName(FileObject view) {
        return view.getName() + ACTION_METHOD_SUFIX;
    }
}
