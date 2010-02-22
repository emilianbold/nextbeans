/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
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
package org.netbeans.modules.options.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.modules.editor.settings.storage.api.EditorSettings;
import org.netbeans.modules.options.editor.spi.OptionsFilter;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Jan Jancura, Dusan Balek
 */
public final class FolderBasedController extends OptionsPanelController implements PropertyChangeListener {

    private static final String OPTIONS_SUB_FOLDER = "optionsSubFolder"; //NOI18N
    private static final String HELP_CTX_ID = "helpContextId"; //NOI18N
    private static final String ALLOW_FILTERING = "allowFiltering"; //NOI18N
    private static final String BASE_FOLDER = "OptionsDialog/Editor/"; //NOI18N

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final String folder;
    private final HelpCtx helpCtx;
    private Lookup masterLookup;
    private FolderBasedOptionPanel panel;
    private Map<String, OptionsPanelController> mimeType2delegates;
    private final boolean allowFiltering;
    private final Set<String> supportFiltering = new HashSet<String>();
    private final Document filterDocument = new PlainDocument();

    @OptionsPanelController.SubRegistration(
        id="Hints",
        displayName="#CTL_Hints_DisplayName",
        location="Editor",
        keywords="#KW_Hints",
        keywordsCategory="Editor/Hints",
        position=400
//        toolTip="#CTL_Hints_ToolTip"
    )
    public static OptionsPanelController hints() {
        return new FolderBasedController("Hints/", "netbeans.optionsDialog.editor.hints", true);
    }

    @OptionsPanelController.SubRegistration(
        id="MarkOccurrences",
        displayName="#CTL_MarkOccurences_DisplayName",
        location="Editor",
        keywords="#KW_Mark",
        keywordsCategory="Editor/MarkOccurrences",
        position=500
//        toolTip="#CTL_MarkOccurences_ToolTip"
    )
    public static OptionsPanelController markOccurrences() {
        return new FolderBasedController("MarkOccurrences/", "netbeans.optionsDialog.editor.markOccurences", false);
    }

    public static OptionsPanelController create (Map args) {
        FolderBasedController folderBasedController = new FolderBasedController(
                (String) args.get (OPTIONS_SUB_FOLDER),
                (String) args.get (HELP_CTX_ID),
                (Boolean) args.get (ALLOW_FILTERING)
        );

        return folderBasedController;
    }

    private FolderBasedController(String subFolder, String helpCtxId, boolean allowFiltering) {
        folder = subFolder != null ? BASE_FOLDER + subFolder : BASE_FOLDER;
        helpCtx = helpCtxId != null ? new HelpCtx(helpCtxId) : null;
        this.allowFiltering = allowFiltering;
    }    
    
    public final synchronized void update() {
        for (Entry<String, OptionsPanelController> e : getMimeType2delegates ().entrySet()) {
            AtomicBoolean used = new AtomicBoolean();
            OptionsFilter f = createTreeModelFilter(filterDocument, used);
            Lookup innerLookup = new ProxyLookup(masterLookup, Lookups.singleton(f));
            OptionsPanelController c = e.getValue();
            c.getComponent(innerLookup);
            c.update();
            if (used.get()) this.supportFiltering.add(e.getKey());
        }

        assert panel != null;
        panel.update ();
    }
    
    public final synchronized void applyChanges() {
        Collection<? extends OptionsPanelController> controllers = getMimeType2delegates ().values();
        for(OptionsPanelController c : controllers) {
            c.applyChanges();
        }

        mimeType2delegates = null;
    }
    
    public final synchronized void cancel() {
        Collection<? extends OptionsPanelController> controllers = getMimeType2delegates ().values();
        for(OptionsPanelController c : controllers) {
            c.cancel();
        }
        
        mimeType2delegates = null;
    }
    
    public final synchronized boolean isValid() {
        Collection<? extends OptionsPanelController> controllers = getMimeType2delegates ().values();
        for(OptionsPanelController c : controllers) {
            if (!c.isValid()) {
                return false;
            }
        }
        return true;
    }
    
    public final synchronized boolean isChanged() {
        Collection<? extends OptionsPanelController> controllers = getMimeType2delegates ().values();
        for(OptionsPanelController c : controllers) {
            if (c.isChanged()) {
                return true;
            }
        }
        return false;
    }
    
    public final HelpCtx getHelpCtx() {
        return helpCtx;
    }

    @Override
    public synchronized JComponent getComponent(Lookup masterLookup) {
        if (panel == null) {
            this.masterLookup = masterLookup;
            for (Entry<String, OptionsPanelController> e : getMimeType2delegates ().entrySet()) {
                AtomicBoolean used = new AtomicBoolean();
                OptionsFilter f = createTreeModelFilter(filterDocument, used);
                Lookup innerLookup = new ProxyLookup(masterLookup, Lookups.singleton(f));
                OptionsPanelController controller = e.getValue();
                controller.getComponent(innerLookup);
                controller.addPropertyChangeListener(this);
                if (used.get()) this.supportFiltering.add(e.getKey());
            }
            panel = new FolderBasedOptionPanel(this, filterDocument, allowFiltering);
        }
        return panel;
    }
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
	pcs.addPropertyChangeListener(l);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
	pcs.removePropertyChangeListener(l);
    }
        
    @Override
    public Lookup getLookup() {
        return super.getLookup();
    }
    
    Iterable<String> getMimeTypes() {
        return getMimeType2delegates ().keySet();
    }
    
    OptionsPanelController getController(String mimeType) {
        return getMimeType2delegates ().get(mimeType);
    }

    private Map<String, OptionsPanelController> getMimeType2delegates () {
        if (mimeType2delegates == null) {
            mimeType2delegates = new LinkedHashMap<String, OptionsPanelController>();
            for (String mimeType : EditorSettings.getDefault().getAllMimeTypes()) {
                Lookup l = Lookups.forPath(folder + mimeType);
                OptionsPanelController controller = l.lookup(OptionsPanelController.class);
                if (controller != null) {
                    mimeType2delegates.put(mimeType, controller);
                }
            }
        }
        return mimeType2delegates;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    boolean supportsFilter(String mimeType) {
        return supportFiltering.contains(mimeType);
    }

    private static OptionsFilterAccessor filterAccessor;

    public static void setFilterAccessor(OptionsFilterAccessor filterAccessor) {
        FolderBasedController.filterAccessor = filterAccessor;
    }

    public static OptionsFilter createTreeModelFilter(Document doc, AtomicBoolean used) {
        if (filterAccessor == null) {
            try {
                Class.forName(OptionsFilter.class.getName(), true, OptionsFilter.class.getClassLoader());   //NOI18N
                assert filterAccessor != null;
            } catch (ClassNotFoundException e) {
                Exceptions.printStackTrace(e);
            }
        }
        return filterAccessor.create(doc, used);
    }

    public interface OptionsFilterAccessor {
        public OptionsFilter create(Document doc, AtomicBoolean used);
    }
}
