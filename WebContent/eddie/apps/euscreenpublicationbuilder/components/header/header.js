var Header = function(options){
	Component.apply(this, arguments);
	
	$('#preview').click(function(){
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
			if($(this)[0].children[0].src == undefined){
				obj.value = $($($(this)[0].children[1]).children()[0]).children()[0].src
				obj.poster = $($(this)[0].children[1]).children()[0].poster;
			}else{
				obj.value = $(this)[0].children[0].src;
			}
			mediaArray.push(obj);
		});
		
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
		eddie.putLou("", "preview(" + result + ")");
		
		
		/*var result = JSON.stringify({html:"<div>HASHTAG</div>"});
		console.log(result);
		eddie.putLou("", "preview(" + result + ")");*/
	});
	
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
			if($(this)[0].children[0].src == undefined){
				obj.value = $($($(this)[0].children[1]).children()[0]).children()[0].src
				obj.poster = $($(this)[0].children[1]).children()[0].poster;
			}else{
				obj.value = $(this)[0].children[0].src;
			}
			mediaArray.push(obj);
		});
		
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
		eddie.putLou("", "proccesspublication(" + result + ")");
		$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
	});
}
Header.prototype = Object.create(Component.prototype);

Header.prototype.success = function() {
	$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
}