package org.springfield.lou.application.session;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.types.Bookmarks;
import org.springfield.lou.application.types.Collections;
import org.springfield.lou.application.types.Layout;
import org.springfield.lou.application.types.Overlaydialog;
import org.springfield.lou.application.types.Publication;
import org.springfield.lou.application.types.Theme;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.session.Session;

public class PublicationbuilderSession extends Session {

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
	public static Map<String, String> layoutWithStyle = new HashMap<String, String>();
	public static Map<String, String> styleWithId = new HashMap<String, String>();

	public PublicationbuilderSession(Screen s, Html5Application app) {
		super(s, app);
		System.out.println("PublicationbuilderSession()");

		// Get Current user (bit hacky)
		//I know then you can give your idea :)
		String[] arr = s.getId().split("/");
		this.currentUser = arr[4];

		initScreen(s);
	}

	private void initScreen(Screen s) {
		Html5Application app = this.getApp();
		app.loadStyleSheet(s, "generic");
		app.loadStyleSheet(s, "bootstrap");
		app.loadStyleSheet(s, "font-awesome");
		app.loadStyleSheet(s, "font-awesome.min");
		app.loadStyleSheet(s, "tinycolorpicker");
		app.loadContent(s,  "fontawesomeloader");
		app.loadContent(s, "readycheck");
		app.loadContent(s, "embedlib");
		app.loadContent(s, "comparison");
		app.loadContent(s, "header");
		app.loadContent(s, "iframesender");

		generateLayout(s);
		this.handleEditStatus(s);
	}

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

	public void generateLayout(Screen s) {
		System.out.println("actionGenerateLayout()");
		Html5Application app = this.getApp();
		app.loadContent(s, "layoutsContent");

		// Load layouts
		layouts = new Layout();

		JSONArray jsonlayoutsarray = new JSONArray();

		for (int i = 0; i < layouts.getLayouts().size(); i++) {

			String layoutStr = layouts.getLayouts().get(i).getProperty("css");
			String[] splits = layoutStr.split("/");
			String lo = splits[splits.length - 1];
			lo = lo.trim();

			layoutWithStyle.put(lo, "layout_" + i);

			JSONObject jsonlayoutobject = new JSONObject();
			jsonlayoutobject.put("id", "layout_" + i);
			jsonlayoutobject.put("name", layouts.getLayouts().get(i)
					.getProperty("name"));
			jsonlayoutobject.put("icon", layouts.getLayouts().get(i)
					.getProperty("icon"));
			jsonlayoutobject.put("description", layouts.getLayouts().get(i)
					.getProperty("description"));

			jsonlayoutsarray.add(jsonlayoutobject);

		}

		s.putMsg("layoutsContent", "", "listLayouts(" + jsonlayoutsarray + ")");

	}

	public void handleEditStatus(Screen s) {
	
		if (s.getParameter("status").equals("edit")) {
			String poster_url = s.getParameter("posterid");

			JSONArray arr = Publication.getPublication(poster_url);
			JSONObject idOb = (JSONObject) arr.get(0);
			this.oldPublicationID = idOb.get("id").toString();

			s.putMsg("header", "", "modeEdit()");

			// Set layout
			JSONObject layout_json = (JSONObject) arr.get(1);
			String[] layout = ((String) layout_json.get("layout_type"))
					.split("_");
			this.setLayout(s, layout[1]);

			// Set theme
			JSONObject colorSchema_json = (JSONObject) arr.get(2);
			String[] colorSchema = ((String) colorSchema_json
					.get("colorSchema")).split("_");
			this.setTheme(s, colorSchema[1]);

			s.removeContent("layoutsContent");
			s.removeContent("colorschemesContent");

			s.putMsg("buildContent", "", "edit(" + arr + ")");
			s.putMsg("header", "", "modeEdit()");

		}

	}

	public void setTheme(Screen s, JSONObject data) {
		String themeId = (String) data.get("themeId");
		this.setTheme(s, themeId);
	}
	
	public void setTheme(Screen s, String themeId){
		System.out.println("======== setTheme(" + themeId + ") ========");

		FsNode node = themes.getLayoutBy(Integer.parseInt(themeId));
		setCurrentTheme(node);
		JSONObject message = new JSONObject();
		message.put("style", node.getProperty("css"));
		s.putMsg("buildContent", "", "setTheme(" + message + ")");
	}

	public void setLayout(Screen s, JSONObject data) {
		String layoutId = (String) data.get("layoutId");
		
		this.setLayout(s, layoutId);
	}
	
	private void setLayout(Screen s, String layoutId){
		Html5Application app = this.getApp();
		
		app.loadContent(s, "buildContent");
		this.loadBookmarks(s);

		FsNode node = layouts.getLayoutBy(Integer.parseInt(layoutId));
		setCurrentLayout(node);
		setCurrentLayoutStyle(node.getProperty("css"));
		JSONObject message = new JSONObject();
		message.put("html", node.getProperty("template"));
		message.put("style", node.getProperty("css"));
		s.putMsg("buildContent", "", "update(" + message + ")");
	}

	public void generateColorSchemes(Screen s) {
		System.out.println("actionGeneratecolorschemes()");
		Html5Application app = this.getApp();
		app.removeContent(s, "layoutsContent");
		app.removeContentAllScreens("layoutsContent");
		app.loadContent(s, "colorschemesContent");

		// Load color schemes
		themes = new Theme();

		JSONArray jsonThemeArray = new JSONArray();

		for (int i = 0; i < themes.getThemes().size(); i++) {
			styleWithId.put(
					themes.getThemes().get(i).getProperty("css").trim(),
					"theme_" + i);

			JSONObject jsonThemeObject = new JSONObject();
			jsonThemeObject.put("id", "theme_" + i);
			jsonThemeObject.put("name",
					themes.getThemes().get(i).getProperty("name"));
			jsonThemeObject.put("icon",
					themes.getThemes().get(i).getProperty("icon"));

			jsonThemeArray.add(jsonThemeObject);

		}

		s.putMsg("colorschemesContent", "", "listThemes(" + jsonThemeArray
				+ ")");
	}

	public void closePreview(Screen s, String c) {
		this.overlayDialog.setURL("");
		this.overlayDialog.setVisible(false);
		this.overlayDialog.update();
	}

	public void preview(Screen s, JSONObject data) {
		/*
		 * try { this.overlayDialog = new Overlaydialog(s);
		 * this.overlayDialog.render(); JSONObject json = null; if(c != null)
		 * json = (JSONObject)new JSONParser().parse(c); Publication publication
		 * = new Publication(); if(getCurrentTheme() != null)
		 * publication.theme.setCurrentTheme(getCurrentTheme());
		 * publication.template.layout.setCurrentLayout(getCurrentLayout());
		 * publication
		 * .template.layout.setCurrentLayoutStyle(getCurrentLayoutStyle());
		 * 
		 * if(json.get("mediaItem") != null){ JSONArray mediaArray =
		 * (JSONArray)json.get("mediaItem"); for(int i = 0; i <
		 * mediaArray.size(); i++){ JSONObject ob =
		 * (JSONObject)mediaArray.get(i); String mediaId = (String)ob.get("id");
		 * String mediaValue = (String)ob.get("value"); String mediaPoster =
		 * (String)ob.get("poster");
		 * publication.template.sections.mediaSection.setMediaItems(new
		 * MediaItem(mediaId, mediaValue, mediaPoster)); } }
		 * 
		 * if(json.get("textItem") != null){ JSONArray textArray =
		 * (JSONArray)json.get("textItem");
		 * 
		 * for(int i = 0; i < textArray.size(); i++){ JSONObject ob =
		 * (JSONObject)textArray.get(i); String textId = (String)ob.get("id");
		 * String textValue = (String)ob.get("value");
		 * 
		 * publication.template.sections.textSection.setTextContents(new
		 * TextContent(textId, textValue)); } }
		 * 
		 * 
		 * JSONObject publicationJSON =
		 * Publication.createPreviewXML(publication, this.currentUser);
		 * this.overlayDialog.setHTML(publicationJSON.get("xml").toString());
		 * this.overlayDialog.setVisible(true); this.overlayDialog.update(); }
		 * catch (Exception e) { e.printStackTrace(); }
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
}
