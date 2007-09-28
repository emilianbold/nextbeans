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



package org.netbeans.modules.uml.ui.support;

import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.support.umlutils.ElementLocator;
import org.netbeans.modules.uml.core.support.umlutils.IElementLocator;

/**
 * @author sumitabhk
 *
 */
public class ElementReloader
{

	/**
	 *
	 */
	public ElementReloader()
	{
		super();
	}
	
	/**
	 * Reloads an element given only the XMI id.
	 */
	public IElement getElement(String xmiid)
	{
		if (xmiid != null && xmiid.length() > 0)
		{
			IElementLocator locator = new ElementLocator();
			return locator.findElementByID(xmiid);
		}
		return null;
	}

	/**
	 * Reloads an element given the XMI id and the project (toplevel) XMIID.
	 *
	 * @param elementTopLevelXMIID [in] The toplevel id of the model element to reload (ie IProject XMIID)
	 * @param elementXMIID [in] The XMIID of the element to reload
	 * @param pElement [out,retval] The reloaded element, or NULL
	 */
	public IElement getElement(String topLevelID, String xmiid)
	{
		// when the top level XMI ID is blank, IElementLocator calls FindElementByID2() internally
		if (xmiid != null && xmiid.length() > 0)
		{
			IElementLocator locator = new ElementLocator();
			return locator.findElementByID(topLevelID, xmiid);
		}
		return null;
	}
}



