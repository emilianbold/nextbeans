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
 * "License")
 . You may not use this file except in compliance with the
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javadoc.hints;

import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.hints.test.api.HintTest;
import static org.netbeans.modules.javadoc.hints.JavadocHint.AVAILABILITY_KEY;
import static org.netbeans.modules.javadoc.hints.JavadocHint.SCOPE_KEY;

/**
 *
 * @author Jan Pokorsky
 * @author Ralph Benjamin Ruijs
 */
public class AddTagFixTest extends NbTestCase {

    public AddTagFixTest(String name) {
        super(name);
    }

    public void testAddReturnTagFixInEmptyJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:4-5:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     * @return \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddReturnTagFix() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * bla\n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:4-5:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * bla\n"
                + "     * @return \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddReturnTagFix2() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /** bla\n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("4:4-4:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /** bla\n"
                + "     * @return \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddReturnTagFixInEmpty1LineJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /***/\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("3:4-3:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @return \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddReturnTagFixIn1LineJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /** bla */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("3:4-3:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /** bla\n"
                + "     * @return \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddReturnTagFixIn1LineJavadoc2() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /** @since 1.1 */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("3:4-3:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @return \n"
                + "     * @since 1.1 */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddReturnTagFixIn1LineJavadoc3() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /** bla {@link nekam} */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("3:4-3:7:warning:Missing @return tag.")
                .applyFix("Add @return tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /** bla {@link nekam}\n"
                + "     * @return \n"
                + "     */\n"
                + "    int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddParamTagFixInEmptyJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     */\n"
                + "    void leden(int prvniho) {\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:15-5:26:warning:Missing @param tag for prvniho")
                .applyFix("Add @param prvniho tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     * @param prvniho \n"
                + "     */\n"
                + "    void leden(int prvniho) {\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddParamTagFixWithReturn() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @return bla\n"
                + "     */\n"
                + "    int leden(int prvniho) {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:14-5:25:warning:Missing @param tag for prvniho")
                .applyFix("Add @param prvniho tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param prvniho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    int leden(int prvniho) {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddParamTagFixWithReturn_115974() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @return bla */\n"
                + "    int leden(int prvniho) {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("4:14-4:25:warning:Missing @param tag for prvniho")
                .applyFix("Add @param prvniho tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param prvniho \n"
                + "     * @return bla */\n"
                + "    int leden(int prvniho) {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddParamTagFixAndParamOrder() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param prvniho \n"
                + "     * @param tretiho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    int leden(int prvniho, int druheho, int tretiho) {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("7:27-7:38:warning:Missing @param tag for druheho")
                .applyFix("Add @param druheho tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param prvniho \n"
                + "     * @param druheho \n"
                + "     * @param tretiho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    int leden(int prvniho, int druheho, int tretiho) {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddTypeParamTagFixInEmptyJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     */\n"
                + "    <T> void leden() {\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:5-5:6:warning:Missing @param tag for <T>")
                .applyFix("Add @param <T> tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     * @param <T> \n"
                + "     */\n"
                + "    <T> void leden() {\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddTypeParamTagFixInEmptyClassJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "/**\n"
                + " * \n"
                + " */\n"
                + "class Zima<T> {\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("4:11-4:12:warning:Missing @param tag for <T>")
                .applyFix("Add @param <T> tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "/**\n"
                + " * \n"
                + " * @param <T> \n"
                + " */\n"
                + "class Zima<T> {\n"
                + "}\n");
    }

    public void testAddTypeParamTagFixInClassJavadoc() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "/**\n"
                + " * @param <Q> \n"
                + " */\n"
                + "class Zima<P,Q> {\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("4:11-4:12:warning:Missing @param tag for <P>")
                .applyFix("Add @param <P> tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "/**\n"
                + " * @param <P> \n"
                + " * @param <Q> \n"
                + " */\n"
                + "class Zima<P,Q> {\n"
                + "}\n");
    }

    public void testAddTypeParamTagFixWithReturn() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @return bla\n"
                + "     */\n"
                + "    <T> int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:5-5:6:warning:Missing @param tag for <T>")
                .applyFix("Add @param <T> tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param <T> \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    <T> int leden() {\n"
                + "        return 0;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddTypeParamTagFixAndParamOrder() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param prvniho \n"
                + "     * @param druheho \n"
                + "     * @param tretiho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    <T> T leden(int prvniho, int druheho, T tretiho) {\n"
                + "        return tretiho;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("8:5-8:6:warning:Missing @param tag for <T>")
                .applyFix("Add @param <T> tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param <T> \n"
                + "     * @param prvniho \n"
                + "     * @param druheho \n"
                + "     * @param tretiho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    <T> T leden(int prvniho, int druheho, T tretiho) {\n"
                + "        return tretiho;\n"
                + "    }\n"
                + "}\n");
    }
    
    public void testAddTypeParamTagFixClashAndParamOrder() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param <T> \n"
                + "     * @param prvniho \n"
                + "     * @param druheho \n"
                + "     * @param tretiho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    <T,S> T leden(int prvniho, int druheho, T tretiho) {\n"
                + "        return tretiho;\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("9:7-9:8:warning:Missing @param tag for <S>")
                .applyFix("Add @param <S> tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * @param <T> \n"
                + "     * @param <S> \n"
                + "     * @param prvniho \n"
                + "     * @param druheho \n"
                + "     * @param tretiho \n"
                + "     * @return bla\n"
                + "     */\n"
                + "    <T,S> T leden(int prvniho, int druheho, T tretiho) {\n"
                + "        return tretiho;\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddThrowsTagFix() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     */\n"
                + "    void leden() throws java.io.IOException {\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:24-5:43:warning:Missing @throws tag for java.io.IOException")
                .applyFix("Add @throws java.io.IOException tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     * @throws java.io.IOException \n"
                + "     */\n"
                + "    void leden() throws java.io.IOException {\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddThrowsTagFix2() throws Exception {
        HintTest.create()
                .input(
                "package test;\n"
                + "import java.io.IOException;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     */\n"
                + "    void leden() throws IOException {\n"
                + "    }\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("6:24-6:35:warning:Missing @throws tag for java.io.IOException")
                .applyFix("Add @throws java.io.IOException tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "import java.io.IOException;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     * @throws java.io.IOException \n"
                + "     */\n"
                + "    void leden() throws IOException {\n"
                + "    }\n"
                + "}\n");
    }

    public void testAddThrowsTagFix_NestedClass_160414() throws Exception {
        // issue 160414
        HintTest.create()
                .input(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     */\n"
                + "    void leden() throws MEx {\n"
                + "    }\n"
                + "    public static class MEx extends Exception {}\n"
                + "}\n")
                .preference(AVAILABILITY_KEY + true, true)
                .preference(SCOPE_KEY, "private")
                .run(JavadocHint.class)
                .findWarning("5:24-5:27:warning:Missing @throws tag for test.Zima.MEx")
                .applyFix("Add @throws test.Zima.MEx tag")
                .assertCompilable()
                .assertOutput(
                "package test;\n"
                + "class Zima {\n"
                + "    /**\n"
                + "     * \n"
                + "     * @throws test.Zima.MEx \n"
                + "     */\n"
                + "    void leden() throws MEx {\n"
                + "    }\n"
                + "    public static class MEx extends Exception {}\n"
                + "}\n");
    }
}
