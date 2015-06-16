var Left = function(options){
	Component.apply(this, arguments);
	
}

Left.prototype = Object.create(Component.prototype);

Left.prototype.accordion = function () {
	    $(".accordion").accordion();
    	console.log($(".accordion"));
    	console.log("bang bang");
}