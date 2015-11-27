var BookmarksContent = function(options){
	Component.apply(this, arguments);
	var self = this;
	setInterval(function(){
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true,
		      revertDuration: 0,
		      //helper: 'clone',
		      stop: function(ui, event){
		      }
		    });
		}());
	}, 50);
	
}

BookmarksContent.prototype = Object.create(Component.prototype);

BookmarksContent.prototype.closeAll = function(cnt_header) {
	$(".collection-header").toggle();
	$("#collections").bind( "click", function() {
		 $(".collection-header").toggle();
	});
	for(var i = 0; i < cnt_header; i++) {
		$("#toggle_" + i).toggle("fast");

		$('#right_header_' + i).bind( "click", function() {
			var toggle_id = $(this).attr('id').replace('right_header', 'toggle');
			console.log('Toggle: ' + toggle_id );
			$("#" + toggle_id).toggle("fast");
		});		
	}
}