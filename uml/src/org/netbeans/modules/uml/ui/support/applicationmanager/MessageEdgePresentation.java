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



package org.netbeans.modules.uml.ui.support.applicationmanager;

import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IPresentationElement;
import org.netbeans.modules.uml.core.metamodel.dynamics.ILifeline;
import org.netbeans.modules.uml.core.metamodel.dynamics.IMessage;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IETGraphObject;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IETNode;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.TypeConversions;
import org.netbeans.modules.uml.ui.swing.drawingarea.IReconnectEdgeContext;

/*
 * 
 * @author KevinM
 *
 */
public class MessageEdgePresentation extends EdgePresentation implements IMessageEdgePresentation
{

   /**
    * 
    */
   public MessageEdgePresentation()
   {
      super();
   }

   /* (non-Javadoc)
    * @see org.netbeans.modules.uml.ui.support.applicationmanager.IEdgePresentation#reconnectLink(org.netbeans.modules.uml.ui.swing.drawingarea.IReconnectEdgeContext)
    */
   public boolean reconnectLink(IReconnectEdgeContext pContext)
   {
      if (pContext == null)
         return false;

      boolean bSuccessfullyReconnected = false;
      try
      {
         IETNode pOldNode = pContext.getPreConnectNode(); // The node that is being disconnected
         IETNode pNewNode = pContext.getProposedEndNode(); // The proposed, new node to take its place

         if (pOldNode != null && pNewNode != null && isMessage())
         {

            IElement pFromNodeElement = TypeConversions.getElement(pOldNode);
            IElement pToNodeElement = TypeConversions.getElement(pNewNode);

            ILifeline cpOldLifeline = pFromNodeElement instanceof ILifeline ? (ILifeline) pFromNodeElement : null;
            ILifeline cpNewLifeline = pToNodeElement instanceof ILifeline ? (ILifeline) pToNodeElement : null;

            if (cpOldLifeline != null && cpNewLifeline != null)
            {
               IElement pEdgeElement = getModelElement();
               IMessage cpMessage = pEdgeElement instanceof IMessage ? (IMessage) pEdgeElement : null;
               if (cpMessage != null)
               {
                  boolean bReconnectTarget = false;
                  ILifeline cpReceivingLifeline = cpMessage.getReceivingLifeline();

                  if (cpReceivingLifeline != null)
                  {
                     bReconnectTarget = cpOldLifeline.isSame(cpReceivingLifeline);
                  }
                  
                  if (bReconnectTarget)
                  {
                     cpMessage.changeReceivingLifeline(cpOldLifeline, cpNewLifeline);
                  }
                  else
                  {
                     cpMessage.changeSendingLifeline(cpOldLifeline, cpNewLifeline);
                  }
						bSuccessfullyReconnected = true;
               }
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         bSuccessfullyReconnected = false;
      }
      return bSuccessfullyReconnected;
   }

   /* (non-Javadoc)
    * @see org.netbeans.modules.uml.ui.support.applicationmanager.IEdgePresentation#reconnectLinkToValidNodes()
    */
   public boolean reconnectLinkToValidNodes()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see org.netbeans.modules.uml.ui.support.applicationmanager.IEdgePresentation#validateLinkEnds()
    */
   public boolean validateLinkEnds()
   {
      return true;
   }

   public boolean isMessage()
   {
      IElement pEdgeElement = this.getModelElement();
      String metaTypeString = pEdgeElement != null ? pEdgeElement.getElementType() : null;
      return metaTypeString != null && metaTypeString.equals("Message");
   }

   /* (non-Javadoc)
    * @see org.netbeans.modules.uml.core.metamodel.core.foundation.IPresentationElement#transform(java.lang.String)
    */
   public IPresentationElement transform(String elemName)
	{
		try
		{
			//	Call our base class which clears out the cached model element
			IPresentationElement pe = super.transform(elemName);
			IETGraphObject pETElement = this.getETGraphObject();
			if (pETElement != null)
			{
				pe = pETElement.transform(elemName);
			}

			return pe;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
