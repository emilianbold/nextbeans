/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.db.explorer;

import java.util.*;
import java.io.*;

/** 
* xxx
*
* @author Slavek Psenicka
*/
public class DatabaseDriver extends Object implements Externalizable
{
	private String name;
	private String url;
	private String prefix;

	public DatabaseDriver()
	{
	}
	
	public DatabaseDriver(String dname, String durl)
	{
		name = dname;
		url = durl;
	}

	public DatabaseDriver(String dname, String durl, String dprefix)
	{
		name = dname;
		url = durl;
		prefix = dprefix;
	}
		
	
	public String getName()
	{
		if (name != null) return name;
		return url;
	}
	
	public void setName(String nname)
	{
		name = nname;
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setURL(String nurl)
	{
		url = nurl;
	}

	public String getDatabasePrefix()
	{
		return prefix;
	}
	
	public void setDatabasePrefix(String pref)
	{
		prefix = pref;
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof String) return obj.equals(url); 
		boolean c1 = ((DatabaseDriver)obj).getURL().equals(url); 
		boolean c2 = ((DatabaseDriver)obj).getName().equals(name); 
		return c1 && c2;
	}
	
	public String toString()
	{
		return getName();
	}
	
	/** Writes data
	* @param out ObjectOutputStream
	*/
  	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(name);
		out.writeObject(url);	
		out.writeObject(prefix);
	}
	
	/** Reads data
	* @param in ObjectInputStream
	*/
 	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		name = (String)in.readObject();
		url = (String)in.readObject();
		prefix = (String)in.readObject();
	}	
}