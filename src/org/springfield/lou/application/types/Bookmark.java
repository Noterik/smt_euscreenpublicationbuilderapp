package org.springfield.lou.application.types;

import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONSerializable;

public class Bookmark extends JSONSerializable{
	
	private String id;
	private String videoId;
	private String name;
	private String video;
	private String screenshot;
	private boolean isPublic;
	
	@JSONField(field = "screenshot")
	public String getScreenshot() {
		return screenshot;
	}
	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}
	
	@JSONField(field = "id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@JSONField(field = "videoId")
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	@JSONField(field = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JSONField(field = "video")
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	
	@JSONField(field = "isPublic")
	public boolean getIsPublic() {
		return this.isPublic;
	}
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public Bookmark(String id, String video_id, String name, String video, String screenshot, boolean isPublic) {
		setId(id);
		setVideoId(videoId);
		setName(name);
		setVideo(video);
		setScreenshot(screenshot);
		setIsPublic(isPublic);
	}
	
}
