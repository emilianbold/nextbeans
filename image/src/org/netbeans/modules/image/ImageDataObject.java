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


package org.netbeans.modules.image;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import org.openide.actions.OpenAction;
import org.openide.cookies.CompilerCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.HelpCtx;


/** 
 * Object that represents one file containing an image.
 * @author Petr Hamernik, Jaroslav Tulach, Ian Formanek
 */
public class ImageDataObject extends MultiDataObject {
    
    /** Generated serialized version UID. */
    static final long serialVersionUID = -6035788991669336965L;

    /** Base for image resource. */
    private static final String IMAGE_ICON_BASE = "org/netbeans/modules/image/imageObject"; // NOI18N

    /** Helper variable. Speeds up <code>DataObject</code> recognition. */
    private transient boolean cookiesInitialized = false;
    
    
    /** Constructor.
    * @param pf primary file object for this data object
    * @param loader the data loader creating it
    * @exception DataObjectExistsException if there was already a data object for it 
    */
    public ImageDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
    }

    
    /** Help context for this object.
    * @return the help context
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ImageDataObject.class);
    }

    /** Get a URL for the image.
    * @return the image url
    */
    URL getImageURL() {
        try {
            return getPrimaryFile().getURL();
        } catch (FileStateInvalidException ex) {
            return null;
        }
    }

    /** Get image data for the image.
    * @return the image data
    */
    public byte[] getImageData() {
        try {
            FileObject fo = getPrimaryFile();
            byte[] imageData = new byte[(int)fo.getSize()];
            BufferedInputStream in = new BufferedInputStream(fo.getInputStream());
            in.read(imageData, 0, (int)fo.getSize());
            in.close();
            return imageData; 
        } catch(IOException ioe) {
            return new byte[0];
        }
    }


    /** Create a node to represent the image.
    * @return the node
    */
    protected Node createNodeDelegate () {
        DataNode node = new DataNode (this, Children.LEAF);
        node.setIconBase(IMAGE_ICON_BASE);
        node.setDefaultAction (SystemAction.get (OpenAction.class));
        return node;
    }
    
    /** Overrides superclass method. */
    public CookieSet getCookieSet() {
        synchronized(this) {
            if (!cookiesInitialized) {
                initCookieSet();
            }
        }
        
        return super.getCookieSet();
    }

    /**
     * Overrides superclass method. 
     * Look for a cookie in the current cookie set matching the requested class.
     * @param type the class to look for
     * @return an instance of that class, or <code>null</code> if this class of cookie
     *    is not supported
     */
    public Node.Cookie getCookie(Class type) {
        if (CompilerCookie.class.isAssignableFrom(type)) {
            return null;
        }
        
        synchronized(this) { 
            if(!cookiesInitialized) {
                initCookieSet();
            }
        }
        
        return super.getCookie(type);
    }
    
    /** Initializes cookie set. */
    private synchronized void initCookieSet() {
        // Necessary to set flag before add cookieSet method, cause
        // it fires property event change and some Cookie action in its
        // enable method could call initCookieSet again. 
        
        cookiesInitialized = true;
        super.getCookieSet().add(new ImageOpenSupport(getPrimaryEntry()));
    }
    
}
