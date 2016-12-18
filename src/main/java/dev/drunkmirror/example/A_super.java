package dev.drunkmirror.example;

import dev.drunkmirror.annotation.ReflectedComponent;

@ReflectedComponent
public class A_super {

	public A_super() {
		this.A_supername_priv = "A_supername_priv";
		this.A_supername_prot = "A_supername_prot";
	}
	
	private String A_supername_priv;
	protected String A_supername_prot;
}
