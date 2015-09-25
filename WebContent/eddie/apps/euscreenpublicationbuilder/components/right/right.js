var Right = function(options){
	Component.apply(this, arguments);	
	setInterval(function(){
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true,
		      helper: 'clone',
			  stop: function(event, ui) {
			  	 if(event.target.parentElement != $('#bookmarklayout')[0]){
				     var element = $(ui.helper).clone().removeAttr('style').draggable({ disabled: false });
				     $('#bookmarklayout').append(element);
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