package dev.drunkmirror.example;

public class C {

	static int i = 0;
	
	public C() {
		this.cname = "default_cname_" + i++;
	}
	
	private String cname;
}
