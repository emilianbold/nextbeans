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
package org.netbeans.modules.cnd.modelimpl.platform;

import javax.swing.event.ChangeEvent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.mimelookup.MimeRegistrations;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.api.model.CsmModelAccessor;
import org.netbeans.modules.cnd.debug.CndTraceFlags;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.csm.core.ProjectBase;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Egor Ushakov <gorrus@netbeans.org>
 */
public class CndIndexer extends CustomIndexer {

    @Override
    protected void index(Iterable<? extends Indexable> files, Context context) {
        if (!CndTraceFlags.USE_INDEXING_API) {
            return;
        }
        if (!files.iterator().hasNext()) {
            return;
        }
        FileObject root = context.getRoot();
        ProjectBase project = getProject(context);
        if (project == null) {
            return;
        }
        for (Indexable idx : files) {
            FileObject fo = root.getFileObject(idx.getRelativePath());
            FileImpl file = project.getFile(fo.getPath(), false);
            if (file != null) {
                project.onFileImplExternalChange(file);
            }
        }
    }
    
    private static ProjectBase getProject(Context context) {
        FileObject root = context.getRoot();
        Project prj = FileOwnerQuery.getOwner(root);
        if (prj == null) {
            return null;
        }
        return (ProjectBase)CsmModelAccessor.getModel().getProject(prj);
    }
    
    public static final String NAME = "cnd";
    
    @MimeRegistrations({
        @MimeRegistration(mimeType = MIMENames.C_MIME_TYPE, service = CustomIndexerFactory.class),
        @MimeRegistration(mimeType = MIMENames.CPLUSPLUS_MIME_TYPE, service = CustomIndexerFactory.class),
        @MimeRegistration(mimeType = MIMENames.HEADER_MIME_TYPE, service = CustomIndexerFactory.class)
    })
    public static final class Factory extends CustomIndexerFactory {

        @Override
        public CustomIndexer createIndexer() {
            return new CndIndexer();
        }

        @Override
        public boolean supportsEmbeddedIndexers() {
            return false;
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
            if (CndTraceFlags.USE_INDEXING_API) {
                ModelSupport.instance().modifiedListener.stateChanged(new ChangeEvent(this));
            }
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return 1;
        }
    }
}
