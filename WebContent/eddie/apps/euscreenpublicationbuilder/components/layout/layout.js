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
	    $('.text').each(function(index) {
	       tinymce.init({selector: '#' + $(this).attr('id')});
	    });
	   });
	  }
	 }else if(data.html){
	  this.element.html(data.html);
	 }
	 
	 setTimeout(function(){
	  $('.plus_icon').click(function (e){
	   console.log($('#context'));
	   if($('#context').length == 0){
	    $('#layout').append("<div id=\"context\"><ul><li id=\"youtube\">Youtube Item</li><li id=\"vimeo\">Vimeo Item</li><li id=\"close_menu\">Close menu</li></ul></div>");
	   }
	   if($('.plus_icon')) {   
	    $('#context').show();
	   }
	   
	   $($('#context')).css({'left':e.pageX, 'top':e.pageY});
	  
	   $('#youtube').click(function(){
	    console.log("YOUTUBE()");
	   });
	   
	   $('#vimeo').click(function(){
	    console.log("VIMEO()");
	   });
	   
	   $('#close_menu').click(function(){
	    $('#context').hide();
	   });
	  });
	  
	  $('.media_item').droppable( {
	       accept: '.drag_bookmark',
	       drop: self.handleCardDrop,
	     });

	 }, 100);
	}

	Layout.prototype.handleCardDrop = function ( event, ui ) {
		console.log(ui);
		console.log(event);
		console.log(this);
		console.log(self);
	    ui.draggable.draggable('disable');
	    $(this).droppable('disable');
	    ui.draggable.position( { of: $(this), my: 'left top', at: 'left top' } );
	    ui.draggable.draggable('option', 'revert', false);
	}