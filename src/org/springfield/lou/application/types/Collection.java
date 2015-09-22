package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Collection {
	
	private String name;
	private List<Bookmark> videos;
	
	
	public Collection(String name, List<Bookmark> videos) {
		setName(name);
		setVideos(videos);
	}



	public List<Bookmark> getVideos() {
		return videos;
	}


	public void setVideos(List<Bookmark> videos) {
		this.videos = videos;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
}
