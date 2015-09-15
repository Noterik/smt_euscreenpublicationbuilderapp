package org.springfield.lou.application.types.DTO;

import interfaces.BookmarkItem;
import interfaces.VimeoItem;
import interfaces.YouTubeItem;

public class MediaItem implements BookmarkItem, VimeoItem, YouTubeItem{
	private String id;
	private String value;
	private String poster;
	
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
	
	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}
		
	public MediaItem(String id, String value, String poster) {
		this.setId(id);
		this.setValue(value);
		this.setPoster(poster);
	}
	
}
