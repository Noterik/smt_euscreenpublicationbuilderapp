var Layouts = function(){
	Component.apply(this, arguments);
	console.log("Layouts()");
	
	this.on('layouts-changed', function(layouts){
		console.log(layouts);
	});
}
Layouts.prototype = Object.create(Component.prototype);