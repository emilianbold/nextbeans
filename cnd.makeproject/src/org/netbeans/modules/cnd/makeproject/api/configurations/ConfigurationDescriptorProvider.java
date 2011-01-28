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
package org.netbeans.modules.cnd.makeproject.api.configurations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.api.toolchain.CompilerSet;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationDescriptor.State;
import org.netbeans.modules.cnd.makeproject.platform.Platforms;
import org.netbeans.modules.cnd.makeproject.configurations.ConfigurationXMLReader;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.netbeans.modules.cnd.utils.ui.UIGesturesSupport;
import org.netbeans.modules.dlight.util.usagetracking.SunStudioUserCounter;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

public class ConfigurationDescriptorProvider {

    public static final String USG_PROJECT_CONFIG_CND = "USG_PROJECT_CONFIG_CND"; // NOI18N
    public static final String USG_PROJECT_OPEN_CND = "USG_PROJECT_OPEN_CND"; // NOI18N
    public static final String USG_PROJECT_CREATE_CND = "USG_PROJECT_CREATE_CND"; // NOI18N
    private static final String USG_CND_PROJECT_ACTION = "USG_CND_PROJECT_ACTION"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger("org.netbeans.modules.cnd.makeproject"); // NOI18N
    private final static RequestProcessor RP = new RequestProcessor("Configuration Updater", 1); // NOI18N
    private final FileObject projectDirectory;
    private Project project = null;
    private volatile MakeConfigurationDescriptor projectDescriptor = null;
    private volatile boolean hasTried = false;
    private String relativeOffset = null;
    private List<FileObject> trackedFiles;
    private volatile boolean needReload;

    public ConfigurationDescriptorProvider(FileObject projectDirectory) {
        this.project = null;
        this.projectDirectory = projectDirectory;
    }

    public ConfigurationDescriptorProvider(Project project, FileObject projectDirectory) {
        this.project = project;
        this.projectDirectory = projectDirectory;
    }

    public void setRelativeOffset(String relativeOffset) {
        this.relativeOffset = relativeOffset;
    }
    private final Object readLock = new Object();

    public MakeConfigurationDescriptor getConfigurationDescriptor() {
        return getConfigurationDescriptor(true);
    }

    private boolean shouldBeLoaded() {
        return ((projectDescriptor == null || needReload) && !hasTried);

    }

    public MakeConfigurationDescriptor getConfigurationDescriptor(boolean waitReading) {
        if (shouldBeLoaded()) {
            // attempt to read configuration descriptor
            // do this only once
            synchronized (readLock) {
                // check again that someone already havn't read
                if (shouldBeLoaded()) {
                    LOGGER.log(Level.FINE, "Start reading project descriptor for project {0} in ConfigurationDescriptorProvider@{1}", new Object[]{projectDirectory.getNameExt(), System.identityHashCode(this)}); // NOI18N
                    // It's important to set needReload=false before calling
                    // projectDescriptor.assign(), otherwise there will be
                    // infinite recursion.
                    needReload = false;

                    if (trackedFiles == null) {
                        FileChangeListener fcl = new ConfigurationXMLChangeListener();
                        List<FileObject> files = new ArrayList<FileObject>(2);
                        boolean first = true;
                        for (String path : new String[]{
                                    "nbproject/configurations.xml", //NOI18N
                                    "nbproject/private/configurations.xml"}) { //NOI18N
                            FileObject fo = projectDirectory.getFileObject(path);
                            if (fo != null) {
                                fo.addFileChangeListener(fcl);
                                // We have to store tracked files somewhere.
                                // Otherwise they will be GCed, and we won't get notifications.
                                files.add(fo);
                            } else {
                                if (first) {
                                    // prevent reading configurations before project cration
                                    new Exception("Attempt to read project before creation. Not found file " + projectDirectory.getPath() + "/" + path).printStackTrace(System.err); // NOI18N
                                    return null;
                                }
                            }
                            first = false;
                        }
                        trackedFiles = files;
                    }

                    ConfigurationXMLReader reader = new ConfigurationXMLReader(project, projectDirectory);

                    //                        if (waitReading && SwingUtilities.isEventDispatchThread()) {
                    //                            new Exception("Not allowed to use EDT for reading XML descriptor of project!" + projectDirectory).printStackTrace(System.err); // NOI18N
                    //                            // PLEASE DO NOT ADD HACKS like Task.waitFinished()
                    //                            // CHANGE YOUR LOGIC INSTEAD
                    //
                    //                            // FIXUP for IZ#146696: cannot open projects: Not allowed to use EDT...
                    //                            // return null;
                    //                        }
                    try {
                        MakeConfigurationDescriptor newDescriptor = reader.read(relativeOffset);
                        LOGGER.log(Level.FINE, "End of reading project descriptor for project {0} in ConfigurationDescriptorProvider@{1}", // NOI18N
                                new Object[]{projectDirectory.getNameExt(), System.identityHashCode(this)});
                        if (projectDescriptor == null) {
                            if (newDescriptor != null) {
                                projectDescriptor = newDescriptor;
                                LOGGER.log(Level.FINE, "Created project descriptor MakeConfigurationDescriptor@{0} for project {1} in ConfigurationDescriptorProvider@{2}", // NOI18N
                                        new Object[]{System.identityHashCode(projectDescriptor), projectDirectory.getNameExt(), System.identityHashCode(this)});
                            } else {
                                LOGGER.log(Level.FINE, "Cannot create project descriptor for project {0} in ConfigurationDescriptorProvider@{1}", // NOI18N
                                        new Object[]{projectDirectory.getNameExt(), System.identityHashCode(this)});
                            }
                        } else {
                            if (newDescriptor != null) {
                                newDescriptor.setProject(project);
                                newDescriptor.waitInitTask();
                                Delta delta = getDelta(newDescriptor);
                                projectDescriptor.assign(newDescriptor);
                                projectDescriptor.checkForChangedItems(delta);
                                LOGGER.log(Level.FINE, "Reassigned project descriptor MakeConfigurationDescriptor@{0} for project {1} in ConfigurationDescriptorProvider@{2}", // NOI18N
                                        new Object[]{System.identityHashCode(projectDescriptor), projectDirectory.getNameExt(), System.identityHashCode(this)});
                            } else {
                                LOGGER.log(Level.FINE, "cannot reassign project descriptor MakeConfigurationDescriptor@{0} for project {1} in ConfigurationDescriptorProvider@{2}", // NOI18N
                                        new Object[]{System.identityHashCode(projectDescriptor), projectDirectory.getNameExt(), System.identityHashCode(this)});
                            }
                        }
                    } catch (java.io.IOException x) {
                        x.printStackTrace(System.err);
                        // most likely open failed
                    }

                    hasTried = true;
                }
            }
            }
        if (waitReading && projectDescriptor != null) {
            projectDescriptor.waitInitTask();
        }
        return projectDescriptor;
    }

    private Delta getDelta(MakeConfigurationDescriptor newDescriptor) {
        Item[] oldItems = projectDescriptor.getProjectItems();
        Map<String, Item> oldMap = new HashMap<String, Item>();
        Set<Item> oldSet = new HashSet<Item>();
        for (Item item : oldItems) {
            oldMap.put(item.getAbsolutePath(), item);
            oldSet.add(item);
        }
        Delta delta = new Delta();
        Item[] newItems = newDescriptor.getProjectItems();
        for (Item item : newItems) {
            Item oldItem = oldMap.get(item.getAbsolutePath());
            if (oldItem == null) {
                delta.added.add(item);
            } else {
                oldSet.remove(oldItem);
                if (item.isExcluded() && oldItem.isExcluded()) {
                    // no changes
                    delta.replaced.add(item);
                } else if (item.isExcluded() && !oldItem.isExcluded()) {
                    delta.exluded.add(item);
                } else if (!item.isExcluded() && oldItem.isExcluded()) {
                    delta.included.add(item);
                } else {
                    // compare item properties
                    if (!(item.getUserIncludePaths().equals(oldItem.getUserIncludePaths())
                            && item.getUserMacroDefinitions().equals(oldItem.getUserMacroDefinitions()))) {
                        delta.changed.add(item);
                    } else {
                        delta.replaced.add(item);
                    }
                }
            }
        }
        for (Item item : oldSet) {
            delta.deleted.add(item);
        }
        return delta;
    }

    public boolean gotDescriptor() {
        return projectDescriptor != null && projectDescriptor.getState() != State.READING;
    }

    public static ConfigurationAuxObjectProvider[] getAuxObjectProviders() {
        HashSet<ConfigurationAuxObjectProvider> auxObjectProviders = new HashSet<ConfigurationAuxObjectProvider>();
        Collection<? extends ConfigurationAuxObjectProvider> collection =
                Lookup.getDefault().lookupAll(ConfigurationAuxObjectProvider.class);
//      System.err.println("-------------------------------collection " + collection);
        Iterator<? extends ConfigurationAuxObjectProvider> iterator = collection.iterator();
        while (iterator.hasNext()) {
            auxObjectProviders.add(iterator.next());
        }
//      System.err.println("-------------------------------auxObjectProviders " + auxObjectProviders);
        return auxObjectProviders.toArray(new ConfigurationAuxObjectProvider[auxObjectProviders.size()]);
    }

    public static void recordMetrics(String msg, MakeConfigurationDescriptor descr) {
        recordMetricsImpl(msg, null, descr, null);
    }

    public static void recordCreatedProjectMetrics(MakeConfiguration[] confs) {
        if (confs != null && confs.length > 0) {
            recordMetricsImpl(USG_PROJECT_CREATE_CND, confs[0], null, null);
        }
    }

    public static void recordActionMetrics(String action, MakeConfigurationDescriptor descr) {
        recordMetricsImpl(USG_CND_PROJECT_ACTION, null, descr, action);
    }

    private static void recordMetricsImpl(String msg,
            MakeConfiguration makeConfiguration,
            MakeConfigurationDescriptor descr,
            String action) {
        if (CndUtils.isUnitTestMode()) {
            // we don't want to count own tests
            return;
        }
        if (descr == null && makeConfiguration == null) {
            return;
        }
        Item[] projectItems = null;
        if (makeConfiguration == null) {
            if (descr.getConfs() == null || descr.getConfs().getActive() == null) {
                return;
            }
            if (makeConfiguration == null) {
                makeConfiguration = descr.getActiveConfiguration();
            }
            projectItems = (descr).getProjectItems();
            if (!USG_PROJECT_CREATE_CND.equals(msg) && (projectItems == null || projectItems.length == 0)) {
                // do not track empty applications
                return;
            }
        }
        String type;
        switch (makeConfiguration.getConfigurationType().getValue()) {
            case MakeConfiguration.TYPE_MAKEFILE:
                type = "MAKEFILE"; // NOI18N
                break;
            case MakeConfiguration.TYPE_APPLICATION:
                type = "APPLICATION"; // NOI18N
                break;
            case MakeConfiguration.TYPE_DYNAMIC_LIB:
                type = "DYNAMIC_LIB"; // NOI18N
                break;
            case MakeConfiguration.TYPE_STATIC_LIB:
                type = "STATIC_LIB"; // NOI18N
                break;
            case MakeConfiguration.TYPE_QT_APPLICATION:
                type = "QT_APPLICATION"; // NOI18N
                break;
            case MakeConfiguration.TYPE_QT_DYNAMIC_LIB:
                type = "QT_DYNAMIC_LIB"; // NOI18N
                break;
            case MakeConfiguration.TYPE_QT_STATIC_LIB:
                type = "QT_STATIC_LIB"; // NOI18N
                break;
            default:
                type = "UNKNOWN"; // NOI18N
        }
        String host;
        CompilerSet compilerSet;
        if (makeConfiguration.getDevelopmentHost().isLocalhost()) {
            host = "LOCAL"; // NOI18N
            compilerSet = makeConfiguration.getCompilerSet().getCompilerSet();
        } else {
            host = "REMOTE"; // NOI18N
            // do not force creation of compiler sets
            compilerSet = null;
        }
        String flavor;
        String[] families;
        if (compilerSet != null) {
            families = compilerSet.getCompilerFlavor().getToolchainDescriptor().getFamily();
            flavor = compilerSet.getCompilerFlavor().toString();
        } else {
            families = new String[0];
            if (makeConfiguration.getCompilerSet() != null) {
                families = new String[]{makeConfiguration.getCompilerSet().getName()};
            }
            flavor = makeConfiguration.getCompilerSet().getFlavor();
        }
        String family;
        if (families.length == 0) {
            family = flavor; // NOI18N
        } else {
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < families.length; i++) {
                if (families[i] != null) {
                    buffer.append(families[i]);
                    if (i < families.length - 1) {
                        buffer.append(","); // NOI18N
                    }
                }
            }
            family = buffer.toString();
        }
        String platform;
        int platformID = makeConfiguration.getDevelopmentHost().getBuildPlatform();
        if (Platforms.getPlatform(platformID) != null) {
            platform = Platforms.getPlatform(platformID).getName();
        } else {
            platform = "UNKNOWN_PLATFORM"; // NOI18N
        }

        String ideType = SunStudioUserCounter.getIDEType().getTag();
        if (USG_PROJECT_CREATE_CND.equals(msg)) {
            // stop here
            UIGesturesSupport.submit(msg, type, flavor, family, host, platform, "USER_PROJECT", ideType); //NOI18N
        } else if (USG_CND_PROJECT_ACTION.equals(msg)) {
            UIGesturesSupport.submit(msg, action, type, flavor, family, host, platform, ideType); //NOI18N
        } else if (projectItems != null) {
            makeConfiguration.reCountLanguages(descr);
            int size = 0;
            int allItems = projectItems.length;
            boolean cLang = false;
            boolean ccLang = false;
            boolean fLang = false;
            boolean aLang = false;
            for (Item item : projectItems) {
                ItemConfiguration itemConfiguration = item.getItemConfiguration(makeConfiguration);
                if (itemConfiguration != null && !itemConfiguration.getExcluded().getValue()) {
                    size++;
                    switch (itemConfiguration.getTool()) {
                        case CCompiler:
                            cLang = true;
                            break;
                        case CCCompiler:
                            ccLang = true;
                            break;
                        case FortranCompiler:
                            fLang = true;
                            break;
                        case Assembler:
                            aLang = true;
                            break;
                    }
                }
            }
            String ccUsage = ccLang ? "USE_CPP" : "NO_CPP"; // NOI18N
            String cUsage = cLang ? "USE_C" : "NO_C"; // NOI18N
            String fUsage = fLang ? "USE_FORTRAN" : "NO_FORTRAN"; // NOI18N
            String aUsage = aLang ? "USE_ASM" : "NO_ASM"; // NOI18N
            UIGesturesSupport.submit(msg, type, flavor, family, host, platform, toSizeString(allItems), toSizeString(size), ccUsage, cUsage, fUsage, aUsage, ideType);
        }
    }

    private static String toSizeString(int size) {
        String strSize;
        if (size < 25) {
            strSize = "25"; // NOI18N
        } else if (size < 100) {
            strSize = "100"; // NOI18N
        } else if (size < 500) {
            strSize = "500"; // NOI18N
        } else if (size < 1000) {
            strSize = "1000"; // NOI18N
        } else if (size < 2000) {
            strSize = "2000"; // NOI18N
        } else if (size < 5000) {
            strSize = "5000"; // NOI18N
        } else if (size < 10000) {
            strSize = "10000"; // NOI18N
        } else if (size < 20000) {
            strSize = "20000"; // NOI18N
        } else if (size < 50000) {
            strSize = "50000"; // NOI18N
        } else {
            strSize = "99999"; // NOI18N
        }
        return strSize;
    }

    /**
     * This listener will be notified about updates of files
     * <code>nbproject/configurations.xml</code> and
     * <code>nbproject/private/configurations.xml</code>.
     * These files should be reloaded when changed externally.
     * See IZ#146701: can't update project through subversion, or any other
     */
    private class ConfigurationXMLChangeListener implements FileChangeListener {

        private void resetConfiguration() {
            if (projectDescriptor == null || !projectDescriptor.isModified()) {
                synchronized (readLock) {
                    if (projectDescriptor == null || !projectDescriptor.isModified()) {
                        // Don't reload if descriptor is modified in memory.
                        // This also prevents reloading when descriptor is being saved.
                        LOGGER.log(Level.FINE, "Mark to reload project descriptor MakeConfigurationDescriptor@{0} for project {1} in ConfigurationDescriptorProvider@{2}", new Object[]{System.identityHashCode(projectDescriptor), projectDirectory.getNameExt(), System.identityHashCode(this)}); // NOI18N
                        needReload = true;
                        hasTried = false;
                        RP.post(new Runnable() {

                            @Override
                            public void run() {
                                getConfigurationDescriptor();
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
            resetConfiguration();
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            resetConfiguration();
        }

        @Override
        public void fileChanged(FileEvent fe) {
            resetConfiguration();
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            resetConfiguration();
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            resetConfiguration();
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
            // Don't reset configuration on file attribute change.
        }
    }

    public static final class Delta {

        public List<Item> included = new ArrayList<Item>(); // marked as included
        public List<Item> added = new ArrayList<Item>(); // added in project
        public List<Item> exluded = new ArrayList<Item>(); // marked as excluded
        public List<Item> deleted = new ArrayList<Item>(); // deleted from project items
        public List<Item> changed = new ArrayList<Item>(); // changed properties
        public List<Item> replaced = new ArrayList<Item>(); // properties were not changed (from code model point of view) but instance was replaced

        public boolean isEmpty() {
            return included.isEmpty() && added.isEmpty() && exluded.isEmpty() && deleted.isEmpty() && changed.isEmpty();
        }
    }
}
