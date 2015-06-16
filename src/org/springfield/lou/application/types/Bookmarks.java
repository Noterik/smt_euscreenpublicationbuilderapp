package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Bookmarks {
	public List<BookmarkItem> bookmarklist = new ArrayList<BookmarkItem>();
	private List<FsNode> xmlCallList;
	private String address = "/domain/euscreenxl/user/david/publications/1/bookmark"; 
	
	public List<BookmarkItem> getBookmarklist() {
		return bookmarklist;
	}

	
	public Bookmarks() {
		super();
		
		this.xmlCallList = Fs.getNodes(this.address, 2);

		for (FsNode node : this.xmlCallList) {
			String bookmarkId = node.getId();
			
			String videoUrl = this.address + "/" + node.getId() + "/video";
	
			List<FsNode> videoNodes = Fs.getNodes(videoUrl, 0);
			String videoId = videoNodes.get(0).getId();
			String videoInfoUrl = videoUrl + "/" + videoId;
			FsNode videoInfo = Fs.getNode(videoInfoUrl);
			String videoName = videoInfo.getProperty("TitleSet_TitleSetInEnglish_title");
			String screenshot = videoInfo.getProperty("screenshot");
			
			BookmarkItem bookmark = new BookmarkItem(bookmarkId, videoId, videoName, screenshot);

			bookmarklist.add(bookmark);

		}
	}	

}
