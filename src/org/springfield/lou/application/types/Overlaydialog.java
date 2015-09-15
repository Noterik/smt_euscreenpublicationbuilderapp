package org.springfield.lou.application.types;

import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;

public class Overlaydialog extends ScreenComponent {
	
	private String url;
	private String html;
	private boolean visible = false;

	public Overlaydialog(Screen s) {
		super(s);
		// TODO Auto-generated constructor stub
	}
	
	@JSONField(field = "html")
	public String getHTML(){
		return this.html;
	}
	
	public void setHTML(String html){
		this.html = html;
	}
	
	@JSONField(field = "url")
	public String getURL(){
		return this.url;
	}
	
	public void setURL(String url){
		this.url = url;
	}
	
	@JSONField(field = "visible")
	public boolean getVisible(){
		return this.visible;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public void update(){
		this.sendMessage("update(" + this.toJSON() + ")");
	}
}
