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

package threaddemo.apps.refactor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import org.openide.cookies.SaveCookie;
import org.w3c.dom.*;
import threaddemo.data.DomProvider;
import threaddemo.data.PhadhailLookups;
import threaddemo.locking.LockAction;
import threaddemo.model.Phadhail;

/**
 * Simulates some big model-based refactoring of files.
 * In this case, increments the number in all tag-nnn elements in XML files.
 * @author Jesse Glick
 */
public class Refactor {
    
    /** No instances. */
    private Refactor() {}
    
    /**
     * Begin a refactoring session.
     * Call from event thread; will proceed in its own thread.
     * @param root the root of the tree of phadhails to work on
     * @param app owner app, or null
     */
    public static void run(final Phadhail root, Frame app) {
        final Map/*<Phadhail,DomProvider>*/ data = collectData(root);
        final BoundedRangeModel progress = new DefaultBoundedRangeModel();
        progress.setMinimum(0);
        progress.setMaximum(data.size());
        progress.setValue(0);
        final JProgressBar progressBar = new JProgressBar(progress);
        progressBar.setStringPainted(true);
        Dimension d = progressBar.getPreferredSize();
        d.width = 500;
        progressBar.setPreferredSize(d);
        final JDialog dialog = new JDialog(app, "Refactoring...", false);
        JLabel label = new JLabel("Progress:");
        label.setLabelFor(progressBar);
        final boolean[] cancelled = new boolean[] {false};
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                cancelled[0] = true;
            }
        });
        dialog.getContentPane().setLayout(new FlowLayout());
        dialog.getContentPane().add(label);
        dialog.getContentPane().add(progressBar);
        dialog.getContentPane().add(cancel);
        dialog.pack();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.show();
        new Thread(new Runnable() {
            public void run() {
                final Iterator/*<Map.Entry<Phadhail,DomProvider>>*/ it = data.entrySet().iterator();
                while (it.hasNext() && !cancelled[0]) {
                    Map.Entry e = (Map.Entry)it.next();
                    final Phadhail ph = (Phadhail)e.getKey();
                    final DomProvider p = (DomProvider)e.getValue();
                    ph.lock().read(new Runnable() {
                        public void run() {
                            // Avoid keeping a reference to the old data, since we have
                            // cached DomProvider's and such heavyweight stuff open on them:
                            it.remove(); // do from inside lock - calls Phadhail.hashCode
                            final String path = ph.getPath();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    progress.setValue(progress.getValue() + 1);
                                    progressBar.setString(path);
                                }
                            });
                        }
                    });
                    ph.lock().write(new Runnable() {
                        public void run() {
                            SaveCookie s = (SaveCookie)PhadhailLookups.getLookup(ph).lookup(SaveCookie.class);
                            refactor(p);
                            if (s == null) {
                                // Was unmodified before, so save it now.
                                s = (SaveCookie)PhadhailLookups.getLookup(ph).lookup(SaveCookie.class);
                                if (s != null) {
                                    try {
                                        s.save();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setVisible(false);
                    }
                });
            }
        }, "Refactoring").start();
    }
    
    private static Map/*<Phadhail,DomProvider>*/ collectData(final Phadhail root) {
        return (Map)root.lock().read(new LockAction() {
            private final Map data = new HashMap(); 
            public Object run() {
                collect(root);
                return data;
            }
            private void collect(Phadhail ph) {
                if (ph.hasChildren()) {
                    Iterator/*<Phadhail>*/ it = ph.getChildren().iterator();
                    while (it.hasNext()) {
                        collect((Phadhail)it.next());
                    }
                } else {
                    DomProvider p = (DomProvider)PhadhailLookups.getLookup(ph).lookup(DomProvider.class);
                    if (p != null) {
                        data.put(ph, p);
                    }
                }
            }
        });
    }
    
    private static void refactor(DomProvider p) {
        final Document doc;
        try {
            doc = p.getDocument();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        NodeList nl = doc.getElementsByTagName("*");
        List/*<Element>*/ l = new ArrayList();
        for (int i = 0; i < nl.getLength(); i++) {
            l.add(nl.item(i));
        }
        final Iterator it = l.iterator();
        p.isolatingChange(new Runnable() {
            public void run() {
                while (it.hasNext()) {
                    Element el = (Element)it.next();
                    String tagname = el.getTagName();
                    if (tagname.startsWith("tag-")) {
                        int n = Integer.parseInt(tagname.substring(4));
                        tagname = "tag-" + (n + 1);
                        Element el2 = doc.createElement(tagname);
                        Node parent = el.getParentNode();
                        parent.insertBefore(el2, el);
                        NodeList nl2 = el.getChildNodes();
                        while (nl2.getLength() > 0) {
                            el2.appendChild(nl2.item(0));
                        }
                        parent.removeChild(el);
                    }
                }
            }
        });
        /*
        org.apache.xml.serialize.XMLSerializer ser = new org.apache.xml.serialize.XMLSerializer(System.err, new org.apache.xml.serialize.OutputFormat(doc, "UTF-8", true));
        try {
            ser.serialize(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }
    
}
