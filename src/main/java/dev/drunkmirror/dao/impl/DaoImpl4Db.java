package dev.drunkmirror.dao.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.autumn.annotaion.Component;
import dev.drunkmirror.dao.*;

@Component
public class DaoImpl4Db extends Dao {

	static Logger log = LogManager.getLogger(DaoImpl4Xml.class);
	
	
	public DaoImpl4Db() {
		log.warn("public DaoImpl4Db()");
	}


	@Override
	public void save(Object obj, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object parse(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
