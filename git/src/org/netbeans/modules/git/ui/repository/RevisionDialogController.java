/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.git.ui.repository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author ondra
 */
public class RevisionDialogController implements ActionListener {
    private final RevisionDialog panel;
    private final File repository;
    private final RevisionInfoPanelController infoPanelController;

    public RevisionDialogController (File repository) {
        infoPanelController = new RevisionInfoPanelController(repository);
        panel = new RevisionDialog(infoPanelController.getPanel());
        this.repository = repository;
        infoPanelController.loadInfo(panel.revisionField.getText());
        attachListeners();
    }

    public RevisionDialog getPanel () {
        return panel;
    }

    public void setEnabled (boolean enabled) {
        panel.btnSelectRevision.setEnabled(enabled);
        panel.revisionField.setEnabled(enabled);
    }

    public String getRevision () {
        return panel.revisionField.getText();
    }

    private void attachListeners () {
        panel.btnSelectRevision.addActionListener(this);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == panel.btnSelectRevision) {
            openRevisionPicker();
        }
    }

    private void openRevisionPicker () {
        RevisionPicker picker = new RevisionPicker(repository);
        if (picker.open()) {
            String revision = picker.getRevision();
            if (!revision.equals(panel.revisionField.getText())) {
                panel.revisionField.setText(picker.getRevision());
                infoPanelController.loadInfo(revision);
            }
        }
    }
}
