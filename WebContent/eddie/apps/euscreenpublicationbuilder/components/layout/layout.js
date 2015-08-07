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
		self.bindContext();
		$('.media_item').droppable( {
			accept: '.drag_bookmark',
			drop: self.handleCardDrop,
		});
	}, 100);
}

//SetStyle
Layout.prototype.setTheme = function(message){
 	var self = this;
 	console.log("Layout.setTheme(" + message + ")");
 	var data = JSON.parse(message);
 
 	if(data.style){
  		var styleElement = $('<link rel="stylesheet" type="text/css" href="' + data.style + '">');
  		$('head').append(styleElement);
 	}
}


Layout.prototype.setmediaitem = function (message) {
	var data = JSON.parse(message);
	var self = this;
	$(data.container).html(data.video).droppable("option", "disabled", true);
	$(data.container).append("<div class=\"removeVideo\">RemoveVideo</div>");
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
    ui.draggable.draggable('disable');
    $(this).droppable('disable');
    $(this).append(ui.draggable[0]);
    $(ui.draggable[0]).addClass("clickable");
    $(ui.draggable[0]).attr("bookmark", "true");
    var currentBoxHeight = $($(this)[0]).height();
    
    $($(ui.draggable[0])[0].childNodes[0]).removeClass("layout_image").addClass("videoAfterDrop");
    $($(ui.draggable[0])[0].childNodes[0]).height(currentBoxHeight);
    ui.draggable.position( { of: $(this), my: 'left top', at: 'left top' } );
    ui.draggable.draggable('option', 'revert', false);
    
    
    $(".clickable").click(function() {
		var parent = $(this).parent().droppable( "option", "disabled", false );  	
		$($($($(this)[0])[0].childNodes[0])).parent().remove();
		$($($($(this)[0])[0].childNodes[0])).removeAttr('style').removeClass("videoAfterDrop").addClass("layout_image").remove();
		//var elem = $($(this)[0]).removeAttr('style').attr('style', 'position: relative').draggable('enable');
		//elem.appendTo('#bookmarklayout');
	});
};

Layout.prototype.bindEvent = function() {
	$('.submit_media_id').click(function(v){
		var data_type = $(this).attr("data-type");
		var identifier = $($($($(v)[0].currentTarget).parent()[0])[0].firstChild).html();
		var container = $($($($(v)[0].currentTarget).parent()[0])[0].parentElement).attr("id");
		var result = JSON.stringify({dataType: data_type, identifier: identifier, container: container});
				
		eddie.putLou("", "addexternalidentifire(" + result + ")");
		v.stopPropagation();
	});
}

Layout.prototype.bindContext = function() {
	var self = this;
 	$('.plus_icon').click(function (e){
 	  $(".plus_icon").off('click');
	  e.stopPropagation();
	  e.preventDefault();
	  if($('#context').length == 0){
	    $($($(this))[0]).after("<div id=\"context\"><ul><li id=\"youtube\">Youtube Item</li><li id=\"vimeo\">Vimeo Item</li><li id=\"close_menu\">Close menu</li></ul></div>");
	   }
	   
	   if($($($(this))[0])) {   
	    $('#context').show();
	   }
		var parentOffset = $(this).parent().offset(); 
	   
	   $('#context').css({'left':e.pageX - parentOffset.left + 10, 'top':e.pageY - parentOffset.top});
	  
	   $('#youtube').click(function(v){
	   	   	v.stopPropagation();
	   	   	v.preventDefault();
	   	   	$(".plus_icon").off('click');
	   	   	
	   		$('#context').remove();
			$($($($($(e.currentTarget)[0])[0].parentElement)[0].children)[0]).hide();
			$($($(e.currentTarget)[0])[0].parentElement).append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"YoutubeItem\">Submit Youtube item</button><div>");
	   		self.bindEvent();
	   		self.bindContext();
	   });
	   
	   $('#vimeo').click(function(v){
	   	   	v.stopPropagation();
	   		v.preventDefault();
	   		$(".plus_icon").off('click');
	    	
	    	$('#context').remove();
			$($($($($(e.currentTarget)[0])[0].parentElement)[0].children)[0]).hide();
	   		$($($(e.currentTarget)[0])[0].parentElement).append("<div class=\"addVideoBox\"><div id=\"youtube_id\" contentEditable=\"true\" style=\"border: 1px solid black\"></div><br /> <button class=\"submit_media_id\" data-type=\"VimeoItem\">Submit Vimeo item</button><div>");
	   		self.bindEvent();
	   		self.bindContext();
	   });
	   
	   $('#close_menu').click(function(v){
	   		$('#context').remove();
	   		self.bindContext();
	   });

	$(".plus_icon").off('click');
	});
}