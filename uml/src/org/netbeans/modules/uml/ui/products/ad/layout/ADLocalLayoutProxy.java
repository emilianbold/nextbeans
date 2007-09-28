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


package org.netbeans.modules.uml.ui.products.ad.layout;

import org.netbeans.modules.uml.ui.products.ad.graphobjects.ETGraph;
//import com.tomsawyer.graph.TSTailorProperty;
import com.tomsawyer.drawing.*;
//import com.tomsawyer.layout.glt.TSLocalLayoutProxy;
import com.tomsawyer.service.layout.jlayout.client.TSLayoutProxy;
//import com.tomsawyer.layout.glt.property.*;
import com.tomsawyer.editor.TSEGraph;
import com.tomsawyer.editor.TSEGraphManager;

import com.tomsawyer.service.layout.jlayout.*;
import com.tomsawyer.service.layout.TSLayoutOutputTailor;
import com.tomsawyer.service.TSServiceInputData;
import com.tomsawyer.service.TSServiceOutputData;
import com.tomsawyer.service.TSServiceException;
import com.tomsawyer.util.TSLicenseManager;

/**
 * This class extends the base local layout proxy object to allow
 * the editor to set layout tailoring options for graph objects.
 */
//public class ADLocalLayoutProxy extends TSLocalLayoutProxy
public class ADLocalLayoutProxy extends TSLayoutProxy
{
	/**
	 * This method is called if a property is set on a graph object.
	 
	public void onGraphProperty(com.tomsawyer.jnilayout.TSDGraph graph, TSTailorProperty property)
	{
		// The base class implementation handles the style property
		// plus it commits all instances of TSLayoutProperty that have
		// been added to the graph tailoring options.

		super.onGraphProperty(graph, property);
	}*/


	/**
	 * This method is called if a property is set on a node object.
	 * The default implementation does not handle any properties.
	 
	public void onNodeProperty(com.tomsawyer.jnilayout.TSDNode node,
		TSTailorProperty property)
	{
		super.onNodeProperty(node, property);
	}*/


	/**
	 * This method is called if a property is set on an edge object.
	 * The default implementation does not handle any properties.
	 
	public void onEdgeProperty(com.tomsawyer.jnilayout.TSDEdge edge,
		TSTailorProperty property)
	{
		super.onEdgeProperty(edge, property);
	}*/


	/**
	 * This method is called if a property is set on a node label
	 * object. The default implementation does not handle any
	 * properties.
	
	public void onNodeLabelProperty(
		com.tomsawyer.jnilayout.TSNodeLabel label,
		TSTailorProperty property)
	{
		super.onNodeLabelProperty(label, property);
	} */


	/**
	 * This method is called if a property is set on an edge label
	 * object. The default implementation does not handle any
	 * properties.
	 
	public void onEdgeLabelProperty(
		com.tomsawyer.jnilayout.TSEdgeLabel label,
		TSTailorProperty property)
	{
		super.onEdgeLabelProperty(label, property);
	}*/


	/**
	 * This method allocates the post layout properties for the
	 * specified node.
	 
	protected void allocatePostLayoutProperties(TSDNode node)
	{
		super.allocatePostLayoutProperties(node);

		TSDGraph owner = (TSDGraph) node.getOwner();

		// if the layout style is tree, add the tree-
		// specific post-layout properties.

		if (TSDGraph.TREE.equals(owner.getLayoutStyle()))
		{
			TSBooleanLayoutProperty layoutProperty =
				new TSBooleanLayoutProperty(TSTailorProperties.
					TREE_NODE_IS_ROOT_NODE_POST_LAYOUT);

			node.setTailorProperty(layoutProperty);
		}

		// if the layout style is hierarchical, add the hierarchical-
		// specific post-layout properties.

		else if (TSDGraph.HIERARCHICAL.equals(owner.getLayoutStyle()))
		{
			TSIntLayoutProperty layoutProperty =
				new TSIntLayoutProperty(TSTailorProperties.
					HIERARCHICAL_NODE_ACTUAL_LEVEL_NUMBER_POST_LAYOUT);
			node.setTailorProperty(layoutProperty);
		}
	}*/

	/**
	 * This method allocates the post layout properties for the
	 * specified edge.
	 
	protected void allocatePostLayoutProperties(TSDEdge edge)
	{
		super.allocatePostLayoutProperties(edge);

		TSDGraph owner = (TSDGraph) edge.getOwner();

		// if the layout style is tree, add the tree-
		// specific post-layout properties.

		if (TSDGraph.TREE.equals(owner.getLayoutStyle()))
		{
			TSBooleanLayoutProperty layoutProperty =
				new TSBooleanLayoutProperty(TSTailorProperties.
					TREE_EDGE_IS_TREE_EDGE_POST_LAYOUT);

			edge.setTailorProperty(layoutProperty);
		}
	}*/


	/**
	 * This method performs layout on the given graph manager.
	 *
	 * @param graphManager The graph manager to be laid out
	 */
	public void globalLayout(TSDGraphManager graphManager)
		throws Exception
	{
		ADLayoutProcessor processor =
			new ADLayoutProcessor((TSEGraphManager) graphManager);
		
		processor.preprocess();
			
		//super.globalLayout(graphManager); //jyothi
		
		processor.postprocess();
	}
	

	/**
	 * This method performs layout on the given graph.
	 *
	 * @param graph The graph to be laid out
	 */
	public void globalLayout(TSDGraph graph)
		throws Exception
	{
		ADLayoutProcessor processor =
			new ADLayoutProcessor((TSEGraph) graph);
			
		processor.preprocess();
			
		//super.globalLayout(graph); //jyothi
		
		processor.postprocess();
	}
	
	
	/**
	 * This method performs layout on the given graph manager.
	 *
	 * @param graphManager The graph manager to be laid out
	 */
	public void incrementalLayout(TSDGraphManager graphManager)
		throws Exception
	{
		ADLayoutProcessor processor =
			new ADLayoutProcessor((TSEGraphManager) graphManager);
				
		processor.preprocess();
				
		//super.incrementalLayout(graphManager); //jyothi
			
		processor.postprocess();
	}


	/**
	 * This method performs layout on the given graph.
	 *
	 * @param graph The graph to be laid out
	 */
	public void incrementalLayout(TSDGraph graph)
		throws Exception
	{
		ADLayoutProcessor processor =
			new ADLayoutProcessor((TSEGraph) graph);
				
		processor.preprocess();
			
		//super.incrementalLayout(graph); //jyothi
		
		processor.postprocess();
	}
	
}
