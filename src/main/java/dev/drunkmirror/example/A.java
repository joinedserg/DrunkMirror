package dev.drunkmirror.example;

import java.util.Date;

import dev.drunkmirror.annotation.*;
import java.util.*;

@ReflectedComponent("reflected_A")
public class A extends A_super {
	
	public A() { }
	
	public A(String name, Date date, Integer numb, String ignore) {
		
		this.name = name;
		this.date = date;
		this.numb = numb;
		this.ignore = ignore;
		
		this.list = new ArrayList<C>();
		list.add(new C());
		list.add(new C());
		
		this.b = new B();
		
		this.mapExample = new HashMap();
		this.mapExample.put(1, new C());
		this.mapExample.put(2, new C());
		this.mapExample.put(3, new C());
	}
	
	private String name;
	
	@SecondName("second_name_date")
	private Date date;
	
	private Integer numb;
	
	@Transient
	private String ignore;
	
	@EmbeddedElement
	private B b;
	
	@EmbeddedCollection
	private List<C> list;
	
	@EmbeddedCollection
	private Map<Integer, C> mapExample;
	
}
