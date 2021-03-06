package org.springfield.lou.application.types;

import java.util.List;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Layout {
	private List<FsNode> layouts;

	public FsNode currentLayout;
	public String currentLayoutStyle;

	public String getCurrentLayoutStyle() {
		return currentLayoutStyle;
	}

	public void setCurrentLayoutStyle(String currentLayoutStyle) {
		this.currentLayoutStyle = currentLayoutStyle;
	}

	public FsNode getCurrentLayout() {
		return this.currentLayout;
	}

	public void setCurrentLayout(FsNode currentLayout) {
		this.currentLayout = currentLayout;
	}

	public List<FsNode> getLayouts() {
		return layouts;
	}
	
	public void setLayouts(List<FsNode> layouts) {
		this.layouts = layouts;
	}
	
	public FsNode getLayoutBy(int index) {
		return layouts.get(index);
	}
	
	public Layout() {
		setLayouts(Fs.getNodes(Configuration.getLayoutUrl(), 1));
	}
}
