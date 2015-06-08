var Comparison = function(options){
	Component.apply(this, arguments);	
}
Comparison.prototype = Object.create(Component.prototype);
Comparison.prototype.setComparisonStyle = function () {
	// $('head').append('<link rel="stylesheet" type="text/css" href="/eddie/apps/euscreenpublicationbuilder/css/comparison.css">');
	$('#comparison_page').css('background-color', 'blue');
	$('#media_item').css({'height':'200px', 'float':'left', 'width':'50%', 'border':'1px solid black'});
	$('head').append('<link rel="stylesheet" type="text/css" href="/eddie/apps/euscreenpublicationbuilder/css/comparison.css">');

	console.log("Kolaveri Di");
};