package org.springfield.lou.application.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

public class PublicationHTMLWriter extends HTMLWriter {

	public PublicationHTMLWriter(){
		super(new StringWriter(), OutputFormat.createPrettyPrint());
		Set<String> closeSet = this.getOmitElementCloseSet();
		
		String itemToRemove = null;
		for(Iterator<String> i = closeSet.iterator(); i.hasNext();){
			String tag = i.next();
			if(tag.equals("LINK")){
				itemToRemove = tag;
			}
		}
		closeSet.remove(itemToRemove);
		this.setOmitElementCloseSet(closeSet);
	}
	
	public String getHTML(Document d){
		try {
			this.write(d);
			this.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.writer.toString();
	}
}
