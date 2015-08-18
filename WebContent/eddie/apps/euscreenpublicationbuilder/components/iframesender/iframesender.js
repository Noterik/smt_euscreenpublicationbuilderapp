var Iframesender = function(){
	console.log("new Iframesender");
};
Iframesender.prototype = Object.create(Component.prototype);
Iframesender.prototype.sendToParent = function(message){
	console.log("Iframesender.sendToParent(" + message + ")");
	parent.postMessage(message, "*");
};