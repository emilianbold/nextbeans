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
package org.netbeans.modules.java.navigation.hierarchy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.modules.java.navigation.base.Pair;

/**
 *
 * @author Tomas Zezula
 */
class HierarchyHistory {

    public static final String HISTORY = "history"; //NOI18N
    
    private static final int HISTORY_LENGTH = 10;

    private final PropertyChangeSupport suppot;
    //@GuardedBy("HierarchyHistory.class")
    private static HierarchyHistory instance;
    //@GuardedBy("this")
    private final Deque<Pair<URI, ElementHandle<TypeElement>>> history;

    private HierarchyHistory() {
        suppot = new PropertyChangeSupport(this);
        history = new ArrayDeque<Pair<URI, ElementHandle<TypeElement>>>();
    }

    public void addToHistory(@NonNull final Pair<URI, ElementHandle<TypeElement>> pair) {
        synchronized (this) {
            if (history.size() == HISTORY_LENGTH) {
                history.removeLast();
            }
            boolean contains = false;
            for (Pair<URI,ElementHandle<TypeElement>> p : history) {
                if (p.equals(pair)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                history.addFirst(pair);
            }
        }
        suppot.firePropertyChange(HISTORY, null, null);
    }

    @NonNull
    public synchronized List<? extends Pair<URI, ElementHandle<TypeElement>>> getHistory() {
        final SortedSet<Pair<URI, ElementHandle<TypeElement>>> sorted = new TreeSet<Pair<URI, ElementHandle<TypeElement>>>(new SimpleNameComparator());
        for (Pair<URI,ElementHandle<TypeElement>> p : history) {
            sorted.add(p);
        }
        return Collections.unmodifiableList(new ArrayList<Pair<URI, ElementHandle<TypeElement>>>(sorted));
    }

    public void addPropertyChangeListener(@NonNull final PropertyChangeListener listener) {
        assert listener != null;
        suppot.addPropertyChangeListener(listener);
    }


    public void removePropertyChangeListener(@NonNull final PropertyChangeListener listener) {
        assert listener != null;
        suppot.removePropertyChangeListener(listener);
    }

    synchronized static HierarchyHistory getInstance() {
        if (instance == null) {
            instance = new HierarchyHistory();
        }
        return instance;
    }


    static String getSimpleName(@NonNull final String fqn) {
        int sepIndex = fqn.lastIndexOf('$');   //NOI18N
        if (sepIndex == -1) {
            sepIndex = fqn.lastIndexOf('.');   //NOI18N
        }
        return sepIndex >= 0?
            fqn.substring(sepIndex+1):
            fqn;
    }

    private static class SimpleNameComparator implements Comparator<Pair<URI,ElementHandle<TypeElement>>> {

        @Override
        public int compare(Pair<URI, ElementHandle<TypeElement>> o1, Pair<URI, ElementHandle<TypeElement>> o2) {
            final String simpleName1 = getSimpleName(o1.second.getQualifiedName());
            final String simpleName2 = getSimpleName(o2.second.getQualifiedName());
            return simpleName1.compareTo(simpleName2);
        }
        
    }
}