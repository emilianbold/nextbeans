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

package org.netbeans.modules.form;

import org.openide.nodes.Node;

import java.awt.*;
import java.beans.*;
import java.lang.reflect.Method;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.netbeans.modules.form.fakepeer.FakePeerSupport;

/**
 * BeanSupport is a utility class with various static methods supporting
 * operations with JavaBeans.
 *
 * @author Ian Formanek
 */
public class BeanSupport
{
    public static final Object NO_VALUE = new Object();
    
    // -----------------------------------------------------------------------------
    // Private variables

    private static HashMap errorEmptyMap = new HashMap(3);
    private static HashMap valuesCache = new HashMap(30);
    private static HashMap instancesCache = new HashMap(30);
    private static HashMap deviationsCache = new HashMap();

    // -----------------------------------------------------------------------------
    // Public methods

    /**
     * Utility method to create an instance of given class. Returns null on
     * error.
     * @param beanClass the class to create inctance of
     * @return new instance of specified class or null if an error occured
     * during instantiation
     */
    public static Object createBeanInstance(Class beanClass) {
        try {
            return CreationFactory.createDefaultInstance(beanClass);
        }
        catch (Exception ex) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                ex.printStackTrace();
            System.err.println("[WARNING] BeanSupport cannot create default instance of: "+beanClass.getName());
            return null;
        }
    }

    /**
     * Utility method to obtain a BeanInfo of given JavaBean class. Returns
     * null on error.
     * @param beanClass the class to obtain BeanInfo for
     * @return BeanInfo instance or null if an error occured or the BeanInfo
     * cannot be found throughout the BeanInfoSearchPath
     */
    public static BeanInfo createBeanInfo(Class beanClass) {
        try {
            return org.openide.util.Utilities.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return null;
        }
    }

    /**
     * Utility method to obtain an instance of specified beanClass. The
     * instance is reused, and thus should only be used to obtain info about
     * settings of default instances of the specified class.
     * @param beanClass the class to create inctance of
     * @return instance of specified class or null if an error occured during
     * instantiation
     */
    public static Object getDefaultInstance(Class beanClass) {
        Object defInstance = instancesCache.get(beanClass);
        if (defInstance == null) {
            defInstance = createBeanInstance(beanClass);
            if (defInstance instanceof Component) {
                FakePeerSupport.attachFakePeer((Component)defInstance);
                if (defInstance instanceof Container)
                    FakePeerSupport.attachFakePeerRecursively(
                                                    (Container)defInstance);
            }

            // hack for JTextField - default background depends on whether
            // the component is editable or not
            if (defInstance instanceof javax.swing.JTextField) {
                Object[] values = new Object[2];
                javax.swing.JTextField jtf = (javax.swing.JTextField) defInstance;
                values[0] = jtf.getBackground();
                jtf.setEditable(false);
                values[1] = jtf.getBackground();
                jtf.setEditable(true);

                Map deviationMap = new HashMap();
                deviationMap.put(
                    "background", // NOI18N
                    new DefaultValueDeviation(values) {
                        Object getValue(Object beanInstance) {
                            return ((javax.swing.JTextField)beanInstance).isEditable() ?
                                   this.values[0] : this.values[1];
                        }
                    }
                );

                deviationsCache.put(beanClass, deviationMap);
            }

            instancesCache.put(beanClass, defInstance);
        }
        return defInstance;
    }

    /**
     * Utility method to obtain a default property values of specified JavaBean
     * class.  The default values are property values immediately after the
     * instance is created.  Because some AWT components initialize their
     * properties only after the peer is created, these are treated specially
     * and default values for those properties are provided
     * explicitely(e.g. though the value of Font property of java.awt.Button is
     * null after an instance of Button is created, this method will return the
     * Font(Dialog, 12, PLAIN) as the default value).
     *
     * @param beanClass The Class of the JavaBean for which the default values
     * are to be obtained
     * @return Map containing pairs <PropertyName(String), value(Object)>
     * @see #getDefaultPropertyValue
     */
    
    public static Map getDefaultPropertyValues(Class beanClass) {
        Map defValues =(Map) valuesCache.get(beanClass);
        if (defValues == null) {
            Object beanInstance = getDefaultInstance(beanClass);
            if (beanInstance == null)
                return errorEmptyMap;
            defValues = getPropertyValues(beanInstance);
            valuesCache.put(beanClass, defValues);
        }
        return defValues;
    }

    /**
     * Utility method to obtain a default value of specified JavaBean class and
     * property name.  The default values are property values immediately after
     * the instance is created.  Because some AWT components initialize their
     * properties only after the peer is created, these are treated specially
     * and default values for those properties are provided
     * explicitely(e.g. though the value of Font property of java.awt.Button is
     * null after an instance of Button is created, this method will return the
     * Font(Dialog, 12, PLAIN) as the default value).
     *
     * @param beanClass The Class of the JavaBean for which the default value
     * is to be obtained
     * @param beanClass The name of the propertyn for which the default value
     * is to be obtained
     * @return The default property value for specified property on specified
     * JavaBean class
     * @see #getDefaultPropertyValues
     */
//    public static Object getDefaultPropertyValue(Class beanClass, String propertyName) {
//        Map values = getDefaultPropertyValues(beanClass);
//        Object val = values.get(propertyName);
//        if (val == null && !values.containsKey(propertyName))
//            val = NO_VALUE;
//        return val;
//    }

    public static Object getDefaultPropertyValue(Object bean,
                                                 String propertyName)
    {
        Map deviationMap = (Map) deviationsCache.get(bean.getClass());
        if (deviationMap != null) {
            DefaultValueDeviation deviation = (DefaultValueDeviation)
                                              deviationMap.get(propertyName);
            if (deviation != null)
                return deviation.getValue(bean);
        }

        Map valuesMap = getDefaultPropertyValues(bean.getClass());
        Object value = valuesMap.get(propertyName);
        return value != null || valuesMap.containsKey(propertyName) ?
               value : NO_VALUE;
    }

    /**
     * Utility method to obtain a current property values of given JavaBean instance.
     * Only the properties specified in bean info(if it exists) are provided.
     *
     * @return Map containing pairs <PropertyName(String), value(Object)>
     */
    public static Map getPropertyValues(Object beanInstance) {
        if (beanInstance == null) {
            return errorEmptyMap;
        }

        BeanInfo info = createBeanInfo(beanInstance.getClass());
        PropertyDescriptor[] properties = info.getPropertyDescriptors();
        HashMap defaultValues = new HashMap(properties.length * 2);

        for (int i = 0; i < properties.length; i++) {
            defaultValues.put(properties[i].getName(), NO_VALUE);
            
            Method readMethod = properties[i].getReadMethod();
            if (readMethod != null) {
                try {
                    Object value = readMethod.invoke(beanInstance, new Object [0]);
//                    if (value == null)
//                        value = getSpecialDefaultAWTValue(beanInstance, properties[i].getName());
                    defaultValues.put(properties[i].getName(), value);
                } catch (Exception e) {
                    // problem with reading property ==>> no default value
//                    if (FormEditor.getFormSettings().getOutputLevel() != FormLoaderSettings.OUTPUT_MINIMUM) {
                        //            notifyPropertyException(beanInstance.getClass(), properties [i].getName(), "component", e, true); // NOI18N
//                    }
                }
            } 
//            else { // the property does not have plain read method
//                if (properties[i] instanceof IndexedPropertyDescriptor) {
//                    //          [PENDING]
//                    //          Method indexedReadMethod =((IndexedPropertyDescriptor)properties[i]).getIndexedReadMethod();
//                }
//            }
        }

        return defaultValues;
    }

    /** Utility method that obtains icon for a bean class.
     * (This method is currently used only for obtaining default icons for AWT
     *  components. Other icons should be provided by BeanInfo.)
     */
    public static Image getBeanIcon(Class beanClass, int iconType) {
        return getIconForDefault(beanClass);
/*        Image ret = getIconForDefault(beanClass);
        if (ret != null) {
            return ret;
        }
        // [FUTURE: the icon should be obtained from the InstanceCookie somehow, and customizable by the user]
        BeanInfo bi = createBeanInfo(beanClass);
        if (bi != null) {
            return bi.getIcon(iconType);
        }
        return null; */
    }

    /** A utility method that returns a class of event adapter for
     * specified listener. It works only on known listeners from java.awt.event.
     * Null is returned for unknown listeners.
     * @return class of an adapter for specified listener or null if
     *               unknown/does not exist
     */
    public static Class getAdapterForListener(Class listener) {
        if (java.awt.event.ComponentListener.class.equals(listener))
            return java.awt.event.ComponentAdapter.class;
        else if (java.awt.event.ContainerListener.class.equals(listener))
            return java.awt.event.ContainerAdapter.class;
        else if (java.awt.event.FocusListener.class.equals(listener))
            return java.awt.event.FocusAdapter.class;
        else if (java.awt.event.KeyListener.class.equals(listener))
            return java.awt.event.KeyAdapter.class;
        else if (java.awt.event.MouseListener.class.equals(listener))
            return java.awt.event.MouseAdapter.class;
        else if (java.awt.event.MouseMotionListener.class.equals(listener))
            return java.awt.event.MouseMotionAdapter.class;
        else if (java.awt.event.WindowListener.class.equals(listener))
            return java.awt.event.WindowAdapter.class;
        else return null; // not found
    }

/*    public static Node.Property [] createEventsProperties(Object beanInstance) {
        BeanInfo beanInfo = createBeanInfo(beanInstance.getClass());
        EventSetDescriptor[] events = beanInfo.getEventSetDescriptors();
        ArrayList eventsProps = new ArrayList();
        for (int i = 0; i < events.length; i++) {
        }

        Node.Property[] np = new Node.Property [eventsProps.size()];
        eventsProps.toArray(np);

        return np;
    }*/

    // -----------------------------------------------------------------------------
    // Private methods

    private static Object getSpecialDefaultAWTValue(Object beanObject, String propertyName) {
        if ((beanObject instanceof Frame) ||
            (beanObject instanceof Dialog)) {
            if ("background".equals(propertyName)) // NOI18N
                return SystemColor.window;
            else if ("foreground".equals(propertyName)) // NOI18N
                return SystemColor.windowText;
            else if ("font".equals(propertyName)) // NOI18N
                return FakePeerSupport.getDefaultAWTFont();
        }

        if ((beanObject instanceof Label) ||
            (beanObject instanceof Button) ||
            (beanObject instanceof TextField) ||
            (beanObject instanceof TextArea) ||
            (beanObject instanceof Checkbox) ||
            (beanObject instanceof Choice) ||
            (beanObject instanceof List) ||
            (beanObject instanceof Scrollbar) ||
            (beanObject instanceof Panel) ||
            (beanObject instanceof ScrollPane)) {
            if ("background".equals(propertyName)) // NOI18N
                return SystemColor.control;
            else if ("foreground".equals(propertyName)) // NOI18N
                return SystemColor.controlText;
            else if ("font".equals(propertyName)) // NOI18N
                return FakePeerSupport.getDefaultAWTFont();
        }

        return null;
    }

    static Reference imageCache;

    private static synchronized Image getIconForDefault(Class klass) {
        Map icons;
        if ((imageCache == null) || ((icons = (Map) imageCache.get()) == null)) {
            icons = createImageCache();
            imageCache = new SoftReference(icons);
        }
        
        String name = klass.getName();
        Object img = icons.get(name);
        
        if (img == null) {
            return null;
        }
        
        if (img instanceof Image) {
            return (Image) img;
        } else {
            Image image = java.awt.Toolkit.getDefaultToolkit().createImage(
                                     FormEditor.class.getResource((String)img));
            icons.put(name, image);
            return image;
        }
    }

    private static Map createImageCache() {
        Map map = new HashMap();
        
        map.put("java.awt.Label", "/org/netbeans/modules/form/beaninfo/awt/label.gif"); // NOI18N
        map.put("java.awt.Button", "/org/netbeans/modules/form/beaninfo/awt/button.gif"); // NOI18N
        map.put("java.awt.TextField", "/org/netbeans/modules/form/beaninfo/awt/textfield.gif"); // NOI18N
        map.put("java.awt.TextArea", "/org/netbeans/modules/form/beaninfo/awt/textarea.gif"); // NOI18N
        map.put("java.awt.Checkbox", "/org/netbeans/modules/form/beaninfo/awt/checkbox.gif"); // NOI18N
        map.put("java.awt.Choice", "/org/netbeans/modules/form/beaninfo/awt/choice.gif"); // NOI18N
        map.put("java.awt.List", "/org/netbeans/modules/form/beaninfo/awt/list.gif"); // NOI18N
        map.put("java.awt.Scrollbar", "/org/netbeans/modules/form/beaninfo/awt/scrollbar.gif"); // NOI18N
        map.put("java.awt.ScrollPane", "/org/netbeans/modules/form/beaninfo/awt/scrollpane.gif"); // NOI18N
        map.put("java.awt.Panel", "/org/netbeans/modules/form/beaninfo/awt/panel.gif"); // NOI18N
        map.put("java.awt.Canvas", "/org/netbeans/modules/form/beaninfo/awt/canvas.gif"); // NOI18N
        map.put("java.awt.MenuBar", "/org/netbeans/modules/form/beaninfo/awt/menubar.gif"); // NOI18N
        map.put("java.awt.PopupMenu", "/org/netbeans/modules/form/beaninfo/awt/popupmenu.gif"); // NOI18N
        map.put("java.awt.Menu", "/org/netbeans/modules/form/resources/menu.gif"); // NOI18N
        map.put("java.awt.MenuItem", "/org/netbeans/modules/form/resources/menuItem.gif"); // NOI18N
        map.put("java.awt.CheckboxMenuItem", "/org/netbeans/modules/form/resources/menuItemCheckbox.gif"); // NOI18N
        map.put("org.netbeans.modules.form.Separator", "/org/netbeans/modules/form/resources/menuSeparator.gif"); // NOI18N

        map.put("java.applet.Applet", "/org/netbeans/modules/form/resources/applet.gif"); // NOI18N
        map.put("java.awt.Dialog", "/org/netbeans/modules/form/resources/dialog.gif"); // NOI18N
        map.put("java.awt.Frame", "/org/netbeans/modules/form/resources/frame.gif"); // NOI18N

        return map;
    }

//    private static void include(Map ret, String[] compos, String[] icons) {
//        for (int i = 0; i < compos.length; i++) {
//            ret.put(compos[i], icons[i]);
//        }
//    }

    private static abstract class DefaultValueDeviation {
        protected Object[] values;
        DefaultValueDeviation(Object[] values) {
            this.values = values;
        }
        abstract Object getValue(Object beanInstance);
    }
}
