package org.springfield.lou.application.types;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.DTO.MediaItem;
import org.springfield.lou.application.types.DTO.TextContent;
import org.springfield.lou.application.util.PublicationHTMLWriter;

/* TODO
 * Why are there three functions that almost do the same? avoid code duplication... 
 * Put generic code in a single functions and the small subtleties/difference can be put in 
 * other functions. 
 * Now everytime I change something I have to change it in all three functions. 
 * Not efficient/scalable and very prone to errors. 
 */

public class Publication extends VideoPoster{

	public Publication() {
		super();
	}
	
	/**
	 * TODO: String poster_url should be String posterUrl <- this is correct camelcasing, keep underscores in PHP. 
	 * TODO: This function to me is a bit weird. It returns an object that tells the editor how to use it. But you're not really 
	 * editing the publication in the method itsself. So really the name "editPublication" to me isn't very clear. There's more things
	 * that need to change here, but I'll go into the function itsself. 
	 */
	public static JSONArray editPublication(String poster_url){
		System.out.println("Publication.editPublication(" + poster_url + ")");
        FsNode poster_node = Fs.getNode(poster_url);
        JSONArray jsarr = new JSONArray();

        JSONObject Oldid = new JSONObject();
        Oldid.put("id", poster_node.getId());
        jsarr.add(Oldid);
        
        Document d = null;

        try {
			d = DocumentHelper.parseText(poster_node.getProperty("html"));

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try{
        	//TODO: Change the layout so that h1[@class=\"title\"] is like [@data-section-type], <h1 class="title" data-section-type="title"></h1>
			Node title = d.selectSingleNode("//div[@class=\"title\"]");			 
			
			//TODO: Again get rid of underscores, this should be refactored to mediaItem, also again, get rid of selecting media_item nodes by using the class.
			//Should be like: d.selectNodes("//[@data-section-type=\"media_item\"]"), also for the text_items;
			List<Node> media_item = d.selectNodes("//div[@data-section-type=\"media\"]");
			List<Node> text_item = d.selectNodes("//div[@data-section-type=\"text_big\"]");
			List<Node> links = d.selectNodes("//link");
	
			//Get styles
			//TODO: Extracting this from the HTML is brittle. Let's try to get a reference to this in the actual XML of the video poster. 
			//Like this: 
			/*
			 * <videoposter id="EUS_7AC117E7E4D4B622E4D4B622E4D4B622">
				<properties>
					<author>David Ammeraal</author>
					<creationDate>Wed Dec 02 15:15:54 CET 2015</creationDate>
					<name>LORUM IPSUM</name>
					<layout>layout1.css</layout>
					<theme>theme1.css</theme>
					<image/>
				</properties>
			 */
			
			//In that way we can get rid of a whole lot of code here, and make this function a bit more robust. 
			Element layoutStyleURL = (Element)links.get(1);
			Element colorShemaURL = (Element)links.get(2);
	
			String layoutHref = layoutStyleURL.attributeValue("href").trim();
			String colorHref = colorShemaURL.attributeValue("href").trim();
	
			String[] splits = layoutHref.split("/");
			String layoutStr = splits[splits.length - 1];
			layoutStr = layoutStr.trim();
			String layoutt = EuscreenpublicationbuilderApplication.layoutWithStyle.get(layoutStr);
			
			JSONObject layout = new JSONObject();
			layout.put("type", "layout");
			layout.put("layout_type", layoutt);
			jsarr.add(layout);
	
			JSONObject styles = new JSONObject();
			styles.put("type", "styles");
			styles.put("layout", layoutHref);
			String style = EuscreenpublicationbuilderApplication.styleWithId.get(colorHref);
			styles.put("colorSchema", style);
			jsarr.add(styles);
	
			//Get title object
			Element title_el = (Element) title;
			JSONObject titleObject = new JSONObject();
			titleObject.put("type", "title");
			titleObject.put("id", title_el.attributeValue("id"));
			titleObject.put("value", title.getStringValue());
			jsarr.add(titleObject);
	
			//Get media items objects
			for (Node node : media_item) {
				Element el = (Element) node;
	
				JSONObject mediaObject = new JSONObject();
				mediaObject.put("type", "media_item");
				mediaObject.put("id", el.attributeValue("id"));
				mediaObject.put("value", node.getStringValue());
				jsarr.add(mediaObject);
			}
	
	
			//Get text item objects
			for (Node node : text_item) {
				Element el = (Element) node;
				JSONObject textObject = new JSONObject();
				textObject.put("type", "text_item");
				textObject.put("id", el.attributeValue("id"));
				textObject.put("value", node.getStringValue());
				jsarr.add(textObject);
			}
        }catch(Exception e){
        	e.printStackTrace();
        }
        System.out.println("FINISHED Publication.editPublication()");
		return jsarr;
	}

	
	public static JSONObject createXML(Publication publication, String user, String id){
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();
		String layoutTemplate = layout.getProperty("template").trim();

		String theme = null;
		if(publication.theme.getCurrentTheme() != null){
			if(publication.theme.getCurrentTheme().getProperty("css") != null){
				theme = publication.theme.getCurrentTheme().getProperty("css");
			}
		}
		
		String html_layout = buildHtml(layoutTemplate, layoutStyle, theme);

		Document d = null;
		try {
			d = DocumentHelper.parseText(html_layout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JSONObject object = buildXml(d, publication, user, null, id);

        return object;
	}

	public static JSONObject editXml(Publication publication, String user, String id, String oldId){
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

		String html_layout = buildHtml(layoutTemplate, layoutStyle, theme);
		
		Document d = null;
		try {
			d = DocumentHelper.parseText(html_layout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject object = buildXml(d, publication, user, oldId, null);

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
		String html_layout = buildHtml(layoutTemplate, layoutStyle, theme); 
		

		Document d = null;
		try {
			d = DocumentHelper.parseText(html_layout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject object = buildXml(d, publication, user, null, null);

        return object;
	}
	
	public static JSONObject buildXml (Document d, Publication publication, String user, String oldId, String id) {
		JSONObject object = new JSONObject();
		List<Node> media_items = d.selectNodes("//div[@data-section-type=\"media\"]");
		List<Node> text_items = d.selectNodes("//div[@data-section-type=\"text_big\"]");
		List<Node> title_items = d.selectNodes("//h1[@class=\"title\"]");

		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();

		Bookmarks bookmarks = new Bookmarks(user);
		for (Node media_item : media_items) {
			for(int i = 0; i < mediaItemList.size(); i++) {
				Element element = (Element) media_item;
				if (element != null && mediaItemList.get(i).getId() != null) {
					if (mediaItemList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						element.clearContent();
						String media = null;
						if(mediaItemList.get(i).getValue() != null){
							if (mediaItemList.get(i).getValue().toString().contains("http://www.youtube.com") || mediaItemList.get(i).getValue().toString().contains("https://player.vimeo")) {
								media = "<iframe class=\"videoAfterDrop\" src='" + mediaItemList.get(i).getValue().toString() + "' frameborder=\"0\" allowfullscreen></iframe>";
							}else {
								String src = mediaItemList.get(i).getValue().toString();
								src = src.substring(0, src.lastIndexOf("?"));
								media = "<video data-src=\"" + src + "\" data-poster=\"" + mediaItemList.get(i).getPoster() + "\"/>";
							}

							media_item.setText(media);
						}
					}
				}
			}
		}

		for (Node text_item : text_items) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) text_item;
				if (element != null && textContentList.get(i).getId() != null) {
					if (textContentList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						text_item.setText(textContentList.get(i).getValue().toString());

					}
				}
			}
		}

		String xmlTitle = null;
		for (Node title : title_items) {
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
        
        PublicationHTMLWriter writer = new PublicationHTMLWriter();
        object.put("xml", writer.getHTML(d));
        
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
		result.append("$fullScreenIcon = $('<i class=\"fullscreen glyphicon glyphicon-resize-full\"></i>');");
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
}




