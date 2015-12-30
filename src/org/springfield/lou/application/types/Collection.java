package org.springfield.lou.application.types;

import java.util.List;

import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONSerializable;

public class Collection extends JSONSerializable{
	
	private String id;
	private String name;
	private List<Bookmark> videos;
	
	
	public Collection(String id, String name, List<Bookmark> videos) {
		setId(id);
		setName(name);
		setVideos(videos);
	}

	@JSONField(field = "id")
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	@JSONField(field = "videos")
	public List<Bookmark> getVideos() {
		return videos;
	}


	public void setVideos(List<Bookmark> videos) {
		this.videos = videos;
	}

	@JSONField(field = "name")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
}
