package dev.drunkmirror.example;

import java.util.Date;

import dev.drunkmirror.annotation.*;
import java.util.*;

//@ReflectedComponent("reflected_A")
@ReflectedComponent
public class A extends A_super {
	
	public A() { }
	
	public A(String name, Integer numb, String ignore) {
		
		this.name = name;
		this.numb = new Integer(numb);
		this.ignore = ignore;
		this.b = new B();
		
		this.list = new ArrayList<C>();
		list.add(new C());
		list.add(new C());
		
		
		
		this.mapExample = new HashMap();
		this.mapExample.put(1, new C());
		this.mapExample.put(2, new C());
		this.mapExample.put(3, new C());
	}
	
	
	
	private String name;
	
	@Transient
	private String ignore;
	
	private Integer numb;
	
	@EmbeddedElement
	private B b;
	
	@EmbeddedCollection
	private List<C> list;
	
	
	@EmbeddedCollection
	private Map<Integer, C> mapExample;
	
	

		
	public String toString() {
		String v = "";
		
		v += "\nnumb: " + numb.toString();
		v += "\nname: " + name;
		v += "\n  " + b;
		for(C c : list) {
			v += "\n  " + c;
		}
				
		for(Integer key : mapExample.keySet()) {
			v += "\nkey: " + key + "  value: " + mapExample.get(key);	
		}
		
		v += "\n A_supername_priv: " + this.A_supername_priv;
		v += "\n A_supername_prot: " + this.A_supername_prot;
				
		return v;
	}
	
}
