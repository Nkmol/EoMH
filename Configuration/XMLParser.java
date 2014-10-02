package Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	private Element root;
	
	public XMLParser(String filename){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(filename);
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		} catch (FileNotFoundException e){
			System.out.println("ERROR: File " + filename + " not found");
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		this.root = dom.getDocumentElement();
	}
	

	public Element getRoot(){
		return this.root;
		
	}
	public boolean tagExists(Element el, String tag) {
		NodeList nl = el.getElementsByTagName(tag);
		if (nl != null && nl.getLength() > 0) return true;
		return false;
	}
	public String getTextValue(Element el, String tag){
		String val = null;
		NodeList nl = el.getElementsByTagName(tag);
		if (nl != null && nl.getLength() > 0){
			Element t = (Element)nl.item(0);
			val = t.getFirstChild().getNodeValue();
		}
		return val;
	}
	public Element getFirstElement(Element el, String tag){
		return this.getElement(el, tag, 0);
	}	
	public Element getElement(Element el, String tag, int n){
		Element t = null;
		NodeList nl = el.getElementsByTagName(tag);
		if (nl != null && nl.getLength() > 0){
			 t = (Element)nl.item(n);
		}
		return t;
	}
	public NodeList getNodeList(Element el, String tag){
		return el.getElementsByTagName(tag);
	}
	public int getNodeCount(Element el, String tag){
		NodeList nl = el.getElementsByTagName(tag);
		return nl.getLength();
	}
	public String getElementName(Element el){
		return el.getNodeName();
	}
	public int getIntValue(Element el, String tag) {
		return Integer.parseInt(this.getTextValue(el, tag));
	}
	public String getAttrValue(Element el, String attr){
		return el.getAttribute(attr);
	}
	public int getAttrIntValue(Element el, String attr){
		return Integer.parseInt(el.getAttribute(attr));
	}
}
