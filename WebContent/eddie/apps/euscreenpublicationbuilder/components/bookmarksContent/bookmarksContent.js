var BookmarksContent = function(options) {
	Component.apply(this, arguments);
	var self = this;
	this.element = $('#bookmarksContent');
	this.tabs = this.element.find('.bookmark-tabs');

	this.bookmarkContent = this.element.find('#bookmarksTab');
	this.collectionContent = this.element.find('#collectionsTab');
	/*
	 * this.collectionContent = $('#colectionslayout'); this.collectionHeaders =
	 * $('#colectionslayout_headers'); this.collectionItems =
	 * $('#colectionslayout_items');
	 */

	this.bookmarksTemplate = _.template(this.element.find(
			'#bookmarks_layout_template').text());
	this.collectionsTemplate = _.template(this.element.find(
			'#bookmarksContent_collections_template').text());

	// This is the view, the model is actually defined in bookmarks.js, the view
	// listens to the model and updates the listing.
	this.bookmarksModel = eddie.getComponent('bookmarks');
	this.collectionsModel = eddie.getComponent('collections');
	this.bookmarksModel.on('bookmarks-changed', function() {
		self.renderBookmarks();
	});
	self.renderBookmarks();

	this.collectionsModel.on('collections-changed', function() {
		self.renderCollections();
	});
	self.renderCollections();

	this.tabs.find('.nav-tabs a').click(function(e) {
		e.preventDefault();
		$(this).tab('show');
	});

}

BookmarksContent.prototype = Object.create(Component.prototype);

BookmarksContent.prototype.renderBookmarks = function() {
	var self = this;
	if (self.bookmarksModel.get('bookmarks')) {
		var html = self.bookmarksTemplate({
			bookmarks : self.bookmarksModel.get('bookmarks')
		});
		console.log(self.bookmarkContent[0]);
		self.bookmarkContent.html(html);
		var bookmarks = self.bookmarkContent
				.find('.drag_bookmark:not([data-public="false"])');

		bookmarks.draggable({
			stack : '#bookmarklayout',
			cursor : 'move',
			revert : true,
			revertDuration : 0,
			zIndex : 1000,
			drag : function(event, ui) {

			},
			start : function(ui, event) {

			},
			// helper: 'clone',
			stop : function(ui, event) {
				$(this).css("position", "relative");

			}
		});
	}

}

BookmarksContent.prototype.renderCollections = function() {
	var self = this;
	console.log("renderCollections(", this.collectionsModel.get('collections'),
			")");
	var self = this;
	var collections = this.collectionsModel.get('collections');
	if(collections){
		var html = this.collectionsTemplate({collections: collections});
		console.log("HTML: " + html);
		this.collectionContent.html(html);
	}
	/*
	 * if(collections !== null){ $.each(collections, function() { var headerHTML =
	 * jQuery(self.colectionsLayoutHeadersTemplate({ data : $(this)[0] }));
	 * self.collectionHeaders.append(headerHTML);
	 * 
	 * var collectionBookmarksHTML = jQuery(self
	 * .colectionsLayoutBookmarksTemplate({ data : $(this)[0] }));
	 * //self.collectionItems.append(collectionBookmarksHTML); }); }
	 */

}

BookmarksContent.prototype.closeAll = function(cnt_header) {
	// $(".collection-header").toggle();
	$('#colectionslayout_headers').each(function() {
		$(this).toggle("fast");
	});
	$('#' + 'colectionslayout_headers').hide();
	$('#' + 'colectionslayout_items').hide();
	$(".arrow-wrap").hide();

	$("#collections")
			.bind(
					"click",
					function() {
						$("#toggle_0").hide();
						$('#' + 'colectionslayout_headers').show();
						$('#' + 'colectionslayout_items').show();
						$("#right_header_0").removeClass('right-header_active');
						$("#collections").addClass('right-header_active');

						$(".arrow-wrap").show();

						$cnt = 0;
						$('#' + 'colectionslayout_headers').children().each(
								function() {
									if ($cnt == 0) {
										$(this).show();
									} else {
										$(this).hide();
									}
									$cnt++;
								});

						$cnt = 0;
						$('#' + 'colectionslayout_items').children().each(
								function() {
									if ($cnt == 0) {
										$(this).show();
									} else {
										$(this).hide();
									}
									$cnt++;
								});

						$('.arrow-left')
								.click(
										function() {
											$header_position = 0;
											$('#' + 'colectionslayout_headers')
													.children()
													.each(
															function() {
																if ($(this)
																		.is(
																				":visible")) {
																	return false;
																}
																$header_position++;
															});

											$current_collection_header = $(
													'#'
															+ 'colectionslayout_headers')
													.children().get(
															$header_position);
											if ($header_position > 0) {
												$old_header_element = $(
														'#'
																+ 'colectionslayout_headers')
														.children()
														.get($header_position);
												$old_header_element = $($old_header_element);

												$old_header_element.hide();
												$old_collection = $old_header_element
														.attr("childs_type");

												console
														.log("old collection child type attribute");
												console.log($old_collection);
												// if($old_collection !=
												// undefined){

												$(
														'#'
																+ 'colectionslayout_items')
														.children()
														.each(
																function() {
																	if ($(this)
																			.attr(
																					'collection') == $old_collection) {
																		$(this)
																				.hide();
																		console
																				.log("old collection items");
																		console
																				.log($(this));
																	}
																});

												$header_position--;
												$new_header_element = $(
														'#'
																+ 'colectionslayout_headers')
														.children()
														.get($header_position)
												$new_header_element = $($new_header_element);
												$new_header_element.show();
												$new_collection = $new_header_element
														.attr("childs_type");

												console
														.log("new collection child type attribute");
												console.log($new_collection);

												$(
														'#'
																+ 'colectionslayout_items')
														.children()
														.each(
																function() {
																	if ($(this)
																			.attr(
																					'collection') == $new_collection) {
																		$(this)
																				.show();
																		console
																				.log("new collection items");
																		console
																				.log($(this));
																	}
																});
												// }
											}
										});

						$('.arrow-right')
								.click(
										function() {
											$header_position = 0;
											$('#' + 'colectionslayout_headers')
													.children()
													.each(
															function() {
																if ($(this)
																		.is(
																				":visible")) {
																	return false;
																}
																$header_position++;
															});

											$current_collection_header = $(
													'#'
															+ 'colectionslayout_headers')
													.children().get(
															$header_position);
											if ($header_position + 1 < $(
													'#'
															+ 'colectionslayout_items')
													.children().size()) {
												$old_header_element = $(
														'#'
																+ 'colectionslayout_headers')
														.children()
														.get($header_position);
												$old_header_element = $($old_header_element);

												$old_header_element.hide();
												$old_collection = $old_header_element
														.attr("childs_type");
												console.log($old_collection);
												$(
														'#'
																+ 'colectionslayout_items')
														.children()
														.each(
																function() {
																	if ($(this)
																			.attr(
																					'collection') == $old_collection) {
																		$(this)
																				.hide();

																	}
																});

												$header_position++;
												$new_header_element = $(
														'#'
																+ 'colectionslayout_headers')
														.children()
														.get($header_position)
												$new_header_element = $($new_header_element);
												$new_header_element.show();

												$new_collection = $new_header_element
														.attr("childs_type");

												console.log("new_collection");
												console.log($new_collection);

												$(
														'#'
																+ 'colectionslayout_items')
														.children()
														.each(
																function() {
																	if ($(this)
																			.attr(
																					'collection') == $new_collection) {
																		$(this)
																				.show();
																		console
																				.log("new collection items");
																		console
																				.log($(this));
																	}
																});
											}
										});

					});

	$("#toggle_0").show();

	$('#right_header_0').bind("click", function() {
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