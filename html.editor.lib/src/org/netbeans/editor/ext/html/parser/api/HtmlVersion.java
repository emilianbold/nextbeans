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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.editor.ext.html.parser.api;

import org.netbeans.editor.ext.html.dtd.DTD;
import org.openide.util.NbBundle;

/**
 *
 * @author marekfukala
 */
public enum HtmlVersion {

    HTML32(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_32"),
        "-//W3C//DTD HTML 3.2 Final//EN", //NOI18N
        null),
    HTML40_STRICT(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_40_STRICT"),
        "-//W3C//DTD HTML 4.0//EN",
        "http://www.w3.org/TR/REC-html40/strict.dtd"), //NOI18N

    HTML40_TRANSATIONAL(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_40_TRAN"),
        "-//W3C//DTD HTML 4.0 Transitional//EN",
        "http://www.w3.org/TR/REC-html40/loose.dtd"), //NOI18N

    HTML40_FRAMESET(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_40_FRAM"),
        "-//W3C//DTD HTML 4.0 Frameset//EN",
        "http://www.w3.org/TR/REC-html40/frameset.dtd"), //NOI18N

    HTML41_STRICT(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_401_STRICT"),
        "-//W3C//DTD HTML 4.01//EN",
        "http://www.w3.org/TR/html4/strict.dtd"), //NOI18N

    HTML41_TRANSATIONAL(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_401_TRAN"),
        "-//W3C//DTD HTML 4.01 Transitional//EN",
        "http://www.w3.org/TR/html4/loose.dtd"), //NOI18N

    HTML41_FRAMESET(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_401_FRAM"),
        "-//W3C//DTD HTML 4.01 Frameset//EN",
        "http://www.w3.org/TR/html4/frameset.dtd"), //NOI18N

    HTML5(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_5"), null, null), //no public id nor system id, just <!doctype html>

    XHTML10_STICT(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_X10_STRICT"),
        "-//W3C//DTD XHTML 1.0 Strict//EN",
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd",
        null,
        "http://www.w3.org/1999/xhtml",
        true), //NOI18N

    XHTML10_TRANSATIONAL(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_X10_TRAN"),
        "-//W3C//DTD XHTML 1.0 Transitional//EN",
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
        null,
        "http://www.w3.org/1999/xhtml",
        true), //NOI18N

    XHTML10_FRAMESET(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_X10_FRAM"),
        "-//W3C//DTD XHTML 1.0 Frameset//EN",
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd",
        null,
        "http://www.w3.org/1999/xhtml",
        true), //NOI18N

    //XHTML 1.1 version fallbacks to XHTML 1.0 strict since the current SGML parser
    //cannot properly parse the XHTML1.1 dtd
    XHTML11(NbBundle.getMessage(HtmlVersion.class, "MSG_HTML_VERSION_X11"),
        "-//W3C//DTD XHTML 1.1//EN",
        "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd",
        "-//W3C//DTD XHTML 1.0 Strict//EN",
        "http://www.w3.org/1999/xhtml",
        true); //NOI18N

    private static final String DOCTYPE_PREFIX = "<!doctype html public \""; //NOI18N
    private static final String HTML5_DOCTYPE = "<!doctype html>"; //NOI18N

    public static HtmlVersion findByPublicId(String publicId) {
        for (HtmlVersion version : HtmlVersion.values()) {
            if (publicId == null && publicId == version.publicID //null check
                    || publicId != null && publicId.equals(version.getPublicID())) {
                return version;
            }
        }
        return null;
    }

    public static HtmlVersion findByNamespace(String namespace) {
        for (HtmlVersion version : HtmlVersion.values()) {
            if (namespace.equals(version.getDefaultNamespace())) {
                return version;
            }
        }
        return null;

    }
    /** The default html version. */
    private static final HtmlVersion DEFAULT_VERSION = HTML5;
    public static HtmlVersion DEFAULT_VERSION_UNIT_TESTS_OVERRIDE = null;
    
    public static HtmlVersion getDefaultVersion() {
        return DEFAULT_VERSION_UNIT_TESTS_OVERRIDE != null ? DEFAULT_VERSION_UNIT_TESTS_OVERRIDE : DEFAULT_VERSION;
    }


    private final String displayName;
    private final String publicID, systemID;
    private final String fallbackPublicID;
    private final String defaultNamespace;
    private boolean isXhtml;

    private HtmlVersion(String displayName, String publicID, String systemID) {
        this(displayName, publicID, systemID, null, null, false);
    }

    private HtmlVersion(String displayName, String publicID, String systemID, String fallbackPublicID, String defaultNamespace, boolean isXhtml) {
        this.publicID = publicID;
        this.systemID = systemID;
        this.defaultNamespace = defaultNamespace;
        this.isXhtml = isXhtml;
        this.fallbackPublicID = fallbackPublicID;
        this.displayName = displayName;
    }

    public String getPublicID() {
        return publicID;
    }

    public String getSystemId() {
        return systemID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultNamespace() {
        return this.defaultNamespace;
    }

    public String getDoctypeDeclaration() {
        if (getPublicID() == null) {
            return HTML5_DOCTYPE;
        } else {
            StringBuilder b = new StringBuilder();
            b.append(DOCTYPE_PREFIX);
            b.append(getPublicID());
            b.append('"');

            if (getSystemId() != null) {
                b.append(" \"");
                b.append(getSystemId());
                b.append('"');
            }
            b.append('>');

            return b.toString();
        }
    }

    public boolean isXhtml() {
        return this.isXhtml;
    }

    public DTD getDTD() {
        //use the fallback public id to get the DTD if defined, otherwise
        //use the proper public id. This is needed due to the lack of parser
        //for XHTML 1.1 file. Such files are parsed according to the XHTML1.0 DTD.
        String publicid = fallbackPublicID != null ? fallbackPublicID : publicID;

        return org.netbeans.editor.ext.html.dtd.Registry.getDTD(publicid, null);
    }
}
