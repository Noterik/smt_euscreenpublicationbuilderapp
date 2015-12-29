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

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Collections {
	/*
	 * TODO: Does this really need to be public, I'd prefer a Getter and Setter for this.
	 */
	public List<Collection> collectionlist = new ArrayList<Collection>();
	private List<FsNode> xmlCallList;
	private String address = "/domain/euscreenxl/user/"; 
	
	/*
	 * TODO: Read my comments in Bookmarks.java about this. 
	 */
	public static List<String> blacklistProviders;
	
	public List<Collection> getCollectionlist() {
		return collectionlist;
	}
	
	public void seedBlacklist() {
		String blacklistEntryUrl = "/domain/euscreenxl/config/blacklist/";
		FSList blacklist = FSListManager.get(blacklistEntryUrl);
		
		for (FsNode node : blacklist.getNodes()) {
			boolean videoposter = Boolean.parseBoolean(node.getProperty("videoposter"));
			if(videoposter == false){
				String blacklistVideoNode = "/domain/euscreenxl/config/blacklist/entry/"+node.getId()+"/user";
				List<FsNode> blacklistUserNodes = Fs.getNodes(blacklistVideoNode,1);
				FsNode blacklistUserNode = blacklistUserNodes.get(0);
				blacklistProviders.add(blacklistUserNode.getReferid());
			}
		}
		
	}

	
	public Collections(String user) {
		super();
		
		this.blacklistProviders = new ArrayList<String>();
		this.seedBlacklist();
		address = address + user + "/publications/1/collection";

		this.xmlCallList = Fs.getNodes(this.address, 2);

		
		for (FsNode node : this.xmlCallList) {

			String collectionId = node.getId();

			String currCollectionAddress = address + "/" + collectionId;
			try {
				List<FsNode> collectionNodesList = Fs.getNodes(currCollectionAddress, 0);
				
				FsNode colNode = Fs.getNode(currCollectionAddress);
				String collection_name = colNode.getProperty("name");
				
				List<Bookmark> videos = new ArrayList<Bookmark>();
				
				for (FsNode collection : collectionNodesList) {
			 
					if(!collection.getId().contentEquals("1")){

						String videoId = collection.getId();
						
						String videoUrl = currCollectionAddress + "/collectionitem/" +videoId+"/video/"+videoId;
						
						FsNode videoNd = Fs.getNode(videoUrl);
						
						if(videoNd != null){
							try {
								String referId = videoNd.getReferid();
								System.out.println("referId: " + referId);
								String referUrl = referId + "/" + "rawvideo";
								List<FsNode> referNodes = Fs.getNodes(referUrl, 1);
								System.out.println("REFER NODES: " + referNodes.size());
								String videoInfoUrl = currCollectionAddress + "/collectionitem/" +videoId+"/video/"+videoId;
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
									videos.add(new Bookmark(collectionId, videoId, videoName, mount, screenshot, Bookmarks.checkIsPublic(referId, blacklistProviders)));
								}	
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}				
				}
				
				collectionlist.add(new Collection(collection_name, videos));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}
