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
package org.netbeans.modules.search.ui;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.search.Constants;
import org.netbeans.spi.search.provider.SearchComposition;
import org.netbeans.spi.search.provider.SearchProvider;
import org.netbeans.spi.search.provider.SearchProvider.Presenter;
import org.netbeans.spi.search.provider.SearchResultsDisplayer;
import org.netbeans.spi.search.provider.SearchResultsDisplayer.NodeDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author jhavlin
 */
public class DefaultSearchResultsPanel<T> extends AbstractSearchResultsPanel {

    private List<T> matchingObjects = new ArrayList<T>();
    private final NodeDisplayer<T> nodeDisplayer;
    private ResultsNode resultsNode;

    public DefaultSearchResultsPanel(
            SearchResultsDisplayer.NodeDisplayer<T> nodeDisplayer,
            SearchComposition searchComposition,
            Class<? extends SearchProvider> searchProviderClass,
            Presenter searchProviderPresenter) {

        super(searchComposition, searchProviderClass, searchProviderPresenter);
        this.resultsNode = new ResultsNode();
        getExplorerManager().setRootContext(resultsNode);
        this.nodeDisplayer = nodeDisplayer;
        resultsNode.update();
    }

    public void addMathingObject(T object) {
        matchingObjects.add(object);
        resultsNode.update();
    }

    /**
     * Root node shows search info and statistics.
     */
    private class ResultsNode extends AbstractNode {

        private ResultsNodeChildren children;

        public ResultsNode() {
            this(new ResultsNodeChildren());
        }

        public ResultsNode(ResultsNodeChildren children) {
            super(children);
            this.children = children;
        }

        void update() {
            setDisplayName(NbBundle.getMessage(Constants.class,
                    "TXT_RootSearchedNodes", //NOI18N
                    matchingObjects.size()));
            children.update();
        }
    }

    /**
     * Children of the root node represent matching object that have been found
     * so far.
     */
    private class ResultsNodeChildren extends Children.Keys<T> {

        @Override
        protected Node[] createNodes(T key) {
            return new Node[]{nodeDisplayer.matchToNode(key)};
        }

        void update() {
            this.setKeys(matchingObjects);
        }
    }

    @Override
    public void searchFinished() {
        super.searchFinished();
        resultsNode.setDisplayName(NbBundle.getMessage(Constants.class,
                "TEXT_MSG_FOUND_X_NODES", //NOI18N
                matchingObjects.size()));
    }
}
