var Embedlib = function(){
	var self = this;
	this.ready = false;
	this.loaded = function(){
		console.info(Embedlib, " LOADED");
		return new Promise(function(resolve, reject){
			if(!self.ready){
				$.getScript("http://pb.euscreenxl.eu:8080/euscreen_embed/euscreenembedlib.js", function( data, textStatus, jqxhr ) {
					self.ready = true;
					resolve();
				});
			}else{
				resolve();
			}
		});
	};
};

Embedlib.prototype = Object.create(Component.prototype);