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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.performance.scalability;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Permission;
import junit.framework.Assert;

/**
 *
 * @author Jaroslav Tulach <jaroslav.tulach@netbeans.org>
 */
public final class CountingSecurityManager extends SecurityManager {
    private static int cnt;
    private static StringWriter msgs;
    private static PrintWriter pw;
    private static String prefix;
    
    public static void register() {
        initialize("NONE");
    }
    
    public static void initialize(String prefix) {
        Assert.assertNotNull(prefix);
        
        if (! (System.getSecurityManager() instanceof CountingSecurityManager)) {
            setAllowedReplace(true);
            System.setSecurityManager(new CountingSecurityManager());
            setAllowedReplace(false);
        }
        if (!System.getSecurityManager().getClass().getName().equals(CountingSecurityManager.class.getName())) {
            throw new IllegalStateException("Wrong security manager: " + System.getSecurityManager());
        }
        cnt = 0;
        msgs = new StringWriter();
        pw = new PrintWriter(msgs);
        CountingSecurityManager.prefix = prefix;
    }
    
    public static int assertCounts(String msg, int expectedCnt) {
        int real = cnt;
        if (cnt < expectedCnt / 10) {
            throw new AssertionError("Too small expectations:\n" + msg + "\n" + msgs + " exp: " + expectedCnt + " was: " + cnt);
        }
        if (expectedCnt < cnt) {
            throw new AssertionError(msg + "\n" + msgs + " exp: " + expectedCnt + " was: " + cnt);
        }
        cnt = 0;
        msgs = new StringWriter();
        pw = new PrintWriter(msgs);
        return real;
    }

    @Override
    public void checkRead(String file) {
        if (file.startsWith(prefix)) {
            cnt++;
//            pw.println("checkRead: " + file);
//            new Exception().printStackTrace(pw);
        }
    }

    @Override
    public void checkRead(String file, Object context) {
        if (file.startsWith(prefix)) {
            cnt++;
            pw.println("checkRead2: " + file);
        }
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        cnt++;
        pw.println("Fd: " + fd);
    }

    @Override
    public void checkWrite(String file) {
        if (file.startsWith(prefix)) {
            cnt++;
            pw.println("checkWrite: " + file);
        }
    }

    @Override
    public void checkPermission(Permission perm) {
        if (perm.getName().equals("setSecurityManager")) { // NOI18N - hardcoded in java.lang
            if (!isAllowedReplace()) {
                throw new SecurityException();
            }
        }
        if (perm.getName().equals("exitVM")) {
            throw new SecurityException();
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
    }

    private static boolean isAllowedReplace() {
        return Boolean.getBoolean("CountingSecurityManager.allowReplace");
    }

    private static void setAllowedReplace(boolean aAllowedReplace) {
        System.setProperty("CountingSecurityManager.allowReplace", String.valueOf(aAllowedReplace));
    }
}
