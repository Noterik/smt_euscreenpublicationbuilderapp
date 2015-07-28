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
	   		v.stopPropagation();
	   });
	   
	   $('#vimeo').click(function(v){
	    	$('#context').remove();
			$($($($($(e.currentTarget)[0])[0].parentElement)[0].children)[0]).hide();
	   		$($($(e.currentTarget)[0])[0].parentElement).append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"VimeoItem\">Submit Vimeo item</button><div>");
	   		self.bindEvent();
	   		v.stopPropagation();
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


Layout.prototype.setmediaitem = function (message) {
	console.log("Layout.mediaurl(" + message + ")");
	var data = JSON.parse(message);
	console.log(data.container);
	console.log(data.video);
	$(container).append(data.video); 
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

		var data_type = $(this).attr("data-type");
<<<<<<< HEAD
		var identifier = $($($($(v)[0].currentTarget).parent()[0])[0].firstChild).html();
		var container = $($($($(v)[0].currentTarget).parent()[0])[0].parentElement).attr("id");
		console.log('asdasdasdasda');
		var result = JSON.stringify({dataType: data_type, identifier: identifier, container: container});
		console.log(result);
		eddie.putLou("", "addexternalidentifire(" + result + ")");
=======
		var identifire = $($($($(v)[0].currentTarget).parent()[0])[0].firstChild).html();
		var result = JSON.stringify({dataType: data_type, identifire: identifire});
		eddie.putLou("", "addExternalIdentifire(" + result + ")");
>>>>>>> 4f999341f5935e419094690b4979d57d88d9a412
		v.stopPropagation();
	});
}	