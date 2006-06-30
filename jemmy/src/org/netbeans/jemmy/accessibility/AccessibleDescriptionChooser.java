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
 * The Original Software is the Jemmy library.
 * The Initial Developer of the Original Software is Alexandre Iline.
 * All Rights Reserved.
 *
 * Contributor(s): Alexandre Iline.
 *
 * $Id$ $Revision$ $Date$
 *
 */

package org.netbeans.jemmy.accessibility;

import javax.accessibility.AccessibleContext;

import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.Operator.StringComparator;

public class AccessibleDescriptionChooser extends AccessibilityChooser {
    String description;
    StringComparator comparator;
    public AccessibleDescriptionChooser(String description, StringComparator comparator) {
        this.description = description;
        this.comparator = comparator;
    }
    public AccessibleDescriptionChooser(String description) {
        this(description, Operator.getDefaultStringComparator());
    }
    public final boolean checkContext(AccessibleContext context) {
        return(comparator.equals(context.getAccessibleDescription(), description));
    }
    public String getDescription() {
        return("JComponent with \"" + description + "\" accessible description");
    }
}
