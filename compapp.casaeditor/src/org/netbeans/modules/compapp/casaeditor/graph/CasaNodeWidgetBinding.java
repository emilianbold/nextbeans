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

package org.netbeans.modules.compapp.casaeditor.graph;

import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.model.ObjectState;
import org.openide.util.NbBundle;

/**
 *
 * @author Ramesh Dara
 */
public class CasaNodeWidgetBinding extends CasaNodeWidget {
    
    private static final int VERT_TEXT_BAR_WIDTH      = 20;
    private static final int VERT_TEXT_BAR_MIN_HEIGHT = 40;
    private static final int VERT_TEXT_BAR_MAX_CHAR   =  6;
    private static final int VERT_TEXT_BAR_SPACING    =  8;
    
    private static final int PIN_X_START              =  4;
    private static final int PIN_Y_CONSUMES_START     =  4;
    private static final int PIN_Y_PROVIDES_START     = 26;
    
    private static final int NAME_LEFT_EDGE_SPACING   =  0;
    
    private static final int BORDER_WIDTH             =  2;
    
    // The amount of space below the widget's label, 
    // to help visually separate it from other widgets that might
    // be immediately below it.
    private static final int TRAILING_VERTICAL_GAP    =  10;

    private Widget mBodyWidget;
    private ImageWidget mVerticalTextImageWidget;
    private CasaBindingBadges mBadges;
    private LabelWidget mNameWidget;
    private Widget mPinsHolderWidget;
    private String mVertTextBarText;
    
    // Used for determining when we need to regenerate the vertical text image.
    // It must be regenerated any time we alter the node's height.
    private int mPreviousVertTextBarHeight;
    private Widget mHeaderHolder;

    
    public CasaNodeWidgetBinding(Scene scene) {
        super(scene);
        setOpaque(true);
        setBackground(CasaFactory.getCasaCustomizer().getCOLOR_BC_BACKGROUND());
        setLayout(LayoutFactory.createVerticalLayout());
        
        mBadges = new CasaBindingBadges(scene);
        mVerticalTextImageWidget = new ImageWidget(scene);
        mVerticalTextImageWidget.setMinimumSize(new Dimension(VERT_TEXT_BAR_WIDTH, 0));
        
        mHeaderHolder = new Widget(scene);
        mHeaderHolder.setOpaque(true);
        mHeaderHolder.setBackground(CasaFactory.getCasaCustomizer().getCOLOR_BC_TITLE_BACKGROUND());
        mHeaderHolder.setLayout(LayoutFactory.createHorizontalLayout(
                LayoutFactory.SerialAlignment.LEFT_TOP, 0));
        mHeaderHolder.addChild(mBadges.getContainerWidget());
        mHeaderHolder.addChild(mVerticalTextImageWidget);

        mPinsHolderWidget = new Widget(scene);
        mPinsHolderWidget.setLayout(new BindingPinsLayout());
        mPinsHolderWidget.setMinimumSize(new Dimension(44, VERT_TEXT_BAR_MIN_HEIGHT));
        
        mBodyWidget = new Widget(scene);
        mBodyWidget.setOpaque(true);
        mBodyWidget.setLayout(LayoutFactory.createHorizontalLayout(
                LayoutFactory.SerialAlignment.LEFT_TOP, 0));
        mBodyWidget.setBackground(CasaFactory.getCasaCustomizer().getCOLOR_BC_BACKGROUND());
        mBodyWidget.addChild(mHeaderHolder);
        mBodyWidget.addChild(mPinsHolderWidget);
        
        mContainerWidget = new Widget(scene);
        mContainerWidget.setBackground(CasaFactory.getCasaCustomizer().getCOLOR_BC_BACKGROUND());
        mContainerWidget.setLayout(LayoutFactory.createVerticalLayout(
                LayoutFactory.SerialAlignment.CENTER, 0));
        mContainerWidget.addChild(mBodyWidget);
        
        addChild(mContainerWidget);
        
        regenerateHeaderBorder();
    }
    
    
    protected void notifyAdded() {
        super.notifyAdded();
        
        notifyStateChanged(ObjectState.createNormal(), ObjectState.createNormal());
        
        Widget.Dependency verticalTextizer = new Widget.Dependency() {
            public void revalidateDependency() {
                if (
                        getScene().getGraphics() == null || 
                        getBounds() == null ||
                        getParentWidget() == null) {
                    return;
                }
                // Maintain the height of the vertical text bar.
                if (mBodyWidget.getClientArea().getBounds().height != mPreviousVertTextBarHeight) {
                    regenerateVerticalTextBarImage();
                    mPreviousVertTextBarHeight = mBodyWidget.getClientArea().getBounds().height;
                }
            }
        };
        getRegistry().registerDependency(verticalTextizer);
    }
    
    protected void notifyRemoved() {
        super.notifyRemoved();
        
        mNameWidget.removeFromParent();
    }

    public void initializeGlassLayer(LayerWidget layer) {
        mNameWidget = new LabelWidget(getScene());
        mNameWidget.setFont(CasaFactory.getCasaCustomizer().getFONT_BC_LABEL());
        mNameWidget.setForeground(CasaFactory.getCasaCustomizer().getCOLOR_BC_LABEL());
        layer.addChild(mNameWidget);
        
        // Update the name label location if the widget moves.
        Widget.Dependency nameLabeler = new Widget.Dependency() {
            public void revalidateDependency() {
                if (
                        getPreferredLocation() == null ||
                        getBounds() == null ||
                        getParentWidget() == null) {
                    return;
                }
                
                Rectangle nameRect = mNameWidget.getBounds();
                Rectangle nodeRect = getBounds();
                int newX = (nodeRect.width - nameRect.width) / 2;
                
                Point nodeSceneLocation = getParentWidget().convertLocalToScene(getPreferredLocation());
                Point point = new Point(
                        nodeSceneLocation.x + newX, 
                        nodeSceneLocation.y + getBodyHeight() + nameRect.height);
                point.x = point.x < NAME_LEFT_EDGE_SPACING ? NAME_LEFT_EDGE_SPACING : point.x;
                
                mNameWidget.setPreferredLocation(point);
            }
        };
        getRegistry().registerDependency(nameLabeler);
    }
    
    public void setLabelFont(Font font) {
        mNameWidget.setFont(font);
        mNameWidget.revalidate();
        revalidate();
    }
    
    public void setLabelColor(Color color) {
        mNameWidget.setForeground(color);
    }

    protected void notifyStateChanged(ObjectState previousState, ObjectState state) {
        super.notifyStateChanged(previousState, state);
        if (!previousState.isSelected() && state.isSelected()) {
            regenerateHeaderBorder();
        } else if (previousState.isSelected() && !state.isSelected()) {
            regenerateHeaderBorder();
        }
    }
    
    /**
     * Attaches a pin widget to the node widget.
     * @param widget the pin widget
     */
    public void attachPinWidget(CasaPinWidget widget) {
        mPinsHolderWidget.addChild(widget);
    }
    
    /**
     * Returns an anchor for the given pin anchor.
     * Simply returns the same anchor passed-in.
     * @param anchor the original pin anchor
     * @return the extended pin anchor
     */
    protected Anchor createAnchorPin (Anchor pinAnchor) {
        return pinAnchor;
    }
    
    // The height of our widget body (the visible height of the widget excluding the bottom label).
    // We prefer this calculation of height vs. getBounds().height, as getBounds().height
    // is erroneous depending upon the state of scene validation.
    private int getBodyHeight() {
        int bodyHeight = VERT_TEXT_BAR_MIN_HEIGHT;
        bodyHeight = Math.max(bodyHeight, mVerticalTextImageWidget.getPreferredBounds().height);
        if (mPinsHolderWidget.getChildren().size() == 2) {
            CasaPinWidgetBinding bindingPinChild = (CasaPinWidgetBinding) 
                mPinsHolderWidget.getChildren().get(0);
            bodyHeight = Math.max(bodyHeight, bindingPinChild.getPinWidgetBounds().height * 2);
        }
        bodyHeight += BORDER_WIDTH * 2;
        return bodyHeight;
    }
    
    // This includes our main widget body as well as the name label widget.
    public Rectangle getEntireBounds() {
        Point p = getLocation();
        
        Rectangle bounds = new Rectangle(
                Math.min(p.x, mNameWidget.getLocation().x),
                p.y,
                Math.max(getBounds().width, mNameWidget.getBounds().width),
                getBodyHeight() + mNameWidget.getBounds().height + TRAILING_VERTICAL_GAP);

        return bounds;
    }

    public void setEndpointLabel(String nodeName) {
        mNameWidget.setToolTipText(nodeName);
        mNameWidget.setLabel(nodeName);
        // validate to trigger a bounds update
        getScene().validate();
    }
    
    /**
     * Sets all node properties at once.
     * @param image the node image
     * @param nodeName the node name
     * @param nodeType the node type (secondary name)
     * @param glyphs the node glyphs
     */
    public void setNodeProperties(String nodeName, String nodeType) {
        mNameWidget.setToolTipText(nodeName);
        mNameWidget.setLabel(nodeName);
        mVertTextBarText = nodeType;
        revalidate();
        getScene().revalidate();
    }
    
    public void regenerateHeaderBorder() {
        BorderDefinition definition = null;
        if (getState().isSelected()) {
            definition = BorderDefinition.createSelectedDefinition();
            mBodyWidget.setBorder(BorderFactory.createSwingBorder(
                    getScene(), 
                    definition.getBorder()));
            mVerticalTextImageWidget.setBorder(BorderFactory.createSwingBorder(
                    getScene(), 
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, definition.getBorderColor())));
        } else {
            definition = BorderDefinition.createDefaultDefinition();
            mBodyWidget.setBorder(BorderFactory.createSwingBorder(
                    getScene(), 
                    definition.getBorder()));
            mVerticalTextImageWidget.setBorder(BorderFactory.createSwingBorder(
                    getScene(), 
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, definition.getBorderColor())));
        }
    }
    
    public void regenerateVerticalTextBarImage() {
        String displayedText = mVertTextBarText;
        if (mVertTextBarText.length() > VERT_TEXT_BAR_MAX_CHAR) {
            displayedText = displayedText.substring(0, VERT_TEXT_BAR_MAX_CHAR) + NbBundle.getMessage(getClass(), "ELLIPSIS");
        }
        
        Graphics2D sceneGraphics = getScene().getGraphics();
        sceneGraphics.setFont(CasaFactory.getCasaCustomizer().getFONT_BC_HEADER());
        FontMetrics fm = sceneGraphics.getFontMetrics(CasaFactory.getCasaCustomizer().getFONT_BC_HEADER());
        int fontLength = fm.stringWidth(displayedText);

        int barHeight = fontLength < VERT_TEXT_BAR_MIN_HEIGHT ? VERT_TEXT_BAR_MIN_HEIGHT : fontLength;
        barHeight += VERT_TEXT_BAR_SPACING;
        
        // This image must support the alpha channel, which provides transparency.
        BufferedImage image = new BufferedImage(
                VERT_TEXT_BAR_WIDTH, 
                barHeight, 
                BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D graphics = image.createGraphics();
        
        // The RGB values don't matter, as this color is 100% transparent.
        graphics.setColor(new Color(255, 255, 255, 0));
        
        graphics.fill(new Rectangle(0, 0, VERT_TEXT_BAR_WIDTH, barHeight));
        graphics.setFont(CasaFactory.getCasaCustomizer().getFONT_BC_HEADER());
        graphics.setPaint(CasaFactory.getCasaCustomizer().getCOLOR_BC_TITLE());
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        int characterHeight = fm.getAscent() + fm.getDescent();
        int textOffset = textOffset = (barHeight - fontLength) / 2;
        
        graphics.translate(characterHeight, textOffset + fontLength);
        graphics.rotate(-Math.toRadians(90.0));
        graphics.drawString(displayedText, 0, -fm.getDescent());
        graphics.rotate(Math.toRadians(90.0));
        graphics.translate(-characterHeight, -(textOffset + fontLength));
        
        graphics.dispose();
        
        Rectangle iconsRect = mBadges.getContainerWidget().getPreferredBounds();
        iconsRect.height = barHeight;
        mBadges.getContainerWidget().setPreferredBounds(iconsRect);

        mVerticalTextImageWidget.setImage(image);
    }

    public void setEditable(boolean bValue) {
        super.setEditable(bValue);
        mBadges.setBadge(CasaBindingBadges.Badge.IS_EDITABLE, bValue);
    }

    public void setWSPolicyAttached(boolean bValue) {
        super.setWSPolicyAttached(bValue);
        mBadges.setBadge(CasaBindingBadges.Badge.WS_POLICY, bValue);
    }

    public void setBackgroundColor(Color color) {
        setBackground(color);
        mBodyWidget.setBackground(CasaFactory.getCasaCustomizer().getCOLOR_BC_BACKGROUND());
        mContainerWidget.setBackground(CasaFactory.getCasaCustomizer().getCOLOR_BC_BACKGROUND());
    }
    
    public void setTitleBackgroundColor(Color color) {
        mHeaderHolder.setBackground(color);
        mHeaderHolder.repaint();
    }
    
    public CasaBindingBadges getBadges() {
        return mBadges;
    }
    
    
        
    private static class BindingPinsLayout implements Layout {
        public void layout(Widget widget) {
            CasaPinWidgetBindingConsumes consumesPin = null;
            CasaPinWidgetBindingProvides providesPin = null;
            for (Widget child : widget.getChildren()) {
                if        (child instanceof CasaPinWidgetBindingConsumes) {
                    consumesPin = (CasaPinWidgetBindingConsumes) child;
                } else if (child instanceof CasaPinWidgetBindingProvides) {
                    providesPin = (CasaPinWidgetBindingProvides) child;
                }
            }
            if (consumesPin != null) {
                // The Consumes pin is on the top.
                consumesPin.resolveBounds(new Point(PIN_X_START, PIN_Y_CONSUMES_START), null);
            }
            if (providesPin != null) {
                // The Provides pin is on the bottom.
                providesPin.resolveBounds(new Point(PIN_X_START, PIN_Y_PROVIDES_START), null);
            }
        }
        public boolean requiresJustification(Widget widget) {
            return false;
        }
        public void justify(Widget widget) {}
    }
    
    
    private static class BorderDefinition {
        private Border mBorder;
        private Color mBorderColor;
        private BorderDefinition(Color borderColor) {
            mBorderColor = borderColor;
            mBorder = javax.swing.BorderFactory.createMatteBorder(
                    BORDER_WIDTH,
                    BORDER_WIDTH,
                    BORDER_WIDTH,
                    0,
                    borderColor);
        }
        public Border getBorder() {
            return mBorder;
        }
        public Color getBorderColor() {
            return mBorderColor;
        }
        public static BorderDefinition createDefaultDefinition() {
            return new BorderDefinition(
                CasaFactory.getCasaCustomizer().getCOLOR_BC_BORDER());
        }
        public static BorderDefinition createSelectedDefinition() {
            return new BorderDefinition(
                CasaFactory.getCasaCustomizer().getCOLOR_SELECTION());
        }
    }
}
