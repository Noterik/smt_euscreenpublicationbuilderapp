package models;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LayoutIcon{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String prefix = "/springfield/tomcat/webapps/ROOT/eddie/apps/euscreenpublicationbuilder/img/layouts/svg";	
	private String id;
	private Document svg;
	
	public LayoutIcon(String id){
		this.id = id;
		populate();
	}
	
	public void applyTheme(Theme theme){
		Node svgNode = svg.getElementsByTagName("svg").getLength() > 0 ? svg.getElementsByTagName("svg").item(0) : null;
		if(svgNode != null){
			Element svgEl = (Element) svgNode;
			svgEl.setAttribute("class", "theme-" + theme.getId());
		}
		Node styleNode = svg.getElementsByTagName("style").getLength() > 0 ? svg.getElementsByTagName("style").item(0) : null;
		if(styleNode != null){
			styleNode.setTextContent(theme.getIconCSS());
		}
	}
	
	public String getSVG(){
		DOMSource domSource = new DOMSource(svg);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void populate(){
		String path = prefix + "/" + id + ".svg";
		try {
			String content = new Scanner(new File(path)).useDelimiter("\\Z").next();
			System.out.println("CONTENT: " + content);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"));
				System.out.println("LETS TRY TO PARSE THE XML");
				svg = builder.parse(input);
				System.out.println("XML PARSED!");
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (Exception e){
				System.out.println(e);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println(e);
		}
	}
}
