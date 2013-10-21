/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.localtasks;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.modules.localtasks.task.LocalTask;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;

/**
 *
 * @author Ondrej Vrabec
 */
public class RepositoryProviderImpl implements RepositoryProvider<LocalRepository, LocalQuery, LocalTask> {

    @Override
    public RepositoryInfo getInfo (LocalRepository r) {
        return new RepositoryInfo(r.getID(), LocalTaskConnector.CONNECTOR_NAME, r.getUrl(), r.getDisplayName(), r.getTooltip());
    }

    @Override
    public Image getIcon (LocalRepository r) {
        return r.getIcon();
    }

    @Override
    public LocalTask[] getIssues (LocalRepository r, String... ids) {
        return r.getTasks(ids);
    }

    @Override
    public void remove (LocalRepository r) {
        throw new UnsupportedOperationException("Not supported for Local Tasks");
    }

    @Override
    public RepositoryController getController (LocalRepository r) {
        throw new UnsupportedOperationException("Not supported for Local Tasks");
    }

    @Override
    public LocalQuery createQuery (LocalRepository r) {
        return null;
    }

    @Override
    public LocalTask createIssue (LocalRepository r) {
        return r.createTask();
    }

    @Override
    public Collection<LocalQuery> getQueries (LocalRepository r) {
        return r.getQueries();
    }

    @Override
    public Collection<LocalTask> simpleSearch (LocalRepository r, String criteria) {
        return r.simpleSearch(criteria);
    }

    @Override
    public void removePropertyChangeListener (LocalRepository r, PropertyChangeListener listener) {
        r.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener (LocalRepository r, PropertyChangeListener listener) {
        r.addPropertyChangeListener(listener);
    }

    @Override
    public LocalTask createIssue(LocalRepository r, String summary, String description) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
