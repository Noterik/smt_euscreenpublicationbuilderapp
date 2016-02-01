var Header = function(options){
	Component.apply(this, arguments);
	$("#edit").hide();

	$('#x-icon').click(function(){
		eddie.getComponent('iframesender').sendToParent('close');
	});


	$('#preview').on('click', function(){
		eddie.putLou('', 'preview()');
	});

};
Header.prototype = Object.create(Component.prototype);

Header.prototype.success = function() {
	$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
};

Header.prototype.setStep = function(message){
	message = JSON.parse(message);
	var steps = {
		'layouts': $('#layout-nav'),
		'themes': $('#color-schemes'),
		'build': $('#build')
	};

	if(message['step']){
		var step = message.step;

		for(var cStep in steps){
			steps[cStep].addClass('inactive');
			steps[cStep].removeClass('arrow-pointer');
		}

		steps[step].removeClass('inactive');
		steps[step].addClass('arrow-pointer');
	}
}
});
