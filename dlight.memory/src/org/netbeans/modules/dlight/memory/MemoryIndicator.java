/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.dlight.memory;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.modules.dlight.indicator.spi.Indicator;
import org.netbeans.modules.dlight.indicator.api.IndicatorMetadata;
import org.netbeans.modules.dlight.storage.api.DataRow;


/**
 * Mmory usage indicator
 * @author Vladimir Kvashin
 */
public class MemoryIndicator extends Indicator<MemoryIndicatorConfiguration> {

    private final MemoryIndicatorPanel panel;
    private final String colName;

    public MemoryIndicator(MemoryIndicatorConfiguration configuration) {
        super(configuration);
        this.panel = new MemoryIndicatorPanel();
        this.colName = configuration.getColName();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    public void reset() {
    }

    public void updated(List<DataRow> data) {
        DataRow lastRow = data.get(data.size() - 1);
        String value = lastRow.getStringValue(colName); //TODO: change to Long
        panel.setValue(Long.parseLong(value));
    }

    private static class MemoryIndicatorPanel extends JPanel {

        private final JLabel label;
        private final JLabel data;
        private final NumberFormat nf;

        MemoryIndicatorPanel() {
            nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);

            label = new JLabel("Heap size: ");
            data = new JLabel(""); //NOI18N

            setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints;

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            add(label, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            add(data, gridBagConstraints);

        }

        void setValue(long value) {
            value = value / 1000 + (value % 1000 >= 500 ? 1 : 0);
            String text = nf.format(value) + " Kb";
            data.setText(text);
        }
    }
}
