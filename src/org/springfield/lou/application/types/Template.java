package org.springfield.lou.application.types;


public class Template {
	protected Section sections;
	protected Layout layout;
	
	public Section getSections() {
		return sections;
	}

	public void setSections(Section sections) {
		this.sections = sections;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public Template() {
		this.sections = new Section();
		this.layout = new Layout();
	}
}
