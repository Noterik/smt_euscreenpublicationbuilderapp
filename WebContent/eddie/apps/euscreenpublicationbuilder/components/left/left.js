var Left = function(options){
	Component.apply(this, arguments);
	setTimeout(function(){ 
		console.log($(".layout_image"));
		$( ".leftNavUl" ).hover(function() {
			console.log("neshto");
			$('.leftNavUl').notify("Once you select one\n of these layouts you\n cannot come back and\n change it. Take your time\n and select with care.",   { position:"right", className: "warn", autoHideDelay: 3400});
		});	 
	}, 100);
	
}

Left.prototype = Object.create(Component.prototype);

Left.prototype.accordion = function () {
	    $(".accordion").accordion();
    	console.log($(".accordion"));
    	console.log($(this));
//    	$(this).find("#layouts").toggle( "fast" );
    	$("#layouts").toggle( "fast" );
    	$("#left-header").css('background-color', 'lightgray')
}