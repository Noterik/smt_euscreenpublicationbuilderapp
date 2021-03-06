var Left = function(options){
	Component.apply(this, arguments);
	setTimeout(function(){ 
		$( ".leftNavUl" ).hover(function() {
			$('.leftNavUl').notify("Once you select one\n of these layouts you\n cannot come back and\n change it. Take your time\n and select with care.",   { position:"right", className: "warn", autoHideDelay: 3400});
		});	 
		
		$( ".themeNavi" ).hover(function() {
			$('.themeNavi').notify('To approve your color schema please click on "color schemes title"',   { position:"right", className: "warn", autoHideDelay: 3400});
		});
		
		var $box = $('#colorPicker');
            $box.tinycolorpicker();
            
            $("#left-header-theme").css('background-color', 'lightgray')
            $("#color_schemes").toggle("fast");
            
            $("#left-header-background").css('background-color', 'lightgray');
            $("#background").toggle("fast");
	}, 100);
	
}

Left.prototype = Object.create(Component.prototype);

Left.prototype.accordion = function () {
    $(".accordion").accordion();
    
	$("#left-header-layout").css('background-color', 'lightgray');
	$("#layouts").toggle( "fast" );
	
	$("#left-header-theme").css('background-color', '#00AEEF');
	$("#color_schemes").toggle("fast");  	
}

Left.prototype.setLayoutClick = function(count) {
	var self = this;

	for(var i = 0; i < count; i++)
	{
		this.bindLayoutClick(i);
	}
};

Left.prototype.bindLayoutClick = function(i) {
	var result = JSON.stringify({none: "none"});
	
	$('#layout_' + i).click(function(){
		console.log("setLayout");
		var message = {
			layoutId: "" + i
		};
		eddie.putLou("", "setLayout(" + JSON.stringify(message) + ")");
	});
};

Left.prototype.setThemeClick = function(count) {
	console.log("test");
	var self = this;

	for(var i = 0; i < count; i++)
	{
		this.bindThemeClick(i);
	}
};

Left.prototype.bindThemeClick = function(i) {	
	$('#theme_' + i).click(function(){
		var message = {
			themeId: "" + i
		};
		eddie.putLou("", "setTheme(" + JSON.stringify(message) + ")");
	});
};

Left.prototype.approveTheme = function() {
	$('#left-header-theme').click(function(){
		eddie.putLou("", "approveTheme()");
	});
};

Left.prototype.accordionThemes = function () {
	    $(".accordion").accordion();
    	
    	$("#left-header-theme").css('background-color', '#lightgray');
    	$("#color_schemes").toggle("fast");
    	
    	$("#left-header-background").css('background-color', '#00AEEF');
    	$("#background").toggle("fast");    	
}