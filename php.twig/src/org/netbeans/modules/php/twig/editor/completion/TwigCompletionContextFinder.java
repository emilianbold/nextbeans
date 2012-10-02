/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.twig.editor.completion;

import java.util.Arrays;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.twig.editor.lexer.TwigLexerUtils;
import org.netbeans.modules.php.twig.editor.lexer.TwigTokenId;
import org.netbeans.modules.php.twig.editor.parsing.TwigParserResult;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class TwigCompletionContextFinder {

    public static enum CompletionContext {
        VARIABLE,
        BLOCK,
        FILTER,
        NONE,
        ALL;
    }

    private static final List<Object[]> FILTER_TOKEN_CHAINS = Arrays.asList(
            new Object[]{TwigTokenId.T_TWIG_PUNCTUATION},
            new Object[]{TwigTokenId.T_TWIG_PUNCTUATION, TwigTokenId.T_TWIG_NAME}
    );

    public static CompletionContext find(final TwigParserResult parserResult, final int offset) {
        assert parserResult != null;
        CompletionContext result = CompletionContext.NONE;
        TokenSequence<? extends TwigTokenId> tokenSequence = TwigLexerUtils.getTwigMarkupTokenSequence(parserResult.getSnapshot(), offset);
        if (tokenSequence != null) {
            tokenSequence.move(offset);
            if (canComplete(tokenSequence, offset)) {
                result = findContext(tokenSequence);
            }
        }
        return result;
    }

    private static boolean canComplete(final TokenSequence<? extends TwigTokenId> tokenSequence, final int caretOffset) {
        boolean result = true;
        if (tokenSequence.moveNext()) {
            result = tokenSequence.token() != null
                && (!TwigLexerUtils.isDelimiter(tokenSequence.token().id()) || tokenSequence.offset() == caretOffset);
        } else {
            if (tokenSequence.movePrevious()) {
                Token<? extends TwigTokenId> token = tokenSequence.token();
                if (token != null && TwigLexerUtils.isEndingDelimiter(token.id())) {
                    result = false;
                } else {
                    result = true;
                }
                tokenSequence.moveNext();
            }
        }
        return result;
    }

    private static CompletionContext findContext(TokenSequence<? extends TwigTokenId> tokenSequence) {
        CompletionContext result = CompletionContext.ALL;
        do {
            Token<? extends TwigTokenId> token = tokenSequence.token();
            if (token == null) {
                break;
            }
            TwigTokenId tokenId = token.id();
            if (acceptTokenChains(tokenSequence, FILTER_TOKEN_CHAINS, true)) {
                result = CompletionContext.FILTER;
                break;
            } else if (TwigTokenId.T_TWIG_BLOCK_START.equals(tokenId) || TwigTokenId.T_TWIG_BLOCK_END.equals(tokenId)) {
                result = CompletionContext.BLOCK;
                break;
            } else if (TwigTokenId.T_TWIG_VAR_START.equals(tokenId) || TwigTokenId.T_TWIG_VAR_END.equals(tokenId)) {
                result = CompletionContext.VARIABLE;
                break;
            }
        } while (tokenSequence.movePrevious());
        return result;
    }

    private static boolean acceptTokenChains(final TokenSequence tokenSequence, final List<Object[]> tokenIdChains, final boolean movePrevious) {
        boolean result = false;
        for (Object[] tokenIDChain : tokenIdChains) {
            if (acceptTokenChain(tokenSequence, tokenIDChain, movePrevious)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean acceptTokenChain(final TokenSequence tokenSequence, final Object[] tokenIdChain, final boolean movePrevious) {
        int originalPosition = tokenSequence.offset();
        boolean accept = true;
        boolean moreTokens = movePrevious ? tokenSequence.movePrevious() : true;
        for (int i = tokenIdChain.length - 1; i >= 0; i--) {
            Object tokenId = tokenIdChain[i];
            if (!moreTokens) {
                accept = false;
                break;
            }
            if (tokenId instanceof TwigTokenId) {
                if (tokenSequence.token().id() == tokenId) {
                    moreTokens = tokenSequence.movePrevious();
                } else {
                    accept = false;
                    break;
                }
            } else {
                assert false : "Unsupported token type: " + tokenId.getClass().getName();
            }
        }
        tokenSequence.move(originalPosition);
        tokenSequence.moveNext();
        return accept;
    }

}
