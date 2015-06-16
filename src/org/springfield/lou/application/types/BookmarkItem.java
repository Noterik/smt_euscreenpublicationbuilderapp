package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class BookmarkItem {
	
	private String id;
	private String video_id;
	private String name;
	private String screenshot;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVideo_id() {
		return video_id;
	}
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScreenshot() {
		return screenshot;
	}
	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}
	
	public BookmarkItem(String id, String video_id, String name, String screenshot) {
		setId(id);
		setVideo_id(video_id);
		setName(name);
		setScreenshot(screenshot);
	}
	
}
