var BuildContent = function(options){
	 this.element = jQuery("#buildCont");
	 Component.apply(this, arguments);
	 
	 jQuery("#buildContent").hide();
}
BuildContent.prototype = Object.create(Component.prototype);

BuildContent.prototype.initTinyMce = function(){
	console.log("BuildContent.initTinyMce()");
	 this.element.find('.text_item[data-section-type="text"]').each(function(){
		 tinymce.init({
			 mode: "exact",
			 menubar: false,
			 plugins: 'link',
			 toolbar: "fontselect | fontsizeselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
			 elements: this.id,
			 height: 256
		 });
	 });

	 this.element.find('.text_item[data-section-type="text_big"]').each(function(){
		 tinymce.init({
			 mode: "exact",
			 elements: this.id,
			 menubar: false,
			 plugins: 'link',
			 toolbar: "fontselect fontsizeselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
		 });
	 });

};

BuildContent.prototype.update = function(message){
	 var self = this;
	 var data = JSON.parse(message);
	 
	 if(data.style){
		 var styleElement = $('<link rel="stylesheet" type="text/css" href="' + data.style + '">');
		 $('head').append(styleElement);
		 if(data.html){
			 styleElement.load(function(){
				 
				 self.element.html(data.html);
				 self.initTinyMce();
				 
				 $('.title').each(function(index){
					 $(this).attr('contenteditable','true');
				 });
				 
			 });
		 }
	 }else if(data.html){
		 this.element.html(data.html);
		 
	 }


	var setDropable = setInterval(function(){		
		self.bindToggleEvents(); 
		
		$('.media_item').droppable({
			accept: 'div.drag_bookmark',
			drop: self.handleCardDrop
		});

		if($('.media_item').attr('class') == "media_item ui-droppable"){
			clearInterval(setDropable);
		}

	}, 50);
}

BuildContent.prototype.bindToggleEvents = function() {
	BuildContent.prototype.bindContext();

	$('.additional-icon-3').unbind('click').click(function(event) {
		event.stopPropagation();
		event.preventDefault();
		 $(this).parent().children().each(function() {
			 
			if($(this).attr("class").indexOf("additional-icon-3") !=-1){
				$(this).toggleClass('tgl-rotate');
				$(this).parent().find(".addVideoBox").remove();
				
			}else {
			
				$(this).toggle('slow');
			}				
		});
	 });
}

BuildContent.prototype.edit = function(message){
	var self = this;
	var data = JSON.parse(message);
	eddie.getComponent('readycheck').loaded().then(function(){
		
		setTimeout(function(){
			console.log("WE ARE AT EDIT FUNCTIONALITY");
			$.each(data, function(key, value){

					switch(value.type) {
						case "media_item":
							var self = this;
									if(value.value) {

										$elem = $("#" + value.id);
										$elem.draggable({ disabled: true });
										$elem.html(value.value).droppable("option", "disabled", true);
//										var fullScreenIcon = $('<i class=\"fullscreen glyphicon glyphicon-resize-full\"></i>');
										var removeIcon = $('<i class=\"remove glyphicon glyphicon-remove\"></i>');
//										$elem.append(fullScreenIcon);
										$elem.append(removeIcon);
//										fullScreenIcon.on('click', function(){
//											if (value.value.requestFullscreen) {
//												value.value.requestFullscreen();
//											} else if (value.value.mozRequestFullScreen) {
//												value.value.mozRequestFullScreen();
//											} else if (value.value.webkitRequestFullscreen) {
//												value.value.webkitRequestFullscreen();
//											}
//										});
										removeIcon.on('click', function(){
//											$elem.droppable("option", "disabled", false);
//											$elem.html("<div id=\"youtube-input\" class=\"additional-icon-1 additional-icon\"></div> <div id=\"vmeo-input\" class=\"additional-icon-2 additional-icon\"></div><div class=\"additional-icon-3\"></div>");
//											BuildContent.prototype.bindToggleEvents();
											var baseElement = $($(this).parent()[0]);
											baseElement.droppable( "option", "disabled", false );
											baseElement.draggable({ disabled: true });
											$($(this).parent()[0]).children(0).remove();

											baseElement.html("<div id=\"youtube-input\" class=\"additional-icon-1 additional-icon\"></div> <div id=\"vmeo-input\" class=\"additional-icon-2 additional-icon\"></div><div class=\"additional-icon-3\"></div>");
											BuildContent.prototype.bindToggleEvents();
										});
										
										//$elem.append("<div class=\"removeVideo\" style=\"position:absolute; bottom:0;\">Remove video</div>");
										$elem.attr("aria-disabled", "true");

										$(".removeVideo").click(function(){
											var baseElement = $($(this).parent()[0]);
											baseElement.droppable( "option", "disabled", false );
											baseElement.draggable({ disabled: true });
											$($(this).parent()[0]).children(0).remove();

											baseElement.html("<div id=\"youtube-input\" class=\"additional-icon-1 additional-icon\"></div> <div id=\"vmeo-input\" class=\"additional-icon-2 additional-icon\"></div><div class=\"additional-icon-3\"></div>");
											BuildContent.prototype.bindToggleEvents();
										});

									}else{
										$("#" + value.id).html("<div id=\"youtube-input\" class=\"additional-icon-1 additional-icon\"></div> <div id=\"vmeo-input\" class=\"additional-icon-2 additional-icon\"></div><div class=\"additional-icon-3\"></div>");
										BuildContent.prototype.bindToggleEvents();
									}
							break;
						case "text_item":
								var id = "#" + $("#" + value.id).prev().attr("id");
								tinyMCE.get(value.id).setContent(value.value);
							break;
						case "title":
							 $("#" + value.id).text(value.value);
							break;
					}
				});
				eddie.getComponent('embedlib').transformVideos();
			}, 20);
	});
}

//SetStyle
BuildContent.prototype.setTheme = function(message){
 	var self = this;
 	console.log("BuildContent.setTheme(" + message + ")");
 	var data = JSON.parse(message);

 	if(data.style){
 		$('#buildContent').removeAttr('style');
  		var styleElement = $('<link rel="stylesheet" type="text/css" href="' + data.style + '">');
  		$('head').append(styleElement);
 	}
}

//SET MEDIA ITEM BIND REMOVE BUTTON
BuildContent.prototype.setmediaitem = function (message) {
	var data = JSON.parse(message);
	var self = this;
	
	$(data.container).html(data.video).droppable("option", "disabled", true);
	$(data.container).append("<div class=\"removeVideo\">Remove video</div>");
	$(data.container).attr("aria-disabled", "true");
	$(data.container).draggable({ disabled: false });

	$(".removeVideo").click(function(){
		var baseElement = $($(this).parent()[0]);
		
		baseElement.droppable( "option", "disabled", false );
		
		baseElement.draggable({ disabled: true });

		$($(this).parent()[0]).children(0).remove();
		
		//There is some position relative inline we are before release I'll remove it in js
		baseElement.removeAttr("style");
		baseElement.append("<div id=\"youtube-input\" class=\"additional-icon-1 additional-icon\"></div> <div id=\"vmeo-input\" class=\"additional-icon-2 additional-icon\"></div><div class=\"additional-icon-3\"></div>");
		BuildContent.prototype.bindToggleEvents();
	});
}

//HERE WE CATCH BOOKMARKS DROP
BuildContent.prototype.handleCardDrop = function ( event, ui ) {
	console.log("BuildContent.handleCardDrop(event, event)");
	
	var video = ui.helper.find('video');
	var src = video.attr('src');
	
	console.log("MEDIA ITEM SOURCE: " + src);

	src = src.substring(0, src.lastIndexOf("?"));
	var poster = video.attr('poster');
	
	eddie.getComponent('embedlib').loaded().then(function(){
		EuScreen.getVideo({
			src: src,
			poster: poster,
			controls: true
		}, function(html){
			
			var video = $(html)[0];
			var $target = $(event.target);
			$target.html(video);
			
			var fullScreenIcon = $('<i class=\"fullscreen glyphicon glyphicon-resize-full\"></i>');
			var removeIcon = $('<i class=\"remove glyphicon glyphicon-remove\"></i>');
			$target.append(fullScreenIcon);
			$target.append(removeIcon);
			fullScreenIcon.on('click', function(){
				console.log(video);
				if (video.requestFullscreen) {
					video.requestFullscreen();
				} else if (video.mozRequestFullScreen) {
					video.mozRequestFullScreen();
				} else if (video.webkitRequestFullscreen) {
					video.webkitRequestFullscreen();
				}
			});
			removeIcon.on('click', function(){
				$target.html("<div id=\"youtube-input\" class=\"additional-icon-1 additional-icon\"></div> <div id=\"vmeo-input\" class=\"additional-icon-2 additional-icon\"></div><div class=\"additional-icon-3\"></div>");
				BuildContent.prototype.bindToggleEvents();
			});
		});
	})
};

//BIND SUBMIT MEDIA ITEM
BuildContent.prototype.bindEvent = function() {
	$('.submit_media_id').click(function(v){
		v.stopPropagation();

		var data_type = $(this).attr("data-type");
		var identifier = $($(v).first().get(0).currentTarget.parentElement.firstChild).html()
		var container = $($(v).first().get(0).currentTarget.parentElement.parentElement).attr("id");
		var result = JSON.stringify({dataType: data_type, identifier: identifier, container: container});
		eddie.putLou("", "addexternalidentifire(" + result + ")");
				
	});
}


//BIND CONTEXT
BuildContent.prototype.bindContext = function() {
	var self = this;

 	$('.additional-icon').unbind('click').click(function (e){
 		
		$(".additional_icon").off('click');

		e.stopPropagation();
	 	e.preventDefault();
	  
	 	$icon_type = $(this).attr("id");
	 	
	 	$(this).parent().children().each(function() {
	 		$(this).hide();
	 		$(this).toggle();
	 	});
	 	
	 	if ($(this).parent().find(".addVideoBox").length > 0){ 
	 		$(this).parent().find(".addVideoBox").remove();
	 	}
	 	
	 	if($icon_type != "vmeo-input"){
	 		$(this).parent().append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\"></div><br /> <button class=\"submit_media_id\" data-type=\"YoutubeItem\">Submit Youtube item</button><div>");
	 		BuildContent.prototype.bindEvent();
	 		
	 	}else {
	 		$(this).parent().append("<div class=\"addVideoBox\"><div id=\"vimeo_id\" contentEditable=\"true\"></div><br /> <button class=\"submit_media_id\" data-type=\"VimeoItem\">Submit Vimeo item</button><div>");
	 		BuildContent.prototype.bindEvent();
	 		
	 	}

	});
}
