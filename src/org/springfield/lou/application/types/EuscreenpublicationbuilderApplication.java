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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.application.types.DTO.MediaItem;
import org.springfield.lou.application.types.DTO.TextContent;
import org.springfield.lou.screen.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EuscreenpublicationbuilderApplication extends Html5Application{
    public Layout layouts;
    public Theme themes;
    public Bookmarks bookmarks;
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
        loadContent(s, "comparison");
        loadContent(s, "header");
        loadContent(s, "iframesender");
        loadContent(s, "left");     
        loadContent(s, "section");
        loadContent(s, "right");
        
        //Get Current user
        this.getCurrentUser(s);
        //Load bookmarks
    	bookmarks = new Bookmarks(currentUser);
    	System.out.println(bookmarks.getBookmarklist());
    	System.out.println(bookmarks);
    	System.out.println("YES");
    	String bookmarkLayout = "<div class=\"right-header\" id=\"right_header_0\">Bookmarks</div>";
 		bookmarkLayout += "<div id=\"toggle_0\" class=\"tgl\">";

     	int cnt_bookmark = 0;
     	for (Bookmark bmi : bookmarks.getBookmarklist()) {
    		bookmarkLayout += "<div id=\"bookmark_"+ cnt_bookmark +"\" class=\"drag_bookmark\"><video class=\"layout_image\" poster='"+bmi.getScreenshot()+"' controls><source src='"+bmi.getVideo()+"' type=\"video/mp4\"></video></div>";
			cnt_bookmark++;
		}
     	bookmarkLayout += "</div>";
     	
     	//Load collections
     	Collections collections = new Collections(currentUser);    	
     	int cnt_header = 1;
     	for (Collection col : collections.getCollectionlist()) {
     		String right_header_div_id = "right_header_" + cnt_header;
     		String right_toggle_div_id = "toggle_" + cnt_header;
     		bookmarkLayout += "<div class=\"right-header\" id='" + right_header_div_id + "'>" + col.getName() + "</div>";
     		bookmarkLayout += "<div id='" + right_toggle_div_id + "' class=\"tgl\">";
     		for (Bookmark bk : col.getVideos()) {
        		bookmarkLayout += "<div id=\"bookmark_"+ cnt_bookmark +"\" class=\"drag_bookmark\"><video class=\"layout_image\" poster='"+bk.getScreenshot()+"' controls><source src='"+bk.getVideo()+"' type=\"video/mp4\"></video></div>";
        		cnt_bookmark++;
			}
     		bookmarkLayout += "</div>";
     		cnt_header++;
		}
    	
    	s.setContent("bookmarklayout", bookmarkLayout);
    	
        s.setContent("middle", "<div id=\"layout\"></div>");
        this.loadContent(s, "layout", "layout");
                
        //Load layouts
        layouts = new Layout();
    	String layoutBody = "<ul class=\"leftNavUl\">";
    	int cnt = 0;
    	
    	for(FsNode node : layouts.getLayouts()) {
    		layoutBody += "<li><h3 class=\"theme_name\">" + node.getProperty("name") + "</h3><img  class=\"layout_image\" id=\"layout_"+ cnt +"\" src='" + node.getProperty("icon") + "'/></li>";
    		layoutWithStyle.put(node.getProperty("css"), "layout_"+ cnt);
    		System.out.println(layoutWithStyle.size());
    		cnt++;
    	}
    	layoutBody += "</ul>";
    	s.setContent("layouts", layoutBody);

    	s.putMsg("left", "", "setLayoutClick(" + cnt + ")");

    	//Load themes
        themes = new Theme();
    	String themeBody = "<ul class=\"themeNavi\">";
    	int cntThema = 0;
    	
    	for(FsNode node : themes.getThemes()) {
    		themeBody += "<li><h3 class=\"theme_name\">" + node.getProperty("name") + "</h3><img  class=\"scheme_image\" id=\"theme_"+ cntThema +"\" src='" + node.getProperty("icon") + "'/></li>";
    		styleWithId.put(node.getProperty("css").trim(), "theme_"+ cntThema);
    		cntThema++;
    	}
    	themeBody += "</ul>";
    	s.setContent("color_schemes", themeBody);
    	
       	s.putMsg("left", "", "setThemeClick(" + cntThema + ")");
    	s.putMsg("left", "", "approveTheme()");

    	//s.setDiv("left-header-theme", "bind:mousedown","approveTheme" , this);
    	
    	
     	s.putMsg("right", "", "closeAll(" + cnt_header + ")");
     	
        //Catch modes
        if(s.getParameter("status").equals("edit")){
        	System.out.println("MODE = EDIT");
            String poster_url = s.getParameter("posterid");
            System.out.println("-----------POSTER URL----------");
            
            JSONArray arr = Publication.editPublication(poster_url);
            JSONObject idOb = (JSONObject) arr.get(0);
            this.oldPublicationID = idOb.get("id").toString();
            s.putMsg("header", "", "modeEdit()");
        	s.putMsg("layout", "", "edit(" + arr + ")");

        }
    }
    
    //Set layout actions
    public void actionSetlayout0(Screen s, String c) {
    	FsNode node = layouts.getLayoutBy(0);
    	setCurrentLayout(node);
    	setCurrentLayoutStyle(node.getProperty("css"));
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", node.getProperty("css"));
    	s.putMsg("layout", "", "update(" + message + ")");
    	s.putMsg("left", "", "accordion(" + ")");
    	s.putMsg("header", "", "showbuttons(" + ")");

   }
    
	public void actionSetlayout1(Screen s, String c) {
		FsNode node = layouts.getLayoutBy(1);
    	setCurrentLayout(node);
    	setCurrentLayoutStyle(node.getProperty("css"));
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", node.getProperty("css"));
    	s.putMsg("layout", "", "update(" + message + ")");
    	s.putMsg("left", "", "accordion(" + ")");
    	s.putMsg("header", "", "showbuttons(" + ")");
	}
	
	public void actionSetlayout2(Screen s, String c) {
    	FsNode node = layouts.getLayoutBy(2);
    	setCurrentLayout(node);
    	setCurrentLayoutStyle(node.getProperty("css"));
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", node.getProperty("css"));
    	s.putMsg("layout", "", "update(" + message + ")");
    	s.putMsg("left", "", "accordion(" + ")");
    	s.putMsg("header", "", "showbuttons(" + ")");
	}
	
	//Set theme actions
	 public void actionSettheme0(Screen s, String c) {
	    	FsNode node = themes.getLayoutBy(0);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	 }
	 
	 public void actionSettheme1(Screen s, String c) {
	    	FsNode node = themes.getLayoutBy(1);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	 
	 public void actionSettheme2(Screen s, String c) {
	    	FsNode node = themes.getLayoutBy(2);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	 
	 public void actionSettheme3(Screen s, String c) {
	    	FsNode node = themes.getLayoutBy(3);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	 
	 public void actionSettheme4(Screen s, String c) {
	    	FsNode node = themes.getLayoutBy(4);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }

	 public void actionSettheme5(Screen s, String c) {
	    	FsNode node = themes.getLayoutBy(5);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	
	 //Approve theme
	 public void actionApprovetheme(Screen s, String c) {
		 s.putMsg("left", "", "accordionThemes(" + ")");
	     loadStyleSheet(s, "tinycolorpicker");
	 }
	 
	//Action Preview
	public void actionPreview(Screen s, String c) {
		System.out.println("actionPreview()");

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
			System.out.println(publicationJSON.get("xml").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void actionClosepreview(Screen s, String c){
		System.out.println("actionClosePreview()");
		this.overlayDialog.setURL("");
		this.overlayDialog.setVisible(false);
		this.overlayDialog.update();
	}
	
	//Add media item external identifier
	public void actionAddexternalidentifire(Screen s, String c){
		try {
			JSONObject json = (JSONObject)new JSONParser().parse(c);
			System.out.println(json.toJSONString());
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
	    	s.putMsg("layout", "", "setmediaitem(" + message + ")");
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
					System.out.println("============================================");
					System.out.println("EDIT IS CALLED");
					JSONObject publicationJSON = Publication.editXml(publication, this.currentUser, s.getId(), this.oldPublicationID);
					System.out.println("EDIT JSON IFRAME SENDER");
					System.out.println(publicationJSON.toJSONString());
					s.putMsg("iframesender", "", "sendToParent(" + publicationJSON + ")");
				}
			}else{
				JSONObject publicationJSON = Publication.createXML(publication, this.currentUser, s.getId());
				System.out.println("----------------------Publication-----------------------");
				System.out.println(publicationJSON.toJSONString());
				s.putMsg("iframesender", "", "sendToParent(" + publicationJSON + ")");	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getCurrentUser(Screen s){

		String[] arr = s.getId().split("/");
	    System.out.println("--------------------------------------");
    	System.out.println(arr[4]);
    	this.currentUser = arr[4];
	}

	//Get meta headers
	public String getMetaHeaders(HttpServletRequest request) {
		ipAddress=getClientIpAddress(request);
		
		System.out.println("Get ip = "+ipAddress);
		
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