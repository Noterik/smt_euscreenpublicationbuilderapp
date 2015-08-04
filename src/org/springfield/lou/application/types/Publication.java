package org.springfield.lou.application.types;

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
		String theme = publication.theme.getCurrentTheme().getProperty("css");
		List<TextContent> textContentList = publication.template.sections.textSection.getTextContents();
		List<MediaItem> mediaItemList = publication.template.sections.mediaSection.getMediaItems();
		System.out.println("----------------------------");
		System.out.println("CreateXML()");
		System.out.println(theme);
		System.out.println("----------------------------");
		
		String html_layout = "<html><head><title>First parse</title>" + "<link rel=\"stylesheet\" type=\"text/css\" href='" + theme + "'></link>" + "</head><body>" + layout.getProperty("template").trim() + "</body></html>";

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
						String media = "<video class=\"layout_image\" controls><source src='" + mediaItemList.get(i).getValue().toString() + "' type=\"video/mp4\"></video>";
						media_item.setText(media);
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
		System.out.println("-----------------");
		System.out.println("Title items");
		for (Node title : title_items) {
			for(int i = 0; i < textContentList.size(); i++) {
				Element element = (Element) title;

				System.out.println(title + "---->" + element.asXML());

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
