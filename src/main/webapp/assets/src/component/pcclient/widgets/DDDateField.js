define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    require('bootstrap-datetimepicker');
    require('bootstrap-datetimepicker-zh-CN');

    var WidgetBase = require('./base');

    var htmlTmpl = '<label>{{name}}</label>\
        <input type="text" name="{{id}}" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} readonly>';

    var defaultSetting = {
        format: 'yyyy-mm-dd',
        language: 'zh-CN',
        autoclose: true,
        minView: 2
    };
    var formatSettings = {
        '0': _.extend({}, defaultSetting, {
            format: 'yyyy-mm-dd',
            minView: 2,
            startView: 2
        }),
        '1': _.extend({}, defaultSetting, {
            format: 'yyyy-mm-dd hh:ii',
            minView: 0,
            startView: 2
        }),
        '2': _.extend({}, defaultSetting, {
            format: 'hh:ii',
            minView: 0,
            startView: 1,
            maxView: 1
        })
    };

    var DDDateField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DDDateField'
        },
        events: {
            'hide input': 'onChange'
        },
        render: function() {
            this._super_invoke('render');
            this.$('input').datetimepicker(formatSettings[this.model.get('timeFormat')]);
            return this;
        },
        getValue: function() {
            return this.$('input').val();
        },
        setValue: function(val) {
            this.$('input').val(val);
            this.onChange();
        }
    });

    module.exports = DDDateField;
});