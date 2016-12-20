package dev.drunkmirror;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public void saveObject(Object obj, String path) {
		dao.save(obj, path);
	}
	
	
	public Object getObject(String path) throws Exception {
		
		
		return dao.parse(path);
	}
	
	public static void main(String [] args) throws Exception {
		String log4jConfPath = "src/main/resources/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		
		log.warn("DrunkMirror 0.0.0.1");
		//log.debug("DrunkMirror 0.0.0.1");
		//System.out.println("DrunkMirror 0.0.0.1");
		
		Autumn autumn = new Autumn("/home/serg/workspace_spr/DrunkMirrorProject/src/main/resources/nodes_autumn.xml");
		
		
		
		DrunkMirror drunkMirror = (DrunkMirror) autumn.getNode("DrunkMirror");
		
		A a1 = new A("t_name1", 12, "ignore_inf1");
		A a2 = new A("t_name2", 2, "ignore_inf2");
		
		String path = "testXML.xml";
		drunkMirror.saveObject(a1, path);		
		Object obj = drunkMirror.getObject(path);
		
		System.out.println(obj);
		
		
		List list = new ArrayList();
		list.add(a1);
		list.add(a2);
		drunkMirror.saveObject(list, path);
		
		List listExample = (List)drunkMirror.getObject(path);
		System.out.println(listExample);
		
		Map<Integer, A> map = new HashMap();
		map.put(1, a1);
		map.put(2, a2);
		drunkMirror.saveObject(map, path);
		
		Map mapExample = (Map)drunkMirror.getObject(path);
		System.out.println(mapExample);
		
		
	}
	
	
}
