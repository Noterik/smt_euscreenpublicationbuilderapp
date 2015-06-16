var Header = function(options){
	Component.apply(this, arguments);
	
	$('#publish').click(function(){
		var textAreas = [];
		$('.text').each(function(index) {
			var obj = {};
			obj.id = $(this).attr('id');
			obj.value = tinyMCE.get($(this).attr('id')).getContent();;
			textAreas.push(obj);
			
			console.log(tinyMCE.get($(this).attr('id')).getContent());
		});
		
		console.log(textAreas);
		var mediaArray = [];
		$('.media_item').each(function(index) {
			var obj = {};
			obj.id = "media_text_" + index;
			obj.value = "25c0d775-bd0a-442c-a99d-8384cfdd9ef2";
			mediaArray.push(obj);
		});
		
		console.log(mediaArray);
		eddie.putLou("", "proccesspublication()");
	});
}
Header.prototype = Object.create(Component.prototype);
