/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.openfile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.ExternalDropHandler;

/**
 *
 * @author S. Aubrecht
 */
public class DefaultExternalDropHandler extends ExternalDropHandler {
    
    public boolean canDrop(DropTargetDragEvent e) {
        return canDrop( e.getCurrentDataFlavors() );
    }

    public boolean canDrop(DropTargetDropEvent e) {
        return canDrop( e.getCurrentDataFlavors() );
    }

    boolean canDrop( DataFlavor[] flavors ) {
        for( int i=0; null != flavors && i<flavors.length; i++ ) {
            if( DataFlavor.javaFileListFlavor.equals( flavors[i] )
                || getUriListDataFlavor().equals( flavors[i] ) ) {

                return true;
            }
        }
        return false;
    }

    public boolean handleDrop(DropTargetDropEvent e) {
        Transferable t = e.getTransferable();
        if( null == t )
            return false;
        List fileList = getFileList( t );

        if( null != fileList && !fileList.isEmpty() ) {
            for( Iterator i=fileList.iterator(); i.hasNext(); ) {
                openFile( (File)i.next() );
            }
            return true;
        }
        return false;
    }

    List getFileList( Transferable t ) {
        try {
            if( t.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
                //windows & mac
                return (List)t.getTransferData( DataFlavor.javaFileListFlavor );
            } else if( t.isDataFlavorSupported( getUriListDataFlavor() ) ) {
                //linux
                String uriList = (String)t.getTransferData( getUriListDataFlavor() );
                return textURIListToFileList( uriList );
            }
        } catch( UnsupportedFlavorException ex ) {
            ErrorManager.getDefault().notify( ErrorManager.INFORMATIONAL, ex );
        } catch( IOException ex ) {
            ErrorManager.getDefault().notify( ErrorManager.INFORMATIONAL, ex );
        }
        return null;
    }

    void openFile( File file ) {
        FileObject fo = FileUtil.toFileObject( FileUtil.normalizeFile( file ) );
        OpenFile.open( fo );
    }

    private static DataFlavor uriListDataFlavor;

    DataFlavor getUriListDataFlavor() {
        if( null == uriListDataFlavor ) {
            try {
                uriListDataFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
            } catch( ClassNotFoundException cnfE ) {
                //cannot happen
                throw new AssertionError(cnfE);
            }
        }
        return uriListDataFlavor;
    }

    List textURIListToFileList( String data ) {
        List list = new ArrayList(1);
        for( StringTokenizer st = new StringTokenizer(data, "\r\n");
            st.hasMoreTokens();) {
            String s = st.nextToken();
            if( s.startsWith("#") ) {
                // the line is a comment (as per the RFC 2483)
                continue;
            }
            try {
                URI uri = new URI(s);
                File file = new File(uri);
                list.add( file );
            } catch( java.net.URISyntaxException e ) {
                // malformed URI
            } catch( IllegalArgumentException e ) {
                // the URI is not a valid 'file:' URI
            }
        }
        return list;
    }
}
