/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visualweb.insync.faces;


import org.netbeans.modules.visualweb.insync.UndoEvent;
import org.netbeans.modules.visualweb.insync.beans.BeanStructureScanner;
import org.netbeans.modules.visualweb.insync.beans.BeansUnit;
import org.netbeans.modules.visualweb.insync.java.MethodInfo;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ListIterator;
import org.netbeans.modules.visualweb.insync.java.Statement;
import org.openide.util.NbBundle;

public class ThresherFacesBeanStructureScanner extends FacesBeanStructureScanner {
    public static String PROP_INITMETHOD = "_init";
    public static String INITMETHOD = "init";
    public static String DESTROYMETHOD = "destroy";

    protected MethodInfo initInfo = new MethodInfo(INITMETHOD, Modifier.PUBLIC, Void.TYPE, 
            getInitMethodComment(), ENSURE_INITBLOCK);

    public ThresherFacesBeanStructureScanner(BeansUnit unit) {
        super(unit);
        ctorInfo.setComment(getConstructorComment());
        ctorInfo.setEnsureMethodName(null);
        destroyInfo = new MethodInfo(DESTROYMETHOD);
        destroyInfo.setComment(getDestroyMethodComment());
        propertiesInitInfo = new MethodInfo(PROP_INITMETHOD, Modifier.PRIVATE, Void.TYPE,
                getComment("COMMENT_PropInitMethodComment"), null, "Exception");
    }

   public List<Statement> getPropertiesInitStatements() {
       List<Statement> stmts = ctorInfo.getMethod().getPropertySetStatements();
       stmts.addAll(propertiesInitInfo.getMethod().getPropertySetStatements());
       return stmts;
   }

   protected void ensureInitBlock(MethodInfo mi) {
       UndoEvent event = null;
       try {
           String eventName = NbBundle.getMessage(BeanStructureScanner.class, "EnsureInitBlock"); //NOI18N
           event = beansUnit.getModel().writeLock(eventName);
           if(mi.getMethod().hasInitBlock()) {
               return;
           }
           String bodyText = "// " + getComment("COMMENT_InitSuperCall") + "\n" +
                   "super.init();\n" +
                   "// " + getComment("COMMENT_UserPreInit") + "\n\n" +
                   "// <editor-fold defaultstate=\"collapsed\" desc=\"" +
                   getComment("COMMENT_InitDescription") + "\">\n" +
                   "// " + getComment("COMMENT_PropInit") + "\n" +
                   "try {\n" +
                   "_init();\n" +
                   "} catch (Exception e) {\n" +
                   "log(\"Page1 Initialization Failure\", e);\n" +
                   "throw e instanceof FacesException ? (FacesException) e: new FacesException(e);\n" +
                   "}\n\n" +
                   "// </editor-fold>\n" +
                   "// " + getComment("COMMENT_UserPostInit");
           mi.getMethod().replaceBody(bodyText);
           javaUnit.ensureImport("javax.faces.FacesException");
       }finally {
           if(event != null) {
               beansUnit.getModel().writeUnlock(event);
           }
       }
   }

   public String getConstructorComment() {
       return null;
   }
   
   public String getDestroyMethodComment() {
       return null;
   }
   
   public String getInitMethodComment() {
       return null;
   }
    
   public String getComment(String id) {
       return NbBundle.getMessage(ThresherFacesBeanStructureScanner.class, id);
   }
}
