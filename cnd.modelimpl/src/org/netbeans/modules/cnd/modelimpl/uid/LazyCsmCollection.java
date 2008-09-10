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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.modelimpl.uid;

import java.util.Collection;
import java.util.Iterator;
import org.netbeans.modules.cnd.api.model.CsmIdentifiable;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.services.CsmSelect.CsmFilter;
import org.netbeans.modules.cnd.modelimpl.impl.services.UIDFilter;

/**
 * The lazy implementation of the collection 
 * backed by the collection of UIDs.
 * 
 * It uses two template parameters: Tuid and Tfact.
 * The idea behind that is that often we use 
 * interface type (e.g. CsmFile) UID, 
 * while implementation knows that all the instances are 
 * of implementation type (e.g. FileImpl).
 * So Tuid is an interface (CsmFile) type, 
 * while Tfact is implementation (FileImpl) type.
 *  
 * @author Alexander Simon
 */
public class LazyCsmCollection<Tuid, Tfact extends Tuid> implements Collection<Tfact> {
    private Collection<CsmUID<Tuid>> uids;
    boolean allowNullsAndSkip;
    public LazyCsmCollection(Collection<CsmUID<Tuid>> uids, boolean allowNullsAndSkip){
        this.uids = uids;
        this.allowNullsAndSkip = allowNullsAndSkip;
    }

    private Tfact convertToObject(CsmUID uid){
        return (Tfact) UIDCsmConverter.UIDtoCsmObject(uid);
    }

    private CsmUID<Tuid> convertToUID(Tfact object){
        return ((CsmIdentifiable)object).getUID();
    }

    public int size() {
        return uids.size();
    }

    public boolean isEmpty() {
        return uids.isEmpty();
    }

    public boolean contains(Object o) {
        Iterator<Tfact> it = iterator();
        while(it.hasNext()){
            Tfact object = it.next();
            if (o == object || 
                o != null && o.equals(object)){
                return true;
            }
        }
        return false;
    }

    public Iterator<Tfact> iterator() {
        return allowNullsAndSkip ? new MySafeIterator<Tfact>() : new MyIterator();
    }

    public Iterator<Tfact> iterator(CsmFilter filter) {
        return new MySafeIterator<Tfact>(filter);
    }

    public Object[] toArray() {
	Object[] result = new Object[size()];
	Iterator<Tfact> e = iterator();
	for (int i=0; e.hasNext(); i++)
	    result[i] = e.next();
	return result;
    }

    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            a = (T[])java.lang.reflect.Array
		.newInstance(a.getClass().getComponentType(), size);

        Iterator<T> it = (Iterator<T>) iterator();
	Object[] result = a;
        for (int i=0; i<size; i++)
            result[i] = it.next();
        if (a.length > size)
	    a[size] = null;
        return a;
    }

    public boolean add(Tfact o) {
        return uids.add(convertToUID(o));
    }

    public boolean remove(Object o) {
        return uids.remove(convertToUID((Tfact)o));
    }

    public boolean containsAll(Collection<?> c) {
	Iterator<?> e = c.iterator();
	while (e.hasNext())
	    if(!contains(e.next()))
		return false;
	return true;
    }

    public boolean addAll(Collection<? extends Tfact> c) {
	boolean modified = false;
    if (c instanceof LazyCsmCollection) {
        return uids.addAll(((LazyCsmCollection)c).uids);
    } else {
        Iterator<? extends Tfact> it = c.iterator();
        while (it.hasNext()) {
            if (add(it.next()))
            modified = true;
        }
    }
	return modified;
    }

    public boolean removeAll(Collection<?> c) {
	boolean modified = false;
	Iterator<?> it = iterator();
	while (it.hasNext()) {
	    if (c.contains(it.next())) {
		it.remove();
		modified = true;
	    }
	}
	return modified;
    }

    public boolean retainAll(Collection<?> c) {
	boolean modified = false;
	Iterator<Tfact> it = iterator();
	while (it.hasNext()) {
	    if (!c.contains(it.next())) {
		it.remove();
		modified = true;
	    }
	}
	return modified;
    }

    public void clear() {
        uids.clear();
    }

    @Override
    public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("["); // NOI18N

        Iterator<Tfact> it = iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Tfact o = it.next();
            buf.append(o == this ? "(this Collection)" : String.valueOf(o));  // NOI18N
            hasNext = it.hasNext();
            if (hasNext)
                buf.append(", "); // NOI18N
        }

	buf.append("]"); // NOI18N
	return buf.toString();
    }

    private class MyIterator<T> implements Iterator<T>{
        
        private Iterator it;       
        private MyIterator(){
            it = uids.iterator();
        }
        public boolean hasNext() {
            return it.hasNext();
        }
        
        public T next() {
            CsmUID uid = (CsmUID)it.next();
            T decl = (T) convertToObject(uid);
            assert decl != null : "no object for UID " + uid;
            return decl;
        }
        
        public void remove() {
            it.remove();
        }
    }
    
    private class MySafeIterator<T> implements Iterator<T>{
        
        private Iterator it;       
        private T next;
        private CsmFilter filter;
        
        private MySafeIterator(){
            this(null);
        }
        private MySafeIterator(CsmFilter filter){
            this.filter = filter;
            it = uids.iterator();
            next = getNextNonNull();
        }
        public boolean hasNext() {
            return next != null;
        }
        
        private T getNextNonNull() {
            T out = null;
            while (out == null && it.hasNext()) {
                CsmUID uid = (CsmUID)it.next();
                if (uid == null ||
                    (filter != null && !((UIDFilter)filter).accept(uid))){
                    continue;
                }
                out = (T) convertToObject(uid);
            }
            return out;
        }
        
        public T next() {
            T decl = next;
            next = getNextNonNull();
            return decl;
        }
        
        public void remove() {
            it.remove();
        }
    }    
}
