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
	private String address = "/domain/euscreenxl/user/david/publications/1/bookmark"; 
	private static String ipAddress = "";
	private static boolean isAndroid = false;
	
	public List<Bookmark> getBookmarklist() {
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
			FsNode videoNd = Fs.getNode(videoUrl + "/" +videoId);
			String referId = videoNd.getReferid();
			String referUrl = referId + "/" + "rawvideo";
			List<FsNode> referNode = Fs.getNodes(referUrl, 1);
			String videoMount = referNode.get(0).getProperty("mount");

			String[]videoMountArr = videoMount.split(","); 
			String videoInfoUrl = videoUrl + "/" + videoId;
			FsNode videoInfo = Fs.getNode(videoInfoUrl);
			String videoName = videoInfo.getProperty("TitleSet_TitleSetInEnglish_title");
			String screenshot = videoInfo.getProperty("screenshot");
			
			String mount = videoMountArr[0];
			String ap = "";
			if (mount.indexOf("http://")==-1 && mount.indexOf("rtmp://")==-1) {
				Random randomGenerator = new Random();
				Integer random= randomGenerator.nextInt(100000000);
				String ticket = Integer.toString(random);
				
				String videoFile= mount;
				ipAddress = EuscreenpublicationbuilderApplication.ipAddress;
				isAndroid = EuscreenpublicationbuilderApplication.isAndroid;
				
				try{						
					//System.out.println("CallingSendTicket");						
					sendTicket(videoFile,ipAddress,ticket);
				} catch (Exception e) {}
				
				
				ap = mount+"?ticket="+ticket;
			} else if (mount.indexOf(".noterik.com/progressive/") > -1) {
				Random randomGenerator = new Random();
				Integer random= randomGenerator.nextInt(100000000);
				String ticket = Integer.toString(random);
				
				String videoFile = mount.substring(mount.indexOf("progressive")+11);
				
				ipAddress = EuscreenpublicationbuilderApplication.ipAddress;
				isAndroid = EuscreenpublicationbuilderApplication.isAndroid;
				
				try{						
					//System.out.println("CallingSendTicket");						
					sendTicket(videoFile,ipAddress,ticket);
				} catch (Exception e) {}
				
				ap = mount+"?ticket="+ticket;				
			} 
			System.out.println(videoId);
			Bookmark bookmark = new Bookmark(bookmarkId, videoId, videoName, ap);

			bookmarklist.add(bookmark);
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
	
		// Reading from the HTTP response body
		Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
		while(httpResponseScanner.hasNextLine()) {
			System.out.println(httpResponseScanner.nextLine());
		}
		httpResponseScanner.close();		
	}	
}
