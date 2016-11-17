define(function(require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');
    require('bootstrap-datetimepicker');
    require('bootstrap-datetimepicker-zh-CN');

    var WidgetBase = require('./base');

    var htmlTmpl = '<div class="part-region"><label>{{name[0]}}</label>\
        <input type="text" role="start" name="{{id}}" value="{{value[0]}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} readonly></div>\
        <div class="part-region"><label>{{name[1]}}</label>\
        <input type="text" role="end" name="{{id}}" value="{{value[1]}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} readonly></div>';

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

    var DDDateRangeField = WidgetBase.extend({
        template: template(htmlTmpl),
        attributes: {
            'data-widget': 'DDDateRangeField'
        },
        events: {
            'hide input': 'onChange'
        },
        render: function() {
            var names = this.model.get('name').split(',');
            this.model.set('name', names);
            // this._super_invoke('render');
            var attrJson = this.model.toJSON();
            var html = this.template(attrJson);
            this.$el.html(html);
            this.$('input').datetimepicker(formatSettings[this.model.get('timeFormat')]);
            return this;
        },
        getValue: function() {
            var startTime = this.$('[role="start"]').val(),
                endTime = this.$('[role="end"]').val();
            return [startTime, endTime];
        },
        setValue: function(val) {
            this.$('[role="start"]').val(val[0]);
            this.$('[role="end"]').val(val[1]);
            this.onChange();
        }
    });

    module.exports = DDDateRangeField;
});