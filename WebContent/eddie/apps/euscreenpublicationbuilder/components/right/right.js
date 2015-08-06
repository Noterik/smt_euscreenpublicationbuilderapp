var Right = function(options){
	Component.apply(this, arguments);	
	setInterval(function(){
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true,
		      helper: 'clone',
			  stop: function(event, ui) {
			     var element = $(ui.helper).clone().removeAttr('style').draggable({ disabled: false });;
			  	 $('#bookmarklayout').append(element);
			  }
		    });
		}());
	}, 50);
}

Right.prototype = Object.create(Component.prototype);
