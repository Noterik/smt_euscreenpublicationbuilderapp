var Layout = function(options){
	 this.element = jQuery("#layout");
	 Component.apply(this, arguments);
}
Layout.prototype = Object.create(Component.prototype);

Layout.prototype.initTinyMce = function(){
	console.log("Layout.initTinyMce()");
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

Layout.prototype.update = function(message){
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
		self.bindContext();
		$('.media_item').droppable( {
			accept: '.drag_bookmark',
			drop: self.handleCardDrop,
		});

		if($('.media_item').attr('class') == 'media_item ui-droppable'){
			clearInterval(setDropable);
		}

	}, 50);
}

Layout.prototype.edit = function(message){
	var self = this;
	var data = JSON.parse(message);
	eddie.getComponent('readycheck').loaded().then(function(){

		setTimeout(function(){
			$.each(data, function(key, value){
					switch(value.type) {
						case "media_item":
							var self = this;
									if(value.value) {
										$elem = $("#" + value.id);
										$elem.draggable({ disabled: true });
										$elem.html(value.value).droppable("option", "disabled", true);
										$elem.append("<div class=\"removeVideo\">Remove video</div>");
										$elem.attr("aria-disabled", "true");

										$(".removeVideo").click(function(){
											var baseElement = $($(this).parent()[0]);
											baseElement.droppable( "option", "disabled", false );
											baseElement.draggable({ disabled: true });
											$($(this).parent()[0]).children(0).remove();

											baseElement.append("<div class=\"plus_icon\"></div>");
											console.log(window.Layout.prototype);
											window.Layout.prototype.bindContext();
										});

									}else{
										$("#" + value.id).html("<div class=\"plus_icon\"></div>");
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
Layout.prototype.setTheme = function(message){
 	var self = this;
 	console.log("Layout.setTheme(" + message + ")");
 	var data = JSON.parse(message);

 	if(data.style){
 		$('#layout').removeAttr('style');
  		var styleElement = $('<link rel="stylesheet" type="text/css" href="' + data.style + '">');
  		$('head').append(styleElement);
 	}
}


Layout.prototype.setmediaitem = function (message) {
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


		baseElement.append("<div class=\"plus_icon\"></div>");
		self.bindContext();
	});

}

Layout.prototype.handleCardDrop = function ( event, ui ) {
	console.log("Layout.handleCardDrop(", event , ", " , ui , ")");
	var video = ui.helper.find('video');
	var src = video.attr('src');
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
				$target.html('<div class="plus_icon"></div>');
				window.Layout.prototype.bindContext();
			});
		});
	})
};

Layout.prototype.bindEvent = function() {
	$('.submit_media_id').click(function(v){
		var data_type = $(this).attr("data-type");
		var identifier = $($(v).first().get(0).currentTarget.parentElement.firstChild).html()
		var container = $($(v).first().get(0).currentTarget.parentElement.parentElement).attr("id");
		var result = JSON.stringify({dataType: data_type, identifier: identifier, container: container});
		eddie.putLou("", "addexternalidentifire(" + result + ")");
		v.stopPropagation();
	});
}

Layout.prototype.bindContext = function() {
	var self = this;
 	$('.plus_icon').unbind('click').click(function (e){
 		console.log(e);
		$(".plus_icon").off('click');

	  e.stopPropagation();
	  e.preventDefault();
	  if($('#context').length == 0){
	  	$(this).after("<div id=\"context\"><ul><li id=\"youtube\">Youtube Item</li><li id=\"vimeo\">Vimeo Item</li><li id=\"close_menu\">Close menu</li></ul></div>");
	  }

	   if($(this)) {
	    $('#context').show();
	   }
		var parentOffset = $(this).parent().offset();

	   $('#context').css({'left':e.pageX - parentOffset.left + 10, 'top':e.pageY - parentOffset.top});

	   $('#youtube').click(function(v){
	   	   	v.stopPropagation();
	   	   	v.preventDefault();
	   	   	$(".plus_icon").off('click');

	   		$('#context').remove();
			$(e.currentTarget).hide();
	   		$(e.currentTarget).parent().append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"YoutubeItem\">Submit Youtube item</button><div>");
	   		self.bindEvent();

	   		self.bindContext();
	   });

	   $('#vimeo').click(function(v){
	   	   	v.stopPropagation();
	   		v.preventDefault();
	   		$(".plus_icon").off('click');
	    	$('#context').remove();
			$(e.currentTarget).hide();
	   		$(e.currentTarget).parent().append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"YoutubeItem\">Submit Youtube item</button><div>");
	   		self.bindEvent();
	   		self.bindContext();
	   });

	   $('#close_menu').click(function(v){
	   		$('#context').remove();
	   		self.bindContext();
	   });

		$(".plus_icon").click(function (e){
			$(".plus_icon").off('click');

		});
	});
}
