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

import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.screen.*;
import org.json.simple.JSONObject;

public class EuscreenpublicationbuilderApplication extends Html5Application{
    public Publication publication = null;
    
 	public EuscreenpublicationbuilderApplication(String id) {
		super(id); 
	}
	
 	/*
 	 * This method is called when a browser window opens the application
 	 * @see org.springfield.lou.application.Html5Application#onNewScreen(org.springfield.lou.screen.Screen)
 	 */
    public void onNewScreen(Screen s) {
        loadStyleSheet(s, "generic"); //Loading the genereic style from css folder
        loadContent(s, "comparison");
        loadContent(s, "header");
        loadContent(s, "left");
        loadContent(s, "section");
        loadContent(s, "right");
        s.setContent("middle", "<div id=\"layout\"></div>");
        this.loadContent(s, "layout", "layout");
        
        publication = new Publication();
    	String layoutBody = "";
    	int cnt = 0;
    	for(FsNode node : publication.layout.getLayouts()) {
    		layoutBody += "<div><img  class=\"layout_image\" id=\"layout_"+ cnt +"\" src='" + node.getProperty("icon") + "'/></div>";
    		cnt++;
    	}
    	
    	s.setContent("layouts", layoutBody);
    	s.setDiv("layout_0", "bind:mousedown","setLayout0" , this);
    	s.setDiv("layout_1", "bind:mousedown","setLayout1" , this);
    }
    
    public void setLayout0(Screen s, String c) {
    	s.setProperties(c);
    	FsNode node = publication.layout.getLayoutBy(0);
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/comparison.css");
    	s.putMsg("layout", "", "update(" + message + ")");
   }
    
	public void setLayout1(Screen s, String c) {
		s.setProperties(c);
    	FsNode node = publication.layout.getLayoutBy(1);
    	JSONObject message = new JSONObject();
    	message.put("html", node.getProperty("template"));
    	message.put("style", "/eddie/apps/euscreenpublicationbuilder/css/comparison.css");
    	s.putMsg("layout", "", "update(" + message + ")");
	}
    
}