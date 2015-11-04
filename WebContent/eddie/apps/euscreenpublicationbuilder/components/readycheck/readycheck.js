var Readycheck = function(){
     var tinyMCELoaded = false;
     var media_item = false;
     this.loaded = function(){
         return new Promise(function(resolve, reject){
      	
              var loadCheck = setInterval(function(){
				if(tinyMCE.editors.length > 0){
					tinyMCELoaded = true;
				}
				if(tinyMCELoaded){
					console.log(tinyMCELoaded);
					console.log(media_item);
				    resolve()
				    clearInterval(loadCheck);
				    
				}
              }, 5); 
         });
     }

}
Readycheck.prototype = Object.create(Component.prototype);