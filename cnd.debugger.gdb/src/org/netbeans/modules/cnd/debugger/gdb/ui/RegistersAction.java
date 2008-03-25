/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.cnd.debugger.gdb.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Registers component.
 */
public class RegistersAction extends AbstractAction {

    public RegistersAction() {
        super(NbBundle.getMessage(RegistersAction.class, "CTL_RegistersAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(RegistersTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = RegistersTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
