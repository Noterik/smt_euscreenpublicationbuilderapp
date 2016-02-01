var Overlaydialog = function(){
	console.log("OVERLAY DIALOG!");
	Component.apply(this, arguments);
	var self = this;
	this.element = jQuery("#overlaydialog");
	this.template = _.template(this.element.find('#overlaydialogcontents_template').text());
	this.closeButton = this.element.find('.action.close');
	this.target = this.element.find(".contents");
	
	/*
	this.on('url-changed', function(){
<<<<<<< HEAD
		console.log("THIS URL CHANGED!");
		//I comment this line cuz it breaks preview part of publications in future if we need to show overlay with url we can fix it.
//		self.target.html(self.template({data:self.vars}));
	});
	
	this.on('html-changed', function(){
		var html = self.vars.html;
		html = html.decodeHTML();
		html = html.replace(new RegExp("&lt;", "g"), '<')
		self.target.html();
		console.log(self.target);
		self.target.html(html);
=======
		self.target.html(self.template({data:self.vars}));
	});*/
	
	this.on('html-changed', function(){
		var html = self.vars.html;
		console.log("TARGET", self.target);
		console.log("HTML: " + html);
		var iframeHTML = self.template({data: { url : 'data:text/html;charset=utf-8,' + encodeURI(html) }});
		console.log(iframeHTML);
		self.target.html(iframeHTML);
>>>>>>> svg-icons
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
