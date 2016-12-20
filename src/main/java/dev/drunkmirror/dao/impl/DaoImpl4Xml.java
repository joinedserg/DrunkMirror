package dev.drunkmirror.dao.impl;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import dev.autumn.annotaion.Component;
import dev.drunkmirror.annotation.EmbeddedCollection;
import dev.drunkmirror.annotation.EmbeddedElement;
import dev.drunkmirror.annotation.ReflectedComponent;
import dev.drunkmirror.annotation.SecondName;
import dev.drunkmirror.annotation.Transient;
import dev.drunkmirror.dao.Dao;
import dev.drunkmirror.example.*;



@Component//("xmldao")
public class DaoImpl4Xml extends Dao {


	int level;
	Document doc;

	static Logger log = LogManager.getLogger(DaoImpl4Xml.class);

	public DaoImpl4Xml() {
		log.warn("public DaoImpl4Xml()");

		level = 0;
	}
	
	

	private String getSpaces() {
		String space = "    ";
		String spaces = ""; 
		for(int i = 0; i < level; i++) {
			spaces += space;
		}

		return spaces;
	}

	@Override
	public void save(Object obj, String path) {
		log.info("\n\n\n");
		log.info("DaoImpl4Xml save");

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element main = doc.createElement("main");
			Attr attrType = doc.createAttribute("type");
			attrType.setTextContent(obj.getClass().getName());
			main.setAttributeNode(attrType);
			doc.appendChild(main);

			Class c = obj.getClass();
			log.info(getSpaces() + "Type: " + c.getName());

			researchObj(c, obj, main, false);

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}


	private void researchObj(Class c, Object obj, Element elDom, boolean isSuper) /* throws  */{
		level++;
		String nameType = c.getName();
		String simpleName = c.getSimpleName();

		log.info(getSpaces() + "Type ins: " + nameType + "  " + obj.getClass().getSimpleName());
		if(c == Object.class) {
			level--;
			return;
		}

		ReflectedComponent a = (ReflectedComponent)c.getAnnotation(ReflectedComponent.class);
		String reflectedName = simpleName; 

		if(a != null) {
			log.info(getSpaces() + "valueAnn: " + reflectedName);

			if(a.value().equals("auto")) {
				log.info(getSpaces() + "valueAnn: " + a.value() + "  value: " + simpleName);
			}
			else {
				reflectedName = a.value();
			}
		}

		Element curElem = elDom;

		Class researchedClass = c;
		if(Map.class.isAssignableFrom(researchedClass)) {
			log.info(getSpaces() + "Field type: map");
			log.info(getSpaces() + "Field concrete type: " + researchedClass);

			Map map = (Map)obj;
			for(Object key: map.keySet()) {								
				Element mapElem = doc.createElement("pair");
				Attr attrType = doc.createAttribute("type");
				attrType.setTextContent(Pair.class.getName());
				mapElem.setAttributeNode(attrType);
				
				curElem.appendChild(mapElem);				
				researchObj(KeyMap.class, new KeyMap(key), mapElem, false);
				researchObj(Value.class, new Value(map.get(key)), mapElem, false);
			}		
			level--;
			return;
		}
		else if(List.class.isAssignableFrom(researchedClass)) {
			log.info(getSpaces() + "Field type: list");
			log.info(getSpaces() + "Field concrete type: " + researchedClass);

			List list = (List)obj;
			for(Object el : list) {
				curElem = doc.createElement("el");				
				Attr attrType = doc.createAttribute("type");
				attrType.setTextContent(el.getClass().getName());

				curElem.setAttributeNode(attrType);
				elDom.appendChild(curElem);

				researchObj(el.getClass(), el, curElem, false);
			}

			level--;
			return;
		}

		if(isSuper == true) {
			curElem = doc.createElement("Super");
			Attr attrType = doc.createAttribute("type");
			attrType.setTextContent(c.getName());
			curElem.setAttributeNode(attrType);
			elDom.appendChild(curElem);
		}
		researchObj(c.getSuperclass(), obj, curElem, true);

		Field[] fields = c.getDeclaredFields();
		for(Field f : fields) {
			log.info(getSpaces() + "Field: " + f.getName());

			try {
				f.setAccessible(true);
				Class type = f.get(obj).getClass();
				log.info(getSpaces() + "Field type: " + type.getName() + "  " + f.get(obj).getClass().getName());

				Transient tn = (Transient)f.getAnnotation(Transient.class);
				if(tn == null) {
					EmbeddedCollection embCol = (EmbeddedCollection)f.getAnnotation(EmbeddedCollection.class);
					EmbeddedElement embType = (EmbeddedElement)f.getAnnotation(EmbeddedElement.class);

					if(embType != null || embCol != null) {
						log.info(getSpaces() + "Collection handler");

						curElem = doc.createElement(f.getName());
						Attr attrType = doc.createAttribute("type");
						attrType.setTextContent(f.get(obj).getClass().getName());
						curElem.setAttributeNode(attrType);
						elDom.appendChild(curElem);

						researchObj(f.get(obj).getClass(), f.get(obj), curElem, false);
					}
					else {
						log.info(getSpaces() + "Field value=" + f.get(obj));

						Element field = doc.createElement(f.getName());
						Attr fieldType = doc.createAttribute("type");
						fieldType.setTextContent(type.getName());
						Attr fieldValue = doc.createAttribute("value");
						fieldValue.setTextContent(f.get(obj).toString());

						field.setAttributeNode(fieldType);
						field.setAttributeNode(fieldValue);

						curElem.appendChild(field);
					}
				}
				else {
					log.info(getSpaces() + "Trancient");
				}
				f.setAccessible(false);
			}
			catch(Exception ex) {				
				ex.printStackTrace();
			}
		}
		level--;
	}


	@Override
	public Object parse(String path) throws Exception {		
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		level = 0;
		return researchXml(doc.getDocumentElement(), new Object());
	}





	
	public Object researchXml(Node node, Object obj) throws Exception {
		level++;		
		String nodeName = node.getNodeName();		
		Object curLevelObj;
		
		Class<?> clazz = null;
		Node nodeAttr = node.getAttributes().getNamedItem("value");
		String nodeValue = "0";
		if(nodeAttr != null) {
			nodeValue = nodeAttr.getNodeValue();
		}
		
		nodeAttr = node.getAttributes().getNamedItem("type");
		String nodeType = "null";
		
		if(nodeAttr != null) {
			nodeType = nodeAttr.getNodeValue();			
		}

		nodeAttr = node.getAttributes().getNamedItem("super");
		String nodeSuper = "";
		if(nodeAttr != null) {
			nodeSuper = nodeAttr.getNodeValue();
		}		
		clazz = Class.forName(nodeType);
		if(Number.class.isAssignableFrom(clazz)) {
			curLevelObj = clazz.getDeclaredConstructor(String.class).newInstance(nodeValue);
			level--;
			return curLevelObj;
		}
		if(node.getChildNodes().getLength() == 0) {
			level--;
			return nodeValue; 
		}
		curLevelObj = clazz.getDeclaredConstructor().newInstance();
		
		log.info(getSpaces() + "nodeName: " + nodeName + "   nodeType: " + nodeType + "   nodeValue: " + nodeValue + "  " + nodeSuper);		
		for(int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node cnode = node.getChildNodes().item(i);
			log.info(getSpaces() + "  cnode: " + cnode.getNodeName());
			
			if(cnode.getNodeName().equals("Super")) {
				
				continue;
			}
			
			Object value = researchXml(cnode, curLevelObj);
			if(Map.class.isAssignableFrom(clazz)) {
				((Map)curLevelObj).put(((Pair)value).getKey(), ((Pair)value).getValue());
			}
			else if(List.class.isAssignableFrom(clazz)) {
				((List)curLevelObj).add(value);				
			}
			else {
				Field f = curLevelObj.getClass().getDeclaredField(cnode.getNodeName());
				if(f != null) {
					f.setAccessible(true);
					f.set(curLevelObj, value);
					f.setAccessible(false);
				}
			}
		}
		level--;
		return curLevelObj;
	}


	public static void main(String[] args) throws Exception {
		String log4jConfPath = "src/main/resources/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

		String path = "testXML.xml";
		DaoImpl4Xml dao = new DaoImpl4Xml();


		A a1 = new A("t_name1", 12, "ignore_inf1");
		A a2 = new A("t_name2", 2, "ignore_inf2");


		dao.save(a1, path);

		A obj = (A)dao.parse(path);
		System.out.println(obj);
		
		List list = new ArrayList();
		list.add(a1);
		list.add(a2);
		dao.save(list, path);
		
		List listExample = (List)dao.parse(path);
		System.out.println(listExample);
		

		Map<Integer, A> map = new HashMap();
		map.put(1, a1);
		map.put(2, a2);
		dao.save(map, path);
		
		Map mapExample = (Map)dao.parse(path);
		System.out.println(mapExample);
		
		
	}
	
}


/*4 work with map */
class Pair {
	public Pair() {
		
	}
	
	private Object key;
	private Object value;

	public Object getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
}

class KeyMap {
	public KeyMap() {

	}

	public KeyMap(Object key) {
		this.key = key;
	}

	private Object key;
}

class Value {
	public Value() {

	}

	public Value(Object value) {
		this.value = value;
	}

	@EmbeddedElement
	private Object value;
}