var LayoutsContent = function(options){
	Component.apply(this, arguments);
	this.element = jQuery("#layoutsContent");
	this.layoutListTemplate = _.template(this.element.find('#layout_listing_template').text());


}

LayoutsContent.prototype = Object.create(Component.prototype);

LayoutsContent.prototype.setLayoutClick = function(count) {
	console.log("test");
	var self = this;

	for(var i = 0; i < count; i++)
	{
		this.bindLayoutClick(i);
	}
};

LayoutsContent.prototype.bindLayoutClick = function(i) {
	var result = JSON.stringify({none: "none"});
	var function_name ="setlayout"+i+"(" + result + ")"; 
	
	$('#layout_' + i).click(function(){
		LayoutsContent.prototype.closeLayoutsTab();
		eddie.putLou("", "setlayout"+"(" + i + ")");
	});
};

LayoutsContent.prototype.listLayouts = function(data) {
	var html = jQuery(this.layoutListTemplate({data: JSON.parse(data)}));
	this.element.append(html);
	this.setLayoutClick(data.length);
};

LayoutsContent.prototype.closeLayoutsTab = function () {
	$layout = $("#layout-nav");
	$layout.css("color", "lightgray");
	$layout.unbind("click");
	$layout.removeClass("arrow-pinter");
	
	eddie.putLou("", "generatecolorschemes()");
	
	$("#color-schemes").addClass("arrow-pinter");	
}