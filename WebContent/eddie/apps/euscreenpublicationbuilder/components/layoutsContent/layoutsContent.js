var LayoutsContent = function(options){
	Component.apply(this, arguments);
	var self = this;
	this.element = jQuery("#layoutsContent");
	this.layoutListTemplate = _.template(this.element.find('#layout_listing_template').text());
	this.layouts = eddie.getComponent('layouts');
	
	this.layouts.on('layouts-changed', function(layouts){
		self.renderLayouts()
	});
	
}

LayoutsContent.prototype = Object.create(Component.prototype);

LayoutsContent.prototype.renderLayouts = function(data) {
	console.log('LayoutsContent.renderLayouts(' + data + ')');
	var layouts = this.layouts.get('layouts');
	var html = jQuery(this.layoutListTemplate({data: layouts}));
	this.element.append(html);
	
	this.element.find('[data-layout-id]').on('click', function(){
		var id = jQuery(this).data('layout-id');
		var message = {
			layoutId: id
		};
		eddie.putLou("", "setLayout(" + JSON.stringify(message) + ")");
	});
};