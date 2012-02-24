/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
package org.netbeans.modules.ide.ergonomics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.*;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class FilesAndAttributesCheck extends NbTestCase {

    public FilesAndAttributesCheck(String name) {
        super(name);
    }

    public void testGetAllTemplates() throws Exception {
        clearWorkDir();
       
        FileObject orig = FileUtil.getConfigFile("Templates");

        LocalFileSystem lfs = new LocalFileSystem();
        final File lfsRoot = new File(getWorkDir(), "copies");
        lfsRoot.mkdirs();
        lfs.setRootDirectory(lfsRoot);
        System.setProperty("fileCopies", lfsRoot.getPath());

        Enumeration<? extends FileObject> allTemplates = orig.getChildren(true);
        StringBuilder errors = new StringBuilder();
        while (allTemplates.hasMoreElements()) {
            FileObject fo = allTemplates.nextElement();

            int cnt = 0;
            Enumeration<String> allAttributes = fo.getAttributes();
            while (allAttributes.hasMoreElements()) {
                String name = allAttributes.nextElement();
                Object attr = fo.getAttribute(name);
                if (attr == null) {
                    if ("instantiatingIterator".equals(name)) {
                        final Object tvi = fo.getAttribute("templateWizardIterator");
                        if (tvi == null) {
                            fail("There shall be templateWizardIterator: " + tvi + " for " + fo + " when " + name + " is null");
                        }
                        continue;
                    }
                    fail("fo: " + fo + " has null " + name + " attribute");
                }
                System.setProperty(dynAttr + fo.getPath() + "@" + name, attr.toString());
                cnt++;

                if (attr instanceof URL) {
                    URL u = (URL) attr;
                    int read = -1;
                    try {
                        read = u.openStream().read(new byte[4096]);
                    } catch (IOException ex) {
                        errors.append(ex.getMessage()).append('\n');
                    }
                    if (read <= 0) {
                        errors.append("Resource shall exist: " + fo + " attr: " + name + " value: " + attr + "\n");
                    }
                }
            }
            System.setProperty(dynVery + fo.getPath(), String.valueOf(cnt));
            String locName = getDisplayName(fo);
            System.setProperty(dynName + fo.getPath(), locName);

            if (fo.isData()) {
                FileObject newfo = FileUtil.createData(lfs.getRoot(), fo.getPath());
                final OutputStream os = newfo.getOutputStream();
                final InputStream is = fo.getInputStream();
                FileUtil.copy(is, os);
                is.close();
                os.close();
                FileUtil.copyAttributes(fo, newfo);
                assertEquals("Old and new mimetypes are same for " + fo, fo.getMIMEType(), newfo.getMIMEType());
                newfo.setAttribute("computedMimeType", fo.getMIMEType());
            }
        }

        if (errors.length() > 0) {
            fail(errors.toString());
        }
    }
    private static final String dynName = "dynamic/name/";
    private static final String dynVery = "dynamic/verify/";
    private static final String dynAttr = "dynamic/attr/";

    public void testCheckAllTemplatesPretest() throws Exception {
        testCheckAllTemplatesReal();
    }
    public void testCheckAllTemplatesReal() throws Exception {
        Map<String, List<String>> filesAndAttribs = new TreeMap<String, List<String>>();
        for (Object o : System.getProperties().keySet()) {
            String f = (String)o;
            if (f.startsWith(dynVery)) {
                int cnt = Integer.parseInt(System.getProperties().getProperty(f));
                f = f.substring(dynVery.length());
                ArrayList<String> arr = new ArrayList<String>();
                String pref = dynAttr + f + "@";
                for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
                    if (entry.getKey() instanceof String) {
                        String a = (String) entry.getKey();
                        if (a.startsWith(pref)) {
                            a = a.substring(pref.length());
                            arr.add(a);
                        }
                    }
                }
                if (arr.size() != cnt) {
                    fail("There shall be " + cnt + " attributes for " + f);
                }
                filesAndAttribs.put(f, arr);
            }
        }
        if (filesAndAttribs.size() == 0) {
            fail("Some properties shall be set: " + System.getProperties());
        }

        Iterator<? extends String> allTemplates = filesAndAttribs.keySet().iterator();
        StringBuilder errors = new StringBuilder();
        while (allTemplates.hasNext()) {
            String fo = allTemplates.next();
            FileObject clone = FileUtil.getConfigFile(fo);

            if (clone == null) {
                errors.append("Both files exist: " + fo + "\n");
                continue;
            }
            if (!clone.getPath().startsWith("Templates/Privileged/")) {
                String locName = System.getProperties().getProperty(dynName + clone.getPath());
                assertNotNull("Localized name is recorded: " + clone, locName);
                String newName = getDisplayName(clone);
                if (!locName.equals(newName)) {
                    errors.append("Localized name for " + clone + " does not match " + locName + " != " + newName + "\n");
                }
            }
            Enumeration<String> allAttributes = Collections.enumeration(filesAndAttribs.get(fo));
            while (allAttributes.hasMoreElements()) {
                String name = allAttributes.nextElement();
                Object attr = clone.getAttribute(name);
                if (attr == null && "templateWizardIterator".equals(name)) {
                    attr = clone.getAttribute("instantiatingIterator");
                }

                if (attr == null) {
                    errors.append("Attribute " + name + " present in orig on " + fo + " but null in clone\n");
                }

                if (attr instanceof URL) {
                    URL u = (URL) attr;
                    int read = -1;
                    try {
                        read = u.openStream().read(new byte[4096]);
                    } catch (IOException ex) {
                        errors.append(ex.getMessage()).append('\n');
                    }
                    if (read <= 0) {
                        errors.append("Resource shall exist: " + fo + " attr: " + name + " value: " + attr + "\n");
                    }
                }
            }
            allTemplates.remove();
        }


        LocalFileSystem lfs = new LocalFileSystem();
        final File lfsRoot = new File(System.getProperty("fileCopies"));
        lfs.setRootDirectory(lfsRoot);
        System.setProperty("fileCopies", lfsRoot.getPath());

        int cnt = 0;
        Enumeration<? extends FileObject> en = lfs.getRoot().getChildren(true);
        while (en.hasMoreElements()) {
            FileObject fo = en.nextElement();
            if (!fo.isData()) {
                continue;
            }

            String exp = (String) fo.getAttribute("computedMimeType");
            assertNotNull("mimetype found for " + fo, exp);
            assertEquals("mimetype remains the same for " + fo, exp, fo.getMIMEType());
            cnt++;
        }
        if (cnt == 0) {
            fail("Expected some files for mimetype check");
        }

        Enumeration<? extends FileObject> en2 = FileUtil.getConfigFile("Services/MIMEResolver").getChildren(true);
        while (en2.hasMoreElements()) {
            FileObject fo = en2.nextElement();
            if (!fo.isData()) {
                continue;
            }
            if (fo.getAttribute("instanceCreate") instanceof MIMEResolver) {
                if (fo.getSize() != 0) {
                    errors.append("Should be empty ").append(fo).append(" size: ").append(fo.getSize()).append('\n');
                }
                continue;
            }

            int read = -1;
            InputStream is = null;
            try {
                is = fo.getInputStream();
                read = is.read(new byte[4096]);
            } catch (IOException ex) {
                errors.append(ex.getMessage()).append('\n');
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            if (read <= 0) {
                errors.append("Content shall exist: ").append(fo).append("\n");
            }
        }

        if (errors.length() > 0) {
            fail(errors.toString());
        }

        if (!filesAndAttribs.isEmpty()) {
            fail("All should be empty: " + filesAndAttribs);
        }
    }

    public void testTemplateHTMLDescriptions() throws Exception {
        FileObject orig = FileUtil.getConfigFile("Templates");

        Enumeration<? extends FileObject> allTemplates = orig.getChildren(true);
        StringBuilder errors = new StringBuilder();
        int checked = 0;

        while (allTemplates.hasMoreElements()) {
            FileObject fo = allTemplates.nextElement();
            if (fo.isFolder()) {
                continue;
            }

            Object attr = fo.getAttribute("instantiatingWizardURL");
            if (attr == null) {
                attr = fo.getAttribute("templateWizardURL");
            }
            if (attr == null) {
                continue;
            }
            String whoDefines = Arrays.toString((Object[])fo.getAttribute("layers"));
            if (!whoDefines.contains("org-netbeans-modules-ide-ergonomics.jar")) {
                continue;
            }
            checked++;

            URL u = (URL) attr;
            byte[] arr = new byte[1024 * 8];
            int len = 0;
            InputStream is = u.openStream();
            for (;;) {
                int r = is.read(arr, len, arr.length - len);
                if (r == -1) {
                    break;
                }
                len += r;
            }
            String s = new String(arr, 0, len);
            if (!s.contains("This feature is not yet enabled")) {
                errors.append("The HTML description shall contain ergonomics warning for " + fo + " at " + u + ":\n" + s + "\n");
            }
        }

        if (errors.length() > 0) {
            fail(errors.toString());
        }

        if (checked == 0) {
            fail("There shall be at least one file defined by ergonomics JAR, shall it not?");
        }
    }

    private static String getDisplayName(FileObject f) throws FileStateInvalidException {
        return f.getFileSystem().getStatus().annotateName(
            f.getNameExt(), Collections.<FileObject>singleton(f)
        );
    }
}
