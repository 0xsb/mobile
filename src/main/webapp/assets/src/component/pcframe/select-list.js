define(function (require, exports, module) {
    var $ = require('jquery'),
        _ = require('underscore');
    var template = require('template');
    var util = require('component/mobile/util');

    var isDecimal = function (val) { return /^\d+(\.\d*){0,1}$/.test(val); };
    var popupTemplate = '<div class="select-list">\
        <ul></ul>\
        </div>';
    var itemTemplate = '<# _.each(list, function (item, index) { #>\
            <li data-key="{{item.value}}" data-index="{{index}}">{{item.text}}</li>\
        <# }) #>';
    var listRender = template(itemTemplate);

    var selectList = function (option) {
        this.init(option);
    };
    selectList.prototype = {
        noDataMessage: '没有列表数据！',
        init: function (option) {
            this.$popup = $(popupTemplate);
            this.$list = this.$popup.find('ul');
            this.$popup.hide();

            this.multi = option.multi;
            this.eventMap = {};
            this.template = option.template || listRender;

            if (this.multi) {
                this.initSubmitPanel();
            }

            var fieldSet = {
                textField: option.textField || 'text',
                valueField: option.valueField || 'value'
            };
            this.parser = option.parser || function (data) {
                return { text: data[fieldSet.textField], value: data[fieldSet.valueField] };
            };

            var _this = this;
            this.$popup.on('click', function (e) {
                var elem = $(e.target);
                if (elem.is('li')) {
                    if (_this.multi) {
                        elem.is('.current') ? elem.removeClass('current') : elem.addClass('current');
                        var objs = [];
                        var vals = _this.$popup.find('li.current').map(function (index, item) {
                                objs.push(_this.dataList[$(item).data('index')]);
                                return { value: $(item).data('key'), text: $(item).text() };
                            }).toArray();
                        _this.trigger('select', [e, vals, objs, elem, this]);
                    }
                    else {
                        _this.$popup.find('li.current').removeClass('current');
                        elem.addClass('current');
                        var obj = _this.dataList[$(elem).data('index')];
                        var val = { value: $(elem).data('key'), text: $(elem).text() };
                        _this.trigger('select', [e, val, obj, elem, this]);
                    }
                }
                else {
                    _this.hide();
                }
            });
        },
        initSubmitPanel: function () {
            var _this = this;
            var submitBtn = $('<div class="list-submit-box"><a class="list-submit-btn" href="javascript:">确定</a></div>');

            this.$popup.append(submitBtn);
            submitBtn.on('click', function (e) {
                _this.hide();
                e.stopPropagation();
            });
        },
        triggerFor: function (triggerEl) {
            var _this = this;
            this.$trigger = triggerEl;
            this.$trigger.on('click', function () {
                _this.show();
            });


            $(document.body).on('click', function (e) {
                if (!(_this.$trigger.is(e.target) || _this.$popup.find(e.target).length > 0)) {
                    _this.hide();
                }
            });
            this.$popup.insertAfter(triggerEl);

            return this;
        },
        setList: function (list) {
            var _this = this;
            this.dataList = list;
            list = _.map(list, function (item) { return _this.parser(item); });
            this.$popup.find('ul').html(this.template({ list: list }));

            return this;
        },
        show: function () {
            if (!this.dataList) {
                util.alert(this.noDataMessage); return;
            }

            this.$popup.show();
            var pos = this.$trigger.position(),
                h = this.$trigger.outerHeight(),
                w = this.$trigger.outerWidth();

            this.$popup.css({
                'top': pos.top + h + 'px',
                'left': pos.left + 'px',
                'width': w + 'px'
            });

            return this.trigger('show');
        },
        hide: function () {
            this.$popup.hide();
            return this.trigger('hide');
        },
        trigger: function (event, args) {
            if (this.eventMap[event]) {
                var _this = this;
                _.each(this.eventMap[event], function (func) {
                    func.apply(_this, args);
                });
            }
            return this;
        },
        on: function (event, func) {
            return this.bind(event, func);
        },
        bind: function (event, func) {
            if (!this.eventMap[event]) { this.eventMap[event] = []; }
            if (this.eventMap[event].indexOf(func) < 0) {
                this.eventMap[event].push(func);
            }
            return this;
        },
        unbind: function (event, func) {
            if (this.eventMap[event]) {
                if (!func) {
                    while (this.eventMap[event].length > 0) { this.eventMap[event].pop(); }
                }
                else {
                    var index = this.eventMap[event].indexOf(func);
                    if (index >= 0) {
                        this.eventMap[event].splice(index, 1);
                    }
                }
            }
            return this;
        }
    };

    module.exports = selectList;
});
