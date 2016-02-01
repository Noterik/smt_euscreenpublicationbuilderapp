var BuildContent = function(options) {
    var self = this;
    var initialized = false;
    this.element = jQuery("#buildCont");
    
    /**
     * These are all the templates, we want to keep the amount of HTML in the javascript to a minimum. 
     */
    this.videoTemplate = _.template(jQuery("#video-template").text());
    this.videoControlsTemplate = _.template(jQuery("#video-controls-template").text());
    this.widgetControlsTemplate = _.template(jQuery('#widget-controls-template').text());
    this.additionalMediaButtonHTML = _.template(jQuery('#external-videos-template').text());
    this.embedFormTemplate = _.template(jQuery('#embed-form-template').text());
    this.parent = eddie.getComponent('iframesender');
    this.blockUpdates = false;
    
    /**
     * Video poster contents object
     */
    
    this.contents = null;
    
    /**
     * Video poster template
     */
    
    this.template = null;
    
    /**
     * This contains all the pending updates!
     */
    this.timeouts = {};
    this.timeoutTime = 1000;
    
    /**
     * This contains the templates for the different embed types we support right now. Contains the wrapper html like <iframe src="....">
     */
    this.embedTemplates = {
    	'youtube': _.template(jQuery('#youtube-embed').text()),
    	'vimeo': _.template(jQuery('#vimeo-embed').text())
    }
    
    Component.apply(this, arguments);
    
    /**
     * These are the attributes we can give to a media item. 
     * 
     * - data-media-filled indicates that a media item has content and should be emptied before putting new stuff in. 
     * - data-media-src indicates that this media item has an internal (EUscreen media item)
     * - data-media-external-src indicates that this media item has an external media item (YouTube or Vimeo for now)
     */
    this.mediaAttributes = [
    	'data-media-filled',
    	'data-media-src',
    	'data-media-thumb'
    ];

    /**
     * Sets up the building environment. Parses the template and goes through its elements, calls functions that render inputs. 
     */
    function renderBuildContent() {
    	console.log("RENDER BUILD CONTENT: " , self.videoposter);
        
        self.element.html(self.template);
        
        //Let's first do the title
        self.element.find('[data-section-type="title"]').each(function() {
        	var $this = $(this);
            $this.attr('contenteditable', 'true');
            var id = $this.data('section-id');
            
            //Check if there's existing content (edit mode)
            if(self.contents[id] && self.contents[id].contents){
            	$this.html(self.contents[id].contents);
            }
            
            self.setupDelayedSaving.apply(self, [$this]);
        });
        
        //Each media item is indicated in the template by a data-section-type attribute, like this <div class="..." data-section-type="media"></div>
        self.element.find('[data-section-type="media"]').each(function(){
        	var $this = $(this);
        	self.setupMediaItem.apply(self, [$(this)]);
        	var id = $this.data('section-id');
        	
        	//Check if there's existing content (edit mode)
        	if(self.contents[id] && self.contents[id].contents){
        		self.setVideo($this, self.contents[id].contents, self.contents[id]['thumb'])
        	}
        });
        
        self.element.find('[data-section-type="text"],[data-section-type="text_big"]').each(function(){
        	var $this = $(this);
        	var id = $this.data('section-id');
        	
        	//Check if there's existing content (edit mode)
        	if(self.contents[id] && self.contents[id].contents){
        		$this.html(self.contents[id].contents);
        	}
        });
        
        //Initializes tinymce on the text inputs 
        self.initTinyMce();
        
        setTimeout(function(){
    		var message = {
    			height: jQuery('body').height()
    		}
    		
    		self.parent.sendToParent(JSON.stringify(message));
    	}, 500);
        
    }
    
    /**
     * Loads the css for the current layout.
     */
    function setLayoutCss() {
        var layoutCSS = self.videoposter.get('layoutcss');
        var layoutStyleElement = $('<link rel="stylesheet" id="layoutcss" type="text/css" href="' + layoutCSS + '">');
        jQuery('head').append(layoutStyleElement);
    }
    
    /**
     * Loads the css for the current theme. 
     */
    function setThemeCss() {
        var themeCSS = self.videoposter.get('themecss');
        var themeStyleElement = $('<link rel="stylesheet" id="themecss" type="text/css" href="' + themeCSS + '">');
        jQuery('head').append(themeStyleElement);
    }

    /**
     * The videoposter.js (represented by VideoPoster.java on the server) is a domain object that can be listened to. This object contains no viewing logic and should be seen as a pure domain model
     * containing the data specific to a videoposter. This model can be rendered into viewing HTML by the server, or into an editable document with this 
     * this object (buildContent.js)
     */
    this.videoposter = eddie.getComponent('videoposter');
    this.videoposter.on('layoutcss-changed', setLayoutCss);
    this.videoposter.on('themecss-changed', setThemeCss);
    this.videoposter.on('template-changed', function(template){
    	self.template = template;
    	continueWhenReady();
    });
    this.videoposter.on('contents-changed', function(contents){
    	self.contents = contents;
    	continueWhenReady();
    });
    
    function continueWhenReady(){
    	if(!self.initialized){
    		console.log("SELF CONTENTS: " , self.contents);
    		console.log("SELF TEMPLATE: " , self.template);
	    	if(self.template && self.contents){
	    		self.initialized = true;
	    		console.info("CONTENTS: " , self.contents);
	    		renderBuildContent();
	    	}else{
	    		console.info("NOT READY!");
	    	}
    	}
    }

}
BuildContent.prototype = Object.create(Component.prototype);

BuildContent.prototype.setupDelayedSaving = function($target){
	var self = this;
	var id = $target.data('section-id');
	$target.on('propertychange change click keyup input paste DOMSubtreeModified', function(){
		console.info($target[0], "SOMETHING CHANGED!");
    	if(self.timeouts[id]){
    		clearTimeout(self.timeouts[id]);
    	}
    	self.timeouts[id] = setTimeout(function(){
    		if(!self.blockUpdates){
    			console.info("SEND THE UPDATE!");
    			self.serializeContents();
    		}
    	}, self.timeoutTime);
    });
};

/**
 * Sets up a media item input in the current template. Defines listeners to a drop event from the end user. Also defines listeners for when a 
 * user specifies he wants to use a media item from an external provider (YouTube, Vimeo)
 */
BuildContent.prototype.setupMediaItem = function(target) {
	var self = this;
	var $mediaItem = target;
	
	//Listen to drop events
	$mediaItem.droppable({
        accept: 'div.list-media-item',
        drop: function(){
        	self.handleCardDrop.apply(self, arguments);
        }
    });
	
	//Initializes the widget with which the user can add videos from external providers. 
	if(!$mediaItem.data('media-filled')){
		$mediaItem.html(self.additionalMediaButtonHTML);
		$mediaItem.find('.add-external-button').unbind('click').click(function(event) {
	        event.stopPropagation();
	        event.preventDefault();
	        $(this).parent().children().each(function() {
	            if ($(this).attr("class").indexOf("add-external-button") != -1) {
	                $(this).toggleClass('tgl-rotate');
	                $(this).parent().find(".addVideoBox").remove();
	            } else {
	                $(this).toggle('slow');
	            }
	        });
	    });
		
		//These functions listen to the youtube and vimeo buttons, can be made cleaner.
		$mediaItem.find('.add-youtube-button').on('click', function(){
			self.setupExternalInputForm.apply(self, [$mediaItem, 'youtube']);
	    });
	    
	    $mediaItem.find('.add-vimeo-button').on('click', function(){
			self.setupExternalInputForm.apply(self, [$mediaItem, 'vimeo']);
	    });
	}
    
};

/**
 * This function is triggered when a user drops an media-item from the bookmark list on the media-item input. 
 */
BuildContent.prototype.handleCardDrop = function(event, ui) {
    console.log("BuildContent.handleCardDrop(event, event)");
    var self = this;
    var video = ui.helper.find('img');
    var src = video.attr('data-video-src');
    var poster = video.attr('data-video-poster');

    var $target = $(event.target);

    self.setLocalVideo($target, src, poster);
    self.serializeContents.apply(self);
};

/**
 * Clears a media item of any content. A new clean slate. 
 */
BuildContent.prototype.clearMediaItem = function($mediaItem, update){
	$mediaItem.html('');
	_.each(this.mediaAttributes, function(attr){
		$mediaItem.removeAttr(attr);
	});
	this.setupMediaItem($mediaItem);
	
	if(update)
		this.serializeContents();
};

BuildContent.prototype.setVideo = function($mediaItem, videoStr, poster){
	console.info("setVideo(" , $mediaItem, "," , videoStr , ")");
	var self = this;
	if(videoStr.indexOf("(") !== -1){
		console.info("CORRECT VIDEO!");
		var videoId = videoStr.substring(videoStr.indexOf("(") + 1, videoStr.lastIndexOf(")"));
		
		if(videoStr.indexOf("vimeo(") !== -1){
			self.setEmbed($mediaItem, 'vimeo', videoId);
		}else if(videoStr.indexOf("youtube(") !== -1){
			self.setEmbed($mediaItem, 'youtube', videoId);
		}else {
			self.setLocalVideo($mediaItem, videoId, poster);
		}
	} else{
		console.info("INCORRECT VIDEO!");
	}
	
	
}

BuildContent.prototype.setLocalVideo = function($mediaItem, src, poster){
	var self = this;
	$mediaItem.attr("data-media-src", 'internal(' + src + ')');
	$mediaItem.attr("data-media-thumb", poster);
	
	 eddie
	 .getComponent('embedlib')
     .loaded()
     .then(
         function() {
             EuScreen
                 .getVideo({
                         src: src,
                         poster: poster,
                         controls: true
                     },
                     function(html) {
                         var contents = jQuery(self.videoTemplate());
                         contents.html(html);
                         $mediaItem.html(contents);
                         self.addVideoControls($mediaItem);
                     });
         });
};

BuildContent.prototype.setEmbed = function($mediaItem, embedType, embedId){
	var self = this;
	
	$mediaItem.html(self.embedTemplates[embedType]({external: {id: embedId}}));
	$mediaItem.attr('data-media-src', embedType + '(' + embedId + ')');
	self.addVideoControls($mediaItem);
	
};

/**
 * Initializes the form that allows a user to specify a URL from an external provider (YouTube or Vimeo for now). 
 */
BuildContent.prototype.setupExternalInputForm = function($mediaItem, embedType){
	var self = this;
	var $addVideoBox = jQuery(self.embedFormTemplate({external: {type: embedType}}));
	$mediaItem.append( $addVideoBox );
	
	self.addWidgetControls.apply(self, [$mediaItem, $addVideoBox]);
	
	var $form = $addVideoBox.find('.embed-form');
	var $urlInputGroup = $form.find('#external-id-input-group');
	var $urlInput = $form.find('#external-id-input');
	
	var validation = PublicationBuilder.validation();
	var validationRule = validation[embedType];
	
	$form.on('submit', function(e){
		e.preventDefault();
		var valid = validationRule($urlInput.val());
		if(valid){
			$urlInputGroup.toggleClass('has-feedback', true);
			$urlInputGroup.toggleClass('has-warning', false);
			$urlInputGroup.toggleClass('has-success', true);
			
			var id = valid;
			
			self.setEmbed($mediaItem, embedType, id);
			
			self.serializeContents.apply(self);
			self.addVideoControls.apply(self, [$mediaItem]);
		}else{
			$urlInputGroup.toggleClass('has-feedback', true);
			$urlInputGroup.toggleClass('has-success', false);
			$urlInputGroup.toggleClass('has-warning', true);
		}
	});
	
	$urlInput.on('propertychange change click keyup input paste', function(){
		var valid = validationRule($urlInput.val());
		$urlInputGroup.toggleClass('has-feedback', false);
		$urlInputGroup.toggleClass('has-success', false);
		$urlInputGroup.toggleClass('has-warning', false);
		if(valid){
			$urlInputGroup.toggleClass('has-feedback', true);
			$urlInputGroup.toggleClass('has-success', true);
		}
	});
};

/**
 * Initializes tinyMCE on the text inputs
 */
BuildContent.prototype.initTinyMce = function() {
    console.log("BuildContent.initTinyMce()");
    var self = this;
    var textElements = this.element.find('.text_item[data-section-type="text"]');
	textElements.each(function(){
		var $element = $(this);
		tinymce
	        .init({
	            mode: "exact",
	            menubar: false,
	            plugins: 'link',
	            toolbar: "fontselect | fontsizeselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
	            elements: this.id,
	            height: 256,
	            setup: function(ed){
	            	            	
	            	ed.on('keyup', function(){
	            		ed.save();
	            		console.log("CHANGED!");
	            	});
	            	ed.on('init', function(){
	            		self.setupDelayedSaving.apply(self, [$element]);
	            	});
	            }
	        });
	});
    
    var bigTextElements = this.element.find('.text_item[data-section-type="text_big"]');
    bigTextElements.each(function(){
		console.info("THIS ID = " + this.id);
		var $element = $(this);
		console.info("HTML: " + $element.html());
        var editor = tinymce
            .init({
                mode: "exact",
                elements: this.id,
                menubar: false,
                plugins: 'link',
                setup: function(ed){    
                	ed.on('keyup', function(){
                		ed.save();
                	});
                	ed.on('init', function(){
                		self.setupDelayedSaving.apply(self, [$element]);
                	});
                },
                toolbar: "fontselect fontsizeselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image"
            });
        	
        }
    );


};

BuildContent.prototype.addWidgetControls = function($mediaItem, $itemToRemove){
	console.log("addWidgetControls()");
	var self = this;
	
	var $widgetControlsEl = jQuery(this.widgetControlsTemplate());
	
	$mediaItem.append($widgetControlsEl);
	
	var removeIcon = $widgetControlsEl.find('.remove');
	removeIcon.on('click', function(){
		$widgetControlsEl.remove();
		$itemToRemove.remove();
	});
};

BuildContent.prototype.addVideoControls = function($mediaItem){
	var self = this;
	
	var videoControlsEl = jQuery(this.videoControlsTemplate());
	
	var fullScreenIcon = videoControlsEl.find('.fullscreen');
    var removeIcon = videoControlsEl.find('.remove');
    
    $mediaItem.append(videoControlsEl);
    
    fullScreenIcon
        .on(
            'click',
            function() {
                console
                    .log(video);
                if (video.requestFullscreen) {
                    video
                        .requestFullscreen();
                } else if (video.mozRequestFullScreen) {
                    video
                        .mozRequestFullScreen();
                } else if (video.webkitRequestFullscreen) {
                    video
                        .webkitRequestFullscreen();
                }
            });
    removeIcon
        .on(
            'click',
            function() {
               self.clearMediaItem.apply(self, [$mediaItem]);
            });
};

BuildContent.prototype.serializeContents = function(){
	console.info("SERIALIZE CONTENTS")
	var self = this;
	var object = {};
	self.element.find('[data-section-id]').each(function(){
		var $this = $(this);
		console.log(this);
		var id = $this.data('section-id');
		var type = $this.data('section-type');
		
		var fieldObj = {
			type: type
		};
		switch(type){
			case "title":
				fieldObj.contents = $this.text();
				break;
			case "media":
				fieldObj.contents = $this.attr('data-media-src') ? $this.attr('data-media-src') : null;
				fieldObj.thumb = $this.attr('data-media-thumb') ? $this.attr('data-media-thumb') : null;
				break;
			case "text":
				fieldObj.contents = $this.html();
				break;
			case "text_big": 
				fieldObj.contents = $this.html();
				break;
		}
		object[id] = fieldObj;
	});
	eddie.putLou('', 'savePoster(' + JSON.stringify(object) + ')');
};

BuildContent.prototype.draggingStarted = function(){
	 this.element.find('[data-section-type="media"]').toggleClass('dragging', true);
};

BuildContent.prototype.draggingStopped = function(){
	this.element.find('[data-section-type="media"]').toggleClass('dragging', false);
};

