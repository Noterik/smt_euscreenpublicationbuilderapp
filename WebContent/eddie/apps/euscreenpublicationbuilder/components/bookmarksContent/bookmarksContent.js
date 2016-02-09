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
	
	this.collectionsVideosTemplate = _.template(this.element.find('#bookmarksContent_collections_videos_template').text());
	
	this.parent = eddie.getComponent('iframesender');
	
	this.collectionsRendered = false;
		
	// This is the view, the model is actually defined in bookmarks.js, the view
	// listens to the model and updates the listing.
	this.bookmarksModel = eddie.getComponent('bookmarks');
	this.collectionsModel = eddie.getComponent('collections');
	
	this.bookmarksModel.on('bookmarks-changed', function(bookmarks) {
		self.renderBookmarks();
	});
	
	this.bookmarksModel.on('pages-changed', function(){
		self.renderBookmarks();
	});
	
	this.bookmarksModel.on('page-changed', function(){
		self.renderBookmarks();
	});
	
	self.renderBookmarks();

	this.collectionsModel.on('collections-changed', function(collections) {
		self.renderCollections();
	});
	self.renderCollections();
}

BookmarksContent.prototype = Object.create(Component.prototype);

BookmarksContent.prototype.renderBookmarks = function() {
	var self = this;
	if (self.bookmarksModel.get('bookmarks') && self.bookmarksModel.get('pages') !== null && self.bookmarksModel.get('page') !== null) {
		var html = self.bookmarksTemplate({
			bookmarks : self.bookmarksModel.get('bookmarks'),
			page: self.bookmarksModel.get('page'),
			pages: self.bookmarksModel.get('pages')
		});
		self.bookmarkContent.html(html);
		var bookmarks = self.bookmarkContent
				.find('.list-media-item[data-public="true"]');
		
		var pageLink = self.bookmarkContent.find('[data-page]');
		pageLink.off('click').on('click', function(){
			var page = $(this).data('page');
			var message = {
				page: page
			};
			eddie.putLou('', 'getBookmarkPage(' + JSON.stringify(message) + ')');
		});

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
}


BookmarksContent.prototype.renderVideosForCollection = function(target, collection){
	target.html(this.collectionsVideosTemplate({collection: collection}));
	
	var pageLink = target.find('[data-page]');
	pageLink.off('click').on('click', function(){
		var collection = $(this).data('collection');
		var page = $(this).data('page');
		var message = {
			collection: collection,
			page: page
		};
		eddie.putLou('', 'getCollectionPage(' + JSON.stringify(message) + ')');
	});
	
	var items = target.find('.list-media-item[data-public="true"]');

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

BookmarksContent.prototype.renderCollections = function() {
	var self = this;
	
	var collections = this.collectionsModel.get('collections');
	
	//If the collection structure hasn't been rendered out. 
	if(!this.collectionsRendered && collections){		
		var html = this.collectionsTemplate({collections: collections});
		console.log("HTML: " + html);
		this.collectionContent.html(html);
		
		this.collectionContent.find('a[data-toggle]').click(function(){
			setTimeout(function(){
	    		var message = {
	    			height: document.documentElement.scrollHeight
	    		}

	    		self.parent.sendToParent(JSON.stringify(message));
	    	}, 1000);
		});
		this.collectionsRendered = true;
	}
	
	//Just update the structure
	for(var i = 0; i < collections.length; i++){
		var collection = collections[i];
		var target = "#collapsibleCol_" + collection.id + " .panel-body";
		self.renderVideosForCollection(jQuery(target), collection);
	}
}