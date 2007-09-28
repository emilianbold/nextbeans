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



package org.netbeans.modules.uml.ui.swing.drawingarea;

import org.netbeans.modules.uml.common.ETSystem;
import org.netbeans.modules.uml.core.support.umlutils.InvalidArguments;
import com.tomsawyer.editor.TSEGraphWindow;
import com.tomsawyer.editor.overview.TSEOverviewComponent;
import com.tomsawyer.editor.overview.TSEOverviewWindow;

import java.awt.Frame;

/**
 * @author KevinM
 *
 */
public class ETDiagramOverviewWindow extends TSEOverviewWindow implements IETSecondaryWindow
{

   /**
    * @param arg0
    * @param arg1
    * @param arg2
    */
   public ETDiagramOverviewWindow(Frame arg0, String arg1, TSEGraphWindow arg2)
   {
      super(arg0, arg1, arg2);
      // TODO Auto-generated constructor stub
   }

   /**
    * @param arg0
    * @param arg1
    * @param arg2
    */
   public ETDiagramOverviewWindow(Frame arg0, String arg1, TSEOverviewComponent arg2)
   {
      super(arg0, arg1, arg2);
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see org.netbeans.modules.uml.ui.swing.drawingarea.IETSecondaryWindow#setGraphWindow(com.tomsawyer.editor.TSEGraphWindow)
    */
   public void setGraphWindow(TSEGraphWindow graphWindow)
   {
      if (this.getOverviewComponent() != null)
			this.getOverviewComponent().setGraphWindow(graphWindow);

   }
   
   /*
    *  (non-Javadoc)
    * @see org.netbeans.modules.uml.ui.swing.drawingarea.IETSecondaryWindow#getGraphWindow()
    */
	public TSEGraphWindow getGraphWindow()
	{
		return this.getOverviewComponent() != null ? this.getOverviewComponent().getGraphWindow() : null;
	}
	
	public void setVisible(boolean show)
	{
		super.setVisible(show);
		if (show == false)
		{
			ETSystem.out.println("ETDiagramOverview is being hiden");
		}
		else
		{
			ETSystem.out.println("ETDiagramOverview is being shown");
		}
	}
	
}
