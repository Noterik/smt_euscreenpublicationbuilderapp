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
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.application.types.DTO.MediaItem;
import org.springfield.lou.application.types.DTO.TextContent;
import org.springfield.lou.screen.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EuscreenpublicationbuilderApplication extends Html5Application{
    public Layout layouts;
    public Theme themes;
    private FsNode currentLayout;
    private FsNode currentTheme;
	public static String ipAddress = "";
	public static boolean isAndroid;
	
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
        loadStyleSheet(s, "tinycolorpicker");
        loadContent(s, "comparison");
        loadContent(s, "header");

        loadContent(s, "left");
        loadContent(s, "section");
        loadContent(s, "right");
        s.setContent("middle", "<div id=\"layout\"></div>");
        this.loadContent(s, "layout", "layout");
        
        //Load layouts
        layouts = new Layout();
    	String layoutBody = "<ul class=\"leftNavUl\">";
    	int cnt = 0;
    	
    	for(FsNode node : layouts.getLayouts()) {
    		layoutBody += "<li><img  class=\"layout_image\" id=\"layout_"+ cnt +"\" src='" + node.getProperty("icon") + "'/></li>";
    		cnt++;
    	}
    	layoutBody += "</ul>";
    	s.setContent("layouts", layoutBody);
    	s.setDiv("layout_0", "bind:mousedown","setLayout0" , this);
    	s.setDiv("layout_1", "bind:mousedown","setLayout1" , this);
    	s.setDiv("layout_2", "bind:mousedown","setLayout2" , this);
    	
    	//Load themes
        themes = new Theme();
    	String themeBody = "<ul class=\"themeNavi\">";
    	int cntThema = 0;
    	
    	for(FsNode node : themes.getTehems()) {
    		themeBody += "<li><h3 class=\"theme_name\">" + node.getProperty("name") + "</h3><img  class=\"layout_image\" id=\"theme_"+ cntThema +"\" src='" + node.getProperty("icon") + "'/></li>";
    		cntThema++;
    	}
    	themeBody += "</ul>";
    	s.setContent("color_schemes", themeBody);
    	s.setDiv("theme_0", "bind:mousedown","setTheme0" , this);
    	s.setDiv("theme_1", "bind:mousedown","setTheme1" , this);
    	s.setDiv("theme_2", "bind:mousedown","setTheme2" , this);
    	s.setDiv("theme_3", "bind:mousedown","setTheme3" , this);
    	s.setDiv("theme_4", "bind:mousedown","setTheme4" , this);
    	s.setDiv("theme_5", "bind:mousedown","setTheme5" , this);

    	//Load bookmarks
    	Bookmarks bookmarks = new Bookmarks();    	
    	String bookmarkLayout = "";

     	int cnt_bookmark = 0;
    	for (Bookmark bmi : bookmarks.getBookmarklist()) {
    		bookmarkLayout += "<div id=\"bookmark_"+ cnt_bookmark +"\" class=\"drag_bookmark\"><video class=\"layout_image\" controls><source src='"+bmi.getVideo()+"' type=\"video/mp4\"></video></div>";
			cnt_bookmark++;
		}
    	s.setContent("bookmarklayout", bookmarkLayout);
    }
    
    //Set layout actions
    public void setLayout0(Screen s, String c) {
    	s.setProperties(c);
    	FsNode node = layouts.getLayoutBy(0);
    	setCurrentLayout(node);
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/layout1.css");
    	s.putMsg("layout", "", "update(" + message + ")");
    	s.putMsg("left", "", "accordion(" + ")");
   }
    
	public void setLayout1(Screen s, String c) {
		s.setProperties(c);
    	FsNode node = layouts.getLayoutBy(1);
    	setCurrentLayout(node);
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/comparison.css");
    	s.putMsg("layout", "", "update(" + message + ")");
    	s.putMsg("left", "", "accordion(" + ")");
	}
	
	public void setLayout2(Screen s, String c) {
		s.setProperties(c);
    	FsNode node = layouts.getLayoutBy(2);
    	setCurrentLayout(node);
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/comparison.css");
    	s.putMsg("layout", "", "update(" + message + ")");
    	s.putMsg("left", "", "accordion(" + ")");
	}
	
	//Set theme actions
	 public void setTheme0(Screen s, String c) {
	    	s.setProperties(c);
	    	FsNode node = themes.getLayoutBy(0);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/" + node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
//	    	s.putMsg("left", "", "accordionThemes(" + ")");
	 }
	 
	 public void setTheme1(Screen s, String c) {
	    	s.setProperties(c);
	    	FsNode node = themes.getLayoutBy(1);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/" + node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
//	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	 
	 public void setTheme2(Screen s, String c) {
	    	s.setProperties(c);
	    	FsNode node = themes.getLayoutBy(2);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/" + node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
//	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	 
	 public void setTheme3(Screen s, String c) {
	    	s.setProperties(c);
	    	FsNode node = themes.getLayoutBy(3);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/" + node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }
	 
	 public void setTheme4(Screen s, String c) {
	    	s.setProperties(c);
	    	FsNode node = themes.getLayoutBy(4);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/" + node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
//	    	s.putMsg("left", "", "accordionThemes(" + ")");
	   }

	 public void setTheme5(Screen s, String c) {
	    	s.setProperties(c);
	    	FsNode node = themes.getLayoutBy(5);
	    	setCurrentTheme(node);
	    	JSONObject message = new JSONObject();
	    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/" + node.getProperty("css"));
	    	s.putMsg("layout", "", "setTheme(" + message + ")");
//	    	s.putMsg("left", "", "accordionThemes(" + ")");
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
				String video = "<iframe class=\"videoAfterDrop ui-draggable\" src='" + "http://www.youtube.com/embed/" + identifier + "'></iframe>";
//				String video = "<video class=\"layout_image\" controls><source src='" + "www.youtube.com/watch?v=" + identifier + "' type=\"video/youtube\"></video>"; //djs43DFd2
		    	message.put("video", video);
			}else if (data_type.equals("vimeoitem")) {
				String video = "<iframe class=\"videoAfterDrop\" src='" + "https://player.vimeo.com/video/" + identifier + "'></iframe>";
//				String video = "<video class=\"layout_image\" controls><source src='" + "www.vimeo.com/" + identifier + "' type=\"video/vimeo\"></video>"; //234234234234
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

			JSONArray mediaArray = (JSONArray)json.get("mediaItem");
			JSONArray textArray = (JSONArray)json.get("textItem");

			for(int i = 0; i < mediaArray.size(); i++){
				JSONObject ob = (JSONObject)mediaArray.get(i);
				String mediaId = (String)ob.get("id");
				String mediaValue = (String)ob.get("value");
				publication.template.sections.mediaSection.setMediaItems(new MediaItem(mediaId, mediaValue));
			}
			
			for(int i = 0; i < textArray.size(); i++){
				JSONObject ob = (JSONObject)textArray.get(i);
				String textId = (String)ob.get("id");
				String textValue = (String)ob.get("value");
				
				publication.template.sections.textSection.setTextContents(new TextContent(textId, textValue));
			}
			
			publication.createXML(publication);
		} catch (Exception e) {
			e.printStackTrace();
		}

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