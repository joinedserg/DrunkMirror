package dev.autumn;

import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.autumn.parser.AutumnResultParsing;
import dev.autumn.parser.AutumnXmlParser;
import dev.autumn.annotaion.AnnotationHandler;
import dev.autumn.annotaion.Component;
import dev.autumn.annotaion.handlers.AutoInjectHandler;
import dev.autumn.annotaion.handlers.AutoWiredHandler;
import dev.autumn.annotaion.handlers.OutputContextHandler;
import dev.autumn.annotaion.handlers.ValueHandler;
import dev.autumn.example.main.*;
import dev.autumn.finder.ClassFinder;
import dev.drunkmirror.dao.impl.DaoImpl4Xml;

public class Autumn {

	AutumnXmlParser xmlParser;
	Map<String, Class<?>> nodes;
	List<AnnotationHandler> handlers;
	
	static Logger log = LogManager.getLogger(Autumn.class);
	
	//TODO: change it, remove readin conf from constructor
	public Autumn(String xmlPath) throws Exception {
		xmlParser = new AutumnXmlParser();
		xmlParser.parseSettings(xmlPath);
				
		String avPath4Search = xmlParser.getAvailablePath4Scan();
		nodes = new HashMap<String, Class<?>>();
		
		if(avPath4Search != null) {
			nodes = ClassFinder.getMarkedClassesFromPackage(avPath4Search, Component.class);
			
			for(String k : nodes.keySet()) {				
				Class c = nodes.get(k);
				
				//log.warn("key:" + k + "  name:" + c.getName());
				xmlParser.addResult(k, c.getName());
			}
		}
		for(AutumnResultParsing res : xmlParser.getResults()) {			
			Class c = Class.forName(res.getAvailablePath());
			
			//log.warn("key:" + res.getIdNode() + "  name:" + c.getName());
			nodes.put(res.getIdNode(), c);
		}
		
		//after apply classfinder		
		handlers = new ArrayList<AnnotationHandler>();
		handlers.add(new ValueHandler());
		handlers.add(new OutputContextHandler(nodes));
		handlers.add(new AutoWiredHandler(nodes));
		
		//handlers.add(new AutoInjectHandler());		
	}
	
	public Object getNode(String idNode) throws InstantiationException, IllegalAccessException, NullPointerException {
		Class c = nodes.get(idNode);		
		Object obj = c.newInstance();
		
		for(AnnotationHandler an : handlers) {
			try {
				an.handler(c, obj, xmlParser.getResultParsing(idNode));
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return obj;
	}
	
	public static void main(String [] args) throws Exception {
		System.out.println("Autumn 0.0.0.1");
		
		Autumn autumn = new Autumn("/home/serg/workspace_spr/AutumnProject/target/classes/example_nodes.xml");
		
		MainContext m = (MainContext)autumn.getNode("mainContext");
		m.hey();
		
		
		
		/*for(String name : autumn.nodes.keySet()) {
			System.out.println(name);
			
			Object o = autumn.getNode(name);
			if(o.getClass() == MainContext.class) {
				((MainContext)o).hey();
			}
		}*/
	}
}
