var Layout = function(options){
	 this.element = jQuery("#layout");
	 Component.apply(this, arguments);
}
Layout.prototype = Object.create(Component.prototype);
