define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var SelectList = require('./select-list');
    var util = require('./util');

    var LinkageSelect = function (element, options) {
        this.init(element, options);
    };
    LinkageSelect.prototype = {
        init: function (element, options) {
            this.$el = element;
            this.options = options;
            this.selects = options.jsonData.linkageOption.selects;
            this.childSelects = null;
            this.list = new SelectList({
                multi: false,
                couldAdd: false,
                textField: 'value',
                valueField: 'value'
            });

            this.bindEvent();
        },
        bindEvent: function () {
            var _this = this;
            var parentTrigger = this.$el.find('.trigger.parent'),
                childTrigger = this.$el.find('.trigger.child');

            _this.list.on('select', function (e, val, obj) {
                _this.list.withInput.val(val.text);
                _this.list.withInput.trigger('change');
                if (obj.hasOwnProperty('children')) {
                    _this.childSelects = obj.children;
                    childTrigger.val('');
                }
                _this.list.hide();
            });
            parentTrigger.on('click', function (e) {
                _this.list.withInput = parentTrigger;
                _this.list.setList(_this.selects).show();
            });
            childTrigger.on('click', function (e) {
                if (!_this.childSelects) {
                    util.alert('请先选择' + _this.options.label[0]);
                    return;
                }

                _this.list.withInput = childTrigger;
                _this.list.setList(_this.childSelects).show();
            });

        }
    };

    module.exports = function (element, options) {
        return new LinkageSelect(element, options);
    };
});
