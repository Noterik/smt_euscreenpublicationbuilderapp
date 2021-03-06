var Embedlib = function(){
	var self = this;
	this.ready = false;
	this.loaded = function(){
		return new Promise(function(resolve, reject){
			if(!self.ready){
				$.getScript("http://euscreen.eu:8080/euscreen_embed/euscreenembedlib.js", function( data, textStatus, jqxhr ) {
					self.ready = true;
					resolve();
				});
			}else{
				resolve();
			}
		});
	};
	
	this.transformVideos = function() {
      $('video[data-src]').each(function(index, video) {
        var src = $(video).data('src');
        var poster = $(video).data('poster');
        if(src && src !== ""){
	        EuScreen.getVideo({
	        	src: src,
	        	poster: poster,
	        	controls: true
	        }, (function(video){
	        	return function(html){
	        		video.replaceWith(html);
	        	}
	        })($(video)))
        }

      });
    };
}

Embedlib.prototype = Object.create(Component.prototype);