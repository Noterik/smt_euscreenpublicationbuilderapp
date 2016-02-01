package org.springfield.lou.application.session;

import java.util.Date;

import models.Layout;
import models.LayoutThemes;
import models.Layouts;
import models.Theme;
import models.Themes;
import models.VideoPoster;

import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.types.Bookmarks;
import org.springfield.lou.application.types.Collections;
import org.springfield.lou.application.types.Overlaydialog;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.session.Session;

public class PublicationbuilderSession extends Session {

	private Bookmarks bookmarks;
	private Collections collections;
	private Layouts layouts;
	private Themes themes;
	private VideoPoster poster;
	private Layout currentLayout;
	private String currentLayoutStyle;

	private String currentUser;
	private Overlaydialog overlayDialog = null;

	public PublicationbuilderSession(Screen s, Html5Application app) {
		super(s, app);
		System.out.println("PublicationbuilderSession()");

		// Get Current user (bit hacky)
		// I know then you can give your idea :)
		String[] arr = s.getId().split("/");
		this.currentUser = arr[4];
		themes = new Themes();
		initScreen(s);
	}

	private void initScreen(Screen s) {
		System.out.println("Publicationbuilder.initScreen();");
		Html5Application app = this.getApp();
		app.loadStyleSheet(s, "bootstrap");
		app.loadStyleSheet(s, "generic");
		app.loadStyleSheet(s, "font-awesome");
		app.loadStyleSheet(s, "font-awesome.min");
		app.loadStyleSheet(s, "tinycolorpicker");
		app.loadContent(s, "fontawesomeloader");
		app.loadContent(s, "readycheck");
		app.loadContent(s, "embedlib");
		app.loadContent(s, "comparison");
		app.loadContent(s, "header");
		app.loadContent(s, "iframesender");

		this.overlayDialog = new Overlaydialog(s);
		this.overlayDialog.render();
		
		this.layouts = new Layouts(s);
		this.themes = new Themes();
		
		if(s.getParameter("path") != null){
			System.out.println("Publicationbuilder.initScreen() THE PATH = " + s.getParameter("path"));
			this.populateVideoPoster(s);
		}else{
			this.renderLayouts(s);
		}
		
	}

	public String getCurrentLayoutStyle() {
		return currentLayoutStyle;
	}

	public void setCurrentLayoutStyle(String currentLayoutStyle) {
		this.currentLayoutStyle = currentLayoutStyle;
	}

	public void renderLayouts(Screen s) {
		this.setStep(s, "layouts");
		this.layouts.render();
		this.getApp().loadContent(s, "layoutsContent");
		this.layouts.sync();
	}

	public void populateVideoPoster(Screen s) {
		System.out.println("Publicationbuilder.populateVideoPoster()");
		String path = s.getParameter("path");
		FsNode node = Fs.getNode(path);
		
		String id = node.getId();
		Layout layout = layouts.getByCSSPath(node.getProperty("layout"));
		Theme theme = themes.getByCSSPath(node.getProperty("theme"));
		String html = node.getProperty("html");
		
		poster = new VideoPoster(s, id, html, layout, theme);
		this.initEditingGUI(s);
	}
	
	private void setStep(Screen s, String step){
		JSONObject message = new JSONObject();
		message.put("step", step);
		s.putMsg("header", "", "setStep(" + message + ")");
	}

	public void generateBuild(Screen s) {
		System.out.println("=== Empty generateBuild() method ===");
	}

	public void setTheme(Screen s, JSONObject data) {
		String themeId = (String) data.get("themeId");
		this.setTheme(s, themeId);
	}

	public void setTheme(Screen s, String themeId) {
		System.out.println("======== setTheme(" + themeId + ") ========");
		Theme theme = themes.getThemeById(themeId);
		Layout layout = this.currentLayout;
		
		s.removeContent("colorschemesContent");
		this.setStep(s, "build");
		poster = new VideoPoster(s, layout, theme);	
		this.initEditingGUI(s);
	}
	
	public void initEditingGUI(Screen s){
		poster.render();
		this.getApp().loadContent(s, "buildContent");
		this.loadBookmarks(s);
		
		poster.sync();
	}
	
	public void savePoster(Screen s, JSONObject data){
		poster.processUpdate(data);
		poster.sync();
		
		if(poster.getId() == null){
			long time = new Date().getTime();
     		int hash = (this.currentUser + ":poster_12345t"+time).hashCode();
			String eusId = "EUS_"+Integer.toHexString(hash).toUpperCase()+Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase()+Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase()+Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase();
			poster.setId(eusId);
			
		}
		s.putMsg("header", "", "success()");
		s.putMsg("iframesender", "", "sendToParent(" + poster.toJSON() + ")");
	}

	public void setLayout(Screen s, JSONObject data) {
		String layoutId = (String) data.get("layoutId");
		this.setLayout(s, layoutId);
	}

	private void setLayout(Screen s, String layoutId) {
		System.out.println("========= setLayout() ==========");

		this.currentLayout = layouts.getById(layoutId);
		this.renderColorSchemes(s);
	}

	public void renderColorSchemes(Screen s) {
		System.out.println("actionGeneratecolorschemes()");
		this.setStep(s, "themes");
		LayoutThemes layoutThemes = new LayoutThemes(s, this.currentLayout,
				themes);
		layoutThemes.render();
		this.getApp().removeContent(s, "layoutsContent");
		this.getApp().loadContent(s, "colorschemesContent");
		layoutThemes.sync();
	}

<<<<<<< HEAD
	public void closePreview(Screen s, JSONObject c) {
=======
	public void closePreview(Screen s) {
>>>>>>> svg-icons
		this.overlayDialog.setURL("");
		this.overlayDialog.setVisible(false);
		this.overlayDialog.update();
	}

	public void preview(Screen s) {
		overlayDialog.setHTML(poster.getHTML());
		overlayDialog.setVisible(true);
		overlayDialog.sync();
		/*
		try {
			Publication publication = new Publication();
			if (getCurrentTheme() != null)
				publication.theme.setCurrentTheme(getCurrentTheme());
			// publication.template.getLayout().setCurrentLayout(getCurrentLayout());
			publication.template.getLayout().setCurrentLayoutStyle(
					getCurrentLayoutStyle());

			if (data.get("mediaItem") != null) {
				JSONArray mediaArray = (JSONArray) data.get("mediaItem");
				for (int i = 0; i < mediaArray.size(); i++) {
					JSONObject ob = (JSONObject) mediaArray.get(i);
					String mediaId = (String) ob.get("id");
					String mediaValue = (String) ob.get("value");
					String mediaPoster = (String) ob.get("poster");
					publication.template.getSections().mediaSection
							.setMediaItems(new MediaItem(mediaId, mediaValue,
									mediaPoster));
				}
			}

			if (data.get("textItem") != null) {
				JSONArray textArray = (JSONArray) data.get("textItem");

<<<<<<< HEAD
				JSONObject publicationJSON = Publication.createPreviewXML(publication, this.currentUser);
				this.overlayDialog.setHTML(publicationJSON.get("xml").toString());
				this.overlayDialog.setVisible(true); 			
				this.overlayDialog.update(); 
			} catch (Exception e) { 
				e.printStackTrace(); 
=======
				for (int i = 0; i < textArray.size(); i++) {
					JSONObject ob = (JSONObject) textArray.get(i);
					String textId = (String) ob.get("id");
					String textValue = (String) ob.get("value");
					publication.template.getSections().textSection
							.setTextContents(new TextContent(textId, textValue));
				}
>>>>>>> svg-icons
			}

			JSONObject publicationJSON = Publication.createPreviewXML(
					publication, this.currentUser);
			this.overlayDialog.setHTML(publicationJSON.get("xml").toString());
			this.overlayDialog.setVisible(true);
			this.overlayDialog.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
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
			} else if (data_type.equals("vimeoitem") && identifier.contains("americanarchive.org")) {
				String video = "<iframe class=\"videoAfterDrop\" src='"
						+ identifier
						+ "' frameborder=\"0\" allowfullscreen></iframe>";
				message.put("video", video);
			} else if (data_type.equals("vimeoitem")) {
				System.out.println("================== SET EXTERNAL IDENTIFIRE =================");
				System.out.println(identifier);
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

	public void proccessPublication(Screen s, JSONObject c) {
		
		/*
		System.out.println("======== Process Publication() ==========");
		System.out.println(c.toJSONString());
		try {
			Publication publication = new Publication();

			publication.theme.setCurrentTheme(getCurrentTheme());
			// publication.template.getLayout().setCurrentLayout(getCurrentLayout());
			publication.template.getLayout().setCurrentLayoutStyle(
					getCurrentLayoutStyle());

			JSONArray mediaArray = (JSONArray) c.get("mediaItem");
			JSONArray textArray = (JSONArray) c.get("textItem");

			for (int i = 0; i < mediaArray.size(); i++) {
				JSONObject ob = (JSONObject) mediaArray.get(i);
				String mediaId = (String) ob.get("id");
				String mediaValue = (String) ob.get("value");
				String mediaPoster = (String) ob.get("poster");
				publication.template.getSections().mediaSection
						.setMediaItems(new MediaItem(mediaId, mediaValue,
								mediaPoster));
			}

			for (int i = 0; i < textArray.size(); i++) {
				JSONObject ob = (JSONObject) textArray.get(i);
				String textId = (String) ob.get("id");
				String textValue = (String) ob.get("value");

				publication.template.getSections().textSection
						.setTextContents(new TextContent(textId, textValue));
			}

			if (c.get("mode") != null) {
				if (c.get("mode").toString().trim().equals("edit")) {

					if (this.oldPublicationID == "") {
						this.oldPublicationID = this.createPosterID;
					}

					JSONObject publicationJSON = Publication.editXml(
							publication, this.currentUser, s.getId(),
							this.oldPublicationID);
					s.putMsg("iframesender", "", "sendToParent("
							+ publicationJSON + ")");
				}
			} else {
				JSONObject publicationJSON = Publication.createXML(publication,
						this.currentUser, s.getId());

				// In case of edit after immediately create an poster we save
				// old id
				this.createPosterID = (String) publicationJSON.get("id");

				s.putMsg("iframesender", "", "sendToParent(" + publicationJSON
						+ ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
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

	public void getNextBookmarkPage(Screen s) {
		bookmarks.setPage(bookmarks.getPage() + 1);
		bookmarks.sync();
	}

	public void getPrevBookmarkPage(Screen s) {
		bookmarks.setPage(bookmarks.getPage() - 1);
		bookmarks.sync();
	}
}
