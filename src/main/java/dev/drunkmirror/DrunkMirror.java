package dev.drunkmirror;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.drunkmirror.dao.*;
import dev.autumn.*;
import dev.autumn.annotaion.*;

@Component
public class DrunkMirror {
	
	static Logger log = LogManager.getLogger(Autumn.class);
	
	@Autowired("DaoImpl4Db")
	Dao dao;
	
	public void hey() {
		if(dao == null) {
			
		}
	}
	
	
	public static void main(String [] args) throws Exception {
		
		log.warn("kk");
		System.out.println("DrunkMirror 0.0.0.1");
		
		
		Autumn autumn = new Autumn("/home/serg/workspace_spr/DrunkMirrorProject/src/main/resources/nodes_autumn.xml");
		
		
		
		DrunkMirror drunkMirror = (DrunkMirror) autumn.getNode("DrunkMirror");
		
		
		System.out.println("DrunkMirror 0.0.0.1");
		
	}
	
	
}
