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
				tinymce.init({selector: "#txt"});
			});
		}
	}else if(data.html){
		this.element.html(data.html);
	}

}