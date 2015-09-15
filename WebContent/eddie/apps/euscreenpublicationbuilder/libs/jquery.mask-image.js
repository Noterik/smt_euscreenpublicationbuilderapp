(function ( $ ) {
	
	var parent;
 
    $.fn.maskImage = function() {
    	return this.each(function(){
    		var self = $(this);
        	var parent = self.parent();
        	parent.css('overflow', 'hidden');
        	var parentWidth = parent.width()
        	var parentHeight = parent.height();
        	var src = self.data('src');
        	
        	var img = new Image;
        	img.onload = (function(self){
        		return function(){
        			var imgWidth = this.width;
            		var imgHeight = this.height;
            		
            		var aspectRatio = imgWidth / imgHeight;
            		            		
            		var elementWidth;
            		var elementHeight;
            		
            		if(imgWidth > imgHeight){
            			elementHeight = parentHeight;
            			elementWidth = elementHeight * aspectRatio;
            		}else{
            			elementWidth = parentWidth;
            			elementHeight = elementWidth / aspectRatio;
            		}
            		
            		while(elementHeight < parentHeight){
            			elementHeight++;
            			elementWidth = elementHeight * aspectRatio;
            		}
            		
            		while(elementWidth < parentWidth){
            			elementWidth++;
            			elementHeight = elementWidth / aspectRatio;
            		}
            		
            		var heightDifference = elementHeight - parentHeight;
            		var widthDifference = elementWidth - parentWidth;
            		
            		var top = heightDifference > 0 ? -heightDifference / 2 : 0;
            		var left = widthDifference > 0 ? -widthDifference / 2 : 0;
            		
            		
            		self[0].src = src;
            		self.css('width', elementWidth);
            		self.css('height', elementHeight);
            		self.css('margin-left', left);
            		self.css('margin-top', top);
        		}
        	})(self);
        	
        	img.src = src;
        	return this;
    	});
    };
 
}( jQuery ));