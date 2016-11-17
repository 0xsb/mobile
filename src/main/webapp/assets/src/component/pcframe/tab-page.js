define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');

    var TabPage = function () {
        this.init.apply(this, arguments);
    };
    TabPage.prototype = {
        init: function (element, option, children, widgetsFactory) {
            this.$el = element;
            this.$container = this.$el.find('.tabpage-container');

            this.option = option;
            this.children = children;
            this.widgetsFactory = widgetsFactory;

            this.widgets = {};
        },
        render: function ($form, formObj) {
            var _this = this;
            this.parentForm = formObj;

            var widgetsFactory = this.widgetsFactory;
            var afterRender = [],
                elements = [];

            _.each(this.children, function (child) {
                if (!widgetsFactory.hasOwnProperty(child.controlId)) { return; }
                var widget = widgetsFactory[child.controlId](child, null, afterRender);
                widget.$el.css('width', parseInt(child.field1 || '12') * 8.333333 + '%');
                elements.push(widget.$el);
                _this.widgets[child.reName] = widget;
            });

            this.$container.append(elements);
            _.each(afterRender, function (func) {
                func(_this.$container, _this);
            });
        }
    };

    module.exports = function (elem, option, children, widgetsFactory) {
        return new TabPage(elem, option, children, widgetsFactory);
    };
});
