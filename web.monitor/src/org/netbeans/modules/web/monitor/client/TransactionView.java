/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/**
 * TransactionView.java
 *
 *
 * Created: Wed Feb  2 15:42:32 2000
 *
 * @author Ana von Klopp
 * @version
 */

package org.netbeans.modules.web.monitor.client;

import org.netbeans.modules.web.monitor.server.Constants;
import org.netbeans.modules.web.monitor.data.*;
import javax.swing.*;     // widgets
import javax.swing.border.*;     // widgets
import javax.swing.event.*;
import java.awt.Font;

import java.net.*;        // url
import java.awt.*;          // layouts, dialog, etc.
import java.awt.event.*;    // Events
import java.io.*;           // I/O
import java.text.*;         // I/O
import java.util.*;         // local GUI

import org.openide.awt.SplittedPanel;
import org.openide.awt.ToolbarButton;
import org.openide.awt.ToolbarToggleButton;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children.SortedArray;
import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;
import org.openide.windows.Mode;
import org.openide.util.NbBundle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;

/**
 * Update title does not work like it should. Maybe there is a getName
 * method for this that I can override.
 */
public class TransactionView extends ExplorerPanel implements
				     PropertyChangeListener, ChangeListener {

    // Handles all the files etc. 
    private static Controller controller = null;
    
    // Misc
    private transient Frame parentFrame = null;
    private transient JLabel transactionTitle = null;
    private transient ToolbarToggleButton timeAButton, 	timeDButton,
	alphaButton, browserCookieButton, savedCookieButton; 

    // Sizing and stuff...
    private transient  Dimension logD = new Dimension(250, 400);
    private transient  Dimension dataD = new Dimension(500, 400);
    private transient  Dimension tabD = new Dimension(500,472);
    
    // Are we debugging?
    private transient  final static boolean debug = false;

    // Display stuff 
    private transient static ExplorerManager mgr = null;
    private transient BeanTreeView tree = null;
    private transient AbstractNode selected = null;

    private transient RequestDisplay requestDisplay = null;
    private transient CookieDisplay  cookieDisplay = null;
    private transient SessionDisplay sessionDisplay = null;
    private transient ServletDisplay servletDisplay = null;
    private transient ContextDisplay contextDisplay = null;
    private transient ClientDisplay  clientDisplay = null;
    private transient HeaderDisplay  headerDisplay = null;

    private transient EditPanel editPanel = null;


    // Data display tables 
    private int displayType = 0;

    
    // Button icons

    static protected Icon updateIcon;
    static protected Icon a2zIcon;
    static protected Icon timesortAIcon;
    static protected Icon timesortDIcon;
    static protected Icon timestampIcon;
    static protected Icon browserCookieIcon;
    static protected Icon savedCookieIcon;
    static protected ImageIcon frameIcon;

   
    static {
		
	try {
	    updateIcon =
	    new ImageIcon(TransactionView.class.getResource
	    ("/org/netbeans/modules/web/monitor/client/icons/update.gif")); // NOI18N

	    a2zIcon =
	    new ImageIcon(TransactionView.class.getResource
	    ("/org/netbeans/modules/web/monitor/client/icons/a2z.gif")); // NOI18N

	    timesortAIcon =
	    new ImageIcon(TransactionView.class.getResource
            ("/org/netbeans/modules/web/monitor/client/icons/timesortA.gif")); // NOI18N

	    timesortDIcon =
	    new ImageIcon(TransactionView.class.getResource
			  ("/org/netbeans/modules/web/monitor/client/icons/timesortB.gif")); // NOI18N

	    timestampIcon =
	    new ImageIcon(TransactionView.class.getResource
			  ("/org/netbeans/modules/web/monitor/client/icons/timestamp.gif")); // NOI18N

	    browserCookieIcon = 
	    new ImageIcon(TransactionView.class.getResource
			  ("/org/netbeans/modules/web/monitor/client/icons/browsercookie.gif")); // NOI18N


	    savedCookieIcon = 
	    new ImageIcon(TransactionView.class.getResource
			  ("/org/netbeans/modules/web/monitor/client/icons/savedcookie.gif")); // NOI18N

	    frameIcon =
	    new ImageIcon(TransactionView.class.getResource
            ("/org/netbeans/modules/web/monitor/client/icons/menuitem.gif")); // NOI18N

	} catch(Throwable t) {
	    t.printStackTrace();
	} 
    }

    public HelpCtx getHelpCtx() {
	String helpID = NbBundle.getBundle(TransactionView.class).getString("MON_Transaction_View_F1_Help_ID"); // NOI18N
	return new HelpCtx( helpID );
    }

    /**
     * Creates the display and the nodes that are present all the
     * time. Because all this is done at startup, we don't actually
     * retrieve any data until the Monitor is opened.
     */
    public TransactionView(Controller c) {
        setIcon(frameIcon.getImage());
	controller = c;
	initialize();
	DisplayAction.setTransactionView(this);
	SaveAction.setTransactionView(this);
	EditReplayAction.setTransactionView(this);
	DeleteAction.setTransactionView(this);
	this.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(TransactionView.class).getString("ACS_MON_monitorDesc"));
	this.getAccessibleContext().setAccessibleName(NbBundle.getBundle(TransactionView.class).getString("ACS_MON_monitorName"));

	if (debug) log ("Calling opentransactions from constructor"); // NOI18N
    }

    private void initialize() {

	mgr = getExplorerManager();
	mgr.addPropertyChangeListener(this);
	mgr.setRootContext(controller.getRoot());

	tree = new BeanTreeView();
	tree.setDefaultActionAllowed(true);
	tree.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(TransactionView.class).getString("ACS_MON_treeName"));
	tree.getAccessibleContext().setAccessibleName(NbBundle.getBundle(TransactionView.class).getString("ACS_MON_treeDesc"));

	SplittedPanel splitPanel = new SplittedPanel();
	splitPanel.setSplitPosition(35);
	splitPanel.setSplitterComponent(new SplittedPanel.EmptySplitter(5));
	splitPanel.setSwapPanesEnabled(false);
	splitPanel.setKeepFirstSame(true);

	splitPanel.add(createLogPanel(), SplittedPanel.ADD_LEFT);
	splitPanel.add(createDataPanel(), SplittedPanel.ADD_RIGHT);

	this.add(splitPanel);

	setName(NbBundle.getBundle(TransactionView.class).getString("MON_Title"));
    }

    /**
     * Open the transaction nodes (i.e. first level children of the root).
     */
    public void openTransactionNodes() {

	// Post the request for later in case there are timing issues
	// going on here. 

	OpenTransactionNodesRequest req = new
	    OpenTransactionNodesRequest();
	
	if(debug) 
	    log("OpenTransactionNodesRequest:: " +  // NOI18N
			       "posting request..."); // NOI18N
				     
	RequestProcessor.Task t = 
	    RequestProcessor.postRequest(req, 500); // wait a sec...
    }

    class OpenTransactionNodesRequest implements Runnable {
	
	public void run() {
	    if(debug) 
		log("OpenTransactionNodesRequest:: " + // NOI18N
				   "running..."); // NOI18N
	    openTransactionNodes();
	}

	public void openTransactionNodes() {
	    if (debug) 
		log("TransactionView::openTransactionNodes"); // NOI18N
	    NavigateNode root = controller.getRoot();
	    Children ch = root.getChildren();
	    Node [] nodes = ch.getNodes();
	    CurrNode cn = (CurrNode)nodes[0];
	    SavedNode sn = (SavedNode)nodes[1];
	    
	    
	    // If there are any current nodes, then select the most
	    // recent (i.e. the last?) one. 

	    Children currCh = cn.getChildren();
	    Node [] currChNodes = currCh.getNodes();
	    int numCN = currChNodes.length;
	    if(debug)
		log("TransactionView::openTransactionNodes. currCHNodes.length = " + numCN); // NOI18N
	    if (numCN > 0) {
		int selectThisOne = 0;
		if (timeAButton.isSelected()) {
		    selectThisOne = numCN - 1;
		}
		if(debug) log("TransactionView::openTransactionNodes. selecting node " + currChNodes[selectThisOne] + "("+selectThisOne+")"); // NOI18N
		selectNode(currChNodes[selectThisOne]);
	    } else {
		Children savedCh = sn.getChildren();
		Node [] savedChNodes = savedCh.getNodes();
		int numSN = savedChNodes.length;
		if(debug) log("TransactionView::openTransactionNodes. savedChNodes.length = " + numSN); // NOI18N
		if (numSN > 0) {
		    selectNode(savedChNodes[0]);
		}
	    }
	}
    }

    public void selectNode(Node n) {

	try {
	    mgr.setSelectedNodes(new Node[] {n});
	    
	} catch (Exception exc) {
	    if (debug) {
		log("TransactionView::caught exception selecting node. " + exc); // NOI18N
		exc.printStackTrace();
	    }
	} // safely ignored
    }
    
    /**
     * Starts the monitor client in a specific workspace */
    private boolean openedOnceAlready = false;
    public void open(Workspace w) {

	if(debug) log("running open(Workspace)"); //NOI18N
	if(w == null) super.open();

	if(debug) log("opening in workspace: " + String.valueOf(w)); //NOI18N
	super.open(w); 
	setName(NbBundle.getBundle(TransactionView.class).getString("MON_Title"));	
	if (!openedOnceAlready) {
	    openedOnceAlready = true;
	    controller.getTransactions();
	    openTransactionNodes();
	}
	controller.checkServer(false);
        requestFocus();
    }

    protected void updateTitle() {
	setName(NbBundle.getBundle(TransactionView.class).getString("MON_Title"));	
    }
    
    /**
     * Invoked by IDE when trying to close monitor. */
    public boolean canClose(Workspace w, boolean last) {
	return true;
    }

    /**
     * Do not serialize this component, substitute null instead.
     */
    public Object writeReplace ()
	throws ObjectStreamException {
	return null;
    }


    /**
     * Invoked at startup, creates the display GUI.
     */
    private JPanel createLogPanel() {

	JPanel logPanel = null;
	JLabel title =
	    new JLabel(NbBundle.getBundle(TransactionView.class).getString("MON_Transactions_27"), SwingConstants.CENTER);
	title.setBorder (new EtchedBorder (EtchedBorder.LOWERED));

	JToolBar buttonPanel = new JToolBar();
	buttonPanel.setBorder
	    (new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				new EmptyBorder (4, 4, 4, 4)
				    ));
	buttonPanel.setFloatable (false);

	ToolbarButton updateButton = new ToolbarButton(updateIcon);
	updateButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Reload_all_17"));
	updateButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    controller.getTransactions();
		}});

	timeAButton = new ToolbarToggleButton(timesortAIcon, false);
	timeAButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Order_transactions_15"));

	timeAButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

		    if(!((ToolbarToggleButton)e.getSource()).isSelected())
			return;
		    else {
			timeDButton.setSelected(false);
			alphaButton.setSelected(false);
			controller.setComparator
			    (controller.new CompTime(false));
		    }
		}});

	timeDButton = new ToolbarToggleButton(timesortDIcon, true);
	timeDButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Order_transactions_16"));
	timeDButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

		    if(!((ToolbarToggleButton)e.getSource()).isSelected())
			return;
		    else {
			timeAButton.setSelected(false);
			alphaButton.setSelected(false);
			controller.setComparator
			    (controller.new CompTime(true));
		    }

		}});

	alphaButton = new ToolbarToggleButton(a2zIcon, false);
	alphaButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Order_transactions_14"));
	alphaButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {

		    if(!((ToolbarToggleButton)e.getSource()).isSelected())
			return;
		    else {
			timeAButton.setSelected(false);
			timeDButton.setSelected(false);
			controller.setComparator
			    (controller.new CompAlpha());
		    }

		}});


	// Do we use the browser's cookie or the saved cookie? 
	browserCookieButton = new ToolbarToggleButton(browserCookieIcon, true);
	browserCookieButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Browser_cookie"));
	browserCookieButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    browserCookieButton.setSelected(true);
		    savedCookieButton.setSelected(false);
		    controller.setUseBrowserCookie(true); 

		}});

	savedCookieButton = new ToolbarToggleButton(savedCookieIcon, false);
	savedCookieButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Saved_cookie"));
	savedCookieButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    browserCookieButton.setSelected(false);
		    savedCookieButton.setSelected(true);
		    controller.setUseBrowserCookie(false); 
		}});


	ToolbarToggleButton timestampButton = new
	    ToolbarToggleButton(timestampIcon,
				TransactionNode.showTimeStamp());
	timestampButton.setToolTipText(NbBundle.getBundle(TransactionView.class).getString("MON_Show_time_25"));
	timestampButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    TransactionNode.toggleTimeStamp();
		    // PENDING - should find a way to repaint
		    // the tree. tree.repaint() does not work. 
		    controller.updateNodeNames();
		}});

	buttonPanel.add(updateButton);

	JPanel sep = new JPanel() {
		public float getAlignmentX() {
		    return 0;
		}
		public float getAlignmentY() {
		    return 0;
		}
	    };
	sep.setMaximumSize(new Dimension(10, 10));
	buttonPanel.add(timeDButton);
	buttonPanel.add(timeAButton);
	buttonPanel.add(alphaButton);
	sep = new JPanel() {
		public float getAlignmentX() {
		    return 0;
		}
		public float getAlignmentY() {
		    return 0;
		}
	    };
	sep.setMaximumSize(new Dimension(10, 10));
	buttonPanel.add(sep);
	buttonPanel.add(browserCookieButton);
	buttonPanel.add(savedCookieButton);
	//browserCookieButton.setSelected(true);
	sep = new JPanel() {
		public float getAlignmentX() {
		    return 0;
		}
		public float getAlignmentY() {
		    return 0;
		}
	    };
	sep.setMaximumSize(new Dimension(10, 10));
	buttonPanel.add(sep);
	buttonPanel.add(timestampButton);

	logPanel = new JPanel();
	logPanel.setLayout(new BorderLayout());
        //logPanel.setBorder(new CompoundBorder
	//(new LineBorder (getBackground ()),
	//new BevelBorder(EtchedBorder.LOWERED)));

	logPanel.setPreferredSize(logD);
	logPanel.setMinimumSize(logD);

	logPanel.add(title, "North"); // NOI18N

	JPanel p = new JPanel (new BorderLayout ());
	//p.setBorder (new EtchedBorder (EtchedBorder.LOWERED));
	p.add(BorderLayout.NORTH, buttonPanel);
	p.add(BorderLayout.CENTER, tree);
	logPanel.add(BorderLayout.CENTER, p);

	return logPanel;

    }


    /**
     * Invoked at startup, creates the display GUI.
     */
    private JPanel createDataPanel() {

	JPanel dataPanel = null;

	transactionTitle =
	    new JLabel(NbBundle.getBundle(TransactionView.class).getString("MON_Transaction_data_26"), SwingConstants.CENTER);
	transactionTitle.setBorder (new EtchedBorder (EtchedBorder.LOWERED));

	JTabbedPane jtp = new JTabbedPane();
        jtp.getAccessibleContext().setAccessibleName(NbBundle.getBundle(TransactionView.class).getString("ACS_MON_Transaction_dataName"));
        jtp.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(TransactionView.class).getString("ACS_MON_Transaction_dataDesc"));

	jtp.setPreferredSize(tabD);
	jtp.setMaximumSize(tabD);

	requestDisplay = new RequestDisplay(); 
	JScrollPane p = new JScrollPane(requestDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Request_19"), p);


	cookieDisplay = new CookieDisplay(); 
	p = new JScrollPane(cookieDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Cookies_4"), p);

	sessionDisplay = new SessionDisplay(); 
	p = new JScrollPane(sessionDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Session_24"), p); 

	servletDisplay = new ServletDisplay(); 
	p = new JScrollPane(servletDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Servlet_23"), p);

	contextDisplay = new ContextDisplay(); 
	p = new JScrollPane(contextDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Context_23"), p);

	clientDisplay = new ClientDisplay(); 
	p = new JScrollPane(clientDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Client_Server"), p);

	headerDisplay = new HeaderDisplay(); 
	p = new JScrollPane(headerDisplay);
	jtp.addTab(NbBundle.getBundle(TransactionView.class).getString("MON_Header_19"), p);

	jtp.addChangeListener(this);

	dataPanel = new JPanel();
	dataPanel.setLayout(new BorderLayout());
	dataPanel.setPreferredSize(dataD);
	dataPanel.setMinimumSize(dataD);

	dataPanel.add(transactionTitle, "North"); //NOI18N
	dataPanel.add(BorderLayout.CENTER, jtp);
	return dataPanel;
    }


    /**
     * Invoked by DisplayAction. Displays monitor data for the selected
     * node. 
     * PENDING - register this as a listener for the display action
     */
    public void displayTransaction(Node node) {
	if(debug) log("Displaying a transaction. Node: "  + (node == null ? "null" : node.getName())); //NOI18N
	if (node == null)
	    return;

	if(node instanceof TransactionNode || 
	   node instanceof NestedNode) {
	    try {
		selected = (AbstractNode)node;
	    } 
	    catch (ClassCastException ex) {
		selected = null;
		selectNode(null);
	    }
	}
	else {
	    selected = null;
	    selectNode(null);
	}
	
	if(debug) log("Set the selected node to\n" + // NOI18N
					 (selected == null ? "null" : selected.toString())); // NOI18N
	showData(); 
	if(debug) log("Finished displayTransaction())"); // NOI18N
    }

    public void saveTransaction(Node[] nodes) {
	if(debug) log("In saveTransaction())"); // NOI18N
	if((nodes == null) || (nodes.length == 0)) return;
	controller.saveTransaction(nodes);
	selected = null;
	selectNode(null);
	showData(); 
	if(debug) log("Finished saveTransaction())"); // NOI18N
    }
    
    /**
     * Invoked by EditReplayAction. 
     */
    public void editTransaction(Node node) {
	if(debug) log("Editing a transaction"); //NOI18N
	// Exit if the internal server is not running - the user
	// should start it before they do this. 
	if(!controller.checkServer(true)) return;
	selected = (TransactionNode)node;
	if(debug) log("Set the selected node to\n" + // NOI18N
					 selected.toString()); 
	editData(); 
	if(debug) log("Finished editTransaction())"); // NOI18N
    }


    /**
     * Listens to events from the tab pane, displays different
     * categories of data accordingly. 
     */
    public void stateChanged(ChangeEvent e) {

	setName(NbBundle.getBundle(TransactionView.class).getString("MON_Title"));

	JTabbedPane p = (JTabbedPane)e.getSource();
	displayType = p.getSelectedIndex();
	showData();
    }
    

    void showData() {
	 
	if(selected == null) {
	    // PENDING
	    if(debug) 
		log("No selected node, why is this?"); // NOI18N
	    if(debug) log("  Probably because user selected a non-transaction node (i.e. one of the folders. So we clear the display."); // NOI18N
	}
	
	if(debug) log("Now in showData()"); // NOI18N
	    
	DataRecord dr = null;	    
	try {
	    if (selected != null) {
		dr = controller.getDataRecord(selected);
	    }
	}
	catch(Exception ex) {
	    if(debug) log(ex.getMessage());
	    ex.printStackTrace();
	}
	
	if(debug) {
	    log("Got this far"); // NOI18N
	    log("displayType:" + String.valueOf(displayType)); // NOI18N
	}
	
	
	if (displayType == 0)
	    requestDisplay.setData(dr);
	else if (displayType == 1)
	    cookieDisplay.setData(dr);
	else if (displayType == 2)
	    sessionDisplay.setData(dr);
	else if (displayType == 3)
	    servletDisplay.setData(dr);
	else if (displayType == 4)
	    contextDisplay.setData(dr);
	else if (displayType == 5)
	    clientDisplay.setData(dr);
	else if (displayType == 6)
	    headerDisplay.setData(dr);
	this.repaint();
	
	if(debug) log("Finished showData()"); // NOI18N
    }


    void editData() {

	if(selected == null) {
	    if(debug) 
		log("No selected node, why is this?"); // NOI18N 
	    return;
	}
	
	if(!(selected instanceof TransactionNode)) return;
		
	if(debug) log("Now in editData()"); // NOI18N
	    
	MonitorData md = null;	    
	try {
	    // We retrieve the data from the file system, not from the 
	    // cache
	    md = controller.getMonitorData((TransactionNode)selected, 
					   false,  // get from file
					   false); // and don't cache
	}
	catch(Exception ex) {
	    if(debug) log(ex.getMessage());
	    ex.printStackTrace();
	}
	
	if(debug) {
	    log("Got this far"); // NOI18N
	    log(md.dumpBeanNode()); 
	    log("displayType:" + // NOI18N
			       String.valueOf(displayType));
	}
	
	// Bring up the dialog. 
	if (editPanel == null) {
	    editPanel = new EditPanel(md);
	}

	editPanel.setData(md);
	editPanel.showDialog();

	if(debug) log("Finished editData()"); // NOI18N
    }

    /**
     * Display the data for a node if it's selected. This should
     * probably be done by checking if you can get the DisplayAction
     * from the Node, and then calling it if it's enabled.
     */
    public void propertyChange(PropertyChangeEvent evt) {

	setName(NbBundle.getBundle(TransactionView.class).getString("MON_Title"));
	//updateTitle();

	if(evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {

	    if(evt.getNewValue() instanceof Node[]) {
		try {
		    Node[] ns = (Node[])evt.getNewValue();
		    if(ns.length == 1) {
			displayTransaction(ns[0]); 
		    }
		}
		// Do nothing, this was not a proper node
		catch(Exception e) {
		    if(debug) {
			log(e.getMessage());
			e.printStackTrace();
		    }
		    selected = null;
		    if(debug) 
			log("Set the selected node to null"); // NOI18N
		    showData();
		    return;
		}
	    }
	}
	if(debug) log("Finished propertyChange()"); // NOI18N
    }

    /**
     * Blanks out the displays - this is used by the delete actions
     */
    public void blank() {
	selected = null;
	selectNode(null);
	showData(); 
    }

    class ComboBoxRenderer extends JLabel implements ListCellRenderer {
	public ComboBoxRenderer() {
	    setOpaque(true);
	}
	public Component getListCellRendererComponent(JList list,
						      Object o,
						      int index,
						      boolean isSelected,
						      boolean cellHasFocus) {
	    if(isSelected) {
		setBackground(list.getSelectionBackground());
		setForeground(list.getSelectionForeground());
	    }
	    else {
		setBackground(list.getBackground());
		setForeground(list.getForeground());
	    }
	    ImageIcon icon = (ImageIcon)o;
	    setText(icon.getDescription());
	    setIcon(icon);
	    return this;
	}
    }

    private void log(String s) {
	System.out.println("TransactionView::" + s); //NOI18N
    }
     
}
