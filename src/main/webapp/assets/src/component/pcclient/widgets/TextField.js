define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var WidgetBase = require('./base');

    var htmlTmpl = '<label>{{name}}</label>\
        <input type="text" name="{{id}}" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>';

    var TextField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'TextField'
        },
        events: {
            'input input': 'onChange'
        },
        getValue: function() {
            return this.$('input').val();
        },
        setValue: function(val) {
            this.$('input').val(val);
            this.onChange();
        }
    });

    module.exports = TextField;
});