var Header = function(options){
	Component.apply(this, arguments);
	$("#edit").hide();

	$('#x-icon').click(function(){
		eddie.getComponent('iframesender').sendToParent('close');
	});
	
	$('#preview').on('click', function(){
		eddie.putLou('', 'preview()');
	});

}
Header.prototype = Object.create(Component.prototype);

Header.prototype.success = function() {
	$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
}

Header.prototype.modeEdit = function () {

}

Header.prototype.setStep = function(message){
	console.info("SET STEP: " + message);
	var message = JSON.parse(message);
	var steps = {
		'layouts': $('#layout-nav'),
		'themes': $('#color-schemes'),
		'build': $('#build')
	};
	
	if(message['step']){
		var step = message.step;
		
		for(cStep in steps){
			steps[cStep].addClass('inactive');
			steps[cStep].removeClass('arrow-pointer');
		}
		
		steps[step].removeClass('inactive');
		steps[step].addClass('arrow-pointer');
	}
}

//nav menu arrow classs toogle
$( ".nav-arrow-togle" ).click(function() {
	/*
	var arr = $( ".nav-arrow-togle" );
	for (var i = 0; i < arr.length; i++) {
		$(arr.get(i)).removeClass("arrow-pointer");  
	}
	
	$(this).addClass("arrow-pointer");
	
	switch($(this).attr("type")) {
    case "layout":
        	eddie.putLou("", "generateLayout()");
        break;
    case "colorShemes":
        	eddie.putLou("", "generateColorSchemes()");
        break;
    case "build" :
    		eddie.putLou("", "generateBuild()");
    	break;
    default:
		break;
	}
	
	return false;
	*/
});


