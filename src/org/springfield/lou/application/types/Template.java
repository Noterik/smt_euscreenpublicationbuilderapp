package org.springfield.lou.application.types;


public class Template {
	protected Section sections;
	protected Layout layout;
	
	public Template() {
		this.sections = new Section();
		this.layout = new Layout();
	}
}
