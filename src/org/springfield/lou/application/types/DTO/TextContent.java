package org.springfield.lou.application.types.DTO;

public class TextContent {
	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TextContent(String id, String value) {
		setId(id);
		setValue(value);
	}
}
