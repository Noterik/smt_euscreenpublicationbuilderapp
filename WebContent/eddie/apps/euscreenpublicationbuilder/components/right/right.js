var Right = function(options){
	Component.apply(this, arguments);	
	setTimeout(function(){
		(function(){
			$('.drag_bookmark').draggable( {
		      stack: '#bookmarklayout',
		      cursor: 'move',
		      revert: true
		    });
		}());
	}, 50);
}

Right.prototype = Object.create(Component.prototype);
