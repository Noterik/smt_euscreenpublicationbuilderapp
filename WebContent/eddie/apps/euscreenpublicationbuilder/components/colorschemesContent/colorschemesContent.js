var ColorschemesContent = function(options){
	Component.apply(this, arguments);
	var self = this;
	var element = jQuery("#colorschemesContent");
	var themeListTemplate = _.template(element.find('#theme_listing_template').text());
	var themes = eddie.getComponent('layoutthemes');
	
	function renderThemes(){
		var data = themes.get('layoutThemes');
		var html = jQuery(themeListTemplate({data: data}));
		element.html(html);
		
		element.find('[data-theme-id]').on('click', function(){
			var $this = jQuery(this);
			var message = {
				'themeId': $this.data('theme-id')
			};
			eddie.putLou('', 'setTheme(' + JSON.stringify(message) + ')');
		});
	};
	
	console.log("THEMES OBJECT", themes);
	themes.on('layoutThemes-changed', function(){
		renderThemes();
	});
	renderThemes();
	
	
}

ColorschemesContent.prototype = Object.create(Component.prototype);

/*
ColorschemesContent.prototype.setThemeClick = function(count) {
	console.log("test");
	var self = this;
	console.log("=============BIND THEME CLICK(" + count + ")");
	for(var i = 0; i < count; i++)
	{
		this.bindThemeClick(i);
	}
};
*/

/*
ColorschemesContent.prototype.bindThemeClick = function(i) {
	var result = JSON.stringify({none: "none"});
	
	$('#theme_' + i).click(function(){
		ColorschemesContent.prototype.closeThemesTab();
		var message = {
			themeId: "" + i
		};
		eddie.putLou("", "setTheme(" + JSON.stringify(message) + ")");
	});
};
*/


ColorschemesContent.prototype.closeThemesTab = function () {
	$("#colorschemesContent").hide();
	$("#buildContent").show();
	
	$colorSchemes = $("#color-schemes");
	$colorSchemes.removeClass("arrow-pinter");	

	$layout = $("#layout-nav");
	$layout.css("color", "lightgray");
	$layout.unbind("click");
	$layout.removeClass("arrow-pinter");
	
	$('#buildContent').show();
	$colorSchemes = $("#color-schemes");
	$colorSchemes.css("color", "lightgray");
	$colorSchemes.removeClass("arrow-pinter");
	$colorSchemes.unbind('click');
	
	
	$("#build").addClass("arrow-pinter");	
	$("#build").css("color", "black");	
}