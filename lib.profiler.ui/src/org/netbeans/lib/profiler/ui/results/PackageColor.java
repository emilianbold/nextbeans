/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.lib.profiler.ui.results;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Objects;
import javax.swing.Icon;

/**
 *
 * @author Jiri Sedlacek
 */
public final class PackageColor {
    
    private String name;
    private String value;
    private String[] values;
    private Color color;
    
    private Icon icon;
    
    
    public PackageColor(PackageColor other) {
        this(other.name, other.value, other.values, other.color);
    }
    
    public PackageColor(String name, String value, Color color) {
        this(name, value, null, color);
    }

    private PackageColor(String name, String value, String[] values, Color color) {
        this.name = name;
        this.value = value;
        this.values = values;
        this.color = color;
    }

    public final void setName(String name) { this.name = name; }
    public final String getName() { return name; }
    
    public final void setValue(String value) { this.value = value; values = null; }
    public final String getValue() { return value; }

//    public final void setValues(String[] values) { this.values = values; }
    public final String[] getValues() { if (values == null) values = values(value); return values; }

    public final void setColor(Color color) { this.color = color; }
    public final Color getColor() { return color; }
    
    
    public final Icon getIcon(int width, int height) {
        if (icon == null || icon.getIconWidth() != width || icon.getIconHeight() != height) {
            final int w = Math.max(16, width);
            final int h = Math.max(16, height);
            final int ww = width;
            final int hh = height;
            final int wo = ww >= 16 ? 0 : (16 - ww) / 2;
            final int ho = hh >= 16 ? 0 : (16 - hh) / 2;
            icon = new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    if (color == null) {
                        g.setColor(Color.BLACK);
                        g.drawLine(x + wo, y + ho, x + wo + ww, y + ho + hh);
                        g.drawLine(x + wo + ww, y + ho, x + wo, y + ho + hh);
                        g.drawRect(x + wo, y + ho, ww, hh);
                    } else {
                        g.setColor(color);
                        g.fillRect(x + wo, y + ho, ww, hh);
                        g.setColor(Color.BLACK);
                        g.drawRect(x + wo, y + ho, ww, hh);
                    }
                }
                public int getIconWidth() {
                    return w;
                }
                public int getIconHeight() {
                    return h;
                }
            };
        }
        return icon;
    }


    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (getClass() != obj.getClass()) return false;

        final PackageColor other = (PackageColor)obj;
        if (!name.equals(other.name)) return false;
        if (!value.equals(other.value)) return false;
        if (!Objects.equals(color, other.color)) return false;

        return true;
    }

    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + name.hashCode();
        hash = 67 * hash + value.hashCode();
        hash = 67 * hash + (color == null ? 0 : color.hashCode());
        return hash;
    }
    
    
//    private static String value(String[] values) {
//        int length = values.length;
//
//        if (length == 0) return ""; // NOI18N
//        if (length == 1) return values[0];
//
//        StringBuilder b = new StringBuilder();
//        for (int i = 0; i < length - 1; i++)
//            b.append(values[i]).append(", "); // NOI18N
//        b.append(values[values.length - 1]);
//
//        return b.toString().trim();
//    }
    
    private static String[] values(String value) {
        return value.replace(',', ' ').split(" +"); // NOI18N
    }
    
}
