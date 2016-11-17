define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var widgets = {
        TextField: require('./widgets/TextField'),
        TextareaField: require('./widgets/TextareaField'),
        DDSelectField: require('./widgets/DDSelectField'),
        NumberField: require('./widgets/NumberField'),

        DDMultiSelectField: require('./widgets/DDMultiSelectField'),
        DDDateField: require('./widgets/DDDateField'),
        DDDateRangeField: require('./widgets/DDDateRangeField'),
        DDPhotoField: require('./widgets/DDPhotoField')
    };

    var widgetFactory = function(setting) {
        var WidgetView = widgets[setting.controlId];
        if (WidgetView) {
            var widget = new WidgetView({
                modelAttrs: setting
            });
            var width = widget.model.get('width');
            widget.$el.css('width', width * 100 / 12 + '%');
            return widget;
        } else {
            console.log(setting);
            return null;
        }
    };

    module.exports = widgetFactory;
});