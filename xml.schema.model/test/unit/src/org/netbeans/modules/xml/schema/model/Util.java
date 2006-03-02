/*
 * Util.java
 *
 * Created on October 4, 2005, 7:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.xml.schema.model;

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
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Document;
import org.netbeans.modules.xml.schema.model.impl.SchemaModelImpl;
import org.netbeans.modules.xml.schema.model.visitor.FindSchemaComponentFromDOM;
import org.netbeans.modules.xml.xam.AbstractModel;
import org.netbeans.modules.xml.xam.Model;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Element;
/**
 *
 * @author nn136682
 */
public class Util {
    public static final String EMPTY_XSD = "resources/Empty.xsd";
    
    public static Document getResourceAsDocument(String path) throws Exception {
        InputStream in = Util.class.getResourceAsStream(path);
        return loadDocument(in);
    }

    public static Document loadDocument(InputStream in) throws Exception {
//	Document sd = new PlainDocument();
        Document sd = new org.netbeans.editor.BaseDocument(
                org.netbeans.modules.xml.text.syntax.XMLKit.class, false);
        return setDocumentContentTo(sd, in);
    }
    
    public static Document setDocumentContentTo(Document doc, InputStream in) throws Exception {
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
        doc.remove(0, doc.getLength());
        doc.insertString(0,sbuf.toString(),null);
        return doc;
    }
    
    public static Document setDocumentContentTo(Document doc, String resourcePath) throws Exception {
        return setDocumentContentTo(doc, Util.class.getResourceAsStream(resourcePath));
    }

    public static void setDocumentContentTo(Model model, String resourcePath) throws Exception {
        setDocumentContentTo(((AbstractModel)model).getBaseDocument(), resourcePath);
    }
    
    public static int count = 0;
    public static SchemaModel loadSchemaModel(String resourcePath) throws Exception {
        NamespaceLocation nl = NamespaceLocation.valueFromResourcePath(resourcePath);
        if (nl != null) {
            return TestResolver.getDefault().getSchemaModel(nl);
        }
        String location = resourcePath.substring(resourcePath.lastIndexOf('/')+1);
        URI locationURI = new URI(location);
        TestResolver.getDefault().addURI(locationURI, getResourceURI(resourcePath));
        return TestResolver.getDefault().getSchemaModel(locationURI);
    }
    
    public static SchemaModel loadSchemaModel(File schemaFile) throws Exception {
        URI locationURI = new URI(schemaFile.getName());
        TestResolver.getDefault().addURI(locationURI, schemaFile.toURI());
        return TestResolver.getDefault().getSchemaModel(locationURI);
    }
    
    public static SchemaModel createEmptySchemaModel() throws Exception {
        return loadSchemaModel(EMPTY_XSD);
    }
    
    public static void dumpToStream(Document doc, OutputStream out) throws Exception{
        PrintWriter w = new PrintWriter(out);
        w.print(doc.getText(0, doc.getLength()));
        w.close();
        out.close();
    }
    
    public static void dumpToFile(Document doc, File f) throws Exception {
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
    
    public static Document loadDocument(File f) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        return loadDocument(in);
    }
    
    public static SchemaComponent findComponent(Schema schema, String xpath) {
        return (new FindSchemaComponentFromDOM().findComponent(schema, xpath));
    }
    
    public static Sequence toSequence(SchemaComponent sc) {
        return (sc instanceof Sequence) ? (Sequence) sc : null;
    }
    public static LocalElement toLocalElement(SchemaComponent sc) {
        return (sc instanceof LocalElement) ? (LocalElement) sc : null;
    }
    public static SchemaModelImpl toSchemaModelImpl(SchemaModel sc) {
        return sc instanceof SchemaModelImpl ? (SchemaModelImpl) sc : null;
    }
    
    public static GlobalElement createGlobalElement(SchemaModel model, String name) {
        GlobalElement ge = model.getFactory().createGlobalElement();
        ge.setName(name);  model.getSchema().addElement(ge);
        return ge;
    }
    public static GlobalSimpleType createGlobalSimpleType(SchemaModel model, String name) {
        GlobalSimpleType t = model.getFactory().createGlobalSimpleType();
        t.setName("LoanType");  model.getSchema().addSimpleType(t);
        return t;
    }
    
    public static GlobalComplexType createGlobalComplexType(SchemaModel model, String name) {
        GlobalComplexType t = model.getFactory().createGlobalComplexType();
        t.setName("LoanType");  model.getSchema().addComplexType(t);
        return t;
    }
    
    public static Sequence createSequence(SchemaModel m, ComplexType gct) {
        Sequence s = m.getFactory().createSequence();  gct.setDefinition(s);
        return s;
    }
    
    public static LocalElement createLocalElement(SchemaModel m, Sequence seq, String name, int i) {
        LocalElement le = m.getFactory().createLocalElement();
        seq.addContent(le, i); le.setName(name);
        return le;
    }
    
    public static LocalSimpleType createLocalSimpleType(SchemaModel m, LocalElement e) {
        LocalSimpleType t = m.getFactory().createLocalSimpleType();
        e.setInlineType(t);
        return t;
    }
    
    public static LocalComplexType createLocalComplexType(SchemaModel m, LocalElement e) {
        LocalComplexType t = m.getFactory().createLocalComplexType();
        e.setInlineType(t);
        return t;
    }
    
    public static LocalComplexType createLocalComplexType(SchemaModel m, GlobalElement e) {
        LocalComplexType t = m.getFactory().createLocalComplexType();
        e.setInlineType(t);
        return t;
    }
    
    public static GlobalSimpleType getPrimitiveType(String typeName){
        SchemaModel primitiveModel = SchemaModelFactory.getDefault().getPrimitiveTypesModel();
        Collection<GlobalSimpleType> primitives = primitiveModel.getSchema().getSimpleTypes();
        for(GlobalSimpleType ptype: primitives){
            if(ptype.getName().equals(typeName)){
                return ptype;
            }
        }
        return null;
    }
    
    public static Annotation createAnnotation(SchemaModel m, SchemaComponent p, String s) {
        Annotation a = m.getFactory().createAnnotation();
        Documentation d = m.getFactory().createDocumentation();
        a.addDocumentation(d);  p.setAnnotation(a);
        Element e = d.getDocumentationElement();  e.getOwnerDocument().createTextNode(s);
        d.setDocumentationElement(e);
        return a;
    }
    
    public static SimpleTypeRestriction createSimpleRestriction(SchemaModel m, SimpleType st) {
        SimpleTypeRestriction csr = m.getFactory().createSimpleTypeRestriction();
        st.setDefinition(csr);
        return csr;
    }
    
    public static URI getResourceURI(String path) throws RuntimeException {
        try {
            return Util.class.getResource(path).toURI();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static File getTempDir(String path) throws Exception {
        File tempdir = new File(System.getProperty("java.io.tmpdir"), path);
        tempdir.mkdirs();
        return tempdir;
    }
    
    public static void printMemoryUsage(String str){
        long init = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit();
        long cur = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        long max = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
        System.out.printf("%s:\n@@@@@@MEMORY: %d/%d/%d\n",str, (init/(1024*1024)), (cur/(1024*1024)), (max/(1024*1024)));
    }

    public static SchemaModel dumpAndReloadModel(SchemaModel sm) throws Exception {
        return dumpAndReloadModel(sm.getModelSource().getDocument());
    }
    
    public static SchemaModel dumpAndReloadModel(Document doc) throws Exception {
        File f = dumpToTempFile(doc);
        URI dumpURI = new URI("dummyDump" + count++);
        TestResolver.getDefault().addURI(dumpURI, f.toURI());
        return TestResolver.getDefault().getSchemaModel(dumpURI);
    }
}
