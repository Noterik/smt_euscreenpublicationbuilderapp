var Layout = function(options){
	this.element = jQuery("#layout");
	Component.apply(this, arguments);	
}
Layout.prototype = Object.create(Component.prototype);

Layout.prototype.update = function(message){
	var self = this;
	console.log("Layout.update(" + message + ")");
	var data = JSON.parse(message);
	
	if(data.style){
		var styleElement = $('<link rel="stylesheet" type="text/css" href="' + data.style + '">');
		$('head').append(styleElement);
		if(data.html){
			styleElement.load(function(){
				self.element.html(data.html);
			});
		}
	}else if(data.html){
		this.element.html(data.html);
	}
}
/*
Layout.prototype.setStyle = function(){
	
};
Layout.prototype.loadHTML = function(html){
	
};
Layout.prototype.setComparisonStyle = function(){
	// $('head').append('<link rel="stylesheet" type="text/css" href="/eddie/apps/euscreenpublicationbuilder/css/comparison.css">');
	$('#comparison_page').css('background-color', 'blue');
	$('#media_item').css({'height':'200px', 'float':'left', 'width':'50%', 'border':'1px solid black'});
	$('head').append('<link rel="stylesheet" type="text/css" href="/eddie/apps/euscreenpublicationbuilder/css/comparison.css">');
	//tinymce.init({selector:'#middle'});
};
*/