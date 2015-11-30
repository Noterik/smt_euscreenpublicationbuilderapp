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
		 $("#toggle_0").toggle("fast");
		 console.log($("#right_header_0"));
	});
	
	var cnt_header_temp = 0;
	for(var i = 1; i < cnt_header; i++) {
		$("#toggle_" + i).toggle("fast");
		if(cnt_header_temp == 0){
			$('#right_header_0').bind( "click", function() {
				var toggle_id = $(this).attr('id').replace('right_header', 'toggle');
				
				$("#" + toggle_id).toggle("fast");
				for(var i = 1; i < cnt_header; i++) {
					$("#right_header_" + i).toggle("fast");
					var next_elem_id = $("#right_header_" + i).next().attr('id');
					$('#'+next_elem_id).toggle("fast");
					console.log(next_elem_id);
				}
			});
		}else{
					
			$('#right_header_' + cnt_header_temp).bind( "click", function() {
				var toggle_id = $(this).attr('id').replace('right_header', 'toggle');
	
				$("#" + toggle_id).toggle("fast");
			});
		}
		cnt_header_temp++;
	}
	

}