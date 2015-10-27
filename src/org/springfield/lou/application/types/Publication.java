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

	//Should really be put in a Config object that should be injected into every class that needs it, no time for this so quick and dirty way.
	private static String server = Fs.getNode("/domain/euscreenxl/user/admin/config/publicationbuilder").getProperty("server");
	private static String libServer =  Fs.getNode("/domain/euscreenxl/user/admin/config/publicationbuilder").getProperty("embedLib");
	
	public Publication() {
		super();
	}
	public static JSONArray editPublication(String poster_url){
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

		System.out.println(poster_node.asXML());
		Node title = d.selectSingleNode("//h1[@class=\"title\"]");
		List<Node> media_item = d.selectNodes("//div[@class=\"media_item\"]");
		List<Node> text_item = d.selectNodes("//div[@class=\"text_item\"]");
		List<Node> links = d.selectNodes("//link");

		//Get styles
		Element layoutStyleURL = (Element)links.get(1);
		Element colorShemaURL = (Element)links.get(2);

		String layoutHref = layoutStyleURL.attributeValue("href").trim();
		String colorHref = colorShemaURL.attributeValue("href").trim();

		String[] splits = layoutHref.split("/");
		String layoutStr = splits[splits.length - 1];
		layoutStr = layoutStr.trim();
		System.out.println("layoutStr: " + layoutStr.trim());
		System.out.println(EuscreenpublicationbuilderApplication.layoutWithStyle);
		System.out.println(EuscreenpublicationbuilderApplication.layoutWithStyle.containsKey(layoutStr));
		String layoutt = EuscreenpublicationbuilderApplication.layoutWithStyle.get(layoutStr);
		System.out.println("LAYOUT: " + layoutt);
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

		return jsarr;
	}

	public static JSONObject createXML(Publication publication, String user, String id){
		System.out.println("Publication.createXML()");
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();
		System.out.println("layout style: " + layoutStyle);
		String theme = null;
		if(publication.theme.getCurrentTheme() != null){
			if(publication.theme.getCurrentTheme().getProperty("css") != null){
				theme = publication.theme.getCurrentTheme().getProperty("css");
			}
		}


		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();

		String html_layout = "<html><head><title>First parse</title>"
			+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\">&#xA0;</script>"
			+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>"
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\">&#xA0;</script>"
			+ "<script src=\"" + libServer + "\">&#xA0;</script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + server + "/euscreenpublicationbuilder/css/layouts/comparison_after.css'></link>"
			+ "</head>"
			+ "<body style=\"background-color: rgba(0, 0, 0, 0); overflow-y: hidden; height: 100%\"><div id=\"layout\" style=\"width: 50%;margin: 0 auto; height: 100%; overflow-y: auto;\">"
			+ layout.getProperty("template").trim()
			+ "</div>"
			+ "<script type=\"text/javascript\">"
			+ "	   <![CDATA["		
			+ "    $('video[data-src]').each(function(index, video){"
			+ "			console.log(video); "
			+ "			var src = $(video).data('src');"
			+ "			var poster = $(video).data('poster');"
			+ "			EuScreen.getVideo({"
			+ "				src: src,"
			+ "				poster: poster,"
			+ "				controls: true"
			+ "			   }, (function(video){"
			+ "				   return function(html){"
			+ "					   var $video = $(html);"
			+ "					   $(video).replaceWith($video); "
			+ "					   $fullScreenIcon = $('<i class=\"fullscreen glyphicon glyphicon-resize-full\"></i>');"
			+ "					   $video.parent().append($fullScreenIcon);"						
			+ "					   $fullScreenIcon.on('click', function(){"
			+ "						var elem = $video[0];"
			+ "						if (elem.requestFullscreen) {"
			+ "							elem.requestFullscreen();"
			+ "						} else if (elem.msRequestFullscreen) {"
			+ "							elem.msRequestFullscreen();"
			+ "						} else if (elem.mozRequestFullScreen) {"
			+ "							elem.mozRequestFullScreen();"
			+ "						} else if (elem.webkitRequestFullscreen) {"
			+ "							elem.webkitRequestFullscreen();"
			+ "						}"
			+ "					   });"
			+ "				   }"
			+ "			   })(video)"
			+ "		   )"
			+ "	   });"
			+ " 	]]>"
			+ "</script>"
			+ "</body></html>";

		Document d = null;
		try {
			d = DocumentHelper.parseText(html_layout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Node> media_items = d.selectNodes("//div[@class=\"media_item\"]");
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

		List<Node> text_items = d.selectNodes("//div[@class=\"text_item\"]");

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

		List<Node> title_items = d.selectNodes("//h1[@class=\"title\"]");
		String xmlTitle = null;
		for (Node title : title_items) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) title;

				if (element != null && textContentList.get(i).getId() != null) {
					if (textContentList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						title.setText(textContentList.get(i).getValue().toString());
						xmlTitle = textContentList.get(i).getValue().toString();
						System.out.println(textContentList.get(i).getValue().toString());
					}
				}
			}
		}

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

		System.out.println(originalid);
		System.out.println("MD5="+eusId);


        JSONObject object = new JSONObject();
        object.put("type", "videoposter");
        object.put("id", eusId);
        object.put("title", xmlTitle);
        
        PublicationHTMLWriter writer = new PublicationHTMLWriter();
        object.put("xml", writer.getHTML(d));
        
        return object;

        /*
        FsNode posterNode = new FsNode("videoposter", randomUUIDString);
		posterNode.setPath("/domain/euscreenxl/user/" + user + "/publications/1/videoposter/" + randomUUIDString);
		posterNode.setProperty("title", xmlTitle);
		posterNode.setProperty("xml", d.asXML());


		Fs.insertNode(posterNode, "/domain/euscreenxl/user/" + user + "/publications/1");
		*/
	}

	public static JSONObject editXml(Publication publication, String user, String id, String oldId){
		System.out.println("createXML()");
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();
		System.out.println("layout style: " + layoutStyle);
		String theme = null;
		if(publication.theme.getCurrentTheme() != null){
			if(publication.theme.getCurrentTheme().getProperty("css") != null){
				theme = publication.theme.getCurrentTheme().getProperty("css");
			}
		}


		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();

		String html_layout = "<html><head><title>First parse</title>"
			+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\">gadfaf</script>"
			+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>"
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\">&#xA0;</script>"
			+ "<script src=\"" + libServer + "\">&#xA0;</script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + server + "/euscreenpublicationbuilder/css/layouts/comparison_after.css'></link>"
			+ "</head>"
			+ "<body style=\"background-color: rgba(0, 0, 0, 0); overflow-y: hidden; height: 100%\"><div id=\"layout\" style=\"width: 50%;margin: 0 auto; height: 100%; overflow-y: auto;\">"
			+ layout.getProperty("template").trim()
			+ "</div>"
			+ "<script type=\"text/javascript\">"
			+ "	   <![CDATA["
			+ "    $('.fullscreen').click(function(){"
			+ "		$video = $(this).parent().find('video');"
			+ "		$video[0].requestFullscreen();"
			+ "	   });"
			+ "    $('video[data-src]').each(function(index, video){"
			+ "			console.log(video); "
			+ "			var src = $(video).data('src');"
			+ "			var poster = $(video).data('poster');"
			+ "			EuScreen.getVideo({"
			+ "				src: src,"
			+ "				poster: poster,"
			+ "				controls: true"
			+ "			   }, (function(video){"
			+ "				   return function(html){"
			+ "					   var $video = $(html);"
			+ "					   $(video).replaceWith($video); "
			+ "					   $fullScreenIcon = $('<i class=\"fullscreen glyphicon glyphicon-resize-full\"></i>');"
			+ "					   $video.parent().append($fullScreenIcon);"						
			+ "					   $fullScreenIcon.on('click', function(){"
			+ "						var elem = $video[0];"
			+ "						if (elem.requestFullscreen) {"
			+ "							elem.requestFullscreen();"
			+ "						} else if (elem.msRequestFullscreen) {"
			+ "							elem.msRequestFullscreen();"
			+ "						} else if (elem.mozRequestFullScreen) {"
			+ "							elem.mozRequestFullScreen();"
			+ "						} else if (elem.webkitRequestFullscreen) {"
			+ "							elem.webkitRequestFullscreen();"
			+ "						}"
			+ "					   });"
			+ "				   }"
			+ "			   })(video)"
			+ "		   )"
			+ "	   });"
			+ " 	]]>"
			+ "</script>"
			+ "</body></html>";

		Document d = null;
		try {
			d = DocumentHelper.parseText(html_layout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Node> media_items = d.selectNodes("//div[@class=\"media_item\"]");
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

		List<Node> text_items = d.selectNodes("//div[@class=\"text_item\"]");

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

		List<Node> title_items = d.selectNodes("//h1[@class=\"title\"]");
		String xmlTitle = null;
		for (Node title : title_items) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) title;

				if (element != null && textContentList.get(i).getId() != null) {
					if (textContentList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						title.setText(textContentList.get(i).getValue().toString());
						xmlTitle = textContentList.get(i).getValue().toString();
						System.out.println(textContentList.get(i).getValue().toString());
					}
				}
			}
		}

        JSONObject object = new JSONObject();
        object.put("type", "videoposter");
        object.put("id", oldId);
        object.put("title", xmlTitle);
        
        PublicationHTMLWriter writer = new PublicationHTMLWriter();
        object.put("xml", writer.getHTML(d));

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

		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();

		String html_layout = "<html><head><title>First parse</title>"
			+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>"
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\">&#xA0;</script>"
			+ "<script src=\"" + libServer + "\">&#xA0;</script>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + server + "/euscreenpublicationbuilder/css/layouts/comparison_after.css'></link>"
			+ "</head>"
			+ "<body style=\"background-color: rgba(0, 0, 0, 0); overflow-y: hidden; height: 100%;\"><div id=\"layout\" style=\"width: 50%;margin: 0 auto; height: 100%; overflow-y: auto;\">"
			+ layout.getProperty("template").trim()
			+ "</div>"
			+ "<script type=\"text/javascript\">"
			+ "	   <![CDATA["
			+ "    $('.fullscreen').click(function(){"
			+ "		$video = $(this).parent().find('video');"
			+ "		$video[0].requestFullscreen();"
			+ "	   });"
			+ "    $('video[data-src]').each(function(index, video){"
			+ "			console.log('VIDEO: ', video);"
			+ "			var src = $(video).data('src);"
			+ "			var poster = $(video).data('poster');"
			+ "			EuScreen.getVideo({"
			+ "				src: src,"
			+ "				poster: poster,"
			+ "				controls: true"
			+ "			   }, (function(video){"
			+ "				   return function(html){"
			+ "					   var $video = $(html);"
			+ "					   $(video).replaceWith($video); "
			+ "					   $fullScreenIcon = $('<i class=\"fullscreen glyphicon glyphicon-resize-full\"></i>');"
			+ "					   $video.parent().append($fullScreenIcon);"						
			+ "					   $fullScreenIcon.on('click', function(){"
			+ "						var elem = $video[0];"
			+ "						if (elem.requestFullscreen) {"
			+ "							elem.requestFullscreen();"
			+ "						} else if (elem.msRequestFullscreen) {"
			+ "							elem.msRequestFullscreen();"
			+ "						} else if (elem.mozRequestFullScreen) {"
			+ "							elem.mozRequestFullScreen();"
			+ "						} else if (elem.webkitRequestFullscreen) {"
			+ "							elem.webkitRequestFullscreen();"
			+ "						}"
			+ "					   });"
			+ "				   }"
			+ "			   })(video)"
			+ "		   )"
			+ "	   });"
			+ " 	]]>"
			+ "</script>"
			+ "</body></html>";

		Document d = null;
		try {
			d = DocumentHelper.parseText(html_layout);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Node> media_items = d.selectNodes("//div[@class=\"media_item\"]");
		Bookmarks bookmarks = new Bookmarks(user);
		for (Node media_item : media_items) {
			System.out.println("MEDIA ITEM LIST : " + mediaItemList.size());
			if(mediaItemList.size() > 0){
				for(int i = 0; i < mediaItemList.size(); i++) {
					//TODO: Instead of always doing mediaItemList.get(i), just make a variable and store it. Makes it more readable.
					MediaItem currItem = mediaItemList.get(i);
					Element element = (Element) media_item;
					if (element != null && currItem.getId() != null) {
						if (currItem.getId().trim().equals(element.attributeValue("id").trim())) {
							element.clearContent();
							String media = null;
							if(mediaItemList.get(i).getValue() != null){
								if(mediaItemList.get(i).getValue().toString().contains("http://www.youtube.com") || mediaItemList.get(i).getValue().toString().contains("https://player.vimeo")) {
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
		}

		List<Node> text_items = d.selectNodes("//div[@class=\"text_item\"]");

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

		List<Node> title_items = d.selectNodes("//h1[@class=\"title\"]");
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

		UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();

        System.out.println("UNIQUE IDENTIFIRE: " + randomUUIDString);

        JSONObject object = new JSONObject();
        object.put("type", "videoposter");
        object.put("id", randomUUIDString);
        object.put("title", xmlTitle);

        PublicationHTMLWriter writer = new PublicationHTMLWriter();
	    object.put("xml", writer.getHTML(d));

        return object;
	}
}
