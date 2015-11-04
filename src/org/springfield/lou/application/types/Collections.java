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

public class Collections {
	public List<Collection> collectionlist = new ArrayList<Collection>();
	private List<FsNode> xmlCallList;
	private String address = "/domain/euscreenxl/user/"; 
	private static String ipAddress = "";
	private static boolean isAndroid = false;
	
	public List<Collection> getCollectionlist() {
		return collectionlist;
	}

	
	public Collections(String user) {
		super();
		address = address + user + "/publications/1/collection";
		
		this.xmlCallList = Fs.getNodes(this.address, 2);

		for (FsNode node : this.xmlCallList) {

			String collectionId = node.getId();
			
			String currCollectionAddress = address + "/" + collectionId;
			List<FsNode> collectionNodesList = Fs.getNodes(currCollectionAddress, 0);
			
			FsNode colNode = Fs.getNode(currCollectionAddress);
			String collection_name = colNode.getProperty("name");
			
			List<Bookmark> videos = new ArrayList<Bookmark>();
			
			for (FsNode collection : collectionNodesList) {
		 
				if(!collection.getId().contentEquals("1")){

					String videoId = collection.getId();
					String videoUrl = currCollectionAddress + "/video/" +videoId;

					FsNode videoNd = Fs.getNode(videoUrl);
					String referId = videoNd.getReferid();
					
					String referUrl = referId + "/" + "rawvideo";
					List<FsNode> referNodes = Fs.getNodes(referUrl, 1);
					
					String videoInfoUrl = currCollectionAddress + "/video/" + videoId;
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
						videos.add(new Bookmark(collectionId, videoId, videoName, mount, screenshot));
					}
				}				
			}
			collectionlist.add(new Collection(collection_name, videos));
		}
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
