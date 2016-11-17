define(function (require, exports, moduel) {
    var $ = require('jquery');
    var _ = require('underscore');
    require('bootstrap-datetimepicker');
    require('bootstrap-datetimepicker-zh-CN');

    var defaultSetting = {
            format: 'yyyy-mm-dd',
            language: 'zh-CN',
            autoclose: true,
            minView: 2
        };
    var formatSettings = {
        '0': _.extend({}, defaultSetting, { format: 'yyyy-mm-dd', minView: 2, startView: 2 }),
        '1': _.extend({}, defaultSetting, { format: 'yyyy-mm-dd hh:ii', minView: 0, startView: 2 }),
        '2': _.extend({}, defaultSetting, { format: 'hh:ii' ,minView: 0, startView: 1, maxView: 1 })
    };

    var datetimeSelect = function (elements, format) {
        format = format || '0';
        elements.each(function () {
            $(this).datetimepicker(formatSettings[format]);
        });
    };

    moduel.exports = datetimeSelect;
});
