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
	private static String domain = "/domain/euscreenxl";
	public static List<String> blacklistProviders = new ArrayList();
	private String address = domain + "/user/";

	public List<Bookmark> getBookmarklist() {
		return bookmarklist;
	}

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
	
	public Bookmarks(String user) {
		super();
		//Seed blacklist
		
		this.blacklistProviders = new ArrayList<String>();
		this.seedBlacklist();
		
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
			//String videoMount = referNodes.get(0).getProperty("mount");
			
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
			
			/*
			if(videoMount.contains("noterik.com") && !videoMount.contains("rtmp://")){
				String[]videoMountArr = videoMount.split(","); 	
				mount = videoMountArr[0];
			}*/
			
			if(mount != null){
				
				Bookmark bookmark = new Bookmark(bookmarkId, videoId, videoName, mount, screenshot, checkIsPublic(referId, blacklistProviders));
				bookmarklist.add(bookmark);
			}
		}
		
	}
	
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
	
}
