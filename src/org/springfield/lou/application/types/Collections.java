package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.ViewFactory;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screencomponent.component.ScreenComponent;

public class Collections extends ScreenComponent {
	/*
	 * TODO: Does this really need to be public, I'd prefer a Getter and Setter
	 * for this.
	 */
	private List<Collection> collectionlist = new ArrayList<Collection>();
	private List<FsNode> xmlCallList;
	private String address = "/domain/euscreenxl/user/";
	private Blacklist blacklist;

	public Collections(Screen s, String user) {
		super(s);
		blacklist = new Blacklist();
		populateCollections(user);
	}


	@JSONField(field = "collections")
	public List<Collection> getCollectionlist() {		
		return collectionlist;
	}
	
	public Collection getCollectionById(String colId){
		for(Collection col : collectionlist){
			if(col.getId().equals(colId)){
				return col;
			}
		}
		return null;
	}

	private void populateCollections(String user) {
		address = address + user + "/publications/1/collection";
		this.xmlCallList = Fs.getNodes(this.address, 2);

		for (FsNode node : this.xmlCallList) {

			String collectionId = node.getId();

			String currCollectionAddress = address + "/" + collectionId;
			try {
				List<FsNode> collectionNodesList = Fs.getNodes(
						currCollectionAddress, 0);

				FsNode colNode = Fs.getNode(currCollectionAddress);
				String collection_name = colNode.getProperty("name");

				List<Bookmark> videos = new ArrayList<Bookmark>();

				for (FsNode collection : collectionNodesList) {

					if (!collection.getId().contentEquals("1")) {

						String videoId = collection.getId();

						String videoUrl = currCollectionAddress
								+ "/collectionitem/" + videoId + "/video/"
								+ videoId;

						FsNode videoNd = Fs.getNode(videoUrl);

						if (videoNd != null) {
							try {
								String referId = videoNd.getReferid();
								System.out.println("referId: " + referId);
								String referUrl = referId + "/" + "rawvideo";
								List<FsNode> referNodes = Fs.getNodes(referUrl,
										1);
								System.out.println("REFER NODES: "
										+ referNodes.size());
								String videoInfoUrl = currCollectionAddress
										+ "/collectionitem/" + videoId
										+ "/video/" + videoId;
								FsNode videoInfo = Fs.getNode(videoInfoUrl);
								String videoTitle = videoInfo
										.getProperty("TitleSet_TitleSetInEnglish_title");
								String videoProvider = videoInfo
										.getProperty("publisherbroadcaster");
								String screenshot = videoInfo
										.getProperty("screenshot");

								String mount = "";

								for (FsNode referNode : referNodes) {
									if (referNode.getProperty("format").equals(
											"MP4")) {
										String[] mounts = referNode
												.getProperty("mount")
												.split(",");
										for (String mnt : mounts) {
											if (!mnt.startsWith("http://") && !mnt.startsWith("https://")) {
												mount = "http://" + mnt + ".noterik.com/progressive/" + mnt + referNode.getPath() + "/raw.mp4";
												break;
											}
											mount = mnt;
										}
										break;
									}
								}
								
								if(mount != null){
									videos.add(new Bookmark(videoId, videoId, mount, screenshot, videoTitle, videoProvider, blacklist.checkIsPublic(referId)));
								}	
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				collectionlist.add(new Collection(colNode.getId(), collection_name, videos));
			} catch (Exception e) {
				System.out.println("COLLECTION EXCEPTION");
				e.printStackTrace();
			}
		}
	}


	public void getPageForCollection(String collection, Integer page) {
		System.out.println("nextPageForCollection(" + collection + ")");
		Collection col = getCollectionById(collection);
		if(col != null){
			col.setPage(page);
		}
	}
}
