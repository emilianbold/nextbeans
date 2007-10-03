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

/**
 * This interface has all of the bean info accessor methods.
 *
 * @Generated
 */

package org.netbeans.modules.j2ee.dd.api.application;

import org.netbeans.modules.j2ee.dd.api.common.CommonDDBean;
import org.netbeans.modules.j2ee.dd.api.common.VersionNotSupportedException;

public interface Module extends CommonDDBean {
	public static final String CONNECTOR = "Connector";	// NOI18N
	public static final String EJB = "Ejb";	// NOI18N
	public static final String JAVA = "Java";	// NOI18N
	public static final String WEB = "Web";	// NOI18N
	public static final String ALT_DD = "AltDd";	// NOI18N
        
	public void setConnector(String value);

	public String getConnector();

	public void setConnectorId(java.lang.String value) throws VersionNotSupportedException;

	public java.lang.String getConnectorId() throws VersionNotSupportedException;

	public void setEjb(String value);

	public String getEjb();

	public void setEjbId(java.lang.String value) throws VersionNotSupportedException;

	public java.lang.String getEjbId() throws VersionNotSupportedException;

	public void setJava(String value);

	public String getJava();

	public void setJavaId(java.lang.String value) throws VersionNotSupportedException;

	public java.lang.String getJavaId() throws VersionNotSupportedException;

	public void setWeb(Web value);

	public Web getWeb();

	public Web newWeb();

	public void setAltDd(String value);

	public String getAltDd();

	public void setAltDdId(java.lang.String value) throws VersionNotSupportedException;

	public java.lang.String getAltDdId() throws VersionNotSupportedException;

}
