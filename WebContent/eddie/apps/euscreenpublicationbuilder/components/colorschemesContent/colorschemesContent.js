var ColorschemesContent = function(options){
	Component.apply(this, arguments);
	var self = this;
	var element = jQuery("#colorschemesContent");
	var themeListTemplate = _.template(element.find('#theme_listing_template').text());
	var themes = eddie.getComponent('layoutthemes');
	var parent = eddie.getComponent('iframesender');
	
	function renderThemes(){
		var data = themes.get('layoutThemes');
		var html = jQuery(themeListTemplate({data: data}));
		element.html(html);
		
		element.find('[data-theme-id]').on('click', function(){
			var $this = jQuery(this);
			var message = {
				'themeId': $this.data('theme-id')
			};
			eddie.putLou('', 'setTheme(' + JSON.stringify(message) + ')');
		});
	};
	
	console.log("THEMES OBJECT", themes);
	themes.on('layoutThemes-changed', function(){
		renderThemes();
	});
	renderThemes();
	
	setTimeout(function(){
		var message = {
			height: jQuery('body').height()
		}
		
		parent.sendToParent(JSON.stringify(message));
	}, 500);
	
}

ColorschemesContent.prototype = Object.create(Component.prototype);