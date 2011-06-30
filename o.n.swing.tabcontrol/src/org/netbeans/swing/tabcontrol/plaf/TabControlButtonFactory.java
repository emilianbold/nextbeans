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

package org.netbeans.swing.tabcontrol.plaf;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.TabDisplayer;
import org.netbeans.swing.tabcontrol.TabListPopupAction;
import org.netbeans.swing.tabcontrol.WinsysInfoForTabbedContainer;
import org.netbeans.swing.tabcontrol.event.TabActionEvent;
import org.openide.util.ImageUtilities;

/**
 * A factory to create tab control buttons.
 * 
 * @since 1.9
 * @author S. Aubrecht
 */
public class TabControlButtonFactory {
    
    private TabControlButtonFactory() {
    }
    
    public static Icon getIcon( String iconPath ) {
        return ImageUtilities.loadImageIcon( iconPath, true );
    }
    
    /**
     * Create default close button.
     */
    public static TabControlButton createCloseButton( TabDisplayer displayer ) {
        return new CloseButton( displayer );
    }
    
    /**
     * Create button to close the whole window group.
     * @since 1.27
     */
    public static TabControlButton createCloseGroupButton( TabDisplayer displayer ) {
        return new CloseGroupButton( displayer );
    }
    
    /**
     * Create default auto-hide/pin button. The button changes icons depending
     * on the state of tab component.
     */
    public static TabControlButton createSlidePinButton( TabDisplayer displayer ) {
        return new SlidePinButton( displayer );
    }
    
    /**
     * Create default minimize window group button.
     * @since 1.27
     */
    public static TabControlButton createSlideGroupButton( TabDisplayer displayer ) {
        return new SlideGroupButton( displayer );
    }
    
    /**
     * Create button to restore a group of windows from minimized state.
     * @param displayer 
     * @param groupName Name of the group of windows to un-minimize. When the default
     * window system implementation is being used then the group name is the name
     * of TopComponent Mode.
     * @see org.openide.windows.Mode#getName() 
     * @since 1.27
     */
    public static TabControlButton createRestoreGroupButton( TabDisplayer displayer, String groupName ) {
        return new RestoreGroupButton( displayer, groupName );
    }
    
    /**
     * Create default maximize/restore button. The button changes icons depending
     * on the state of tab component.
     */
    public static TabControlButton createMaximizeRestoreButton( TabDisplayer displayer, boolean showBorder ) {
        return new MaximizeRestoreButton( displayer, showBorder );
    }
    
    public static TabControlButton createScrollLeftButton( TabDisplayer displayer, Action scrollAction, boolean showBorder ) {
        TabControlButton button = new TimerButton( TabControlButton.ID_SCROLL_LEFT_BUTTON, displayer, scrollAction, showBorder );
        button.setToolTipText( java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Scroll_Documents_Left") );
        return button;
    }
    
    public static TabControlButton createScrollRightButton( TabDisplayer displayer, Action scrollAction, boolean showBorder ) {
        TabControlButton button = new TimerButton( TabControlButton.ID_SCROLL_RIGHT_BUTTON, displayer, scrollAction, showBorder );
        button.setToolTipText( java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Scroll_Documents_Right") );
        return button;
    }
    
    public static TabControlButton createDropDownButton( TabDisplayer displayer, boolean showBorder ) {
        return new DropDownButton( displayer, showBorder );
    }
    
    private static class CloseButton extends TabControlButton {
        
        public CloseButton( TabDisplayer displayer ) {
            super( TabControlButton.ID_CLOSE_BUTTON, displayer );
            setToolTipText( java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Close_Window") );
        }
        
        @Override
        protected String getTabActionCommand( ActionEvent e ) {
//            if( (e.getModifiers() & ActionEvent.SHIFT_MASK) > 0 )
//                return TabDisplayer.COMMAND_CLOSE_ALL;
//            else if( (e.getModifiers() & ActionEvent.ALT_MASK) > 0 )
//                return TabDisplayer.COMMAND_CLOSE_ALL_BUT_THIS;
            return TabDisplayer.COMMAND_CLOSE;
        }
    }
    
    private static class CloseGroupButton extends TabControlButton {
        
        public CloseGroupButton( TabDisplayer displayer ) {
            super( TabControlButton.ID_CLOSE_BUTTON, displayer );
            setToolTipText( java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Close_Window_Group") );
        }
        
        @Override
        protected String getTabActionCommand( ActionEvent e ) {
            return TabDisplayer.COMMAND_CLOSE_GROUP;
        }
    }
    
    private static class SlidePinButton extends TabControlButton {
        
        public SlidePinButton( TabDisplayer displayer ) {
            super( displayer );
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.registerComponent( this );
        }
        
        @Override
        protected String getTabActionCommand( ActionEvent e ) {
            if( getButtonId() == TabControlButton.ID_PIN_BUTTON )
                return TabDisplayer.COMMAND_DISABLE_AUTO_HIDE;
            return TabDisplayer.COMMAND_ENABLE_AUTO_HIDE;
        }

        @Override
        protected int getButtonId() {
            int retValue = TabControlButton.ID_PIN_BUTTON;
            Component currentTab = getActiveTab( getTabDisplayer() );
            if( null != currentTab ) {
                WinsysInfoForTabbedContainer winsysInfo = getTabDisplayer().getContainerWinsysInfo();
                if( null != winsysInfo ) {
                    Object orientation = winsysInfo.getOrientation( currentTab );
                    if( TabDisplayer.ORIENTATION_EAST.equals( orientation ) ) 
                        retValue = TabControlButton.ID_SLIDE_RIGHT_BUTTON;
                    else if( TabDisplayer.ORIENTATION_WEST.equals( orientation ) ) 
                        retValue = TabControlButton.ID_SLIDE_LEFT_BUTTON;
                    else if( TabDisplayer.ORIENTATION_SOUTH.equals( orientation ) ) 
                        retValue = TabControlButton.ID_SLIDE_DOWN_BUTTON;
                }
            }
            
            return retValue;
        }

        @Override
        public String getToolTipText() {
            if( getButtonId() == TabControlButton.ID_PIN_BUTTON )
                return java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Pin");
            return java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Minimize_Window");
        }
    }
    
    
    private static class SlideGroupButton extends TabControlButton {
        
        public SlideGroupButton( TabDisplayer displayer ) {
            super( displayer );
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.registerComponent( this );
        }
        
        @Override
        protected String getTabActionCommand( ActionEvent e ) {
            return TabDisplayer.COMMAND_MINIMIZE_GROUP;
        }

        @Override
        protected int getButtonId() {
            return TabControlButton.ID_SLIDE_GROUP_BUTTON;
        }

        @Override
        public String getToolTipText() {
            return java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Minimize_Window_Group"); //NOI18N
        }
    }
    
    private static class RestoreGroupButton extends TabControlButton {
        
        private final String groupName;
        
        public RestoreGroupButton( TabDisplayer displayer, String groupName ) {
            super( displayer );
            assert null != groupName;
            this.groupName = groupName;
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.registerComponent( this );
        }
        
        @Override
        protected String getTabActionCommand( ActionEvent e ) {
            return TabDisplayer.COMMAND_RESTORE_GROUP;
        }

        @Override
        protected int getButtonId() {
            return TabControlButton.ID_RESTORE_GROUP_BUTTON;
        }

        @Override
        protected TabActionEvent createTabActionEvent( ActionEvent e ) {
            TabActionEvent res = super.createTabActionEvent( e );
            res.setGroupName( groupName );
            return res;
        }

        @Override
        public String getToolTipText() {
            return java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Restore_Window_Group"); //NOI18N
        }
    }

    private static class MaximizeRestoreButton extends TabControlButton {
        
        public MaximizeRestoreButton( TabDisplayer displayer, boolean showBorder ) {
            super( -1, displayer, showBorder );
            ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
            toolTipManager.registerComponent( this );
        }
        
        @Override
        protected String getTabActionCommand( ActionEvent e ) {
            return TabDisplayer.COMMAND_MAXIMIZE;
        }

        @Override
        protected int getButtonId() {
            int retValue = TabControlButton.ID_MAXIMIZE_BUTTON;
            Component currentTab = getActiveTab( getTabDisplayer() );
            if( null != currentTab ) {
                WinsysInfoForTabbedContainer winsysInfo = getTabDisplayer().getContainerWinsysInfo();
                if( null != winsysInfo ) {
                    if( winsysInfo.inMaximizedMode( currentTab ) ) {
                        retValue = TabControlButton.ID_RESTORE_BUTTON;
                    }
                }
            }
            
            return retValue;
        }

        @Override
        public String getToolTipText() {
            if( getButtonId() == TabControlButton.ID_MAXIMIZE_BUTTON )
                return java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Maximize_Window");
            return java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Restore_Window");
        }
    }

    private static Component getActiveTab( TabDisplayer displayer ) {
        Component res = null;
        int selIndex = displayer.getSelectionModel().getSelectedIndex();
        if( selIndex >= 0 ) {
            TabData tab = displayer.getModel().getTab( selIndex );
            res = tab.getComponent();
        }
        return res;
    }


    /** 
     * A convenience button class which will continue re-firing its action
     * on a timer for as long as the button is depressed.  Used for left-right scroll
     * buttons.
     */
    private static class TimerButton extends TabControlButton implements ActionListener {
        Timer timer = null;

        public TimerButton( int buttonId, TabDisplayer displayer, Action a, boolean showBorder ) {
            super( buttonId, displayer, showBorder );
            setAction( a );
        }

        private Timer getTimer() {
            if (timer == null) {
                timer = new Timer(400, this);
                timer.setRepeats(true);
            }
            return timer;
        }

        int count = 0;

        @Override
        public void actionPerformed( ActionEvent e ) {
            count++;
            if (count > 2) {
                if (count > 5) {
                    timer.setDelay(75);
                } else {
                    timer.setDelay(200);
                }
            }
            performAction();
        }

        private void performAction() {
            if (!isEnabled()) {
                stopTimer();
                return;
            }
            getAction().actionPerformed(new ActionEvent(this,
                                                        ActionEvent.ACTION_PERFORMED,
                                                        getActionCommand()));
        }

        private void startTimer() {
            Timer t = getTimer();
            if (t.isRunning()) {
                return;
            }
            repaint();
            t.setDelay(400);
            t.start();
        }

        private void stopTimer() {
            if (timer != null) {
                timer.stop();
            }
            repaint();
            count = 0;
        }

        @Override
        protected void processMouseEvent(MouseEvent me) {
            if (isEnabled() && me.getID() == me.MOUSE_PRESSED) {
                startTimer();
            } else if (me.getID() == me.MOUSE_RELEASED) {
                stopTimer();
            }
            super.processMouseEvent(me);
        }

        @Override
        protected void processFocusEvent(FocusEvent fe) {
            super.processFocusEvent(fe);
            if (fe.getID() == fe.FOCUS_LOST) {
                stopTimer();
            }
        }

        @Override
        protected String getTabActionCommand(ActionEvent e) {
            return null;
        }
    }

    /**
     * A button for editor tab control to show a list of opened documents.
     */
    private static class DropDownButton extends TabControlButton {
        
        private boolean forcePressedIcon = false;
        
        public DropDownButton( TabDisplayer displayer, boolean showBorder ) {
            super( TabControlButton.ID_DROP_DOWN_BUTTON, displayer, showBorder );
            setAction( new TabListPopupAction( displayer ) );
            setToolTipText( java.util.ResourceBundle.getBundle("org/netbeans/swing/tabcontrol/plaf/Bundle").getString("Tip_Show_Opened_Documents_List") );
        }

        @Override
        protected void processMouseEvent(MouseEvent me) {
            super.processMouseEvent(me);
            if (isEnabled() && me.getID() == me.MOUSE_PRESSED) {
                forcePressedIcon = true;
                repaint();
                getAction().actionPerformed(new ActionEvent(this,
                                                            ActionEvent.ACTION_PERFORMED,
                                                            "pressed"));
            } else if (isEnabled() && me.getID() == me.MOUSE_RELEASED) {
                forcePressedIcon = false;
                repaint();
            }
        }

        @Override
        protected String getTabActionCommand(ActionEvent e) {
            return null;
        }
        
        @Override
        void performAction( ActionEvent e ) {
        }

        @Override
        public Icon getRolloverIcon() {
            if( forcePressedIcon )
                return getPressedIcon();
            
            return super.getRolloverIcon();
        }

        @Override
        public Icon getIcon() {
            if( forcePressedIcon )
                return getPressedIcon();
            
            return super.getIcon();
        }
    }
}
