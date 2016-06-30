/* The following code was generated by JFlex 1.4.1 on 20.3.08 11:54 */

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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

/* Preprocessor Scanner
 *
 * !!! do not modify PreprocessorScanner.java !!! primary source is PreprocessorScanner.jflex !!!
 *
 * @author Adam Sotona
 *
 */

package org.netbeans.mobility.antext.preprocessor;

import java.io.*;


/**
 * This class is a scanner generated by
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1
 * on 20.3.08 11:54 from the specification file
 * <tt>PreprocessorScanner.jflex</tt>
 */
public final class PreprocessorScanner implements LineParserTokens {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int OLDCOMMAND = 2;
  public static final int COMMAND = 1;
  public static final int YYINITIAL = 0;
  public static final int OTHERTEXT = 3;

  /**
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED =
    "\11\0\1\3\1\1\1\0\1\3\1\2\22\0\1\3\1\37\1\5"+
    "\1\7\1\27\1\0\1\33\1\0\1\31\1\32\1\26\1\0\1\43"+
    "\1\30\1\46\1\6\1\50\11\47\1\44\1\0\1\41\1\40\1\42"+
    "\1\0\1\36\6\52\21\45\1\51\2\45\1\0\1\4\1\0\1\35"+
    "\1\45\1\0\1\52\1\22\1\12\1\15\1\17\1\11\1\24\1\45"+
    "\1\10\2\45\1\20\1\25\1\14\1\13\3\45\1\21\1\16\1\23"+
    "\2\45\1\51\2\45\1\0\1\34\uff83\0";

  /**
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\1\2\2\3\1\4\1\2\2\5\1\6"+
    "\1\2\2\7\1\2\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\1\16\1\17\1\20\1\21\1\22\1\2\2\23"+
    "\1\2\1\7\1\2\1\16\1\1\2\0\1\6\1\24"+
    "\1\25\1\7\1\12\1\13\1\26\1\27\1\30\1\31"+
    "\4\0\1\32\1\0\1\33\1\34\1\24\1\7\1\0"+
    "\1\23\1\35\1\36\1\37\1\32\6\37\1\32\1\7"+
    "\1\0\1\40\6\37\1\0\1\7\1\0\12\37\1\0"+
    "\1\7\1\0\7\37\1\41\1\42\2\37\1\43\1\0"+
    "\1\37\1\44\2\37\1\45\1\46\5\37\1\47\1\50"+
    "\1\37\1\51\4\37\1\52\3\37\1\53\2\37\1\54"+
    "\1\55\1\56\1\57";

  private static int [] zzUnpackAction() {
    int [] result = new int[138];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\53\0\126\0\201\0\254\0\254\0\327\0\u0102"+
    "\0\u012d\0\254\0\u0158\0\u0183\0\u01ae\0\u01d9\0\u0204\0\u022f"+
    "\0\254\0\254\0\u025a\0\u0285\0\254\0\254\0\u02b0\0\u02db"+
    "\0\u0306\0\u0331\0\254\0\u035c\0\u022f\0\u0387\0\u03b2\0\u03dd"+
    "\0\u0408\0\254\0\u0433\0\u045e\0\u0489\0\u04b4\0\254\0\254"+
    "\0\u04df\0\254\0\254\0\254\0\254\0\254\0\254\0\u050a"+
    "\0\u0535\0\u0560\0\u058b\0\u05b6\0\u05e1\0\254\0\254\0\u0183"+
    "\0\u060c\0\u0637\0\u0535\0\254\0\254\0\u0662\0\254\0\u068d"+
    "\0\u06b8\0\u06e3\0\u070e\0\u0739\0\u0764\0\u078f\0\u07ba\0\u07e5"+
    "\0\u0810\0\u083b\0\u0866\0\u0891\0\u08bc\0\u08e7\0\u0912\0\u093d"+
    "\0\u0968\0\u0993\0\u09be\0\u09e9\0\u0a14\0\u0a3f\0\u0a6a\0\u0a95"+
    "\0\u0ac0\0\u0aeb\0\u0b16\0\u0b41\0\u0b6c\0\u0b97\0\u0bc2\0\u0bed"+
    "\0\u0c18\0\u0c43\0\u0c6e\0\u0c99\0\u0cc4\0\u0cef\0\u0d1a\0\u0662"+
    "\0\u0d45\0\u0d70\0\u01d9\0\u0d9b\0\u0dc6\0\u0662\0\u0df1\0\u0e1c"+
    "\0\u0662\0\u0662\0\u0e47\0\u0e72\0\u0e9d\0\u0ec8\0\u0ef3\0\254"+
    "\0\u0662\0\u0f1e\0\u0662\0\u0f49\0\u0f74\0\u0f9f\0\u0fca\0\u0662"+
    "\0\u0ff5\0\u1020\0\u104b\0\u0662\0\u1076\0\u10a1\0\u0662\0\u0662"+
    "\0\u0662\0\u0662";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[138];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\5\1\6\1\7\1\10\2\5\1\11\45\5\1\12"+
    "\1\13\1\10\1\5\1\14\1\15\1\5\5\16\1\17"+
    "\10\16\2\5\1\20\1\21\1\22\1\23\1\24\1\25"+
    "\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\16"+
    "\1\5\1\35\1\36\2\16\1\5\1\12\1\13\1\10"+
    "\2\5\1\15\1\37\16\40\1\5\1\41\7\5\1\42"+
    "\3\5\1\33\1\5\1\40\3\5\2\40\1\43\1\12"+
    "\1\13\50\43\54\0\1\6\54\0\1\10\55\0\1\44"+
    "\17\0\1\45\25\0\1\12\51\0\1\14\2\0\1\14"+
    "\1\46\1\47\45\14\6\0\1\50\17\0\1\50\30\0"+
    "\1\16\1\0\1\16\1\0\16\16\1\0\1\16\15\0"+
    "\6\16\4\0\1\16\1\0\1\16\1\0\7\16\1\51"+
    "\6\16\1\0\1\16\15\0\6\16\47\0\2\35\35\0"+
    "\1\52\53\0\1\53\56\0\1\54\52\0\1\55\52\0"+
    "\1\56\52\0\1\57\27\0\1\60\104\0\2\35\1\61"+
    "\27\0\1\62\30\0\1\40\1\0\1\40\1\0\16\40"+
    "\17\0\6\40\26\0\1\63\24\0\1\43\2\0\50\43"+
    "\7\0\1\64\20\0\1\65\31\0\1\66\17\0\1\67"+
    "\23\0\1\14\2\0\1\14\1\46\1\70\45\14\4\0"+
    "\1\16\1\0\1\16\1\0\1\16\1\71\14\16\1\0"+
    "\1\16\15\0\6\16\17\0\1\72\44\0\2\73\2\0"+
    "\1\73\1\0\1\73\2\0\1\73\24\0\2\73\1\0"+
    "\1\73\6\0\1\74\52\0\1\75\44\0\1\76\2\0"+
    "\1\77\4\76\1\100\1\76\1\101\2\76\1\102\1\76"+
    "\1\103\3\76\1\104\1\76\1\105\25\76\30\0\1\106"+
    "\26\0\1\16\1\0\1\16\1\0\1\107\15\16\1\0"+
    "\1\16\15\0\6\16\11\0\1\110\41\0\1\76\3\0"+
    "\50\76\3\0\5\76\1\111\42\76\3\0\7\76\1\112"+
    "\40\76\3\0\13\76\1\113\34\76\3\0\10\76\1\114"+
    "\3\76\1\115\33\76\3\0\10\76\1\116\37\76\3\0"+
    "\11\76\1\117\35\76\30\0\1\120\26\0\1\16\1\0"+
    "\1\16\1\0\4\16\1\121\11\16\1\0\1\16\15\0"+
    "\6\16\10\0\1\122\42\0\1\76\3\0\10\76\1\123"+
    "\1\124\36\76\3\0\10\76\1\125\37\76\3\0\5\76"+
    "\1\126\10\76\1\127\31\76\3\0\11\76\1\130\36\76"+
    "\3\0\4\76\1\131\10\76\1\132\32\76\3\0\11\76"+
    "\1\133\36\76\3\0\13\76\1\134\33\76\30\0\1\135"+
    "\26\0\1\16\1\0\1\16\1\0\7\16\1\136\6\16"+
    "\1\0\1\16\15\0\6\16\14\0\1\137\36\0\1\76"+
    "\3\0\11\76\1\140\36\76\3\0\13\76\1\141\34\76"+
    "\3\0\11\76\1\142\36\76\3\0\4\76\1\143\43\76"+
    "\3\0\17\76\1\144\30\76\3\0\4\76\1\145\4\76"+
    "\1\146\36\76\3\0\5\76\1\147\42\76\3\0\13\76"+
    "\1\150\34\76\3\0\13\76\1\151\34\76\3\0\16\76"+
    "\1\152\30\76\30\0\1\5\26\0\1\16\1\0\1\16"+
    "\1\0\5\16\1\153\10\16\1\0\1\16\15\0\6\16"+
    "\17\0\1\154\33\0\1\76\3\0\13\76\1\155\34\76"+
    "\3\0\5\76\1\156\42\76\3\0\4\76\1\157\43\76"+
    "\3\0\10\76\1\160\37\76\3\0\20\76\1\161\27\76"+
    "\3\0\5\76\1\162\42\76\3\0\13\76\1\163\34\76"+
    "\3\0\10\76\1\164\1\165\36\76\3\0\5\76\1\166"+
    "\42\76\3\0\17\76\1\167\27\76\15\0\1\170\35\0"+
    "\1\76\3\0\5\76\1\171\42\76\3\0\12\76\1\172"+
    "\35\76\3\0\13\76\1\173\34\76\3\0\16\76\1\174"+
    "\31\76\3\0\11\76\1\175\36\76\3\0\13\76\1\176"+
    "\34\76\3\0\4\76\1\177\43\76\3\0\20\76\1\200"+
    "\27\76\3\0\4\76\1\201\43\76\3\0\17\76\1\202"+
    "\30\76\3\0\13\76\1\203\34\76\3\0\5\76\1\204"+
    "\42\76\3\0\10\76\1\205\37\76\3\0\7\76\1\206"+
    "\40\76\3\0\20\76\1\207\27\76\3\0\5\76\1\210"+
    "\42\76\3\0\13\76\1\211\34\76\3\0\10\76\1\212"+
    "\36\76";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4300];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\1\2\11\3\1\1\11\6\1\2\11\2\1"+
    "\2\11\4\1\1\11\6\1\1\11\1\1\2\0\1\1"+
    "\2\11\1\1\6\11\4\0\1\1\1\0\2\11\2\1"+
    "\1\0\1\1\2\11\1\1\1\11\10\1\1\0\7\1"+
    "\1\0\1\1\1\0\12\1\1\0\1\1\1\0\14\1"+
    "\1\0\13\1\1\11\22\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[138];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */

    private StringBuffer padding = new StringBuffer();
    private PPToken lastToken;

    public boolean hasMoreTokens() {
    	return lastToken == null || lastToken.getType() != END_OF_FILE;
    }

    public PPToken nextToken() throws IOException {
    	yylex();
    	return lastToken;
    }	

    public PPToken getLastToken() {
    	return lastToken;
    }	

    private int token(int type) {
    	lastToken = new PPToken(type, yyline + 1, yycolumn, padding.toString(), yytext());
        padding = new StringBuffer();
        return type;
    }
    
    public static void main(String argv[]) throws IOException {
        if (argv.length == 0) {
            System.out.println("Usage : java PreprocessorScanner <inputfile>");
        } else {
            PreprocessorScanner scanner = new PreprocessorScanner( new FileReader(argv[0]));
            while (scanner.hasMoreTokens()) System.out.print(scanner.nextToken());
        }
    }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public PreprocessorScanner(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public PreprocessorScanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 132) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzPushbackPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead < 0) {
      return true;
    }
    else {
      zzEndRead+= numRead;
      return false;
    }
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = zzLexicalState;


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 44: 
          { yybegin(COMMAND); return token(COMMAND_ENDDEBUG);
          }
        case 48: break;
        case 11: 
          { return token(OP_OR);
          }
        case 49: break;
        case 13: 
          { return token(OP_AT);
          }
        case 50: break;
        case 37: 
          { yybegin(COMMAND); return token(COMMAND_DEBUG);
          }
        case 51: break;
        case 7: 
          { return token(ABILITY);
          }
        case 52: break;
        case 12: 
          { return token(OP_XOR);
          }
        case 53: break;
        case 15: 
          { return token(ASSIGN);
          }
        case 54: break;
        case 45: 
          { yybegin(COMMAND); return token(COMMAND_ELIFNDEF);
          }
        case 55: break;
        case 21: 
          { yybegin(OTHERTEXT); return token(SIMPLE_COMMENT);
          }
        case 56: break;
        case 1: 
          { return token(OTHER_TEXT);
          }
        case 57: break;
        case 6: 
          { return token(UNFINISHED_STRING);
          }
        case 58: break;
        case 5: 
          { yybegin(YYINITIAL); return token(END_OF_LINE);
          }
        case 59: break;
        case 32: 
          { yybegin(COMMAND); return token(COMMAND_IF);
          }
        case 60: break;
        case 25: 
          { return token(OP_GREATER_OR_EQUAL);
          }
        case 61: break;
        case 47: 
          { yybegin(COMMAND); return token(COMMAND_CONDITION);
          }
        case 62: break;
        case 46: 
          { yybegin(COMMAND); return token(COMMAND_UNDEFINE);
          }
        case 63: break;
        case 43: 
          { yybegin(COMMAND); return token(COMMAND_ELIFDEF);
          }
        case 64: break;
        case 27: 
          { yybegin(OLDCOMMAND); return token(OLD_STX_HEADER_START);
          }
        case 65: break;
        case 39: 
          { return token(COLON_DEFINED);
          }
        case 66: break;
        case 38: 
          { yybegin(COMMAND); return token(COMMAND_ENDIF);
          }
        case 67: break;
        case 33: 
          { yybegin(COMMAND); return token(COMMAND_ELIF);
          }
        case 68: break;
        case 28: 
          { yybegin(OLDCOMMAND); return token(OLD_STX_FOOTER_START);
          }
        case 69: break;
        case 2: 
          { yybegin(OTHERTEXT); padding.append(yytext());
          }
        case 70: break;
        case 31: 
          { yybegin(OTHERTEXT); return token(COMMAND_UNKNOWN);
          }
        case 71: break;
        case 9: 
          { return token(RIGHT_BRACKET);
          }
        case 72: break;
        case 4: 
          { padding.append(yytext());
          }
        case 73: break;
        case 8: 
          { return token(LEFT_BRACKET);
          }
        case 74: break;
        case 17: 
          { return token(OP_GREATER);
          }
        case 75: break;
        case 18: 
          { return token(COMMA);
          }
        case 76: break;
        case 42: 
          { yybegin(COMMAND); return token(COMMAND_MDEBUG);
          }
        case 77: break;
        case 36: 
          { yybegin(COMMAND); return token(COMMAND_IFDEF);
          }
        case 78: break;
        case 34: 
          { yybegin(COMMAND); return token(COMMAND_ELSE);
          }
        case 79: break;
        case 16: 
          { return token(OP_LESS);
          }
        case 80: break;
        case 14: 
          { return token(OP_NOT);
          }
        case 81: break;
        case 10: 
          { return token(OP_AND);
          }
        case 82: break;
        case 19: 
          { return token(NUMBER);
          }
        case 83: break;
        case 23: 
          { return token(OP_EQUALS);
          }
        case 84: break;
        case 3: 
          { return token(END_OF_LINE);
          }
        case 85: break;
        case 20: 
          { return token(STRING);
          }
        case 86: break;
        case 22: 
          { return token(OP_NOT_EQUALS);
          }
        case 87: break;
        case 41: 
          { yybegin(COMMAND); return token(COMMAND_DEFINE);
          }
        case 88: break;
        case 26: 
          { yybegin(OTHERTEXT); return token(PREPROCESSOR_COMMENT);
          }
        case 89: break;
        case 29: 
          { yybegin(OTHERTEXT); return token(OLD_STX_HEADER_END);
          }
        case 90: break;
        case 30: 
          { yybegin(OTHERTEXT); return token(OLD_STX_FOOTER_END);
          }
        case 91: break;
        case 40: 
          { yybegin(COMMAND); return token(COMMAND_IFNDEF);
          }
        case 92: break;
        case 24: 
          { return token(OP_LESS_OR_EQUAL);
          }
        case 93: break;
        case 35: 
          { return token(DEFINED);
          }
        case 94: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              {     return token(END_OF_FILE);
 }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
