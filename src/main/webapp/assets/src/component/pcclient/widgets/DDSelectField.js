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
        <div><select data-width="100%" class="selectpicker" name="{{id}}" {{isRequired ? "required" : ""}} \
        data-none-selected-text="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}">\
            <# _.each(options, function (item, index) { #>\
                <option value="{{item}}"<# if(index == 0) { #>selected="selected"<#}#>>{{item}}</option>\
            <# }) #>\
        </select></div>';

    var DDSelectField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DDSelectField'
        },
        events: {
            'changed.bs.select select': 'onChange'
        },
        render: function() {
            this._super_invoke('render');
            this.$select = this.$('.selectpicker');
            this.$select.selectpicker();
            return this;
        },
        getValue: function() {
            return this.$('select').val();
        },
        setValue: function(val) {
            this.$select.selectpicker('val', val);
            this.onChange();
        }
    });

    module.exports = DDSelectField;
});