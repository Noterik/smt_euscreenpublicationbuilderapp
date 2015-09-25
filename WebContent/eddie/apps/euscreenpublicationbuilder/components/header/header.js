var Header = function(options){
	Component.apply(this, arguments);
	$("#edit").hide();

	var preview = true;
	$('#preview').click(function(){
		var textAreas = [];
		$('.text_item').each(function(index) {
			var obj = {};
			obj.id = $(this).attr('id');
			obj.value = tinyMCE.get($(this).attr('id')).getContent();
			obj.value = obj.value.replace(/&lt;/g,'<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
			textAreas.push(obj);			
		});
		
		 $('.title').each(function(index){
	    	var obj = {};
	    	obj.id = $(this).attr('id');
	    	obj.value = $(this).text();
	    	
	    	if($(this).text() == "Fill in the title") {
	    		preview = false;
	    		$('#header').notify("In order to save you need to change the title of your video poster",   { className:"error", autoHideDelay: 3400});
	    	}
	    	
	    	textAreas.push(obj);
	    });
		
		var mediaArray = [];
		$('.media_item').each(function(index) {
			var obj = {};
		
			obj.id = $(this).attr('id');
			if($(this)[0].children[0].src == undefined){
				if($($($(this)[0].children[1]).children()[0]).children()[0]){
					obj.value = $($($(this)[0].children[1]).children()[0]).children()[0].src
					obj.poster = $($(this)[0].children[1]).children()[0].poster;
				}
			}else{
				obj.value = $(this)[0].children[0].src;
			}
			mediaArray.push(obj);
		});
		
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
		if(preview == true){
			eddie.putLou("", "preview(" + result + ")");
		}
		console.log("preview()");
	});
	
	$('#publish').click(function(){
		var textAreas = [];
		var publish = true;
		$('.text_item').each(function(index) {
			var obj = {};
			obj.id = $(this).attr('id');
			obj.value = tinyMCE.get($(this).attr('id')).getContent();
			obj.value = obj.value.replace(/&lt;/g,'<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
			
			textAreas.push(obj);			
		});
		
		 $('.title').each(function(index){
	    	var obj = {};
	    	obj.id = $(this).attr('id');
	    	obj.value = $(this).text();
	    	
	    	if($(this).text() == "Fill in the title") {
	    		publish = false;
	    		$('#header').notify("In order to save you need to change the title of your video poster",   { className:"error", autoHideDelay: 3400});
	    	}
	    	
	    	textAreas.push(obj);
	    });
		
		var mediaArray = [];
		$('.media_item').each(function(index) {
			var obj = {};
		
			obj.id = $(this).attr('id');
			if($(this)[0].children[0].src == undefined){
				if($($($(this)[0].children[1]).children()[0]).children()[0]){
					obj.value = $($($(this)[0].children[1]).children()[0]).children()[0].src
					obj.poster = $($(this)[0].children[1]).children()[0].poster;
				}
			}else{
				obj.value = $(this)[0].children[0].src;
			}
			mediaArray.push(obj);
		});
		
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
		if(publish == true){
			eddie.putLou("", "proccesspublication(" + result + ")");
			$('#publish').hide();
			$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
		}
	});
	
	$('#edit').click(function(){
		var textAreas = [];
		var publish = true;
		$('.text_item').each(function(index) {
			var obj = {};
			obj.id = $(this).attr('id');
			obj.value = tinyMCE.get($(this).attr('id')).getContent();
			obj.value = obj.value.replace(/&lt;/g,'<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
			
			textAreas.push(obj);			
		});
		
		 $('.title').each(function(index){
	    	var obj = {};
	    	obj.id = $(this).attr('id');
	    	obj.value = $(this).text();
	    	
	    	if($(this).text() == "Fill in the title") {
	    		publish = false;
	    		$('#header').notify("In order to save you need to change the title of your video poster",   { className:"error", autoHideDelay: 3400});
	    	}
	    	
	    	textAreas.push(obj);
	    });
		
		var mediaArray = [];
		$('.media_item').each(function(index) {
			var obj = {};
		
			obj.id = $(this).attr('id');
			if($(this)[0].children[0].src == undefined){
				if($($($(this)[0].children[1]).children()[0]).children()[0]){
					obj.value = $($($(this)[0].children[1]).children()[0]).children()[0].src
					obj.poster = $($(this)[0].children[1]).children()[0].poster;
				}
			}else{
				obj.value = $(this)[0].children[0].src;
			}
			mediaArray.push(obj);
		});
		
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray, mode: "edit"});
		if(publish == true){
			eddie.putLou("", "proccesspublication(" + result + ")");
			$('#publish').hide();
			$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
		}
	});

}
Header.prototype = Object.create(Component.prototype);

Header.prototype.success = function() {
	$('#header').notify("Your Poster has been saved",   { className:"success", autoHideDelay: 3400});
}

Header.prototype.showbuttons = function () {
	$('#preview').show();
	$('#publish').show();	
}

Header.prototype.modeEdit = function () {
	$('#publish').hide();
	$("#edit").show();
}