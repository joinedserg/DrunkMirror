package dev.drunkmirror.example;

import dev.drunkmirror.annotation.Transient;

public class C {

	@Transient
	static int i = 0;
	
	public C() {
		this.cname = "default_cname_" + i++;
	}
	
	private String cname;
	
	
	public String toString() {
		String v = "";
		
		v += "C name: " + cname;

		return v;
	}
}
