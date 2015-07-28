package org.springfield.lou.application.types.DTO;

import interfaces.BookmarkItem;
import interfaces.VimeoItem;
import interfaces.YouTubeItem;

public class MediaItem implements BookmarkItem, VimeoItem, YouTubeItem{
	private String id;
	private String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	public MediaItem(String id, String value) {
		this.setId(id);
		setValue(value);
	}
	
}
