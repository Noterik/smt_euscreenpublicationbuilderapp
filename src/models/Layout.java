package models;

import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.Configuration;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONSerializable;

public class Layout extends JSONSerializable{

	private FsNode node;
	private String id;
	private String name;
	private String description;
	private String template;
	private int order;
	private LayoutIcon icon;
	
	public Layout(FsNode node) {
		this.node = node;
		populate();
	}
	
	@JSONField(field = "id")
	public String getId(){
		return this.id;
	}
	
	@JSONField(field = "name")
	public String getName(){
		return this.name;
	}
	
	@JSONField(field = "description")
	public String getDescription(){
		return this.description;
	}
	
	public LayoutIcon getIcon(){
		return icon;
	}
	
	@JSONField(field = "icon")
	public String getIconSVG(){
		return icon.getSVG();
	}
	
	@JSONField(field = "order")
	public Integer getOrder(){
		return this.order;
	}
	
	@JSONField(field = "template")
	public String getTemplate(){
		return this.template;
	}
	
	@JSONField(field = "cssuri")
	public String getCSSURI(){
		return Configuration.getServer() + "/eddie/apps/euscreenpublicationbuilder/css/layouts/" + this.id + ".css";
	}
	
	private void populate(){
		this.id = node.getId();
		this.name = node.getProperty("name");
		this.description = node.getProperty("description");
		this.icon = new LayoutIcon(node.getId());
		this.order = Integer.parseInt(node.getProperty("order"));
		this.template = node.getProperty("template");
	}

}
