/*
 * IncludeTest.java
 * JUnit based test
 *
 * Created on October 4, 2005, 4:26 PM
 */

package org.netbeans.modules.xml.schema.model;
import java.beans.PropertyChangeListener;
import junit.framework.*;
import java.net.URI;
import java.util.Collection;

/**
 *
 * @author rico
 */
public class IncludeTest extends TestCase {
    private static final String TEST_XSD = "resources/testInclude.xsd";
    
    public IncludeTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(IncludeTest.class);
        
        return suite;
    }
    
    /**
     * Test of getSchemaLocation method, of class org.netbeans.modules.xmlschema.api.model.Include.
     */
    public void testGetSchemaLocation() throws Exception {
        Include instance = null;
        SchemaModel model = Util.loadSchemaModel(TEST_XSD);
         Schema schema = model.getSchema();
        assertNotNull("Null schema " , schema);
        
	Collection<SchemaComponent> refs = schema.getSchemaReferences();
	this.assertNotNull("Null refs ", refs);
	instance = (Include)refs.iterator().next();
        this.assertNotNull("Null include ", instance);
        String expResult = "somefile.xsd";
        System.out.println("expResult: " + expResult.toString());
        String result = instance.getSchemaLocation();
        System.out.println("result: " + result.toString());
        assertEquals(expResult.toString(), result.toString());
    }
    
    /**
     * Test of setSchemaLocation method, of class org.netbeans.modules.xmlschema.api.model.Include.
     */
    public void testSetSchemaLocation() throws Exception {
        URI uri = null;
        Include instance = null;
        SchemaModel model = Util.loadSchemaModel(TEST_XSD);
        Schema schema = model.getSchema();
        assertNotNull("Null schema " , schema);
        
        java.util.List<SchemaComponent> children = schema.getChildren();
        for(SchemaComponent child: children){
            if(child instanceof Include){
                instance = (Include)child;
                break;
            }
        }
        this.assertNotNull("Null include ", instance);
	TestListener tl = new TestListener();
	instance.getSchemaModel().addPropertyChangeListener(tl);
        String result = instance.getSchemaLocation();
        assertEquals("somefile.xsd", instance.getSchemaLocation().toString());
	model.startTransaction();
	instance.setSchemaLocation("newfile.xsd");
        model.endTransaction();
	assertEquals("newfile.xsd", instance.getSchemaLocation().toString());
	assertTrue("only one event should be fired " + tl.getEventsFired(), tl.getEventsFired()==1);
	assertEquals("event should be modified", Include.SCHEMA_LOCATION_PROPERTY, tl.getLastEventName());
    }
    
    static class TestListener implements PropertyChangeListener {
	    private String eventName;
	    private int count = 0;
	    
	    public void propertyChange(java.beans.PropertyChangeEvent evt) {
		    eventName = evt.getPropertyName();
		    count++;
	    }
	    
	    public int getEventsFired() {
		    return count;
	    }
	    
	    public String getLastEventName() {
		    return eventName;
	    }
	    
	    public void resetFiredEvents() {
		    count = 0;
	    }
    }
}
