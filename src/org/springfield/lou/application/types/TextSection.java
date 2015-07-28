package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.lou.application.types.DTO.TextContent;

public class TextSection extends TextContent{
	private List<TextContent> textContents = new ArrayList<TextContent>();
	
	public List<TextContent> getTextContents() {
		return textContents;
	}
	public void setTextContents(TextContent textContents) {
		this.textContents.add(textContents);
	}
	
	public TextSection(String id, String value) {
		super(id, value);
		setTextContents(new TextContent(id, value));
	}

}
