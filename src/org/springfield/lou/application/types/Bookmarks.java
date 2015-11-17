package org.springfield.lou.application.types;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Bookmarks {
	public List<Bookmark> bookmarklist = new ArrayList<Bookmark>();
	private List<FsNode> xmlCallList;
	private static String domain = "/domain/euscreenxl";

	private String address = domain + "/user/";
	private static String ipAddress = "";
	private static boolean isAndroid = false;
	
	public List<Bookmark> getBookmarklist() {
		return bookmarklist;
	}

	
	public Bookmarks(String user) {
		super();
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
				System.out.println(checkIsPublic(videoId));
				Bookmark bookmark = new Bookmark(bookmarkId, videoId, videoName, mount, screenshot, checkIsPublic(videoId));
				bookmarklist.add(bookmark);
			}
		}
		
	}
	
	public static boolean checkIsPublic(String Id) {
		String blacklistEntryUrl = domain + "/config/blacklist/entry/" + Id;
		System.out.println("=====================================");
		System.out.println("CHECK IS PUBLIC URL = " + blacklistEntryUrl);
		FsNode isPublicNode = Fs.getNode(blacklistEntryUrl);
		if(isPublicNode !=  null && isPublicNode.getProperty("videoposter") != null) {
			if(Boolean.parseBoolean(isPublicNode.getProperty("videoposter")) == true) {
				return true;
			}
		}
		
		return false;
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
	
	private static void sendTicket(String videoFile, String ipAddress, String ticket) throws IOException {
		URL serverUrl = new URL("http://82.94.187.227:8001/acl/ticket");
		HttpURLConnection urlConnection = (HttpURLConnection)serverUrl.openConnection();
	
		Long Sytime = System.currentTimeMillis();
		Sytime = Sytime / 1000;
		String expiry = Long.toString(Sytime+(15*60));
		
		// Indicate that we want to write to the HTTP request body
		
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		videoFile=videoFile.substring(1);
		
		// Writing the post data to the HTTP request body
		BufferedWriter httpRequestBodyWriter = 
		new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		String content="";
		if (isAndroid){
			content = "<fsxml><properties><ticket>"+ticket+"</ticket>"
			+ "<uri>/"+videoFile+"</uri><ip>"+ipAddress+"</ip> "
			+ "<role>user</role>"
			+ "<expiry>"+expiry+"</expiry><maxRequests>10</maxRequests></properties></fsxml>";
			isAndroid=false;
			//System.out.println("Android ticket!");
		}
		else {
			content = "<fsxml><properties><ticket>"+ticket+"</ticket>"
			+ "<uri>/"+videoFile+"</uri><ip>"+ipAddress+"</ip> "
			+ "<role>user</role>"
			+ "<expiry>"+expiry+"</expiry><maxRequests>10</maxRequests></properties></fsxml>";
		}

		httpRequestBodyWriter.write(content);
		httpRequestBodyWriter.close();
	
	}	
}
