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
	this.bookmarksModel.on('bookmarks-changed', function(bookmarks) {
		self.renderBookmarks();
	});
	self.renderBookmarks();

	this.collectionsModel.on('collections-changed', function() {
		self.renderCollections();
	});
	self.renderCollections();

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