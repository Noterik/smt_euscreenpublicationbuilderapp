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

        //loadContent(s, "left");     
        //loadContent(s, "right");
        
        //Get Current user
        this.getCurrentUser(s);
        
     	
        //Catch modes
        if(s.getParameter("status").equals("edit")){
            String poster_url = s.getParameter("posterid");     
            JSONArray arr = Publication.editPublication(poster_url);
            JSONObject idOb = (JSONObject) arr.get(0);
            this.oldPublicationID = idOb.get("id").toString();
            
            s.putMsg("header", "", "modeEdit()");

			//Set layout
            JSONObject layout_json = (JSONObject)arr.get(1);
			String layout = (String) layout_json.get("layout_type");


			if(layout.equals("layout_0")) {
				this.actionSetlayout(s, "0");
				
			}else if(layout.equals("layout_1")) {
				this.actionSetlayout(s, "1");
			
			}else if(layout.equals("layout_2")) {
				this.actionSetlayout(s, "2");
				
			}

			//Set theme
			JSONObject colorSchema_json = (JSONObject)arr.get(2);
            String colorSchema = (String) colorSchema_json.get("colorSchema");
            System.out.println("COLOR SCHEMA: " + colorSchema);
            
			if(colorSchema == null || colorSchema.equals("theme_0")) {
				this.actionSettheme(s, "0");
				
			}else if(colorSchema.equals("theme_1")) {
				this.actionSettheme(s, "1");

			}else if(colorSchema.equals("theme_2")) {
				this.actionSettheme(s, "2");

			}else if(colorSchema.equals("theme_3")) {
				this.actionSettheme(s, "3");
				
			}else if(colorSchema.equals("theme_4")) {
				this.actionSettheme(s, "4");
				
			}else if(colorSchema.equals("theme_5")) {
				this.actionSettheme(s, "5");

			}
			s.removeContent("layoutsContent");
	    	s.putMsg("layoutsContent", "", "closeLayoutsTab()");
			s.putMsg("buildContent", "", "edit(" + arr + ")");
	    	s.putMsg("header", "", "modeEdit()");

        }else {
	    	s.putMsg("header", "", "showbuttons(" + ")");
	    	
        }
        
     
    }
    
	public void actionSetlayout(Screen s, String c) {
		System.out.println("======== actionSetlayout(" + c + ") ========");
        loadContent(s, "buildContent");
        loadContent(s, "bookmarksContent");
        
        this.loadBookmarks(s);

		if(c.equals("0")){
			FsNode node = layouts.getLayoutBy(0);
			setCurrentLayout(node);
			setCurrentLayoutStyle(node.getProperty("css"));
			JSONObject message = new JSONObject();
			message.put("html", node.getProperty("template"));
			message.put("style", node.getProperty("css"));
			s.putMsg("buildContent", "", "update(" + message + ")");
		
		}else if(c.equals("1")) {
			FsNode node = layouts.getLayoutBy(1);
	    	setCurrentLayout(node);
	    	setCurrentLayoutStyle(node.getProperty("css"));
	    	JSONObject message = new JSONObject();
	    	message.put("html", node.getProperty("template"));
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "update(" + message + ")");
		
		}else if(c.equals("2")) {
	    	FsNode node = layouts.getLayoutBy(2);
	    	setCurrentLayout(node);
	    	setCurrentLayoutStyle(node.getProperty("css"));
	    	JSONObject message = new JSONObject();
	    	message.put("html", node.getProperty("template"));
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "update(" + message + ")");
		}

	}
	
    //Load bookmarks
	public void loadBookmarks(Screen s) {
    	bookmarks = new Bookmarks(currentUser);
    	//String bookmarkLayout = "<div class=\"right-header\" id=\"right_header_0\">Bookmarks</div>" + "<div class=\"right-header\" id=\"collections\">Collections</div>";
    	String bookmarkLayout = "";
    	bookmarkLayout += "<div id=\"toggle_0\" class=\"tgl\">";
 
 		
 		System.out.println("=============================LOAD BOOKMARK===========================");
 		
     	int cnt_bookmark = 0;
     	for (Bookmark bmi : bookmarks.getBookmarklist()) {
     	
     		String id = "bookmark_"+ cnt_bookmark;
     		if(bmi.getIsPublic() == false){ 
	    		bookmarkLayout += "<div class=\"not_public_video\"><p>Unfortunately due to copyright agreements with the Content Provider this video cannot be used for making a video poster.</p><div id=\"" + id +"\" class=\"drag_bookmark\"><video  poster='"+bmi.getScreenshot()+"' src=\"" + bmi.getVideo() + "\" controls></video></div>";
	    		bookmarkLayout += "<script type=\"text/javascript\">"
	    				+ "eddie.getComponent('embedlib').loaded().then(function(){"
	    				+ "		EuScreen.getVideo({src: '" + bmi.getVideo() + "', poster: '" + bmi.getScreenshot() + "', controls: true}, function(html){"
	    				+ "			jQuery('#" + id + "').html(html);"
	    				+ "		});"
	    				+ "});</script>";
	    		bookmarkLayout += "</div>";
     		}else {
	    		bookmarkLayout += "<div id=\"" + id +"\" class=\"drag_bookmark\"><video  poster='"+bmi.getScreenshot()+"' src=\"" + bmi.getVideo() + "\" controls></video></div>";
	    		bookmarkLayout += "<script type=\"text/javascript\">"
	    				+ "eddie.getComponent('embedlib').loaded().then(function(){"
	    				+ "		EuScreen.getVideo({src: '" + bmi.getVideo() + "', poster: '" + bmi.getScreenshot() + "', controls: true}, function(html){"
	    				+ "			jQuery('#" + id + "').html(html);"
	    				+ "		});"
	    				+ "});</script>";
     		}
			cnt_bookmark++;
		}
     	bookmarkLayout += "</div>";
     	
     	//Load collections

        collections = new Collections(currentUser);   

     	int cnt_header = 1;
     	String colectionslayout_headers = "";
     	String colectionslayout_items = "";

     	System.out.println(collections.getCollectionlist().size());
     	
     	for (Collection col : collections.getCollectionlist()) {
         	
     		String right_header_div_id = "right_header_" + cnt_header;
     		String right_toggle_div_id = "toggle_" + cnt_header;
     		colectionslayout_headers += "<div class=\"right-header collection-header\" id='" + right_header_div_id + "' childs_type='"+col.getName()+"'>" + col.getName() + "</div>";
     		
     		colectionslayout_items += "<div id='" + right_toggle_div_id + "' class=\"tgl\" collection='"+ col.getName()+"'>";
     		
     		System.out.println("================ COLLECTION: " + col.getName() + " ================");
     		for (Bookmark bk : col.getVideos()) {
         		System.out.println("BOOKMARK IS PUBLIC = " + bk.getIsPublic());

     			try{
	     			String id = "bookmark_" + cnt_bookmark;
	     			String src = bk.getVideo();
	     			if(bk.getIsPublic() == false){
	     			if(src != null && src.contains("http://")){
	     				colectionslayout_items += "<div class=\"not_public_video\"><p>Unfortunately due to copyright agreements with the Content Provider this video cannot be used for making a video poster.</p><div id=\"" + id + "\" class=\"drag_bookmark\"><video poster='"+bk.getScreenshot()+"' data-src='" + bk.getVideo() + "' controls></video></div>";
	     				colectionslayout_items += "<script type=\"text/javascript\">"
	            				+ "eddie.getComponent('embedlib').loaded().then(function(){"
	            				+ "		EuScreen.getVideo({src: '" + src + "', poster: '" + bk.getScreenshot() + "', controls: true}, function(html){"
	            				+ "			jQuery('#" + id + "').html(html);"
	            				+ "		});"
	            				+ "});</script>";
	     	     		colectionslayout_items += "</div>";

	     			}
	     			}else {
	     				colectionslayout_items += "<div id=\"" + id + "\" class=\"drag_bookmark\"><video poster='"+bk.getScreenshot()+"' data-src='" + bk.getVideo() + "' controls></video></div>";
	     				colectionslayout_items += "<script type=\"text/javascript\">"
	            				+ "eddie.getComponent('embedlib').loaded().then(function(){"
	            				+ "		EuScreen.getVideo({src: '" + src + "', poster: '" + bk.getScreenshot() + "', controls: true}, function(html){"
	            				+ "			jQuery('#" + id + "').html(html);"
	            				+ "		});"
	            				+ "});</script>";
	     			}
            		cnt_bookmark++;

     			}catch(Exception e){
     				e.printStackTrace();
     			}
        		
			}
     		colectionslayout_items += "</div>";
     		cnt_header++;
		}
   

    	s.setContent("bookmarklayout", bookmarkLayout);
    	
    	s.setContent("colectionslayout_headers", colectionslayout_headers);
    	s.setContent("colectionslayout_items", colectionslayout_items);
    	
     	s.putMsg("bookmarksContent", "", "closeAll(" + cnt_header + ")");
	}
	
	
	//Set theme actions
	 public void actionSettheme(Screen s, String c) {
		 System.out.println("======== actionSettheme(" + c + ") ========");
	
		 if(c.equals("0")) {
	    	FsNode node = themes.getLayoutBy(0);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
	    	
		 } else if (c.equals("1")) {
	    	FsNode node = themes.getLayoutBy(1);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
		    	
		 }else if (c.equals("2")) {
	    	FsNode node = themes.getLayoutBy(2);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
	    	
		 }else if (c.equals("3")) {
	    	FsNode node = themes.getLayoutBy(3);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
	    	
		 }else if (c.equals("4")) {
	    	FsNode node = themes.getLayoutBy(4);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
	    	
		 }else if (c.equals("5")) {
	    	FsNode node = themes.getLayoutBy(5);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("buildContent", "", "setTheme(" + message + ")");
	    	
		 }
	 }
	
	//Generate layout
	public void actionGeneratelayout(Screen s, String c) {
		System.out.println("actionGenerateLayout()");
        this.loadContent(s, "layoutsContent");


        //Load layouts
        layouts = new Layout();
    	String layoutBody = "<div class=\"container\"><div class=\"row\"><div class=\"col-sm-12 col-md-12 col-lg-12\"><h1 class=\"layouts-title\">Please select the available video poster layout below. Remember that this is one-time only, once you select a layout there is no coming back</h1></div></div></div>";
		int cntRow = 0;
		int cntLayout = 0;
		boolean isRow = false;
		
		layoutBody += "<div class=\"container\">";
		
    	for(int i = 0; i < layouts.getLayouts().size(); i++) {
			if (i % 3 == 0) {
    			isRow = true;
    			layoutBody += "<div class=\"row\">";
				
			}
    		
    		layoutBody += "<div class=\"col-sm-4 col-md-4 col-lg-4\"><img  class=\"layout_image\" id=\"layout_"+ i +"\" src='" + layouts.getLayouts().get(i).getProperty("icon") + "'/><h4 class=\"theme_name\">" + layouts.getLayouts().get(i).getProperty("name") + "</h4><p class=\"theme-desc\">" + layouts.getLayouts().get(i).getProperty("description") + "</p></div>";
    		
    		cntRow++;
    		cntLayout++;
    		
    		String layoutStr = layouts.getLayouts().get(i).getProperty("css");
    		String[] splits = layoutStr.split("/");
    		String lo = splits[splits.length -1];
    		lo = lo.trim();
    		layoutWithStyle.put(lo, "layout_"+ i);
    		
    		
    		if(isRow == true) {
	    		if(layouts.getLayouts().size() > 3){
		    		if(cntRow == 3){
		    			layoutBody += "</div>"; 
		    			isRow = false;
		    			cntRow = 0;
		    			
		    		}
		    		
	    		}else {
	    			if(cntRow == layouts.getLayouts().size() - 1){
	    				layoutBody += "</div>"; 
		    			isRow = false;
		    			cntRow = 0;
		    			
		    		}
	    			
	    		}
	    	}
    	}
    	layoutBody += "</div>";

    	s.setContent("layoutsContent", layoutBody);
    	s.putMsg("layoutsContent", "", "setLayoutClick(" + cntLayout + ")");
    	
	}
	
	//Generate color schemes
	public void actionGeneratecolorschemes(Screen s, String c) {
		System.out.println("actionGeneratecolorschemes()");
		this.removeContent(s, "layoutsContent");
		this.removeContentAllScreens("layoutsContent");
        this.loadContent(s, "colorschemesContent");
                
        //Load color schemes
        themes = new Theme();
    	String colorSchemesBody = "<div class=\"container\"><div class=\"row\"><div class=\"col-sm-12 col-md-12 col-lg-12\"><h1 class=\"layouts-title\">Please select the color scheme for your Video Poster below, alternatively you can edit the font color in the build section.</h1></div></div></div>";
    	colorSchemesBody += "<div class=\"container\">";
		int cntRow = 0;
		int cntTheme = 0;
		boolean isRow = false;
    	for(int i = 0; i < themes.getThemes().size(); i++) {
			
			if (i % 3 == 0) {
    			isRow = true;
				colorSchemesBody += "<div class=\"row\">";
				
			}
    
    		colorSchemesBody += "<div class=\"col-sm-4 col-md-4 col-lg-4\"><div class=\"inner-div-scheme\"><img  class=\"scheme_image\" id=\"theme_"+ i +"\" src='" + themes.getThemes().get(i).getProperty("icon") + "'/><h3 class=\"theme_name\">" + themes.getThemes().get(i).getProperty("name") + "</h3></div></div>";    
   
    		cntRow++;
    		cntTheme++;
  
    		styleWithId.put(themes.getThemes().get(i).getProperty("css").trim(), "theme_"+ i);

    		if(isRow == true) {
	    		if(themes.getThemes().size() > 3){
		    		if(cntRow == 3){
		    			colorSchemesBody += "</div>"; 
		    			isRow = false;
		    			cntRow = 0;
		    			
		    		}
	    		}else {
	    			if(cntRow == themes.getThemes().size() - 1){
		    			colorSchemesBody += "</div>"; 
		    			isRow = false;
		    			cntRow = 0;
		    			
		    		}
	    		}
	    	}
    	}
    	
		colorSchemesBody += "</div>";    		
    	s.setContent("colorschemesContent", colorSchemesBody);
        s.putMsg("colorschemesContent", "", "setThemeClick(" + cntTheme + ")");
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
	public void actionAddexternalidentifire(Screen s, String c){
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