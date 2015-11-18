var ColorschemesContent = function(options){
	Component.apply(this, arguments);
}

LayoutsContent.prototype = Object.create(Component.prototype);

LayoutsContent.prototype.setThemeClick = function(count) {
	console.log("test");
	var self = this;

	for(var i = 0; i < count; i++)
	{
		this.bindThemeClick(i);
	}
};

LayoutsContent.prototype.bindThemeClick = function(i) {
	var result = JSON.stringify({none: "none"});
	var function_name ="settheme"+i+"(" + result + ")"; 
	
	$('#theme_' + i).click(function(){
		eddie.putLou("", "settheme"+"(" + i + ")");
	});
};

LayoutsContent.prototype.approveTheme = function() {
	$('#left-header-theme').click(function(){
		eddie.putLou("", "approvetheme()");
	});
};

LayoutsContent.prototype.accordionThemes = function () {
	    $(".accordion").accordion();
    	
    	$("#left-header-theme").css('background-color', '#lightgray');
    	$("#color_schemes").toggle("fast");
    	
    	$("#left-header-background").css('background-color', '#00AEEF');
    	$("#background").toggle("fast");    	
}