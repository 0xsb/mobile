define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');

    var boxRender = template('<div class="table-box clearfix">' +
            '<div class="box-title"><span>{{describeName}}()</span>' +
            '<a href="javascript:" class="remove">删除</a>' +
            '</div>');

    var TableFieldView = function (element, option, children, widgetsFactory) {
        this.init.apply(this, arguments);
    };
    TableFieldView.prototype = {
        init: function (element, option, children, widgetsFactory) {
            this.$el = element;
            this.$container = this.$el.find('.table-container');
            this.$addBtn = this.$el.find('.add-button');

            this.children = children;
            this.option = option;
            this.widgetsFactory = widgetsFactory;
            this.boxes = {};

            var _this = this;
            this.$addBtn.on('click', function (e) {
                _this.addOne();
            });
        },
        refreshIndex: function () {
            var option = this.option;
            this.$container.children().each(function (index, item) {
                $(this).find('.box-title span').text(option.describeName + '(' + (index + 1) + ')');
                var $remove = $(this).find('.remove');
                if (index > 0) { $remove.show(); }
                else { $remove.hide(); }
            });
        },
        addOne: function () {
            var _this = this;
            var children = this.children;
            var widgetsFactory = this.widgetsFactory;

            var boxid = 'box' + (new Date).getTime() + (Math.random() * 1000).toFixed(0);
            var $box = $(boxRender(this.option)).data('id', boxid);
            $box.find('.remove').on('click', function () {
                $box.remove();
                _.each(children, function (child) {
                    if (child.compute !== false && (child.controlId === 'NumberField'
                        || child.controlId === 'MoneyField' || child.controlId === 'NumberCompute')) {
                        container.find('[name=' + child.reName + ']').trigger('change');
                    }
                });

                _this.refreshIndex();
                delete _this.boxes[boxid];
            });

            var elements = [],
                widgets = {},
                childAfterRender = [];
            _.each(children, function (child) {
                if (!widgetsFactory.hasOwnProperty(child.controlId)) { return; }
                var widget = widgetsFactory[child.controlId](child, null, childAfterRender);

                widgets[child.reName] = widget;
                elements.push(widget.$el);
            });

            $box.append(elements).appendTo(this.$container);
            _.each(childAfterRender, function (func) {
                func($box, _this);
            });

            this.refreshIndex();

            this.boxes[boxid] = { $el: $box, widgets: widgets };
        },
        render: function ($form, formObj) {
            this.parentForm = formObj;

            var _this = this;
            var $container = this.$container;
            var widgetsFactory = this.widgetsFactory;
            var computeElems = [];
            var afterRender = [];
            _.each(this.children, function (child) {
                if (child.compute !== false && (child.controlId === 'NumberField'
                    || child.controlId === 'MoneyField' || child.controlId === 'NumberCompute'
                    || child.controlId === 'ComputeField')) {
                    var computeElem = widgetsFactory['NumberCompute']({
                        'describeName': child.describeName + '合计：',
                        'isRequired': false,
                        'reName': 'total' + child.reName,
                        'sequence': 100,
                        'controlId': 'NumberCompute',
                        'factors': [child.reName],
                        'type': (child.type === 'integer') ? 'integer' : 'float',
                        'operation': 'plus'
                    }, null, afterRender);
                    computeElems.push(computeElem.$el);
                }
            });

            this.$el.find('.compute-container').append(computeElems);
            _.each(afterRender, function (func) {
                func($container, _this);
            });

            this.addOne();
        }
    };

    module.exports = function (element, option, children, widgetsFactory) {
        return new TableFieldView(element, option, children, widgetsFactory);
    };
});
