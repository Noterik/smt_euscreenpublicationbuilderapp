var Overlaydialog = function(){
	var self = this;
	this.element = jQuery("#overlaydialog");
	this.template = _.template(this.element.find('#overlaydialogcontents_template').text());
	this.closeButton = this.element.find('.action.close');
	this.target = this.element.find(".contents");
	
	String.prototype.decodeHTML = function() {
	    var map = {"gt":">" /* , … */};
	    return this.replace(/&(#(?:x[0-9a-f]+|\d+)|[a-z]+);?/gi, function($0, $1) {
	        if ($1[0] === "#") {
	            return String.fromCharCode($1[1].toLowerCase() === "x" ? parseInt($1.substr(2), 16)  : parseInt($1.substr(1), 10));
	        } else {
	            return map.hasOwnProperty($1) ? map[$1] : $0;
	        }
	    });
	};
	
	this.on('url-changed', function(){
		console.log("THIS URL CHANGED!");
		self.target.html(self.template({data:self.vars}));
	});
	
	this.on('html-changed', function(){
		var html = self.vars.html;
		html = html.decodeHTML();
		html = html.replace(new RegExp("&lt;", "g"), '<')
		console.log(html);
		self.target.html(html);
	});
	
	this.on('visible-changed', function(){
		console.log("Overlaydialog::visible-changed!");
		if(self.vars.visible){
			self.element.fadeIn();
		}else{
			self.element.fadeOut();
		}
	});
	
	this.closeButton.on('click', function(){
		self.element.hide();
		eddie.putLou('', 'closePublicationBuilder()');
	});
	
	
	var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
	var eventer = window[eventMethod];
	var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

	// Listen to message from child window
	eventer(messageEvent,function(e) {
	  console.log('parent received message!:  ',e.data);
	  eddie.putLou("", "createVideoPoster(" + e.data + ")");
	},false);
	
};
Overlaydialog.prototype = Object.create(Component.prototype);
