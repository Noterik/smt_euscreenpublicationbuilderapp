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

	public void closePreview(Screen s) {
		this.overlayDialog.setURL("");
		this.overlayDialog.setVisible(false);
		this.overlayDialog.update();
	}

	public void preview(Screen s) {
		overlayDialog.setHTML(poster.getHTML());
		overlayDialog.setVisible(true);
		overlayDialog.sync();
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
