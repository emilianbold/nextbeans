/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vmd.analyzer;

import org.netbeans.modules.vmd.api.analyzer.Analyzer;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.DesignDocumentAwareness;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.openide.awt.UndoRedo;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 * @author David Kaspar
 */
public class AnalyzerEditorView implements DataEditorView, DesignDocumentAwareness {

    private static final Color BACKGROUND_COLOR = new Color (0xFBF9F3);

    private static final long serialVersionUID = -1;

    static final String ANALYZER_ID = "analyzer"; // NOI18N

    private DataObjectContext context;
    private transient HashMap<Analyzer, JComponent> components;
    private transient DesignDocument document;
    private transient JScrollPane scroll;
    private transient JPanel panel;
    private transient JPanel toolbar;

    public AnalyzerEditorView (DataObjectContext context) {
        this.context = context;
        init ();
    }

    private void init () {
        components = new HashMap<Analyzer, JComponent> ();
        Collection<? extends Analyzer> analyzers = Lookup.getDefault ().lookupResult (Analyzer.class).allInstances ();
        toolbar = new JPanel ();
        panel = new JPanel ();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder (BorderFactory.createEmptyBorder (1, 1, 1, 1));
        scroll = new JScrollPane (panel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets (10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = GridBagConstraints.REMAINDER;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        for (Analyzer analyzer : analyzers) {
            String projectType = context.getProjectType ();
            if (projectType == null  ||  ! projectType.equals (analyzer.getProjectType ()))
                continue;
            JComponent visualRepresentation = analyzer.createVisualRepresentation ();
            components.put (analyzer, visualRepresentation);
            if (visualRepresentation != null)
                panel.add (new AnalyzerPanel (analyzer, visualRepresentation), constraints);
        }
        constraints.weighty = 1.0;
        JPanel filler = new JPanel ();
        filler.setOpaque (false);
        panel.add (filler, constraints);
        context.addDesignDocumentAwareness (this);
    }

    public DataObjectContext getContext () {
        return context;
    }

    public DataEditorView.Kind getKind () {
        return DataEditorView.Kind.MODEL;
    }

    public boolean canShowSideWindows () {
        return true;
    }

    public Collection<String> getTags () {
        return Collections.emptySet ();
    }

    public String preferredID () {
        return ANALYZER_ID;
    }

    public String getDisplayName () {
        return "Analyzer"; // TODO
    }

    public HelpCtx getHelpCtx () {
        return new HelpCtx (AnalyzerEditorView.class);
    }

    public JComponent getVisualRepresentation () {
        return scroll;
    }

    public JComponent getToolbarRepresentation () {
        return toolbar;
    }

    public UndoRedo getUndoRedo () {
        return null;
    }

    public void componentOpened () {
    }

    public void componentClosed () {
    }

    public void componentShowing () {
    }

    public void componentHidden () {
    }

    public void componentActivated () {
        final DesignDocument doc = document;
        for (Map.Entry<Analyzer, JComponent> entry : components.entrySet ())
            entry.getKey ().update (entry.getValue (), doc);
    }

    public void componentDeactivated () {
    }

    public int getOpenPriority () {
        return 0;
    }

    public int getEditPriority () {
        return 0;
    }

    public int getOrder () {
        return 3000;
    }

    private void writeObject (java.io.ObjectOutputStream out) throws IOException {
        out.writeObject (context);
    }

    private void readObject (java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object object = in.readObject ();
        if (! (object instanceof DataObjectContext))
            throw new ClassNotFoundException ("DataObjectContext expected but not found");
        context = (DataObjectContext) object;
        init ();
    }

    public void setDesignDocument (DesignDocument designDocument) {
        document = designDocument;
        componentActivated ();
    }

}
