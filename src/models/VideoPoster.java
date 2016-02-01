package models;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.Configuration;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screencomponent.component.ScreenComponent;

public class VideoPoster extends ScreenComponent{
	
	String id = null;
	String title = null;
	Layout layout;
	Theme theme;
	Document html;
	String template;
	LayoutIcon icon;
	JSONObject contents;
	
	//TODO: Not happy with two different templates
	String youtubeEmbed = "<iframe src=\"https://www.youtube.com/embed/<%= external.id %>?wmode=opaque&amp;rel=0&amp;autohide=1&amp;showinfo=0&amp;wmode=transparent\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";
	String vimeoEmbed = "<iframe src=\"https://player.vimeo.com/video/<%= external.id %>?color=ffffff&title=0&byline=0&portrait=0\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";
	String videoHTML = "<video data-src=\"<%= video.src %>\" data-poster=\"<%= video.poster %>\"></video>";
	
	//New videoposter
	public VideoPoster(Screen s, Layout layout, Theme theme) {
		super(s);
		contents = new JSONObject();
		this.layout = layout;
		this.theme = theme;		
		this.icon = new LayoutIcon(layout.getId());
		this.icon.applyTheme(theme);
		init();
	}
	
	//Existing videoposter
	public VideoPoster(Screen s, String id, String html, Layout layout, Theme theme){
		super(s);
		contents = new JSONObject();
		this.id = id;
		this.html = Jsoup.parse(html);
		this.template = layout.getTemplate();
		this.layout = layout;
		this.theme = theme;		
		this.icon = new LayoutIcon(layout.getId());
		this.icon.applyTheme(theme);
		populate();
	}
	
	private void populate(){
		Elements elements = this.html.select("[data-section-id]");
		for(Element element : elements){
			
			String id = element.attr("data-section-id");
			String type = element.attr("data-section-type");
			JSONObject fieldObj = new JSONObject();
			fieldObj.put("type", type);
			if(type.equals("media")){
				System.out.println("VideoPoster.populate() MEDIA = " + element);
				fieldObj.put("contents", element.attr("data-media-src"));
				fieldObj.put("thumb", element.attr("data-media-thumb"));
			}else{
				fieldObj.put("contents", element.html());
			}
			contents.put(id, fieldObj);
		}
	}

	@JSONField(field = "id")
	public String getId(){
		return this.id;
	}
	
	@JSONField(field = "title")
	public String getTitle(){
		return title;
	}
	
	@JSONField(field = "template")
	public String getTemplate(){
		return template;
	}
	
	@JSONField(field = "html")
	public String getHTML(){
		return html.toString();
	}	
	
	@JSONField(field = "icon")
	public String getIcon(){
		return icon.getSVG();
	}
		
	@JSONField(field = "layoutcss")
	public String getLayoutCSS(){
		return layout.getCSSURI();
	}
	
	@JSONField(field = "themecss")
	public String getThemeCSS(){
		return theme.getCSSURI();
	}
	
	@JSONField(field = "contents")
	public JSONObject getContents(){
		return contents;
	}
	
	private void setGenericHead(){
		html.head().append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\">&#xA0;</script>");
		html.head().append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>");
		html.head().append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\">&#xA0;</script>");
		html.head().append("<script src=\"" + Configuration.getLibServer() + "\">&#xA0;</script>");
		html.head().append("<link rel=\"stylesheet\" type=\"text/css\" href='" + Configuration.getServer() + "/eddie/apps/euscreenpublicationbuilder/css/layouts/shared.css'></link>");
		html.head().append("<link rel=\"stylesheet\" type=\"text/css\" href='" + layout.getCSSURI() + "'></link>");
		html.head().append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css\"></link>");
	}
	
	private void setScript(){
		String htmlStr = "<script type=\"text/javascript\">$(\"video[data-src]\").each(function(a,b){console.log(b);var c=$(b).data(\"src\"),d=$(b).data(\"poster\");EuScreen.getVideo({src:c,poster:d,controls:!0},function(a){return function(b){var c=$(b);$(a).replaceWith(c),$fullScreenIcon=$(\"<i class='fullscreen fa fa-resize-full'></i>\"),c.parent().append($fullScreenIcon),$fullScreenIcon.on(\"click\",function(){var a=c[0];a.requestFullscreen?a.requestFullscreen():a.msRequestFullscreen?a.msRequestFullscreen():a.mozRequestFullScreen?a.mozRequestFullScreen():a.webkitRequestFullscreen&&a.webkitRequestFullscreen()})}}(b))});</script>";
		html.body().append(htmlStr);
	}
	
	public void setTheme(Theme theme){
		if(html.select("#theme-style").size() > 0){
			html.select("#theme-style").get(0).attr("href", theme.getCSSURI());
		}else{
			html.head().append("<link id=\"theme-style\" rel=\"stylesheet\" href=\"" + theme.getCSSURI() + "\">");
		}
	}
	
	private void init(){
		this.template = layout.getTemplate();
		html = Jsoup.parseBodyFragment(this.template);
		setGenericHead();
		setTheme(theme);
		setScript();
	}
	
	private void handleTextUpdate(String id, JSONObject fieldData){
		String value = (String) fieldData.get("contents");
		if(html.select("[data-section-id=\"" + id + "\"]").size() > 0 && value != null){
			html.select("[data-section-id=\"" + id + "\"]").get(0).html(value);
		}
	}
	
	private void handleMediaUpdate(String id, JSONObject fieldData){
		System.out.println("handleMediaUpdate(" + fieldData + ")");
		String value = (String) fieldData.get("contents");
		Element element = html.select("[data-section-id=\"" + id + "\"]").size() > 0 ? html.select("[data-section-id=\"" + id + "\"]").get(0) : null;
		
		if(element != null && value != null && (value.indexOf("youtube(") != -1 || value.indexOf("vimeo(") != -1 || value.indexOf("internal(") != -1)){
			String idOrSrc = value.substring(value.indexOf("(") + 1, value.lastIndexOf(")"));
			element.attr("data-media-src", value);
			if(value.indexOf("youtube(") != -1){
				element.html(this.youtubeEmbed.replace("<%= external.id %>", idOrSrc));
			}else if(value.indexOf("vimeo(") != -1){
				element.html(this.vimeoEmbed.replace("<%= external.id %>", idOrSrc));
			}else{
				String poster = (String) fieldData.get("thumb");
				if(poster != null)
					element.attr("data-media-thumb", poster);
				String videoStr = this.videoHTML.replace("<%= video.src %>", idOrSrc);
				videoStr = videoStr.replace("<%= video.poster %>", poster);
				element.html(videoStr);
			}
			
		}else if(element != null){
			element.html("");
		}
	}

	public void processUpdate(JSONObject data) {
		this.contents = data;
		for(Object keyObj : data.keySet()){
			String key = (String) keyObj;
			JSONObject fieldData = (JSONObject) data.get(keyObj);
			String type = (String) fieldData.get("type");
			if(type.equals( "title" ) || type.equals("text") || type.equals("text_big")){
				handleTextUpdate(key, fieldData);
				if(type.equals("title")){
					SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy");
					String dateStr = sdf.format(new Date());
					String newTitleStr;
					if(fieldData.get("contents") != null && !fieldData.get("contents").equals("Fill in the title") && !fieldData.get("contents").equals("")){
						newTitleStr = (String) fieldData.get("contents");
					}else{
						newTitleStr = "New Videoposter - " + dateStr;
					}
					
					this.title = newTitleStr;
				}
			}else if(type.equals("media")){
				handleMediaUpdate(key, fieldData);
			}
		}
	}

	public void setId(String eusId) {
		this.id = eusId;
	}
	
	/*
	public static JSONArray getPublication(String posterUrl){
		System.out.println("Publication.editPublication(" + posterUrl + ")");
        FsNode posterNode = Fs.getNode(posterUrl);
        JSONArray jsArr = new JSONArray();
        
        JSONObject oldId = new JSONObject();
        oldId.put("id", posterNode.getId());
        jsArr.add(oldId);
        
        Document d = null;

        try {
			d = DocumentHelper.parseText(posterNode.getProperty("html"));

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try{
			Node title = d.selectSingleNode("//h1[@class=\"title\"]");			 
			List<Node> mediaItem = d.selectNodes("//div[@data-section-type=\"media\"]");
			List<Node> textItem = d.selectNodes("//div[@data-section-type=\"text_big\"]");			
	
			String layoutHref = posterNode.getProperty("layout").trim();
			String colorHref = posterNode.getProperty("theme").trim();
			
			String[] splits = layoutHref.split("/");
			String layoutStr = splits[splits.length - 1];
			layoutStr = layoutStr.trim();
			String layoutt = PublicationbuilderSession.layoutWithStyle.get(layoutStr);
			
			JSONObject layout = new JSONObject();
			layout.put("type", "layout");
			layout.put("layout_type", layoutt);
			jsArr.add(layout);
	
			JSONObject styles = new JSONObject();
			styles.put("type", "styles");
			styles.put("layout", layoutHref);
			String style = PublicationbuilderSession.styleWithId.get(colorHref);
			styles.put("colorSchema", style);
			jsArr.add(styles);
			
			//Get title object
			Element titleElement = (Element) title;
			JSONObject titleObject = new JSONObject();
			titleObject.put("type", "title");
			titleObject.put("id", titleElement.attributeValue("id"));
			titleObject.put("value", title.getStringValue());
			jsArr.add(titleObject);
	
			//Get media items objects
			for (Node node : mediaItem) {
				Element el = (Element) node;
	
				JSONObject mediaObject = new JSONObject();
				mediaObject.put("type", "media_item");
				mediaObject.put("id", el.attributeValue("id"));
				mediaObject.put("value", node.getStringValue());
				jsArr.add(mediaObject);
			}
	
	
			//Get text item objects
			for (Node node : textItem) {
				Element el = (Element) node;
				JSONObject textObject = new JSONObject();
				textObject.put("type", "text_item");
				textObject.put("id", el.attributeValue("id"));
				textObject.put("value", node.getStringValue());
				jsArr.add(textObject);
			}
        }catch(Exception e){
        	e.printStackTrace();
        }
        System.out.println("FINISHED Publication.editPublication()");
        
		return jsArr;
	}*/
	
	
	/*
	public static JSONObject createXML(VideoPoster publication, String user, String id){
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();
		String layoutTemplate = layout.getProperty("template").trim();

		String theme = null;
		if(publication.theme.getCurrentTheme() != null){
			if(publication.theme.getCurrentTheme().getProperty("css") != null){
				theme = publication.theme.getCurrentTheme().getProperty("css");
			}
		}
		
		String htmlLayout = buildHtml(layoutTemplate, layoutStyle, theme);

		Document d = null;
		try {
			d = DocumentHelper.parseText(htmlLayout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JSONObject object = buildXml(d, publication, user, null, id, layoutStyle, theme);

        return object;
	}

	public static JSONObject editXml(VideoPoster poster, String user, String id, String oldId){
		System.out.println("Publication.editXML()");
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();
		String layoutTemplate = layout.getProperty("template").trim();

		String theme = null;
		if(publication.theme.getCurrentTheme() != null){
			if(publication.theme.getCurrentTheme().getProperty("css") != null){
				theme = publication.theme.getCurrentTheme().getProperty("css");
			}
		}

		String htmlLayout = buildHtml(layoutTemplate, layoutStyle, theme);
		
		Document d = null;
		try {
			d = DocumentHelper.parseText(htmlLayout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject object = buildXml(d, publication, user, oldId, null, layoutStyle, theme);

        return object;
	}

	public static JSONObject createPreviewXML(Publication publication, String user){
		System.out.println("createPreviewXML()");
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();

		String theme = null;
		if(publication.theme.getCurrentTheme() != null){
			if(publication.theme.getCurrentTheme().getProperty("css") != null){
				theme = publication.theme.getCurrentTheme().getProperty("css");
			}
		}


		String layoutTemplate = layout.getProperty("template").trim();
		String htmlLayout = buildHtml(layoutTemplate, layoutStyle, theme); 
		

		Document d = null;
		try {
			d = DocumentHelper.parseText(htmlLayout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject object = buildXml(d, publication, user, null, null, layoutStyle, theme);

        return object;
	}
	
	public static JSONObject buildXml(Document d, Publication publication, String user, String oldId, String id, String layoutStyle, String theme) {
		JSONObject object = new JSONObject();
		List<Node> mediaItems = d.selectNodes("//div[@data-section-type=\"media\"]");
		List<Node> textItems = d.selectNodes("//div[@data-section-type=\"text_big\"]");
		List<Node> titleItems = d.selectNodes("//h1[@class=\"title\"]");

		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();
		
		
		for (Node mediaItem : mediaItems) {
			for(int i = 0; i < mediaItemList.size(); i++) {
				Element element = (Element) mediaItem;
				if (element != null && mediaItemList.get(i).getId() != null) {
					if (mediaItemList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						element.clearContent();
						String media = null;
						if(mediaItemList.get(i).getValue() != null){
							if (mediaItemList.get(i).getValue().toString().contains("http://www.youtube.com") || mediaItemList.get(i).getValue().toString().contains("https://player.vimeo") || mediaItemList.get(i).getValue().toString().contains("http://americanarchive.org")) {
								media = "<iframe class=\"videoAfterDrop\" src='" + mediaItemList.get(i).getValue().toString() + "' frameborder=\"0\" allowfullscreen></iframe>";
							}else {
								String src = mediaItemList.get(i).getValue().toString();
								src = src.substring(0, src.lastIndexOf("?"));
								media = "<video data-src=\"" + src + "\" data-poster=\"" + mediaItemList.get(i).getPoster() + "\"/>";
							}

							mediaItem.setText(media);
						}
					}
				}
			}
		}

		for (Node textItem : textItems) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) textItem;
				if (element != null && textContentList.get(i).getId() != null) {
					if (textContentList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						textItem.setText(textContentList.get(i).getValue().toString());

					}
				}
			}
		}

		String xmlTitle = null;
		for (Node title : titleItems) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) title;

				if (element != null && textContentList.get(i).getId() != null) {
					if (textContentList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						title.setText(textContentList.get(i).getValue().toString());
						xmlTitle = textContentList.get(i).getValue().toString();
					}
				}
			}
		}

        object.put("type", "videoposter");
        
      if(id != "" && id != null){
        	 long time = new Date().getTime();
     		int hash = (user + ":poster_"+id+"t"+time).hashCode();

     		String eusId = "EUS_"+Integer.toHexString(hash).toUpperCase()+Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase()+Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase()+Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase();
     		//Random geted id from preview just to compare
     		String originalid = "EUS_0612ECCF06F3082EB2B36A8432245F7A";

     		if(eusId.length() > originalid.length()){
     			eusId = eusId.substring(0, originalid.length() - eusId.length());
     		}else if (eusId.length() < originalid.length()){
     			eusId += Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase();
     			eusId += Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase();
     			eusId += Integer.toHexString((""+new Date().getTime()).hashCode()).toUpperCase();
     			eusId = eusId.substring(0, originalid.length());
     		}
     		
        	object.put("id", eusId);

        }else if(oldId != "" && oldId != null){
        	object.put("id", oldId);
        	
        }else {
    		UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            object.put("id", randomUUIDString);
            
        }
        object.put("title", xmlTitle);
        object.put("layout", layoutStyle);
        object.put("theme", theme);
        PublicationHTMLWriter writer = new PublicationHTMLWriter();
        //object.put("xml", writer.getHTML(d));
        System.out.println("=========== BUILD XML() ==========");
        System.out.println(object.toJSONString());
		return object;
	}
	
	public static String buildHtml(String layoutTemplate, String layoutStyle, String theme) {
		StringBuilder result = new StringBuilder();
		
		result.append("<html><head><title>First parse</title>");
		result.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\">&#xA0;</script>");
		result.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>");
		result.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\">&#xA0;</script>");
		result.append("<script src=\"" + Configuration.getLibServer() + "\">&#xA0;</script>");
		result.append("<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>");
		result.append("<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>");
		result.append("<link rel=\"stylesheet\" type=\"text/css\" href='" + Configuration.getServer() + "/euscreenpublicationbuilder/css/layouts/comparison_after.css'></link>");
		result.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css\"></link>");
		result.append("</head>");
		result.append("<body style=\"background-color: rgba(0, 0, 0, 0); overflow-y: hidden; height: 100%\"><div id=\"layout\" style=\"width: 50%;margin: 0 auto; height: 100%; overflow-y: auto;\">");
		result.append(layoutTemplate);
		result.append("</div>");
		result.append("<script type=\"text/javascript\">");
		result.append("<![CDATA[");
		result.append("$('video[data-src]').each(function(index, video){");
		result.append("console.log(video);");
		result.append("var src = $(video).data('src');");
		result.append("var poster = $(video).data('poster');");
		result.append("EuScreen.getVideo({");
		result.append("src: src,");
		result.append("poster: poster,");
		result.append("controls: true");
		result.append("}, (function(video){");
		result.append("return function(html){");
		result.append("var $video = $(html);");
		result.append("$(video).replaceWith($video);");
		result.append("$fullScreenIcon = $('<i class=\"fullscreen fa fa-resize-full\"></i>');");
		result.append("$video.parent().append($fullScreenIcon);");
		result.append("$fullScreenIcon.on('click', function(){");
		result.append("var elem = $video[0];");
		result.append("if (elem.requestFullscreen) {");
		result.append("elem.requestFullscreen();");
		result.append("} else if (elem.msRequestFullscreen) {");
		result.append("elem.msRequestFullscreen();");
		result.append("} else if (elem.mozRequestFullScreen) {");
		result.append("elem.mozRequestFullScreen();");
		result.append("} else if (elem.webkitRequestFullscreen) {");
		result.append("elem.webkitRequestFullscreen();");
		result.append("}");
		result.append("})");
		result.append("}");
		result.append("})(video)");
		result.append(");");
		result.append("});");
		result.append("]]>");
		result.append("</script>");
		result.append("</body>");
		result.append("</html>");
	
		return result.toString();
	}
	*/
}




