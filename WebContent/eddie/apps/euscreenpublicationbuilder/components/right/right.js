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
		      },
		      stop: function(event, ui) {
			  	 if(event.target.parentElement != $('#bookmarklayout')[0]){
				    var element = $(ui.helper).clone();
				    element = element.removeAttr('style').draggable({ disabled: false }).attr("id", ui.helper.context.attributes[0].nodeValue);
				    					
					if(element.children().length > 0) {
						element.children().remove(".removeVideo");
					}
				   	
				    if ($('#' + self.parentBox).find("#" + ui.helper.context.attributes[0].nodeValue).length == 0) {
				    	$('#' + self.parentBox).append(element);
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