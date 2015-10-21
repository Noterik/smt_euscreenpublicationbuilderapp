var Right = function(options){
	var self = this;
	Component.apply(this, arguments);	
	setInterval(function(){
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true,
		      //helper: 'clone',
		      drag: function( event, ui ) {
		      	self.parentBox = ui.helper.parent().attr("id");
		      	self.el = $(ui.helper).clone();
		      	self.sibling = $(ui.helper).context.previousSibling;
		      },
		      stop: function(event, ui) {
			  	 if(event.target.parentElement != $('#bookmarklayout')[0]){
				    var element = $(ui.helper).clone();
				    console.log(ui.helper.context.attributes['id'].nodeValue);
				    element = element.removeAttr('style').draggable({ disabled: false }).attr("id", ui.helper.context.attributes['id'].nodeValue);
				    					
					if(element.children().length > 0) {
						element.children().first().removeClass('videoAfterDrop');
						element.children().remove(".removeVideo").addClass('layout_image').removeAttr('style');
					}
				   	
				   	console.log(self.el);
				    if ($('#' + self.parentBox).find("#" + ui.helper.context.attributes['id'].nodeValue).length == 0) {
				    	var pos = element.attr('id').split('_');
				    	console.log(pos[1]);
				    	if(self.sibling != null) {
				    		console.log("sibling");
				    		console.log($(self.sibling));
				    		$(self.sibling).after(element);
				    	}else {
				    		$('#' + self.parentBox).prepend(element);
				    	}
				    	//$('#' + self.parentBox).append(element);
				    					    	console.log($('#' + self.parentBox).children());
				    	
					}					
			  	 }
			  	 
			  }
		    });
		}());
	}, 50);
}

Right.prototype = Object.create(Component.prototype);

Right.prototype.closeAll = function(cnt_header) {
	for(var i = 0; i < cnt_header; i++) {
		$("#toggle_" + i).toggle("fast");

		$('#right_header_' + i).bind( "click", function() {
			console.log($(this));
			var toggle_id = $(this).attr('id').replace('right_header', 'toggle');
			console.log('Toggle: ' + toggle_id );
			$("#" + toggle_id).toggle("fast");
		});		
	}
}

Right.prototype.accordion = function() {
    $(".accordion").accordion();
    
	$("#left-header-layout").css('background-color', 'lightgray');
	$("#layouts").toggle( "fast" );
	
	$("#left-header-theme").css('background-color', '#00AEEF');
	$("#color_schemes").toggle("fast");  	
}