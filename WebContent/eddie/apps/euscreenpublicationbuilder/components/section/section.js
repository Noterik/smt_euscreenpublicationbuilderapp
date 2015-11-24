var Section = function(options){
	this.element = jQuery("#section");
	 Component.apply(this, arguments);
	 
	 this.element.hide();
}

Section.prototype = Object.create(Component.prototype);
