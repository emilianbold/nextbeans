/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.j2seembedded.platform;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.java.queries.SourceLevelQuery.Profile;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardValidationException;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.loaders.XMLDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Zezula
 * @author Roman Svitanic
 */
final class RemotePlatformNode extends AbstractNode {
    
    
    public RemotePlatformNode(
        @NonNull final RemotePlatform platform,
        @NonNull final XMLDataObject store) {
        super(Children.LEAF, Lookups.fixed(platform, store));
        Parameters.notNull("platform", platform);   //NOI18N
        Parameters.notNull("store", store);         //NOI18N
        setDisplayName(platform.getDisplayName());
        setIconBaseWithExtension("org/netbeans/modules/java/j2seembedded/resources/platform.gif");  //NOI18N
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set setConnection = Sheet.createPropertiesSet();
        setConnection.setName(NbBundle.getMessage(RemotePlatformNode.class, "LBL_ConnectionProperties")); //NOI18N
        setConnection.setDisplayName(NbBundle.getMessage(RemotePlatformNode.class, "LBL_ConnectionProperties")); //NOI18N
        Property property = new PropertySupport.ReadOnly<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_DisplayName"), //NOI18N
                String.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_DisplayName"), //NOI18N
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_DisplayNameDesc")) { //NOI18N
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getPlatform().getDisplayName();
            }
        };
        setConnection.put(property);
        
        property = new PropertySupport.ReadWrite<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Host"), //NOI18N
                String.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_Host"), //NOI18N
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_HostDesc")) { //NOI18N
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getPlatform().getConnectionMethod().getHost();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null) {
                    return;
                }
                updateConnectionMethod(val, null, null, null, null, null);
            }
        };
        setConnection.put(property);

        property = new PropertySupport.ReadWrite<Integer>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Port"), //NOI18N
                Integer.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_Port"), //NOI18N
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_PortDesc")) { //NOI18N
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return getPlatform().getConnectionMethod().getPort();
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null || val <= 0) {
                    return;
                }
                updateConnectionMethod(null, val, null, null, null, null);
            }

            @Override
            public Class<Integer> getValueType() {
                return Integer.class;
            }
        };
        setConnection.put(property);

        property = new PropertySupport.ReadWrite<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Username"), //NOI18N
                String.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_Username"), //NOI18N
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_UsernameDesc")) { //NOI18N
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getPlatform().getConnectionMethod().getAuthentification().getUserName();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null || val.isEmpty()) {
                    return;
                }
                updateConnectionMethod(null, null, val, null, null, null);
            }
        };
        setConnection.put(property);

        if (getPlatform().getConnectionMethod().getAuthentification().getKind() == ConnectionMethod.Authentification.Kind.PASSWORD) {
            property = new PropertySupport.ReadWrite<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Password"), //NOI18N
                    String.class,
                    NbBundle.getMessage(RemotePlatformNode.class, "LBL_Password"), //NOI18N
                    NbBundle.getMessage(RemotePlatformNode.class, "LBL_PasswordDesc")) { //NOI18N
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return ((ConnectionMethod.Authentification.Password) getPlatform().getConnectionMethod().getAuthentification()).getPassword();
                }

                @Override
                public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (val == null || val.isEmpty()) {
                        return;
                    }
                    updateConnectionMethod(null, null, null, val, null, null);
                }

                @Override
                public PropertyEditor getPropertyEditor() {
                    return new PasswordPropertyEditor();
                }
            };
            setConnection.put(property);
        } else if (getPlatform().getConnectionMethod().getAuthentification().getKind() == ConnectionMethod.Authentification.Kind.KEY) {
            property = new PropertySupport.ReadWrite<File>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Keyfile"), //NOI18N
                    File.class,
                    NbBundle.getMessage(RemotePlatformNode.class, "LBL_Keyfile"), //NOI18N
                    NbBundle.getMessage(RemotePlatformNode.class, "LBL_KeyfileDesc")) { //NOI18N
                @Override
                public File getValue() throws IllegalAccessException, InvocationTargetException {
                    return ((ConnectionMethod.Authentification.Key) getPlatform().getConnectionMethod().getAuthentification()).getKeyStore();
                }

                @Override
                public void setValue(File val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (val == null || !val.exists()) {
                        return;
                    }
                    updateConnectionMethod(null, null, null, null, val, null);
                }
            };
            setConnection.put(property);

            property = new PropertySupport.ReadWrite<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Passphrase"), //NOI18N
                    String.class,
                    NbBundle.getMessage(RemotePlatformNode.class, "LBL_Passphrase"), //NOI18N
                    NbBundle.getMessage(RemotePlatformNode.class, "LBL_PassphraseDesc")) { //NOI18N
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return ((ConnectionMethod.Authentification.Key) getPlatform().getConnectionMethod().getAuthentification()).getPassPhrase();
                }

                @Override
                public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (val == null) {
                        return;
                    }
                    updateConnectionMethod(null, null, null, null, null, val);
                }

                @Override
                public PropertyEditor getPropertyEditor() {
                    return new PasswordPropertyEditor();
                }
            };
            setConnection.put(property);
        }
        sheet.put(setConnection);

        Sheet.Set setPlatform = Sheet.createPropertiesSet();
        setPlatform.setName(NbBundle.getMessage(RemotePlatformNode.class, "LBL_PlatformProperties")); //NOI18N
        setPlatform.setDisplayName(NbBundle.getMessage(RemotePlatformNode.class, "LBL_PlatformProperties")); //NOI18N
        property = new PropertySupport.ReadOnly<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_InstallFolder"), //NOI18N
                String.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_InstallFolder"), //NOI18N
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_InstallFolderDesc")) { //NOI18N
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getPlatform().getInstallFolder().getPath();
            }
        };
        setPlatform.put(property);

        property = new PropertySupport.ReadOnly<String>(
                "profile",  //NOI18N
                String.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_Profile"),
                NbBundle.getMessage(RemotePlatformNode.class, "DESC_Profile")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                SourceLevelQuery.Profile profile = Profile.forName(getPlatform().getProperties().get(RemotePlatform.PROP_PROFILE));
                if (profile == null) {
                    profile = SourceLevelQuery.Profile.DEFAULT;
                }
                return profile.getDisplayName();
            }
        };
        setPlatform.put(property);

        property = new PropertySupport.ReadWrite<String>(NbBundle.getMessage(RemotePlatformNode.class, "LBL_Workdir"), //NOI18N
                String.class,
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_Workdir"), //NOI18N
                NbBundle.getMessage(RemotePlatformNode.class, "LBL_WorkdirDesc")) { //NOI18N
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getPlatform().getWorkFolder().getPath();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (val == null || val.isEmpty()) {
                    return;
                }
                URI modifiedURI = null;
                try {
                    modifiedURI = new URI(val);
                } catch (URISyntaxException ex) {
                    // User has entered invalid URI
                    return;
                }
                getPlatform().setWorkFolder(modifiedURI);
            }
        };
        setPlatform.put(property);
        sheet.put(setPlatform);

        Sheet.Set setSysProperties = Sheet.createPropertiesSet();
        setSysProperties.setName(NbBundle.getMessage(RemotePlatformNode.class, "LBL_SysProperties")); //NOI18N
        setSysProperties.setDisplayName(NbBundle.getMessage(RemotePlatformNode.class, "LBL_SysProperties")); //NOI18N
        Iterator<Entry<String, String>> iterator = getPlatform().getSystemProperties().entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, String> entry = iterator.next();
            property = new PropertySupport.ReadOnly<String>(entry.getKey(), String.class, entry.getKey(), entry.getKey()) {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return entry.getValue();
                }
            };
            setSysProperties.put(property);
        }
        sheet.put(setSysProperties);

        return sheet;
    }

    private void updateConnectionMethod(String host, Integer port, String username, String password, File keyFile, String passphrase) {
        ConnectionMethod cm = null;
        if (host == null) {
            host = getPlatform().getConnectionMethod().getHost();
        }
        if (port == null) {
            port = getPlatform().getConnectionMethod().getPort();
        }
        if (username == null) {
            username = getPlatform().getConnectionMethod().getAuthentification().getUserName();
        }
        if (getPlatform().getConnectionMethod().getAuthentification().getKind() == ConnectionMethod.Authentification.Kind.PASSWORD) {
            if (password == null) {
                password = ((ConnectionMethod.Authentification.Password) getPlatform().getConnectionMethod().getAuthentification()).getPassword();
            }
            cm = ConnectionMethod.sshPassword(host, port, username, password);
        } else {
            if (keyFile == null) {
                keyFile = ((ConnectionMethod.Authentification.Key) getPlatform().getConnectionMethod().getAuthentification()).getKeyStore();
            }
            if (passphrase == null) {
                passphrase = ((ConnectionMethod.Authentification.Key) getPlatform().getConnectionMethod().getAuthentification()).getPassPhrase();
            }
            cm = ConnectionMethod.sshKey(host, port, username, keyFile, passphrase);
        }
        getPlatform().setConnectionMethod(cm);
    }

    public static class PasswordPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

        private InplaceEditor editor;

        @Override
        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
        }

        @Override
        public InplaceEditor getInplaceEditor() {
            if (editor == null) {
                editor = new PasswordInplaceEditor();
            }
            return editor;
        }

        @Override
        public String getAsText() {
            if (getValue() == null) {
                return ""; // NOI18N
            }
            String pass = (String) getValue();
            StringBuilder sb = new StringBuilder(pass.length());
            for (int i = 0; i < pass.length(); i++) {
                sb.append("*"); // NOI18N
            }
            return sb.toString();
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            if (text != null) {
                try {
                    setValue(text);
                } catch (Exception ex) {
                    throw new IllegalArgumentException(text);
                }
            }
        }

        private class PasswordInplaceEditor implements InplaceEditor {

            private JPasswordField password;
            private PropertyEditor editor = null;
            private PropertyModel model;

            public PasswordInplaceEditor() {
                this.password = new JPasswordField();
                this.password.setEchoChar('*'); // NOI18N
            }

            @Override
            public void connect(PropertyEditor pe, PropertyEnv env) {
                editor = pe;
                reset();
            }

            @Override
            public JComponent getComponent() {
                return password;
            }

            @Override
            public void clear() {
                editor = null;
                model = null;
            }

            @Override
            public Object getValue() {
                return String.valueOf(password.getPassword());
            }

            @Override
            public void setValue(Object o) {
                if (o instanceof String) {
                    password.setText((String) o);
                }
            }

            @Override
            public boolean supportsTextEntry() {
                return true;
            }

            @Override
            public void reset() {
                String editorValue = (String) editor.getValue();
                if (editorValue != null) {
                    password.setText(editorValue);
                }
            }

            @Override
            public void addActionListener(ActionListener al) {
            }

            @Override
            public void removeActionListener(ActionListener al) {
            }

            @Override
            public KeyStroke[] getKeyStrokes() {
                return new KeyStroke[0];
            }

            @Override
            public PropertyEditor getPropertyEditor() {
                return editor;
            }

            @Override
            public PropertyModel getPropertyModel() {
                return model;
            }

            @Override
            public void setPropertyModel(PropertyModel pm) {
                this.model = pm;
            }

            @Override
            public boolean isKnownComponent(Component c) {
                return c.equals(password) || password.isAncestorOf(c);
            }
        }
    }

    @Override
    public boolean hasCustomizer() {
        return true;
    }

    @Override
    public Component getCustomizer() {
        final JPanel customizer = new JPanel();
        customizer.setLayout(new GridBagLayout());
        final PropertySheet sheet = new PropertySheet();
        sheet.setNodes(new Node[]{this});
        GridBagConstraints c = new GridBagConstraints(
                0,
                0,
                GridBagConstraints.REMAINDER,
                1,
                1.0,
                1.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH,
                new Insets(0,6,6,6),
                0,
                0);
        customizer.add(sheet, c);
        JButton test = new JButton(NbBundle.getMessage(RemotePlatformNode.class, "LBL_TestPlatform"));
        test.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgressUtils.showProgressDialogAndRun(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final RemotePlatform rp = getPlatform();
                                RemotePlatformProbe.verifyPlatform(
                                    rp.getInstallFolder().toString(),
                                    rp.getWorkFolder().toString(),
                                    rp.getConnectionMethod());
                                DialogDisplayer.getDefault().notify(
                                    new NotifyDescriptor.Message(
                                        NbBundle.getMessage(RemotePlatformNode.class, "TXT_CorrectPlatform"),
                                        NotifyDescriptor.INFORMATION_MESSAGE));
                            } catch (WizardValidationException e) {
                                DialogDisplayer.getDefault().notify(
                                    new NotifyDescriptor.Message(
                                        e.getLocalizedMessage(),
                                        NotifyDescriptor.ERROR_MESSAGE));
                            }
                        }
                    },
                    NbBundle.getMessage(RemotePlatformNode.class, "TXT_VerifyingPlatform"));
            }
        });
        c = new GridBagConstraints(
                GridBagConstraints.RELATIVE,
                GridBagConstraints.RELATIVE,
                GridBagConstraints.REMAINDER,
                1,
                0.0,
                0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(0,6,0,6),
                0,
                0);
        customizer.add(test,c);
        return customizer;
    }

    @NonNull
    private RemotePlatform getPlatform() {
        return getLookup().lookup(RemotePlatform.class);
    }

}
