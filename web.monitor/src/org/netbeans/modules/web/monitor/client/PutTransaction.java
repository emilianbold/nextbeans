/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.monitor.client; 

import java.io.*;
import java.text.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.servlet.*;
import javax.servlet.http.*;
import org.netbeans.modules.web.monitor.server.Constants;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;

/*
 * Put a transaction
 */
  
public class PutTransaction extends HttpServlet {


    private static FileObject currDir = null;
    private static boolean debug = false;
     
    private ServletConfig servletConfig = null;
    

    public void doPost(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {
	 
	if(debug) log("doPost"); //NOI18N
	if(currDir == null) {
	    try { 
		currDir = Controller.getCurrDir();
	    }
	    catch(FileNotFoundException ex) {
		// PENDING report this error properly
		if(debug) log("Couldn't write the transaction data");  //NOI18N
		return;
	    }
	}

	// As soon as you get the parameters, you've gotten an input
	// string for this. Don't do that. 

	String id = req.getQueryString(); 
	FileObject fo = null;
	FileLock lock = null;
	PrintWriter fout = null, out = null;
	InputStreamReader isr = null;
	 
	try {
	    if(debug) log(" Trying to add the transaction"); //NOI18N

	    // PENDING: the id is parsed in TransactionNode. Should
	    // *not* do that twice - may be it should be parsed here. 
	    String name = 
		id.substring(0, id.indexOf(Constants.Punctuation.itemSep));
		
	    if(debug) log(" Before creating the file"); //NOI18N
	    fo = currDir.createData(name, "xml"); //NOI18N
	    if(debug) log(" After creating the file"); //NOI18N
	    lock = fo.lock();
	    if(debug) log(" Got the lock"); //NOI18N
	    fout = new PrintWriter(fo.getOutputStream(lock));
	    if(debug) log(" Got the lock, reading buffer"); //NOI18N

	    isr = new InputStreamReader(req.getInputStream());

	    char[] charBuf = new char[4096];
	    int numChars;
	     
	    while((numChars = isr.read(charBuf, 0, 4096)) != -1) {
		fout.write(charBuf, 0, numChars);
		//if(debug) log(new String(charBuf));
	    }

 	    if(debug) log("...success"); //NOI18N

	    res.setContentType("text/plain");  //NOI18N
	    out = res.getWriter();
	    out.println(Constants.Comm.ACK); 
	}
	
	catch(Exception ex) {
	    if(debug) log("Couldn't add the transaction");  //NOI18N
	    if(debug) 
		log("MonitorAction.getController(): " +  //NOI18N
		    MonitorAction.getController());
	    if (debug) log(ex); 
	}
	finally {
	    
	    try {
		lock.releaseLock();
		if(debug) log(" Released the lock"); //NOI18N
	    }
	    catch(Exception ex1) { }

	    try { out.close(); }
	    catch(Exception ex2) { }

	    try { isr.close();}
	    catch(Exception ex3) { }

	    try { fout.close(); }
	    catch(Exception ex4) { }
	}
	MonitorAction.getController().addTransaction(id); 
    }

    // PENDING - deal better with this
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {

	if(debug) log("doGet");  //NOI18N

	PrintWriter out = res.getWriter();
	try { 
	    //out.println(id); 
	    out.println("Shouldn't use GET for this!");  //NOI18N
	}
	catch (Exception e) { 
	    if(debug) log(e.getMessage());
	}
	try { out.close(); } catch(Exception ex) {}
    }


    /**
     * Init method for this filter 
     *
     */
    public void init(ServletConfig servletConfig) { 

	this.servletConfig = servletConfig;
	if(debug) log("init");  //NOI18N
    }
    
    public void log(String msg) {
	System.out.println("PutTransaction::" + msg); //NOI18N
	
    }

    public void log(Throwable t) {
	log(getStackTrace(t));
    }


    public static String getStackTrace(Throwable t) {

	String stackTrace = null;
	    
	try {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    t.printStackTrace(pw);
	    pw.close();
	    sw.close();
	    stackTrace = sw.getBuffer().toString();
	}
	catch(Exception ex) {}
	return stackTrace;
    }

} //PutTransaction.java



