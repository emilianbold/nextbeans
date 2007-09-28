/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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



package org.netbeans.modules.uml.ui.products.ad.viewfactory;

import java.awt.Graphics;
import java.util.List;

import org.netbeans.modules.uml.common.ETException;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.metamodel.core.foundation.INamespace;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IPresentationElement;
import org.netbeans.modules.uml.core.support.umlsupport.IETPoint;
import org.netbeans.modules.uml.core.support.umlsupport.IETRect;
import org.netbeans.modules.uml.core.support.umlsupport.IStrings;
import org.netbeans.modules.uml.ui.products.ad.drawengines.ETDrawEngineFactory;
import org.netbeans.modules.uml.ui.products.ad.graphobjects.ETEdgeLabel;
import org.netbeans.modules.uml.ui.support.ElementReloader;
import org.netbeans.modules.uml.ui.support.applicationmanager.DrawingFactory;
import org.netbeans.modules.uml.ui.support.applicationmanager.IGraphPresentation;
import org.netbeans.modules.uml.ui.support.applicationmanager.ILabelPresentation;
import org.netbeans.modules.uml.ui.support.archivesupport.IProductArchive;
import org.netbeans.modules.uml.ui.support.archivesupport.IProductArchiveDefinitions;
import org.netbeans.modules.uml.ui.support.archivesupport.IProductArchiveElement;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IDrawEngine;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IDrawInfo;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.IETGraphObject;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.ITSGraphObject;
import org.netbeans.modules.uml.ui.support.viewfactorysupport.TypeConversions;
import org.netbeans.modules.uml.ui.swing.drawingarea.IDrawingAreaControl;
import com.tomsawyer.editor.TSEEdgeLabel;
import com.tomsawyer.editor.TSEGraphWindow;
import com.tomsawyer.editor.TSEObjectUI;
import com.tomsawyer.editor.graphics.TSEGraphics;
import com.tomsawyer.editor.ui.TSELabelUI;
import com.tomsawyer.graph.TSGraphObject;
import com.tomsawyer.util.TSProperty;

public class ETGenericEdgeLabelUI extends TSELabelUI implements IETGraphObjectUI
{

	private final String ZERO_VALUE = "";

	private String peidValue = this.ZERO_VALUE;
	private String meidValue = this.ZERO_VALUE;
	private String topLevelMEIDValue = this.ZERO_VALUE;
	private String initStringValue = this.ZERO_VALUE;
	private String m_ReloadedOwnerPresentationXMIID = this.ZERO_VALUE;

	private IElement modelElement = null;
	private IDrawEngine drawEngine = null;
	private String drawEngineClass = null;
	private IStrings m_PresentationReferenceReferredElements = null;
	private boolean m_WasModelElementDeleted = false;
	private boolean m_FailedToCreateDrawEngine = false;

	private int m_Placement = 0;
	private int m_LabelKind = 0;
	/// Logical offset from the top left of the draw engine for the location of the label
	private IETPoint m_ptSpecifiedXY;

	public ETGenericEdgeLabelUI()
	{
		super();
	}

	private boolean initDrawEngine() throws ETException
	{
		if (this.getInitStringValue() != null && getInitStringValue().length() > 0)
		{
			this.setDrawEngine(ETDrawEngineFactory.createDrawEngine(this));
			return this.drawEngine != null;
		}
		else
			return false;
	}

	public IDrawEngine getDrawEngine()
	{
		if (drawEngine == null)
		{
			try
			{
				this.initDrawEngine();
			}
			catch (Exception e)
			{
				return null;
			}
		}

		return drawEngine;
	}

	public void setDrawEngine(IDrawEngine newDrawEngine)
	{
		try
		{
			if (newDrawEngine != drawEngine)
			{
				drawEngine = newDrawEngine;
				//				if (drawEngine != null)
				//					drawEngine.init();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void draw(TSEGraphics graphics)
	{
		if (getDrawEngine() != null)
		{
			IDrawInfo drawInfo = getDrawInfo(graphics);
			if(getDrawEngine().getParent()==null)
				setDrawEngine(null);
			else
				if (drawInfo != null)
					getDrawEngine().doDraw(drawInfo);
		}
	}

	/**
	 * Retrieves the graphics context for the node ui.
	 * 
	 * @return The graphics context.
	 */
	public IDrawInfo getDrawInfo()
	{
		IDrawInfo retVal = null;

		TSEGraphWindow window = getGraphWindow();
		if (window != null)
		{
			Graphics g = window.getGraphics();
			if (g instanceof TSEGraphics)
			{
				TSEGraphics tsGraphics = (TSEGraphics) g;
				retVal = getDrawInfo(tsGraphics);

			}
		}

		return retVal;
	}

	/*
	 * Returns the GraphWindow.
	 */
	public TSEGraphWindow getGraphWindow()
	{
		return ETBaseUI.getGraphWindow(this);
	}

	/*
	 * Returns World points, (Logical)
	 */
	public IETRect getLogicalBounds()
	{
		return ETBaseUI.getLogicalBounds(this);
	}

	/*
	 * Returns the device bounding rect.
	 */
	public IETRect getDeviceBounds()
	{
		return ETBaseUI.getDeviceBounds(this);
	}

	/**
	 * Retrieves the graphics context for the node ui.
	 * 
	 * @param graphics The TS graphics class.
	 * @return The graphics context.
	 */
	public IDrawInfo getDrawInfo(TSEGraphics graphics)
	{
		IDrawInfo retVal = ETBaseUI.getDrawInfo(graphics, this);

		if (retVal != null)
		{
			retVal.setIsTransparent(isTransparent());
			retVal.setIsBorderDrawn(isBorderDrawn());
		}

		// TODO: Determine what the DrawinToMainDrawingArea and AlwaysSetFont
		//       Should be set to.

		return retVal;
	}
	

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#getDrawingArea()
	 */
	public IDrawingAreaControl getDrawingArea()
	{
		return ETBaseUI.getDrawingArea(this);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#setDrawingArea(org.netbeans.modules.uml.ui.swing.drawingarea.IDrawingAreaControl)
	 */
	public void setDrawingArea(IDrawingAreaControl control)
	{
		//drawingArea = control;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#getDrawEngineClass()
	 */
	public String getDrawEngineClass()
	{
		return drawEngineClass;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#setDrawEngineClass(java.lang.String)
	 */
	public void setDrawEngineClass(String string)
	{
		drawEngineClass = string;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#getModelElement()
	 */
	public IElement getModelElement()
	{
		return modelElement;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#setModelElement(org.netbeans.modules.uml.core.metamodel.core.foundation.IElement)
	 */
	public void setModelElement(IElement element)
	{
		modelElement = element;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#getInitStringValue()
	 */
	public String getInitStringValue()
	{
		return this.initStringValue;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#setInitStringValue(java.lang.String)
	 */
	public void setInitStringValue(String string)
	{
		this.initStringValue = string;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#getTSObject()
	 */
	public ITSGraphObject getTSObject()
	{
		return getOwner() instanceof ITSGraphObject ? (ITSGraphObject) getOwner() : null;
	}

	/// Returns the Model Element XMIID that was loaded from the file.
	public String getReloadedModelElementXMIID()
	{
		return meidValue;
	}
	/// Sets the Model Element XMIID that will be persisted to the file.
	public void setReloadedModelElementXMIID(String newVal)
	{
		meidValue = newVal;
	}
	/// Returns the Toplevel Element XMIID that was loaded from the file.
	public String getReloadedTopLevelXMIID()
	{
		return topLevelMEIDValue;
	}
	/// Sets the Toplevel Element XMIID that will be persisted to the file.
	public void setReloadedTopLevelXMIID(String newVal)
	{
		topLevelMEIDValue = newVal;
	}
	/// Returns the Toplevel Element XMIID that was loaded from the file.
	public String getReloadedPresentationXMIID()
	{
		return peidValue;
	}
	/// Sets the Toplevel Element XMIID that will be persisted to the file.
	public void setReloadedPresentationXMIID(String newVal)
	{
		peidValue = newVal;
	}
	/// Returns the Toplevel Element XMIID that was loaded from the file.
	public String getReloadedOwnerPresentationXMIID()
	{
		return m_ReloadedOwnerPresentationXMIID;
	}
	/// Sets the Toplevel Element XMIID that will be persisted to the file.
	public void setReloadedOwnerPresentationXMIID(String newVal)
	{
		m_ReloadedOwnerPresentationXMIID = newVal;
	}
	public IStrings getReferredElements()
	{
		return m_PresentationReferenceReferredElements;
	}
	public void setReferredElements(IStrings newVal)
	{
		m_PresentationReferenceReferredElements = newVal;
	}

	public void readFromArchive(IProductArchive prodArch, IProductArchiveElement archEle)
	{
		m_LabelKind = (int)archEle.getAttributeLong(IProductArchiveDefinitions.LABELVIEW_TSLABELKIND);
		m_Placement = (int)archEle.getAttributeLong(IProductArchiveDefinitions.LABELVIEW_TSLABELPLACEMENTKIND);

		//read from archive using the common static method
		ETBaseUI.readFromArchive(prodArch, archEle, this);

		// Create the presentation element and hook things up
		IETGraphObject etObj = (IETGraphObject) getTSObject();
		if (etObj != null)
		{
			IPresentationElement pPresEle = ((IETGraphObject) getTSObject()).getPresentationElement();
			if (pPresEle != null)
			{
				if (meidValue.length() > 0 && topLevelMEIDValue.length() > 0)
				{
					// Reattach to this presentation element
					ElementReloader reloader = new ElementReloader();
					IElement modEle = reloader.getElement(topLevelMEIDValue, meidValue);
					if (modEle != null && pPresEle instanceof IGraphPresentation)
					{
						((IGraphPresentation) pPresEle).setModelElement(modEle);
					}

					ETBaseUI.createDrawEngineOneTime(this);

					// Allow the engine to read in any specific engine stuff
					if (drawEngine != null)
					{
						IProductArchiveElement foundEngEle = archEle.getElement(IProductArchiveDefinitions.ENGINENAMEELEMENT_STRING);
						if (foundEngEle != null)
						{
							drawEngine.readFromArchive(prodArch, foundEngEle);
						}
					}
				}
			}
		}
	}

	/**
	 * Saves the basic label stuff to the product archive
	 *
	 * @param pProductArchive [in] The archive we're saving to
	 * @param pElement [in] The current element, or parent for any new attributes or elements.
	 */
	public void writeToArchive(IProductArchive prodArch, IProductArchiveElement archEle)
	{
		// Write the label kind
		archEle.addAttributeLong(IProductArchiveDefinitions.LABELVIEW_TSLABELKIND, (int) m_LabelKind);

		// Write the placement
		archEle.addAttributeLong(IProductArchiveDefinitions.LABELVIEW_TSLABELPLACEMENTKIND, (int) m_Placement);

		ETBaseUI.writeToArchive(prodArch, archEle, this);
	}

	/**
	 * Gets the IPresentationElement
	 */
	public IPresentationElement createPresentationElement(IElement pElem)
	{
		IPresentationElement retObj = null;
		ILabelPresentation labelPres = DrawingFactory.retrieveLabelPresentationMetaType();
		if (labelPres != null)
		{
			String xmiid = getReloadedPresentationXMIID();
			if (xmiid != null && xmiid.length() > 0)
			{
				// Reset the presentation XMIID to what it was last time.
				labelPres.setXMIID(xmiid);
			}

			// Assign the TS element to the presentation element
			TSEEdgeLabel label =(TSEEdgeLabel)getOwnerLabel();
			if (label != null)
			{
				labelPres.setTSLabel(label);
			}

			// Hook up the default model element (the parent's).  
			// This can be overriden in LabelPresentationImpl::put_ModelElement)
			IPresentationElement parentPE = getParentPresentationElement();
			if (parentPE != null)
			{
				IElement elem = parentPE.getFirstSubject();
				if (elem != null)
				{
					labelPres.addSubject(elem);
				}
			}
			retObj = labelPres;
		}
		return retObj;
	}

	/**
	 * Gets the parent's IPresentationElement
	 */
	private IPresentationElement getParentPresentationElement()
	{
		IPresentationElement retObj = null;
		IETGraphObject pPE = getParentETElement();
		if (pPE != null)
		{
			retObj = pPE.getPresentationElement();
		}
		return retObj;
	}

	/**
	 * Gets the parent's IETElement
	 */
	private IETGraphObject getParentETElement()
	{
		IETGraphObject retObj = null;
		TSEEdgeLabel label = (TSEEdgeLabel)getOwnerLabel();
		TSGraphObject obj = label.getOwner();
		if (obj != null)
		{
			retObj = TypeConversions.getETGraphObject(obj);
		}
		return retObj;
	}

	public boolean getWasModelElementDeleted()
	{
		return m_WasModelElementDeleted;
	}
	
	public void setWasModelElementDeleted(boolean newVal)
	{
		m_WasModelElementDeleted = newVal;
	}
	
	public boolean getFailedToCreateDrawEngine()
	{
		return m_FailedToCreateDrawEngine;
	}
	
	public void setFailedToCreateDrawEngine(boolean newVal)
	{
		m_FailedToCreateDrawEngine = newVal;
	}

	public IElement createNew(INamespace space, String initStr)
	{
		IElement retEle = null;
		IPresentationElement presEle = createPresentationElement(null);
		((ETEdgeLabel) this.getTSObject()).setPresentationElement(presEle);
		if (presEle != null)
		{
			retEle = presEle.getFirstSubject();
		}
		return retEle;
	}

	/* (non-Javadoc)
	 * @see com.tomsawyer.editor.ui.TSEAnnotatedUI#isAnnotationEditable()
	 */
	public boolean isAnnotationEditable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#getTopLevelMEIDValue()
	 */
	public String getTopLevelMEIDValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.netbeans.modules.uml.ui.products.ad.viewfactory.IETGraphObjectUI#isOnTheScreen(com.tomsawyer.editor.graphics.TSEGraphics)
	 */
	public boolean isOnTheScreen(TSEGraphics g)
	{
		return ETBaseUI.isOnTheScreen(g, this);
	}
	
	public void reset()
	{
		super.reset();
		this.peidValue = this.ZERO_VALUE;
		this.meidValue = this.ZERO_VALUE;
		this.topLevelMEIDValue = this.ZERO_VALUE;
		this.initStringValue = this.ZERO_VALUE;
	}
   
	public void copy(TSEObjectUI sourceUI)
	{
		super.copy(sourceUI);
		ETGenericEdgeLabelUI sourceNodeUI = (ETGenericEdgeLabelUI) sourceUI;
		this.peidValue = sourceNodeUI.peidValue;
		this.meidValue = sourceNodeUI.meidValue;
		this.topLevelMEIDValue = sourceNodeUI.topLevelMEIDValue;
      
		this.setInitStringValue(sourceNodeUI.getInitStringValue());
		this.setDrawEngineClass(sourceNodeUI.getDrawEngineClass());
	}
   
	public List getProperties()
	{
            /* jyothi
		List list = super.getProperties();
		list.add(new TSProperty(IProductArchiveDefinitions.PRESENTATIONELEMENTID_STRING, this.peidValue));
		return list;
             */
		List list = super.getProperties();
            String value = null;
            IPresentationElement pPE = ((IETGraphObject) getTSObject()).getPresentationElement();
            if (pPE != null) {
                // Get the presentation el  ement id
                String presEleId = pPE.getXMIID();
                if (presEleId != null && presEleId.length() > 0) {
                    value = presEleId;
                }
            }
            
            list.add(new TSProperty(IProductArchiveDefinitions.PRESENTATIONELEMENTID_STRING, value));
            return list;
            
	}
   
	public void setProperty(TSProperty property)
	{
		//String attrString = (String) property.getValue(); //jyothi
		if (IProductArchiveDefinitions.PRESENTATIONELEMENTID_STRING.equals(property.getName()))
		{
                    String attrString = (String) property.getValue(); //jyothi
                    this.peidValue = attrString;
		} else
		{
			super.setProperty(property);
		}
	}
   
	public List getChangedProperties()
	{
		List list = super.getChangedProperties();
      
		IPresentationElement pPE = ((IETGraphObject) getTSObject()).getPresentationElement();
		if (pPE != null)
		{
			// Get the presentation element id
			String presEleId = pPE.getXMIID();
			if (presEleId != null && presEleId.length() > 0)
			{
				list.add(new TSProperty(IProductArchiveDefinitions.PRESENTATIONELEMENTID_STRING, presEleId));
			}
		}
		return list;
	}	
	
	public int getLabelKind()
	{
		return m_LabelKind;
	}

	public int getPlacement()
	{
		return m_Placement;
	}

	public IETPoint getSpecifiedXY()
	{
		return m_ptSpecifiedXY;
	}

	public void setLabelKind(int l)
	{
		m_LabelKind = l;
	}

	public void setPlacement(int l)
	{
		m_Placement = l;
	}

	public void setSpecifiedXY(IETPoint point)
	{
		m_ptSpecifiedXY = point;
	}
	
}
