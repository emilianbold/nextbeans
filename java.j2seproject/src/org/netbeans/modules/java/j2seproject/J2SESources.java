/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seproject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;
import org.openide.util.Mutex;
import org.openide.util.RequestProcessor;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;

public class J2SESources implements Sources, PropertyChangeListener  {

    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private final SourceRoots sourceRoots;
    private final SourceRoots testRoots;
    private Sources delegate;
    private final List/*<ChangeListener>*/ listeners = new ArrayList();

    J2SESources(AntProjectHelper helper, PropertyEvaluator evaluator,
                SourceRoots sourceRoots, SourceRoots testRoots) {
        this.helper = helper;
        this.evaluator = evaluator;
        this.sourceRoots = sourceRoots;
        this.testRoots = testRoots;
        this.sourceRoots.addPropertyChangeListener(this);
        this.testRoots.addPropertyChangeListener(this);
        initSources(); // have to register external build roots eagerly
    }


    public SourceGroup[] getSourceGroups(final String type) {
        return (SourceGroup[]) ProjectManager.mutex().readAccess(new Mutex.Action() {
            public Object run() {
                if (delegate == null) {
                    delegate = initSources();
                }
                return delegate.getSourceGroups(type);
            }
        });
    }

    private Sources initSources() {
        final SourcesHelper h = new SourcesHelper(helper, evaluator);
        String[] propNames = sourceRoots.getRootProperties();
        String[] rootNames = sourceRoots.getRootNames();
        for (int i = 0; i < propNames.length; i++) {
            String displayName = rootNames[i];
            if (displayName.length() ==0) {
                displayName = NbBundle.getMessage(J2SEProject.class, "NAME_src.dir");
            }
            h.addPrincipalSourceRoot("${" + propNames[i] + "}", displayName, /*XXX*/null, null);
            h.addTypedSourceRoot("${" + propNames[i] + "}", JavaProjectConstants.SOURCES_TYPE_JAVA, displayName, /*XXX*/null, null);
        }
        propNames = testRoots.getRootProperties();
        rootNames = testRoots.getRootNames();
        for (int i = 0; i < propNames.length; i++) {
            String displayName = rootNames[i];
            if (displayName.length() ==0) {
                displayName = NbBundle.getMessage(J2SEProject.class, "NAME_test.src.dir");
            }
            h.addPrincipalSourceRoot("${" + propNames[i] + "}", displayName, /*XXX*/null, null);
            h.addTypedSourceRoot("${" + propNames[i] + "}", JavaProjectConstants.SOURCES_TYPE_JAVA, displayName, /*XXX*/null, null);
        }
        // XXX add build dir too?
        ProjectManager.mutex().postWriteRequest(new Runnable() {
            public void run() {
                h.registerExternalRoots(FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
            }
        });
        return h.createSources();
    }

    public void addChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.add(changeListener);
        }
    }

    public void removeChangeListener(ChangeListener changeListener) {
        synchronized (listeners) {
            listeners.remove(changeListener);
        }
    }

    private void fireChange() {
        ChangeListener[] _listeners;
        synchronized (listeners) {
            delegate = null;
            if (listeners.isEmpty()) {
                return;
            }
            _listeners = (ChangeListener[])listeners.toArray(new ChangeListener[listeners.size()]);
        }
        final ChangeListener[] finalListeners = _listeners;
        //XXX: Has to be fired asynchronously, otherwise causes deadlock with Children's mutex
        // XXX That is NOT a valid reason to fire asynch changes. Fix the deadlock on the node side if necessary. -jglick
        RequestProcessor.getDefault().post(
                new Runnable () {
                    public void run () {
                        ChangeEvent ev = new ChangeEvent(this);
                        for (int i = 0; i < finalListeners.length; i++) {
                            finalListeners[i].stateChanged(ev);
                        }
                    }
                }
        );        
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (SourceRoots.PROP_ROOT_PROPERTIES.equals(evt.getPropertyName())) {
            this.fireChange();
        }
    }

}
