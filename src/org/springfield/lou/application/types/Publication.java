package org.springfield.lou.application.types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.text.html.HTML;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.DTO.MediaItem;
import org.springfield.lou.application.types.DTO.TextContent;


public class Publication extends VideoPoster{

	public Publication() {
		super();

	}
	
	public static void createXML(Publication publication){
		System.out.println("createXML()");
		FsNode layout = publication.template.layout.getCurrentLayout();
		String layoutStyle = publication.template.layout.getCurrentLayoutStyle();
		System.out.println("layout style: " + layoutStyle);
		String theme = null;
		if(publication.theme.getCurrentTheme().getProperty("css") != null){
			theme = publication.theme.getCurrentTheme().getProperty("css");
		}
		
		
		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();
		System.out.println("----------------------------");
		System.out.println("CreateXML()");
		System.out.println(theme);
		System.out.println("----------------------------");
		
		String html_layout = "<html><head><title>First parse</title>"
			+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"/>"
			+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\"></link>"
			+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js\"/>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + layoutStyle + "'></link>" 
			+ "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>" 
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
		Bookmarks bookmarks = new Bookmarks();
		for (Node media_item : media_items) {
			for(int i = 0; i < mediaItemList.size(); i++) {
				Element element = (Element) media_item;
				if (element != null && mediaItemList.get(i).getId() != null) {
					if (mediaItemList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						element.clearContent();
						String media = null;
						if (mediaItemList.get(i).getValue().toString().contains("http://www.youtube.com") || mediaItemList.get(i).getValue().toString().contains("https://player.vimeo")) {
							media = "<iframe class=\"videoAfterDrop\" src='" + mediaItemList.get(i).getValue().toString() + "'></iframe>";
						}else {
							media = "<video class=\"videoAfterDrop\" controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";
						}
						
						media_item.setText(media);
						System.out.println("-----------------Media item-----------------");
						System.out.println(media_item.asXML());
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

		for (Node title : title_items) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) title;

				if (element != null && textContentList.get(i).getId() != null) {
					if (textContentList.get(i).getId().trim().equals(element.attributeValue("id").trim())) {
						title.setText(textContentList.get(i).getValue().toString());
						System.out.println(textContentList.get(i).getValue().toString());
					}
				}
			}
		}
		
		String user = "david";
		UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        
        System.out.println("UNIQUE IDENTIFIRE: " + randomUUIDString);
		
        FsNode posterNode = new FsNode("videoposter", randomUUIDString);
		posterNode.setPath("/domain/euscreenxl/user/" + user + "/publications/1/videoposter/" + randomUUIDString);
		posterNode.setProperty("xml", d.asXML());
		Fs.insertNode(posterNode, "/domain/euscreenxl/user/" + user + "/publications/1");
		
	}
}
