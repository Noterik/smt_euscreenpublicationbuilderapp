package org.springfield.lou.application.types;

import java.util.List;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Layout {
	private List<FsNode> layouts;
	private String address = "/domain/euscreenxl/layout"; 
	
	public List<FsNode> getLayouts() {
		return layouts;
	}
	
	public void setLayouts(List<FsNode> layouts) {
		this.layouts = layouts;
	}
	
	public  FsNode getLayoutBy(int index) {
		return layouts.get(index);
	}
	
	public Layout() {
		setLayouts(Fs.getNodes(this.address, 1));
	}
}
