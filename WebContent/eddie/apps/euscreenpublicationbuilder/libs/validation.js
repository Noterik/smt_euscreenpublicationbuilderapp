(function(){
	console.log("PublicationBuilder.validation()");
	var root = this;
		
	if(!root.PublicationBuilder)
		root.PublicationBuilder = {};
	
	var PublicationBuilder = root.PublicationBuilder;
	
	PublicationBuilder.validation = (function(){
		return {
			'youtube': function(url){
				var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
			    var match = url.match(regExp);
			    return (match&&match[7].length==11)? match[7] : false;
			},
			'vimeo': function(url){
				var re = /\/\/(?:www\.)?vimeo.com\/([0-9a-z\-_]+)/i;
			    var matches = url.match(re);
			    return (matches && matches[1]) ? matches[1] : false;
			}
		}
	});
})(this);