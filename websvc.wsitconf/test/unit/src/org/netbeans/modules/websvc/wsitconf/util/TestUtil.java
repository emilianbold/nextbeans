/*
 * TestUtil.java
 *
 * Created on October 4, 2005, 7:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.websvc.wsitconf.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import javax.swing.text.Document;
import org.netbeans.modules.websvc.wsitconf.util.TestCatalogModel;
import org.netbeans.modules.xml.schema.model.GlobalSimpleType;
import org.netbeans.modules.xml.schema.model.SchemaModel;
import org.netbeans.modules.xml.schema.model.SchemaModelFactory;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileLock;

/**
 *
 * @author nn136682
 */
public class TestUtil {
    public static final String EMPTY_XSD = "resources/Empty.wsdl";
    
    public static Document getResourceAsDocument(String path) throws Exception {
        InputStream in = TestUtil.class.getResourceAsStream(path);
        return loadDocument(in);
    }
    
    public static Document loadDocument(InputStream in) throws Exception {
	Document sd = new org.netbeans.editor.BaseDocument(
            org.netbeans.modules.xml.text.syntax.XMLKit.class, false);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuffer sbuf = new StringBuffer();
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                sbuf.append(line);
                sbuf.append(System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        sd.insertString(0,sbuf.toString(),null);
        return sd;
    }
    
    public static int count = 0;
    public static WSDLModel loadWSDLModel(String resourcePath) throws Exception {
        NamespaceLocation nl = NamespaceLocation.valueFromResourcePath(resourcePath);
        if (nl != null) {
            return TestCatalogModel.getDefault().getWSDLModel(nl);
        }
        String location = resourcePath.substring(resourcePath.lastIndexOf('/')+1);
        URI locationURI = new URI(location);
        TestCatalogModel.getDefault().addURI(locationURI, getResourceURI(resourcePath));
        return TestCatalogModel.getDefault().getWSDLModel(locationURI);
    }
    
    public static WSDLModel createEmptyWSDLModel() throws Exception {
        return loadWSDLModel(EMPTY_XSD);
    }
    
    /*public static WSDLModel loadWSDLModel(Document doc) throws Exception {
        return WSDLModelFactory.getDefault().getModel(doc);
    }*/
    
    public static void dumpToStream(Document doc, OutputStream out) throws Exception{
        PrintWriter w = new PrintWriter(out);
        w.print(doc.getText(0, doc.getLength()));
        w.close();
        out.close();
    }
    
    public static void dumpToFile(Document doc, File f) throws Exception {
        if (! f.exists()) {
            f.createNewFile();
        }
        OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        PrintWriter w = new PrintWriter(out);
        w.print(doc.getText(0, doc.getLength()));
        w.close();
        out.close();
    }
    
    public static File dumpToTempFile(Document doc) throws Exception {
        File f = File.createTempFile("xsm", "xsd");
        dumpToFile(doc, f);
        return f;
    }
    
    public static WSDLModel dumpAndReloadModel(Document doc) throws Exception {
        File f = dumpToTempFile(doc);
        URI dumpURI = new URI("dummyDump" + count++);
        TestCatalogModel.getDefault().addURI(dumpURI, f.toURI());
        return TestCatalogModel.getDefault().getWSDLModel(dumpURI);
    }
    
    public static Document loadDocument(File f) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        return loadDocument(in);
    }
    
    public static URI getResourceURI(String path) throws RuntimeException {
        try {
            return TestUtil.class.getResource(path).toURI();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static File getTempDir(String path) throws Exception {
        File tempdir = new File(System.getProperty("java.io.tmpdir"), path);
        tempdir.mkdirs();
        return tempdir;
    }

    public static FileObject copyResource(String path, FileObject destFolder) throws Exception {
        String filename = getFileName(path);
        
        FileObject dest = destFolder.getFileObject(filename);
        if (dest == null) {
            dest = destFolder.createData(filename);
        }
        FileLock lock = dest.lock();
        OutputStream out = dest.getOutputStream(lock);
        InputStream in = TestUtil.class.getResourceAsStream(path);
        try {
            FileUtil.copy(in, out);
        } finally {
            out.close();
            in.close();
            if (lock != null) lock.releaseLock();
        }
        return dest;
    }
    
    public static String getFileName(String path) {
        int i = path.lastIndexOf('/');
        if (i > -1) {
            return path.substring(i+1);
        }
        return path;
    }

    public static File getGoldenFile(File dir, String testName, String methodName) {
        String s = dir.getPath() + File.separator + "goldenfiles" + File.separator + 
                testName + File.separator + methodName + ".pass";
        return new File(s);
    }
}
