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


package org.netbeans.modules.uml.ui.products.ad.graphobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.tomsawyer.drawing.TSDGraphManager;
import com.tomsawyer.editor.TSEGraph;
import com.tomsawyer.graph.TSGraph;
import com.tomsawyer.graph.TSGraphManager;
import com.tomsawyer.graph.TSNode;
import com.tomsawyer.graph.TSGraphObject;
import com.tomsawyer.graph.TSEdge;
//import com.tomsawyer.graph.TSTailorMap;
//import com.tomsawyer.graph.TSTailorProperty;
//import com.tomsawyer.jnilayout.TSDGraph;
import com.tomsawyer.drawing.TSDGraph;
//import com.tomsawyer.layout.glt.TSLayoutEngine;
import com.tomsawyer.util.TSObject;

import org.netbeans.modules.uml.common.generics.IteratorT;
import org.netbeans.modules.uml.core.metamodel.diagrams.IGraphEventKind;
import org.netbeans.modules.uml.core.support.umlsupport.IETPoint;
import org.netbeans.modules.uml.core.support.umlutils.ETList;
import org.netbeans.modules.uml.core.support.umlutils.ETArrayList;
import org.netbeans.modules.uml.ui.products.ad.ADDrawEngines.IADContainerDrawEngine;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IETGraphObject;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IETNode;

/*
 * 
 * @author Kevinm
 *
 * Main logical datastructure for all UML Diagram types.
 * 
 */
public class ETGraph extends TSEGraph
{
   /**
    * Constructor of the class. This constructor should be implemented
    * to enable <code>TSEGraph</code> inheritance.
    */
   protected ETGraph()
   {
      super();
   }

   /**
    * This method allocates a new node for this graph. This method
    * should be implemented to enable <code>TSENode</code> inheritance.
    *
    * @return an object of the type derived from <code>TSENode</code>
    */
   protected TSNode newNode()
   {
      return new ETNode();
   }

   /**
    * This method allocates a new edge for this graph. This method
    * should be implemented to enable <code>TSEEdge</code> inheritance.
    *
    * @return an object of the type derived from <code>TSEEdge</code>
    
   protected TSEdge newEdge()
   {
      return new ETEdge();
   }
    */
   /**
    * This method copies attributes of the source object to this
    * object. The source object has to be of the type compatible
    * with this class (equal or derived). The method should make a
    * deep copy of all instance variables declared in this class.
    * Variables of simple (non-object) types are automatically copied
    * by the call to the copy method of the super class.
    *
    * @param sourceObject  the source from which all attributes must
    *                      be copied
    */
   public void copy(Object sourceObject)
   {
      // copy the attributes of the super class first
      super.copy(sourceObject);

      // copy any class specific attributes here
      // ...
   }

   /*
    * Returns the list of selected nodes and edges. 
    */
   public ETList < TSGraphObject > getSelectedNodesAndEdges()
   {
      ETList < TSGraphObject > selectedObjects = null;
      if (hasSelected())
      {
         selectedObjects = new ETArrayList < TSGraphObject > ()
         {
            public boolean addAll(List c)
            {
               return c != null ? super.addAll(c) : false;
            }
         };
      }
      else
         return null;

      selectedObjects.addAll(selectedNodes());
      selectedObjects.addAll(selectedEdges());
      return selectedObjects;
   }

   /*
    * Returns the list of selected Graph objects, null if no objects are selected.
    */
   public ETList < TSGraphObject > getSelectedObjects(boolean includeConnectors, boolean includePathNodes)
   {
      ETList < TSGraphObject > selectedObjects = null;
      if (hasSelected())
      {
         selectedObjects = new ETArrayList < TSGraphObject > ()
         {
            public boolean addAll(List c)
            {
               return c != null ? super.addAll(c) : false;
            }
         };
      }
      else
         return null;

      selectedObjects.addAll(selectedNodes());
      selectedObjects.addAll(selectedEdges());
      selectedObjects.addAll(selectedEdgeLabels());
      selectedObjects.addAll(selectedNodeLabels());

      if (includePathNodes)
         selectedObjects.addAll(selectedPathNodes());
      if (includeConnectors)
         selectedObjects.addAll(selectedConnectors());
      return selectedObjects;
   }

   protected void sendGraphEvent(List graphObjects, int pGraphEventKind)
   {
      IteratorT < IETGraphObject > iter = new IteratorT < IETGraphObject > (graphObjects);
      while (iter.hasNext())
      {
         iter.next().onGraphEvent(pGraphEventKind);
      }
   }

   /*
    * Returns all reachable node Labels
    */
   public List nodeLabels()
   {
      return buildNodeLabels(TSDGraphManager.REACHABLE);
   }

   /*
    * Returns all reachable edge Labels.
    */
   public List edgeLabels()
   {
      return buildEdgeLabels(TSDGraphManager.REACHABLE);
   }

   public void onGraphEvent(int pGraphEventKind, IETPoint pStartPoint, IETPoint pEndPoint, ETList < IETGraphObject > affectedObjects)
   {
      if (affectedObjects == null || affectedObjects.size() < 0)
      {
         List objectList = new ArrayList();
         objectList.addAll(this.nodes());
         objectList.addAll(this.edges());
         sendGraphEvent(objectList, pGraphEventKind);
      }
      else
      {
         sendGraphEvent(affectedObjects, pGraphEventKind);
      }
   }

   /*
    * Returns all the IETGraphObjects.
    */
   public ETList < IETGraphObject > getAllETGraphObjects()
   {
      ETList < IETGraphObject > retVal = new ETArrayList < IETGraphObject > ();

      retVal.addAll(nodes());
      retVal.addAll(edges());
      retVal.addAll(nodeLabels());
      retVal.addAll(edgeLabels());

      return retVal;
   }

   /*
    * Returns all the TSObjects.
    */
   public ETList < TSObject > getAllGraphObjects()
   {
      ETList < IETGraphObject > iETGraphObjects = getAllETGraphObjects();
      ETList < TSObject > tsObjects = iETGraphObjects != null ? new ETArrayList < TSObject > () : null;
      if (tsObjects != null)
      {
         tsObjects.addAll((List)iETGraphObjects);
      }

      return tsObjects;
   }

   /*
    * Returns a pointer to our extended graph manager.
    */
   protected ETGraphManager getETGraphManager()
   {
      TSGraphManager mgr = this.getOwnerGraphManager();
      return mgr instanceof ETGraphManager ? (ETGraphManager)mgr : null;
   }

   /*
    *  (non-Javadoc)
    * @see com.tomsawyer.drawing.TSDGraph#setBoundsUpdatingEnabled(boolean)
    */
   public void setBoundsUpdatingEnabled(boolean enabled)
   {
      boolean curState = isBoundsUpdatingEnabled();
      ETGraphManager mgr = getETGraphManager();
      boolean readingFile = mgr != null ? mgr.isReadingGMF() : false;

      // If we are still reading the graph model file, return.
      if (readingFile && enabled)
      {
         return;
      }

      super.setBoundsUpdatingEnabled(enabled);

      if (curState == false && enabled)
      {
         updateBounds();
      }
   }
}
