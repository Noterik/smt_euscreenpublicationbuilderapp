var Overlaydialog = function(){
	console.log("OVERLAY DIALOG!");
	Component.apply(this, arguments);
	var self = this;
	this.element = jQuery("#overlaydialog");
	this.template = _.template(this.element.find('#overlaydialogcontents_template').text());
	this.closeButton = this.element.find('.action.close');
	this.target = this.element.find(".contents");
	
	this.on('html-changed', function(){
		var html = self.vars.html;
		var iframeHTML = self.template({data: { url : 'data:text/html;charset=utf-8,' + encodeURI(html) }});
		console.log(self.target[0]);
		console.log("HTML: " + iframeHTML);
		self.target.html(iframeHTML);
	});
	
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
