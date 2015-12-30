package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screencomponent.component.ScreenComponent;
import org.springfield.mojo.ftp.URIParser;

public class Bookmarks extends ScreenComponent{
	private List<Bookmark> bookmarklist = new ArrayList<Bookmark>();	
	/*
	 * TODO: Get rid of these statics, check comments in Bookmarks()
	 */
	private static String domain = "/domain/euscreenxl";
	public static List<String> blacklistProviders;
	private String address = domain + "/user/";
	private Blacklist blacklist;
	
	public Bookmarks(Screen s, String user) {
		super(s);
		blacklist = new Blacklist();
		
		this.populateBookmarks(user);
	}
	
	@JSONField(field = "bookmarks")
	public List<Bookmark> getBookmarklist() {
		return bookmarklist;
	}
	
	/*
	 * TODO: So also move this. seedBlacklist() sounds a bit vague, call it populateBlacklist() instead. 
	 */
	public void seedBlacklist() {
		String blacklistEntryUrl = domain + "/config/blacklist/";
		FSList blacklist = FSListManager.get(blacklistEntryUrl);
		
		for (FsNode node : blacklist.getNodes()) {
			boolean videoposter = Boolean.parseBoolean(node.getProperty("videoposter"));
			if(videoposter == false){
				String blacklistVideoNode = domain + "/config/blacklist/entry/"+node.getId()+"/user";
				List<FsNode> blacklistUserNodes = Fs.getNodes(blacklistVideoNode,1);
				FsNode blacklistUserNode = blacklistUserNodes.get(0);
				blacklistProviders.add(blacklistUserNode.getReferid());
			}
		}
		
	}
	
	/*TODO:
	 * This can also be moved to Blacklist.java
	 */
	public static boolean checkIsPublic(String referId, List<String> blacklistProviders) {
		String user = URIParser.getUserIdFromUri(referId);
		System.out.println("CheckIsPublic: " + user);
		for (String blacklsit_entry : blacklistProviders) {	
			blacklsit_entry = blacklsit_entry.replaceAll("/domain/euscreenxl/user/", "");

			if(blacklsit_entry.equals(user)){
				System.out.println("CHECK IS PUBLIC : " + "No");
				return false;
			}
		}
		return true;
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
	
	private void populateBookmarks(String user){
		address = address + user + "/publications/1/bookmark";
		List<FsNode> xmlCallList = Fs.getNodes(this.address, 2);

		for (FsNode node : xmlCallList) {
			
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
	
	
}
