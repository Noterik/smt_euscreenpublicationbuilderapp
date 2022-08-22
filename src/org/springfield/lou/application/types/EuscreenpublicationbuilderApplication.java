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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.session.PublicationbuilderSessionContext;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.session.ISession;
import org.springfield.lou.session.ISessionContext;

public class EuscreenpublicationbuilderApplication extends Html5Application{
	
	//The session context that is responsible for keeping track of all the current sessions. 
	private ISessionContext context;
	
	//Are these neccesary?
	public static String ipAddress = "";
	public static boolean isAndroid;
	
	public EuscreenpublicationbuilderApplication(String id) {
		super(id); 	
		System.out.println("publicationbuilder()");
		context = new PublicationbuilderSessionContext(this);
		this.setSessionRecovery(true);
	}
	
	//Shows the favicon in the browser
	public String getFavicon() {
        return "/eddie/apps/euscreenxlelements/img/favicon.png";
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
	
	//This function delegates a screen to the correct session. It might be that a screen already belongs to a session. 
	@Override
	public void onNewScreen(Screen s) {
		//super.onNewScreen(s);
		System.out.println("publicationbuilder onNewScreen()");
		try{
			ISession session = context.getSession(s);
			if(session == null){
				session = context.createSession(s);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//This function delegates a call on a screen to the correct session and automatically calls the function based on name. 
	//With this you can actually CamelCase properly, there's no need for an action prefix. 
	@Override
	public void putOnScreen(Screen s, String from, String msg) {
		ISession session = this.context.getSession(s);
        int pos = msg.indexOf("(");
        if (pos!=-1) {
            String command = msg.substring(0,pos);
            String content = msg.substring(pos+1,msg.length()-1);
            JSONObject params = (JSONObject) JSONValue.parse(content);
            try {
            	Method method;
            	if(params != null){
            		method = session.getClass().getMethod(command, Screen.class, JSONObject.class);
            		method.invoke(session, s, params);
            	}else{
            		method = session.getClass().getMethod(command, Screen.class);
            		method.invoke(session, s);
            	}
            	
        	} catch (SecurityException e) {
        		e.printStackTrace();
        	} catch (NoSuchMethodException e) {
        		e.printStackTrace();
        	} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
 	}
	
	
	/*
    

	public EuscreenpublicationbuilderApplication(String id) {
		super(id); 
	}
	
	*/
	
 	/*
 	 * This method is called when a browser window opens the application
 	 * @see org.springfield.lou.application.Html5Application#onNewScreen(org.springfield.lou.screen.Screen)
 	 */
	/*
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
     */

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