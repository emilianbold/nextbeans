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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.schema.completion;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.HashMap;
import java.text.AttributedString;
import javax.swing.*;
import org.netbeans.modules.xml.axi.AbstractAttribute;
import org.netbeans.modules.xml.axi.Attribute;
import org.netbeans.modules.xml.schema.model.Attribute.Use;

/**
 *
 * @author Samaresh (Samaresh.Panda@Sun.Com)
 */
public abstract class CompletionPaintComponent extends JPanel {

    /**
     * Creates a new instance of CompletionPaintComponent
     */
    public CompletionPaintComponent(CompletionResultItem item) {
        this.completionItem = item;
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    
    public  boolean isSelected() {
        return isSelected;
    }
    
    public void paintComponent(Graphics g) {
        // clear background
        g.setColor(getBackground());
        java.awt.Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);
        draw(g);
    }
        
    protected void draw(Graphics g) {
        drawIcon(g, completionItem.getIcon());
        drawString(g, completionItem.getItemText(), completionItem.getPaintColor(),
            getDrawFont(), false);
    }

    /**
     * Draw the icon if it is valid for the given type.
     * Here the initial drawing assignments are also done.
     */
    protected void drawIcon(Graphics g, Icon icon) {
        Insets i = getInsets();
        if (i != null) {
            drawX = i.left;
            drawY = i.top;
        } else {
            drawX = 0;
            drawY = 0;
        }

        if (icon != null) {
            if (g != null) {
                icon.paintIcon(this, g, drawX, drawY);
            }
            drawX += icon.getIconWidth() + iconTextGap;
            drawHeight = Math.max(fontHeight, icon.getIconHeight());
        } else {
            drawHeight = fontHeight;
        }
        if (i != null) {
            drawHeight += i.bottom;
        }
        drawHeight += drawY;
        drawY += ascent;
    }

    /**
     * Draw string using the foreground color
     */
    protected void drawString(Graphics g, String s, boolean strike) {
        if (g != null) {
            g.setColor(getForeground());
        }
        drawStringToGraphics(g, s, null, strike);
    }


    /**
     * Draw string with given color which is first possibly modified
     * by calling getColor() method to care about selection etc.
     */
    protected void drawString(Graphics g, String s, Color c) {
        if (g != null) {
            g.setColor(getColor(s, c));
        }
        drawStringToGraphics(g, s, getDrawFont(), false);
    }

    protected void drawString(Graphics g, String s, Color c, Font font, boolean strike) {
        if (g != null) {
            g.setColor(getColor(s, c));
            g.setFont(font);
        }
        drawStringToGraphics(g, s, font,  strike);
        if (g != null) {
            g.setFont(drawFont);
        }

    }
    
    protected void drawTypeName(Graphics g, String s, Color c) {
        if (g == null) {
            drawString(g, "   ", false); // NOI18N
            drawString(g, s, c);
        } else {
            int w = getWidth() - getWidth(s) - drawX;
            int spaceWidth = getWidth(" "); // NOI18N
            if (w > spaceWidth * 2) {
                drawX = getWidth() - 2 * spaceWidth - getWidth(s);
            } else {
                drawX = getWidth() - 2 * spaceWidth - getWidth(s) - getWidth("..   "); // NOI18N
                g.setColor(getBackground());
                g.fillRect(drawX, 0, getWidth() - drawX, getHeight());
                drawString(g, "..   ", c); // NOI18N
            }
            drawString(g, s, c);
        }
    }

    protected void drawStringToGraphics(Graphics g, String s, Font font, boolean strike) {
        if (g != null) {
            if (!strike){
                g.drawString(s, drawX, drawY);
            } else {
                Graphics2D g2 = ((Graphics2D)g);
                AttributedString strikeText = new AttributedString(s);
                strikeText.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                strikeText.addAttribute(TextAttribute.FONT, g.getFont());
                g2.drawString(strikeText.getIterator(), drawX, drawY);
            }
        }
        drawX += getWidth(s, font);
    }

    protected int getWidth(String s) {
        Integer i = (Integer)widths.get(s);
        if (i != null) {
            return i.intValue();
        } else {
            if (s == null) {
                s = "";
            }
            return fontMetrics.stringWidth(s);
        }
    }

    protected int getWidth(String s, Font font) {
        if (font == null) return getWidth(s);
        return getFontMetrics(font).stringWidth(s);
    }

    protected Color getColor(String s, Color defaultColor) {
        return isSelected ? getForeground()
        : defaultColor;
    }

    private void storeWidth(String s) {
        fontMetrics.stringWidth(s);
    }

    public void setFont(Font font) {
        super.setFont(font);

        fontMetrics = this.getFontMetrics(font);
        fontHeight = fontMetrics.getHeight();
        ascent = fontMetrics.getAscent();
        if (widths != null) {
            widths.clear();
        } else {
            widths = new HashMap();
        }
        for (int i = 0; i < frequentWords.length; i++) {
            storeWidth(frequentWords[i]);
        }
        drawFont = font;
    }

    protected Font getDrawFont(){
        return drawFont;
    }

    public Dimension getPreferredSize() {
        draw(null);
        Insets i = getInsets();
        if (i != null) {
            drawX += i.right;
        }
        if (drawX > getMaximumSize().width)
            drawX = getMaximumSize().width;
        return new Dimension(drawX, drawHeight);
    }
    
    CompletionResultItem getCompletionItem() {
        return completionItem;
    }
    
    public static class AttributePaintComponent extends CompletionPaintComponent {        
        public AttributePaintComponent(CompletionResultItem item) {
            super(item);
        }
        
        protected Font getDrawFont() {
            AbstractAttribute aa = (AbstractAttribute)getCompletionItem().
                    getAXIComponent();
            if(aa instanceof Attribute) {
                if(((Attribute)aa).getUse() == Use.REQUIRED) {
                    return super.getDrawFont().deriveFont(Font.BOLD);
                }
            }
            
            return super.getFont();
        }        
    }
    
    public static class ElementPaintComponent extends CompletionPaintComponent {
        public ElementPaintComponent(CompletionResultItem item) {
            super(item);
        }        
    }
    
    protected int drawX;
    protected int drawY;
    protected int drawHeight;
    private Font drawFont;
    private int iconTextGap = 5;
    private int fontHeight;
    private int ascent;
    private Map widths;
    private FontMetrics fontMetrics;
    private boolean isSelected;
    private CompletionResultItem completionItem;
    
    private static final String THROWS = " throws "; // NOI18N
    private static String str; //completion item text
    private static final String[] frequentWords = new String[] {
        "", " ", "[]", "(", ")", ", ", "String", THROWS // NOI18N
    };
}
