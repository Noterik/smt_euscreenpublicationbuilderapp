package models;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.Configuration;
import org.springfield.lou.json.JSONSerializable;

public class Themes extends JSONSerializable {
	private String address = Configuration.getDomain() + "/user/admin/config/publicationbuilder/theme";
	private List<Theme> themes;
	
	public Themes(){
		populate();
	}
	
	public Theme getThemeById(String id){
		for(Theme theme : themes){
			if(theme.getId().equals(id)){
				return theme;
			}
		}
		return null;
	}
		
	public List<Theme> getThemes(){
		return themes;
	}
	
	private void populate(){
		themes = new ArrayList<Theme>();
		List<FsNode> nodes = Fs.getNodes(address, 1);
		for(FsNode node : nodes){
			System.out.println("NODE ID: " + node.getId());
			if(node.getProperty("css") == null){
				try {
					Theme theme = new Theme(node);
					themes.add(theme);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("EXCEPTION!");
					e.printStackTrace();
				}
				
			}
		}
	}
}
