/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.glassfish.cloud.data;

import javax.swing.JComponent;
import org.glassfish.tools.ide.data.cloud.GlassFishCloudEntity;
import org.netbeans.spi.server.ServerInstanceImplementation;
import static org.openide.util.NbBundle.getMessage;

/**
 * GlassFish Cloud instance.
 * <p/>
 * GlassFish cloud instance represents CPAS interface. Based on Tooling SDK
 * entity object.
 * <p/>
 * @author Tomas Kraus, Peter Benedikovic
 */
public class GlassFishCloudInstance extends GlassFishCloudEntity
        implements ServerInstanceImplementation {

    /** The display name of GlassFish cloud server type. */
    private final String serverDisplayName;

    /**
     * Constructs GlassFish Cloud class instance with ALL values set.
     * <p/>
     * @param name GlassFish cloud name to set.
     * @param host GlassFish cloud host to set.
     * @param port GlassFish server port to set.
     */
    public GlassFishCloudInstance(String name, String host, int port) {
        super(name, host, port);
        this.serverDisplayName = getMessage(GlassFishCloudInstance.class,
                Bundle.GLASSFISH_CLOUD_SERVER_TYPE, new Object[]{});
    }

    /**
     * Get GlassFish cloud name (display name in IDE).
     *
     * @return GlassFish cloud name (display name in IDE).
     */
    @Override
    public String getDisplayName() {
        return this.name;
    }

    /**
     * Returns the display name of the server type to which this instance belongs.
     *
     * @return the display name of the server type to which this instance belongs
     */
    @Override
    public String getServerDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public org.openide.nodes.Node getFullNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public org.openide.nodes.Node getBasicNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JComponent getCustomizer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRemovable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
