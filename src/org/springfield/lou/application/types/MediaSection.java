package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.lou.application.types.DTO.MediaItem;

public class MediaSection extends MediaItem{
	private List<MediaItem> mediaItems = new ArrayList<MediaItem>();
	public List<MediaItem> getMediaItems() {
		return mediaItems;
	}
	public void setMediaItems(MediaItem mediaItems) {
		this.mediaItems.add(mediaItems);
	}

	public MediaSection(String id, String value) {
		super(id, value);
		this.setMediaItems(new MediaItem(id, value ));
	}

}
