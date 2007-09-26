/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.openide.loaders;


import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.beans.*;
import java.io.*;
import java.lang.ref.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.*;
import org.openide.nodes.*;
import org.openide.nodes.Node.*;
import org.openide.util.*;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.xml.XMLUtil;

/** Default implementation of a shortcut to another data object.
* Since 1.13 it extends MultiDataObject.
* @author Jan Jancura, Jaroslav Tulach
*/
public class DataShadow extends MultiDataObject implements DataObject.Container {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 6305590675982925167L;

    /** original data object */
    private DataObject original;
    /** Listener attached to original DataObject. */
    private OrigL origL = null;
    /** List of nodes created for the DataShadow. */
    private LinkedList<ShadowNode> nodes = new LinkedList<ShadowNode> ();

    /** Extension name. */
    static final String SHADOW_EXTENSION = "shadow"; // NOI18N
    
    /** Map of all <FileObject, Set<DataShadow>>. Where the file object
     Is the original file */
    private static Map<FileObject, Set<Reference<DataShadow>>> allDataShadows;
    
    private static Mutex MUTEX = new Mutex ();

    /** Getter for the Set that contains all DataShadows. */
    private static synchronized Map<FileObject, Set<Reference<DataShadow>>> getDataShadowsSet() {
        if (allDataShadows == null) {
            allDataShadows = new HashMap<FileObject, Set<Reference<DataShadow>>>();
        }
        return allDataShadows;
    }
    
    private static synchronized void enqueueDataShadow(DataShadow ds) {
        Map<FileObject, Set<Reference<DataShadow>>> m = getDataShadowsSet ();
        
        FileObject prim = ds.original.getPrimaryFile ();
        Reference<DataShadow> ref = new DSWeakReference<DataShadow>(ds);
        Set<Reference<DataShadow>> s = m.get (prim);
        if (s == null) {
            s = Collections.singleton (ref);
            getDataShadowsSet ().put (prim, s);
        } else {
            if (! (s instanceof HashSet)) {
                s = new HashSet<Reference<DataShadow>> (s);
                getDataShadowsSet ().put (prim, s);
            }
            s.add (ref);
        }
    }

    /** @return all active DataShadows or null */
    private static synchronized List<DataShadow> getAllDataShadows() {
        if (allDataShadows == null || allDataShadows.isEmpty()) {
            return null;
        }
        
        List<DataShadow> ret = new ArrayList<DataShadow>(allDataShadows.size());
        Iterator<Set<Reference<DataShadow>>> it = allDataShadows.values ().iterator();
        while (it.hasNext()) {
            Set<Reference<DataShadow>> ref = it.next();
            Iterator<Reference<DataShadow>> refs = ref.iterator ();
            while (refs.hasNext ()) {
                Reference<DataShadow> r = refs.next ();
                DataShadow shadow = r.get ();
                if (shadow != null) {
                    ret.add (shadow);
                }
            }
        }
        
        return ret;
    }
    
    /** Checks whether a change of the given dataObject
     * does not hurt validity of a DataShadow
     */
    static void checkValidity(EventObject ev) {
        DataObject src = null;
        if (ev instanceof OperationEvent) {
            src = ((OperationEvent)ev).getObject();
        }

        Set<Reference<DataShadow>> shadows = null;
        synchronized (DataShadow.class) {
            if (allDataShadows == null || allDataShadows.isEmpty ()) return;
            
            if (src != null) {
                shadows = allDataShadows.get (src.getPrimaryFile ());
                if (shadows == null) {
                    // we know the source of the event and there are no
                    // shadows with such original
                    return;
                }
                // to prevent modifications
                shadows = new HashSet<Reference<DataShadow>> (shadows);
            }
        }
        
        DataObject changed = null;
        OperationEvent.Copy c;
        if (
            ev instanceof OperationEvent.Rename
            ||
            ev instanceof OperationEvent.Move
        ) {
            changed = ((OperationEvent)ev).getObject();
        }
        
        if (shadows != null) {
            //
            // optimized for speed, we have found the shadow(s) that
            // belong to this FileObject
            //
            for (Reference<DataShadow> r: shadows) {
                DataShadow shadow = r.get ();
                if (shadow != null) {
                    shadow.refresh (shadow.original == changed);
                }
            }
            return;
        }
        
        List<DataShadow> all = getAllDataShadows();
        if (all == null) {
            return;
        }
        
        
        int size = all.size();
        for (int i = 0; i < size; i++) {
            DataShadow obj = all.get(i);
            // if original was renamed or moved update 
            // the file with the link
            obj.refresh (obj.original == changed);
        }
    }
    
    /** Constructs new data shadow for given primary file and referenced original.
    * Method to allow subclasses of data shadow.
    *
    * @param fo the primary file
    * @param original original data object
    * @param loader the loader that created the object
    */
    protected DataShadow (
        FileObject fo, DataObject original, MultiFileLoader loader
    ) throws DataObjectExistsException {
        super (fo, loader);
        init(original);
    }

    /** Constructs new data shadow for given primary file and referenced original.
    * Method to allow subclasses of data shadow.
    *
    * @param fo the primary file
    * @param original original data object
    * @param loader the loader that created the object
    * @deprecated Since 1.13 do not use this constructor, it is for backward compatibility only
    */
    @Deprecated
    protected DataShadow (
        FileObject fo, DataObject original, DataLoader loader
    ) throws DataObjectExistsException {
        super (fo, loader);
        init(original);
    }
    
    /** Perform initialization after construction.
    * @param original original data object
    */
    private void init(DataObject original) {
        if (original == null)
            throw new IllegalArgumentException();
        setOriginal (original);
        enqueueDataShadow(this);
    }
    
    /** Constructs new data shadow for given primary file and referenced original.
    * @param fo the primary file
    * @param original original data object
    */
    private DataShadow (FileObject fo, DataObject original) throws DataObjectExistsException {
        this (fo, original, (MultiFileLoader)DataLoaderPool.getShadowLoader ());
    }

    /** Method that creates new data shadow in a folder. The name chosen is based
    * on the name of the original object.
    *
    * @param folder target folder to create data in
    * @param original original object that should be represented by the shadow
    */
    public static DataShadow create (DataFolder folder, DataObject original)
    throws IOException {
        return create (folder, null, original, SHADOW_EXTENSION);
    }

    /** Method that creates new data shadow in a folder. The default extension
    * is used.
    *
    * @param folder target folder to create data in
    * @param name name to give to the shadow
    * @param original object that should be represented by the shadow
    */
    public static DataShadow create (
        DataFolder folder,
        final String name,
        final DataObject original
    ) throws IOException {
        return create (folder, name, original, SHADOW_EXTENSION);
    }
    
    /** Method that creates new data shadow in a folder. All modifications are
    * done atomically using {@link FileSystem#runAtomicAction}.
    *
    * @param folder target folder to create data in
    * @param name name to give to the shadow
    * @param original original object that should be represented by the shadow
    */
    public static DataShadow create (
        DataFolder folder,
        final String name,
        final DataObject original,
        final String ext
    ) throws IOException {
        final FileObject fo = folder.getPrimaryFile ();
        final DataShadow[] arr = new DataShadow[1];

        DataObjectPool.getPOOL().runAtomicAction(fo, new FileSystem.AtomicAction() {
            public void run() throws IOException {
                FileObject file = writeOriginal(name, ext, fo, original);
                final DataObject obj = DataObject.find(file);
                if (obj instanceof DataShadow) {
                    arr[0] = (DataShadow)obj;
                } else {
                    // wrong instance => shadow was not found
                    throw new DataObjectNotFoundException(obj.getPrimaryFile()) {
                        public String getMessage() {
                            return super.getMessage() + ": " + obj.getClass().getName(); // NOI18N
                        }
                    };
                }
            }
        });

        return arr[0];
    }
    
    /** Writes the original DataObject into file of given name and extension.
     * Both parameters {@link name} and {@link ext} are ignored when the data file
     * is passed in as a {@link trg} parameter, in that case name and link can be <code>null</code>.
     * @param name name of the file to write original DataObject in
     * @param ext extension of the file to write original DataObject in
     * @param trg folder where FileObject of given name and ext will be created or
     * file which content is replaced
     * @param obj DataObject which link is stored into
     * @return the file with link
     * @exception IOException on I/O error
     */
    private static FileObject writeOriginal (
        final String name, final String ext, final FileObject trg, final DataObject obj
    ) throws IOException {
        try {
            return MUTEX.writeAccess (new Mutex.ExceptionAction<FileObject> () {
                public FileObject run () throws IOException {
                    FileObject fo;
                    if (trg.isData ()) {
                        fo = trg;
                    } else {
                         String n;
                         if (name == null) {
                             // #45810 - if obj is disk root then fix the filename
                             String baseName = obj.getName().replace(':', '_').replace('/', '_'); // NOI18N
                             n = FileUtil.findFreeFileName (trg, baseName, ext);
                         } else {
                             n = name;
                         }
                         fo = trg.createData (n, ext);
                    }
                    writeShadowFile(fo, obj.getPrimaryFile().getURL());
                    return fo;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException ();
        }
    }
    
    /** Overwrites existing file object with new link and fs.
     * @exception IOException on I/O error
     */
    static void writeOriginal (final FileObject shadow, final URL url) throws IOException {
        try {
            MUTEX.writeAccess (new Mutex.ExceptionAction<Void> () {
                public Void run () throws IOException {
                    writeShadowFile(shadow, url);
                    return null;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException ();
        }
    }

    /**
     * Writes content of shadow file.
     * @param fo shadow file
     * @param url URL to original
     */
    private static void writeShadowFile(FileObject fo, URL url) throws IOException {
        FileLock lock = fo.lock();
        Writer os = new OutputStreamWriter(fo.getOutputStream(lock), "UTF-8");
        try {
            os.write(url.toExternalForm()); // NOI18N
        } finally {
            os.close();
            lock.releaseLock();
        }
    }
    
    /**
     * Tries to load the original file from a shadow.
     * Looks for file contents as well as the originalFile/originalFileSystem attributes.
     * @param fileObject a data shadow
     * @return the original <code>DataObject</code> referenced by the shadow
     * @throws IOException error during load or broken link
     */
    protected static DataObject deserialize(FileObject fileObject) throws IOException {
        String[] fileAndFileSystem = readOriginalFileAndFileSystem(fileObject);
        assert fileAndFileSystem[0] != null;
        FileObject target;
        URI u;
        try {
            u = new URI(fileAndFileSystem[0]);
        } catch (URISyntaxException e) {
            u = null;
        }
        if (u != null && u.isAbsolute()) {
            target = URLMapper.findFileObject(u.toURL());
        } else {
            FileSystem fs;
            if ("SystemFileSystem".equals(fileAndFileSystem[1])) { // NOI18N
                fs = Repository.getDefault().getDefaultFileSystem();
            } else {
                // Even if it is specified, we no longer have mounts, so we can no longer find it.
                fs = fileObject.getFileSystem();
            }
            target = fs.findResource(fileAndFileSystem[0]);
        }
        if (target != null) {
            return DataObject.find(target);
        } else {
            throw new FileNotFoundException(fileAndFileSystem[0] + ':' + fileAndFileSystem[1]);
        }
    }
    static URL readURL(FileObject fileObject) throws IOException {
        String[] fileAndFileSystem = readOriginalFileAndFileSystem(fileObject);
        assert fileAndFileSystem[0] != null;
        URI u;
        try {
            u = new URI(fileAndFileSystem[0]);
        } catch (URISyntaxException e) {
            u = null;
        }
        if (u != null && u.isAbsolute()) {
            return u.toURL();
        } else {
            FileSystem fs;
            if ("SystemFileSystem".equals(fileAndFileSystem[1])) { // NOI18N
                fs = Repository.getDefault().getDefaultFileSystem();
            } else {
                fs = fileObject.getFileSystem();
            }
            return new URL(fs.getRoot().getURL(), fileAndFileSystem[0]);
        }
    }
    private static String[] readOriginalFileAndFileSystem(final FileObject f) throws IOException {
        if ( f.getSize() == 0 ) {
            Object fileName = f.getAttribute("originalFile"); // NOI18N
            if ( fileName instanceof String ) {
                return new String[] {(String) fileName, (String) f.getAttribute("originalFileSystem")}; // NOI18N
            } else if (fileName instanceof URL) {
                return new String[] {((URL) fileName).toExternalForm(), null};
            } else {
                throw new FileNotFoundException(f.getPath());
            }
        } else {
            try {
                return MUTEX.readAccess(new Mutex.ExceptionAction<String[]>() {
                    public String[] run() throws IOException {
                        BufferedReader ois = new BufferedReader(new InputStreamReader(f.getInputStream(), "UTF-8")); // NOI18N
                        try {
                            String s = ois.readLine();
                            String fs = ois.readLine();
                            return new String[] {s, fs};
                        } finally {
                            ois.close();
                        }
                    }
                });
            } catch (MutexException e) {
                throw (IOException) e.getException();
            }
        }
    }
    
    private FileObject checkOriginal (DataObject orig) throws IOException {
        if (orig == null)
            return null;
        return deserialize(getPrimaryFile()).getPrimaryFile();
    }

    /** Return the original shadowed object.
    * @return the data object
    */
    public DataObject getOriginal () {
        waitUpdatesProcessed();
        return original;
    }
    
    /** Implementation of Container interface.
     * @return array of one element, the original
     */
    public DataObject[] getChildren () {
        return new DataObject[] { original };
    }

    /* Creates node delegate.
    */
    protected Node createNodeDelegate () {
        return new ShadowNode (this);
    }

    /* Getter for delete action.
    * @return true if the object can be deleted
    */
    public boolean isDeleteAllowed () {
        return getPrimaryFile().canWrite();
    }

    /* Getter for copy action.
    * @return true if the object can be copied
    */
    public boolean isCopyAllowed ()  {
        return true;
    }

    /* Getter for move action.
    * @return true if the object can be moved
    */
    public boolean isMoveAllowed ()  {
        return getPrimaryFile().canWrite();
    }

    /* Getter for rename action.
    * @return true if the object can be renamed
    */
    public boolean isRenameAllowed () {
        return getPrimaryFile().canWrite();
    }

    /* Help context for this object.
    * @return help context
    */
    public HelpCtx getHelpCtx () {
        return original.getHelpCtx ();
    }

    /* Creates shadow for this object in specified folder. The current
    * implementation creates reference data shadow and pastes it into
    * specified folder.
    *
    * @param f the folder to create shortcut in
    * @return the shadow
    */
    protected DataShadow handleCreateShadow (DataFolder f) throws IOException {
        if (original instanceof DataFolder) {
            DataFolder.testNesting(((DataFolder)original), f);
        }
        return original.handleCreateShadow (f);
    }

    /* Scans the orginal bundle */
    @Override
    public <T extends Node.Cookie> T getCookie(Class<T> c) {
        if (c.isInstance(this)) {
            return c.cast(this);
        }
        return original.getCookie (this, c);
    }

    /* Try to refresh link to original file */
    public void refresh() {
        refresh(false);
    }
    
    private void refresh(boolean moved) {        
        try {
            /* Link isn't broken */            
            if (moved)
                tryUpdate();
            FileObject obj = checkOriginal(original);
            if (obj != null) {
                if (obj != this.original.getPrimaryFile ()) {
                    this.setOriginal (DataObject.find (obj));
                }
                return;
            }
        } catch (IOException e) {            
        }
        try {            
            /* Link is broken */
            this.setValid(false);            
        } catch (java.beans.PropertyVetoException e) {                        
        }         
    }
    
    private void tryUpdate() throws IOException {
        URL url = readURL(getPrimaryFile ());
        if (url.equals(original.getPrimaryFile().getURL())) {
            return;
        }
        writeOriginal (null, null, getPrimaryFile (), original);
    }
    
    private void setOriginal (DataObject o) {
        if (origL == null) {
            origL = new OrigL (this);
        }

        // set new original
        if (original != null) {
            original.removePropertyChangeListener (origL);
        }

        DataObject oldOriginal = original;
        
        o.addPropertyChangeListener (origL);
        original = o;

        // update nodes
        ShadowNode n [] = null;
        synchronized (nodes) {
            n = (ShadowNode [])nodes.toArray (new ShadowNode [nodes.size ()]);
        }
        
        try {
            for (int i = 0; i < n.length; i++) {
                n[i].originalChanged ();
            }
        }
        catch (IllegalStateException e) {
            System.out.println("Please reopen the bug #18998 if you see this message."); // NOI18N
            System.out.println("Old:"+oldOriginal + // NOI18N
                ((oldOriginal == null) ? "" : (" / " + oldOriginal.isValid() + " / " + System.identityHashCode(oldOriginal)))); // NOI18N
            System.out.println("New:"+original + // NOI18N
                ((original == null) ? "" : (" / " + original.isValid() + " / " + System.identityHashCode(original)))); // NOI18N
            throw e;
        }
    }
    
    private static RequestProcessor RP = new RequestProcessor("DataShadow validity check");
    private static Reference<Task> lastTask = new WeakReference<Task>(null);

    private static void updateShadowOriginal(DataShadow shadow) {
        class Updator implements Runnable {
            DataShadow sh;
            FileObject primary;
            
            public void run () {
                DataObject newOrig;

                try {
                    newOrig = DataObject.find (primary);
                } catch (DataObjectNotFoundException e) {
                    newOrig = null;
                }

                if (newOrig != null) {
                    sh.setOriginal (newOrig);
                } else {
                    checkValidity (new OperationEvent (sh.original));
                }
                
                primary = null;
                sh = null;
            }
        }
        Updator u = new Updator();
        u.sh = shadow;
        u.primary = u.sh.original.getPrimaryFile ();
        ERR.fine("updateShadowOriginal: " + u.sh + " primary " + u.primary); // NOI18N
        lastTask = new WeakReference<Task>(RP.post(u, 100, Thread.MIN_PRIORITY + 1));
    }
    
    /** For use in tests that need to be sure that all updators have finished.
     */
    static final void waitUpdatesProcessed() {
        if (!RP.isRequestProcessorThread()) {
            Task t = lastTask.get();
            if (t != null) {
                t.waitFinished();
            }
        }
    }
    
    protected DataObject handleCopy (DataFolder f) throws IOException {
        if (original instanceof DataFolder) {
            DataFolder.testNesting(((DataFolder)original), f);
        }
        return super.handleCopy(f);
    }
    
    protected FileObject handleMove (DataFolder f) throws IOException {
        if (original instanceof DataFolder) {
            DataFolder.testNesting(((DataFolder)original), f);
        }
        return super.handleMove(f);
    }

    private static class OrigL implements PropertyChangeListener {
        Reference<DataShadow> shadow = null;
        public OrigL (DataShadow shadow) {
            this.shadow = new WeakReference<DataShadow> (shadow);
        }
        public void propertyChange (PropertyChangeEvent evt) {
            final DataShadow shadow = this.shadow.get ();

            if (shadow != null && DataObject.PROP_VALID.equals (evt.getPropertyName ())) {
                updateShadowOriginal(shadow);
            }
        }
    }

    /** Node for a shadow object. */
    protected static class ShadowNode extends FilterNode {
        /** message to create name of node */
        private static MessageFormat format;
        /** message to create short description of node */
        private static MessageFormat descriptionFormat;
        /** if true, the DataShadow name is used instead of original's name, 
         * affects DataShadows of filesystem roots only
         */
        private static final String ATTR_USEOWNNAME = "UseOwnName"; //NOI18N

        /** shadow */
        private DataShadow obj;

        /** the sheet computed for this node or null */
        private Sheet sheet;
        
        /** Create a shadowing node.
         * @param shadow the shadow
         */
        public ShadowNode (DataShadow shadow) {
            this (shadow, shadow.original.getNodeDelegate ());
        }

        /** Initializes it */
        private ShadowNode (DataShadow shadow, Node node) {
            super (node);
            this.obj = shadow;
            synchronized (this.obj.nodes) {
                this.obj.nodes.add (this);
            }
        }

        /* Clones the node
        */
        public Node cloneNode () {
            ShadowNode sn = new ShadowNode (obj);
            return sn;
        }

        /* Renames the shadow data object.
        * @param name new name for the object
        * @exception IllegalArgumentException if the rename failed
        */
        public void setName (String name) {
            try {
                if (!name.equals (obj.getName ())) {
                    obj.rename (name);
                    if (obj.original.getPrimaryFile ().isRoot ()) {
                        obj.getPrimaryFile ().setAttribute (ATTR_USEOWNNAME, Boolean.TRUE);
                    }
                    fireDisplayNameChange (null, null);
                    fireNameChange (null, null);
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException (ex.getMessage ());
            }
        }

        /** The name of the shadow.
        * @return the name
        */
        public String getName () {
            return obj.getName ();
        }

        /* Creates name based on the original one.
        */
        public String getDisplayName () {
            if (format == null) {
                format = new MessageFormat (NbBundle.getBundle (DataShadow.class).getString ("FMT_shadowName"));
            }
            String n = format.format (createArguments ());
            try {
                return obj.getPrimaryFile().getFileSystem().getStatus().annotateName(n, obj.files());
            } catch (FileStateInvalidException fsie) {
                // ignore
                return n;
            }
        }
        public String getHtmlDisplayName() {
            if (format == null) {
                format = new MessageFormat(NbBundle.getBundle(DataShadow.class).getString("FMT_shadowName"));
            }
            try {
                String n = XMLUtil.toElementContent(format.format(createArguments()));
                FileSystem.Status s = obj.getPrimaryFile().getFileSystem().getStatus();
                if (s instanceof FileSystem.HtmlStatus) {
                    return ((FileSystem.HtmlStatus) s).annotateNameHtml(n, obj.files());
                }
            } catch (IOException e) {
                // ignore, OK
            }
            return null;
        }

        /** Creates arguments for given shadow node */
        private Object[] createArguments () {
            String origDisp;
            String shadowName = obj.getName ();
            if (obj.original.isValid()) {
                origDisp = obj.original.getNodeDelegate().getDisplayName();
            } else {
                // We will soon be a broken data shadow, in the meantime...
                origDisp = ""; // NOI18N
            }
            Boolean useOwnName = (Boolean)obj.getPrimaryFile ().getAttribute (ATTR_USEOWNNAME);
            if (obj.original.getPrimaryFile ().isRoot () && 
                (useOwnName == null || !useOwnName.booleanValue ())) {
                try {
                    shadowName = obj.original.getPrimaryFile ().getFileSystem ().getDisplayName ();
                } catch (FileStateInvalidException e) {
                    // ignore
                }
            }
            return new Object[] {
                       shadowName, // name of the shadow
                       super.getDisplayName (), // name of original
                       systemNameOrFileName (obj.getPrimaryFile ()), // full name of file for shadow
                       systemNameOrFileName (obj.original.getPrimaryFile ()), // full name of original file
                       origDisp, // display name of original
                   };
        }

        /** System name of file name
        */
        private static String systemNameOrFileName (FileObject fo) {
            return FileUtil.getFileDisplayName(fo);
        }

        /* Creates description based on the original one.
        */
        public String getShortDescription () {
            if (descriptionFormat == null) {
                descriptionFormat = new MessageFormat (
                                        NbBundle.getBundle (DataShadow.class).getString ("FMT_shadowHint")
                                    );
            }
            return descriptionFormat.format (createArguments ());
        }
        
        /* Show filesystem icon if it is a root.
         */
        public Image getIcon(int type) {
            Image i = rootIcon(type);
            if (i != null) {
                return i;
            } else {
                return super.getIcon(type);
            }
        }
        public Image getOpenedIcon(int type) {
            Image i = rootIcon(type);
            if (i != null) {
                return i;
            } else {
                return super.getOpenedIcon(type);
            }
        }
        private Image rootIcon(int type) {
            FileObject orig = obj.original.getPrimaryFile();
            if (orig.isRoot()) {
                try {
                    FileSystem fs = orig.getFileSystem();
                    try {
                        Image i = Introspector.getBeanInfo(fs.getClass()).getIcon(type);
                        return fs.getStatus().annotateIcon(i, type, obj.original.files());
                    } catch (IntrospectionException ie) {
                        Logger.getLogger(DataShadow.class.getName()).log(Level.WARNING, null, ie);
                        // ignore
                    }
                } catch (FileStateInvalidException fsie) {
                    // ignore
                }
            }
            return null;
        }

        /* @return obj.isDeleteAllowed () */
        public boolean canDestroy () {
            return obj.isDeleteAllowed ();
        }

        /* Destroyes the node
        */
        public void destroy () throws IOException {
            synchronized (obj.nodes) {
                obj.nodes.remove (this);
            }
            obj.delete ();
            //      super.destroy ();
        }

        /** @return true if shadow can be renamed
        */
        public final boolean canRename () {
            return obj.isRenameAllowed ();
        }

        /* Returns true if this object allows copying.
        * @returns true if so
        */
        public final boolean canCopy () {
            return obj.isCopyAllowed ();
        }

        /* Returns true if this object allows cutting.
        * @returns true if so
        */
        public final boolean canCut () {
            return obj.isMoveAllowed ();
        }

        /* First of all the DataObject.getCookie method is
        * called. If it produces non-null result, it is returned.
        * Otherwise the value returned from super.getCookie
        * method is returned.
        *
        * @return the cookie or null
        */
        @Override
        public <T extends Node.Cookie> T getCookie(Class<T> cl) {
            T c = obj.getCookie(cl);
            if (c != null) {
                return c;
            } else {
                return super.getCookie (cl);
            }
        }

        /** Returns modified properties of the original node.
        * @return property sets 
        */
        public PropertySet[] getPropertySets () {
            Sheet s = sheet;
            if (s == null) {
                s = sheet = cloneSheet ();
            }
            return s.toArray ();
        }

        /** Copy this node to the clipboard.
        *
        * @return {@link org.openide.util.datatransfer.ExTransferable.Single} with one copy flavor
        * @throws IOException if it could not copy
        * @see NodeTransfer
        */
        public Transferable clipboardCopy () throws IOException {
            ExTransferable t = ExTransferable.create (super.clipboardCopy ());
            t.put (LoaderTransfer.transferable (
                obj, 
                LoaderTransfer.CLIPBOARD_COPY)
            );
            return t;
        }

        /** Cut this node to the clipboard.
        *
        * @return {@link org.openide.util.datatransfer.ExTransferable.Single} with one cut flavor
        * @throws IOException if it could not cut
        * @see NodeTransfer
        */
        public Transferable clipboardCut () throws IOException {
            ExTransferable t = ExTransferable.create (super.clipboardCut ());
            t.put (LoaderTransfer.transferable (
                obj, 
                LoaderTransfer.CLIPBOARD_CUT)
            );
            return t;
        }
        /**
        * This implementation only calls clipboardCopy supposing that 
        * copy to clipboard and copy by d'n'd are similar.
        *
        * @return transferable to represent this node during a drag
        * @exception IOException when the
        *    cut cannot be performed
        */
        public Transferable drag () throws IOException {
            return clipboardCopy ();
        }

        /** Creates a node listener that allows listening on the
        * original node and propagating events to the proxy.
        * <p>Intended for overriding by subclasses, as with {@link #createPropertyChangeListener}.
        *
        * @return a {@link org.openide.nodes.FilterNode.NodeAdapter} in the default implementation
        */
        protected org.openide.nodes.NodeListener createNodeListener () {
            return new PropL (this);
        }

        /** Equal if the o is ShadowNode to the same shadow object.
        */
        public boolean equals (Object o) {
            if (o instanceof ShadowNode) {
                ShadowNode sn = (ShadowNode)o;
                return sn.obj == obj;
            }
            return false;
        }

        /** Hashcode is computed by the represented shadow.
        */
        public int hashCode () {
            return obj.hashCode ();
        }


        /** Clones the property sheet of original node.
        */
        private Sheet cloneSheet () {
            PropertySet[] sets = this.getOriginal().getPropertySets ();

            Sheet s = new Sheet ();
            for (int i = 0; i < sets.length; i++) {
                Sheet.Set ss = new Sheet.Set ();
                ss.put (sets[i].getProperties ());
                ss.setName (sets[i].getName ());
                ss.setDisplayName (sets[i].getDisplayName ());
                ss.setShortDescription (sets[i].getShortDescription ());

                // modifies the set if it contains name of object property
                modifySheetSet (ss);

                s.put (ss);
            }

            return s;
        }

        /** Modifies the sheet set to contain name of property and name of
        * original object.
        */
        private void modifySheetSet (Sheet.Set ss) {
            Property p = ss.remove (DataObject.PROP_NAME);
            if (p != null) {
                p = new PropertySupport.Name (this);
                ss.put (p);

                p = new Name ();
                ss.put (p);
            }
        }

        private void originalChanged () {
            DataObject ori = obj.original;
            if (ori.isValid()) {
                changeOriginal (ori.getNodeDelegate(), true);
            } else {
                updateShadowOriginal(obj);
            }
        }

        /** Class that renames the original object and also updates
        * the link
        */
        private final class Name extends PropertySupport.ReadWrite<String> {
            public Name () {
                super (
                    "OriginalName", // NOI18N
                    String.class,
                    DataObject.getString ("PROP_ShadowOriginalName"),
                    DataObject.getString ("HINT_ShadowOriginalName")
                );
            }

            public String getValue () {
                return obj.original.getName();
            }

            public void setValue (String val) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
                if (!canWrite())
                    throw new IllegalAccessException();

                try {
                    DataObject orig = obj.original;
                    orig.rename (val);
                    writeOriginal (null, null, obj.getPrimaryFile (), orig);
                } catch (IOException ex) {
                    throw new InvocationTargetException (ex);
                }
            }

            public boolean canWrite () {
                return obj.original.isRenameAllowed();
            }
        }
        
        /** Property listener on data object that delegates all changes of
        * properties to this node.
        */
        private static class PropL extends FilterNode.NodeAdapter {
            public PropL (ShadowNode sn) {
                super (sn);
            }

            protected void propertyChange (FilterNode fn, PropertyChangeEvent ev) {
              if (Node.PROP_PROPERTY_SETS.equals(ev.getPropertyName ())) {
                // clear the sheet
                ShadowNode sn = (ShadowNode)fn;
                sn.sheet = null;
              }
              
              super.propertyChange (fn, ev);
              }
        }
    }
    
    static final class DSWeakReference<T> extends WeakReference<T>
    implements Runnable {
        private int hash;
        private FileObject original;
        
        DSWeakReference(T o) {
            super(o, org.openide.util.Utilities.activeReferenceQueue());
            this.hash = o.hashCode();
            if (o instanceof DataShadow) {
                DataShadow s = (DataShadow)o;
                this.original = s.original.getPrimaryFile ();
            }
        }
        
        public int hashCode() {
            return hash;
        }
        
        public boolean equals(Object o) {
            T mine = get();
            if (mine == null) {
                return false;
            }
            
            if (o instanceof DSWeakReference) {
                DSWeakReference him = (DSWeakReference) o;
                return mine.equals(him.get());
            }
            
            return false;
        }

        public void run() {
            if (original != null) {
                synchronized (getDataShadowsSet ()) {
                    getDataShadowsSet().remove(original);
                }            
            } else {
                synchronized (BrokenDataShadow.getDataShadowsSet()) {
                    BrokenDataShadow.getDataShadowsSet().remove(this); // XXX this is wrong - key is String (URL of broken shadow)
                }
            }
        }
    }
}
