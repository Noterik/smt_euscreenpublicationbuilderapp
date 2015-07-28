package org.springfield.lou.application.types;

import java.util.List;

import org.springfield.fs.FsNode;

public class Template {
	protected Section sections;
	protected Layout layout;
	
	public Template() {
		this.sections = new Section();
		this.layout = new Layout();
	}
}
