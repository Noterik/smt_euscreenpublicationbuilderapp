package org.springfield.lou.application.types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.swing.text.html.HTML;

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

import com.sun.org.apache.bcel.internal.generic.NEW;


public class Publication extends VideoPoster{

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
		
		
		String layoutt = EuscreenpublicationbuilderApplication.layoutWithStyle.get(layoutHref);
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
			+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"/>"
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\"/>" 
			+ "<link rel=\"stylesheet\" href=\"https://maxcd.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>"
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + "http://images1.noterik.com/euscreen/publicationbuilder/style/comparison_after.css" + "'></link>" 
			+ "</head>"
			+ "<body><div id=\"layout\" style=\"width: 50%;margin: 0 auto;\">"
			+ layout.getProperty("template").trim()
			+ "</div></body></html>";

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
							if(mediaItemList.get(i).getPoster() != null){
								media = "<video class=\"videoAfterDrop\" poster='" + mediaItemList.get(i).getPoster() + "' controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";	
							}else {
								media = "<video class=\"videoAfterDrop\" controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";
							}
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
        object.put("xml", d.asXML());
        System.out.println("-----------------------------CREATE XML-----------------------");
        System.out.println(object.get("xml"));
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
			+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"/>"
			+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>"
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\"/>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + "http://images1.noterik.com/euscreen/publicationbuilder/style/comparison_after.css" + "'></link>" 
			+ "</head>"
			+ "<body><div id=\"layout\" style=\"width: 50%;margin: 0 auto;\">"
			+ layout.getProperty("template").trim()
			+ "</div></body></html>";

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
							if(mediaItemList.get(i).getPoster() != null){
								media = "<video class=\"videoAfterDrop\" poster='" + mediaItemList.get(i).getPoster() + "' controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";	
							}else {
								media = "<video class=\"videoAfterDrop\" controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";
							}
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
        object.put("xml", d.asXML());
        
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
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\"/>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + "http://images1.noterik.com/euscreen/publicationbuilder/style/comparison_after.css" + "'></link>" 
			+ "</head>"
			+ "<body><div id=\"layout\" style=\"width: 50%;margin: 0 auto;\">"
			+ layout.getProperty("template").trim()
			+ "</div></body></html>";

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
					Element element = (Element) media_item;
					if (element != null && mediaItemList.get(i).getId() != null) {
						if (mediaItemList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
							element.clearContent();
							String media = null;
							if(mediaItemList.get(i).getValue() != null){
								if(mediaItemList.get(i).getValue().toString().contains("http://www.youtube.com") || mediaItemList.get(i).getValue().toString().contains("https://player.vimeo")) {
									media = "<iframe class=\"videoAfterDrop\" src='" + mediaItemList.get(i).getValue().toString() + "' frameborder=\"0\" allowfullscreen></iframe>";
								}else {
									if(mediaItemList.get(i).getPoster() != null){
										media = "<video class=\"videoAfterDrop\" poster='" + mediaItemList.get(i).getPoster() + "' controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";	
									}else {
										media = "<video class=\"videoAfterDrop\" controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";
									}
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
        object.put("xml", d.asXML());
        
        return object;
	}
}
