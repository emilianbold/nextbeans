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
package org.netbeans.modules.search.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import org.netbeans.modules.search.MatchingObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jhavlin
 */
public class MatchingObjectNode extends FilterNode {

    private MatchingObject matchingObject;

    public MatchingObjectNode(Node original,
            org.openide.nodes.Children children,
            MatchingObject matchingObject) {
        super(original, children, Lookups.fixed());

        this.matchingObject = matchingObject;
    }

    @Override
    public PropertySet[] getPropertySets() {

        PropertySet[] sets = new PropertySet[1];
        PropertySet set = new PropertySet("default", "default properties",
                "Default Properties") {

            @Override
            public Property<?>[] getProperties() {
                Property[] properties = new Property[]{
                    new SizeProperty(),
                    new LastModifiedProperty(),
                    new DetailsCountProperty(),
                    new PathProperty()
                };
                return properties;
            }
        };

        sets[0] = set;
        return sets;
    }

    private class SizeProperty extends Property<Long> {

        public SizeProperty() {
            super(Long.class);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public Long getValue() throws IllegalAccessException, InvocationTargetException {
            return matchingObject.getFileObject().getSize();
        }

        @Override
        public boolean canWrite() {
            return false;
        }

        @Override
        public void setValue(Long val) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getName() {
            return "size";
        }
    }

    private class LastModifiedProperty extends Property<Date> {

        public LastModifiedProperty() {
            super(Date.class);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public Date getValue() throws IllegalAccessException,
                InvocationTargetException {
            return matchingObject.getFileObject().lastModified();
        }

        @Override
        public boolean canWrite() {
            return false;
        }

        @Override
        public void setValue(Date val) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            return "lastModified";                                      //NOI18N
        }
    }

    private class DetailsCountProperty extends Property<Integer> {

        public DetailsCountProperty() {
            super(Integer.class);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public Integer getValue() throws IllegalAccessException,
                InvocationTargetException {
            return matchingObject.getDetailsCount();
        }

        @Override
        public boolean canWrite() {
            return false;
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            throw new UnsupportedOperationException();                  //NOI18N
        }

        @Override
        public String getName() {
            return "detailsCount";                                      //NOI18N
        }
    }

     private class PathProperty extends Property<String> {

        public PathProperty() {
            super(String.class);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public String getValue() throws IllegalAccessException,
                InvocationTargetException {
            return matchingObject.getFileObject().getPath();
        }

        @Override
        public boolean canWrite() {
            return false;
        }

        @Override
        public void setValue(String val) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            throw new UnsupportedOperationException();                  //NOI18N
        }

        @Override
        public String getName() {
            return "path";                                              //NOI18N
        }
    }
}
