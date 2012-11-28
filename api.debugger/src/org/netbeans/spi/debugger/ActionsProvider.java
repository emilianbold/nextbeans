/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.spi.debugger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netbeans.debugger.registry.ContextAwareServiceHandler;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * Represents implementation of one or more actions.
 *
 * @author   Jan Jancura
 */
public abstract class ActionsProvider {

    private static final RequestProcessor debuggerActionsRP = new RequestProcessor("Debugger Actions", 5);

    /**
     * Returns set of actions supported by this ActionsProvider.
     *
     * @return set of actions supported by this ActionsProvider
     */
    public abstract Set getActions ();

    /**
     * Called when the action is called (action button is pressed).
     *
     * @param action an action which has been called
     */
    public abstract void doAction (Object action);
    
    /**
     * Should return a state of given action.
     *
     * @param action action
     */
    public abstract boolean isEnabled (Object action);
    
    /**
     * Adds property change listener.
     *
     * @param l new listener.
     */
    public abstract void addActionsProviderListener (ActionsProviderListener l);
    

    /**
     * Removes property change listener.
     *
     * @param l removed listener.
     */
    public abstract void removeActionsProviderListener (ActionsProviderListener l);
    
    /**
     * Post the action and let it process asynchronously.
     * The default implementation just delegates to {@link #doAction}
     * in a separate thread and returns immediately.
     *
     * @param action The action to post
     * @param actionPerformedNotifier run this notifier after the action is
     *        done.
     * @since 1.5
     */
    public void postAction (final Object action,
                            final Runnable actionPerformedNotifier) {
        debuggerActionsRP.post(new Runnable() {
            public void run() {
                try {
                    doAction(action);
                } finally {
                    actionPerformedNotifier.run();
                }
            }
        });
    }

    /**
     * Declarative registration of an ActionsProvider implementation.
     * By marking the implementation class with this annotation,
     * you automatically register that implementation for use by debugger.
     * The class must be public and have a public constructor which takes
     * no arguments or takes {@link ContextProvider} as an argument.
     * @since 1.16
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE})
    public @interface Registration {
        /**
         * An optional path to register this implementation in.
         * Usually the session ID.
         */
        String path() default "";

        /**
         * Provide the list of actions that this provider supports.
         * This list is used before an instance of the registered class is created,
         * it's necessary when {@link #activateForMIMETypes()} is overriden
         * to prevent from the class instantiation.
         * @return The list of actions.
         * @since 1.23
         */
        String[] actions() default {};

        /**
         * Provide a list of MIME types that are compared to the MIME type of
         * a file currently active in the IDE and when matched, this provider
         * is activated (an instance of the registered class is created).
         * By default, the provider instance is created immediately.
         * This method can be used to delay the instantiation of the
         * implementation class for performance reasons.
         * @return The list of MIME types
         * @since 1.23
         */
        String[] activateForMIMETypes() default {};

    }

    /**
     * Allows registration of multiple {@link Registration} annotations.
     * @since 1.28
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE})
    public @interface Registrations {
        Registration[] value();
    }

    static class ContextAware extends ActionsProvider implements ContextAwareService<ActionsProvider> {

        private static final String ERROR = "error in getting MIMEType";    // NOI18N

        private String path;        // The file path, that declares this service, or null if not known.
        private String serviceName;
        private ContextProvider context;
        private ActionsProvider delegate;

        private Set actions;
        private Set<String> enabledOnMIMETypes;
        private List<ActionsProviderListener> listeners = new ArrayList<ActionsProviderListener>();
        private PropertyChangeListener contextDispatcherListener;

        private ContextAware(String serviceName, Set actions, Set<String> enabledOnMIMETypes) {
            this.serviceName = serviceName;
            this.actions = actions;
            this.enabledOnMIMETypes = enabledOnMIMETypes;
        }

        private ContextAware(String serviceName, Set actions, Set<String> enabledOnMIMETypes,
                             ContextProvider context) {
            this.serviceName = serviceName;
            this.actions = actions;
            this.enabledOnMIMETypes = enabledOnMIMETypes;
            this.context = context;
        }

        private synchronized ActionsProvider getDelegate() {
            if (delegate == null) {
                delegate = (ActionsProvider) ContextAwareSupport.createInstance(serviceName, context);
                if (delegate == null) {
                    throw new IllegalStateException("No instance created for service "+serviceName+", context = "+context+", path = "+path);
                }
                for (ActionsProviderListener l : listeners) {
                    delegate.addActionsProviderListener(l);
                }
                listeners.clear();
                if (contextDispatcherListener != null) {
                    detachContextDispatcherListener();
                }
            }
            return delegate;
        }

        @Override
        public Set getActions() {
            ActionsProvider actionsDelegate;
            if (actions != null) {
                synchronized (this) {
                    actionsDelegate = this.delegate;
                }
            } else {
                actionsDelegate = getDelegate();
            }
            if (actionsDelegate == null) {
                return actions;
            }
            return actionsDelegate.getActions();
        }

        @Override
        public void doAction(Object action) {
            getDelegate().doAction(action);
        }

        @Override
        public void postAction(Object action, Runnable actionPerformedNotifier) {
            getDelegate().postAction(action, actionPerformedNotifier);
        }

        @Override
        public boolean isEnabled(Object action) {
            ActionsProvider actionsDelegate;
            if (enabledOnMIMETypes != null) {
                synchronized (this) {
                    actionsDelegate = this.delegate;
                }
            } else {
                actionsDelegate = getDelegate();
            }
            if (actionsDelegate == null) {
                String currentMIMEType = getCurrentMIMEType();
                if (currentMIMEType != ERROR) {
                    if (!enabledOnMIMETypes.contains(currentMIMEType)) {
                        //System.err.println("Delegate '"+serviceName+"' NOT enabled on "+currentMIMEType+", enabled MIME types = "+enabledOnMIMETypes);
                        return false;
                    }
                }
            }
            return getDelegate().isEnabled(action);
        }

        @Override
        public void addActionsProviderListener(ActionsProviderListener l) {
            ActionsProvider actionsDelegate;
            synchronized (this) {
                actionsDelegate = delegate;
                if (actionsDelegate == null) {
                    listeners.add(l);
                    if (contextDispatcherListener == null && enabledOnMIMETypes != null) {
                        contextDispatcherListener = attachContextDispatcherListener();
                    }
                    return ;
                }
            }
            actionsDelegate.addActionsProviderListener(l);
        }

        @Override
        public void removeActionsProviderListener(ActionsProviderListener l) {
            ActionsProvider actionsDelegate;
            synchronized (this) {
                actionsDelegate = delegate;
                if (actionsDelegate == null) {
                    listeners.remove(l);
                    if (listeners.size() == 0 && contextDispatcherListener != null) {
                        detachContextDispatcherListener();
                    }
                    return ;
                }
            }
            actionsDelegate.removeActionsProviderListener(l);
        }

        public ActionsProvider forContext(ContextProvider context) {
            if (context == this.context) {
                return this;
            } else {
                ContextAware ca = new ActionsProvider.ContextAware(serviceName, actions, enabledOnMIMETypes, context);
                ca.path = path;
                return ca;
            }
        }

        /**
         * Creates instance of <code>ContextAwareService</code> based on layer.xml
         * attribute values
         *
         * @param attrs attributes loaded from layer.xml
         * @return new <code>ContextAwareService</code> instance
         */
        static ContextAwareService createService(Map attrs) throws ClassNotFoundException {
            String serviceName = (String) attrs.get(ContextAwareServiceHandler.SERVICE_NAME);
            String actionsStr = (String) attrs.get(ContextAwareServiceHandler.SERVICE_ACTIONS);
            String enabledOnMIMETypesStr = (String) attrs.get(ContextAwareServiceHandler.SERVICE_ENABLED_MIMETYPES);
            String[] actions = parseArray(actionsStr);
            String[] enabledOnMIMETypes = parseArray(enabledOnMIMETypesStr);
            String path = null;
            try {
                Field foField = attrs.getClass().getDeclaredField("fo");
                foField.setAccessible(true);
                FileObject fo = (FileObject) foField.get(attrs);
                path = fo.getPath();
            } catch (Exception ex) {}
            ContextAware ca = new ActionsProvider.ContextAware(serviceName,
                                                               createSet(actions),
                                                               createSet(enabledOnMIMETypes));
            ca.path = path;
            return ca;
        }

        private static String[] parseArray(String strArray) {
            if (strArray == null) {
                return null;
            }
            if (strArray.startsWith("[")) strArray = strArray.substring(1);
            if (strArray.endsWith("]")) strArray = strArray.substring(0, strArray.length() - 1);
            strArray = strArray.trim();
            int index = 0;
            List<String> strings = new ArrayList<String>();
            while (index < strArray.length()) {
                int index2 = strArray.indexOf(',', index);
                if (index2 < 0) index2 = strArray.length();
                if (index2 > index) {
                    String s = strArray.substring(index, index2).trim();
                    if (s.length() > 0) { // Can be trimmed to 0 length
                        strings.add(s);
                    }
                    index = index2 + 1;
                } else {
                    index++;
                    continue;
                }
            }
            return strings.toArray(new String[0]);
        }

        private static <T> Set<T> createSet(T[] array) {
            if (array != null) {
                return Collections.unmodifiableSet(new HashSet(Arrays.asList(array)));
            } else {
                return null;
            }
        }

        private static String getCurrentMIMEType() {
            // Ask EditorContextDispatcher.getDefault().getMostRecentFile()
            // It's not in a dependent module, therefore we have to find it dynamically:
            try {
                Class editorContextDispatcherClass = Lookup.getDefault().lookup(ClassLoader.class).loadClass("org.netbeans.spi.debugger.ui.EditorContextDispatcher");
                try {
                    try {
                        Object editorContextDispatcher = editorContextDispatcherClass.getMethod("getDefault").invoke(null);
                        FileObject file = (FileObject) editorContextDispatcherClass.getMethod("getMostRecentFile").invoke(editorContextDispatcher);
                        if (file != null) {
                            return file.getMIMEType();
                        } else {
                            return null;
                        }
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } catch (NoSuchMethodException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } catch (ClassNotFoundException ex) {
            }
            return ERROR;
        }

        private PropertyChangeListener attachContextDispatcherListener() {
            // Call EditorContextDispatcher.getDefault().addPropertyChangeListener(String MIMEType, PropertyChangeListener l)
            // It's not in a dependent module, therefore we have to find it dynamically:
            PropertyChangeListener l = null;
            try {
                Class editorContextDispatcherClass = Lookup.getDefault().lookup(ClassLoader.class).loadClass("org.netbeans.spi.debugger.ui.EditorContextDispatcher");
                try {
                    try {
                        Object editorContextDispatcher = editorContextDispatcherClass.getMethod("getDefault").invoke(null);
                        java.lang.reflect.Method m = editorContextDispatcherClass.getMethod(
                                "addPropertyChangeListener",
                                String.class,
                                PropertyChangeListener.class);
                        l = new ContextDispatcherListener();
                        for (String mimeType : enabledOnMIMETypes) {
                            m.invoke(editorContextDispatcher, mimeType, l);
                        }
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } catch (NoSuchMethodException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } catch (ClassNotFoundException ex) {
            }
            return l;
        }

        private void detachContextDispatcherListener() {
            // Call EditorContextDispatcher.getDefault().removePropertyChangeListener(PropertyChangeListener l)
            // It's not in a dependent module, therefore we have to find it dynamically:
            PropertyChangeListener l = null;
            try {
                Class editorContextDispatcherClass = Lookup.getDefault().lookup(ClassLoader.class).loadClass("org.netbeans.spi.debugger.ui.EditorContextDispatcher");
                try {
                    try {
                        Object editorContextDispatcher = editorContextDispatcherClass.getMethod("getDefault").invoke(null);
                        java.lang.reflect.Method m = editorContextDispatcherClass.getMethod(
                                "removePropertyChangeListener",
                                PropertyChangeListener.class);
                        m.invoke(editorContextDispatcher, contextDispatcherListener);
                        contextDispatcherListener = null;
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } catch (NoSuchMethodException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (SecurityException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } catch (ClassNotFoundException ex) {
            }
        }

        private class ContextDispatcherListener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<ActionsProviderListener> ls;
                synchronized (ContextAware.this) {
                    ls = new ArrayList<ActionsProviderListener>(listeners);
                }
                for (ActionsProviderListener l : ls) {
                    for (Object action : actions) {
                        l.actionStateChange(action, isEnabled(action));
                    }
                }
            }
            
        }

        @Override
        public synchronized String toString() {
            return "ActionsProvider.ContextAware for service "+serviceName+", context = "+context+", path = "+path+", delegate = "+delegate;
        }
        
    }
    
}

