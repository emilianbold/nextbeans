/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.css.lib.api.CssParserFactory;
import org.netbeans.modules.css.lib.api.CssParserResult;
import org.netbeans.modules.css.lib.api.CssTokenId;
import org.netbeans.modules.css.lib.api.NodeUtil;
import org.netbeans.modules.css.lib.api.ProblemDescription;
import org.netbeans.modules.css.lib.nbparser.CssParser;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author marekfukala
 */
public class TestUtil {
    
    public static final String bodysetPath = "styleSheet/body/bodyItem/";

    public static CssParserResult parse(String code) {
        try {
            Document doc = new PlainDocument();
            doc.putProperty("mimeType", "text/css");
            doc.insertString(0, code, null);
            Source source = Source.create(doc);
            return parse(source);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public static CssParserResult parse(FileObject file) throws ParseException, BadLocationException, IOException {
        //no loader here so we need to create the swing document
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        StringBuilder builder = new StringBuilder();

        char[] buffer = new char[8096];
        int read;
        while ((read = reader.read(buffer)) > 0) {
            builder.append(buffer, 0, read);
        }
        reader.close();
        return parse(builder.toString());
    }

    public static CssParserResult parse(Source source) throws ParseException, org.netbeans.modules.parsing.spi.ParseException {
//        final AtomicReference<CssParserResult> resultRef = new AtomicReference<CssParserResult>();
//        ParserManager.parse(Collections.singleton(source), new UserTask() {
//
//            @Override
//            public void run(ResultIterator resultIterator) throws Exception {
//                CssParserResult result = (CssParserResult) resultIterator.getParserResult();
//                resultRef.set(result);
//            }
//        });
//        
//        return resultRef.get();

        //the CssParserFactory is not registered in the system fs by default. 
        //If it was the above is the clean way how to get the CssParserResult
        CssParser parser = (CssParser) CssParserFactory.getDefault().createParser(null);
        parser.parse(source.createSnapshot(), null, null);
        return (CssParserResult) parser.getResult(null);

    }

    public static void dumpResult(CssParserResult result) {
        System.out.println("Parse Tree:");
        NodeUtil.dumpTree(result.getParseTree());
        Collection<ProblemDescription> problems = result.getDiagnostics();
        if (!problems.isEmpty()) {
            System.out.println(String.format("Found %s problems while parsing:", problems.size()));
            for (ProblemDescription pp : problems) {
                System.out.println(pp);
            }
        }

    }
    
    public static void dumpTokens(CssParserResult result) {
        System.out.println("Tokens:");
        TokenSequence<CssTokenId> ts = result.getSnapshot().getTokenHierarchy().tokenSequence(CssTokenId.language());
        while (ts.moveNext()) {
            System.out.println(ts.offset() + "-" + (ts.token().length() + ts.offset()) + ": " + ts.token().text() + "(" + ts.token().id() + ")");
        }
        System.out.println("-------------");
    }
    
    public static void dumpTokens(Css3Lexer lexer) {
        System.out.println("Tokens:");
        CommonToken t;
        while ((t = (CommonToken)lexer.nextToken()) != null) {
            System.out.println(
                    t.getStartIndex() + "-" + t.getStopIndex() 
                    + ": " + t.getText() + "(" + (t.getType() == -1 ? "" : Css3Parser.tokenNames[t.getType()]) + ")");
            
            if(t.getType() == Css3Lexer.EOF) {
                break;
            }
        }
        System.out.println("-------------");
    }
}
