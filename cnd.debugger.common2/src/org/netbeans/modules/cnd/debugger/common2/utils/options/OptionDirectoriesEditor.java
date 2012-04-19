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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.debugger.common2.utils.options;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.netbeans.modules.cnd.utils.CndPathUtilitities;
import org.netbeans.modules.cnd.utils.ui.FileChooser;
import org.netbeans.modules.cnd.utils.ui.ListEditorPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.NbBundle;

class OptionDirectoriesEditor extends PropertyEditorSupport
			    implements ExPropertyEditor {

    private OptionPropertySupport ops;
    private final String baseDir;
    private PropertyEnv env;

    public OptionDirectoriesEditor(OptionPropertySupport ops,
				 String baseDir) {
	this.ops = ops;
	this.baseDir = baseDir;
    }

    // interface PropertyEditor
    public void setAsText(String text) {
	ops.setValue(text); // from PropertyEditorSupport
    }

    // interface PropertyEditor
    public String getAsText() {
        return (String) getValue();
    }

    // interface PropertyEditor
    @Override
    public void setValue(Object v) {
        if (v instanceof List) {
            boolean addSep = false;
            StringBuilder ret = new StringBuilder();
            for (Object row : ((List) v)) {
                if (addSep) {
                    ret.append(File.pathSeparator);
                }
                ret.append(row.toString());
                addSep = true;
            }
            ops.setValue(ret.toString());
        }
    }

    // interface PropertyEditor
    @Override
    public Object getValue() {
	return ops.getValue();
    }

    // interface PropertyEditor
    public boolean supportsCustomEditor() {
	return true;
    }

    // interface PropertyEditor
    public java.awt.Component getCustomEditor() {
        return new DirectoriesChooser(this, env, baseDir, (String) ops.getValue());
    }

    // interface ExPropertyEditor
    public void attachEnv(PropertyEnv env) {
	this.env = env;
    }
    
    static class DirectoriesChooser extends ListEditorPanel<String>
                                    implements PropertyChangeListener {

        private final PropertyEditorSupport editor;
        private final String baseDir;
        private final String path;
        
        private DirectoriesChooser(PropertyEditorSupport editor, PropertyEnv env, String baseDir, String path){
            super(getPathsFromString(editor.getValue().toString()));
            
            getDefaultButton().setVisible(false);
            
            this.editor = editor;
            this.baseDir = baseDir;
            this.path = path;
            
            env.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
	    env.addPropertyChangeListener(this);
        }
        
        private static String fullPath(String baseDir, String path) {
	    String seed = path;
	    if (seed.length() == 0)
		seed = ".";	// NOI18N
	    if (!CndPathUtilitities.isPathAbsolute(seed))
		seed = baseDir + File.separatorChar + seed;
	    return seed;
	}
        
        private static List getPathsFromString(String inpStr){
            List<String> newList = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(inpStr, File.pathSeparator); // NOI18N
            while (st.hasMoreTokens()) {
                newList.add(st.nextToken());
            }
            return newList;
        }

        @Override
        public String addAction() {
            FileChooser fileChooser = new FileChooser(
                    getString("ADD_DIRECTORY_DIALOG_TITLE"),  // NOI18N
                    getString("ADD_DIRECTORY_BUTTON_TXT"),  // NOI18N
                    FileChooser.DIRECTORIES_ONLY,
                    null,
                    fullPath(baseDir, path),
                    true);

            int ret = fileChooser.showOpenDialog(this);
            if (ret == FileChooser.CANCEL_OPTION) {
                return null;
            }
            String itemPath = fileChooser.getSelectedFile().getPath();
            itemPath = CndPathUtilitities.naturalizeSlashes(itemPath);
            String bd = baseDir;
            if (bd != null) {
                bd = CndPathUtilitities.naturalizeSlashes(bd);
            }
            itemPath = CndPathUtilitities.toRelativePath(bd, itemPath);
            itemPath = CndPathUtilitities.normalizeSlashes(itemPath);
            return itemPath;
        }
        
        @Override
        public String copyAction(String o) {
            return o;
        }

        @Override
        public void editAction(String o, int i) {
            String s = o;

            NotifyDescriptor.InputLine notifyDescriptor = 
                    new NotifyDescriptor.InputLine(getString("EDIT_DIALOG_LABEL_TXT"),  // NOI18N
                    getString("EDIT_DIALOG_TITLE_TXT"));    // NOI18N
            notifyDescriptor.setInputText(s);
            DialogDisplayer.getDefault().notify(notifyDescriptor);
            if (notifyDescriptor.getValue() != NotifyDescriptor.OK_OPTION) {
                return;
            }
            String newS = notifyDescriptor.getInputText();
            replaceElement(o, newS, i);
        }
        
        @Override
        public String getListLabelText() {
            return getString("DIRECTORIES_LABEL_TXT");  // NOI18N
        }

        @Override
        public char getListLabelMnemonic() {
            return getString("DIRECTORIES_LABEL_MN").charAt(0); // NOI18N
        }

        @Override
        public String getAddButtonText() {
            return getString("ADD_BUTTON_TXT"); // NOI18N
        }

        @Override
        public char getAddButtonMnemonics() {
            return getString("ADD_BUTTON_MN").charAt(0);    // NOI18N
        }

        @Override
        public String getRenameButtonText() {
            return getString("EDIT_BUTTON_TXT");    // NOI18N
        }

        @Override
        public char getRenameButtonMnemonics() {
            return getString("EDIT_BUTTON_MN").charAt(0);   // NOI18N
        }

        @Override
        public String getDownButtonText() {
            return getString("DOWN_BUTTON_TXT");    // NOI18N
        }

        @Override
        public char getDownButtonMnemonics() {
            return getString("DOWN_BUTTON_MN").charAt(0);   // NOI18N
        }

        @Override
        protected String getUpButtonText() {
            return getString("UP_BUTTON_TXT");  // NOI18N
        }
        
        @Override
        public char getUpButtonMnemonics() {
            return getString("UP_BUTTON_MN").charAt(0); // NOI18N
        }
        
        private Object getPropertyValue() throws IllegalStateException {
            return new ArrayList<String>(getListData());
        }
        
        //PropertyChangeListener interface
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName()) && evt.getNewValue() == PropertyEnv.STATE_VALID) {
                editor.setValue(getPropertyValue());
            }
        }
        
        private static String getString(String key) {
            return NbBundle.getMessage(OptionDirectoriesEditor.class, key);
        }
    }
}
