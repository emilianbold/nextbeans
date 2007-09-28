/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.uml.ui.support.messaging;

import org.netbeans.modules.uml.core.support.umlutils.ETList;
import org.netbeans.modules.uml.ui.support.SimpleQuestionDialogKind;

/**
 *
 * @author  Thuy
 */
public class SimplePickListDialog extends javax.swing.JPanel
{
    protected String labelText;
    protected String  m_Result = "";
    protected String m_Title = "";
    protected String m_DefaultValue = "";
    protected ETList<String> m_PickListItems = null;
    
    /** Creates new form SimplePickListDialog */
    public SimplePickListDialog()
    {
        initComponents();
    }
     
    public SimplePickListDialog(String labelText, String defaultItem, ETList<String> listItems)
    {
        this.labelText = labelText;
        m_DefaultValue = defaultItem;
        m_PickListItems = listItems;
        initComponents();
        setPickListItems();
    }
    
    public void setResult(String val)
    {
        m_Result = val;
    }
    
    public String getResult()
    {
        return m_Result;
    }
    
    public void performOKAction () {
	m_Result = (String) nameComboBox.getEditor().getItem();	
    } 

    public void setPickListItems()
    {
        if (m_PickListItems == null)
            return;
        
        for (int i = 0; i < m_PickListItems.size(); i++)
        {
            nameComboBox.addItem(m_PickListItems.get(i));
        }
        nameComboBox.setSelectedItem(m_DefaultValue);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        label = new javax.swing.JLabel();
        nameComboBox = new javax.swing.JComboBox();

        label.setLabelFor(nameComboBox);
        label.setText(labelText);
        label.setFocusable(false);
        label.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SimplePickListDialog.class, "ACSN_PickListLabel"));
        label.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SimplePickListDialog.class, "ACSN_PickListLabel"));

        nameComboBox.setEditable(true);
        nameComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SimplePickListDialog.class, "ACSN_PickListComboBox"));
        nameComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SimplePickListDialog.class, "ACSD_PickListComboBox"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, nameComboBox, 0, 435, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nameComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    private javax.swing.JComboBox nameComboBox;
    // End of variables declaration//GEN-END:variables
    
}
