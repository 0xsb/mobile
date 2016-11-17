define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var SelectList = require('./select-list');
    var util = require('component/mobile/util');

    var LinkageSelect = function (element, options) {
        this.init(element, options);
    };
    LinkageSelect.prototype = {
        init: function (element, options) {
            this.$el = element;
            this.options = options;
            this.selects = options.jsonData.linkageOption.selects;
            this.childSelects = null;
            this.parentList = new SelectList({
                multi: false,
                couldAdd: false,
                textField: 'value',
                valueField: 'value'
            });
            this.childList = new SelectList({
                multi: false,
                couldAdd: false,
                textField: 'value',
                valueField: 'value'
            });
            this.parentList.setList(this.selects);
            this.childList.noDataMessage = '请先选择' + options.label[0];

            this.bindEvent();
        },
        bindEvent: function () {
            var _this = this;
            var parentTrigger = this.$el.find('.trigger.parent'),
                childTrigger = this.$el.find('.trigger.child');

            this.parentList.triggerFor(parentTrigger);
            this.childList.triggerFor(childTrigger);
            this.parentList.on('select', function (e, val, obj, el, list) {
                parentTrigger.val(val.text);
                childTrigger.val('');
                parentTrigger.trigger('change');

                _this.childList.setList(obj.children);
                _this.parentList.hide();
            });
            this.childList.on('select', function (e, val, obj, el, list) {
                childTrigger.val(val.text);
                childTrigger.trigger('change');

                _this.childList.hide();
            });

        }
    };

    module.exports = function (element, options) {
        return new LinkageSelect(element, options);
    };
});
