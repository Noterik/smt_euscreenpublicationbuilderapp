var BookmarksContent = function(options){
	Component.apply(this, arguments);
	var self = this;
	this.element = jQuery("#bookmarksContent");
	this.bookmarkContent = $('#bookmarklayout');
	this.collectionContent = $('#colectionslayout');
	this.collectionHeaders = $('#colectionslayout_headers');
	this.collectionItems = $('#colectionslayout_items');
	
	this.bookmarksTemplate = _.template(this.element.find('#bookmarks_layout_template').text());
	this.colectionsLayoutHeadersTemplate = _.template(this.element.find('#colectionslayout_headers_template').text());
	this.colectionsLayoutBookmarksTemplate = _.template(this.element.find('#colectionslayout_bookmarks_template').text());
	
	setInterval(function(){
		var x = 0;
		var y = 0;
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true,
		      revertDuration: 0,
		      zIndex: 1000,
		      drag: function(event,ui) { 
		    	  

		      },
		      start: function(ui, event) {

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

BookmarksContent.prototype.displayBookmarks = function(data) {
	var self = this;
	var html = jQuery(this.bookmarksTemplate({bookmarks: JSON.parse(data)}));
	this.bookmarkContent.append(html);
}

BookmarksContent.prototype.displayCollections = function(data) {
	var self = this;
	var dt = JSON.parse(data);

	$.each(dt, function(){
		var header_html = jQuery(self.colectionsLayoutHeadersTemplate({data: $(this)[0]}));
		self.collectionHeaders.append(header_html);
		
		var collection_bookmakrs_html = jQuery(self.colectionsLayoutBookmarksTemplate({data: $(this)[0]}));
		self.collectionItems.append(collection_bookmakrs_html);
	});

}


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