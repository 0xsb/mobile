define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var WidgetBase = require('./base');

    var htmlTmpl = '<label>{{name}}</label>\
        <textarea type="text" name="{{id}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>{{value}}</textarea>';

    var TextareaField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'TextareaField'
        },
        events: {
            'input textarea': 'onChange'
        },
        getValue: function() {
            return this.$('textarea').val();
        },
        setValue: function(val) {
            this.$('textarea').val(val);
            this.onChange();
        }
    });

    module.exports = TextareaField;
});