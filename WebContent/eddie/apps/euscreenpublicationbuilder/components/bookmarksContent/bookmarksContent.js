var BookmarksContent = function(options) {
	Component.apply(this, arguments);
	var self = this;
	this.element = $('#bookmarksContent');
	this.tabs = this.element.find('.bookmark-tabs');

	this.bookmarkContent = this.element.find('#bookmarksTab');
	this.collectionContent = this.element.find('#collectionsTab');
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
	
	this.bookmarksModel.on('pages-changed', function(pages){
		self.renderArrows();
	});
	
	this.bookmarksModel.on('page-changed', function(page){
		self.renderArrows();
	});

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
				.find('.list-media-item[data-public="true"]');

		bookmarks.draggable({
			cursor : 'move',
			revert : true,
			revertDuration : 0,
			zIndex : 1000,
			// helper: 'clone',
			start: function(ui, event){
				if(eddie.getComponent('buildContent')){
					eddie.getComponent('buildContent').draggingStarted();
				}
			},
			stop : function(ui, event) {
				$(this).css("position", "relative");
				if(eddie.getComponent('buildContent')){
					eddie.getComponent('buildContent').draggingStopped();
				}
			}
		});
		
	}
	self.renderArrows();
}

BookmarksContent.prototype.renderArrows = function(){
	var self = this;
	if((self.bookmarksModel.get('page') == 0 || self.bookmarksModel.get('page')) && (self.bookmarksModel.get('pages') == 0 || self.bookmarksModel.get('pages'))){
		console.log("we need to do something with the arrows!");
		var nextArrow = self.bookmarkContent.find('.arrow-right');
		var prevArrow = self.bookmarkContent.find('.arrow-left');
		
		nextArrow.off('click').on('click', function(){
			eddie.putLou('', 'getNextBookmarkPage()');
		});
		
		prevArrow.off('click').on('click', function(){
			eddie.putLou('', 'getPrevBookmarkPage()');
		});
		
		if(nextArrow[0] && prevArrow[0]){
			if(self.bookmarksModel.get('pages') == 1){
				nextArrow.hide();
				prevArrow.hide();
			}else if(self.bookmarksModel.get('page') == 0){
				nextArrow.show();
				prevArrow.hide();
			}else if(self.bookmarksModel.get('page') == (self.bookmarksModel.get('pages') - 1)){
				nextArrow.hide();
				prevArrow.show();
			}else{
				nextArrow.show();
				prevArrow.show();
			}
		}
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
		
		var items = self.collectionContent
			.find('.list-media-item[data-public="true"]');
		
		items.draggable({
			cursor : 'move',
			revert : true,
			revertDuration : 0,
			zIndex : 1000,
			// helper: 'clone',
			stop : function(ui, event) {
				$(this).css("position", "relative");
			}
		});
	}
}