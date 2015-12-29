/* 
* ScreeneventsApplication.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of Lou, related to the Noterik Springfield project.
* It was created as a example of how to use the multiscreen toolkit
*
* Screenevents app is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Helloworld app is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Screenevents app.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.lou.application.types;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.types.DTO.MediaItem;
import org.springfield.lou.application.types.DTO.TextContent;
import org.springfield.lou.screen.Screen;

public class EuscreenpublicationbuilderApplication extends Html5Application{
    public Layout layouts;
    public Theme themes;
    public Bookmarks bookmarks;
    public Collections collections;
    private FsNode currentLayout;
    private String currentLayoutStyle;
	private FsNode currentTheme;
	private String currentUser;
	public static String ipAddress = "";
	public static boolean isAndroid;
	private Overlaydialog overlayDialog = null;
	private String oldPublicationID = "";
	public static HashMap<String, String> layoutWithStyle = new HashMap<String, String>();
	public static HashMap<String, String> styleWithId = new HashMap<String, String>(); 

    public String getCurrentLayoutStyle() {
		return currentLayoutStyle;
	}
    
	public void setCurrentLayoutStyle(String currentLayoutStyle) {
		this.currentLayoutStyle = currentLayoutStyle;
	}
    public FsNode getCurrentTheme() {
		return currentTheme;
	}

	public void setCurrentTheme(FsNode currentTheme) {
		this.currentTheme = currentTheme;
	}
	
 	public FsNode getCurrentLayout() {
		return currentLayout;
	}

	public void setCurrentLayout(FsNode currentLayout) {
		this.currentLayout = currentLayout;
	}

	public EuscreenpublicationbuilderApplication(String id) {
		super(id); 
	}
	
 	/*
 	 * This method is called when a browser window opens the application
 	 * @see org.springfield.lou.application.Html5Application#onNewScreen(org.springfield.lou.screen.Screen)
 	 */
    public void onNewScreen(Screen s) {
        loadStyleSheet(s, "generic"); //Loading the genereic style from css folder
        loadStyleSheet(s, "bootstrap");
        loadStyleSheet(s, "font-awesome");
        loadStyleSheet(s, "font-awesome.min");
        loadStyleSheet(s, "tinycolorpicker");
        loadContent(s, "readycheck");
        loadContent(s, "embedlib");
        loadContent(s, "comparison");
        loadContent(s, "header");
        loadContent(s, "iframesender");
       
        actionGeneratelayout(s, "");

        //Get Current user
        this.getCurrentUser(s);
        
        this.handleEditStatus(s);
     	
     }
    
    public void handleEditStatus(Screen s) {
    	 /*
         * TODO: If a method gets really long, try splitting it up in multiple functions. It keeps your code clear and more readable. It also 
         * promotes reusability. So for example, this could be moved to a function called handleEditStatus(Screen s) or something. 
         */
        if(s.getParameter("status").equals("edit")){
            String poster_url = s.getParameter("posterid");     
            
            JSONArray arr = Publication.editPublication(poster_url);
            JSONObject idOb = (JSONObject) arr.get(0);
            this.oldPublicationID = idOb.get("id").toString();
            
            
            s.putMsg("header", "", "modeEdit()");

			//Set layout
            JSONObject layout_json = (JSONObject)arr.get(1);
			String[] layout =  ((String) layout_json.get("layout_type")).split("_");
			this.actionSetlayout(s, layout[1]);

			//Set theme
			JSONObject colorSchema_json = (JSONObject)arr.get(2);
			String[] colorSchema = ((String) colorSchema_json.get("colorSchema")).split("_");
            this.actionSettheme(s, colorSchema[1]);
			
			//TODO: Why do we remove stuff when loading a new screen, nothing should be there.
            /*
             * We just remove layout and colorschemas tab when we are in edit mode
             * I will check again
             * [SHUKRI]
             */
			s.removeContent("layoutsContent");
			s.removeContent("colorschemesContent");
	    	
			s.putMsg("buildContent", "", "edit(" + arr + ")");
	    	s.putMsg("header", "", "modeEdit()");

        }

    }
    
	public void actionSetlayout(Screen s, String c) {
		System.out.println("======== actionSetlayout(" + c + ") ========");
        loadContent(s, "buildContent");
        loadContent(s, "bookmarksContent");
        
        this.loadBookmarks(s);

        FsNode node = layouts.getLayoutBy(Integer.parseInt(c));
		setCurrentLayout(node);
		setCurrentLayoutStyle(node.getProperty("css"));
		JSONObject message = new JSONObject();
		message.put("html", node.getProperty("template"));
		message.put("style", node.getProperty("css"));
		s.putMsg("buildContent", "", "update(" + message + ")");
	}
	
    //Load bookmarks
	public void loadBookmarks(Screen s) {
    	bookmarks = new Bookmarks(currentUser);
 		
     	int cnt_bookmark = 0;

     	JSONArray bookmarkJsonArray = new JSONArray();
     	
     	for (Bookmark bmi : bookmarks.getBookmarklist()) {
     		JSONObject bookmarkJson = new JSONObject();
     		String id = "bookmark_"+ cnt_bookmark;
     		
     		bookmarkJson.put("id", id);
     		bookmarkJson.put("screenshot", bmi.getScreenshot());
     		bookmarkJson.put("video", bmi.getVideo());
     		bookmarkJson.put("ispublic", bmi.getIsPublic());
     		bookmarkJsonArray.add(bookmarkJson);
     		
			cnt_bookmark++;
		}

     	//Load collections
        collections = new Collections(currentUser);   
        JSONArray  collectionsArray = new JSONArray();
        
     	int cnt_header = 1;
     	
     	for (Collection col : collections.getCollectionlist()) {
         	
     		String right_header_div_id = "right_header_" + cnt_header;
     		String right_toggle_div_id = "toggle_" + cnt_header;
     		
     		JSONObject collection = new JSONObject();
     		collection.put("right_header_id", right_header_div_id);
     		collection.put("right_toggle_id", right_toggle_div_id);
     		collection.put("collection_name", col.getName());
     		
     		JSONArray collectionBookmarksArray = new JSONArray();
     		
     		for (Bookmark bk : col.getVideos()) {
         		
     			try{
     				JSONObject collectionBookmarkJson = new JSONObject();
     				String id = "bookmark_" + cnt_bookmark;		
     				
     				collectionBookmarkJson.put("id", id);
     				collectionBookmarkJson.put("screenshot", bk.getScreenshot());
     				collectionBookmarkJson.put("video", bk.getVideo());
     				collectionBookmarkJson.put("ispublic", bk.getIsPublic());
     				
     				collectionBookmarksArray.add(collectionBookmarkJson);
	     			
            		cnt_bookmark++;

     			}catch(Exception e){
     				e.printStackTrace();
     			}
        		
			}
     		
     		collection.put("bookmarks", collectionBookmarksArray);
 			collectionsArray.add(collection);

 			cnt_header++;
		}
   
    	s.putMsg("bookmarksContent", "", "displayBookmarks(" + bookmarkJsonArray + ")");
    	s.putMsg("bookmarksContent", "", "displayCollections(" + collectionsArray + ")");
    	s.putMsg("bookmarksContent", "", "closeAll(" + cnt_header + ")");
	}
	
	
	//Set theme actions
	 public void actionSettheme(Screen s, String c) {
		 System.out.println("======== actionSettheme(" + c + ") ========");

	    	FsNode node = themes.getLayoutBy(Integer.parseInt(c));
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
	 }
	

	public void actionGeneratelayout(Screen s, String c) {
		System.out.println("actionGenerateLayout()");
        this.loadContent(s, "layoutsContent");
        
        //Load layouts
        layouts = new Layout();
		
		JSONArray jsonlayoutsarray = new JSONArray();
		
		
    	for(int i = 0; i < layouts.getLayouts().size(); i++) {
    		
    		String layoutStr = layouts.getLayouts().get(i).getProperty("css");
    		String[] splits = layoutStr.split("/");
    		String lo = splits[splits.length -1];
    		lo = lo.trim();
    		
    		layoutWithStyle.put(lo, "layout_"+ i);
    		
    		JSONObject jsonlayoutobject = new JSONObject();
    		jsonlayoutobject.put("id", "layout_"+ i);
    		jsonlayoutobject.put("name", layouts.getLayouts().get(i).getProperty("name"));
    		jsonlayoutobject.put("icon", layouts.getLayouts().get(i).getProperty("icon"));
    		jsonlayoutobject.put("description", layouts.getLayouts().get(i).getProperty("description"));
    		
    		jsonlayoutsarray.add(jsonlayoutobject);
    		
    	}
    	
    	s.putMsg("layoutsContent", "", "listLayouts(" + jsonlayoutsarray + ")");
    	
	}
	
	public void actionGeneratecolorschemes(Screen s, String c) {
		System.out.println("actionGeneratecolorschemes()");
		this.removeContent(s, "layoutsContent");
		this.removeContentAllScreens("layoutsContent");
        this.loadContent(s, "colorschemesContent");
                
        //Load color schemes
        themes = new Theme();
		
		JSONArray jsonThemeArray = new JSONArray();
		
		
    	for(int i = 0; i < themes.getThemes().size(); i++) {
    		styleWithId.put(themes.getThemes().get(i).getProperty("css").trim(), "theme_"+ i);

    		JSONObject jsonThemeObject = new JSONObject();
    		jsonThemeObject.put("id", "theme_"+ i);
    		jsonThemeObject.put("name", themes.getThemes().get(i).getProperty("name"));
    		jsonThemeObject.put("icon", themes.getThemes().get(i).getProperty("icon"));
    		
    		jsonThemeArray.add(jsonThemeObject);
    		
    	}
		
    	s.putMsg("colorschemesContent", "", "listThemes(" + jsonThemeArray + ")");

	}
	
	
	//Generate build
	public void actionGeneratebuild(Screen s, String c) {
		System.out.println("========GENERATE BUILD======");
    	
	}
	
	//Action Preview
	public void actionPreview(Screen s, String c) {

		try {
			this.overlayDialog = new Overlaydialog(s);
			this.overlayDialog.render();
			JSONObject json = null;
			if(c != null) json = (JSONObject)new JSONParser().parse(c);
			Publication publication = new Publication();
			if(getCurrentTheme() != null) publication.theme.setCurrentTheme(getCurrentTheme());
			publication.template.layout.setCurrentLayout(getCurrentLayout());
			publication.template.layout.setCurrentLayoutStyle(getCurrentLayoutStyle());
			
			if(json.get("mediaItem") != null){
				JSONArray mediaArray = (JSONArray)json.get("mediaItem");
				for(int i = 0; i < mediaArray.size(); i++){
					JSONObject ob = (JSONObject)mediaArray.get(i);
					String mediaId = (String)ob.get("id");
					String mediaValue = (String)ob.get("value");
					String mediaPoster = (String)ob.get("poster");
					publication.template.sections.mediaSection.setMediaItems(new MediaItem(mediaId, mediaValue, mediaPoster));
				}
			}
			
			if(json.get("textItem") != null){
				JSONArray textArray = (JSONArray)json.get("textItem");

				for(int i = 0; i < textArray.size(); i++){
					JSONObject ob = (JSONObject)textArray.get(i);
					String textId = (String)ob.get("id");
					String textValue = (String)ob.get("value");
					
					publication.template.sections.textSection.setTextContents(new TextContent(textId, textValue));
				}
			}

			
			JSONObject publicationJSON = Publication.createPreviewXML(publication, this.currentUser);
			this.overlayDialog.setHTML(publicationJSON.get("xml").toString());
			this.overlayDialog.setVisible(true);
			this.overlayDialog.update();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void actionClosepreview(Screen s, String c){
		this.overlayDialog.setURL("");
		this.overlayDialog.setVisible(false);
		this.overlayDialog.update();
	}
	
	//Add media item external identifier
	public void actionAddexternalidentifier(Screen s, String c){
		try {
			JSONObject json = (JSONObject)new JSONParser().parse(c);
			String data_type = json.get("dataType").toString().toLowerCase();
			String identifier = json.get("identifier").toString();
			
			String container = "#" + json.get("container").toString();
	    	JSONObject message = new JSONObject();
	    	
			if(data_type.equals("youtubeitem")) {
				String[] youtubeId = identifier.split("=");
//				https://www.youtube.com/watch?v=A4Tme1q2iew
				String video = "<iframe class=\"videoAfterDrop ui-draggable\" src='" + "http://www.youtube.com/embed/" + youtubeId[1] + "' frameborder=\"0\" allowfullscreen></iframe>";
		    	message.put("video", video);
			}else if (data_type.equals("vimeoitem")) {
				String[] vimeoId = identifier.split("/");
				String video = "<iframe class=\"videoAfterDrop\" src='" + "https://player.vimeo.com/video/" + vimeoId[3] + "' frameborder=\"0\" allowfullscreen></iframe>";
		    	message.put("video", video);
			}
			
			message.put("container", container);
	    	
			s.putMsg("buildContent", "", "setmediaitem(" + message + ")");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	//Create publication XML
	public void actionProccesspublication(Screen s, String c){
		try {
			JSONObject json = (JSONObject)new JSONParser().parse(c);
			Publication publication = new Publication();
			
			publication.theme.setCurrentTheme(getCurrentTheme());
			publication.template.layout.setCurrentLayout(getCurrentLayout());
			publication.template.layout.setCurrentLayoutStyle(getCurrentLayoutStyle());
			
			JSONArray mediaArray = (JSONArray)json.get("mediaItem");
			JSONArray textArray = (JSONArray)json.get("textItem");

			for(int i = 0; i < mediaArray.size(); i++){
				JSONObject ob = (JSONObject)mediaArray.get(i);
				String mediaId = (String)ob.get("id");
				String mediaValue = (String)ob.get("value");
				String mediaPoster = (String)ob.get("poster");
				publication.template.sections.mediaSection.setMediaItems(new MediaItem(mediaId, mediaValue, mediaPoster));
			}
			
			for(int i = 0; i < textArray.size(); i++){
				JSONObject ob = (JSONObject)textArray.get(i);
				String textId = (String)ob.get("id");
				String textValue = (String)ob.get("value");
				
				publication.template.sections.textSection.setTextContents(new TextContent(textId, textValue));
			}
			if(json.get("mode") != null){
				if(json.get("mode").toString().trim().equals("edit")){
					JSONObject publicationJSON = Publication.editXml(publication, this.currentUser, s.getId(), this.oldPublicationID);
					s.putMsg("iframesender", "", "sendToParent(" + publicationJSON + ")");
				}
			}else{
				JSONObject publicationJSON = Publication.createXML(publication, this.currentUser, s.getId());
				s.putMsg("iframesender", "", "sendToParent(" + publicationJSON + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getCurrentUser(Screen s){

		String[] arr = s.getId().split("/");
    	this.currentUser = arr[4];
	}

	//Get meta headers
	public String getMetaHeaders(HttpServletRequest request) {
		ipAddress=getClientIpAddress(request);
				
		String browserType = request.getHeader("User-Agent");
		if(browserType.indexOf("Mobile") != -1) {
			String ua = request.getHeader("User-Agent").toLowerCase();
			isAndroid = ua.indexOf("android") > -1; //&& ua.indexOf("mobile");	
		}	
		return "";
	}
	
	private static final String[] HEADERS_TO_TRY = { 
		"X-Forwarded-For",
		"Proxy-Client-IP",
		"WL-Proxy-Client-IP",
		"HTTP_X_FORWARDED_FOR",
		"HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP",
		"HTTP_CLIENT_IP",
		"HTTP_FORWARDED_FOR",
		"HTTP_FORWARDED",
		"HTTP_VIA",
		"REMOTE_ADDR" 
	};
	
	//Get IP address
	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
}