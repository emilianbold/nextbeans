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
package org.netbeans.modules.analysis;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.fileinfo.NonRecursiveFolder;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.modules.analysis.spi.Analyzer.AnalyzerFactory;
import org.netbeans.modules.analysis.ui.AdjustConfigurationPanel;
import org.netbeans.modules.analysis.ui.AnalysisResultTopComponent;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizer;
import org.netbeans.modules.refactoring.api.Scope;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lahvac
 */
public class RunAnalysis {

    private static final RequestProcessor WORKER = new RequestProcessor(RunAnalysisAction.class.getName(), 1, false, false);
    private static final int MAX_WORK = 1000;

    @Messages({"BN_Inspect=Inspect",
               "BN_Cancel=Cancel",
               "TL_Inspect=Inspect"})
    public static void showDialogAndRunAnalysis() {
        final ProgressHandle progress = ProgressHandleFactory.createHandle("Analyzing...", null, null);
        final RunAnalysisPanel rap = new RunAnalysisPanel(progress, Utilities.actionsGlobalContext());
        final JButton runAnalysis = new JButton(Bundle.BN_Inspect());
        JButton cancel = new JButton(Bundle.BN_Cancel());
        HelpCtx helpCtx = new HelpCtx("org.netbeans.modules.analysis.RunAnalysis");
        DialogDescriptor dd = new DialogDescriptor(rap, Bundle.TL_Inspect(), true, new Object[] {runAnalysis, cancel}, runAnalysis, DialogDescriptor.DEFAULT_ALIGN, helpCtx, null);
        dd.setClosingOptions(new Object[0]);
        final Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        final AtomicBoolean doCancel = new AtomicBoolean();
        final AtomicReference<Analyzer> currentlyRunning = new AtomicReference<Analyzer>();

        runAnalysis.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                runAnalysis.setEnabled(false);

                final AnalyzerFactory toRun = rap.getSelectedAnalyzer();
                final String configuration = rap.getConfiguration();
                final String singleWarningId = rap.getSingleWarningId();
                final Collection<? extends AnalyzerFactory> analyzers = rap.getAnalyzers();

                rap.started();
                progress.start();

                WORKER.post(new Runnable() {
                    @Override public void run() {
                        Scope scope = rap.getSelectedScope(doCancel);

                        progress.switchToDeterminate(MAX_WORK);

                        final Map<AnalyzerFactory, List<ErrorDescription>> result = new HashMap<AnalyzerFactory, List<ErrorDescription>>();

                        if (toRun == null) {
                            int doneSoFar = 0;
                            int bucketSize = MAX_WORK / analyzers.size();
                            for (AnalyzerFactory analyzer : analyzers) {
                                if (doCancel.get()) break;
                                doRunAnalyzer(analyzer, scope, progress, doneSoFar, bucketSize, result);
                                doneSoFar += bucketSize;
                            }
                        } else if (!doCancel.get()) {
                            doRunAnalyzer(toRun, scope, progress, 0, MAX_WORK, result);
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                if (!doCancel.get()) {
                                    AnalysisResultTopComponent.findInstance().setData(Lookups.fixed(), result);
                                    AnalysisResultTopComponent.findInstance().open();
                                    AnalysisResultTopComponent.findInstance().requestActive();
                                }

                                d.setVisible(false);
                                d.dispose();
                            }
                        });
                    }

                    private void doRunAnalyzer(AnalyzerFactory analyzer, Scope scope, ProgressHandle handle, int bucketStart, int bucketSize, final Map<AnalyzerFactory, List<ErrorDescription>> result) {
                        List<ErrorDescription> current = new ArrayList<ErrorDescription>();
                        Preferences settings = configuration != null ? getConfigurationSettingsRoot(configuration).node(SPIAccessor.ACCESSOR.getAnalyzerId(analyzer)) : null;
                        Analyzer a = analyzer.createAnalyzer(SPIAccessor.ACCESSOR.createContext(scope, settings, singleWarningId, handle, bucketStart, bucketSize));
                        currentlyRunning.set(a);
                        if (doCancel.get()) return;
                        for (ErrorDescription ed : a.analyze()) {
                            current.add(ed);
                        }
                        currentlyRunning.set(null);
                        if (!current.isEmpty())
                            result.put(analyzer, current);
                    }
                });
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                doCancel.set(true);
                Analyzer a = currentlyRunning.get();

                if (a != null) a.cancel();
                
                d.setVisible(false);
                d.dispose();
            }
        });

        d.setVisible(true);
    }

    public static Preferences getConfigurationsRoot() {
        return NbPreferences.forModule(AdjustConfigurationPanel.class).node("configurations");
    }

    public static Preferences getConfigurationSettingsRoot(String configuration) {
        return getConfigurationsRoot().node(configuration);
    }

    public static Iterable<? extends Configuration> readConfigurations() {
        List<Configuration> result = new ArrayList<Configuration>();
        Preferences root = getConfigurationsRoot();

        try {
            for (String configurationName : root.childrenNames()) {
                Preferences node = root.node(configurationName);
                String displayName = node != null ? node.get("displayName", null) : null;

                if (displayName != null) {
                    result.add(ConfigurationsManager.getDefault().getDefaultConfiguration());
                }
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }

        return result;
    }

    
    static Map<Project, Map<FileObject, ClassPath>> projects2RegisteredContent(AtomicBoolean cancel) {
        Set<String> sourceIds = new HashSet<String>();

        for (PathRecognizer pr : Lookup.getDefault().lookupAll(PathRecognizer.class)) {
            Set<String> ids = pr.getSourcePathIds();

            if (ids == null) continue;

            sourceIds.addAll(ids);
        }

        Map<Project, Map<FileObject, ClassPath>> sourceRoots = new IdentityHashMap<Project, Map<FileObject, ClassPath>>();

        for (String id : sourceIds) {
            for (ClassPath sCP : GlobalPathRegistry.getDefault().getPaths(id)) {
                for (FileObject root : sCP.getRoots()) {
                    if (cancel.get()) return null;
                    Project owner = FileOwnerQuery.getOwner(root);

                    if (owner != null) {
                        Map<FileObject, ClassPath> projectSources = sourceRoots.get(owner);

                        if (projectSources == null) {
                            sourceRoots.put(owner, projectSources = new HashMap<FileObject, ClassPath>());
                        }

                        projectSources.put(root, sCP);
                    }
                }
            }
        }

        return sourceRoots;
    }

    static Scope addProjectToScope(Project p, Scope target, AtomicBoolean cancel, Map<Project, Map<FileObject, ClassPath>> projects2RegisteredContent) {
        Scope projectScope = p.getLookup().lookup(Scope.class);

        if (projectScope != null) {
            return augment(target, projectScope.getSourceRoots(), projectScope.getFolders(), projectScope.getFiles());
        }

        Map<FileObject, ClassPath> roots = projects2RegisteredContent.get(p);

        if (roots != null) {
            for (Entry<FileObject, ClassPath> e : roots.entrySet()) {
                if (cancel.get()) return null;
                target = augment(target, Collections.singletonList(e.getKey()), null, null);
            }
        } else {
            for (SourceGroup sg : ProjectUtils.getSources(p).getSourceGroups(Sources.TYPE_GENERIC)) {
                if (cancel.get()) return null;
                target = augment(target, Collections.singletonList(sg.getRootFolder()), null, null);
            }
        }

        return target;
    }

    private static @NonNull Scope augment(@NonNull Scope source, @NullAllowed Collection<FileObject> sourceRoots,
                                        @NullAllowed Collection<NonRecursiveFolder> folders,
                                        @NullAllowed Collection<FileObject> files) {
        Collection<FileObject> sourceRootsSet = new HashSet<FileObject>(source.getSourceRoots());
        if (sourceRoots != null) {
            sourceRootsSet.addAll(sourceRoots);
        }
        Collection<FileObject> filesSet = new HashSet<FileObject>(source.getFiles());
        if (files != null) {
            filesSet.addAll(files);
        }
        Collection<NonRecursiveFolder> foldersSet = new HashSet<NonRecursiveFolder>(source.getFolders());
        if (folders != null) {
            foldersSet.addAll(folders);
        }
        return Scope.create(sourceRootsSet, foldersSet, filesSet);
    }
}
