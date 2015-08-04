var Header = function(options){
	Component.apply(this, arguments);
	
	$('#publish').click(function(){
		var textAreas = [];
		$('.text_item').each(function(index) {
			var obj = {};
			obj.id = $(this).attr('id');
			obj.value = tinyMCE.get($(this).attr('id')).getContent();;
			textAreas.push(obj);			
		});
		
		 $('.title').each(function(index){
	    	console.log("title");
	    	console.log($(this));
	    	var obj = {};
	    	obj.id = $(this).attr('id');
	    	obj.value = $(this).text();
	    	console.log(obj);
	    	textAreas.push(obj);
	    });
		
		var mediaArray = [];
		$('.media_item').each(function(index) {
			var obj = {};
		
			obj.id = $(this).attr('id');
			if($(this)[0].childNodes[3] == undefined){
				obj.value = $(this)[0].childNodes[0].src;
			}else{
				obj.value = $(this)[0].childNodes[3].childNodes[0].childNodes[0].src;
			}
			mediaArray.push(obj);
		});
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
		eddie.putLou("", "proccesspublication(" + result + ")");
	});
}
Header.prototype = Object.create(Component.prototype);
