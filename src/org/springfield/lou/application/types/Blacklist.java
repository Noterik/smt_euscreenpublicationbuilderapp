package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.mojo.ftp.URIParser;

public class Blacklist {
	private static List<String> blacklistProviders;

	public static List<String> getBlacklistProviders() {
		return blacklistProviders;
	}

	public static void setBlacklistProviders(String blacklistProvider) {
		Blacklist.blacklistProviders.add(blacklistProvider);
	}

	public Blacklist() {
		super();
		this.blacklistProviders = new ArrayList();
		populateBlacklist();
		
	}
	
	public void populateBlacklist() {
		String blacklistEntryUrl = Configuration.getDomain() + "/config/blacklist/";
		FSList blacklist = FSListManager.get(blacklistEntryUrl);
		
		for (FsNode node : blacklist.getNodes()) {
			boolean videoposter = Boolean.parseBoolean(node.getProperty("videoposter"));
			if(videoposter == false){
				String blacklistVideoNode = Configuration.getDomain() + "/config/blacklist/entry/"+node.getId()+"/user";
				List<FsNode> blacklistUserNodes = Fs.getNodes(blacklistVideoNode,1);
				FsNode blacklistUserNode = blacklistUserNodes.get(0);
				this.setBlacklistProviders(blacklistUserNode.getReferid());

			}
		}
	}
	
	public boolean checkIsPublic(String referId) {
		String user = URIParser.getUserIdFromUri(referId);
		System.out.println("CheckIsPublic: " + user);
		for (String blacklsit_entry : getBlacklistProviders()) {	
			blacklsit_entry = blacklsit_entry.replaceAll("/domain/euscreenxl/user/", "");

			if(blacklsit_entry.equals(user)){
				System.out.println("CHECK IS PUBLIC : " + "No");
				return false;
			}
		}
		return true;
	}
}
