package org.springfield.lou.application.types;

import java.util.List;

import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONSerializable;

public class Collection extends JSONSerializable{
	
	private String id;
	private String name;
	private List<Bookmark> videos;
	
	private int page = 0;
	private int itemsPerPage = 4;
	private int pages;
	
	public Collection(String id, String name, List<Bookmark> videos) {
		setId(id);
		setName(name);
		setVideos(videos);
		
		Double pages = (double) videos.size() / itemsPerPage;
		
		this.pages = (int) Math.ceil(pages);
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
	
	@JSONField(field = "page")
	public int getPage(){
		return page;
	}
	
	@JSONField(field = "pages")
	public int getPages(){
		return pages;
	}
	
	@JSONField(field = "paginatedVideos")
	public List<Bookmark> getPaginatedVideos(){
		int start = page * itemsPerPage;
		int end = (start + itemsPerPage) < videos.size() ? start + itemsPerPage : videos.size();  
		return videos.subList(start, end);
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
	
	public void setPage(int page){
		if(page < pages){
			this.page = page;
		}
	}
	
	public void nextPage(){
		System.out.println("Collection.nextPage()");
		if((page + 1) < pages){
			System.out.println("LET'S INCREASE THE PAGE COUNTER");
			this.page++;
		}
	}
	
	public void prevPage(){
		if((page - 1) >= 0){
			System.out.println("Collection.prevPage()");
			System.out.println("LET'S DECREASE THE PAGE COUNTER!");
			this.page--;
		}
	}
}
