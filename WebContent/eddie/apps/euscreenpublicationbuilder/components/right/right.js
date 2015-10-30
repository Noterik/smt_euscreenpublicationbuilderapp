var Right = function(options){
	var self = this;
	Component.apply(this, arguments);	
	setInterval(function(){
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true,
		      revertDuration: 0,
		      //helper: 'clone',
		      stop: function(ui, event){
		    	  console.log("UI", ui);
		    	  console.log("EVENT", event);
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