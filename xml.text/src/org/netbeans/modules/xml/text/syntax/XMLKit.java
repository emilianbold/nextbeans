/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.text.syntax;

import java.awt.event.ActionEvent;
import java.awt.Panel;
import java.util.*;

// prevent ambiguous reference to Utilities
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;

import javax.swing.event.*;
import javax.swing.*;

import org.openide.*;
import org.openide.awt.StatusDisplayer;

// we depend on NetBeans editor stuff
import org.netbeans.editor.*;
import org.netbeans.editor.ext.*;
import org.netbeans.modules.editor.*;

import org.netbeans.modules.xml.core.XMLDataObject;
import org.netbeans.modules.xml.text.completion.NodeSelector;


/**
 * NetBeans editor kit implementation for xml content type.
 * <p>
 * It provides syntax coloring, code completion, actions, abbrevirations, ...
 *
 * @author Libor Kramolis
 * @author Petr Kuzel
 * @author Sandeep
 */
public class XMLKit extends UniKit {

    /** Serial Version UID */
    private static final long serialVersionUID =5326735092324267367L;
    
    // comment action name
    public static final String xmlCommentAction = "xml-comment";
    
    // uncomment action name
    public static final String xmlUncommentAction = "xml-uncomment";

    // dump XML sysntax
    public static final String xmlTestAction = "xml-dump";
    
    // hack to be settings browseable //??? more info needed
    public static Map settings;
    
    /** Create new instance of syntax coloring parser */
    public Syntax createSyntax(Document doc) {
        return new XMLDefaultSyntax();
//        return new JJEditorSyntax(
//            new XMLSyntaxTokenManager(null).new Bridge(),
//            new XMLSyntaxTokenMapper(),
//            XMLTokenContext.contextPath
//        );
    }

    public Document createDefaultDocument() {
        return new NbEditorDocument (this.getClass());
    }


    /** Create syntax support */
    public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
        return new XMLSyntaxSupport(doc);
    }
    

    public Completion createCompletion(ExtEditorUI extEditorUI) {
        return new org.netbeans.modules.xml.text.completion.XMLCompletion(extEditorUI);
    }
    
    public void install(JEditorPane c) {
        super.install(c);
        if (Boolean.getBoolean("netbeans.experimental.xml.nodeselectors")) {  // NOI18N
            new NodeSelector(c);
        }
    }

    // hack to be settings browseable //??? more info needed    
    public static void setMap(Map map) {
        settings = map;
    }

    // hack to be settings browseable //??? more info needed        
    public Map getMap() {
        return settings;
    }

    //??? +xml handling
    public String getContentType() {
        return XMLDataObject.MIME_TYPE;
    }

    /**
     * Provide XML related actions.
     */
    protected Action[] createActions() {
        Action[] actions = new Action[] {
            new XMLCommentAction(),
            new XMLUncommentAction(),
            new TestAction(),
        };
        return TextAction.augmentList(super.createActions(), actions);
    }
    
    
    public abstract static class XMLEditorAction extends BaseAction {
        
        public XMLEditorAction (String id) {
            super(id);
        }
        
        /**
         * Uniform way of reporting problem while action executing #15589
         */
        protected void problem(String reason) {
            if (reason != null) StatusDisplayer.getDefault().setStatusText("Cannot proceed: " + reason);
            new Panel().getToolkit().beep();
        }
    }
    
    /**
     * Comment out editor selection.
     */
    public static class XMLCommentAction extends XMLEditorAction {
        
        private static final long serialVersionUID =4004056745446061L;

        private static final String commentStartString = "<!--";  //NOI18N
        private static final String commentEndString = "-->";  //NOI18N
        
        public XMLCommentAction() {
            super( xmlCommentAction);
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            if (target == null) return;
            if (!target.isEditable() || !target.isEnabled()) {
                problem(null);
                return;
            }
            Caret caret = target.getCaret();
            BaseDocument doc = (BaseDocument)target.getDocument();
            try {
                if (caret.isSelectionVisible()) {
                    int startPos = Utilities.getRowStart(doc, target.getSelectionStart());
                    int endPos = target.getSelectionEnd();
                    doc.atomicLock();
                    try {

                        if (endPos > 0 && Utilities.getRowStart(doc, endPos) == endPos) {
                            endPos--;
                        }

                        int pos = startPos;
                        int lineCnt = Utilities.getRowCount(doc, startPos, endPos);                            

                        for (;lineCnt > 0; lineCnt--) {
                            doc.insertString(pos, commentStartString, null); 
                            doc.insertString(Utilities.getRowEnd(doc,pos), commentEndString, null);
                            pos = Utilities.getRowStart(doc, pos, +1);
                        }

                    } finally {
                        doc.atomicUnlock();
                    }
                } else { // selection not visible
                    doc.insertString(Utilities.getRowStart(doc, target.getSelectionStart()),
                        commentStartString, null);
                    doc.insertString(Utilities.getRowEnd(doc, target.getSelectionStart()),
                        commentEndString, null);
                }
            } catch (BadLocationException e) {
                problem(null);
            }
        }
        
    }

    /**
     * Uncomment selected text
     *
     */
    public static class XMLUncommentAction extends XMLEditorAction {
        private static final String commentStartString = "<!--";  //NOI18N
        private static final String commentEndString = "-->";  //NOI18N
        private static final char[] commentStart = {'<','!','-','-'};
        private static final char[] commentEnd = {'-','-','>'};
        
        static final long serialVersionUID = 40040567454546061L;
        
        public XMLUncommentAction() {
            super( xmlUncommentAction);
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            if (target == null) return;
            if (!target.isEditable() || !target.isEnabled()) {
                problem(null);
                return;
            }
            Caret caret = target.getCaret();
            BaseDocument doc = (BaseDocument)target.getDocument();
            try {
                if (caret.isSelectionVisible()) {
                    int startPos = Utilities.getRowStart(doc, target.getSelectionStart());
                    int endPos = target.getSelectionEnd();
                    doc.atomicLock();
                    try {

                        if (endPos > 0 && Utilities.getRowStart(doc, endPos) == endPos) {
                            endPos--;
                        }

                        int pos = startPos;
                        int lineCnt = Utilities.getRowCount(doc, startPos, endPos);
                        char[] startChars, endChars;

                        for (; lineCnt > 0; lineCnt-- ) {
                            startChars = doc.getChars(pos, 4 );
                            endChars = doc.getChars(Utilities.getRowEnd(doc,pos)-3, 3 );

                            if(startChars[0] == commentStart[0] && startChars[1] == commentStart[1] &&
                                startChars[2] == commentStart[2] && startChars[3] == commentStart[3] &&
                                endChars[0] == commentEnd[0] && endChars[1] == commentEnd[1] && endChars[2] == commentEnd[2] ){

                                doc.remove(pos,4);
                                doc.remove(Utilities.getRowEnd(doc,pos)-3,3);
                            }                                
                            pos = Utilities.getRowStart(doc, pos, +1);
                        }

                    } finally {
                        doc.atomicUnlock();
                    }
                } else { // selection not visible
                  char[] startChars = doc.getChars(target.getSelectionStart(), 4 );
                  char[] endChars = doc.getChars(Utilities.getRowEnd(doc,target.getSelectionStart())-3, 3 );
                  if(startChars[0] == commentStart[0] && startChars[1] == commentStart[1] &&
                                startChars[2] == commentStart[2] && startChars[3] == commentStart[3] &&
                                endChars[0] == commentEnd[0] && endChars[1] == commentEnd[1] && endChars[2] == commentEnd[2] ){
                        doc.remove(target.getSelectionStart(),4);
                        doc.remove(Utilities.getRowEnd(doc,target.getSelectionStart())-3,3);
                    }
                }
            } catch (BadLocationException e) {
                problem(null);
            }
        }
    }    

    
    /**
     * Dump it.
     */
    public static class TestAction extends XMLEditorAction {
        
        private static final long serialVersionUID =4004056745446099L;

        public TestAction() {
            super( xmlTestAction);
        }
        
        public void actionPerformed(ActionEvent evt, JTextComponent target) {
            if (target == null) return;
            if (!target.isEditable() || !target.isEnabled()) {
                problem(null);
                return;
            }
            Caret caret = target.getCaret();
            BaseDocument doc = (BaseDocument)target.getDocument();
            try {
                doc.dump(System.out);    
                if (target == null)  throw new BadLocationException(null,0);  // folish compiler
            } catch (BadLocationException e) {
                problem(null);
            }
        }
        
    }
    
}
