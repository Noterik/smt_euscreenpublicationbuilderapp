package models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.types.Configuration;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.screencomponent.component.ScreenComponent;

public class Layouts extends ScreenComponent {
	private String address = Configuration.getDomain() + "/user/admin/config/publicationbuilder/layout";
	private Map<String, Layout> layouts;

	public Layouts(Screen s) {
		super(s);
		populate();
		// TODO Auto-generated constructor stub
	}

	public Layouts(Screen s, String target) {
		super(s, target);
		populate();
		// TODO Auto-generated constructor stub
	}
	
	@JSONField(field = "layouts")
	public Collection<Layout> getLayouts(){
		return layouts.values();
	}
	
	public Layout getById(String id){
		return layouts.get(id);
	}

	private void populate(){
		layouts = new HashMap<String, Layout>();
		List<FsNode> layoutsList = Fs.getNodes(address, 1);
		for(FsNode layoutNode : layoutsList){
			if(layoutNode.getProperty("icon") == null){
				layouts.put(layoutNode.getId(), new Layout(layoutNode));
			}
		}
	}
}
