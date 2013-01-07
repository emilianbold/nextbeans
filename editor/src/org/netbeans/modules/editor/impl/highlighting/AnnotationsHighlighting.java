/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.editor.impl.highlighting;

import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.AnnotationType;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.spi.editor.highlighting.HighlightsChangeEvent;
import org.netbeans.spi.editor.highlighting.HighlightsChangeListener;
import org.netbeans.spi.editor.highlighting.HighlightsContainer;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Vita Stejskal
 */
public final class AnnotationsHighlighting extends AbstractHighlightsContainer implements Annotations.AnnotationsListener, HighlightsChangeListener {

    public static final String LAYER_TYPE_ID = "org.netbeans.modules.editor.oldlibbridge.AnnotationsHighlighting"; //NOI18N

    @SuppressWarnings("LeakingThisInConstructor")
    public AnnotationsHighlighting(Document document) {
        if (document instanceof BaseDocument) {
            this.document = (BaseDocument) document;
            this.annotations = this.document.getAnnotations();
            this.annotations.addAnnotationsListener(WeakListeners.create(Annotations.AnnotationsListener.class, this, this.annotations));
            this.bag = new OffsetsBag(document, true);
            this.bag.addHighlightsChangeListener(this);
            changedAll();
        } else {
            this.document = null;
            this.annotations = null;
            this.bag = null;
        }
    }

    public @Override HighlightsSequence getHighlights(int startOffset, int endOffset) {
        if (bag != null) {
            return bag.getHighlights(startOffset, endOffset);
        } else {
            return HighlightsSequence.EMPTY;
        }
    }

    // ----------------------------------------------------------------------
    //  HighlightsChangeListener implementation
    // ----------------------------------------------------------------------

    @Override
    public void highlightChanged(HighlightsChangeEvent event) {
        fireHighlightsChange(event.getStartOffset(), event.getEndOffset());
    }

    // ----------------------------------------------------------------------
    //  AnnotationsListener implementation
    // ----------------------------------------------------------------------

    @Override
    public void changedLine(final int line) {
        scheduleRefresh(isTyping() ? 343 : 43); //ms
    }

    @Override
    public void changedAll() {
        scheduleRefresh(isTyping() ? 343 : 43); //ms
    }

    // ----------------------------------------------------------------------
    //  Private implementation
    // ----------------------------------------------------------------------

    // -J-Dorg.netbeans.modules.editor.impl.highlighting.AnnotationsHighlighting.level=FINE
    private static final Logger LOG = Logger.getLogger(AnnotationsHighlighting.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(LAYER_TYPE_ID, 1, false, false);

    private final BaseDocument document;
    private final Annotations annotations;
    private final OffsetsBag bag;
    private final Map<AnnotationType, AttributeSet> cache = new WeakHashMap<AnnotationType, AttributeSet>();

    private R refreshAllLines = null;
    private RequestProcessor.Task refreshAllLinesTask = null;

    private void scheduleRefresh(int delay) {
        LOG.log(Level.FINE, "scheduleRefresh: delay={0}", delay); //NOI18N
        synchronized (this) {
            if (refreshAllLines == null) {
                refreshAllLines = new R();
                refreshAllLinesTask = RP.post(refreshAllLines, delay);
            } else {
                refreshAllLines.cancelled.set(true);
                refreshAllLinesTask.schedule(delay);
            }
        }
    }

    private static boolean isTyping() {
        JTextComponent jtc = EditorRegistry.focusedComponent();
        return jtc != null ? DocumentUtilities.isWriteLocked(jtc.getDocument()) : false;
    }

    private final class R implements Runnable {

        public final AtomicBoolean cancelled = new AtomicBoolean(false);
        
        public @Override void run() {
            cancelled.set(false);
            refreshAllLines();
            synchronized (AnnotationsHighlighting.this) {
                refreshAllLines = null;
                refreshAllLinesTask = null;
            }
        }

        private void refreshAllLines() {
            final OffsetsBag b = new OffsetsBag(document, true);

            try {
                for(int line = annotations.getNextLineWithAnnotation(0); line != -1; line = annotations.getNextLineWithAnnotation(line + 1)) {
                    if (cancelled.get()) {
                        return;
                    }
                    refreshLine(line, b, -1, -1);
                }
            } catch (Exception e) {
                // ignore, refreshLine is intentionally called outside of the document lock
                // in order not to block editing
                return;
            }

            if (!cancelled.get()) {
                document.render(new Runnable() {
                    public @Override void run() {
                        bag.setHighlights(b);
                    }
                });
            }
        }

        private void refreshLine(int line, OffsetsBag b, int lineStartOffset, int lineEndOffset) {
            LOG.log(Level.FINE, "Refreshing line {0}", line); //NOI18N

           AnnotationDesc [] allPassive = annotations.getPassiveAnnotationsForLine(line);
            if (allPassive != null) {
                for(AnnotationDesc passive : allPassive) {
                    AttributeSet attribs = getAttributes(passive.getAnnotationTypeInstance());
                    if (passive.isVisible()) {
                        if (passive.isWholeLine()) {
                            if (lineStartOffset == -1 || lineEndOffset == -1) {
                                Element lineElement = document.getDefaultRootElement().getElement(line);
                                lineStartOffset = lineElement.getStartOffset();
                                lineEndOffset = lineElement.getEndOffset();
                            }
                            b.addHighlight(lineStartOffset, lineEndOffset, attribs);
                        } else {
                            b.addHighlight(passive.getOffset(), passive.getOffset() + passive.getLength(), attribs);
                        }
                    }
                }
            }

            AnnotationDesc active = annotations.getActiveAnnotation(line);
            if (active != null && active.isVisible()) {
                AttributeSet attribs = getAttributes(active.getAnnotationTypeInstance());
                if (active.isWholeLine()) {
                    if (lineStartOffset == -1 || lineEndOffset == -1) {
                        Element lineElement = document.getDefaultRootElement().getElement(line);
                        lineStartOffset = lineElement.getStartOffset();
                        lineEndOffset = lineElement.getEndOffset();
                    }
                    b.addHighlight(lineStartOffset, lineEndOffset, attribs);
                } else {
                    b.addHighlight(active.getOffset(), active.getOffset() + active.getLength(), attribs);
                }
            }
        }

        private AttributeSet getAttributes(AnnotationType annotationType) {
            synchronized (cache) {
                AttributeSet attrs = cache.get(annotationType);
                if (attrs == null) {
                    LinkedList<Object> l = new LinkedList<Object>();
                    if (!annotationType.isInheritForegroundColor()) {
                        l.add(StyleConstants.Foreground);
                        l.add(annotationType.getForegroundColor());
                    }
                    if (annotationType.isUseHighlightColor()) {
                        l.add(StyleConstants.Background);
                        l.add(annotationType.getHighlight());
                    }
                    if (annotationType.isUseWaveUnderlineColor()) {
                        l.add(EditorStyleConstants.WaveUnderlineColor);
                        l.add(annotationType.getWaveUnderlineColor());
                    }
                    if (annotationType.isWholeLine()) {
                        l.add(HighlightsContainer.ATTR_EXTENDS_EMPTY_LINE);
                        l.add(Boolean.valueOf(annotationType.isWholeLine()));
                        l.add(HighlightsContainer.ATTR_EXTENDS_EOL);
                        l.add(Boolean.valueOf(annotationType.isWholeLine()));
                    }

                    attrs = AttributesUtilities.createImmutable(l.toArray());
                    cache.put(annotationType, attrs);
                }
                return attrs;
            }
        }
    }
}
