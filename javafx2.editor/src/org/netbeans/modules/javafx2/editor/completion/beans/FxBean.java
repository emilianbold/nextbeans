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
package org.netbeans.modules.javafx2.editor.completion.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;

/**
 * Provides a definition of a JavaFX bean. JavaFX bean features are used in
 * scene builder and FXML editor. Features are:
 * <ul>
 * <li>properties
 * <li>custom events
 * <li>attached properties
 * </ul>
 * The bean info does not enumerate <i>property change events</i> as by definition
 * there's an event for each defined property.
 * <p/>
 * Use {@link FxBeanProvider} to obtain instances of FxBean. The default Provider
 * implementation can be obtained by {@link #getBeanProvider}.
 * 
 * @author sdedic
 */
public final class FxBean extends FxDefinition {
    /**
     * Contains the 'value-of' method
     */
    private boolean hasValueOf;
    
    /**
     * The Typeelement for the bean
     */
    private ElementHandle<TypeElement>  javaType;
    
    /**
     * Properties available on this class
     */
    private Map<String, FxProperty>    simpleProperties = Collections.emptyMap();
    
    private Map<String, FxProperty>    properties =  Collections.emptyMap();
    
    /**
     * Attached properties
     */
    private Map<String, FxProperty>   attachedProperties =  Collections.emptyMap();
    
    /**
     * Custom events fired from the object
     */
    private Map<String, FxEvent>    events =  Collections.emptyMap();

    /**
     * Names of factory methods
     */
    private Set<String> factoryNames = Collections.emptySet();

    /**
     * Definition of the superclass' bean
     */
    private FxBean superclassInfo;

    /**
     * BeanInfo, which only contains declarations present on the class itself,
     * does not include parents.
     */
    private FxBean declaredInfo;
    
    private String defaultPropertyName;
    
    private boolean fxInstance;
    
    /**
     * Provides the default {@link FxBeanProvider} instance for the given {@link CompilationInfo}.
     * The provider utilizes a cache for FxBeans, so repeated queries will not analyse a Type again,
     * but rather serve the information form the cache.
     * <p/>
     * Use as follows:
     * <code><pre>
     * ParserManager.parse("text/x-java", new UserTask() { public void run(ResultIterator iter) {
     *  CompilationInfo info = CompilationInfo.get(iter.getParserResult());
     *  
     *  FxBeanProvider fxProvider = FxBean.getBeanProvider(info);
     * 
     *  // now we can get FX bean information.
     * });
     * </pre></code>
     * @param info Java Parser compilation info
     * @return default Bean Provider implementation.
     */
    public static FxBeanProvider  getBeanProvider(final CompilationInfo info) {
        return new FxBeanProvider() {
            public FxBean getBeanInfo(String fqn) {
                if (fqn == null) {
                    return null;
                }
                return new BeanModelBuilder(this, info, fqn).getBeanInfo();
            }

            @Override
            public CompilationInfo getCompilationInfo() {
                return info;
            }
        };
    }

    /**
     * Provides default property definition. Returns {@code null}, if the 
     * class does not have a default property.
     * 
     * @return default property definition, or {@code null}
     */
    @CheckForNull
    public FxProperty getDefaultProperty() {
        return defaultPropertyName == null ? null : properties.get(defaultPropertyName);
    }
    
    /**
     * Provides names of all factory methods. Factory method is, per JavaFX guide,
     * a static public method on the class, which returns the class itself.
     * 
     * @return all factory methods
     */
    @NonNull
    public Set<String> getFactoryNames() {
        return Collections.unmodifiableSet(factoryNames);
    }

    void setFactoryNames(Set<String> factoryNames) {
        this.factoryNames = factoryNames;
    }
    
    /**
     * Provides name of the inspected class
     * @return class name
     */
    @NonNull
    public String getClassName() {
        return getName();
    }

    /**
     * Java type for the FxBean. You must use {@link CompilationInfo} to resolve
     * the info to something usable.
     * @return handle to TypeElement that correspond to the class' type
     */
    @NonNull
    public ElementHandle<TypeElement> getJavaType() {
        return javaType;
    }

    /**
     * Provides dictionary of all properties.
     * Map is keyed by property name, values are property definitions.
     * 
     * @return dictionary of properties.
     */
    @NonNull
    public Map<String, FxProperty> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    /**
     * Enumerates all property names. Returns both simple and 'non-simple' properties
     * 
     * @return property names
     */
    @NonNull
    public Collection<String> getPropertyNames() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    /**
     * Provides map of all simple properties. The map is keyed by property name,
     * values are property definitions.
     * 
     * @return dictionary of simple properties
     */
    @NonNull
    public Map<String, FxProperty> getSimpleProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Enumerates all simple property names.
     * 
     * @return names of all simple properties
     */
    @NonNull
    public Collection<String> getSimplePropertyNames() {
        return Collections.unmodifiableSet(simpleProperties.keySet());
    }
    
    /**
     * Enumerates names of all attached properties defined by the class     * 
     * @return 
     */
    @NonNull
    public Collection<String> getAttachedPropertyNames() {
        return Collections.unmodifiableSet(attachedProperties.keySet());
    }
    
    /**
     * Provides property definition for instance property named 'n'. Returns {@code null},
     * if the class does not define such property. For attached properties,
     * use {@link #getAttachedProperty}.
     * 
     * @param n property name
     * @return property definition
     */
    @CheckForNull
    public FxProperty getProperty(String n) {
        return properties.get(n);
    }
    
    /**
     * Returns definition for simple property named 'n'.
     * Simple properties are properties, whose type can be converted from String.
     * Either properties with String or primitive type, or type's class must have
     * the {@code valueOf} method.
     * 
     * @return property definition, or {@code null}, if name does not correspond
     * to any simple property
     */
    public FxProperty getSimpleProperty(String n) {
        return simpleProperties.get(n);
    }
    
    /**
     * Returns definition for attached property named 'n'.
     * @return property definition, or {@code null}, if name does not correspond
     * to class' attached property.
     */
    @CheckForNull
    public FxProperty getAttachedProperty(String n) {
        return attachedProperties.get(n);
    }

    /**
     * Returns attached properties supported by this class.
     * 
     * @return attached properties
     */
    @NonNull
    public Map<String, FxProperty> getAttachedProperties() {
        return Collections.unmodifiableMap(attachedProperties);
    }
    
    public Set<String> getEventNames() {
        return Collections.unmodifiableSet(events.keySet());
    }

    public Map<String, FxEvent> getEvents() {
        return Collections.unmodifiableMap(events);
    }
    
    public FxEvent getEvent(String eventName) {
        FxEvent ev = events.get(eventName);
        return ev;
    }

    FxBean(String className) {
        super(className);
    }
    
    void setFxInstance(boolean bean) {
        this.fxInstance = bean;
    }

    public boolean isFxInstance() {
        return fxInstance;
    }
    
    void setValueOf(boolean has) {
        this.hasValueOf = has;
    }
    
    public boolean hasValueOf() {
        return hasValueOf;
    }
    
    void setDefaultPropertyName(String propName) {
        this.defaultPropertyName = propName;
    }

    void setJavaType(ElementHandle<TypeElement> javaType) {
        this.javaType = javaType;
    }

    void setProperties(Map<String, FxProperty> properties) {
        this.properties = properties;
    }

    void setSimpleProperties(Map<String, FxProperty> properties) {
        this.simpleProperties = properties;
    }

    void setAttachedProperties(Map<String, FxProperty> attachedProperties) {
        this.attachedProperties = attachedProperties;
    }

    void setEvents(Map<String, FxEvent> events) {
        this.events = events;
    }
    
    void setParentBeanInfo(FxBean parent) {
        this.superclassInfo = parent;
    }
    
    /**
     * Provides FxBean instance for the superclass.
     * Returns {@code null} for no superclass or j.l.Object, which does
     * not have any interesting features anyway.
     * 
     * @return 
     */
    @CheckForNull
    public FxBean getSuperclassInfo() {
        return superclassInfo;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BeanInfo[");
        sb.append("\n  className: ").append(getClassName()).
                append("; default: ").append(getDefaultProperty()).
                append("; value: ").append(hasValueOf).
                append("\n factories: ").append(getFactoryNames());
        sb.append("\n properties: ").append("\n");
        appendMap(sb, getProperties());
        sb.append("\n events: ").append("\n");
        appendMap(sb, getEvents());
        
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    private void appendMap(StringBuilder sb, Map m) {
        ArrayList al = new ArrayList(m.keySet());
        Collections.sort(al);
        
        for (Object o : al) {
            Object v = m.get(o);
            sb.append("    ").append(v).append("\n");
        }
    }
    
    /**
     * Provides a definition for this class only, excluding
     * all inherited items. The returned FxBean only enumerates
     * features available directly on the inspected class. This is
     * useful to determine whether a feature was inherited, or defined
     * anew.
     * 
     * @return FxBean instance, which does not collect inherited features
     */
    @NonNull
    public FxBean getDeclareadInfo() {
        return declaredInfo;
    }
    
    void setDeclaredInfo(FxBean declaredInfo) {
        this.declaredInfo = declaredInfo;
    }
    
    /**
     * Merges superclass' beaninfo into this instance.
     * @param superBi 
     */
    void merge(FxBean superBi) {
        if (superBi == null) {
            return;
        }
        
        if (attachedProperties.isEmpty() && !superBi.getAttachedProperties().isEmpty()) {
            attachedProperties = new HashMap<String, FxProperty>(superBi.getAttachedProperties());
        } else {
            attachedProperties.putAll(superBi.getAttachedProperties());
        }
        if (properties.isEmpty() && !superBi.getProperties().isEmpty()) {
            properties = new HashMap<String, FxProperty>(superBi.getProperties());
        } else {
            properties.putAll(superBi.getProperties());
        }
        if (simpleProperties.isEmpty() && !superBi.getSimpleProperties().isEmpty()) {
            simpleProperties = new HashMap<String, FxProperty>(superBi.getSimpleProperties());
        } else {
            simpleProperties.putAll(superBi.getSimpleProperties());
        }
        if (events.isEmpty() && !superBi.getEvents().isEmpty()) {
            events = new HashMap<String, FxEvent>(superBi.getEvents());
        } else {
            events.putAll(superBi.getEvents());
        }
        
        defaultPropertyName = superBi.getDefaultProperty() == null ? null : superBi.getDefaultProperty().getName();
    }
    
    /**
     * Reports BEAN as the FxDefinition kind.
     * 
     * @return BEAN
     */
    public FxDefinitionKind getKind() {
        return FxDefinitionKind.BEAN;
    }
    
}