/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.form.codestructure;

import java.beans.PropertyEditor;
import java.lang.reflect.*;
import org.openide.nodes.Node;
import org.netbeans.modules.form.*;

/**
 * @author Tomas Pavek
 */

public class FormCodeSupport {

    public static CodeElementOrigin createOrigin(Node.Property property) {
        if (property instanceof FormProperty)
            return new FormPropertyValueOrigin((FormProperty)property);
        else
            return new PropertyValueOrigin(property);
    }

    public static CodeElementOrigin createOrigin(RADComponent component) {
        return new RADComponentOrigin(component);
    }

    public static void readPropertyElement(CodeElement element,
                                           Node.Property property,
                                           boolean allowChangeFiring)
    {
        FormProperty fProperty = null;
        if (!allowChangeFiring && property instanceof FormProperty) {
            FormProperty fProp = (FormProperty) property;
            if (fProp.isChangeFiring()) {
                fProp.setChangeFiring(false);
                fProperty = fProp;
            }
        }

        try {
            property.setValue(element.getOrigin().getValue());
        }
        catch (Exception ex) { // ignore
            if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                ex.printStackTrace();
        }
        element.setOrigin(createOrigin(property));

        if (fProperty != null)
            fProperty.setChangeFiring(true);
    }

    public static void readPropertyConnection(CodeConnection connection,
                                              Node.Property property,
                                              boolean allowChangeFiring)
    {
        // expecting connection with one element parameter
        CodeElement[] params = connection.getConnectionParameters();
        if (params.length != 1)
            throw new IllegalArgumentException();

        readPropertyElement(params[0], property, allowChangeFiring);
    }

    // --------

    static final class PropertyValueOrigin implements CodeElementOrigin {
        private Node.Property property;

        public PropertyValueOrigin(Node.Property property) {
            this.property = property;
        }

        public Class getType() {
            return property.getValueType();
        }

        public CodeElement getParentElement() {
            return null;
        }

        public Object getValue() {
            try {
                return property.getValue();
            }
            catch (Exception ex) {} // should no happen

            return null;
        }

        public Object getCreatingObject() {
            return property;
        }

        public String getJavaCodeString(String parentStr, String[] paramsStr) {
            try {
                PropertyEditor pred = property.getPropertyEditor();
                pred.setValue(property.getValue());
                return pred.getJavaInitializationString();
            }
            catch (Exception ex) {} // should not happen
            return null;
        }

        public CodeElement[] getCreationParameters() {
            return CodeStructure.EMPTY_PARAMS;
        }
    }


    static final class FormPropertyValueOrigin implements CodeElementOrigin {
        private FormProperty property;

        public FormPropertyValueOrigin(FormProperty property) {
            this.property = property;
        }

        public Class getType() {
            return property.getValueType();
        }

        public CodeElement getParentElement() {
            return null;
        }

        public Object getValue() {
            try {
                return property.getRealValue();
                // [or getValue() ??]
            }
            catch (Exception ex) {} // should no happen

            return null;
        }

        public Object getCreatingObject() {
            return property;
        }

        public String getJavaCodeString(String parentStr, String[] paramsStr) {
            return property.getJavaInitializationString();
        }

        public CodeElement[] getCreationParameters() {
            return CodeStructure.EMPTY_PARAMS;
        }
    }

    static final class RADComponentOrigin implements CodeElementOrigin {
        private RADComponent component;

        public RADComponentOrigin(RADComponent component) {
            this.component = component;
        }

        public Class getType() {
            return component.getBeanClass();
        }

        public CodeElement getParentElement() {
            return null;
        }

        public Object getCreatingObject() {
            return component;
        }

        public Object getValue() {
            return component.getBeanInstance();
        }

        public CodeElement[] getCreationParameters() {
            return CodeStructure.EMPTY_PARAMS;
        }

        public String getJavaCodeString(String parentStr, String[] paramsStr) {
            if (component == component.getFormModel().getTopRADComponent())
                return "this"; // NOI18N

            StringBuffer buf = new StringBuffer();

            buf.append("new "); // NOI18N
            buf.append(component.getBeanClass().getName().replace('&','.')); // NOI18N
            buf.append("()"); // NOI18N

            return buf.toString();
        }
    }
}
