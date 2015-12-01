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
		      zIndex: 10000,
		      start: function(ui, event) {
		    	  $(this).css("position", "absolute");

		      },
		      //helper: 'clone',
		      stop: function(ui, event){
		    	  $(this).css("position", "relative");

		      }
		    });
		}());
	}, 50);

}

BookmarksContent.prototype = Object.create(Component.prototype);

BookmarksContent.prototype.closeAll = function(cnt_header) {
	//$(".collection-header").toggle();
	 $('#colectionslayout_headers').each(function() {
			$(this).toggle("fast");
	 });
	 $('#' + 'colectionslayout_headers').hide();
	 $('#' + 'colectionslayout_items').hide();
	 $(".arrow-wrap").hide();

	$("#collections").bind( "click", function() {
		 $("#toggle_0").hide();	
		 $('#' + 'colectionslayout_headers').show();
		 $('#' + 'colectionslayout_items').show();
		 $("#right_header_0").removeClass('right-header_active');
		 $("#collections").addClass('right-header_active');
			
		 $(".arrow-wrap").show();
 
		 $cnt = 0;
		 $('#' + 'colectionslayout_headers').children().each(function() {
			if($cnt == 0){
				$(this).show();	
			}else {
				$(this).hide();
			}
			$cnt++;
		 });
		 
		 $cnt = 0;
		 $('#' + 'colectionslayout_items').children().each(function() {
			if($cnt == 0){
				$(this).show();	
			}else {
				$(this).hide();
			}
			$cnt++;
		 });
		 
		$('.arrow-left').click(function() {
			 $header_position = 0;
			 $('#' + 'colectionslayout_headers').children().each(function() {
				if($(this).is(":visible")){
					return false;
				}
				$header_position++;
			 });
			 
			 $current_collection_header = $('#' + 'colectionslayout_headers').children().get($header_position);
			 if($header_position > 0){
				 $old_header_element = $('#' + 'colectionslayout_headers').children().get($header_position);
				 $old_header_element = $($old_header_element);
				 
				 $old_header_element.hide();
				 $old_collection = $old_header_element.attr("childs_type");
				 
				 console.log("old collection child type attribute");
				 console.log($old_collection);
//				 if($old_collection != undefined){
					 
					 $('#' + 'colectionslayout_items').children().each(function() {
						 if($(this).attr('collection') == $old_collection){
							 $(this).hide();
							 console.log("old collection items");
							 console.log($(this));
						 }
					 });
					 
					 $header_position--;
					 $new_header_element = $('#' + 'colectionslayout_headers').children().get($header_position)
					 $new_header_element = $($new_header_element);
					 $new_header_element.show();
					 $new_collection = $new_header_element.attr("childs_type");
					 
					 console.log("new collection child type attribute");
					 console.log($new_collection);
					 
					 $('#' + 'colectionslayout_items').children().each(function() {
						 if($(this).attr('collection') == $new_collection){
							 $(this).show();
							 console.log("new collection items");
							 console.log($(this));
						 }
					 });
//				 }
			 }
		});
		 
		$('.arrow-right').click(function() {
			$header_position = 0;
			 $('#' + 'colectionslayout_headers').children().each(function() {
				if($(this).is(":visible")){
					return false;
				}
				$header_position++;
			 });
			 
			 $current_collection_header = $('#' + 'colectionslayout_headers').children().get($header_position);
			 if($header_position + 1 < $('#' + 'colectionslayout_items').children().size()){
				 $old_header_element = $('#' + 'colectionslayout_headers').children().get($header_position);
				 $old_header_element = $($old_header_element);

				 $old_header_element.hide();
				 $old_collection = $old_header_element.attr("childs_type");
				 console.log($old_collection);
					 $('#' + 'colectionslayout_items').children().each(function() {
						 if($(this).attr('collection') == $old_collection){
							 $(this).hide();
	
						 }
					 });
					 
					 $header_position++;
					 $new_header_element = $('#' + 'colectionslayout_headers').children().get($header_position)
					 $new_header_element = $($new_header_element);
					 $new_header_element.show();
					 
					 $new_collection = $new_header_element.attr("childs_type");
					 
					 console.log("new_collection");
					 console.log($new_collection);

					 
					 $('#' + 'colectionslayout_items').children().each(function() {
						 if($(this).attr('collection') == $new_collection){
							 $(this).show();
							 console.log("new collection items");
							 console.log($(this));
						 }
					 });
			 }
		 });

	});
	

	$("#toggle_0").show();

	$('#right_header_0').bind( "click", function() {
		var toggle_id = $(this).attr('id').replace('right_header', 'toggle');
		
		$("#" + toggle_id).show();
		$("#right_header_0").addClass('right-header_active');
		$("#collections").removeClass('right-header_active');
		$('#' + 'colectionslayout_headers').each(function() {
			$(this).hide();
		});

		$(".arrow-wrap").hide();

		$('#' + 'colectionslayout_items').each(function() {
			$(this).hide();
		});


	});

}