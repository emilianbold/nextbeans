/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.navigator;

import javax.swing.JComponent;
import org.openide.util.Lookup;

/** Navigation related view description. 
 * 
 * Implementors of this interface, also registered in layer,
 * will be plugged into Navigator UI. 
 *
 * @author Dafe Simonek
 */
public interface NavigatorPanel {

    /** Name of the view which will be shown in navigator UI. 
     * 
     * @return Displayable name of the view
     */
    public String getDisplayName ();
    
    /** Description of the view, explaining main purpose of the view.
     * Will be shown in Navigator UI.
     * 
     * @return String description of the view.
     */
    public String getDisplayHint ();
    
    /** JComponent representation of this view. System will ask
     * multiple times and it is strongly recommended to
     * return the same JComponent instance each call for performance
     * reasons.<p>
     *
     * This method is always called in event dispatch thread. 
     * 
     * @return JComponent representation of this view.
     */
    public JComponent getComponent ();
    
    /** Called when this panel's component is about to being displayed.
     * Right place to attach listeners to current navigation data context,
     * as clients are responsible for listening to context changes when active
     * (in the time between panelActivated - panelDeactivated calls). 
     *
     * This method is always called in event dispatch thread. 
     *
     * @param context Lookup instance representing current context to take
     * data from
     */
    public void panelActivated (Lookup context);
    
    /** Called when this panel's component is about to being hidden.
     * Right place to detach, remove listeners from data context, that 
     * were added in panelActivated impl.
     *
     * This method is always called in event dispatch thread. 
     */
    public void panelDeactivated ();
    
    
    /** Returns Lookup that will be integrated into Lookup of Navigator UI
     * TopComponent. Allows clients for example to specify activated Node
     * of Navigator UI TopComponent when this panel is active.<p></p>
     * 
     * Method may return null, signalizing that default mechanism should be enabled.
     * Default mechanism chooses first Node from Utilities.actionsGlobalContext()
     * as activated Node for Navigator's TopComponent.  
     *   
     * @return Lookup instance or null
     */
    public Lookup getLookup ();
    
    
}
