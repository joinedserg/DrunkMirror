package dev.drunkmirror.example;

import dev.drunkmirror.annotation.ReflectedComponent;

@ReflectedComponent
public class B {
	public B() {
		this.name = "B_defName";
	}
	
	private String name;
	
	public String toString() {
		String v = "";
		
		v += "B name: " + name;

		return v;
	}
	
}
