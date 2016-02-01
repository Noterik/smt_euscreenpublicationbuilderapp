var Layoutthemes = function(options){
	console.log("Layoutthemes()");
	Component.apply(this, arguments);
	
	this.on('themes-changed', function(themes){
		console.log("THEMES: " , themes);
	});
};
Layoutthemes.prototype = Object.create(Component.prototype);