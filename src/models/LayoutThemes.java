package models;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.Configuration;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screencomponent.component.ScreenComponent;

public class LayoutThemes extends ScreenComponent {
	
	private Layout layout;
	private Themes themes;
	private List<LayoutTheme> layoutThemes;
	
	public LayoutThemes(Screen s, Layout layout, Themes themes) {
		super(s);
		this.layout = layout;
		this.themes = themes;
		populate();
		// TODO Auto-generated constructor stub
	}

	public LayoutThemes(Screen s, Layout layout, Themes themes, String target) {
		super(s, target);
		this.layout = layout;
		this.themes = themes;
		populate();
		// TODO Auto-generated constructor stub
	}
	
	@JSONField(field = "layoutThemes")
	public List<LayoutTheme> getLayoutThemes(){
		return layoutThemes;
	}
	
	public Themes getThemes(){
		return themes;
	}
	
	private void populate(){
		layoutThemes = new ArrayList<LayoutTheme>();
		for(Theme theme : themes.getThemes()){
			layoutThemes.add(new LayoutTheme(layout, theme));	
		}
	}
	
}
