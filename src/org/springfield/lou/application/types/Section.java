package org.springfield.lou.application.types;

import java.util.List;

public class Section {
	public MediaSection mediaSection;
	public TextSection textSection;
	public Section() {
		this.mediaSection = new MediaSection(null, null, null);
		this.textSection = new TextSection(null, null);
	}
}
