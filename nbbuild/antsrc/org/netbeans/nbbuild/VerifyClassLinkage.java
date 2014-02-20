/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.nbbuild;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Verifies linkage between classes in a JAR (typically a module).
 * @author Jesse Glick
 * @see "#71675"
 * @see <a href="http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html">Class file spec</a>
 */

//-------------------------
//jglick: when considering rewrites please check https://github.com/jenkinsci/constant-pool-scanner 
//-------------------------
public class VerifyClassLinkage extends Task {

    public VerifyClassLinkage() {}

    /*
    private boolean verifyMainJar = true;
    private boolean verifyClassPathExtensions = true;
    public void setVerifyClassPathExtensions(boolean verifyClassPathExtensions) {
        this.verifyClassPathExtensions = verifyClassPathExtensions;
    }
    public void setVerifyMainJar(boolean verifyMainJar) {
        this.verifyMainJar = verifyMainJar;
    }
     */

    private File jar;
    private boolean failOnError = true;
    private boolean warnOnDefaultPackage = true;
    private Path classpath = new Path(getProject());
    private String ignores;
    private int maxWarnings = Integer.MAX_VALUE;

    /**
     * Intended static classpath for this JAR.
     * Any classes loaded in this JAR (and its Class-Path extensions)
     * must be linkable against this classpath plus the JAR (and extensions) itself.
     */
    public Path createClasspath() {
        return classpath.createPath();
    }

    /**
     * Specify the main JAR file.
     * Automatically searches in Class-Path extensions too.
     */
    public void setJar(File jar) {
        this.jar = jar;
    }

    /**
     * If true (default), halt build on error, rather than just
     * reporting a warning.
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * Sets the pattern for classes that are not verified.
     * Allows to skip linkage verification of some classes.
     */
    public void setIgnores(String ignores) {
        this.ignores = ignores;
    }

    /**
     * If true (default), warn if any classes are found in the default
     * package. Never halts the build even if {@link #setFailOnError} true.
     */
    public void setWarnOnDefaultPackage(boolean warnOnDefaultPackage) {
        this.warnOnDefaultPackage = warnOnDefaultPackage;
    }

    /**
     * Limit the number of warnings that will be generated in one task run.
     * If there are more warnings than this, they will not be reported.
     */
    public void setMaxWarnings(int maxWarnings) {
        if (maxWarnings <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxWarnings = maxWarnings;
    }

    public @Override void execute() throws BuildException {
        if (jar == null) {
            throw new BuildException("Must specify a JAR file", getLocation());
        }
        try {
            // Map from class name (foo/Bar format) to true (found), false (not found), null (as yet unknown):
            Map<String,Boolean> loadable = new HashMap<String,Boolean>();
            Map<String,byte[]> classfiles = new TreeMap<String,byte[]>();
            JarFile jf = new JarFile(jar);
            try {
                read(jf, classfiles, new HashSet<File>(Collections.singleton(jar)), this, ignores);
            } finally {
                jf.close();
            }
            for (String clazz: classfiles.keySet()) {
                // All classes we define are obviously loadable:
                loadable.put(clazz, Boolean.TRUE);
                if (warnOnDefaultPackage && clazz.indexOf('.') == -1) {
                    log("Warning: class '" + clazz + "' found in default package", Project.MSG_WARN);
                }
            }
            // XXX should use a load-nothing parent and require nbjdk.bootclasspath to be added explicitly to CP
            // otherwise e.g. libs.jsr223 dep can be removed from contrib/java.hints.scripting without warning if building on JDK 6
            ClassLoader loader = new AntClassLoader(ClassLoader.getSystemClassLoader().getParent(), getProject(), classpath, true);
            AtomicInteger max = new AtomicInteger(maxWarnings);
            for (Map.Entry<String, byte[]> entry: classfiles.entrySet()) {
                String clazz = entry.getKey();
                byte[] data = entry.getValue();
                verify(clazz, data, loadable, loader, max);
                if (max.get() < 0) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new BuildException("While verifying " + jar + " or its Class-Path extensions: " + e, e, getLocation());
        }
    }

    static void read(JarFile jf, Map<String, byte[]> classfiles, Set<File> alreadyRead, Task task, String ignores) throws IOException {
        File jar = new File(jf.getName());
        task.log("Reading " + jar, Project.MSG_VERBOSE);
        Pattern p = (ignores != null)? Pattern.compile(ignores): null;
            Enumeration<JarEntry> e = jf.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }
                String clazz = name.substring(0, name.length() - 6).replace('/', '.');
                if (p != null && p.matcher(clazz).matches()) {
                    continue;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max((int) entry.getSize(), 0));
                InputStream is = jf.getInputStream(entry);
                try {
                    byte[] buf = new byte[4096];
                    int read;
                    while ((read = is.read(buf)) != -1) {
                        baos.write(buf, 0, read);
                    }
                } finally {
                    is.close();
                }
                classfiles.put(clazz, baos.toByteArray());
            }
            Manifest mf = jf.getManifest();
            if (mf != null) {
                String cp = mf.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
                if (cp != null) {
                    String[] uris = cp.trim().split("[, ]+");
                    for (int i = 0; i < uris.length; i++) {
                        String path = uris[i];
                        if (path.equals("${java.home}/lib/ext/jfxrt.jar")) { 
                            String jhm = System.getProperty("java.home");
                            File classpathFile = new File(new File(new File(new File(jhm), "lib"), "ext"), "jfxrt.jar");
                            if (!classpathFile.exists()) {
                                File jdk7 = new File(new File(new File(jhm), "lib"), "jfxrt.jar");
                                if (jdk7.exists()) {
                                    classpathFile = jdk7;
                                }
                            }
                            if (!classpathFile.isFile()) {
                                task.log( "Could not resolve Class-Path item in manifest, path is:" + path +  ", skipping", Project.MSG_WARN);
                                continue; //try to guard against future failures
                            } else {
                                path = classpathFile.getAbsolutePath();
                            }
                        }
                        File otherJar = new File(jar.toURI().resolve(path));
                        if (alreadyRead.add(otherJar)) {
                            if (otherJar.isFile()) {
                                JarFile otherJF = new JarFile(otherJar);
                                try {
                                    read(otherJF, classfiles, alreadyRead, task, ignores);
                                } finally {
                                    otherJF.close();
                                }
                            }
                        } else {
                            task.log("Already read " + jar, Project.MSG_VERBOSE);
                        }
                    }
                }
            }
    }

    private void verify(String clazz, byte[] data, Map<String,Boolean> loadable, ClassLoader loader, AtomicInteger maxWarn)
            throws IOException, BuildException {
        //log("Verifying linkage of " + clazz.replace('/', '.'), Project.MSG_DEBUG);
        Set<String> dependencies = dependencies(data);
        //System.err.println(clazz + " -> " + dependencies);
        for (String clazz2 : dependencies) {
            Boolean exists = loadable.get(clazz2);
            if (exists == null) {
                exists = loader.getResource(clazz2.replace('.', '/') + ".class") != null;
                loadable.put(clazz2, exists);
            }
            if (!exists) {
                String message = clazz + " cannot access " + clazz2;
                if (failOnError) {
                    throw new BuildException(message, getLocation());
                } else if (maxWarn.getAndDecrement() > 0) {
                    log("Warning: " + message, Project.MSG_WARN);
                } else {
                    log("(additional warnings not reported)", Project.MSG_WARN);
                    return;
                }
            } else {
                //log("Working reference to " + clazz2, Project.MSG_DEBUG);
            }
        }
    }
    
    private static void skip(DataInput input, int bytes) throws IOException {
        int skipped = input.skipBytes(bytes);
        if (skipped != bytes) {
            throw new IOException("Truncated class file");
        }
    }
    static Set<String> dependencies(byte[] data) throws IOException {
        Set<String> result = new TreeSet<String>();
        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        skip(input, 8); // magic, minor_version, major_version
        int size = input.readUnsignedShort() - 1; // constantPoolCount
        String[] utf8Strings = new String[size];
        boolean[] isClassName = new boolean[size];
        boolean[] isDescriptor = new boolean[size];
        for (int i = 0; i < size; i++) {
            byte tag = input.readByte();
            switch (tag) {
                case 1: // CONSTANT_Utf8
                    utf8Strings[i] = input.readUTF();
                    break;
                case 7: // CONSTANT_Class
                    int index = input.readUnsignedShort() - 1;
                    if (index >= size) {
                        throw new IOException("@" + i + ": CONSTANT_Class_info.name_index " + index + " too big for size of pool " + size);
                    }
                    //log("Class reference at " + index, Project.MSG_DEBUG);
                    isClassName[index] = true;
                    break;
                case 3: // CONSTANT_Integer
                case 4: // CONSTANT_Float
                case 9: // CONSTANT_Fieldref
                case 10: // CONSTANT_Methodref
                case 11: // CONSTANT_InterfaceMethodref
                    skip(input, 4);
                    break;
                case 12: // CONSTANT_NameAndType
                    skip(input, 2);
                    index = input.readUnsignedShort() - 1;
                    if (index >= size || index < 0) {
                        throw new IOException("@" + i + ": CONSTANT_NameAndType_info.descriptor_index " + index + " too big for size of pool " + size);
                    }
                    isDescriptor[index] = true;
                    break;
                case 8: // CONSTANT_String
                    skip(input, 2);
                    break;
                case 5: // CONSTANT_Long
                case 6: // CONSTANT_Double
                    skip(input, 8);
                    i++; // weirdness in spec
                    break;
                default:
                    throw new IOException("Unrecognized constant pool tag " + tag + " at index " + i +
                            "; running UTF-8 strings: " + Arrays.asList(utf8Strings));
            }
        }
        //task.log("UTF-8 strings: " + Arrays.asList(utf8Strings), Project.MSG_DEBUG);
        for (int i = 0; i < size; i++) {
            String s = utf8Strings[i];
            if (isClassName[i]) {
                while (s.charAt(0) == '[') {
                    // array type
                    s = s.substring(1);
                }
                if (s.length() == 1) {
                    // primitive
                    continue;
                }
                String c;
                if (s.charAt(s.length() - 1) == ';' && s.charAt(0) == 'L') {
                    // Uncommon but seems sometimes this happens.
                    c = s.substring(1, s.length() - 1);
                } else {
                    c = s;
                }
                result.add(c.replace('/', '.'));
            } else if (isDescriptor[i]) {
                int idx = 0;
                while ((idx = s.indexOf('L', idx)) != -1) {
                    int semi = s.indexOf(';', idx);
                    if (semi == -1) {
                        throw new IOException("Invalid type or descriptor: " + s);
                    }
                    result.add(s.substring(idx + 1, semi).replace('/', '.'));
                    idx = semi;
                }
            }
        }
        return result;
    }

}
