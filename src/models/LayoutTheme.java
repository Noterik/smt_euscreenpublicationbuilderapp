package models;

import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONSerializable;

public class LayoutTheme extends JSONSerializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Theme theme;
	private String icon;
	
	public LayoutTheme(Layout layout, Theme theme){
		this.theme = theme;
		LayoutIcon ic = layout.getIcon();
		ic.applyTheme(theme);
		icon = ic.getSVG();
	}
	
	@JSONField(field = "theme")
	public Theme theme(){
		return theme;
	}
	
	@JSONField(field = "icon")
	public String getIcon(){
		return icon;
	}
	
}
