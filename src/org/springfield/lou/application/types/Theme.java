package org.springfield.lou.application.types;

import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Theme {
	private List<FsNode> themes;
	private String address = "/domain/euscreenxl/user/admin/config/publicationbuilder/theme"; 
	public FsNode currentTheme;
	
	public FsNode getCurrentTheme() {
		return this.currentTheme;
	}

	public void setCurrentTheme(FsNode currentTheme) {
		this.currentTheme = currentTheme;
	}

	public List<FsNode> getThemes() {
		return themes;
	}
	
	public void setThemes(List<FsNode> themes) {
		this.themes = themes;
	}
	
	public FsNode getLayoutBy(int index) {
		return themes.get(index);
	}
	
	public Theme() {
		setThemes(Fs.getNodes(this.address, 1));
	}
}
