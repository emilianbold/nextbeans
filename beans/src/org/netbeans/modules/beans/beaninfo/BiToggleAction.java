/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.beans.beaninfo;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
* Toggles selection.
*
* @author   Petr Hrebejk
*/
public class BiToggleAction extends NodeAction  {

    static final long serialVersionUID =3773842179168178798L;
    /** generated Serialized Version UID */
    //static final long serialVersionUID = 1391479985940417455L;

    //private static final Class[] cookieClasses = new Class[] { BiFeatureNode.class };



    /** Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName () {
        return NbBundle.getBundle (GenerateBeanInfoAction.class).getString ("CTL_TOGGLE_MenuItem");
    }


    /*
     public Class[] cookieClasses() {
       return cookieClasses;
     }
     
    */
    /** The action's icon location.
    * @return the action's icon location
    */
    protected String iconResource () {
        return null;
        //return "/org/netbeans/modules/javadoc/resources/searchDoc.gif"; // NOI18N
    }

    /*
    public int mode () {
      return CookieAction.MODE_ALL;
}
    */

    /** Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean enable( Node[] activatedNodes ) {
        activatedNodes = BiPanel.getSelectedNodes();

        if ( activatedNodes.length < 1 )
            return false;

        for(int i = 0; i <activatedNodes.length; i++ ) {
            if( activatedNodes[i].getCookie( BiFeatureNode.class ) == null )
                return false;
            BiFeature biFeature = ((BiFeatureNode)activatedNodes[i].getCookie( BiFeatureNode.class )).getBiFeature();
            BiAnalyser biAnalyser = ((BiFeatureNode)activatedNodes[i].getCookie( BiFeatureNode.class )).getBiAnalyser();
            if( ( biFeature instanceof BiFeature.Property || biFeature instanceof BiFeature.IdxProperty ) && biAnalyser.isNullProperties() )
                return false;
            if( biFeature instanceof BiFeature.EventSet && biAnalyser.isNullEventSets() )
                return false;
            if( biFeature instanceof BiFeature.Method && biAnalyser.isNullMethods() )
                return false;
        }
        return true;
    }


    /** This method is called by one of the "invokers" as a result of
    * some user's action that should lead to actual "performing" of the action.
    * This default implementation calls the assigned actionPerformer if it
    * is not null otherwise the action is ignored.
    */
    public void performAction ( Node[] nodes ) {

        nodes = BiPanel.getSelectedNodes();

        if ( nodes.length < 1 )
            return;

        for(int i = 0; i < nodes.length; i++ ) {
            if( nodes[i].getCookie( BiFeatureNode.class ) != null )
                ((BiFeatureNode)nodes[i].getCookie( BiFeatureNode.class )).toggleSelection();
        }

    }

}
