package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screencomponent.component.ScreenComponent;

public class Bookmarks extends ScreenComponent{
	private List<Bookmark> bookmarklist = new ArrayList<Bookmark>();	
	public static List<String> blacklistProviders;
	private String address = Configuration.getDomain() + "/user/";
	private Blacklist blacklist;
	private int page = 0;
	private int itemsPerPage = 4;
	private int pages;
	
	public Bookmarks(Screen s, String user) {
		super(s);
		blacklist = new Blacklist();
		
		this.populateBookmarks(user);
	}
	
	@JSONField(field = "page")
	public int getPage(){
		return this.page;
	}
	
	public void setPage(int page){
		System.out.println("setPage( " + page + " )");
		this.page = page;
	}
	
	@JSONField(field = "pages")
	public int getPages(){
		return this.pages;
	}
		
	@JSONField(field = "bookmarks")
	public List<Bookmark> getBookmarklist() {
		System.out.println("getBookmarkList()");
		int start = page * itemsPerPage;
		int end = (start + itemsPerPage) < bookmarklist.size() ? start + itemsPerPage : bookmarklist.size();  
		return bookmarklist.subList(start, end);
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
			String videoTitle = videoInfo.getProperty("TitleSet_TitleSetInEnglish_title");
			String provider = videoInfo.getProperty("publisherbroadcaster");
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
				Bookmark bookmark = new Bookmark(bookmarkId, videoId, mount, screenshot, videoTitle, provider, blacklist.checkIsPublic(referId));
				bookmarklist.add(bookmark);
			}
		}
		Double pages = ((double)bookmarklist.size()) / itemsPerPage;
		this.pages = (int) Math.ceil(pages);
	}	
	
	
}
