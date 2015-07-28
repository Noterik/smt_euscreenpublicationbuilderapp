var Layout = function(options){
	 this.element = jQuery("#layout");
	 Component.apply(this, arguments); 
	}
	Layout.prototype = Object.create(Component.prototype);

	Layout.prototype.update = function(message){
	 var self = this;
	 console.log("Layout.update(" + message + ")");
	 var data = JSON.parse(message);
	 
	 if(data.style){
	  var styleElement = $('<link rel="stylesheet" type="text/css" href="' + data.style + '">');
	  $('head').append(styleElement);
	  if(data.html){
	   styleElement.load(function(){
	    self.element.html(data.html);
	    $('.text_item').each(function(index) {
	       tinymce.init({selector: '#' + $(this).attr('id')});
	    });
	    
	    $('.title').each(function(index){
	    	$(this).attr('contenteditable','true');
	    });
	   });
	  }
	 }else if(data.html){
	  this.element.html(data.html);
	 }
	 
	 setTimeout(function(){
	  $('.plus_icon').click(function (e){
	  if($('#context').length == 0){
	    $($($(this))[0]).after("<div id=\"context\"><ul><li id=\"youtube\">Youtube Item</li><li id=\"vimeo\">Vimeo Item</li><li id=\"close_menu\">Close menu</li></ul></div>");
	   }
	   console.log($('#context'));
	   
	   if($($($(this))[0])) {   
	    $('#context').show();
	   }
		var parentOffset = $(this).parent().offset(); 
	   
	   $('#context').css({'left':e.pageX - parentOffset.left + 10, 'top':e.pageY - parentOffset.top});
	  
	   $('#youtube').click(function(v){
	   		$('#context').remove();
			$($($($($(e.currentTarget)[0])[0].parentElement)[0].children)[0]).hide();
			$($($(e.currentTarget)[0])[0].parentElement).append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"YoutubeItem\">Submit Youtube item</button><div>");
	   		self.bindEvent();
	   });
	   
	   $('#vimeo').click(function(v){
	    	$('#context').remove();
			$($($($($(e.currentTarget)[0])[0].parentElement)[0].children)[0]).hide();
	   		$($($(e.currentTarget)[0])[0].parentElement).append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"VimeoItem\">Submit Vimeo item</button><div>");
	   		self.bindEvent();
	   });
	   
	   $('#close_menu').click(function(v){
	   		$('#context').remove();
	   });
	   e.stopPropagation();
	  });
		

	$('.media_item').droppable( {
	       accept: '.drag_bookmark',
	       drop: self.handleCardDrop,
	   });
	 }, 100);
}

Layout.prototype.handleCardDrop = function ( event, ui ) {
    ui.draggable.draggable('disable');
    $(this).droppable('disable');
    $(this).append(ui.draggable[0]);
    
    ui.draggable.position( { of: $(this), my: 'left top', at: 'left top' } );
    ui.draggable.draggable('option', 'revert', false);
}

Layout.prototype.bindEvent = function() {
	$('.submit_media_id').click(function(v){
	console.log($(this).attr("data-type"));
	console.log($($($($(v)[0].currentTarget).parent()[0])[0].firstChild).html());
	//	console.log($($(this)[0]).val("data-type"));
	//	console.log($($(this)[0]).parent());
	});
}	