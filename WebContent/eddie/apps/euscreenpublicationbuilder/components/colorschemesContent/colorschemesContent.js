var ColorschemesContent = function(options){
	Component.apply(this, arguments);
	
	this.element = jQuery("#colorschemesContent");
	this.themeListTemplate = _.template(this.element.find('#theme_listing_template').text());
}

ColorschemesContent.prototype = Object.create(Component.prototype);

ColorschemesContent.prototype.setThemeClick = function(count) {
	console.log("test");
	var self = this;
	console.log("=============BIND THEME CLICK(" + count + ")");
	for(var i = 0; i < count; i++)
	{
		this.bindThemeClick(i);
	}
};

ColorschemesContent.prototype.bindThemeClick = function(i) {
	var result = JSON.stringify({none: "none"});
	var function_name ="settheme"+i+"(" + result + ")"; 
	
	$('#theme_' + i).click(function(){
		ColorschemesContent.prototype.closeThemesTab();

		eddie.putLou("", "settheme"+"(" + i + ")");
	});
};

ColorschemesContent.prototype.listThemes = function(data) {
	var html = jQuery(this.themeListTemplate({data: JSON.parse(data)}));
	this.element.append(html);
	this.setThemeClick(data.length);
};


ColorschemesContent.prototype.closeThemesTab = function () {
	$("#colorschemesContent").hide();
	$("#buildContent").show();
	
	$colorSchemes = $("#color-schemes");
	$colorSchemes.removeClass("arrow-pinter");	

	$layout = $("#layout-nav");
	$layout.css("color", "lightgray");
	$layout.unbind("click");
	$layout.removeClass("arrow-pinter");
	
	eddie.putLou("", "generatebuild()");
	$('#buildContent').show();
	
	$colorSchemes = $("#color-schemes");
	$colorSchemes.css("color", "lightgray");
	$colorSchemes.removeClass("arrow-pinter");
	$colorSchemes.unbind('click');
	
	
	$("#build").addClass("arrow-pinter");	
}