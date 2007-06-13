/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt. 
  * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 */
package org.netbeans.modules.mobility.svgcore.composer;

import com.sun.perseus.awt.SVGAnimatorImpl;
import com.sun.perseus.builder.ModelBuilder;
import com.sun.perseus.model.DocumentNode;
import com.sun.perseus.model.ElementNode;
import com.sun.perseus.model.ModelNode;
import com.sun.perseus.model.SVG;
import com.sun.perseus.model.SVGImageImpl;
import com.sun.perseus.model.Time;
import com.sun.perseus.model.UpdateAdapter;
import com.sun.perseus.util.SVGConstants;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import javax.microedition.m2g.ExternalResourceHandler;
import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.structure.api.DocumentElement;
import org.netbeans.modules.editor.structure.api.DocumentModel;
import org.netbeans.modules.editor.structure.api.DocumentModelException;
import org.netbeans.modules.mobility.svgcore.SVGDataLoader;
import org.netbeans.modules.mobility.svgcore.composer.prototypes.PatchedElement;
import org.netbeans.modules.mobility.svgcore.composer.prototypes.PatchedGroup;
import org.netbeans.modules.mobility.svgcore.composer.prototypes.SVGComposerPrototypeFactory;
import org.openide.util.Exceptions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 *
 * @author Pavel Benes
 */
public class PerseusController {
    public static final int ANIMATION_NOT_AVAILABLE = 0;
    public static final int ANIMATION_NOT_RUNNING   = 1;
    public static final int ANIMATION_RUNNING       = 2;
    public static final int ANIMATION_PAUSED        = 3;

    public static final String ATTR_ID              = "id";
    
    public static final float DEFAULT_MAX           = 30.0f;
    public static final float DEFAULT_STEP          = 0.1f;
        
    protected final SceneManager        m_sceneMgr;
    protected       SVGAnimatorImpl     m_animator;
    protected       SVGImage            m_svgImage;
    protected       DocumentNode        m_svgDoc;
    protected       SVGLocatableElement m_viewBoxMarker;
    protected       int                 m_animationState = ANIMATION_NOT_AVAILABLE;
    protected       float               m_currentTime  = 0.0f;
    

    PerseusController(SceneManager sceneMgr) {
        m_sceneMgr = sceneMgr;
    }

    void initialize() {
        m_svgImage   = m_sceneMgr.getSVGImage();
        m_svgDoc     = (DocumentNode) m_svgImage.getDocument();
        setupPath((ModelNode) getSVGRootElement(), new int[0], 0);
        //System.out.println("Before rendering: " + m_svgImage);
        //PerseusController.printTree( (DocumentNode) m_svgImage.getDocument(), 0);
        
        m_animator = (SVGAnimatorImpl) SVGAnimator.createAnimator( m_svgImage, "javax.swing.JComponent"); //NOI18N        
        m_animationState = m_sceneMgr.getDataObject().getModel().containsAnimations() ? 
            ANIMATION_NOT_RUNNING : ANIMATION_NOT_AVAILABLE;
        
        SVGSVGElement svg  = getSVGRootElement();
        SVGRect       rect = svg.getRectTrait(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
        
        if (rect != null) {
            m_viewBoxMarker = (SVGLocatableElement) m_svgDoc.createElementNS(SVGConstants.SVG_NAMESPACE_URI,
                    SVGConstants.SVG_RECT_TAG);
            m_viewBoxMarker.setTrait(SVGConstants.SVG_FILL_ATTRIBUTE, "none"); //NOI18N
            m_viewBoxMarker.setTrait(SVGConstants.SVG_STROKE_ATTRIBUTE, "none"); //NOI18N
            m_viewBoxMarker.setFloatTrait(SVGConstants.SVG_X_ATTRIBUTE, rect.getX());
            m_viewBoxMarker.setFloatTrait(SVGConstants.SVG_Y_ATTRIBUTE, rect.getY());
            m_viewBoxMarker.setFloatTrait(SVGConstants.SVG_WIDTH_ATTRIBUTE, rect.getWidth());
            m_viewBoxMarker.setFloatTrait(SVGConstants.SVG_HEIGHT_ATTRIBUTE, rect.getHeight());

            svg.appendChild(m_viewBoxMarker);
        } else {
            m_viewBoxMarker = null;
        }
        
        // we need to get the animator into the 'paused' state so that
        // all changes are immediately visible
        m_animator.play();
        m_animator.pause();
    }
    
    public SVGLocatableElement getViewBoxMarker() {
        return m_viewBoxMarker;
    }
    
    public JComponent getAnimatorGUI() {
        return (JComponent) m_animator.getTargetComponent();
    }

    public void execute(Runnable command) {
        try {
            m_animator.invokeAndWait(command);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //TODO use only one of the methods bellow
    public DocumentNode getSVGDocument() {
        return m_svgDoc;        
    }
    
    public SVGSVGElement getSVGRootElement() {
        return (SVGSVGElement) m_svgDoc.getDocumentElement();        
    }
    
    public int [] getPath(ModelNode node) {
        int [] path = null;
        if (node instanceof PatchedElement) {
            path = ((PatchedElement) node).getPath();
            if (path == null) {
                System.err.println("Path not set for element!");
            }
        } else {
            System.err.println("Only PatchedElements can be found!");
        }
        return path;
    }
    
    public int [] _getPath(ModelNode node) {
        int       depth = 0;
        ModelNode n     = node;
        
        while(n != null) {
            n = n.getParent();
            depth++;
        }
        
        // we are not interested in the topmost DocumentNode
        if (depth > 0) {
            depth--;
        }
        int [] path = new int[depth];
        
        while( depth > 0) {
            int index = 0;
            ModelNode prevSibling = node.getPreviousSiblingNode();
            while(prevSibling != null) {
                prevSibling = prevSibling.getPreviousSiblingNode();
                index++;
            }
            path[--depth] = index;
            node = node.getParent();
        }
        
        return path;
    }
        
    protected void setupPath(ModelNode node, int [] parentPath, int nodeIndex) {
        int [] path = new int[parentPath.length + 1];
        for (int i = 0; i < parentPath.length - 1; i++) {
            path[i] = parentPath[i];
        }    
        path[path.length-1] = nodeIndex;
        
        int [] testPath = _getPath(node);
        if ( !isSamePath(path, testPath)) {
            System.err.println("Incorrect path: " + toString(path) + "<>" + toString(testPath));
        } else {
            System.out.println("Correct path: " + toString(path));
        }   
        
        if (node instanceof PatchedElement) {
            ((PatchedElement) node).setPath(testPath);
        }
        
        ModelNode child = (ElementNode) node.getFirstChildNode();
        int       index = 0;
        
        while( child != null) {
            setupPath(child, path, index++);
            child = child.getNextSiblingNode();
        }
    }

    private static boolean isSamePath(int [] p1, int [] p2) {
        if (p1 != null && p2 != null) {
            if (p1.length == p2.length) {
                for (int i = 0; i < p1.length; i++) {
                    if (p1[i] != p2[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
/*    
    protected ModelNode findElement(int [] path) {
        System.out.println("looking for path: " + toString(path));
        return findElement(m_svgDoc, path);
    }

  */  
    private String toString(int [] array) {
        StringBuilder sb = new StringBuilder("[");
        if (array == null) {
            sb.append("null");
        } else {
            for (int i : array) {
                sb.append(i);
                sb.append(',');
            }
            sb.append("]");
        }
        return sb.toString();
    }
    
/*    
    protected ModelNode findElement(ModelNode parent, int [] path) {
        if (parent instanceof PatchedElement) {
            System.out.println("Checking path: " + parent + toString( ((PatchedElement)parent).getPath()));
            if ( isSamePath( ((PatchedElement) parent).getPath(), path)) {
                return parent;
            }
        }
        ModelNode child = (ElementNode) parent.getFirstChildNode();
        ModelNode result;
        
        while( child != null) {
            if ((result=findElement(child, path)) != null) {
                return result;
            }
            child = child.getNextSiblingNode();
        }
        return null;
    }
  */
    
    protected ModelNode findElement(int [] path) {
        ModelNode node = m_svgDoc;
        //System.out.println("Before find: " + node);
        //printTree(node, 0);
        
        for (int i = 0; i < path.length && node != null; i++) {
            node = (ElementNode) node.getFirstChildNode();
            if (node != null) {
                for (int j = path[i]; j > 0 && node != null;) {
                    node = (ElementNode) node.getNextSiblingNode();
                    if (node instanceof PatchedGroup &&
                        ((PatchedGroup) node).isWrapper()) {
                        
                    } else {
                        j--;
                    }
                }
            }
        }
        
        return node;        
    }
        
    public SVGObject [] getObjectsAt(int x, int y) {
        //Rectangle bounds = m_sceneMgr.getScreenManager().getImageBounds();
        //if (bounds.contains(x, y)) {
        SVGLocatableElement elem = findElementAt(x,y);
        if (elem != null) {
            SVGObject obj = getSVGObject(elem);
            if (obj != null) {
                return new SVGObject [] {obj};
            }
        }
        //}
        return null;
    }

    public SVGObject getObject( int [] path) {
        ModelNode elem = findElement(path);
        if (elem != null && elem instanceof SVGLocatableElement) {
            return getSVGObject((SVGLocatableElement)elem);
        }
        return null;
    }

    private synchronized SVGObject getSVGObject(SVGLocatableElement elem) {
        assert elem != null : "Element must not be null";
        if ( elem instanceof PatchedElement) {
            PatchedElement pelem = (PatchedElement) elem;
            SVGObject obj = pelem.getSVGObject();
            if (obj == null) {
                obj = new SVGObject(m_sceneMgr, elem);
                pelem.attachSVGObject(obj);                
            }
            return obj;
        } else {
            System.err.println("PatchedElement must be used instead of " + elem.getClass().getName());
            return null;
        }   
    }

    public SVGLocatableElement findElementAt(int x, int y) {
        float[] pt = {x, y};
        ModelNode target = m_svgDoc.nodeHitAt(pt);
        SVGLocatableElement elt = null;
        if (target != null) {
            while (elt == null && target != null) {
                if (target instanceof SVGLocatableElement &&
                    target instanceof PatchedElement &&
                    ((PatchedElement) target).getPath() != null) {
                    elt = (SVGLocatableElement) target;
                } else {
                    target = target.getParent();
                }
            }
        }
        return elt;
    }
    
    public boolean isAnimationStopped() {
        return m_animationState == ANIMATION_NOT_AVAILABLE ||
               m_animationState == ANIMATION_NOT_RUNNING;
    }
    
    public int getAnimatorState() {
        return m_animationState;
    }
    
    public void startAnimator(){
        if (m_animationState == ANIMATION_NOT_RUNNING ||
            m_animationState == ANIMATION_PAUSED){
            if (m_animator.getState() != SVGAnimatorImpl.STATE_PLAYING) {
                m_animator.play();
            }
            m_animationState = ANIMATION_RUNNING;
            m_sceneMgr.getScreenManager().repaint();
        }
    }
    
    public void pauseAnimator(){
        if (m_animationState == ANIMATION_RUNNING){
            if (m_animator.getState() == SVGAnimatorImpl.STATE_PLAYING) {
                m_animator.pause();
            }
            m_animationState = ANIMATION_PAUSED;
        }
    }

    public void stopAnimator(){
        if (m_animationState == ANIMATION_RUNNING ||
            m_animationState == ANIMATION_PAUSED){
            if (m_animator.getState() != SVGAnimatorImpl.STATE_PAUSED) {
                m_animator.pause();
            }
            setAnimatorTime(0);                    
            m_animationState = ANIMATION_NOT_RUNNING;
            m_sceneMgr.getScreenManager().repaint();            
        }
    }

    public void setAnimatorTime(float time) {
        if (m_animator != null ){
            m_currentTime = time;
            if ( m_animator.getState() != SVGAnimatorImpl.STATE_STOPPED) {
                m_animator.invokeLater( new Runnable() {
                    public void run() {
                        getSVGRootElement().setCurrentTime(m_currentTime);
                    }    
                });
            } else {
                System.err.println("Animator is stopped!");
            }
        }        
    }
    
    public void startAnimation(int [] path) {
        ModelNode elem = findElement(path);
        if (elem instanceof SVGAnimationElement) {
            System.out.println("Starting animation ...");
            ((SVGAnimationElement) elem).beginElementAt(0);
        }        
    }

    public void stopAnimation(int [] path) {
        ModelNode elem = findElement(path);
        if (elem instanceof SVGAnimationElement) {
            System.out.println("Stopping animation ...");
            ((SVGAnimationElement) elem).endElementAt(0);
        }        
    }
    
    public static ModelNode getRootElement(ModelNode node) {
        ModelNode parent;
        
        while( (parent=node.getParent()) != null) {
            node = parent;
        }
        return node;
    }
    
    public void _mergeImage(File file) throws FileNotFoundException {
        FileInputStream     fin = new FileInputStream(file);
        BufferedInputStream in  = new BufferedInputStream(fin);

        try {
            ModelBuilder.loadDocument(in, m_svgDoc,
                    SVGComposerPrototypeFactory.getPrototypes(m_svgDoc));
        } finally {
            try {
                in.close();
            } catch( IOException e) {
                e.printStackTrace();
            }
        }
        
        SVG svgRoot = (SVG)getSVGRootElement();
        System.out.println("Before children transfer");
        printTree(m_svgDoc, 0);
        
        ModelNode sibling = svgRoot;
        while( (sibling=sibling.getNextSiblingNode()) != null) {
            if (sibling instanceof  SVG) {
                transferChildren(svgRoot, (SVG)sibling);
            }
        }
        System.out.println("After children transfer");
        printTree(m_svgDoc, 0);
    }
    
    private static final String [] REPLACE_PATTERNS = {
        "id=\"{0}\"",
        "begin=\"{0}.",
        "end=\"{0}."
    };
    
    public void mergeImage(File file) throws FileNotFoundException, IOException, DocumentModelException, BadLocationException {
        FileInputStream     fin = null;
        DocumentModel       docModel;
        
        try {
            fin = new FileInputStream(file);
            docModel = loadTextDocumentModel(new BufferedInputStream(fin));
        } finally {
            fin.close();
        }
                
//        SVGImage img = (SVGImage) PerseusController.createImage( in);
        
        Set<String> oldIds = new HashSet<String>();
        collectIDs(m_svgDoc, oldIds);
        
        Set<String> newIds = new HashSet<String>();
        collectIDs( docModel.getRootElement(), newIds);
        
        Set<String> conflicts = new HashSet<String>();
        for (String id  : newIds) {
            if (oldIds.contains(id)) {
                conflicts.add(id);
            }
        }

        BaseDocument doc  = (BaseDocument) docModel.getDocument();
        if ( !conflicts.isEmpty()) {
            for (String id : conflicts) {
                String newID = id+System.currentTimeMillis();
                for (String pattern : REPLACE_PATTERNS) {
                    String oldStr = MessageFormat.format(pattern, id);
                    String newStr = MessageFormat.format(pattern, newID);
                    replaceAllOccurences(doc, oldStr, newStr);
                }
            }
        }
        
        String       text = doc.getText(0, doc.getLength());
        StringBufferInputStream in = new StringBufferInputStream(text);
        ModelBuilder.loadDocument(in, m_svgDoc,
                SVGComposerPrototypeFactory.getPrototypes(m_svgDoc));
        
        SVG svgRoot = (SVG)getSVGRootElement();
        System.out.println("Before children transfer");
        printTree(m_svgDoc, 0);
        
        ModelNode sibling = svgRoot;
        while( (sibling=sibling.getNextSiblingNode()) != null) {
            if (sibling instanceof  SVG) {

                PatchedGroup wrapper = (PatchedGroup) m_svgDoc.createElementNS(SVGConstants.SVG_NAMESPACE_URI,
                    SVGConstants.SVG_G_TAG);
                ((SVG)svgRoot).appendChild(wrapper);
                wrapper.setPath( new int[] { 0, 0});
                wrapper.setWrapper(true);
                
                
                
                transferChildren(wrapper, (SVG)sibling);
            }
        }
        System.out.println("After children transfer");
        printTree(m_svgDoc, 0);        
    }
    
    
    protected DocumentModel loadTextDocumentModel(InputStream in) throws IOException, DocumentModelException {
        EditorKit    kit    = JEditorPane.createEditorKitForContentType(SVGDataLoader.REQUIRED_MIME);
        BaseDocument doc    = (BaseDocument) kit.createDefaultDocument();
        try {
            kit.read( in, doc, 0);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        } finally {
            in.close();
        }
        DocumentModel model = DocumentModel.getDocumentModel(doc);
        return model;
    }    
    
    public static SVGImage createImage(InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException();
        }

        SVGImage img = loadDocument(stream, null);

        // Now, get image width/height from <svg> element and set it in
        // DocumentNode
        DocumentNode docNode = (DocumentNode) img.getDocument();
        Element      root    = docNode.getDocumentElement();
        if (!(root instanceof SVG)) {
            //TODO better reporting
            throw new IOException("Problem");
        }  

        SVG svg    = (SVG) root;
        int width  = (int) svg.getWidth();
        int height = (int) svg.getHeight();
        docNode.setSize(width, height);
        
        return img;        
    }

    protected static void collectIDs(DocumentElement de, Set<String> ids) {
        AttributeSet attrs = de.getAttributes();
        String       id    = (String) attrs.getAttribute(ATTR_ID);
        if (id != null) {
            if ( !ids.add(id)) {
                System.err.println("Duplicated id: " + id);
            }
        }
        
        for (int i = de.getElementCount() - 1; i>= 0; i--) {
            collectIDs( de.getElement(i), ids);
        }
    }
    
    protected static void collectIDs(ModelNode node, Set<String> ids) {
        if (node instanceof SVGElement) {
            String id = ((SVGElement) node).getId();
            if (id != null) {
                if ( !ids.add(id)) {
                    System.err.println("Duplicated id: " + id);
                }
            }
        }
        ModelNode child = node.getFirstChildNode();
        while(child != null) {
            collectIDs(child, ids);
            child = child.getNextSiblingNode();
        }               
    }

    protected void replaceAllOccurences(BaseDocument doc, String oldStr, String newStr) throws BadLocationException {
        String text      = doc.getText(0, doc.getLength());
        int    newLength = newStr.length();
        int    oldLength = oldStr.length();
        int    pos  = 0;
        int    diff = 0;
        
        while( (pos=text.indexOf(oldStr, pos)) != -1) {
            doc.replace(pos+diff, oldLength, newStr, null);
            pos += newLength;
            diff += newLength - oldLength;
        }        
    }
    
    protected static SVGImage loadDocument( final InputStream is, final ExternalResourceHandler handler) 
        throws IOException {

        //TODO Find a better way how to uses Perseus prototypes
        SVGImageImpl sii = (SVGImageImpl) SVGImageImpl.createEmptyImage(handler);
        DocumentNode documentNode = (DocumentNode) sii.getDocument();
        //System.out.println("Empty document:");
        //printTree(documentNode, 0);
                
        UpdateAdapter updateAdapter = new UpdateAdapter();
        documentNode.setUpdateListener(updateAdapter);
        
        ModelBuilder.loadDocument(is, documentNode,
                SVGComposerPrototypeFactory.getPrototypes(documentNode));

        //System.out.println("After document loaded: " + sii);
        //printTree(documentNode, 0);
        
        ModelNode root1 = documentNode.getFirstChildNode();
        
        if (root1 != null && root1 instanceof SVG) {
            SVG svg1 = (SVG) root1;
            
            ModelNode root2 = root1.getNextSiblingNode();
            if (root2 != null && root2 instanceof SVG) {
                SVG svg2 = (SVG) root2;
                transferChildren(svg1, svg2);
                svg1.setViewBox(svg2.getViewBox());
                svg1.setWidth(svg2.getWidth());
                svg1.setHeight(svg2.getHeight());
            }
        }
                
        //System.out.println("After document rearranged: " + sii);
        //printTree(documentNode, 0);

        if (updateAdapter.hasLoadingFailed()) {
            if (updateAdapter.getLoadingFailedException() != null) {
                throw new IOException
                    (updateAdapter.getLoadingFailedException().getMessage());
            }
            throw new IOException();
        }

        // Now, initialize the timing engine and sample at zero.
        documentNode.initializeTimingEngine();
        documentNode.sample(new Time(0));
        return sii;        
    }

    protected static void transferChildren(SVGElement target, SVGElement source) {
        ModelNode child = ((ModelNode) source).getFirstChildNode();
        while(child != null) {
            if (child instanceof PatchedElement) {
                PatchedElement pe = (PatchedElement) child;
                child = child.getNextSiblingNode();
                SVGObject.setNullIds( (SVGElement) pe, true);
                source.removeChild((Node)pe);
                target.appendChild((Node)pe);
                SVGObject.setNullIds( (SVGElement) pe, false);
            } else {
                System.err.println("The PatchedElement must be used instead of " + child.getClass().getName());
            }
        }
    }
    
    public static void printTree( ModelNode node, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }
        System.out.println( node.getClass());
        ModelNode child = node.getFirstChildNode();
        while(child != null) {
            printTree(child, level+1);
            child = child.getNextSiblingNode();
        }               
    }
}
