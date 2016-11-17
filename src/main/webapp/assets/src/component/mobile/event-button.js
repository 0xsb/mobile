define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var loader = require('./loader');
    var util = require('./util');

    var EventButton = function () {
        this.init.apply(this, arguments);
    };
    EventButton.prototype = {
        init: function (element, options) {
            this.$el = element;
            this.options = options;

            var _this = this;
            this.$el.find('a').on('click', function () {
                _this.doEvent();
            });
        },
        bind: function (formView) {
            this.formView = formView;
        },
        getButtonIndex: function () {
            if (this.formView.$el.is('form')) {
                return -1;
            }
            else {
                return this.$el.parents('.table-box').index();
            }
        },
        doEvent: function () {
            var formView = this.formView;
            while (!formView.eventTrigger) {
                formView = formView.parentForm;
            }
            formView.eventTrigger(this.options, this.getButtonIndex());
        }
    };

    module.exports = EventButton;
});
