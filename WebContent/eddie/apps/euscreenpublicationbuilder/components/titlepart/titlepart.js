var Titlepart = function(options){
	Component.apply(this, arguments);	

	var self = {};
	var settings = {}
	
	$.extend(settings, options);

	return self;
}

Titlepart.prototype = Object.create(Component.prototype);
