var LayoutsContent = function(options){
	Component.apply(this, arguments);
}

LayoutsContent.prototype = Object.create(Component.prototype);

LayoutsContent.prototype.accordion = function () {
    $(".accordion").accordion();
    
	$("#left-header-layout").css('background-color', 'lightgray');
	$("#layouts").toggle( "fast" );
	
	$("#left-header-theme").css('background-color', '#00AEEF');
	$("#color_schemes").toggle("fast");  	
}

LayoutsContent.prototype.setLayoutClick = function(count) {
	console.log("test");
	var self = this;

	for(var i = 0; i < count; i++)
	{
		this.bindLayoutClick(i);
	}
};

LayoutsContent.prototype.bindLayoutClick = function(i) {
	var result = JSON.stringify({none: "none"});
	var function_name ="setlayout"+i+"(" + result + ")"; 
	
	$('#layout_' + i).click(function(){
		console.log("setLayout");
		eddie.putLou("", "setlayout"+"(" + i + ")");
	});
};
