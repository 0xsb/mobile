define(function (require, exports, module) {
    var $ = require('jquery');
    var _ = require('underscore');
    var Backbone = require('backbone');
    var template = require('template');

    var TextField = require('./widgets/TextField');
    var TextareaField = require('./widgets/TextareaField');
    var DDSelectField = require('./widgets/DDSelectField');
    var NumberField = require('./widgets/NumberField');
    var DDMultiSelectField = require('./widgets/DDMultiSelectField');
    var DDDateField = require('./widgets/DDDateField');
    var DDDateRangeField = require('./widgets/DDDateRangeField');
    var DDPhotoField = require('./widgets/DDPhotoField');
    var DDAttachment = require('./widgets/DDAttachment');
    var MoneyField = require('./widgets/MoneyField');
    var RTEditor = require('./widgets/RTEditor');
    var EventButton = require('./widgets/EventButton');
    var DepartmentSelect = require('./widgets/DepartmentSelect');

    var templates = {
        TextField: template('<input type="text" name="{{id}}" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>'),

        TextareaField: template('<textarea type="text" name="{{id}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>{{value}}</textarea>'),

        DDSelectField: template('<select data-width="100%" class="selectpicker" name="{{id}}" {{isRequired ? "required" : ""}} \
        data-none-selected-text="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}">\
            <# _.each(options, function (item) { #>\
                <option value="{{item}}">{{item}}</option>\
            <# }) #>\
        </select>'),

        NumberField: template('<input type="text" maxlength="12" name="{{id}}" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} {{isReadonly ? "readonly" : ""}}>'),

        DDMultiSelectField: template('<select multiple data-done-button="true" data-width="100%" class="selectpicker" name="{{id}}" {{isRequired ? "required" : ""}} \
        data-none-selected-text="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}">\
            <# _.each(options, function (item) { #>\
                <option value="{{item}}">{{item}}</option>\
            <# }) #>\
        </select>'),

        DDDateField: template('\
        <input type="text" name="{{id}}" value="{{value}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} readonly>'),

        DDDateRangeField: template('<div class="part-region"><label>{{name[0]}}</label>\
        <input type="text" role="start" name="{{id}}" value="{{value[0]}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} readonly></div>\
        <div class="part-region"><label>{{name[1]}}</label>\
        <input type="text" role="end" name="{{id}}" value="{{value[1]}}" \
        placeholder="{{placeholder}}{{isRequired ? \'(必填)\' : \'\'}}" \
        {{isRequired ? "required" : ""}} readonly></div>'),

        DDPhotoField: template('<a href="javascript:" class="trigger" role="trigger">上传图片</a>\
        <input role="uploader" type="file" accept="image/*" style="display: none;">\
        <input role="value-heap" name="{{id}}" type="hidden" {{isRequired ? "required" : ""}}>\
        <div role="img-list" class="list clearfix"></div>')
    };

    var widgets = {
        TextField: TextField.extend({
            tagName: 'td',
            template: templates['TextField']
        }),
        TextareaField: TextareaField.extend({
            tagName: 'td',
            template: templates['TextareaField']
        }),
        DDSelectField: DDSelectField.extend({
            tagName: 'td',
            template: templates['DDSelectField']
        }),
        NumberField: NumberField.extend({
            tagName: 'td',
            template: templates['NumberField']
        }),
        DDMultiSelectField: DDMultiSelectField.extend({
            tagName: 'td',
            template: templates['DDMultiSelectField']
        }),
        DDDateField: DDDateField.extend({
            tagName: 'td',
            template: templates['DDDateField']
        }),
        DDDateRangeField: DDDateRangeField.extend({
            tagName: 'td',
            template: templates['DDDateRangeField']
        }),
        DDPhotoField: DDPhotoField.extend({
            tagName: 'td',
            template: templates['DDPhotoField']
        }),
        DDAttachment: DDAttachment,
        MoneyField: MoneyField,
        RTEditor: RTEditor,
        EventButton: EventButton,
        DepartmentSelect: DepartmentSelect
    };

    var widgetFactory = function (setting) {
        var WidgetView = widgets[setting.controlId];
        if (WidgetView) {
            var widget = new WidgetView({
                modelAttrs: setting
            });
            return widget;
        } else {
            return null;
        }
    };

    module.exports = widgetFactory;
});
