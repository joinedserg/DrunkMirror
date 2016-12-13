package dev.drunkmirror.dao.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.autumn.annotaion.Component;
import dev.drunkmirror.dao.Dao;

@Component//("xmldao")
public class DaoImpl4Xml extends Dao {
	
	static Logger log = LogManager.getLogger(DaoImpl4Xml.class);
	
	public DaoImpl4Xml() {
		log.warn("public DaoImpl4Xml()");
		
		
	}

	@Override
	public void save(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object parse(String path) {
		// TODO Auto-generated method stub
		return null;
	}
}
