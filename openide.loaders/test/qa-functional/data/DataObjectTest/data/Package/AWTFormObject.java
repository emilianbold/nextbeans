package DataLoaderTests.DataObjectTest.data.Package;/*
 * AWTFormObject.java
 *
 * Created on December 20, 1999, 12:17 PM
 */
 


/** 
 *
 * @author  pknakal
 * @version 
 */
public class AWTFormObject extends java.awt.Frame {

  /** Creates new form AWTFormObject */
  public AWTFormObject() {
    initComponents ();
    pack ();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    button1 = new java.awt.Button ();
    label1 = new java.awt.Label ();
    addWindowListener (new java.awt.event.WindowAdapter () {
      public void windowClosing (java.awt.event.WindowEvent evt) {
        exitForm (evt);
      }
    }
    );

    button1.setLabel ("Close");
    button1.addMouseListener (new java.awt.event.MouseAdapter () {
      public void mouseClicked (java.awt.event.MouseEvent evt) {
        button1MouseClicked (evt);
      }
    }
    );


    add (button1, java.awt.BorderLayout.SOUTH);

    label1.setText ("dataObjectsInPkg/AWTFormObject");
    label1.setAlignment (java.awt.Label.CENTER);


    add (label1, java.awt.BorderLayout.CENTER);

  }//GEN-END:initComponents

private void button1MouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button1MouseClicked
    this.dispose();
    this.exitForm(null);
  }//GEN-LAST:event_button1MouseClicked

  /** Exit the Application */
  private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
    System.exit (0);
  }//GEN-LAST:event_exitForm

  /**
  * @param args the command line arguments
  */
  public static void main (String args[]) {
    new AWTFormObject ().show ();
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private java.awt.Button button1;
  private java.awt.Label label1;
  // End of variables declaration//GEN-END:variables

}