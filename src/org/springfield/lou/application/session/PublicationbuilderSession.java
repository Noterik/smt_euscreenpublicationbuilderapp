package org.springfield.lou.application.session;

import java.util.HashMap;
import java.util.Map;

import models.Layout;
import models.LayoutThemes;
import models.Layouts;
import models.Themes;
import models.Theme;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.types.Bookmarks;
import org.springfield.lou.application.types.Collections;
import org.springfield.lou.application.types.Overlaydialog;
import org.springfield.lou.application.types.Publication;
import org.springfield.lou.application.types.DTO.MediaItem;
import org.springfield.lou.application.types.DTO.TextContent;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.session.Session;

public class PublicationbuilderSession extends Session {

	public Bookmarks bookmarks;
	public Collections collections;
	public Layouts layouts;
	private Themes themes;
	private Layout currentLayout;
	private Theme currentTheme;
 	private String currentLayoutStyle;
	
	private String currentUser;
	public static String ipAddress = "";
	public static boolean isAndroid;
	private Overlaydialog overlayDialog = null;
	private String oldPublicationID = "";
	private String createPosterID = "";
	public static Map<String, String> layoutWithStyle = new HashMap<String, String>();
	public static Map<String, String> styleWithId = new HashMap<String, String>();

	public PublicationbuilderSession(Screen s, Html5Application app) {
		super(s, app);
		System.out.println("PublicationbuilderSession()");

		// Get Current user (bit hacky)
		//I know then you can give your idea :)
		String[] arr = s.getId().split("/");
		this.currentUser = arr[4];
		themes = new Themes();
		initScreen(s);
	}

	private void initScreen(Screen s) {
		Html5Application app = this.getApp();
		app.loadStyleSheet(s, "bootstrap");
		app.loadStyleSheet(s, "generic");
		app.loadStyleSheet(s, "font-awesome");
		app.loadStyleSheet(s, "font-awesome.min");
		app.loadStyleSheet(s, "tinycolorpicker");
		app.loadContent(s,  "fontawesomeloader");
		app.loadContent(s, "readycheck");
		app.loadContent(s, "embedlib");
		app.loadContent(s, "comparison");
		app.loadContent(s, "header");
		app.loadContent(s, "iframesender");

		this.overlayDialog = new Overlaydialog(s);
		this.overlayDialog.render();
		
		this.renderLayouts(s);
		
		this.handleEditStatus(s);
	}

	public String getCurrentLayoutStyle() {
		return currentLayoutStyle;
	}

	public void setCurrentLayoutStyle(String currentLayoutStyle) {
		this.currentLayoutStyle = currentLayoutStyle;
	}
	
	public void renderLayouts(Screen s){
		this.layouts = new Layouts(s);
		this.layouts.render();
		this.getApp().loadContent(s, "layoutsContent");
		this.layouts.sync();
	}

	public void handleEditStatus(Screen s) {
	
		if (s.getParameter("status").equals("edit")) {

			String poster_url = s.getParameter("posterid");
			System.out.println("===== HANDLE EDIT STATUS ( " + poster_url + " )");
			JSONArray arr = Publication.getPublication(poster_url);
			
			JSONObject idOb = (JSONObject) arr.get(0);
			this.oldPublicationID = idOb.get("id").toString();

			
			System.out.println("ID OF OBJECT: " + idOb);

			s.putMsg("header", "", "modeEdit()");
// Set layout
			JSONObject layoutJson = (JSONObject) arr.get(1);
			System.out.println("LAYOUT JSON: " + layoutJson.toJSONString());
			String[] layout = ((String) layoutJson.get("layout_type"))
					.split("_");
			
			this.setLayout(s, layout[1]);
			s.removeContent("layoutsContent");
// Set theme
			JSONObject colorSchemaJson = (JSONObject) arr.get(2);
			System.out.println("COLOR SCHEMA OF JESON: " + colorSchemaJson);
			String[] colorSchema = ((String) colorSchemaJson
					.get("colorSchema")).split("_");
		
			this.setTheme(s, colorSchema[1]);

			s.removeContent("colorschemesContent");

			s.putMsg("buildContent", "", "edit(" + arr + ")");
			s.putMsg("header", "", "modeEdit()");

		}

	}
	
	public void generateBuild(Screen s) {
		System.out.println("=== Empty generateBuild() method ===");
	}
	public void setTheme(Screen s, JSONObject data) {
		String themeId = (String) data.get("themeId");
		this.setTheme(s, themeId);
	}
	
	public void setTheme(Screen s, String themeId){
		System.out.println("======== setTheme(" + themeId + ") ========");
		currentTheme = themes.getThemeById(themeId);
		JSONObject message = new JSONObject();
		message.put("style", currentTheme.getCSSURI());
		s.putMsg("buildContent", "", "setTheme(" + message + ")");
		/*
		FsNode node = themes.getLayoutBy(Integer.parseInt(themeId));
		setCurrentTheme(node);
		JSONObject message = new JSONObject();
		message.put("style", node.getProperty("css"));
		
		*/
	}

	public void setLayout(Screen s, JSONObject data) {
		String layoutId = (String) data.get("layoutId");
		this.setLayout(s, layoutId);
	}
	
	private void setLayout(Screen s, String layoutId){
		System.out.println("========= setLayout() ==========");
		Html5Application app = this.getApp();
		
		this.currentLayout = layouts.getById(layoutId);
		
		System.out.println(layoutId);
		app.loadContent(s, "buildContent");
		this.loadBookmarks(s);
		this.renderColorSchemes(s);
	}

	public void renderColorSchemes(Screen s) {
		System.out.println("actionGeneratecolorschemes()");
		LayoutThemes layoutThemes = new LayoutThemes(s, this.currentLayout, themes);
		layoutThemes.render();
		this.getApp().removeContent(s, "layoutsContent");
		this.getApp().loadContent(s, "colorschemesContent");
		layoutThemes.sync();
	}

	public void closePreview(Screen s, String c) {
		this.overlayDialog.setURL("");
		this.overlayDialog.setVisible(false);
		this.overlayDialog.update();
	}
	
	public void preview(Screen s, JSONObject data) {
		
		try { 
				Publication publication = new Publication(); 
				if(getCurrentTheme() != null) publication.theme.setCurrentTheme(getCurrentTheme());
				//publication.template.getLayout().setCurrentLayout(getCurrentLayout());
				publication.template.getLayout().setCurrentLayoutStyle(getCurrentLayoutStyle());
  
				if(data.get("mediaItem") != null){ 
					JSONArray mediaArray = (JSONArray)data.get("mediaItem"); 
					for(int i = 0; i < mediaArray.size(); i++){ 
						JSONObject ob = (JSONObject)mediaArray.get(i); 
						String mediaId = (String)ob.get("id");
						String mediaValue = (String)ob.get("value");
						String mediaPoster = (String)ob.get("poster");
						publication.template.getSections().mediaSection.setMediaItems(new MediaItem(mediaId, mediaValue, mediaPoster));
					}
				}
  
				if(data.get("textItem") != null) {
					JSONArray textArray = (JSONArray)data.get("textItem");
  
					for(int i = 0; i < textArray.size(); i++){
						JSONObject ob = (JSONObject)textArray.get(i);
						String textId = (String)ob.get("id");
						String textValue = (String)ob.get("value");
						publication.template.getSections().textSection.setTextContents(new TextContent(textId, textValue)); 
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

	// Add media item external identifier
	public void addExternalIdentifier(Screen s, JSONObject data) {
		try {
			JSONObject json = data;
			String data_type = json.get("dataType").toString().toLowerCase();
			String identifier = json.get("identifier").toString();

			String container = "#" + json.get("container").toString();
			JSONObject message = new JSONObject();

			if (data_type.equals("youtubeitem")) {
				String[] youtubeId = identifier.split("=");
				// https://www.youtube.com/watch?v=A4Tme1q2iew
				String video = "<iframe class=\"videoAfterDrop ui-draggable\" src='"
						+ "http://www.youtube.com/embed/"
						+ youtubeId[1]
						+ "' frameborder=\"0\" allowfullscreen></iframe>";
				message.put("video", video);
			} else if (data_type.equals("vimeoitem")) {
				String[] vimeoId = identifier.split("/");
				String video = "<iframe class=\"videoAfterDrop\" src='"
						+ "https://player.vimeo.com/video/" + vimeoId[3]
						+ "' frameborder=\"0\" allowfullscreen></iframe>";
				message.put("video", video);
			}

			message.put("container", container);

			s.putMsg("buildContent", "", "setmediaitem(" + message + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public void proccessPublication(Screen s, JSONObject c){
		System.out.println("======== Process Publication() ==========");
		System.out.println(c.toJSONString());
		try {
			Publication publication = new Publication();

			publication.theme.setCurrentTheme(getCurrentTheme());
			//publication.template.getLayout().setCurrentLayout(getCurrentLayout());
			publication.template.getLayout().setCurrentLayoutStyle(getCurrentLayoutStyle());
			
			JSONArray mediaArray = (JSONArray)c.get("mediaItem");
			JSONArray textArray = (JSONArray)c.get("textItem");

			for(int i = 0; i < mediaArray.size(); i++){
				JSONObject ob = (JSONObject)mediaArray.get(i);
				String mediaId = (String)ob.get("id");
				String mediaValue = (String)ob.get("value");
				String mediaPoster = (String)ob.get("poster");
				publication.template.getSections().mediaSection.setMediaItems(new MediaItem(mediaId, mediaValue, mediaPoster));
			}
			
			for(int i = 0; i < textArray.size(); i++){
				JSONObject ob = (JSONObject)textArray.get(i);
				String textId = (String)ob.get("id");
				String textValue = (String)ob.get("value");
				
				publication.template.getSections().textSection.setTextContents(new TextContent(textId, textValue));
			}
			
			if(c.get("mode") != null){
				if(c.get("mode").toString().trim().equals("edit")){
					
					if(this.oldPublicationID == ""){
						this.oldPublicationID = this.createPosterID;
					}
					
					JSONObject publicationJSON = Publication.editXml(publication, this.currentUser, s.getId(), this.oldPublicationID);
					s.putMsg("iframesender", "", "sendToParent(" + publicationJSON + ")");
				}
			}else{
				JSONObject publicationJSON = Publication.createXML(publication, this.currentUser, s.getId());
				
				//In case of edit after immediately create an poster we save old id
				this.createPosterID = (String)publicationJSON.get("id");
				
				s.putMsg("iframesender", "", "sendToParent(" + publicationJSON + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// Load bookmarks
	public void loadBookmarks(Screen s) {
		Html5Application app = this.getApp();
		bookmarks = new Bookmarks(s, currentUser);
		bookmarks.render();
		collections = new Collections(s, currentUser);
		collections.render();
		app.loadContent(s, "bookmarksContent");
		bookmarks.sync();
		collections.sync();
	}
	
	public void getNextBookmarkPage(Screen s){
		bookmarks.setPage(bookmarks.getPage() + 1);
		bookmarks.sync();
	}
	
	public void getPrevBookmarkPage(Screen s){
		bookmarks.setPage(bookmarks.getPage() - 1);
		bookmarks.sync();
	}
}
