var Overlaydialog = function(){
	console.log("OVERLAY DIALOG!");
	Component.apply(this, arguments);
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
	
	this.on('html-changed', function(){
		/*
		var html = self.vars.html;
		var iframeHTML = self.template({data: { url : 'data:text/html;charset=utf-8,' + encodeURI(html) }});
		self.target.html(iframeHTML);
		*/
		
		var html = self.vars.html;
		html = html.decodeHTML();
		html = html.replace(new RegExp("&lt;", "g"), '<')
		self.html = html;
		setTimeout(function(){
			render();
		}, 0);
	});
	
	function render(){
		
		if(self.vars.wrap && self.html){
			self.target.html('<iframe allowfullscreen src="about:blank"></iframe>');
			var context = self.target.find('iframe')[0].contentWindow.document;
			self.html = self.html.replace(/<\?xml.*?\?>/, "");
			context.write(self.html);
			
			var iframeContents = $(self.target.find('iframe').contents());
			iframeContents.find('body').css('background-color', 'rgba(0,0,0,0)');
			iframeContents.find('body').css('overflow-y', 'hidden');
		}else if(self.html){
			self.target.html(self.html);
		}
	}

	this.on('visible-changed', function(){
		if(self.vars.visible){
			self.element.fadeIn();
		}else{
			self.element.fadeOut();
		}
	});

	this.closeButton.on('click', function(){
		self.element.hide();
		var result = JSON.stringify({textItem: ""});

		eddie.putLou('', 'closePreview()');

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
