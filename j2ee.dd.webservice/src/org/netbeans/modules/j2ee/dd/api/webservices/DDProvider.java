/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.dd.api.webservices;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.netbeans.modules.j2ee.dd.impl.webservices.WebServicesProxy;
import org.netbeans.modules.schema2beans.Common;
import org.openide.filesystems.*;
import org.xml.sax.*;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public final class DDProvider {
    
    private static final DDProvider ddProvider = new DDProvider();
    private Map ddMap;
    
    /** Creates a new instance of WebSvcModule */
    private DDProvider() {
        //ddMap=new java.util.WeakHashMap(5);
        ddMap = new java.util.HashMap(5);
    }
    
    /**
    * Accessor method for DDProvider singleton
    * @return DDProvider object
    */
    public static DDProvider getDefault() {
        return ddProvider;
    }
    
    /**
     * Returns the root of deployment descriptor bean graph for given file object.
     * The method is useful for clints planning to read only the deployment descriptor
     * or to listen to the changes.
     * @param fo FileObject representing the xml file
     * @return Webservices object - root of the deployment descriptor bean graph
     */
    public synchronized Webservices getDDRoot(FileObject fo) throws java.io.IOException {
        if(fo == null) return null;
        WebServicesProxy webSvcProxy = getFromCache (fo);
        if (webSvcProxy!=null) {
            return webSvcProxy;
        }
        
        fo.addFileChangeListener(new FileChangeAdapter() {
            public void fileChanged(FileEvent evt) {
                FileObject fo=evt.getFile();
                try {
                    WebServicesProxy webSvcProxy = getFromCache (fo);
                    String version = null;
                    if (webSvcProxy!=null) {
                        try {
                            DDParse parseResult = parseDD(fo);
                            version = parseResult.getVersion();
                            setProxyErrorStatus(webSvcProxy, parseResult);
                            Webservices newValue = createWebSvcJar(parseResult);
                            // replacing original file in proxy Webservices
                            if (!version.equals(webSvcProxy.getVersion().toString())) {
                                webSvcProxy.setOriginal(newValue);
                            } else {// the same version
                                // replacing original file in proxy Webservices
                                if (webSvcProxy.getOriginal()==null) {
                                    webSvcProxy.setOriginal(newValue);
                                } else {
                                    webSvcProxy.getOriginal().merge(newValue,Webservices.MERGE_UPDATE);
                                }
                            }
                        } catch (SAXException ex) {
                            if (ex instanceof SAXParseException) {
                                webSvcProxy.setError((SAXParseException)ex);
                            } else if ( ex.getException() instanceof SAXParseException) {
                                webSvcProxy.setError((SAXParseException)ex.getException());
                            }
                            webSvcProxy.setStatus(Webservices.STATE_INVALID_UNPARSABLE);
                            // cbw if the state of the xml file transitions from
                            // parsable to unparsable this could be due to a user
                            // change or cvs change. We would like to still
                            // receive events when the file is restored to normal
                            // so lets not set the original to null here but wait
                            // until the file becomes parsable again to do a merge
                            //webSvcProxy.setOriginal(null);
                            webSvcProxy.setProxyVersion(version);
                        }
                    }
                } catch (java.io.IOException ex){}
            }
        });
        
        try {
            DDParse parseResult = parseDD(fo);
            SAXParseException error = parseResult.getWarning();
            Webservices original = createWebSvcJar(parseResult);
            webSvcProxy = new WebServicesProxy(original,parseResult.getVersion());
            setProxyErrorStatus(webSvcProxy, parseResult);
        } catch (SAXException ex) {
            // XXX lets throw an exception here
            webSvcProxy = new WebServicesProxy(org.netbeans.modules.j2ee.dd.impl.webservices.model_1_1.Webservices.createGraph(),"1.1");
            webSvcProxy.setStatus(Webservices.STATE_INVALID_UNPARSABLE);
            if (ex instanceof SAXParseException) {
                webSvcProxy.setError((SAXParseException)ex);
            } else if ( ex.getException() instanceof SAXParseException) {
                webSvcProxy.setError((SAXParseException)ex.getException());
            }
        }
        ddMap.put(fo, /*new WeakReference*/ (webSvcProxy));
        return webSvcProxy;
    }

    /**
     * Returns the root of deployment descriptor bean graph for given file object.
     * The method is useful for clients planning to modify the deployment descriptor.
     * Finally the {@link org.netbeans.modules.j2ee.dd.impl.webservices.model_1_1.Webservices#write(org.openide.filesystems.FileObject)} should be used
     * for writing the changes.
     * @param fo FileObject representing the xml file
     * @return Webservices object - root of the deployment descriptor bean graph
     */
    public Webservices getDDRootCopy(FileObject fo) throws java.io.IOException {
        return (Webservices)getDDRoot(fo).clone();
    }

    private WebServicesProxy getFromCache (FileObject fo) {
         return (WebServicesProxy) ddMap.get(fo);
    }
    
    /**
     * Returns the root of deployment descriptor bean graph for java.io.File object.
     *
     * @param is source representing the xml file
     * @return Webservices object - root of the deployment descriptor bean graph
     */    
    public Webservices getDDRoot(InputSource is) throws IOException, SAXException {
        DDParse parse = parseDD(is);
        Webservices webSvcJar = createWebSvcJar(parse);
        WebServicesProxy proxy = new WebServicesProxy(webSvcJar, webSvcJar.getVersion().toString());
        setProxyErrorStatus(proxy, parse);
        return proxy;
    }
    
    // PENDING j2eeserver needs BaseBean - this is a temporary workaround to avoid dependency of web project on DD impl
    /**  Convenient method for getting the BaseBean object from CommonDDBean object
     * @deprecated DO NOT USE - TEMPORARY WORKAROUND !!!!
     */
    public org.netbeans.modules.schema2beans.BaseBean getBaseBean(org.netbeans.modules.j2ee.dd.api.common.CommonDDBean bean) {
        if (bean instanceof org.netbeans.modules.schema2beans.BaseBean) return (org.netbeans.modules.schema2beans.BaseBean)bean;
        else if (bean instanceof WebServicesProxy) return (org.netbeans.modules.schema2beans.BaseBean) ((WebServicesProxy)bean).getOriginal();
        return null;
    }

    private static void setProxyErrorStatus(WebServicesProxy webSvcProxy, DDParse parse) {
        SAXParseException error = parse.getWarning();
        webSvcProxy.setError(error);
        if (error!=null) {
            webSvcProxy.setStatus(Webservices.STATE_INVALID_PARSABLE);
        } else {
            webSvcProxy.setStatus(Webservices.STATE_VALID);
        }
    }
    
    private static Webservices createWebSvcJar(DDParse parse) {        
          Webservices jar = null;
          String version = parse.getVersion();
          if (Webservices.VERSION_1_1.equals(version)) {
              return new org.netbeans.modules.j2ee.dd.impl.webservices.model_1_1.Webservices(parse.getDocument(),  Common.USE_DEFAULT_VALUES);
          } 
          
          return jar;
    }
  
    private static class DDResolver implements EntityResolver {
        static DDResolver resolver;
        static synchronized DDResolver getInstance() {
            if (resolver==null) {
                resolver=new DDResolver();
            }
            return resolver;
        }        
        public InputSource resolveEntity (String publicId, String systemId) {
            if ("http://www.ibm.com/webservices/xsd/j2ee_web_services_1_1.xsd".equals(systemId)) {
                return new InputSource("nbres:/org/netbeans/modules/j2ee/dd/impl/resources/j2ee_web_services_1_1.xsd"); //NOI18N
            } else if ("http://java.sun.com/xml/ns/j2ee/j2ee_web_services_1_1.xsd".equals(systemId)) {
                return new InputSource("nbres:/org/netbeans/modules/j2ee/dd/impl/resources/j2ee_web_services_1_1.xsd"); //NOI18N
            } else {
                // use the default behaviour
                return null;
            }
        }
    }
    
    private static class ErrorHandler implements org.xml.sax.ErrorHandler {
        private int errorType=-1;
        SAXParseException error;

        public void warning(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
            if (errorType<0) {
                errorType=0;
                error=sAXParseException;
            }
            //throw sAXParseException;
        }
        public void error(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
            if (errorType<1) {
                errorType=1;
                error=sAXParseException;
            }
            //throw sAXParseException;
        }        
        public void fatalError(org.xml.sax.SAXParseException sAXParseException) throws org.xml.sax.SAXException {
            errorType=2;
            throw sAXParseException;
        }
        
        public int getErrorType() {
            return errorType;
        }
        public SAXParseException getError() {
            return error;
        }        
    }

    public SAXParseException parse(FileObject fo) 
    throws org.xml.sax.SAXException, java.io.IOException {
        DDParse parseResult = parseDD(fo);
        return parseResult.getWarning();
    }
    
    private DDParse parseDD (FileObject fo) 
    throws SAXException, java.io.IOException {
        return parseDD(fo.getInputStream());
    }
    
    private DDParse parseDD (InputStream is) 
    throws SAXException, java.io.IOException {
        return parseDD(new InputSource(is));
    }
    
    private DDParse parseDD (InputSource is) 
    throws SAXException, java.io.IOException {
        DDProvider.ErrorHandler errorHandler = new DDProvider.ErrorHandler();
        org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();
        parser.setErrorHandler(errorHandler);
        parser.setEntityResolver(DDProvider.DDResolver.getInstance());
        // XXX do we need validation here, if no one is using this then
        // the dependency on xerces can be removed and JAXP can be used
        parser.setFeature("http://xml.org/sax/features/validation", true); //NOI18N
        parser.setFeature("http://apache.org/xml/features/validation/schema", true); // NOI18N
        parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true); //NOI18N
        parser.parse(is);
        Document d = parser.getDocument();
        SAXParseException error = errorHandler.getError();
        return new DDParse(d, error);
    }
    
    /**
     * This class represents one parse of the deployment descriptor
     */
    private static class DDParse {
        private Document document;
        private SAXParseException saxException;
        private String version;
        public DDParse(Document d, SAXParseException saxEx) {
            document = d;
            saxException = saxEx;
            extractVersion();
        }
        
        /**
         * @return document from last parse
         */
        public Document getDocument() {
            return document;
        }
        
        /**
         * @return version of deployment descriptor. 
         */
        private void extractVersion () {
            // This is the default version
            version = Webservices.VERSION_1_1;
            
        }
        
        public String getVersion() {
            return version;
        }
        
        /** 
         * @return validation error encountered during the parse
         */
        public SAXParseException getWarning() {
            return saxException;
        }
    }
    
}
