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
package org.netbeans.modules.search;

import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.search.SearchPattern;
import org.netbeans.api.search.SearchScopeOptions;
import org.netbeans.api.search.provider.SearchInfo;
import org.netbeans.modules.search.IgnoreListPanel.IgnoreListManager;
import org.netbeans.modules.search.MatchingObject.Def;
import org.netbeans.modules.search.matcher.DefaultMatcher;
import org.netbeans.modules.search.ui.UiUtils;
import org.netbeans.spi.search.SearchFilterDefinition;
import org.netbeans.spi.search.SearchFilterDefinition.FolderResult;
import org.netbeans.spi.search.SearchScopeDefinition;
import org.netbeans.spi.search.provider.SearchComposition;
import org.netbeans.spi.search.provider.SearchProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/** Basic Search provider
 *
 * @author jhavlin
 */
@ServiceProvider(service=SearchProvider.class, position=0)
public class BasicSearchProvider extends SearchProvider {

    private static final BasicSearchProvider INSTANCE = new BasicSearchProvider();

    /**
     * Presenter is {@link BasicSearchPresenter}.
     */
    @Override
    public Presenter createPresenter(boolean replaceMode) {
        return new BasicSearchPresenter(replaceMode);
    }

    /**
     * Replacing is supported.
     */
    @Override
    public boolean isReplaceSupported() {
        return true;
    }

    /**
     * This search provider is always enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    public static Presenter createBasicPresenter(boolean replacing) {
        return new BasicSearchPresenter(replacing);
    }

    public static Presenter createBasicPresenter(
            boolean replacing,
            @NullAllowed SearchPattern searchPattern,
            @NullAllowed String replaceString,
            @NullAllowed Boolean preserveCase,
            @NullAllowed SearchScopeOptions searchScopeOptions,
            @NullAllowed Boolean useIgnoreList,
            @NullAllowed String scopeId
            ) {
        BasicSearchCriteria bsc = createCriteria(searchScopeOptions,
                useIgnoreList, searchPattern, preserveCase, replacing,
                replaceString);
        return new BasicSearchPresenter(replacing, scopeId, bsc);
    }

    /**
     *
     */
    private static class BasicSearchPresenter
            extends BasicSearchProvider.Presenter {

        BasicSearchForm form = null;
        private boolean replacing;
        private String scopeId;
        private BasicSearchCriteria explicitCriteria;

        public BasicSearchPresenter(boolean replacing) {
            this(replacing, null, null);
        }

        public BasicSearchPresenter(boolean replacing, String scopeId,
                BasicSearchCriteria explicitCriteria) {
            this.replacing = replacing;
            this.scopeId = scopeId;
            this.explicitCriteria = explicitCriteria;
        }

        @Override
        public JComponent getForm() {
            if (form == null) {
                String scopeToUse = scopeId == null
                        ? FindDialogMemory.getDefault().getScopeTypeId()
                        : scopeId;
                form = new BasicSearchForm(scopeToUse, replacing,
                        explicitCriteria);
                form.setName(UiUtils.getText(
                        "BasicSearchForm.tabText"));                    //NOI18N
                form.setUsabilityChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        fireChange();
                    }
                });
            }
            return form;
        }

        @Override
        public SearchComposition<Def> composeSearch() {

            String msg = Manager.getInstance().mayStartSearching();
            if (msg != null) {
                /*
                 * Search cannot be started, for example because the replace
                 * operation has not finished yet.
                 */
                DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                        msg,
                        NotifyDescriptor.INFORMATION_MESSAGE));
                return null;
            }

            form.onOk();
            SearchScopeDefinition searchScope = form.getSelectedSearchScope();
            BasicSearchCriteria basicSearchCriteria =
                    form.getBasicSearchCriteria();

            SearchScopeOptions so = basicSearchCriteria.getSearcherOptions();
            if (basicSearchCriteria.isUseIgnoreList()) {
                so.addFilter(new IgnoreListFilter());
            }
            SearchInfo ssi = searchScope.getSearchInfo();

            return new BasicComposition(
                    ssi,
                    new DefaultMatcher(basicSearchCriteria.getSearchPattern()),
                    basicSearchCriteria, this);
        }

        @Override
        public boolean isUsable() {
            return form.isUsable();
        }

        @Override
        public void clean() {
            super.clean();
            form.clean();
        }
    }

    /**
     * Get singleton instance.
     */
    public static BasicSearchProvider getInstance() {
        return INSTANCE;
    }

    private static class IgnoreListFilter extends SearchFilterDefinition {

        private IgnoreListManager ignoreListManager;

        @Override
        public boolean searchFile(FileObject file)
                throws IllegalArgumentException {
            if (file.isFolder()) {
                throw new IllegalArgumentException(file
                        + " is folder, but should be regular file."); //NOI18N
            }
            return !isIgnored(file);
        }

        @Override
        public FolderResult traverseFolder(FileObject folder)
                throws IllegalArgumentException {
            if (!folder.isFolder()) {
                throw new IllegalArgumentException(folder
                        + " is file, but should be folder."); //NOI18N
            }
            if (isIgnored(folder)) {
                return FolderResult.DO_NOT_TRAVERSE;
            } else {
                return FolderResult.TRAVERSE;
            }
        }

        /**
         * Check whether the passed file object should be ignored. Use global
         * ignore list.
         *
         * @return true if the file object is ignored, false otherwise.
         */
        private boolean isIgnored(FileObject fileObj) {
            return getIgnoreListManager().isIgnored(fileObj);
        }

        /**
         * Get ignore list manager. If not yet initialized, initialize it.
         */
        IgnoreListPanel.IgnoreListManager getIgnoreListManager() {
            if (ignoreListManager == null) {
                List<String> il = FindDialogMemory.getDefault().getIgnoreList();
                ignoreListManager = new IgnoreListPanel.IgnoreListManager(il);
            }
            return ignoreListManager;
        }
    }

    /**
     * Start a search task with specified parameters.
     */
    public static void startSearch(
            @NonNull SearchPattern searchPattern,
            @NonNull SearchScopeOptions searchScopeOptions,
            @NullAllowed String scopeId) throws IllegalArgumentException {

        BasicSearchCriteria criteria = createCriteria(searchScopeOptions,
                Boolean.FALSE, searchPattern, null, false, null);
        if (!criteria.isUsable()) {
            throw new IllegalArgumentException(
                    "Search cannot be started - No restrictions set."); //NOI18N
        }
        SearchScopeDefinition bestScope = findBestSearchScope(scopeId);
        BasicComposition composition = new BasicComposition(
                bestScope.getSearchInfo(), new DefaultMatcher(searchPattern),
                criteria, BasicSearchProvider.createBasicPresenter(true,
                searchPattern, null, false, searchScopeOptions, false, scopeId));
        Manager.getInstance().scheduleSearchTask(
                new SearchTask(composition, false));
    }

    /**
     * Create basic search criteria instance for passed arguments.
     */
    private static BasicSearchCriteria createCriteria(
            SearchScopeOptions searchScopeOptions, Boolean useIgnoreList,
            SearchPattern searchPattern, Boolean preserveCase,
            boolean replacing, String replaceString) {
        BasicSearchCriteria bsc = new BasicSearchCriteria();
        bsc.setFileNamePattern(searchScopeOptions.getPattern());
        bsc.setFileNameRegexp(searchScopeOptions.isRegexp());
        bsc.setTextPattern(searchPattern.getSearchExpression());
        bsc.setCaseSensitive(searchPattern.isMatchCase());
        bsc.setWholeWords(searchPattern.isWholeWords());
        bsc.setRegexp(searchPattern.isRegExp());
        if (preserveCase != null) {
            bsc.setPreserveCase(preserveCase);
        }
        if (useIgnoreList != null) {
            bsc.setUseIgnoreList(useIgnoreList);
        }
        if (replacing) {
            bsc.setReplaceExpr(replaceString);
        } else {
            bsc.setSearchInArchives(searchScopeOptions.isSearchInArchives());
            bsc.setSearchInGenerated(searchScopeOptions.isSearchInGenerated());
        }
        return bsc;
    }

    /**
     * Find best available search scope.
     */
    private static SearchScopeDefinition findBestSearchScope(
            String preferredscopeId) throws IllegalStateException {
        SearchScopeList ssl = new SearchScopeList();
        SearchScopeDefinition bestScope = null;
        for (SearchScopeDefinition ssd : ssl.getSeachScopeDefinitions()) {
            if (ssd.isApplicable()) {
                if (preferredscopeId != null && ssd.getTypeId().equals(preferredscopeId)) {
                    bestScope = ssd;
                    break;
                } else if (bestScope == null) {
                    bestScope = ssd;
                }
            }
        }
        if (bestScope == null) {
            throw new IllegalStateException("No default search scope"); //NOI18N
        }
        return bestScope;
    }

    /**
     * Create filter for actual ignore list.
     */
    public static SearchFilterDefinition getIgnoreListFilter() {
        return new IgnoreListFilter();
    }
}
