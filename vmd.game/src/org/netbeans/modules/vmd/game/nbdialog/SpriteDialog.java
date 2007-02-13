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

package org.netbeans.modules.vmd.game.nbdialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.vmd.game.GameController;
import org.netbeans.modules.vmd.game.dialog.PartialImageGridPreview;
import org.netbeans.modules.vmd.game.model.GlobalRepository;
import org.netbeans.modules.vmd.game.model.ImageResource;
import org.netbeans.modules.vmd.game.model.Scene;
import org.netbeans.modules.vmd.midp.components.MidpProjectSupport;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Utilities;

/**
 *
 * @author  kherink
 */
public class SpriteDialog extends javax.swing.JPanel implements ActionListener {
	
    private static final Icon ICON_ERROR = new ImageIcon(Utilities.loadImage("org/netbeans/modules/vmd/midp/resources/error.gif"));

	/** Creates new form NewTiledLayerDialog */
	public SpriteDialog() {
		initComponents();
		init();
	}
	
	public SpriteDialog(Scene parent) {
		this();
		this.scene = parent;
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupLayers = new javax.swing.ButtonGroup();
        panelCustomizer = new javax.swing.JPanel();
        labelImageFile = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listImageFileName = new javax.swing.JList();
        panelPreview = new javax.swing.JPanel();
        labelImagePreview = new javax.swing.JLabel();
        panelImage = new javax.swing.JPanel();
        sliderWidth = new javax.swing.JSlider();
        sliderHeight = new javax.swing.JSlider();
        labelTileWidth = new javax.swing.JLabel();
        labelTileHeight = new javax.swing.JLabel();
        panelError = new javax.swing.JPanel();
        labelError = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        panelLayerInfo = new javax.swing.JPanel();
        labelLayerName = new javax.swing.JLabel();
        fieldLayerName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        spinnerFrames = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JSeparator();

        labelImageFile.setText("Select image:");

        listImageFileName.setModel(this.getImageListModel());
        listImageFileName.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(listImageFileName);

        org.jdesktop.layout.GroupLayout panelCustomizerLayout = new org.jdesktop.layout.GroupLayout(panelCustomizer);
        panelCustomizer.setLayout(panelCustomizerLayout);
        panelCustomizerLayout.setHorizontalGroup(
            panelCustomizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelCustomizerLayout.createSequentialGroup()
                .add(panelCustomizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labelImageFile)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelCustomizerLayout.setVerticalGroup(
            panelCustomizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelCustomizerLayout.createSequentialGroup()
                .add(labelImageFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );

        labelImagePreview.setText("Adjust tile size in pixels:");

        panelImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        this.panelImage.add(this.imagePreview, BorderLayout.CENTER);
        panelImage.setLayout(new java.awt.BorderLayout());

        sliderHeight.setOrientation(javax.swing.JSlider.VERTICAL);

        labelTileWidth.setText("Tile width: 0 px");

        labelTileHeight.setText("Tile height: 0 px");

        org.jdesktop.layout.GroupLayout panelPreviewLayout = new org.jdesktop.layout.GroupLayout(panelPreview);
        panelPreview.setLayout(panelPreviewLayout);
        panelPreviewLayout.setHorizontalGroup(
            panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labelImagePreview)
                    .add(panelPreviewLayout.createSequentialGroup()
                        .add(labelTileWidth)
                        .add(40, 40, 40)
                        .add(labelTileHeight)
                        .addContainerGap(116, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, panelPreviewLayout.createSequentialGroup()
                        .add(panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, sliderWidth, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .add(panelImage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sliderHeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        panelPreviewLayout.setVerticalGroup(
            panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, panelPreviewLayout.createSequentialGroup()
                .add(labelImagePreview)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(sliderHeight, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(panelImage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sliderWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                .add(panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelTileHeight)
                    .add(labelTileWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        labelError.setForeground(new java.awt.Color(255, 0, 0));

        org.jdesktop.layout.GroupLayout panelErrorLayout = new org.jdesktop.layout.GroupLayout(panelError);
        panelError.setLayout(panelErrorLayout);
        panelErrorLayout.setHorizontalGroup(
            panelErrorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, labelError, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
        panelErrorLayout.setVerticalGroup(
            panelErrorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelErrorLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .add(labelError, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        labelLayerName.setLabelFor(fieldLayerName);
        labelLayerName.setText("Sprite name:");

        jLabel1.setText("Number of frames:");

        org.jdesktop.layout.GroupLayout panelLayerInfoLayout = new org.jdesktop.layout.GroupLayout(panelLayerInfo);
        panelLayerInfo.setLayout(panelLayerInfoLayout);
        panelLayerInfoLayout.setHorizontalGroup(
            panelLayerInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelLayerInfoLayout.createSequentialGroup()
                .add(panelLayerInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(panelLayerInfoLayout.createSequentialGroup()
                        .add(labelLayerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(42, 42, 42))
                    .add(panelLayerInfoLayout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(panelLayerInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelLayerInfoLayout.createSequentialGroup()
                        .add(spinnerFrames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(481, Short.MAX_VALUE))
                    .add(fieldLayerName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)))
        );
        panelLayerInfoLayout.setVerticalGroup(
            panelLayerInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, panelLayerInfoLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(panelLayerInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelLayerName)
                    .add(fieldLayerName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelLayerInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(spinnerFrames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panelError, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panelLayerInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(panelCustomizer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelPreview, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(panelLayerInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panelCustomizer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelError, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLayers;
    private javax.swing.JTextField fieldLayerName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelImageFile;
    private javax.swing.JLabel labelImagePreview;
    private javax.swing.JLabel labelLayerName;
    private javax.swing.JLabel labelTileHeight;
    private javax.swing.JLabel labelTileWidth;
    private javax.swing.JList listImageFileName;
    private javax.swing.JPanel panelCustomizer;
    private javax.swing.JPanel panelError;
    private javax.swing.JPanel panelImage;
    private javax.swing.JPanel panelLayerInfo;
    private javax.swing.JPanel panelPreview;
    private javax.swing.JSlider sliderHeight;
    private javax.swing.JSlider sliderWidth;
    private javax.swing.JSpinner spinnerFrames;
    // End of variables declaration//GEN-END:variables
	
	
	private DialogDescriptor dd;
	
	public static final boolean DEBUG = false;
	
	private SliderListener sliderListener = new SliderListener();
	private PartialImageGridPreview imagePreview = new PartialImageGridPreview();
	
	private Scene scene;
	
	private List<Integer> tileWidths;
    private List<Integer> tileHeigths;
	
	
	public void setDialogDescriptor(DialogDescriptor dd) {
		this.dd = dd;
	}
	
	private void init() {
		this.labelError.setIcon(ICON_ERROR);
		
		SpinnerNumberModel snm = new SpinnerNumberModel();
		snm.setMinimum(1);
		snm.setMaximum(256);
		snm.setStepSize(1);
		snm.setValue(5);
		this.spinnerFrames.setModel(snm);
		
		this.panelImage.add(this.imagePreview, BorderLayout.CENTER);
		this.fieldLayerName.getDocument().addDocumentListener(new LayerFieldListener());
		this.fieldLayerName.addFocusListener(new LayerFieldListener());
		
		this.listImageFileName.addListSelectionListener(new ImageListListener());
		this.listImageFileName.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList src, Object value, int index, boolean isSelected, boolean hasfocus) {
				Map.Entry<FileObject, String> entry = (Map.Entry<FileObject, String>) value;
                return super.getListCellRendererComponent(src, entry.getValue(), index, isSelected, hasfocus);
            }
		});
		
		this.sliderWidth.setModel(new DefaultBoundedRangeModel());
		this.sliderHeight.setModel(new DefaultBoundedRangeModel());
		
		this.sliderWidth.addChangeListener(sliderListener);
		this.sliderHeight.addChangeListener(sliderListener);
		
		this.sliderWidth.setValue(0);
		this.sliderHeight.setValue(0);

		this.sliderWidth.setPaintLabels(true);
		this.sliderHeight.setPaintLabels(true);
		
		this.sliderWidth.setSnapToTicks(true);
		this.sliderHeight.setSnapToTicks(true);
		
		this.sliderWidth.setEnabled(false);
		this.sliderHeight.setEnabled(false);
	}
	
	private List<Map.Entry<FileObject, String>> getImageList() {
		Map<FileObject, String> imgMap = MidpProjectSupport.getImagesForProject(GameController.getDesignDocument(), true);
		List<Map.Entry<FileObject, String>> list = new ArrayList<Map.Entry<FileObject, String>>();
		list.addAll(imgMap.entrySet());
		Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
				Map.Entry<FileObject, String> ea = (Map.Entry<FileObject, String>) a;
				Map.Entry<FileObject, String> eb = (Map.Entry<FileObject, String>) b;
				return ea.getValue().compareTo(eb.getValue());
			}
		});
		return list;
	}
	
	private DefaultListModel getImageListModel() {
		DefaultListModel dlm = new DefaultListModel();
		List<Map.Entry<FileObject, String>> images = this.getImageList();
		for (Map.Entry<FileObject, String> imageEntry : images) {
			dlm.addElement(imageEntry);
		}
		return dlm;
	}	
	
	private class SliderListener implements ChangeListener {
		
		public void stateChanged(ChangeEvent e) {
			int tileWidth = SpriteDialog.this.tileWidths.get(((Integer) SpriteDialog.this.sliderWidth.getValue()).intValue());
			int tileHeight = SpriteDialog.this.tileHeigths.get(((Integer) SpriteDialog.this.sliderHeight.getValue()).intValue());
			
			if (e.getSource() == SpriteDialog.this.sliderHeight) {
				SpriteDialog.this.imagePreview.setTileHeight(tileHeight);
				SpriteDialog.this.labelTileHeight.setText("Tile height: " + tileHeight + " px");
			} 
			else if (e.getSource() == SpriteDialog.this.sliderWidth) {
				SpriteDialog.this.imagePreview.setTileWidth(tileWidth);
				SpriteDialog.this.labelTileWidth.setText("Tile width: " + tileWidth + " px");
			} 
			else {
				if (DEBUG) System.out.println("ERR: Spinner event came from " + e.getSource());
			}
		}
		
	}
	
	private class LayerFieldListener implements DocumentListener, FocusListener {
		public void insertUpdate(DocumentEvent e) {
			this.handleTextContentChange(e);
		}
		public void removeUpdate(DocumentEvent e) {
			this.handleTextContentChange(e);
		}
		public void changedUpdate(DocumentEvent e) {
			this.handleTextContentChange(e);
		}
		private void handleTextContentChange(DocumentEvent e) {
			String err = getFieldLayerNameError();
			if (e.getDocument() == SpriteDialog.this.fieldLayerName.getDocument()) {
				if (err == null) {
					err = getFieldImageFileNameError();
				}
				SpriteDialog.this.labelError.setText(err);
			}
			if (err == null) {
				SpriteDialog.this.setOKButtonEnabled(true);
			}
			else {
				SpriteDialog.this.setOKButtonEnabled(false);
			}
		}
		
		public void focusGained(FocusEvent e) {
			if (e.getComponent() == SpriteDialog.this.fieldLayerName) {
				SpriteDialog.this.labelError.setText(getFieldLayerNameError());
			}
			if (getFieldLayerNameError() == null && getFieldImageFileNameError() == null)
				SpriteDialog.this.setOKButtonEnabled(true);
			else
				SpriteDialog.this.setOKButtonEnabled(false);
		}
		public void focusLost(FocusEvent e) {
		}
	}
	
	private String getFieldLayerNameError() {
		String illegalIdentifierName = "Layer name must be a valid Java identifier.";
		String errMsg = null;
		String layerName = this.fieldLayerName.getText();
		if (layerName.equals("")) {
			errMsg = "Enter layer name.";
		} 
		else if (GlobalRepository.getInstance().getLayerByName(layerName) != null) {
			errMsg = "Layer already exists. Choose a different name.";
		}
		else if (!isValidJavaIdentifier(layerName)) {
			errMsg = illegalIdentifierName;
		}
		return errMsg;
	}
	
	private static boolean isValidJavaIdentifier(String str) {
		if (!Character.isJavaIdentifierStart(str.charAt(0))) {
			return false;
		}
		for (int i = 1; i < str.length(); i++) {
			if (!Character.isJavaIdentifierPart(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	
	public void setOKButtonEnabled(boolean enable) {
		if (!enable) {
			this.labelError.setIcon(ICON_ERROR);
		}
		else {
			this.labelError.setIcon(null);
		}
		this.dd.setValid(enable);
	}
	
	private String getFieldImageFileNameError() {
		String errMsg = null;
		if (this.listImageFileName.getModel().getSize() == 0) {
			errMsg = "There are no images available in the project. First add an image resource to the project.";
		} 
		else if (this.listImageFileName.getSelectedValue() == null) {
			errMsg = "Select image file.";
		}
		return errMsg;
	}
	
	private class ImageListListener implements ListSelectionListener {
		
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			this.handleImageSelectionChange();
		}
		
		private void handleImageSelectionChange() {
			SpriteDialog.this.sliderWidth.setEnabled(true);
			SpriteDialog.this.sliderHeight.setEnabled(true);
			
			String errMsg = SpriteDialog.this.getFieldImageFileNameError();
			if (errMsg == null) {
				errMsg = SpriteDialog.this.getFieldLayerNameError();
				SpriteDialog.this.loadImagePreview();
			}
			
			if (errMsg != null) {
				SpriteDialog.this.labelError.setText(errMsg);
				SpriteDialog.this.setOKButtonEnabled(false);
			} 
			else {
				SpriteDialog.this.labelError.setText("");
				SpriteDialog.this.setOKButtonEnabled(true);
			}
		}
	}
	
	private void loadImagePreview() {
		if (DEBUG) System.out.println("load image preview");
		
		Map.Entry<FileObject, String> entry = (Map.Entry<FileObject, String>) this.listImageFileName.getSelectedValue();
		URL imageURL = null;
		try {
			imageURL = entry.getKey().getURL();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		assert(imageURL != null);
		try {
			this.sliderWidth.removeChangeListener(this.sliderListener);
			this.sliderHeight.removeChangeListener(this.sliderListener);
			
			this.imagePreview.setImage(imageURL);
			
			this.tileWidths = this.imagePreview.getValidTileWidths();
			this.tileHeigths = this.imagePreview.getValidTileHeights();
			
			DefaultBoundedRangeModel modelWidth = new DefaultBoundedRangeModel(tileWidths.size() -1, 0, 0, tileWidths.size() -1);
			DefaultBoundedRangeModel modelHeight = new DefaultBoundedRangeModel(tileHeigths.size() -1, 0, 0, tileHeigths.size() -1);
			this.sliderWidth.setModel(modelWidth);
			this.sliderHeight.setModel(modelHeight);			
			
			//set labels
			int tileWidth = this.tileWidths.get(((Integer) this.sliderWidth.getValue()).intValue());
			int tileHeight = this.tileHeigths.get(((Integer) this.sliderHeight.getValue()).intValue());
			this.labelTileHeight.setText("Tile height: " + tileHeight + " px");
			this.labelTileWidth.setText("Tile width: " + tileWidth + " px");

			this.repaint();
			
			this.sliderWidth.addChangeListener(sliderListener);
			this.sliderHeight.addChangeListener(sliderListener);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		//if OK button pressed create the new layer
		if (e.getSource() == NotifyDescriptor.OK_OPTION) {
			this.handleOKButton();
		}
	}
	
	private void handleOKButton() {
		String name = this.fieldLayerName.getText();
		
		int tileWidth = SpriteDialog.this.tileWidths.get(((Integer) SpriteDialog.this.sliderWidth.getValue()).intValue());
		int tileHeight = SpriteDialog.this.tileHeigths.get(((Integer) SpriteDialog.this.sliderHeight.getValue()).intValue());
		
		Map.Entry<FileObject, String> entry = (Map.Entry<FileObject, String>) this.listImageFileName.getSelectedValue();
		
		URL imageURL = null;
		try {
			imageURL = entry.getKey().getURL();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String relativeResourcePath = entry.getValue();
		
		assert (imageURL != null);
		assert (relativeResourcePath != null);
		
		ImageResource imgRes = GlobalRepository.getInstance().getImageResource(imageURL, relativeResourcePath, tileWidth, tileHeight);
		
		if (this.scene != null) {
			this.scene.createSprite(name, imgRes, (Integer) this.spinnerFrames.getValue());
		}
		else {
			GlobalRepository.getInstance().createSprite(name, imgRes, (Integer) this.spinnerFrames.getValue());
		}
	}

}

