define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    require('bootstrap');
    require('bootstrap-select');
    require('bootstrap-select-zh-CN');

    var WidgetBase = require('./base');

    var htmlTmpl = '<label>{{name}}</label>\
        <select multiple data-done-button="true" data-width="100%" class="selectpicker" name="{{id}}" {{isRequired ? "required" : ""}} \
        data-none-selected-text="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}">\
            <# _.each(options, function (item) { #>\
                <option value="{{item}}">{{item}}</option>\
            <# }) #>\
        </select>';

    var DDMultiSelectField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DDMultiSelectField'
        },
        events: {
            'hidden.bs.select select': 'onChange'
        },
        render: function () {
            this._super_invoke('render');
            this.$('.selectpicker').selectpicker();
            return this;
        },
        getValue: function () {
            var val = this.$('select').val();
            if (val) { val = val.join(','); }
            return val;
        },
        setValue: function (val) {
            if (val) {
                val = val.split(',');
            }
            this.$el.find('select').selectpicker('val', val);
            this.onChange();
        }
    });

    module.exports = DDMultiSelectField;
});
