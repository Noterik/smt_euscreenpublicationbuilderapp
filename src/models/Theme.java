package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.Configuration;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONSerializable;

public class Theme extends JSONSerializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String prefix = "/springfield/tomcat/webapps/ROOT/eddie/apps/euscreenpublicationbuilder/img/layouts/svg/css";	
	private FsNode node;
	private String id;
	private String name;
	private String description;
	private String css = null;
	
	public Theme(FsNode node) throws FileNotFoundException{
		this.node = node;
		populate();
	}
	
	@JSONField(field = "id")
	public String getId(){
		return id;
	}
	
	@JSONField(field = "name")
	public String getName(){
		return name;
	}
	
	@JSONField(field = "description")
	public String description(){
		return description;
	}
	
	public String getCSSURI(){
		return Configuration.getServer() + "/eddie/apps/euscreenpublicationbuilder/css/layouts/" + this.id + ".css";
	}
		
	public String getIconCSS(){
		return css;
	}
	
	private void populate() throws FileNotFoundException{
		this.id = node.getId();
		this.name = node.getProperty("name");
		this.description = node.getProperty("description");
		
		String path = prefix + "/" + node.getId() + ".css";
		this.css = new Scanner(new File(path)).useDelimiter("\\Z").next();
	}
}
