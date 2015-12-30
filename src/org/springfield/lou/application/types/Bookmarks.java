package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.mojo.ftp.URIParser;

public class Bookmarks {
	public List<Bookmark> bookmarklist = new ArrayList<Bookmark>();
	private List<FsNode> xmlCallList;
	
	private String address = Configuration.getDomain() + "/user/";

	public List<Bookmark> getBookmarklist() {
		return bookmarklist;
	}

	
	
	public Bookmarks(String user) {
		super();
		Blacklist blacklist = new Blacklist();
		
		address = address + user + "/publications/1/bookmark";
		this.xmlCallList = Fs.getNodes(this.address, 2);

		for (FsNode node : this.xmlCallList) {
			
			String bookmarkId = node.getId();

			String videoUrl = this.address + "/" + node.getId() + "/video";
	
			List<FsNode> videoNodes = Fs.getNodes(videoUrl, 0);

			String videoId = videoNodes.get(0).getId();
			FsNode videoNd = Fs.getNode(videoUrl + "/" +videoId);
			String referId = videoNd.getReferid();
			String referUrl = referId + "/" + "rawvideo";
		
			
			List<FsNode> referNodes = Fs.getNodes(referUrl, 1);
			
			String videoInfoUrl = videoUrl + "/" + videoId;
		
			FsNode videoInfo = Fs.getNode(videoInfoUrl);
			String videoName = videoInfo.getProperty("TitleSet_TitleSetInEnglish_title");
			String screenshot = videoInfo.getProperty("screenshot");

			String mount = "";
			
			for(FsNode referNode : referNodes){
				if(referNode.getProperty("format").equals("MP4")){
					String[] mounts = referNode.getProperty("mount").split(",");
					for(String mnt : mounts){
						if(mnt.contains("http://") && mnt.contains("/progressive/")){
							mount = mnt;
							break;
						}else{
							mount = "http://" + mnt + ".noterik.com/progressive/" + mnt + referNode.getPath() + "/raw.mp4";
							break;
						}
					}
					break;
				};
			}
			
			if(mount != null){
				
				Bookmark bookmark = new Bookmark(bookmarkId, videoId, videoName, mount, screenshot, blacklist.checkIsPublic(referId));
				bookmarklist.add(bookmark);
			}
		}
	}	
	
	public String getBookmarkLinkById(String id){
		String link = null;
		for (Bookmark bookmark : this.bookmarklist) {
			if(bookmark.getId() == id){
				return bookmark.getVideo();
			}
		}
		
		return link;
	}
	
}
