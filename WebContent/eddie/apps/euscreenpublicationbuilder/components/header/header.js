var Header = function(options){
	Component.apply(this, arguments);
	$("#edit").hide();

	var preview = true;
	$('#x-icon').click(function(){
		eddie.getComponent('iframesender').sendToParent('close');
	});
	
//	$('#color-schemes').click(function() {
//		$('#colorschemesContent').show();
//		$('#buildContent').hide();
//	});
	
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
	
	$published = false;
	
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
			var $element = $(element);
			var obj = {
				id: $element.attr('id')
			};
			
			if($element.find('video')[0]){
				var $video = $element.find('video');
				
				obj['value'] = $video.attr('src');
				obj['poster'] = $video.attr('poster');
				
			//When we find an embed from youtube/vimeo
			}else if($element.find('iframe')[0]){
				obj['value'] = $element.find('iframe').attr('src');
			}
			mediaArray.push(obj);
		});
		
		if(publish == true){
			if($published == false){
				var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray});
				eddie.putLou("", "proccessPublication(" + result + ")");
				$published = true;
				console.log('publish -> publish');
			}else {
				var result = JSON.stringify({textItem: textAreas, mediaItem: mediaArray, mode: "edit"});
				eddie.putLou("", "proccessPublication(" + result + ")");
				console.log('publish -> edit');
			}
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
			eddie.putLou("", "proccessPublication(" + result + ")");
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
	$colorSchemes = $("#color-schemes");
	$colorSchemes.css("color", "lightgray");
	$colorSchemes.removeClass("arrow-pinter");
	$colorSchemes.unbind('click');
	
	$layout = $("#layout-nav");
	$layout.css("color", "lightgray");
	$layout.unbind("click");
	$layout.removeClass("arrow-pinter");
	
	$("#build").addClass("arrow-pinter");	
	
}

Header.prototype.modeEdit = function () {
	$('#publish').hide();
	$('#edit').show();
	$colorSchemes = $("#color-schemes");
	$colorSchemes.css("color", "lightgray");
	$colorSchemes.removeClass("arrow-pinter");
	$colorSchemes.unbind('click');
	
	$layout = $("#layout-nav");
	$layout.css("color", "lightgray");
	$layout.unbind("click");
	$layout.removeClass("arrow-pinter");
	
	$("#build").addClass("arrow-pinter");	
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
});


