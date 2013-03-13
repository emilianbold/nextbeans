/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class Bean {

    public List<String> any() {
        return Arrays.asList(
                "Jeden",
                "Dva");
    }

    public String getProperty() {
        return "property";
    }

    public String[] getMyArray() {
        return new String[0];
    }

    public Iterable<String> getMyIterable() {
        return Collections.<String>emptyList();
    }

    public List<String> getMyList() {
        return Collections.<String>emptyList();
    }

    public String getMyString() {
        return "string";
    }

}
