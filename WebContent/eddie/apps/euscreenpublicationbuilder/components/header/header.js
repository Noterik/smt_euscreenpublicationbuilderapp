var Header = function(options){
	Component.apply(this, arguments);
	$("#edit").hide();

	var preview = true;
	$('#x-icon').click(function(){
		eddie.getComponent('iframesender').sendToParent('close');
	});
	
	$('#color-schemes').click(function() {
		$('#colorschemesContent').show();
		$('#buildContent').show();
	});
	
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

		$('.media_item').each(function(index, element){
			//Let's cache this element, doing $() over and over again kills performance, try to learn this and do it next time, I've already said it a couple of times.
			var $element = $(element);
			var obj = {
				id: $element.attr('id')
			};
			
			//Doing stuff like $($().children()) makes your code completely unreadable. We know we want to get the src/poster of a video, so also reflect this in your code. 
			//When we encounter a simple video
			if($element.find('video')[0]){
				var $video = $element.find('video');
				
				//Why are you calling the src a "value" and the poster a "poster"? Stay consistent. 
				obj['value'] = $video.attr('src');
				obj['poster'] = $video.attr('poster');
				
			//When we find an embed from youtube/vimeo
			}else if($element.find('iframe')[0]){
				obj['value'] = $element.find('iframe').attr('src');
			}
			mediaArray.push(obj);
		});
		
		var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
		if(publish == true){
			eddie.putLou("", "proccesspublication(" + result + ")");
			$('#publish').hide();
			$('#header').notify("Your Poster has been saved", { className:"success", autoHideDelay: 3400});
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

	
}

Header.prototype.modeEdit = function () {
	$('#publish').hide();
	$('#edit').show();
	console.log($('#colorschemesContent'));
	$('#colorschemesContent').hide();
}

//nav menu arrow classs toogle
$( ".nav-arrow-togle" ).click(function() {

	var arr = $( ".nav-arrow-togle" );
	for (var i = 0; i < arr.length; i++) {
		$(arr.get(i)).removeClass("arrow-pinter");  
	}
	
	$(this).addClass("arrow-pinter");
	
	switch($(this).attr("type")) {
    case "layout":
        	eddie.putLou("", "generatelayout()");
        break;
    case "colorShemes":
        	eddie.putLou("", "generatecolorschemes()");
        break;
    case "build" :
    		eddie.putLou("", "generatebuild()");
    	break;
    default:
		break;
	}
	
	return false;
});


