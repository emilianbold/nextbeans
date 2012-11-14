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
package org.netbeans.modules.css.lib.api.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.modules.css.lib.CssTestBase;

/**
 *
 * @author marekfukala
 */
public class GrammarResolverListenerTest extends CssTestBase {

    public GrammarResolverListenerTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
//        PRINT_INFO_IN_ASSERT_RESOLVE = true;
//        GrammarResolver.setLogging(GrammarResolver.Log.DEFAULT, true);
    }

    public void testParseSimpleAmbiguousGrammar() {
        PropertyDefinition pm = Properties.getPropertyDefinition( "border-color");
        GrammarResolver gr = new GrammarResolver(pm.getGrammarElement(null));
        final Collection<String> resolvedTokens = new ArrayList<String>();
        final AtomicBoolean started = new AtomicBoolean(false);
        final AtomicBoolean finished = new AtomicBoolean(false);
        
        gr.addGrammarResolverListener(new GrammarResolverListener() {

            @Override
            public void entering(GroupGrammarElement group) {
            }

            @Override
            public void accepted(GroupGrammarElement group) {
            }

            @Override
            public void rejected(GroupGrammarElement group) {
            }

            @Override
            public void entering(ValueGrammarElement value) {
            }

            @Override
            public void accepted(ValueGrammarElement value, ResolvedToken resolvedToken) {
                resolvedTokens.add(resolvedToken.token().image().toString());
            }

            @Override
            public void rejected(ValueGrammarElement group) {
            }

            @Override
            public void ruleChoosen(GroupGrammarElement base, GrammarElement element) {
            }

            @Override
            public void starting() {
                started.set(true);
            }

            @Override
            public void finished() {
                finished.set(true);
            }
            
            
        });
        
        gr.resolve("green blue");
        
        assertEquals(2, resolvedTokens.size());
        Iterator<String> itr = resolvedTokens.iterator();
        assertEquals("green", itr.next());
        assertEquals("blue", itr.next());
        
        assertTrue(started.get());
        assertTrue(finished.get());
        
    
    }
    
    
    public void testFont2() {
        PropertyDefinition p = Properties.getPropertyDefinition( "font");
        ResolvedProperty pv = assertResolve(p.getGrammarElement(null), "20px / 20px fantasy");
        
        List<ResolvedToken> resolved = pv.getResolvedTokens();
        assertNotNull(resolved);
        assertEquals(4, resolved.size());
        
        Iterator<ResolvedToken> itr = resolved.iterator();
        assertEquals("[S0|font]/[S1]/[L2]/[S7|font-size]/[L10|@length]/!length (20px(LENGTH;0-4))", itr.next().toString());
        assertEquals("[S0|font]/[S1]/[L2]/[L14]// (/(SOLIDUS;5-6))", itr.next().toString());
        assertEquals("[S0|font]/[S1]/[L2]/[L14]/[S15|line-height]/[L16|@length]/!length (20px(LENGTH;7-11))", itr.next().toString());
        assertEquals("[S0|font]/[S1]/[L2]/[S20|font-family]/[L21]/[S22]/[S25|@generic-family]/fantasy (fantasy(IDENT;12-19))", itr.next().toString());
        
    }

    public void testZeroMultiplicity() {
        String rule = "[marek]?  [jitka]?  [ovecka]";
        String text = "ovecka";
        ResolvedProperty csspv = new ResolvedProperty(rule, text);
        assertTrue(csspv.isResolved());
    }

    public void testFontFamily() {
        PropertyDefinition p = Properties.getPropertyDefinition( "font-family");

        assertTrue(new ResolvedProperty(p, "serif").isResolved());
        assertTrue(new ResolvedProperty(p, "cursive, serif").isResolved());
        
        //resolves since the "cursive serif" can be considered as unquoted custom family name
        assertTrue(new ResolvedProperty(p, "cursive serif").isResolved());

    }

    public void testFontFamilyWithQuotedValue() {
        PropertyDefinition p = Properties.getPropertyDefinition( "font-family");
        ResolvedProperty csspv = new ResolvedProperty(p, "'Times New Roman',serif");
//        dumpResult(csspv);
        assertTrue(csspv.isResolved());
    }

    public void testFontSize() {
        PropertyDefinition p = Properties.getPropertyDefinition( "font-size");
        String text = "xx-small";

        ResolvedProperty csspv = new ResolvedProperty(p, text);

        assertTrue(csspv.isResolved());
    }

    public void testBorder() {
        PropertyDefinition p = Properties.getPropertyDefinition( "border");
        String text = "20px double";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testMarginWidth() {
        PropertyDefinition p = Properties.getPropertyDefinition( "margin");
        String text = "20px 10em 30px 30em";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testPaddingWidth() {
        PropertyDefinition p = Properties.getPropertyDefinition( "padding");
        String text = "20px 10em 30px 30em";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testTimeUnit() {
        PropertyDefinition p = Properties.getPropertyDefinition( "pause-after");
        assertNotNull(p);
        String text = "200ms";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());

        text = "200";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

        text = "AAms";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

    }

    public void testFrequencyUnit() {
        PropertyDefinition p = Properties.getPropertyDefinition( "pitch");
        String text = "200kHz";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());

        text = "200";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

        text = "AAHz";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

    }

    public void testIdentifierUnit() {
        PropertyDefinition p = Properties.getPropertyDefinition( "counter-increment");
        String text = "ovecka";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());

        text = "10ovecek";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

        text = "-beranek";
        csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());

    }

    public void testBackgroundImageURL() {
        PropertyDefinition p = Properties.getPropertyDefinition( "background-image");
        String text = "url('/images/v6/tabs-bg.png')";
        ResolvedProperty csspv = new ResolvedProperty(p, text);

//        dumpResult(csspv);

        assertTrue(csspv.isResolved());

        text = "url'/images/v6/tabs-bg.png')";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

        text = "ury('/images/v6/tabs-bg.png')";
        csspv = new ResolvedProperty(p, text);
        assertFalse(csspv.isResolved());

    }

    public void testAbsoluteLengthUnits() {
        PropertyDefinition p = Properties.getPropertyDefinition( "font");
        String text = "12px/14cm sans-serif";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testUnquotedURL() {
        PropertyDefinition p = Properties.getPropertyDefinition( "@uri");
        String text = "url(http://www.redballs.com/redball.png)";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testBackroundImage() {
        PropertyDefinition p = Properties.getPropertyDefinition( "background-image");
        assertNotResolve(p.getGrammar(), "");
    }

    public void testBackroundPositionOrder() {
        // TODO: fix #142254 and enable this test again
        PropertyDefinition p = Properties.getPropertyDefinition( "@bg-pos");
        assertResolve(p.getGrammar(), "center top");
    }

    public void testBorderColor() {
        PropertyDefinition p = Properties.getPropertyDefinition( "border-color");
        assertResolve(p.getGrammar(), "red yellow black yellow");
        assertResolve(p.getGrammar(), "red yellow black");
        assertResolve(p.getGrammar(), "red yellow");
        assertResolve(p.getGrammar(), "red");

        assertNotResolve(p.getGrammar(), "xxx");
        assertNotResolve(p.getGrammar(), "");
    }

    public void testIssue185995() {
        PropertyDefinition p = Properties.getPropertyDefinition( "border-color");
        assertResolve(p.getGrammar(), "transparent transparent");
    }

    public void testBorder_Top_Style() {
        PropertyDefinition p = Properties.getPropertyDefinition( "border-top-style");
        assertNotNull(p);
        
        ResolvedProperty csspv = new ResolvedProperty(p, "dotted dotted dashed dashed");
        assertFalse(csspv.isResolved());

        csspv = new ResolvedProperty(p, "dotted");
        assertTrue(csspv.isResolved());

    }

    public void testCaseSensitivity() {
        PropertyDefinition p = Properties.getPropertyDefinition( "azimuth");
        String text = "behind";
        ResolvedProperty csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());

        text = "BEHIND";
        csspv = new ResolvedProperty(p, text);
        assertTrue(csspv.isResolved());

    }

    public void testJindrasCase() {
        String g = "[ [ x || y ] || b";

        assertResolve(g, "x");
        assertResolve(g, "x y");
        assertResolve(g, "y");
        assertResolve(g, "y x");
        assertResolve(g, "y x b");
        assertResolve(g, "x b");
        assertResolve(g, "y b");
        assertResolve(g, "y x b");

        assertNotResolve(g, "x b y");
    }
    
    public void testContentFailure() {
        String g = "[ uri , ]* normal";
        
        assertResolve(g, "normal");
        assertResolve(g, "uri, normal");
        assertResolve(g, "uri, uri, normal");
    }
    
    public void testCase4() {
         String grammar2 = "[ a b ] | [ a c ]";
         assertNotResolve(grammar2, "a");
    }
    
    public void testMultiplicityOfList() {
        String g = " [ a || b ]*";
        
        assertResolve(g, "a");
        assertResolve(g, "a b");
        assertResolve(g, "a b a");
    }
    
    public void testAllGroup() {
        String g = "a && b";
        
        assertResolve(g, "a b");
        assertResolve(g, "b a");
        
        assertNotResolve(g, "");
        assertNotResolve(g, "a");
        assertNotResolve(g, "b");
        assertNotResolve(g, "b b");
        assertNotResolve(g, "a a");
        assertNotResolve(g, "x");
        assertNotResolve(g, "a x");
        assertNotResolve(g, "x x b");
    }
    
    public void testAllGroupComplex() {
        String g = "a [ a && b ] b";        
        assertResolve(g, "a a b b");
        assertResolve(g, "a b a b");        
    }

    public void testAllGroupComplex2() {
        String g = "a [ a && b ]? b";        
        assertResolve(g, "a a b b");
        assertResolve(g, "a b a b");        
        assertResolve(g, "a b");        
        
        assertNotResolve(g, "a a b");        
        assertNotResolve(g, "a b b");        
    }
    
    public void testAllGroupComplex3() {
        String g = "[ a && b ]*";
        assertResolve(g, "");
        assertResolve(g, "a b a b a b");
        assertResolve(g, "b a a b");        
        assertResolve(g, "b a");        
        
        assertNotResolve(g, "a b b");        
        assertNotResolve(g, "b a a b a");        
    }
 
    public void testAllGroupComplex4() {
        String g = "a && c?";
        assertResolve(g, "a");
        assertResolve(g, "a c");
        assertResolve(g, "c a");
    }
    
    public void testBackground() {
        PropertyDefinition pm = Properties.getPropertyDefinition( "background");
        assertResolve(pm.getGrammarElement(null), "url(images/shadow.gif) no-repeat bottom right");
    }
    
    
    //Bug 206035 - Incorrect background property value validation/completion
    public void testBackground2() {
        PropertyDefinition pm = Properties.getPropertyDefinition( "background");
        assertResolve(pm.getGrammarElement(null), "#fff url(\"../images/google\") no-repeat center left");
    }

    
//    //should be already fixed in easel (the grammar resolver uses antlr tokens)
//    public void testURI() {
//        PRINT_INFO_IN_ASSERT_RESOLVE = true;
//        GrammarResolver.setLogging(GrammarResolver.Log.DEFAULT, true);
//        
//        PropertyDefinition pm = CssModuleSupport.getPropertyDefinition("@uri");
//        assertResolve(pm.getGrammarElement(null), "url(images/google)");
//        assertResolve(pm.getGrammarElement(null), "url(../images/google)");        
//    }
//    
    
    public void testBgPosition() {
        PropertyDefinition pm = Properties.getPropertyDefinition( "@bg-pos");
        assertResolve(pm.getGrammarElement(null), "center left");
    }
    
    public void testBgPositionDetail_And() {
        //the minimized grammar to reproduce the bg-position resolving problem
        String grammar = "[ center | a ] && [ center | b ]";
        
        assertResolve(grammar, "center b");
        assertResolve(grammar, "b center");
        assertResolve(grammar, "a b");
        assertResolve(grammar, "b a");
        assertResolve(grammar, "center center");        
        assertResolve(grammar, "a center");
        
        assertResolve(grammar, "center a"); //this used to fail
        
    }
    
    public void testBgPositionDetail_Collection() {
        //the minimized grammar to reproduce the bg-position resolving problem
        String grammar = "[ center | a ] || [ center | b ]";
        
        assertResolve(grammar, "center");
        assertResolve(grammar, "b");
        assertResolve(grammar, "a");
        assertResolve(grammar, "b");
        
        assertResolve(grammar, "center b");
        assertResolve(grammar, "b center");
        assertResolve(grammar, "a b");
        assertResolve(grammar, "b a");
        assertResolve(grammar, "center center");        
        assertResolve(grammar, "a center");
        
        assertResolve(grammar, "center a"); //this used to fail
        
    }
    

}
