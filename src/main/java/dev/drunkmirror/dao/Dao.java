package dev.drunkmirror.dao;

import dev.autumn.annotaion.Component;

//@Component
public abstract class Dao {

	public Dao() {
		//System.out.println("Dao");
	}
	
	abstract public void save(Object obj);
	abstract public Object get()  throws Exception;
	
	Object currentObject;
	
	//path - db any conf (if you want you can use autumn's settings db) 
	//or 
	//or path to file with xml
	//
	abstract protected Object parse(String path);
	
}
