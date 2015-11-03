var Readycheck = function(){
     var tinyMCELoaded = false;
     this.loaded = function(){
         return new Promise(function(resolve, reject){
      	
              var loadCheck = setInterval(function(){
				if(tinyMCE.editors.length > 0){
					tinyMCELoaded = true;
				}
 
				if(tinyMCELoaded){
				    resolve()
				    clearInterval(loadCheck);
				    
				}
              }, 20); 
         });
     }

}
Readycheck.prototype = Object.create(Component.prototype);