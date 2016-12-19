package dev.drunkmirror.dao.impl;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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

	//list, map, set, object

	//ArrayList, HashMap, HashSet ?
	//List<String> availableListType;
	//List<String> availableMapType;
	int level;
	Document doc;

	static Logger log = LogManager.getLogger(DaoImpl4Xml.class);

	public DaoImpl4Xml() {
		log.warn("public DaoImpl4Xml()");

		// = fromXML(xml);

		/*this.availableListType = new ArrayList<String>();
		this.availableMapType = new ArrayList<String>();

		this.availableListType.add("ArrayList");

		//
		this.availableMapType.add("HashMap");*/

		//
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
	public void save(Object obj) {
		log.info("\n\n\n");
		log.info("DaoImpl4Xml save");

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element main = doc.createElement("main");
			doc.appendChild(main);

			Class c = obj.getClass();
			log.info(getSpaces() + "Type: " + c.getName());

			research(c, obj, main, false);


			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(
					new File("testXML.xml"));

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	private void research(Class c, Object obj, Element elDom, boolean isSuper) /* throws  */{
		level++;
		String nameType = c.getName();
		String simpleName = c.getSimpleName();
		
		log.info(getSpaces() + "Type ins: " + nameType);
		if(simpleName.equals("Object")) {
			level--;
			return;
		}
		ReflectedComponent a = (ReflectedComponent)c.getAnnotation(ReflectedComponent.class);
		String reflectedName = simpleName; 
		if(a != null) {
			level++;
			log.info(getSpaces() + "valueAnn: " + reflectedName);

			if(a.value().equals("auto")) {
				log.info(getSpaces() + "valueAnn: " + a.value() + "  value: " + simpleName);
			}
			else {
				reflectedName = a.value();
			}
			level--;
		}

		Element curElem = doc.createElement(reflectedName);
		Attr attrType = doc.createAttribute("type");
		attrType.setTextContent(nameType);
		curElem.setAttributeNode(attrType);
		elDom.appendChild(curElem);	

		if(isSuper == true) {
			Attr id = doc.createAttribute("super");
			id.setTextContent("yes");
			curElem.setAttributeNode(id);
		}

		research(c.getSuperclass(), obj, curElem, true);

		Field[] fields = c.getDeclaredFields();
		for(Field f : fields) {
			log.info(getSpaces() + "Field: " + f.getName());

			Class type = f.getType();
			level++;
			log.info(getSpaces() + "Field type: " + type.getName());

			SecondName sn = (SecondName)c.getAnnotation(SecondName.class);
			if(sn != null) {
				log.info(getSpaces() + "SecondName: " + sn.value());
			}
			Transient tn = (Transient)c.getAnnotation(Transient.class);
			if(tn == null) {
				f.setAccessible(true);
				try {
					EmbeddedCollection embCol = (EmbeddedCollection)f.getAnnotation(EmbeddedCollection.class);
					EmbeddedElement embType = (EmbeddedElement)f.getAnnotation(EmbeddedElement.class);

					if(embType != null) {
						research(f.get(obj).getClass(), f.get(obj), curElem, false);
					}
					else if(embCol != null) {
						log.info(getSpaces() + "Collection handler");
						
						Class researchedClass = f.get(obj).getClass();
						if(Map.class.isAssignableFrom(researchedClass)) {
							log.info(getSpaces() + "Field type: map");
							log.info(getSpaces() + "Field concrete type: " + researchedClass);
							
							curElem = doc.createElement(f.getName());
							Attr typeMap = doc.createAttribute("type");
							typeMap.setTextContent(researchedClass.getName());
							curElem.setAttributeNode(typeMap);
							elDom.appendChild(curElem);
							
							Map map = (Map)f.get(obj);
							for(Object key: map.keySet()) {								
								Element mapElem = doc.createElement("MapElem");
								curElem.appendChild(mapElem);
								research(key.getClass(), key, mapElem, false);

								research(map.get(key).getClass(), map.get(key), mapElem, false);
							}							
						}
						else if(List.class.isAssignableFrom(researchedClass)) {
							log.info(getSpaces() + "Field type: list");
							log.info(getSpaces() + "Field concrete type: " + researchedClass);
							
							curElem = doc.createElement(f.getName());
							Attr typeList = doc.createAttribute("type");
							typeList.setTextContent(researchedClass.getName());
							curElem.setAttributeNode(typeList);
							elDom.appendChild(curElem);
							
							List list = (List)f.get(obj);
							for(Object el : list) {
								research(el.getClass(), el, curElem, false);
							}
						}
					}
					else {
						log.info(getSpaces() + "Field value=" + f.get(obj));

						Element field = doc.createElement(f.getName());
						Attr fieldType = doc.createAttribute("type"/*type.getName()*/);
						fieldType.setTextContent(type.getName() /*f.get(obj).toString()*/);
						field.setAttributeNode(fieldType);

						Attr fieldValue = doc.createAttribute("value");
						fieldValue.setTextContent(f.get(obj).toString());
						field.setAttributeNode(fieldValue);
						curElem.appendChild(field);
					}
				}
				catch(Exception ex) {				
					ex.printStackTrace();
				}
				f.setAccessible(false);
			}
			else {
				log.info(getSpaces() + "Trancient");
			}
			level--;
		}
		level--;
	}


	public static void main(String[] args) throws SQLException {
		String log4jConfPath = "src/main/resources/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);


		DaoImpl4Xml dao = new DaoImpl4Xml();
		DaoImpl4Db db = new DaoImpl4Db();
		A a1 = new A("t_name1", new Date(), 1, "ignore_inf1");
		A a2 = new A("t_name2", new Date(), 2, "ignore_inf2");


		db.save(a1);
		//dao.save(a1);/**/


		List list = new ArrayList();
		list.add(a1);
		list.add(a2);

		//dao.save(list);

		Map<Integer, A> map = new HashMap();
		map.put(1, a1);
		map.put(2, a2);

		//dao.save(map);

	}


	public void saveElement(Object obj) {



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
