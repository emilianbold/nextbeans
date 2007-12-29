/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.cnd.highlight.semantic;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.model.tasks.CaretAwareCsmFileTaskFactory;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceRepository;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceResolver;
import org.netbeans.modules.cnd.highlight.semantic.options.SemanticHighlightingOptions;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Sergey Grinev
 */
public class MarkOccurrencesHighlighter extends HighlighterBase {

    private static AttributeSet defaultColors;
    private final static String COLORS = "cc-highlighting-mark-occurences"; // NOI18N
    private WeakReference<CsmFile> weakFile;
    //private List<Annotation> annotations = new ArrayList<Annotation>();

    public static OffsetsBag getHighlightsBag(Document doc) {
        if (doc == null) {
            return null;
        }
        
        OffsetsBag bag = (OffsetsBag) doc.getProperty(MarkOccurrencesHighlighter.class);
        
        if (bag == null) {
            doc.putProperty(MarkOccurrencesHighlighter.class, bag = new OffsetsBag(doc, false));
            
            final OffsetsBag bagFin = bag;
            DocumentListener l = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    bagFin.removeHighlights(e.getOffset(), e.getOffset(), false);
                }
                public void removeUpdate(DocumentEvent e) {
                    bagFin.removeHighlights(e.getOffset(), e.getOffset(), false);
                }
                public void changedUpdate(DocumentEvent e) {}
            };
            
            doc.addDocumentListener(l);
        }

        return bag;
    }
    
    private CsmFile getCsmFile() {
        if (weakFile == null || weakFile.get() == null) {
            if (getDocument() == null) {
                return null;
            }
            DataObject dobj = NbEditorUtilities.getDataObject(getDocument());
            CsmFile file = CsmUtilities.getCsmFile(dobj, false);
            if (file != null) {
                weakFile = new WeakReference<CsmFile>(file);
            } else {
                return null;
            }
        }
        return weakFile.get();
    }
    
    private void clean() {
        if (getDocument()!=null) {
            getHighlightsBag(getDocument()).clear();
            /*for (Annotation annotation : annotations){
                if (annotation != null) {
                    NbDocument.removeAnnotation((StyledDocument)getDocument(), annotation);
                }
            }   */         
        }
        //annotations.clear();
    }

    public MarkOccurrencesHighlighter(Document doc, Runnable runMe) {
        super(doc, runMe);
    }

    public MarkOccurrencesHighlighter(Document doc) {
        super(doc);
    }


    // Runnable
    public void run() {
        //System.err.println("MarkOccurrencesHighlighter.run()");
        if (!SemanticHighlightingOptions.getEnableMarkOccurences()) {
            clean();
            return;
        }
        Collection<CsmReference> out = getOccurences();
        if (out == null) {
            if (SemanticHighlightingOptions.getKeepMarks()) {
                return;
            }
            out = Collections.<CsmReference>emptyList();
        }
        clean();
        for (CsmReference csmReference : out) {
            getHighlightsBag(getDocument()).addHighlight(csmReference.getStartOffset(), csmReference.getEndOffset(), defaultColors);
           // addAnnotation(csmReference.getStartOffset());
        }
    }
    
    private Collection<CsmReference> getOccurences() {
        Collection<CsmReference> out = null;
        CsmFile file = getCsmFile();
        if (file != null && file.isParsed() && getDocument() != null ) {
            FileObject fo = CsmUtilities.getFileObject(file);
            assert fo != null;
            CsmReference ref = CsmReferenceResolver.getDefault().findReference(file, CaretAwareCsmFileTaskFactory.getLastPosition(fo));
            if (ref!=null && ref.getReferencedObject()!=null) {
                out = CsmReferenceRepository.getDefault().getReferences(ref.getReferencedObject(), file, true);
            }
        }
        return out;
    }
    
    @Override
    protected void initFontColors(FontColorSettings fcs) {
        defaultColors = fcs.getTokenFontColors(COLORS);
    }

    public void cleanAfterYourself() {
        // removeAnnotations();
    }

    // annotations stuff
    /*private void addAnnotation(int offset) {
        Annotation ann = new OccurenceAnnotation(offset);
        NbDocument.addAnnotation((StyledDocument)getDocument(), new OccurencePosition(offset), -1, ann);
        annotations.add(ann);
    }

    public static class OccurencePosition implements Position {
        private int offset;
        public OccurencePosition(final int offset){
            this.offset = offset;
        }
        public int getOffset() {
            return offset;
        }
    }
    
    public static class OccurenceAnnotation extends Annotation {
        private int offset;
        public OccurenceAnnotation(int offset){
            this.offset = offset;
        }
        public String getAnnotationType() {
            return "org-netbeans-modules-cnd-highligh-semantic-markoccurences"; // NOI18N
        }
        public String getShortDescription() {
            return NbBundle.getMessage(MarkOccurrencesHighlighter.class, "CppParserMarkOccurencesAnnotation");
        }
    }*/
}