var Videoposter = function(options){
	Component.apply(this, arguments);
	
	this.on('template-changed', function(template){
		console.log("TEMPLATE: " + template);
	});
}
Videoposter.prototype = Object.create(Component.prototype);
