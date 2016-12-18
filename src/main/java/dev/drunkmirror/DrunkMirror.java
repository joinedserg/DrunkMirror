package dev.drunkmirror;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dev.drunkmirror.dao.*;
import dev.drunkmirror.example.A;
import dev.autumn.*;
import dev.autumn.annotaion.*;

@Component
public class DrunkMirror {
	
	private static Logger log = LogManager.getLogger(dev.drunkmirror.DrunkMirror.class);
	
	@Autowired("DaoImpl4Xml")
	Dao dao;
	
	public void hey() {
		if(dao == null) {
			
		}
	}
	
	public void saveObject(Object obj) {
		
		dao.save(obj);
		
	}
	
	
	public static void main(String [] args) throws Exception {
		String log4jConfPath = "src/main/resources/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		
		log.warn("DrunkMirror 0.0.0.1");
		//log.debug("DrunkMirror 0.0.0.1");
		//System.out.println("DrunkMirror 0.0.0.1");
		
		Autumn autumn = new Autumn("/home/serg/workspace_spr/DrunkMirrorProject/src/main/resources/nodes_autumn.xml");
		
		
		
		DrunkMirror drunkMirror = (DrunkMirror) autumn.getNode("DrunkMirror");
		
		A object2Save = new A();
		
		drunkMirror.saveObject(object2Save);
		
		//drunkMirror.
		//System.out.println("DrunkMirror 0.0.0.1");
		
	}
	
	
}
